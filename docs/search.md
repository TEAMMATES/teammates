# Full-text Search Feature Support

We use [Apache Solr](https://solr.apache.org/guide/8_8/) to support full-text search features required by the application, such as searching for students and instructors.

Search service is optional for local development. It is perfectly fine to skip the setup described in this page if you are not working on search features.

This document will assume Solr version `8.8.1`.

## Setting up Solr using Docker

If you are familiar with Docker, this method is recommended.

1. Run the `solr:8.8.1` Docker image and bind to the container port `8983`. For example, to run a container named `tm_solr` accessible from `localhost:8983` in the background:
   ```sh
   docker pull solr:8.8.1
   docker run --name=tm_solr -d -p 8983:8983 solr:8.8.1
   ```
   **Verification:** the Solr admin console should be accessible in `http://localhost:8983`.
1. To initialise Solr for our use cases, we run the [Solr startup script](../solr.sh) located in the project directory in the running container:
   ```sh
   docker exec $(docker ps -qf "name=tm_solr") /bin/sh -c "$(cat ./solr.sh)"
   ```

## Setting up Solr manually

You may notice that the setup steps in this method are a manual version of the Docker method.

1. Download and install Solr by following [this guide](https://solr.apache.org/guide/8_8/installing-solr.html#installing-solr).
1. To start the Solr server, navigate to the Solr root directory and run the following command:
   ```sh
   # The server listens to port 8983 by default.
   
   # Linux/MacOS
   bin/solr start
   
   # Windows
   bin\solr.cmd start
   ```
   Wait until the following message (or similar) is printed in the console:
   ```
   Started Solr server on port 8983 (pid=44665). Happy searching!
   ```
   **Verification:** the Solr admin console should be accessible in `http://localhost:8983`.
1. Run all the commands defined in the [Solr startup script](../solr.sh) in the Solr root directory.
