#include "utils.hpp"
#include "constant.hpp"

int main(int argc, char *argv[])
{
    utils::Config config(argv[1]);
    std::string emb_size = config.get_or_default(EMBEDDING_SIZE, DEFAULT_EMBEDDING_SIZE);
    std::string host = config.get_or_default(CLIENT_HOST, DEFAULT_CLIENT_HOST);
    std::string port = config.get_or_default(CLIENT_PORT, DEFAULT_CLIENT_PORT);
    config.get("unknown_field");
    utils::print_info("Test: " + emb_size);
    utils::print_info("Test: getting host and port " + host + " " + port);
}