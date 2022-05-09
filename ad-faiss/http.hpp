#pragma once
#include "httplib.h"
#include "utils.hpp"
#include "indexer.hpp"
#include "redcli.hpp"
#include "json.hpp"

#define CPPHTTPLIB_OPENSSL_SUPPORT

class FaissServer
{
public:
    FaissServer(const char *config_path);
    ~FaissServer();
    bool connect_to_eureka(utils::Config &config);
    void start_server(utils::Config &config);
    bool load_faiss_indexer(utils::Config &config);
    // std::pair<uint64_t, float> *faiss_search(int top_k, std::vector<float> &query_emb);
    FaissIndexer *get_indexer();
    RedisClient *get_redis();

private:
    std::string eureka_host;
    std::string eureka_port;
    std::string app_id;
    std::string client_host;
    std::string client_port;
    std::string server_host;
    std::string server_port;
    httplib::Client *cli;
    httplib::Server *svr;
    FaissIndexer *faiss;
    RedisClient *redcli;
};

void handleFaissRequest(FaissServer *server, const httplib::Request &req, httplib::Response &res);
