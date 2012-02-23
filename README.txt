I/ Setup:
	1) check if you have installed Maven by 'mvn --version', if not then install it
		from http://maven.apache.org/download.html
		
	2) put the build.properties under src/main/webapp/WEB-INF/classes, edit app.account, app.url, app.id
		put	test.properties under src/test/resources, edit test.app.url, test.app.id,
		put the appengine-web.xml under src/main/webapp/WEB-INF
		
	3) get a good internet connection and  run mvn gae:unpack gae:run . It will take a lot of time
	
	4) open Eclipse and import the project as existing project
		- right-click the project; select properties; click google 
			+choose web application; select 'this project has a WAR directory';browse to src/main/webapp 
			and UNSELECT 'Launch and deploy from this directory'
			+click App Engine , select 'use Google App Engine' , choose the SDK (currently version 1.6.1), usually located under  
			  <Maven local repo>/com/google/appengine/appengine-java-sdk/1.6.1/appengine-java-sdk-1.6.1
		-click Ok
		-Open Java build path,
		 	+select tab order and export, select all (not by checking the boxes) the jars that begin
		 	with M2_REPO (have blue circle icon) click bottom
			+select tab libraries, remove faulty dependencies (if any).
		-Click run as web application, and browse to target/teammates-1.0 (can remove run configuration to redo this step)
	optional: Configure email and password in your local settings.xml by following this link
			http://www.kindleit.net/maven_gae_plugin/examples/passwordPrompt.html	
II/ How to use:
	mvn gae:unpack		download the sdk to local .m2 repo
	mvn gae:rollback	rollback any faulty deployment. 
						(use it whenever you are asked to use 'appcfg rollback')
	mvn gae:run			run the local server
	mvn gae:update		deploy to server (with app's id and version in appengine-web.xml)
	mvn clean			clean the target folder 
						(then should use mvn gae:run/mvn integration-test once before use Eclipse)
		
	mvn integration-test	compile and deploy to server, the deploy reports and backups of 
							build.properties and test.properties are placed under report folder
		*Options:
			-Dappversion	: overwrite the version under appengine-web.xml
			-Dtest			: test(s) to run
			-DskipTests or -Dmaven.test.skip=true 	:	skip test(s)
			-DskipApiCode=true					:	skip changing authCode
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

IV/ Troubleshooting:
ERROR: FileWriter at Scenario.java
	- can ignore it. Simply because eclipse realize Teammates as a GAE project and FileWriter class is not supported but we 
	only use it for testing.
ERROR: java.lang.RuntimeException: Unable to locate the App Engine agent 
 		java.lang.RuntimeException: Unable to discover the Google App Engine SDK root. 
	-make sure the app-engine-sdk (properties-> google-> app engine) is the same version as the one in pom.xml, should choose the sdk in the .m2 repo. Sometime should click 'configure SDKs', deselect and select again or choose another sdk then choose the correct one agina
	-make sure all the maven dependencies has been move to bottom
	
ERROR: click to properties-> google -> appengine get a blank screen with a error popup
	-choose web application; select 'this project has a WAR directory';browser to src/main/webapp 
	and UNSELECT 'Launch and deploy from this directory'
	-Make sure the appengine-web.xml is placed at src/main/webapp/WEB-INF
ERROR: Supplied application has to contain WEB-INF directory.
	-Delete the run configuration and run again, browse to target/teammates-1.0
ERROR: javax.xml.parsers.FactoryConfigurationError: Provider org.apache.xerces.jaxp.SAXParserFactoryImpl not found
	-Remove xercesImpl-(version).jar from classpath
ERROR: related to bundler registration of datanuclus
	-Remove one instance of datanucleus-appengine-1.0.10.final.jar, datanucleus-core-1.1.5.jar and datanucleus-jpa-1.1.5.jar from classpath
	
in general, 
	-if having problem with running tests, check the authCode again. (can see the authCode with any deployment under folder report)
	-if having problem with running eclipse, use 'mvn eclipse:clean eclipse:eclipse' and import the project again, remove jar file from above 2 error cases (4 jars file), and follow the step.
