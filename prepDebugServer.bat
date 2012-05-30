xcopy  /E /Y .\target\classes\teammates .\target\teammates-v4\WEB-INF\classes\teammates
xcopy  /E /Y .\src\main\webapp .\target\teammates-v4
del target\teammates-v4\WEB-INF\appengine-generated\local_db.bin
copy local_db.bin target\teammates-v4\WEB-INF\appengine-generated