I/ Setup:
	1) check if you have installed Maven by 'mvn --version', if not then install it
		from http://maven.apache.org/download.html
		
	2) put the build.properties under src/main/webapp/WEB-INF/classes, edit app.account, app.url, app.id
		put	test.properties under src/test/resources, edit test.app.url, test.app.id,
		put the appengine-web.xml under src/main/webapp/WEB-INF
		
	3) get a good internet connection and  run mvn gae:unpack gae:run . It will take a lot of time
	
	4) open Eclipse and import the project as existing project
		- right-click the project; select properties; click google 
			+web application; select 'this project has a WAR directory' ;browser to src/main/webapp and UNSELECT 'Launch and deploy from this directory'
			+click App Engine , select 'use Google App Engine' , choose the SDK (currently version 1.6.1), usually located under  
			  <Maven local repo>/com/google/appengine/appengine-java-sdk/1.6.1/appengine-java-sdk-1.6.1
		-click Ok
		-Open Java build path, select tab order and export, select all the jars that begin with M2_REPO (have blue circle icon)	click bottom
	
	optional: Configure Email and password in your local settings.xml 
			http://www.kindleit.net/maven_gae_plugin/examples/passwordPrompt.html	
II/ How to use:
	mvn gae:unpack		download the sdk
	mvn gae:rollback	rollback any faulty deployment. 
						(use it when you are asked to use appcfg rollback)
	mvn gae:run			run the local server
	mvn gae:update		deploy to server (with app's id and version in appengine-web.xml)
	mvn clean			clean the target folder 
						(the should use mvn gae:run/mvn integration-test once before use Eclipse)
		
	mvn integration-test	compile and deploy to server, the deploy reports and backups of 
							build.properties and test.properties are placed under report folder
							*Options:
								-Dappversion: overwrite the version under appengine-web.xml
								-Dtest	: test(s) to run
								-DskipTests or -Dmaven.test.skip=true		skip test
			
	others : http://www.kindleit.net/maven_gae_plugin/maven-gae-plugin/plugin-info.html	
			
			
			
III/ build.properties  and test.properties	
	
	build.properties: (src/main/webapp/WEB-INF/classes)
		app.account	=<googleID>@gmail.com				googleID of developer (used to login to appengine)
		app.url		=teammates-<name>.appspot.com		url of the app				
		app.id		=teammates-<name>					app id
		api.authCode=xxxxx								authentication, automatically reset *
	
	test.properties: (src/test/resources)
		test.app.url=teammates-<name>.appspot.com		url of the app (will be the target for the test)
		test.app.id= app.id								app id
		test.app.authCode=xxxxx							authentication, automatically reset *

	*note: api.authCode and test.app.authCode are both reset when run 'mvn integration-test', if you edit it yourself, make sure 
	you edit both.


