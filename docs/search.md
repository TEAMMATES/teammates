
# Apache Solr Search Engine
Solr is an open source search platform built on Apache Lucene that provides scalable indexing and search amongst other features.
To learn more about Solr, visit their [official documentation page](https://solr.apache.org/guide/8_8/).

**Note: Search service is optional for local development. It is perfectly fine to skip the setups in this page if you are not working on search features.** 

## Setting up Solr (Direct Setup)

1. Download and install Solr by following this [guide](https://solr.apache.org/guide/8_8/installing-solr.html#installing-solr). 
1. To start the Solr server, navigate to the Solr root directory and run the following command:
   ```sh
   # The server listens on `port 8983` by default.
   
   # Linux/MacOS
   bin/solr start
   
   # Windows
   bin\solr.cmd start
   ```
   **Verification:** If the server runs successfully, you should see the following printed out on your console:
   ```
   Started Solr server on port 8983 (pid=44665). Happy searching!
   ```
1. Before development, we need to ensure that the relevant collections containing our search documents are created.
   
   For example, to create a `students` collection in Solr:
   ```sh
   bin/solr create -c students -s 2 -rf 2
   ```
   To enable full-text search across multiple fields, it is also important to add a [Copy Field](https://solr.apache.org/guide/8_8/copying-fields.html) to the schema for each of our collections.
   
   For example, to add a Copy Field to the `students` collection we have previously created:
   ```sh
   curl -X POST -H "Content-Type: application/json" --data-binary '{"add-copy-field": {"source": "*", "dest": "_text_"}}' localhost:8983/solr/students/schema
   ```
   Alternatively, you can add Copy Field in the Admin Console by going to `http://localhost:8983` in browser.

## Setting up Solr using Docker

*Note: If you are running on Windows, this method is not recommended.*

1. If Docker is not already installed, download it from [here](https://docs.docker.com/get-docker/) and install it to your local machine.
1. While inside the project root directory, run the following command:
   ```sh
   docker run --name=tm_solr -d -p 8983:8983 solr:8.8.1
   ```
   **Verification:** with a web browser go to http://localhost:8983/, you should see the Solr Admin Console.
1. To initialise Solr, we run the `solr.sh` script located in the project directory: 
   ```sh
   docker exec $(docker ps -qf "name=tm_solr") /bin/sh -c "$(cat ./solr.sh)"
   ```
