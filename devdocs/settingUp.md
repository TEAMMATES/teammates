#Table of Contents
* [Setting Up the Developer Environment](#setting-up-the-developer-environment)
* [Prerequisites](#prerequisites)
* [Setting up the dev server](#setting-up-the-dev-server)
* [Setting up static analysis tools](#setting-up-static-analysis-tools)
* [Running the test suite](#running-the-test-suite)
* [Deploying to a staging server](#deploying-to-a-staging-server)
* [Running client scripts](#running-client-scripts)
* [Troubleshooting](#troubleshooting)
* [Tool stack](#tool-stack)
* [Config points](#config-points)

## Setting Up the Developer Environment
>If you encounter any problems during the setting up process, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request in our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

These instructions work for Linux, OS X as well as for the Windows
environment. The only difference for Windows environment is that the command `./gradlew` should be replaced by `gradlew.bat` everywhere.

The full tool stack is given at the [end of this document](#tool-stack).

## Prerequisites
Important: When a version is specified, please install that version instead of the latest version available.

1. Install GitHub for Windows/Mac (recommended), or at least, Git.
2. Install JDK 7.
3. Download [Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/) (version: Luna).
4. Install Google Plugin for Eclipse version 4.4. <br>
   Be careful to omit other plugins shown on the screen 
   (e.g., Google App Engine Tools for Android, GWT plugin).<br>
   Instructions are at https://developers.google.com/eclipse/docs/install-eclipse-4.4 <br>
   Note: Sometimes the update site for the GAE plug-in does not work. In which case, 
   follow the instructions at https://developers.google.com/eclipse/docs/install-from-zip.
5. Install the latest [TestNG Eclipse plugin](http://testng.org/doc/download.html).

## Setting up the dev server
`Dev server` means running the server in your own computer.

1. Fork our repo at https://github.com/TEAMMATES/repo. Clone that fork to your hard disk.
2. Run the command `./gradlew appengineDownloadSdk` to obtain the Google App Engine SDK (version specified in `build.gradle`).
3. Configure Eclipse (if you worry that these settings will interfere with your other projects, you can use a separate Eclipse instance for TEAMMATES):
   * Google App Engine: Go to `Window → Preferences → Google → App Engine` (Mac: `Eclipse → Preferences → Google → App Engine`),
   click the `Add` button, and point it to where Gradle kept the SDK.
   This directory can be found by running the command `./gradlew printUserHomeDir`.<br>
   Further instructions for installing can be found at https://developers.google.com/eclipse/docs/using_sdks.
   * Text encoding: Go to `Window → Preferences → General → Workspace` (Mac: `Eclipse → Preferences → General → Workspace`), change the 
   `Text file encoding` setting from `Default` to `Other: UTF-8`.
   * JRE: Go to `Windows → Preferences → Java → Installed JRE` (Mac: `Eclipse → Preferences → Java → Installed JRE`) and ensure a 
   JDK (not a JRE) is selected(Use a Java 7 JDK, as recommended by GAE). One of the items in the [Troubleshooting help]
   (troubleshooting-guide.md)
    explains how to do this.
    * Tab behavior: In TEAMMATES, we use spaces in place of tabs. 
    `Window → Preferences → General → Editors → Text Editors → Insert spaces for tabs` (Mac: `Eclipse → Preferences → General → Editors → Text Editors → Insert spaces for tabs`)<br>
    Similarly, configure `Web → CSS Files → Editor`, 
    `Web → HTML Files → Editor`, `XML Files → Editor`, and
    `JavaScript → Code Style → Formatter → Edit → Tab Policy → Spaces Only`
    to indent using 4 spaces instead of tabs.
    * HTML syntax: We prefer not to use the HTML syntax validator provided by Eclipse.
    To turn it off, go to `Window → Preferences → Validation → HTML Syntax Validator` (Mac: `Eclipse → Preferences → Validation → HTML Syntax Validator`) and uncheck the `Build` option.
4. Run the command `./gradlew setup`.<br>
   This creates the main config files {These are not under revision control because their content vary from developer to developer}.
   * `.project`<br>
   * `gradle.properties`<br>
   If you want to use a JDK other than the one specified in your PATH variable, add the value to the variable `org.gradle.java.home`.<br>
   This value must be a valid **JDK 1.7** directory.<br>
   * `src/main/resources/build.properties`<br>
   For now, property values can remain as they are.<br>
   (Optional) If you want to use alternative email services to develop and test email features, refer to [this document](emails.md).
   * `src/test/resources/test.properties`<br>
   Append a unique id (e.g. your name) to **each** of the default accounts found at the bottom of this file. 
   e.g. change `test.student1.account=alice.tmms` to `test.student1.account=alice.tmms.KevinChan`<br>
   * `src/main/webapp/WEB-INF/appengine-web.xml`<br>
   For now, property values can remain as they are.
4. Run the command `./gradlew resetEclipseDeps`.<br>
   This will download the dependencies required by TEAMMATES and places them in the appropriate directories to be used by Eclipse.<br>
   In addition, it will generate the `.classpath` file for Eclipse configuration.<br>
   This command can be run again whenever the dependencies need to be updated.
5. Start Eclipse and go to `File → Import...` and select `Existing Projects into Workspace` under `General`. Set the `root directory` to the location where
   the repo is cloned. Click `Finish`.
6. Start the dev server.<br>
    Right-click on the project folder and choose `Run → As Web Application`. 
    After some time, you should see this message on the console 
    `Dev App Server is now running` or something similar.
    The dev server is now ready to serve requests at the URL given in the console output.
    e.g `http://localhost:8888`.<br> 
7. To confirm the server is up, go to the server URL in your Browser.
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
8. On the `dev server`, emails which contains the join link will not be sent to the added instructor.<br>
   Instead, you can use the join link given after adding an intructor, to complete the joining process.<br>
   Remember to change the URL of the link if necessary, but keep the parameters.<br>
   e.g. Change <b>`http://teammates-john.appspot.com`</b>`/page/instructorCourseJoin?key=F2AD69F8994BA92C8D605BAEDB35949A41E71A573721C8D60521776714DE0BF8B0860F12DD19C6B955F735D8FBD0D289&instructorinstitution=NUS` <br>
   to <b>`http://localhost:8888`</b>`/page/instructorCourseJoin?key=F2AD69F8994BA92C8D605BAEDB35949A41E71A573721C8D60521776714DE0BF8B0860F12DD19C6B955F735D8FBD0D289&instructorinstitution=NUS`
9. Now, to access the dev server as a student, first make sure you are logged in as an instructor. Add a course for yourself and then add the students for the course.<br>
   After that, log in as admin by going to `http://localhost:8888/admin/adminSearchPage` and provide the same GoogleID you used for logging in step 6.<br>
   Search for the student you added in as instructor. From the search results, click anywhere on the desired row(except on the student name) to get the course join link for that student.<br>
   Then, log out and use that join link to log in as a student. You have the required access now.<br>
   (Make sure you use the `http://localhost:8888/` as the host instead of the one given in the join link)<br>   
   Alternative : Run the test cases, they create several student accounts in the datastore. Use one of them to log in.<br>

## Setting up static analysis tools

TEAMMATES uses a number of static analysis tools in order to maintain code quality and measure code coverage.
It is highly encouraged to set up these tools in your local development environment.
Refer to [this document](staticAnalysis.md) for details on the tools used, how to set them up, and how to run them locally.

## Running the test suite



1. TEAMMATES automated testing requires Firefox (works on Windows and OS-X).
   Only Firefox between versions 38.0.5 and 46.0 are supported, although the primary support is for 46.0.
   To downgrade your Firefox version, obtain the executable from [here](https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/).
   If you want to use a different path for this version, choose `custom setup` during install.
   After installation, specify the Firefox path in `test.properties` by modifying the `test.firefox.path` property.
   Remember to disable the auto-updates (`Options → Advanced tab → Update`).
   
2. Before running the test suite, both the server and the test environment 
   should be using the UTC time zone.
   
   Here is the procedure:
    
    a. Stop the dev server, if it is running already.

    b. Specify timezone as a VM argument: 
       * Go to the `run configuration` Eclipse created when you started the dev server
        (`Run → Run configurations ...` and select the appropriate one).
       * Click on the `Arguments` tab and add `-Duser.timezone=UTC` to the `VM arguments` text box.
       * Save the configuration for future use: Go to the `Common` tab (the last one) 
       and make sure you have selected `Save as → Local file` and 
       `Display in favorites menu →  Run, Debug`.

    c. Start the server again using the _run configuration_ you created in
       the previous step..<br>
   
3. Run tests. <br>
   Test can be run using the configurations available under the green `Run` button
   in the Eclipse toolbar. There are several configurations that are provided by default.
   These are:

      * `All tests` - This runs the files `src/test/testng-travis.xml` as well as `src/test/testng-local.xml`.
      * `Travis tests` - This runs the file `src/test/testng-travis.xml`. It contains all the tests that are run by Travis.
      * `Local tests` - This runs the file `src/test/testng-local.xml`. It contains all the tests that need
                         to be run locally by developers. `Dev green` mean passing all the tests in this configuration.
      * `Staging tests` - This runs a subset of the tests in `src/test/testng-travis.xml`.
                           This is run before deploying to a staging server.

   New developers should at least run the `Local tests` and have all of them passing on their local environments.
   Running `Travis tests` and getting everything passing is also recommended.

   Additionally, configurations that run the tests with `GodMode` turned on are also provided.
   More info on this can be found [here](/devdocs/godmode.md). Sometimes, Eclipse does not show
   these options immediately after you set up the project. 'Refreshing' the project should fix that. 

   When running the test cases, if a few cases fail (this can happen due to timing issues),
   run the failed cases using the `Run Failed Test` icon in the TestNG tab in Eclipse
   until they pass. 


To change the browser that is used in the UI tests, go to the `test.properties` 
file and change the `test.selenium.browser` value to the browser you want to test. 
Currently only `firefox` is accepted.
In addition, you need to configure the browser you have selected so that 
it works with the test suite. 

####Firefox

* If you are planning to test changes to JavaScript code, disable 
  javascript caching for Firefox - Enter `about:config` into the 
  Firefox address bar and set: `network.http.use-cache = false`
* If you have installed a separate Firefox version, you can choose which 
  Firefox binary to use. You can specify the custom path in `test.firefox.path` 
  value inside the `test.properties` file.

###Running the test suite outside Eclipse
Typically, we run the test suite within Eclipse. But core developers may prefer
to run it outside Eclipse so that they can continue to use Eclipse while the
test suite is running. Given below is the procedure. New developers can omit 
this section.

* Build the project in Eclipse (`Project -> Clean`).
* Start the dev server in Eclipse.
* Run the following command in the project root folder:<br>
  `./gradlew travisTests`<br>
  This will run the full test suite once and retry the failed tests several times.<br>
* The final result can be viewed by opening `{project folder}/build/test-try-{n}/index.html`,
  where `{n}` is the sequence number of the test run.
  
## Deploying to a staging server
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
   * Choose `Deploy to app engine` from eclipse (under the `Google` menu item ![](https://cloud.google.com/appengine/docs/python/images/transform_resize_after.jpg) ) and follow the steps.
   * Wait until you see this message in Eclipse console `Deployment completed successfully`
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


## Running client scripts
Client scripts are scripts that remotely manipulate data on GAE via its Remote API. Most of developers may not need to write and/or run client scripts but if you are to do so, additional steps are required:

1. Download and install Google Cloud SDK at https://cloud.google.com/sdk/downloads.

2. Run `gcloud auth login` in the terminal of your PC and choose your google account for authentication.

3. Now you can run your scripts.


## Troubleshooting
Troubleshooting instructions are given [in this document](troubleshooting-guide.md)

## Tool stack

####Deployment environment
* **Google App Engine** (GAE)
* **Java** [version 7, this is the highest version supported by GAE]
* **Live site**: http://teammatesv4.appspot.com

####Development environment
* **Eclipse** IDE for EE developers [version Luna]
* **Google App Engine Plugin for Eclipse** [version 4.4]
* **Google App Engine SDK** [version 1.9.27]
* **GitHub** : Used to host the repo and code reviewing.
* **Gradle** : Build and dependency management tool.
* **CheckStyle, PMD, FindBugs, Macker, ESLint** [all latest stable versions]: Static analysis tools for code quality check. The details of these tools can be found in [this document](staticAnalysis.md).
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
* **Jsoup** [version 1.9.2]: This library is required to parse HTML files. It is needed by some email services to obtain the plain text component from the HTML component. In addition, this is also used during UI testing, for doing a logical comparison of the pages generated against expected pages.
* **SendGrid, Mailgun, Mailjet** Alternative framework to JavaMail for sending emails. The details of these tools can be found in [this document](emails.md).
* **Google Cloud SDK**: This is a set of tools that helps us manage resources and applications hosted on Google Cloud Platform. We use it to run client scripts on GAE remotely.

####Tools used in testing

* **Selenium** [version 2.53.0]
    Selenium automates browsers. We use it for automating our UI tests.
    We require Selenium standalone server and Java language bindings.
* **JavaMail** [version 1.4.5]
    The JavaMail API provides a platform-independent and protocol-independent framework to build mail and messaging applications.
    Usage: For accessing test users' email accounts to examine emails sent from TEAMMATES.
* **TestNG** [latest stable]
    TestNG is a Java test automation framework.
* **EclEmma/JaCoCo** [latest stable]
    JaCoCo is a Java code coverage library. EclEmma is its plugin and integration for Eclipse.
* **QUnit** [version 1.22.0]
    QUnit is a JavaScript unit test suite.
* **Blanket.js** [version 1.2.1]
    Blanket.js is a JavaScript code coverage library.
* **HttpUnit** [version 1.7]
    We use the ServletUnit component of HttpUnit to create HttpServletUnit objects used for testing.

## Config points
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
    
