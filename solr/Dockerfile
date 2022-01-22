FROM solr:8.11.1

COPY solr.sh solr.sh

ENTRYPOINT ["/bin/sh", "-c", "bin/solr start && ./solr.sh && tail -f /var/solr/logs/solr.log"]
