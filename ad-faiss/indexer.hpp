#pragma once
#include "faiss/index_io.h"
#include "faiss/IndexHNSW.h"
#include "faiss/MetaIndexes.h"
#include <algorithm>

#define DEBUG false

class FaissIndexer
{
public:
    FaissIndexer(uint64_t fea_dim, const char *index_file_path, const char *embedding_csv_path);
    ~FaissIndexer();
    void build_index_from_csv(const char *embedding_file_path, const char *index_file_path);
    void load_index_from_disk(const char *index_file_path);
    void search(std::vector<float> &query_embedding, uint64_t query_num, uint64_t top_k,
                long* ids_res,
                float* score_res);
    bool is_index_ok();
    uint64_t get_embedding_dim();

private:
    faiss::Index *index;
    uint64_t fea_dim;
};
