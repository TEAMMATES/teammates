I/ Setup:

II/ how to use:
mvn gae:rollback
	rollback any faulty deployment. (use it when you are asked to use appcfg rollback)
mvn gae:run
	run the local server
mvn gae:update
	deploy to server
mvn clean
	clean the target folder (the should use mvn gae:run/mvn integration-test once before use Eclipse)
mvn integration-test
	compile and deploy to server,
	Options:
		-Dversion: overwrite the version under appengine-web.xml
		-Dtest 	: test(s) to run
		-DskipTests or -Dmaven.test.skip=true		skip test
		
build.properties: (src/main/webapp/WEB-INF/classes)
	app.account	=<googleID>@gmail.com				googleID of developer (used to login to appengine)
	app.url		=teammates-<name>.appspot.com		url of the app				
	app.id		=teammates-<name>					app id
	api.authCode=xxxxx								authentication, automatically reset after run 'mvn integration-test'
	
test.properties: (src/test/resources)
	test.app.url=teammates-<name>.appspot.com		url of the app	
	test.app.id= app.id								app id
	test.app.authCode=xxxxx							authentication, automatically reset after run 'mvn integration-test'


the deploy reports and backups of build.properties and test.properties are placed under report folder

