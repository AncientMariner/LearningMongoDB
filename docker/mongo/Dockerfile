FROM mongo:latest

# Expose port 27017 from the container to the host
EXPOSE 27016

# Set usr/bin/mongod as the dockerized entry-point application
WORKDIR /home
COPY primer-dataset.json .
ENTRYPOINT ["/usr/bin/mongod"]
