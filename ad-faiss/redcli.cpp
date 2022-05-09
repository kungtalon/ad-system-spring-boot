#include "redcli.hpp"
#include "constant.hpp"

RedisClient::RedisClient(utils::Config &config)
{
    this->embedding_size = std::stoi(config.get_or_default(EMBEDDING_SIZE, DEFAULT_EMBEDDING_SIZE));
    this->prefix = config.get_or_default(REDIS_PREFIX, DEFAULT_REDIS_PREFIX);
    std::string host = config.get_or_default(REDIS_HOST, DEFAULT_EMBEDDING_SIZE);
    size_t port = std::stoi(config.get_or_default(REDIS_PORT, DEFAULT_REDIS_PORT));

    start_client(host, port);
}

RedisClient::~RedisClient()
{
    if (cli != NULL)
    {
        cli->disconnect();
        delete cli;
    }
}

void RedisClient::start_client(const std::string &host = "localhost", size_t port = 6379)
{
    cli = new cpp_redis::client();

    cli->connect(host, port);

    if (cli->is_connected())
    {
        utils::print_info("RedisClient: Connnected!");
    }
    else
    {
        utils::print_error("RedisClient: Failed to connect!");
    }

    cli->select(0);
}

void RedisClient::query_to_embedding(std::string &query, std::vector<float>::iterator begin, size_t length)
{
    std::vector<std::string> query_tokens;
    utils::string_split(query, ' ', query_tokens);

    float* res;
    float** tmp_embeddings;
    bool success = true;
    
    if (query_tokens.size() == 0) {
        success = false;
    }
    else if (query_tokens.size() == 1) {
        res = get_list(query_tokens[0]);
        if (res == NULL)
        {
            success = false;
        }        
    }
    else 
    {
        # if USE_MEAN_POOLING
        // multiple words in a query, use mean-pooling
        res = new float[length];
        memset(res, 0, sizeof(float) * length);
        tmp_embeddings = new float*[query_tokens.size()];
        for (int i = 0; i < query_tokens.size(); i++)
        {
            tmp_embeddings[i] = get_list(query_tokens[i]);
            if (tmp_embeddings[i] == NULL)
            {
                success = false;
                break;
            }
            for (int j = 0; j < length; j++)
            {
                res[j] += tmp_embeddings[i][j];
            }
        }
        for (int i = 0; i< query_tokens.size(); i++)
        {
            if (tmp_embeddings[i] != NULL)
                delete[] tmp_embeddings[i];
        }
        delete[] tmp_embeddings;
        # else
        res = get_list(query_tokens[0]);
        if (res == NULL)
        {
            success = false;
        }
        # endif
    }
    if (!success)
    {
        std::fill(begin, begin + length, FLT_MAX);
        return ;
    }
    # if USE_MEAN_POOLING
    for (int j = 0; j < length; j++)
    {
        res[j] /= query_tokens.size();
    }
    # endif
    memcpy(&(*begin), &res[0], length * sizeof(float));
    if (res != NULL)
    {
        delete[] res;
    }
    return;
}

std::string RedisClient::get(const std::string &key)
{
    float *result = new float[embedding_size];
    std::future<cpp_redis::reply> future_rep = cli->get(prefix + key);
    cli->sync_commit();
    cpp_redis::reply rep = future_rep.get();
    utils::print_info("Get " + rep.as_string());
    return rep.as_string();
}

float *RedisClient::get_list(const std::string &key)
{
    float *result = new float[embedding_size];
    const uint64_t dim = embedding_size;
    std::future<cpp_redis::reply> future_rep = cli->lrange(prefix + key, 0, embedding_size);
    cli->sync_commit();
    cpp_redis::reply rep = future_rep.get();
    if (rep.is_array())
    {
        auto reply_arr = rep.as_array();
        if (reply_arr.size() != dim){
            utils::print_error("Mismatch of redis list length: " + reply_arr.size());
            return NULL;
        }
        for (int i=0; i< reply_arr.size(); i++)
        {
            // utils::print_info("Lambda expr: getting float in reply_arr " + reply_arr[i].as_string());
            result[i] = std::stof(reply_arr[i].as_string());
        }
    }
    return result;
}

void RedisClient::put_list(const std::string& key, std::vector<float>& values) 
{
    if (values.empty()) 
    {
        return ;
    }
    std::vector<std::string> str_values;
    vector_type_conversion(str_values, values);
    std::future<cpp_redis::reply> future_rep = cli->rpush(prefix + key, str_values);
    cli->sync_commit();
    cpp_redis::reply rep = future_rep.get();
    if (rep.is_error())
    {
        utils::print_error("Error when putting list to redis...");
    }
}

void vector_type_conversion(std::vector<std::string> & des, std::vector<float> & src)
{
    std::transform(src.begin(), src.end(), std::back_inserter(des), 
        [](const float a){return std::to_string(a);});
}