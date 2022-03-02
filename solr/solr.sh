#!/usr/bin/env sh

# Create core to run students collection
bin/solr create -c students -s 2 -rf 2
bin/solr config -c students -p 8983 -action set-user-property -property update.autoCreateFields -value false
curl -X POST -H 'Content-type: application/json' --data-binary '{"add-field": {"name": "courseId", "type": "string"}}' localhost:8983/solr/students/schema
curl -X POST -H 'Content-type: application/json' --data-binary '{"add-field": {"name": "email", "type": "string"}}' localhost:8983/solr/students/schema

# Create core to run instructors collection
bin/solr create -c instructors -s 2 -rf 2
bin/solr config -c instructors -p 8983 -action set-user-property -property update.autoCreateFields -value false
curl -X POST -H 'Content-type: application/json' --data-binary '{"add-field": {"name": "courseId", "type": "string"}}' localhost:8983/solr/instructors/schema
curl -X POST -H 'Content-type: application/json' --data-binary '{"add-field": {"name": "email", "type": "string"}}' localhost:8983/solr/instructors/schema

# Create core to run account requests collection
bin/solr create -c accountrequests -s 2 -rf 2
bin/solr config -c accountrequests -p 8983 -action set-user-property -property update.autoCreateFields -value false
curl -X POST -H 'Content-type: application/json' --data-binary '{"add-field": {"name": "email", "type": "string"}}' localhost:8983/solr/accountrequests/schema
curl -X POST -H 'Content-type: application/json' --data-binary '{"add-field": {"name": "institute", "type": "string"}}' localhost:8983/solr/accountrequests/schema
