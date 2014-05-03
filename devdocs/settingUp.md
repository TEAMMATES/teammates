#Setting Up the Developer Environment
These instructions are for the Windows environment. Instructions for Mac OS is similar, with slight variations that you can figure out yourself.

##Prerequisites
Important: When a version is specified, please install that version instead of the latest version available.

1. Install GitHub for Windows/Mac (recommended), or at least, Git.
2. Clone the source code from `https://github.com/TEAMMATES/repo`
3. Install the latest JDK 7.
4. [Download Eclipse IDE for Java EE Developers](http://www.eclipse.org/downloads/) (version: Kepler).
5. Change the text file encoding of your Eclipse workspace to UTF-8: <br> 
   Go to `Window → Preferences → General → Workspace`, change the 
   `Text file encoding` setting from `Default` to `Other: UTF-8`.
6. Install Google Plugin for Eclipse version 4.3. <br>
   Be careful to omit other plugins shown on the screen 
   (e.g., Google App Engine Tools for Android, GWT plugin).<br>
   Instructions are at https://developers.google.com/eclipse/docs/install-eclipse-4.3 <br>
   Note: Sometimes the update site for the GAE plug-in does not work. In which case, 
   follow the instructions at https://developers.google.com/eclipse/docs/install-from-zip.
7. Install Google App Engine SDK version 1.7.7.1 <br>
   Instructions are at https://developers.google.com/eclipse/docs/using_sdks.<br>
   Download link to the SDK is http://googleappengine.googlecode.com/files/appengine-java-sdk-1.7.7.1.zip.
8. Install the latest [TestNG Eclipse plugin](http://testng.org/doc/eclipse.html).
9. If you plan to use Firefox for testing TEAMMATES 
   (alternatively, you may use Chrome or IE, but FF is the most convenient of the three), 
   downgrade to Firefox 12.0 from [here](https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/)
   {The web driver we use does not work with the latest Firefox.} 
   or install multiple version of Firefox. To do this, grab Firefox 12.0 
   from the above link, and choose a custom setup during install, 
   in which you will be able to specify a different path for this version 
   (e.g. C:\Program Files\Mozilla Firefox 12) in a later step.

##Setting up the dev server
`Dev server` means running the server in localhost mode.

1. Create config files {these are not under revision control because their 
   content vary from developer to developer}.
   * `src/main/resources/build.properties`<br>
   Use build.template.properties as a template (i.e. copy → paste → rename)
   For dev server testing, property values can remain as they are.
   * `src/test/resources/test.properties`<br>
   Create it using test.template.properties (in the same folder). 
   For dev server testing, property values can remain as they are.
   If you have installed multiple versions of Firefox, you need to specify 
   which one to use for testing, by modifying the test.firefox.path property.
   * src/main/webapp/WEB-INF/appengine-web.xml<br>
   Create using appengine-web.template.xml. 
   For dev server testing, property values can remain as they are.
    
2. In Eclipse, go to `Windows → Preferences → Java → Installed JRE` and ensure a 
   JDK (not a JRE) is selected. One of the items in the [Troubleshooting help]
   (https://docs.google.com/document/d/1_p7WOGryOStPfTGA_ZifE1kVlskb1zfd3HZwc4lE4QQ/pub?embedded=true)
    explains how to do this.<br>
    Right-click on the project folder and choose `Run → As Web Application`. 
    After some time, you should see this message on the console 
    `Dev App Server is now running` or something similar.
    The dev server is now ready to serve requests at the given URL. 
    You can verify by visiting the URL in your Browser.
    To login to the system, you need to add yourself as an instructor first (described below).
    
#### Adding instructor accounts

1. Go to `http://[appURL]/admin/adminHomePage` 
   (On your computer, it may be `http://localhost:8888/admin/adminHomePage`) 
2. Log in using your Google ID. If this is the dev server, enter any email 
   address, but remember to check the `log in as administrator` check box. 
3. Enter credentials for an instructor. e.g.,

	> Google id: `teammates.instructor` <br>
	  Name: `John Dorian` <br>
	  Email: `teammates.instructor@gmail.com` <br>
	  Institute: `National University of Singapore` 
	  
##Running the test suite

Before running the test suite, we need to change the time zone of the dev server to `UTC`, 
as expected by test cases. Here is the procedure.

1. Stop the dev server, if it is running already.
2. Specify timezone as a VM argument:
    * Go to the 'run configuration' you used to start the dev server.
    * Click on the to the `Arguments` and add `-Duser.timezone=UTC` to the `VM arguments` text box.
    * Save the configuration for future use:<br>
      Go to the `Common` tab (the last one) and make sure you have selected
      `Save as → Local file` and `Display in favorites menu →  Run, Debug`.
3. Start the server.

This can be done using the `All tests` option under the green `Run` button 
in the Eclipse toolbar. If this option is not available 
(sometimes, Eclipse does not show this option immediately after you set up the project. 
It will appear in subsequent runs. 'Refreshing' will make it appear too.), 
run `src/test/testng.xml` (right click and choose `Run as → TestNG Suite`). Most of the tests should pass.
If a few cases fail (this can happen due to timing issues), run the failed cases 
using the `Run Failed Test` icon in the TestNG tab in Eclipse until they pass. 
The default browser used for testing is the Firefox browser. 
Testing on the Firefox browser is relatively faster as compared to the other browsers, 
and it can be run in the background.

To change the browser that is used in the UI tests, go to the test.properties 
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

##Deploying to staging server
`Staging server` is the server instance you set up on Google App Engine for hosting the app for testing purposes.

1. Create your own app on GAE.
    Suggested app identifier: `teammates-yourname` (e.g. `teammates-john`).<br> 
    The URL of the app will be like this. `http://teammates-yourname.appspot.com`

2. Modify configuration files.
   * `src/main/resources/build.properties` <br>
       Follow instructions in the file itself.
   * `src/test/resources/test.properties` <br>
      Edit the file as instructed in its comments. 
      Note that in addition to your own Google account, you need four other 
      Google accounts as test accounts. Create four Google accounts and add 
      their details to the file.
   * `src/main/webapp/WEB-INF/appengine-web.xml`<br>
      Modify to match app name and app id of your own app.
3. Deploy the application to your staging server.
   * Choose `Deploy to app engine` from eclipse (under the `Google` menu item ![](https://developers.google.com/appengine/docs/java/tools/eclipse/google_menu_button.png) ) and follow the steps.
   * Wait until you see this message in Eclipse console `Deployment completed successfully`
   * Go to appengine dashboard `https://appengine.google.com/dashboard?&app_id=teammates-name`
   * Click `Versions` under `Main` menu on the left bar.
   * Set the version you deployed as the `default`. <br>

> Note: You can skip the steps to set the deployed version as the default. 
   In that case, you can access the deployed app using 
   `http://{version}-dot-{app-id}.appspot.com` e.g. `http://4-18-dot-teammates-john.appspot.com`
    You can run the tests again against the deployed app 
    (modify `test.properties` so that tests execute against the 
    deployed app and not the dev server).
    Note that GAE daily quota will be exhausted after 2-3 runs of the full test suite.

##Troubleshooting
Troubleshooting instructions have moved [here](https://docs.google.com/document/d/1_p7WOGryOStPfTGA_ZifE1kVlskb1zfd3HZwc4lE4QQ/pub?embedded=true)
