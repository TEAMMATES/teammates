xcopy  /E /Y .\target\classes\teammates .\target\teammates-v4\WEB-INF\classes\teammates
del target\teammates-v4\WEB-INF\appengine-generated\local_db.bin
copy local_db.bin target\teammates-v4\WEB-INF\appengine-generated