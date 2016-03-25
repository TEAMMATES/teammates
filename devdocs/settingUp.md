#Setting Up the Developer Environment
>If you encounter any problems during the setting up process, please refer to our [troubleshooting guide](https://docs.google.com/document/d/1_p7WOGryOStPfTGA_ZifE1kVlskb1zfd3HZwc4lE4QQ/pub?embedded=true) before posting a help request in our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

These instructions work for Linux, OS X as well as for the Windows 
environment. The only difference for Windows environment is that the command `./gradlew` should be replaced by `gradlew.bat` everywhere.

The full tool stack is given at the [end of this document](#toolStack).

##Prerequisites
Important: When a version is specified, please install that version instead of the latest version available.

1. Install GitHub for Windows/Mac (recommended), or at least, Git.
2. Install JDK 7.
3. Download [Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/) (version: Luna).
4. Install the BuildShip Gradle Plugin for Eclipse. Instructions can be found [here](https://github.com/eclipse/buildship/blob/master/docs/user/Installation.md)

##Setting up the dev server
`Dev server` means running the server in your own computer.

1. Fork our repo at https://github.com/TEAMMATES/repo. Clone that fork to your hard disk.
2. Navigate to the directory where you cloned Teammates and perform the following steps:
   * Copy and rename `gradle.properties.template` to `gradle.properties`
   * Set `org.gradle.java.home` to the location where JDK 7 is installed
   * Run the following command: `./gradlew appengineRun`
   This command downloads all the dependencies required by Teammates and starts the dev server on `localhost:8888`.
   Depending on your network speed, the downloading of the dependencies might take a while.
   After some time, you should see this message on the console 
   `Build Successful`.
   The dev server is now ready to serve requests at `http://localhost:8888`.
   To stop the server, simply run `./gradlew appengineStop`.
3. Modify main config files. {These are not under revision control because their 
   content vary from developer to developer}.
   * `src/main/resources/build.properties`<br>
   If you want to use Sendgrid for developing and testing email features, create a free SendGrid account and update your username and password in `build.properties`
   * `src/test/resources/test.properties`<br>
   Append a unique id (e.g. your name) to **each** of the default accounts found at the bottom of this file. 
   e.g. change `test.student1.account=alice.tmms` to `test.student1.account=alice.tmms.KevinChan`<br>
4. To confirm the server is up, go to the server URL in your Browser.
   To log in to the system, you need to add yourself as an instructor first:
   * Go to `http://[appURL]/admin/adminHomePage` 
   (On your computer, it may be `http://localhost:8888/admin/adminHomePage`) 
   * Log in using your Google ID. If this is the dev server, enter any email 
   address, but remember to check the `log in as administrator` check box. 
   * Enter credentials for an instructor. e.g.,<br>
      Google id: `teammates.instructor` <br>
      Name: `John Dorian` <br>
      Email: `teammates.instructor@university.edu` <br>
      Institute: `National University of Singapore` 
5. On the `dev server`, emails which contains the join link will not be sent to the added instructor.<br>
   Instead, you can use the join link given after adding an intructor, to complete the joining process.<br>
   Remember to change the URL of the link if necessary, but keep the parameters.<br>
   e.g. Change <b>`http://teammates-john.appspot.com`</b>`/page/instructorCourseJoin?key=F2AD69F8994BA92C8D605BAEDB35949A41E71A573721C8D60521776714DE0BF8B0860F12DD19C6B955F735D8FBD0D289&instructorinstitution=NUS` <br>
   to <b>`http://localhost:8888`</b>`/page/instructorCourseJoin?key=F2AD69F8994BA92C8D605BAEDB35949A41E71A573721C8D60521776714DE0BF8B0860F12DD19C6B955F735D8FBD0D289&instructorinstitution=NUS`
8. Now, to access the dev server as a student, first make sure you are logged in as an instructor. Add a course for yourself and then add the students for the course.<br>
   After that, log in as admin by going to `http://localhost:8888/admin/adminSearchPage` and provide the same GoogleID you used for logging in step 6.<br>
   Search for the student you added in as instructor. From the search results, click anywhere on the desired row(except on the student name) to get the course join link for that student.<br>
   Then, log out and use that join link to log in as a student. You have the required access now.<br>
   (Make sure you use the `http://localhost:8888/` as the host instead of the one given in the join link)<br>   
   Alternative : Run the test cases, they create several student accounts in the datastore. Use one of them to log in.<br>

##Setting up Eclipse

1. Start Eclipse and go to `File → Import...`
2. If you installed BuildShip correctly, you should be able to find `Gradle → Gradle Project`.
3. Select `Gradle Project`
4. Set the project root directory to the directory containing Teammates.
5. Click Finish.
6. Configure Eclipse (if you worry that these settings will interfere with your 
    other projects, you can use a separate eclipse instance for TEAMMATES):
   * Text encoding: Go to `Window → Preferences → General → Workspace` (Mac: `Eclipse → Preferences → General → Workspace`), change the 
   `Text file encoding` setting from `Default` to `Other: UTF-8`.
   * JRE: Go to `Windows → Preferences → Java → Installed JRE` (Mac: `Eclipse → Preferences → Java → Installed JRE`) and ensure a 
   JDK (not a JRE) is selected(Use a Java 7 JDK, as recommended by GAE). One of the items in the [Troubleshooting help]
   (https://docs.google.com/document/d/1_p7WOGryOStPfTGA_ZifE1kVlskb1zfd3HZwc4lE4QQ/pub?embedded=true)
    explains how to do this.
    * Tab behavior: In TEAMMATES, we use spaces in place of tabs. 
    `Window → Preferences → General → Editors → Text Editors → Insert spaces for tabs` (Mac: `Eclipse → Preferences → General → Editors → Text Editors → Insert spaces for tabs`)<br>
    Similarly, configure `Web → CSS Files → Editor`, 
    `Web → HTML Files → Editor`, `XML Files → Editor`, and
    `JavaScript → Code Style → Formatter → Edit → Tab Policy → Spaces Only`
    to indent using 4 spaces instead of tabs.
    * HTML syntax: We prefer not to use the HTML syntax validator provided by Eclipse.
    To turn it off, go to `Window → Preferences → Validation → HTML Syntax Validator` (Mac: `Eclipse → Preferences → Validation → HTML Syntax Validator`) and uncheck the `Build` option.
   
##Running the entire test suite

1. TEAMMATES automated testing requires Firefox (works on Windows and OS-X) 
    or Chrome (Windows only). The default browser used for testing is Firefox 
    because it is faster than Chrome and it can be run in the background.
    Firefox 38.0.5 (latest release as at 7th June 2015) is supported.
2. Navigate to the directory containing Teammates.
3. Ensure that the dev server is not running. Run `./gradlew appengineStop` to be safe.
4. Run `./gradlew`
5. That's it!

Running the entire test suite can be very resource intensive for your machine. Use caution!

To change the browser that is used in the UI tests, go to the `test.properties` 
file and change the `test.selenium.browser` value to the browser you want to test. 
Possible values are `firefox` or `chrome`. 
In addition, you need to configure the browser you have selected so that 
it works with the test suite. 

####Firefox

* If you are planning to test changes to JavaScript code, disable 
  javascript caching for Firefox - Enter `about:config` into the 
  Firefox address bar and set: `network.http.use-cache = false`
* If you have installed a separate Firefox version, you can choose which 
  Firefox binary to use. You can specify the custom path in `test.firefox.path` 
  value inside the `test.properties` file.

####Chrome
* If you are planning to test changes to JavaScript code, disable 
  javascript caching for Chrome : 
    * Press ctrl-shift-j to bring up the Web Console. 
    * At the bottom-right corner, there is a settings button. Click on that. 
    * Under the General tab, check 'Disable Cache'
* The chromedriver process started by the test suite will not automatically 
  get killed after the tests have finished executing. 
  You will need to manually kill these processes after the tests are done. 
  On Windows, you can do this using the Task Manager or `tskill` DOS command. 

##Running parts of the test suite

The test suite used by Teammates is divided into some broad (overlapping) categories. These include:

1. Component Tests
2. UI Tests
3. Staging Tests
4. Occasional Tests
5. Rare Tests

Each of these can be run by running the appropriate command based on the category. For example, running the component tests requires running the command `./gradlew componentTests` and so on.

After a test run, you can see the test results on the console. If there were any failing tests, a test report should open automatically. Any failed tests can be rerun immediately after, by running the command `./gradlew failedTests`.
  
##Deploying to a staging server
`Staging server` is the server instance you set up on Google App Engine for hosting the app for testing purposes.

1. Create your own app on GAE.
    Suggested app identifier: `teammates-yourname` (e.g. `teammates-john`).<br> 
    The URL of the app will be like this. `http://teammates-yourname.appspot.com`

2. Modify configuration files.
   * `src/main/resources/build.properties` <br>
      Edit the file as instructed in its comments.
   * `src/test/resources/test.properties` <br>
      Edit the file as instructed in its comments. 
   * `src/main/webapp/WEB-INF/appengine-web.xml`<br>
      Modify to match app name and app id of your own app.
      
3. Deploy the application to your staging server.
   * Run the command `./gradlew appengineUpdate`
   * Wait until you see this message on the console `Deployment completed successfully`
   * Go to appengine dashboard `https://appengine.google.com/dashboard?&app_id=teammates-name`
   * Click `Versions` under `Main` menu on the left bar.
   * Set the version you deployed as the `default`. <br>
   Note: You can skip the steps to set the deployed version as the default. 
   In that case, you can access the deployed app using 
   `http://{version}-dot-{app-id}.appspot.com` e.g. `http://4-18-dot-teammates-john.appspot.com`
    You can run the tests again against the deployed app 
    (modify `test.properties` so that tests execute against the 
    deployed app and not the dev server).
    Note that GAE daily quota will be exhausted after 2-3 runs of the full test suite.


##Running client scripts
Client scripts are scripts that remotely manipulate data on GAE via its Remote API. Most of developers may not need to write and/or run client scripts but if you are to do so, additional steps are required:

1. Download and install Google Cloud SDK at https://cloud.google.com/sdk/downloads.

2. Run `gcloud auth login` in the terminal of your PC and choose your google account for authentication.

3. Now you can run your scripts.


##Troubleshooting
Troubleshooting instructions are given [in this document](https://docs.google.com/document/d/1_p7WOGryOStPfTGA_ZifE1kVlskb1zfd3HZwc4lE4QQ/pub?embedded=true)

##<a name="toolStack"></a>Tool stack

####Deployment environment
* **Google App Engine** (GAE)
* **Java** [version 7, this is the highest version supported by GAE]
* **Live site**: http://teammatesv4.appspot.com

####Development environment
* **Eclipse** IDE for EE developers [version Luna]
* **Google App Engine Plugin for Eclipse** [version 4.4]
* **Google App Engine SDK** [version 1.9.27]
* **GitHub** : Used to host the repo and code reviewing.
* [**PowerPointLabs**](http://PowerPointLabs.info) [Sister project]: Used for creating demo videos.
* Optional: [**HubTurbo**](https://github.com/HubTurbo/HubTurbo/wiki/Getting-Started) [Sister project]: 
  Can be used as a client for accessing the GitHub issue tracker.
* Optional: **SourceTree** or **GitHub for Windows/Mac** or 
  equivalent [version: latest stable] to use as a GUI client for Git.

####Tools used in implementation
* **HTML** [version 5, using latest features is discouraged due to lack of enough Browser support], JavaScript, CSS
* **Bootstrap** [version 3.1.1], as the front-end UI framework
* **jQuery** [version 1.11.1]
  jQuery is a JavaScript Library that simplifies HTML document traversing, event handling, animating, and Ajax interactions for rapid web development.
* **JSON** (JavaScript Object Notation): JSON is a lightweight data-interchange format. It is easy for humans to read and write. It is easy for machines to parse and generate. It is based on a subset of the JavaScript.
* **Gson** [version 2.2.2] Gson is a Java library that can be used to convert Java Objects into their JSON representation. It can also be used to convert a JSON string to an equivalent Java object.
* **JavaServer Pages (JSP)**: JSP technology provides a simplified way to create dynamic web content. A JSP page can be thought as an HTML page with embedded Java code snippets.
* **JavaServer Pages Standard Tag Library (JSTL)**: JSTL extends the JSP specification by providing a standard set of JSP tags for common tasks, and allows creation of custom tags for use in JSP files.
* **Java Servlets**: Java Servlet technology provides a simple, consistent mechanism for extending the functionality of a Web server and for accessing existing business systems. A servlet can almost be thought of as an applet that runs on the server side--without a face.
* **Java Data Objects (JDO)** [version 2.3; while GAE supports JDO 3.0 as well, we continue to use JDO 2.3 because it is easier to set up.]
  JDO is a standard interface for storing objects containing data into a database. The standard defines interfaces for annotating Java objects, retrieving objects with queries, and interacting with a database using transactions. An application that uses the JDO interface can work with different kinds of databases without using any database-specific code, including relational databases, hierarchical databases, and object databases.
* **Datanucleus Access Platform** [version 1; while GAE supports Datanucleus v2 as well, the version that goes with JDO 2.3 is v1]
  The DataNucleus Access Platform provides persistence and retrieval of data to a range of datastores using a range of APIs, with a range of query languages.
  Comes with App Engine SDK.
* **Java Persistence API (JPA)** [version 1.0]: JPA is a standard interface for accessing databases in Java, providing an automatic mapping between Java classes and database tables.
* **Xerces XML Parser** [version 2.9.1]: This library is required to parse the XML config files. This library may not be needed on some platforms as it may already come packaged on some JREs (particulary windows)
* **SendGrid** Alternative framework to JavaMail for sending emails.
* **Google Cloud SDK**: This is a set of tools that helps us manage resources and applications hosted on Google Cloud Platform. We use it to run client scripts on GAE remotely.

####Tools used in testing

* **Selenium** [version 2.46.0]
    Selenium automates browsers. We use it for automating our UI tests.
    We require Selenium standalone server, Chrome driver, IE driver, and Java language bindings.
* **JavaMail** [version 1.4.5]
    The JavaMail API provides a platform-independent and protocol-independent framework to build mail and messaging applications.
    Usage: For accessing test users' email accounts to examine emails sent from TEAMMATES.
* **TestNG** [latest stable]
    TestNG is a Java test automation framework.
* **QUnit** [version 1.22.0]
    QUnit is a JavaScript unit test suite.
* **Blanket.js** [version 1.2.1]
    Blanket.js is a JavaScript code coverage library.
* **NekoHtml** [version 1.9.22]
    NekoHTML is a simple HTML scanner and tag balancer that enables application programmers to parse HTML documents and access the information using standard XML interfaces.
    NekoHTML is included in the Selenium libraries.
    Usage: During UI testing, for doing a logical comparison of the pages generated against expected pages.
* **HttpUnit** [version 1.7]
    We use the ServletUnit component of HttpUnit to create HttpServletUnit objects used for testing.

####Config points
There are several files used to configure various aspects of the system.

**main:**
* `build.properties` : This is the main general purpose configuration file used by the Web app.
* `test.properties` : Contains configuration values for the test driver.
* `appengine-web.xml` : Contains configuration for deploying the application on App Engine.

**other:**
* `logging.properties` : Configuration for java.util.logging users.
* `log4j.properties` : Configuration for log4j users. Not used by us.
* `web.xml` : This is the configurations for the webserver. It specifies servlets to run, mapping from URLs to servlets/JSPs, security constraints, etc.
* `cron.xml` : This specifies cron jobs to run.
* `queue.xml` : Specifies configuration of task queues.
* `jdoconfig.xml` : Specifies the JDO configuration.
* `persistence.xml` : Specifies the JPA configuration. 
    
