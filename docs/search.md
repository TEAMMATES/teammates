<frontmatter>
  title: "Search"
</frontmatter>

# Full-text Search Feature Support

We use [Apache Solr](https://solr.apache.org/guide/8_11/) to support full-text search features required by the application, such as searching for students and instructors.

Search service is optional for local development. It is perfectly fine to skip the setup described in this page if you are not working on search features.

This document will assume Solr version `8.11.1`.

## Setting up Solr using Docker

If you have access to Docker, this method is straightforward and recommended.

We have provided a Docker compose definition to run dependent services, including Solr. Run it under the `solr` service name and bind to the container port `8983`:
```sh
docker-compose run -p 8983:8983 solr
```

**Verification:** the Solr admin console should be accessible in `http://localhost:8983`.

## Setting up Solr manually

You may notice that the setup steps in this method are a manual version of the Docker method.

1. Download and install Solr by following [this guide](https://solr.apache.org/guide/8_11/installing-solr.html#installing-solr).
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
1. Run all the commands defined in the [Solr startup script](https://github.com/TEAMMATES/teammates/blob/master/solr/solr.sh) in the Solr root directory.
