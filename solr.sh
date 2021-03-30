#!/usr/bin/env sh

# Create core to run students collection
bin/solr create -c students -s 2 -rf 2

# Create core to run instructors collection
bin/solr create -c instructors -s 2 -rf 2
