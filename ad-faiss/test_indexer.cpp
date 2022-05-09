#include "indexer.hpp"
#include "constant.hpp"
#include "utils.hpp"

void fill_random_embedding(std::vector<float>& v) 
{
    for (int i = 0; i < v.size(); i++){
        v[i] = (float)rand() / RAND_MAX;
    }
}

int main(int argc, char* argv[]){
    utils::Config config(argv[1]);
    FaissIndexer faiss(100, config.get("index_file_path"), config.get("embedding_file_path"));
    std::vector<float> query_embedding(100 * 10);
    fill_random_embedding(query_embedding);
    long *ids;
    float *scores;
    faiss.search(query_embedding, 10, 4, ids, scores);
}

