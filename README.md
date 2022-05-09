# ad-system-spring-boot
An advertisement system based on spring cloud microservices and FAISS embedding search

# Architecture
Eureka: load balancer & service registry;
Zuul(api-gateway): API-Gateway, routing requests to different microservices;
Sponsors: CRUD operations for sponsors to add, modify, remove advertisements and materials;
Search: Building indexes and searching advertisements given keywords, tags, etc.
Faiss: search service with embedding similarity based on Facebook FAISS in C++;
