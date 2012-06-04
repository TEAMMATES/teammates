del target\teammates-v4\WEB-INF\appengine-generated\local_db.bin
mkdir target\teammates-v4\WEB-INF\appengine-generated\
copy local_db.bin target\teammates-v4\WEB-INF\appengine-generated\
mvn gae:run