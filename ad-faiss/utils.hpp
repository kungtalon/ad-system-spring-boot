#pragma once
#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <unordered_map>
#include <vector>
#include <ctime>
#include <cstring>
#include <functional>
#include <algorithm>

namespace utils
{
    class Config
    {
    public:
        Config(const char *file_path);
        ~Config();
        void put(const std::string &key, const std::string &value);
        const char *get(const std::string &key);
        void update(const std::string &key, const std::string &value);
        bool contains(const std::string &key);
        const std::string &get_or_default(const std::string &key, const std::string &default_value);

    private:
        std::unordered_map<std::string, std::string> *config_map;
    };

    bool read_data(const std::string &filename, std::stringstream &dest_data);

    inline bool is_char_unsafe(char c);

    inline char to_hex(unsigned char c);

    std::string encode_url(const std::string &s);

    void string_split(const std::string &s, const char delim, std::vector<std::string> &parts);

    bool file_exists(const char *file_name);

    void print_error(const std::string &message);

    void print_info(const std::string &message);
}