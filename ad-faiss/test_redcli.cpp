#include "redcli.hpp"
#include "utils.hpp"
#include "constant.hpp"

int main(int argc, char *argv[])
{
    utils::Config config(argv[1]);
    config.update(EMBEDDING_SIZE, "3");
    config.update(REDIS_HOST, "127.0.0.1");
    config.update(REDIS_PREFIX, "test_redcli:");
    RedisClient cli(config);
    std::vector<float> list_to_put({5.0, 3.0, 1.0});
    cli.put_list("5", list_to_put);
    std::string tmp = cli.get("2");
    float *res = cli.get_list("5");
    if (res == NULL)
    {
        utils::print_error("Error in getting embeddings from redis...");
        exit(-1);
    }
    utils::print_info("Get embedding: " +
                      std::to_string(res[0]) + " " + std::to_string(res[1]) + " " + std::to_string(res[2]));
}