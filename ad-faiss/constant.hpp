#pragma once
#include <string>

const std::string EUREKA_HOST = "eureka_host";
const std::string EUREKA_PORT = "eureka_port";
const std::string APP_ID = "app_id";

const std::string CLIENT_HOST = "client_host";
const std::string CLIENT_PORT = "client_port";

const std::string SERVER_HOST = "server_host";
const std::string SERVER_PORT = "server_port";

const std::string DEFAULT_EUREKA_HOST = "localhost";
const std::string DEFAULT_EUREKA_PORT = "8000";
const std::string DEFAULT_APP_ID = "ad-faiss";
const std::string EUREKA_PREFIX = "/eureka/v2/apps/";

const std::string DEFAULT_SERVER_HOST = "localhost";
const std::string DEFAULT_SERVER_PORT = "9009";

const std::string DEFAULT_CLIENT_HOST = "localhost";
const std::string DEFAULT_CLIENT_PORT = "9010";

const std::string INDEX_FILE_PATH = "index_file_path";
const std::string EMBEDDING_FILE_PATH = "embedding_file_path";

const std::string DEFAULT_INDEX_FILE = "./data/faiss.ind";
const std::string DEFAULT_EMBEDDING_FILE = "./data/glove.csv";

const std::string EMBEDDING_SIZE = "embedding_size";
const std::string DEFAULT_EMBEDDING_SIZE = "100";

const std::string REDIS_HOST = "redis_host";
const std::string DEFAULT_REDIS_HOST = "localhost";
const std::string REDIS_PORT = "redis_port";
const std::string DEFAULT_REDIS_PORT = "6379";
const std::string REDIS_PREFIX = "redis_prefix";
const std::string DEFAULT_REDIS_PREFIX = "ad_faiss";