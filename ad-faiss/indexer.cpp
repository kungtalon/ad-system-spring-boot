#include "indexer.hpp"
#include "utils.hpp"

FaissIndexer::FaissIndexer(uint64_t fea_dim, 
            const char *index_file_path, 
            const char *embedding_file_path)
{
    this->fea_dim = fea_dim;
    if (strlen(index_file_path) > 0 && utils::file_exists(index_file_path))
    {
        this->load_index_from_disk(index_file_path);
    }
    else if (strlen(embedding_file_path) > 0 && utils::file_exists(embedding_file_path))
    {
        this->build_index_from_csv(embedding_file_path, index_file_path);
        this->load_index_from_disk(index_file_path);
    }
    else
    {
        utils::print_error("Failed to initialize a faiss index...");
        exit(-1);
    }
}

FaissIndexer::~FaissIndexer()
{
    if (index != NULL)
    {
        delete index;
    }
}

void FaissIndexer::build_index_from_csv(const char *embedding_file_path, const char *index_file_path)
{
    // initialize
    faiss::IndexHNSWFlat index_hsnw(fea_dim, 16);
    index = new faiss::IndexIDMap(&index_hsnw);
    int cur_cnt = 0;
    std::vector<long> ids = std::vector<long>();
    std::vector<float> embeddings = std::vector<float>();
    std::stringstream ss;
    std::string line;
    std::vector<std::string> splitted;

    // read lines from csv file
    utils::read_data(embedding_file_path, ss);

    while (std::getline(ss, line))
    {
        utils::string_split(line, ',', splitted);
        if (splitted.size() != fea_dim + 1)
        {
            utils::print_error("Mismatch embedding dim in line: " + std::to_string(cur_cnt) + 
                               ": expected " + std::to_string(fea_dim) + ", get " + std::to_string((size_t)splitted.size() - 1));
            return;
        }

        ids.push_back(std::stoi(splitted[0]));

        for (auto it = splitted.begin() + 1; it != splitted.end(); it++)
        {
            embeddings.push_back(std::stof(*it));
        }

        cur_cnt += 1;
    }

    // build index
    index->add_with_ids(ids.size(), embeddings.data(), ids.data());

    // persist results by writing to disk
    FILE *fr;
    fr = fopen(index_file_path, "wb");
    faiss::write_index(index, fr);

    utils::print_info("Index saved successfully to " + std::string(index_file_path) + "!");

    embeddings.clear();
    ids.clear();
    fclose(fr);
}

void FaissIndexer::load_index_from_disk(const char *index_file_path)
{
    FILE *fr;
    fr = fopen(index_file_path, "rb");
    index = faiss::read_index(fr);
    fclose(fr);
}

bool FaissIndexer::is_index_ok()
{
    if (index == NULL)
    {
        return false;
    }
    try
    {
        if (index->ntotal == 0)
        {
            return false;
        }
    }
    catch (const std::exception &e)
    {
        return false;
    }
    return true;
}

void FaissIndexer::search(std::vector<float> &query_embedding,
                          uint64_t query_num,
                          uint64_t top_k,
                          long* ids_res,
                          float* score_res)
{
    if (ids_res == NULL || score_res == NULL)
    {
        // failed to allocate memory
        utils::print_error("Failed to initialize result in faiss search...");
        return;
    }

    index->search(query_num, query_embedding.data(), top_k, score_res, ids_res);


#if DEBUG
    for (int i = 0; i < query_num; i++)
    {
        printf("query %2d: ", i);
        for (int j = 0; j < top_k; j++)
        {
            printf("%7ld ", ids_res[j + i * top_k]);
        }
        printf("\n     dis: ");
        for (int j = 0; j < top_k; j++)
        {
            printf("%7g ", score_res[j + i * top_k]);
        }
        printf("\n");
    }
#endif
}

uint64_t FaissIndexer::get_embedding_dim()
{
    return this->fea_dim;
}