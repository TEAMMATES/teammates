#Setting Up the Developer Environment
These instructions are for the Windows environment. Instructions for Mac OS is similar, with slight variations that you can figure out yourself.

The full tool stack is given at the [end of this document](#toolStack).

##Prerequisites
Important: When a version is specified, please install that version instead of the latest version available.

1. Install GitHub for Windows/Mac (recommended), or at least, Git.
3. Install JDK 7.
4. Download [Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/) (version: Luna).
6. Install Google Plugin for Eclipse version 4.4. <br>
   Be careful to omit other plugins shown on the screen 
   (e.g., Google App Engine Tools for Android, GWT plugin).<br>
   Instructions are at https://developers.google.com/eclipse/docs/install-eclipse-4.4 <br>
   Note: Sometimes the update site for the GAE plug-in does not work. In which case, 
   follow the instructions at https://developers.google.com/eclipse/docs/install-from-zip.
7. Install Google App Engine SDK version 1.9.4 (this is not the latest version)<br>
   Download link to the SDK is https://console.developers.google.com/m/cloudstorage/b/appengine-sdks/o/deprecated/194/appengine-java-sdk-1.9.4.zip.<br>
   Go to `Window → Preferences → Google → App Engine`, click the `Add` button,
   and point it to where you extracted the SDK zip file. <br>
   Further instructions for installing can be found at https://developers.google.com/eclipse/docs/using_sdks.
8. Install the latest [TestNG Eclipse plugin](http://testng.org/doc/eclipse.html).

##Setting up the dev server
`Dev server` means running the server in your own computer.

1. Fork our repo at `https://github.com/TEAMMATES/repo. Clone that fork to your hard disk.
2. Configure Eclipse (if you worry that these settings will interfere with your 
    other projects, you can use a separate eclipse instance for TEAMMATES):
   * Text encoding: Go to `Window → Preferences → General → Workspace`, change the 
   `Text file encoding` setting from `Default` to `Other: UTF-8`.
   * JRE: Go to `Windows → Preferences → Java → Installed JRE` and ensure a 
   JDK (not a JRE) is selected(Use a Java 7 JDK, as recommended by GAE). One of the items in the [Troubleshooting help]
   (https://docs.google.com/document/d/1_p7WOGryOStPfTGA_ZifE1kVlskb1zfd3HZwc4lE4QQ/pub?embedded=true)
    explains how to do this.
    * Tab behavior: In TEAMMATES, we use spaces in place of tabs. 
    `Window → Preferences → General → Editors → Text Editors → Insert spaces for tabs` <br>
    Similarly, configure `Web → CSS Files → Editor`, 
    `Web → HTML Files → Editor`, `XML Files → Editor`, and
    `JavaScript → Code Style → Formatter → Edit → Tab Policy → Spaces Only`
    to indent using 4 spaces instead of tabs.
    * HTML syntax: We prefer not to use the HTML syntax validator provided by Eclipse.
    To turn it off, go to `Window → Preferences → Validation → HTML Syntax Validator` and uncheck the `Build` option.
3. Create main config files {These are not under revision control because their 
   content vary from developer to developer}.
   * `src/main/resources/build.properties`<br>
   Use `build.template.properties` (in the same folder) 
   as the template (i.e. `copy → paste → rename`).
   For now, property values can remain as they are.
   If you want to use Sendgrid for developing and testing email features, create a free SendGrid account and update your username and password in `build.properties`
   * `src/test/resources/test.properties`<br>
   Create it using `test.template.properties`. 
   For now, property values can remain as they are.<br>
   * `src/main/webapp/WEB-INF/appengine-web.xml`<br>
   Create it using `appengine-web.template.xml`. 
   For now, property values can remain as they are.
   * `.settings/com.google.gdt.eclipse.core.prefs`<br>
   Create it using `com.google.gdt.eclipse.core.template.prefs`.
   In the newly created `com.google.gdt.eclipse.core.prefs` file, replace all the `*` in the value of `jarsExcludedFromWebInfLib` to your TEAMMATES project folder,
   e.g. `jarsExcludedFromWebInfLib=*/src/test/resources/lib/appengine/appengine-api-labs.jar` becomes `jarsExcludedFromWebInfLib=C:/TEAMMATES/src/test/resources/lib/appengine/appengine-api-labs.jar` if your TEAMMATES project folder is `C:/TEAMMATES`.
4. Download [this zip file](http://www.comp.nus.edu.sg/~seer/teammates-libs/libsV5.47.zip)
   containing the required library files and unzip it into
   your project folder. Note that this will overwrite some existing library files,
   which is what we want. If you unzipped it into the right location, you should now see
   a `[project folder]/src/test/resources/lib/appengine` containing several jar files.
5. Start the dev server.<br>
    Right-click on the project folder and choose `Run → As Web Application`. 
    After some time, you should see this message on the console 
    `Dev App Server is now running` or something similar.
    The dev server is now ready to serve requests at the URL given in the console output.
    e.g `http://localhost:8888`.<br> 
6. To confirm the server is up, go to the server URL in your Browser.
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
7. On the `dev server`, emails which contains the join link will not be sent to the added instructor.<br>
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
   
##Running the test suite



1. TEAMMATES automated testing requires Firefox (works on Windows and OS-X) 
    or Chrome (Windows only). The default browser used for testing is Firefox 
    because it is faster than Chrome and it can be run in the background.
    Firefox 38.0.5 (latest release as at 7th June 2015) is supported.
   
2. Before running the test suite, both the server and the test environment 
   should be using the UTC time zone. The server and the test environment should 
   also serve the CDN files (files that we off-load to servers such as Google's servers)
   locally instead. In our case, these are files such as jQuery.min.js.
   
   Here is the procedure:
    
    a. Stop the dev server, if it is running already.

    b. Specify timezone as a VM argument: 
       * Go to the `run configuration` Eclipse created when you started the dev server
        (`Run → Run configurations ...` and select the appropriate one).
       * Click on the `Arguments` tab and add `-Duser.timezone=UTC` and '-DisDevEnvironment="true"' to the `VM arguments` text box.
       * Save the configuration for future use: Go to the `Common` tab (the last one) 
       and make sure you have selected `Save as → Local file` and 
       `Display in favorites menu →  Run, Debug`.

    c. Start the server again using the _run configuration_ you created in
       the previous step..<br>
   
4. Run tests. <br>
    This can be done using the `All tests` option under the green `Run` button 
    in the Eclipse toolbar. If this option is not available 
    (sometimes, Eclipse does not show this option immediately after you set up the project. 
    It will appear in subsequent runs. 'Refreshing' will make it appear too.), 
    run `src/test/testng.xml` (right click and choose `Run as → TestNG Suite`). Most of the tests should pass.
    If a few cases fail (this can happen due to timing issues), run the failed cases 
    using the `Run Failed Test` icon in the TestNG tab in Eclipse until they pass. 
    

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

###Running the test suite outside Eclipse
Typically, we run the test suite within Eclipse. But core developers may prefer
to run it outside Eclipse so that they can continue to use Eclipse while the
test suite is running. Given below is the procedure. New developers can omit 
this section.

**On Windows:**
* Build the project in Eclipse (`Project -> Clean`).
* Start the dev server in Eclipse.
* Open a DOS window in the project folder and run the `runtests.bat` 
  in the following manner.<br>
  `runtests.bat  appengine_SDK_location  project_folder_location` <br>
  e.g. `runtests.bat  C:\appengine-java-sdk-1.9.4  C:\teammates`<br>
  This will run the full test suite once and retry the failed tests several times.
* The final result can be viewed by opening `[project folder]/testrunner/test-output/index.html`.
* To run only certain `<test>` segments of the `testng.xml`, add the `-testnames`
  option followed by the names of the `<test>` segments you want to run.<br>e.g.
  `runtests.bat  C:\appengine-java-sdk-1.9.4  C:\teammates -testnames component-tests,sequential-ui-tests,parallel-ui-tests`

**On OS-X:**
TBD
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
   * Choose `Deploy to app engine` from eclipse (under the `Google` menu item ![](https://developers.google.com/appengine/docs/java/tools/eclipse/google_menu_button.png) ) and follow the steps.
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
* **Google App Engine SDK** [version 1.9.4]
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
* **Xerces XML Parser** [version 2.9.1]: This library is required to parse the XML config files. This library may not be needed on some platforms as it may already come packaged on some JREs (particulary windows)
* **SendGrid** Alternative framework to JavaMail for sending emails.

####Tools used in testing

* **Selenium** [version 2.46.0]
    Selenium automates browsers. We use it for automating our UI tests.
    We require Selenium standalone server, Chrome driver, IE driver, and Java language bindings.
* **JavaMail** [version 1.4.5]
    The JavaMail API provides a platform-independent and protocol-independent framework to build mail and messaging applications.
    Usage: For accessing test users' email accounts to examine emails sent from TEAMMATES.
* **TestNG** [latest stable]
    TestNG is a Java test automation framework.
* **QUnit** [version 1.10.0]
    QUnit is a JavaScript unit test suite.
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
* `persistence.xml` : auto-generated. 
    
    
