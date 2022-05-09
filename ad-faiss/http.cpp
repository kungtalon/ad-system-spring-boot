#include "http.hpp"
#include "constant.hpp"

FaissServer::FaissServer(const char *config_path)
{
    utils::Config config = utils::Config(config_path);
    this->server_host = config.get_or_default(SERVER_HOST, DEFAULT_SERVER_HOST);
    this->server_port = config.get_or_default(SERVER_PORT, DEFAULT_SERVER_PORT);

    this->svr = new httplib::Server();

    this->faiss = new FaissIndexer(std::stoi(config.get_or_default(EMBEDDING_SIZE, DEFAULT_EMBEDDING_SIZE)),
                                   config.get_or_default(INDEX_FILE_PATH, "").c_str(),
                                   config.get_or_default(EMBEDDING_FILE_PATH, "").c_str());

    this->redcli = new RedisClient(config);

    this->start_server(config);
}

bool FaissServer::connect_to_eureka(utils::Config &config)
{
    this->eureka_host = config.get_or_default(EUREKA_HOST, DEFAULT_EUREKA_HOST);
    this->eureka_port = config.get_or_default(EUREKA_PORT, DEFAULT_EUREKA_PORT);
    this->app_id = config.get_or_default(APP_ID, DEFAULT_APP_ID);

    // HTTP
    utils::print_info("Creating http client...");
    this->cli = new httplib::Client("http://" + eureka_host + ":" + eureka_port);

    // read Eureka json file
    utils::print_info("Reading json file...");
    const char *registry_json_path = config.get("registry_json_path");
    if (registry_json_path == NULL)
    {
        utils::print_error("Failed to find the config of registry json...");
        return false;
    }
    std::stringstream registry_json_content;
    bool ret = utils::read_data(registry_json_path, registry_json_content);
    if (!ret)
    {
        utils::print_error("Failed to read the content of registry json...");
        return false;
    }

    std::string registry_json = registry_json_content.str();
    utils::print_info("Read json: \n" + registry_json);

    utils::print_info("Sending registry request to eureka...");
    const char *uri = (EUREKA_PREFIX + utils::encode_url(app_id)).c_str();
    auto res = cli->Post(uri,
                         registry_json.c_str(),
                         (size_t)registry_json.size(),
                         "application/json");
    if (res->status == -1)
    {
        utils::print_error("Failed to get response...");
        return false;
    }
    try
    {
        utils::print_info("Sended connect request to eureka, getting response: \nStatus (" +
                          std::to_string(res->status) + ") Body: (" + res->body + ")");
    }
    catch (const std::exception &e)
    {
        utils::print_error(e.what());
        return false;
    }

    return true;
}

FaissServer::~FaissServer()
{
    if (cli != NULL)
    {
        delete this->cli;
    }
    if (svr != NULL)
    {
        delete this->svr;
    }
    if (faiss != NULL)
    {
        delete faiss;
    }
    if (redcli != NULL)
    {
        delete redcli;
    }
}

void FaissServer::start_server(utils::Config &config)
{
    svr->Post("/faiss", [this](const httplib::Request &req, httplib::Response &res)
            { 
                handleFaissRequest(this, req, res); 
            });

    svr->set_exception_handler([](const httplib::Request &req, httplib::Response &res, const std::exception& e) {
        res.status = 500;
        nlohmann::json j = {{"message", "Internal Error :" + std::string(e.what())}};
        res.set_content(j, "application/json");
    });

    utils::print_info("Listening to " + server_host + ":" + server_port);
    svr->listen(server_host.c_str(), std::stoi(server_port));
}

RedisClient *FaissServer::get_redis()
{
    return this->redcli;
}

FaissIndexer *FaissServer::get_indexer()
{
    return this->faiss;
}

void handleFaissRequest(FaissServer *server, const httplib::Request &req, httplib::Response &res)
{
    using namespace nlohmann;
    if (!req.has_header("Content-Type") || req.get_header_value("Content-Type") != "application/json")
    {
        json j({{"message", "Bad Request: Invalid request type!"}});
        res.set_content(j.dump(), "application/json");
        res.status = 400; // bad request!
        return;
    }
    auto payload = json::parse(req.body);
    if (!payload.contains("query"))
    {
        json j({{"message", "Bad Request: Cannot find 'query' field in request body!"}});
        res.set_content(j.dump(), "application/json");
        res.status = 400; // bad request!
        return;
    }

    int top_k = 4;
    try {
        top_k = payload["top_k"].get<int>();
    } catch (const std::exception& ex) {
        utils::print_error("No top_k in the query. " + std::string(ex.what()));
    }
    auto queries = payload["query"].get<std::vector<std::string>>();

    int query_num = queries.size();
    size_t embedding_size = server->get_indexer()->get_embedding_dim();
    std::vector<float> embeddings =
        std::vector<float>((long)embedding_size * query_num);
    auto it = embeddings.begin();
    for (std::string &query : queries)
    {
        server->get_redis()->query_to_embedding(query, it, embedding_size);
        it += embedding_size;
    }

    long* ids_res = new long[query_num * top_k];
    float* score_res = new float[query_num * top_k];
    server->get_indexer()->search(embeddings, query_num, top_k, ids_res, score_res);

    json score_results = json::array();
    for (int i = 0; i < query_num; i++)
    {
        json query_result = json::array();
        for (int j = 0; j < top_k; j++)
        {
            std::string concat = std::to_string(ids_res[i*top_k+j]) + ":" + std::to_string(1 / (score_res[i*top_k + j] + 1e-7));
            query_result.push_back(concat);
        }
        score_results.push_back(query_result);
    }
    json res_body = json::object({{"result", score_results}});
    res.set_content(res_body.dump(), "application/json");
    res.status = 200;

    delete[] ids_res, score_res;
}