# Compiler
CC = g++

CFLAGS = -std=c++11
OBJECTS = http.o utils.o redcli.o indexer.o
INC = -I/usr/local/include

TARGET = faiss.exe

# make object files
%.o: %.cpp
	$(CC) -std=c++11 $*.cpp -c -o $@

# main executable
exec: main.cpp $(OBJECTS) $(SDL)
	$(CC) $(CFLAGS) $(OBJECTS) main.cpp -o $(TARGET) $(INC) -lfaiss -fopenmp /usr/lib/x86_64-linux-gnu/libopenblas.so.0 -lcpp_redis -L/usr/local/lib/libcpp_redis.a -ltacopie -L/usr/local/lib/libtacopie.a -lpthread -lssl -lcrypto

.PHONY: clean
clean:
	rm -rf *.o $(TARGET)

.PHONY: tutils
test-utils: test_utils.cpp utils.cpp 
	$(CC) -o utils.o utils.cpp -c
	$(CC) -o test_utils test_utils.cpp utils.o
	./test_utils ./ad-faiss.conf

.PHONY:	tredcli
test-redcli: redcli.cpp test_redcli.cpp utils.cpp
	$(CC) -o utils.o utils.cpp -c
	$(CC) -o redcli.o redcli.cpp -c
	$(CC) -std=c++11 -o test_redcli test_redcli.cpp redcli.o utils.o -I/usr/local/include -lcpp_redis -L/usr/local/lib/libcpp_redis.a -ltacopie -L/usr/local/lib/libtacopie.a -lpthread 
	./test_redcli ./ad-faiss.conf

.PHONY: tindexer
test-indexer: indexer.cpp test_indexer.cpp utils.cpp
	$(CC) -o utils.o utils.cpp -c
	$(CC) -o indexer.o indexer.cpp utils.o -c
	$(CC) -std=c++11 -o test_indexer test_indexer.cpp utils.o indexer.o -I/sur/local/include -lfaiss -fopenmp /usr/lib/x86_64-linux-gnu/libopenblas.so.0
	./test_indexer ./ad-faiss.conf

