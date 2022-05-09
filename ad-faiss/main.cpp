#include "http.hpp"

int main(int argc, char *argv[])
{
    if (argc == 2)
    {
        std::string file_path(argv[1]);
        FaissServer(file_path.c_str());
    }
    else
    {
        FaissServer("./ad-faiss.conf");
    }
}