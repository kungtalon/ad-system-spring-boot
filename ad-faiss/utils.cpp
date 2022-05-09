#include "utils.hpp"

namespace utils
{
    Config::Config(const char *file_path)
    {
        bool ret;
        std::string result;
        std::stringstream content;
        config_map = new std::unordered_map<std::string, std::string>();
        ret = read_data(file_path, content);
        if (ret)
        {
            std::string line;
            std::vector<std::string> parts;
            while (std::getline(content, line))
            {
                if (line.empty() || line[0] == '#')
                {
                    continue;
                }
                string_split(line, '=', parts);
                if (parts.size() == 2)
                {
                    put(parts[0], parts[1]);
                }
                else
                {
                    print_error("Unable to parse the line correctly: " + line);
                }
            }
        }
    }

    Config::~Config()
    {
        this->config_map->clear();
        if (this->config_map != NULL)
        {
            delete this->config_map;
        }
    }

    void Config::put(const std::string &key, const std::string &value)
    {
        // print_info("Put new key-value pair of " + key + " / " + value);
        this->config_map->insert(std::pair<std::string, std::string>(key, value));
    }

    void Config::update(const std::string &key, const std::string &value)
    {
        auto it = this->config_map->find(key);
        if (it == this->config_map->end())
        {
            put(key, value);
        }
        else
        {
            it->second = value;
        }
    }

    bool Config::contains(const std::string &key)
    {
        auto it = config_map->find(key);
        if (it == this->config_map->end())
        {
            return false;
        }
        return true;
    }

    const char *Config::get(const std::string &key)
    {
        auto it = config_map->find(key);
        if (it == this->config_map->end())
        {
            return NULL;
        }
        else
        {
            return it->second.c_str();
        }
    }

    const std::string &Config::get_or_default(const std::string &key, const std::string &default_value)
    {
        auto it = config_map->find(key);
        if (it == this->config_map->end())
        {
            utils::print_info("Key not found in config : " + key + ", using default value: " + default_value);
            return default_value;
        }
        else
        {
            // utils::print_info("Key retrieved from config : " + key + ", with value: " + default_value);
            return it->second;
        }
    }

    bool read_data(const std::string &filename, std::stringstream &dest_data)
    {
        std::ifstream fin;
        fin.open(filename);
        if (!fin.is_open())
        {
            print_error("Failed to open " + filename);
            return false;
        }
        dest_data << fin.rdbuf();
        fin.close();
        return true;
    }

    inline bool is_char_unsafe(char c)
    {
        switch (c)
        {
        // Reserved characters
        case '!':
        case '#':
        case '$':
        case '&':
        case '\'':
        case '(':
        case ')':
        case '*':
        case '+':
        case ',':
        case '/':
        case ':':
        case ';':
        case '=':
        case '?':
        case '@':
        case '[':
        case ']':
        // Other characters
        case '\x20':
        case '\x7F':
        case '"':
        case '%': /*case '-':*/ /*case '.':*/
        case '<':
        case '>':
        case '\\':
        case '^': /*case '_':*/
        case '`':
        case '{':
        case '|':
        case '}': /*case '~':*/
            return true;
        default:
            return static_cast<unsigned char>(c) < 0x20 || static_cast<unsigned char>(c) >= 0x80;
        }
    }

    inline char to_hex(unsigned char c)
    {
        char table[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        return table[c];
    }

    std::string encode_url(const std::string &s)
    {
        std::string r;
        r.reserve(s.size());

        for (const auto c : s)
        {
            if (is_char_unsafe(c))
            {
                r += '%';
                r += to_hex(static_cast<unsigned char>(c) >> 4);
                r += to_hex(static_cast<unsigned char>(c) & 0xF);
            }
            else
            {
                r += c;
            }
        }

        return r;
    }

    void string_split(const std::string &s, const char delim, std::vector<std::string> &parts)
    {
        std::stringstream ss(s);
        std::string item;

        if (!parts.empty())
        {
            parts.clear();
        }
        while (std::getline(ss, item, delim))
        {
            parts.push_back(item);
        }
    }

    bool file_exists(const char *file_name)
    {
        if (FILE *file = fopen(file_name, "r"))
        {
            fclose(file);
            return true;
        }
        else
        {
            return false;
        }
    }

    void print_error(const std::string &message)
    {
        time_t now = time(0);
        std::cout << "[ERROR " << strtok(std::ctime(&now), "\n") << "] " << message << std::endl;
    }

    void print_info(const std::string &message)
    {
        time_t now = time(0);
        std::cout << "[INFO " << strtok(std::ctime(&now), "\n") << "] " << message << std::endl;
    }
}

