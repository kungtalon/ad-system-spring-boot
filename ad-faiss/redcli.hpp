#pragma once
#include <vector>
#include <string>
#include <future>
#include <cfloat>
#include <cpp_redis/cpp_redis>
#include "utils.hpp"

#define ENABLE_SESSION = 1
#define USE_MEAN_POOLING false

class RedisClient
{
public:
    RedisClient(utils::Config &config);
    ~RedisClient();
    void query_to_embedding(std::string &query, std::vector<float>::iterator begin, size_t length);
    std::string get(const std::string &key);
    float *get_list(const std::string &key);
    void put_list(const std::string& key, std::vector<float>& values);
    inline size_t get_embedding_size() { return embedding_size; }

private:
    size_t embedding_size;
    std::string prefix;
    cpp_redis::client *cli;
    void start_client(const std::string &host, size_t port);
};

void vector_type_conversion(std::vector<std::string> & des, std::vector<float> & src);
