#!/usr/bin/env sh

# Create core to run students collection
bin/solr create -c students -s 2 -rf 2
curl -X POST -H "Content-Type: application/json" --data-binary '{"add-copy-field": {"source": "*", "dest": "_text_"}}' localhost:8983/solr/students/schema

# Create core to run instructors collection
bin/solr create -c instructors -s 2 -rf 2
curl -X POST -H "Content-Type: application/json" --data-binary '{"add-copy-field": {"source": "*", "dest": "_text_"}}' localhost:8983/solr/instructors/schema
