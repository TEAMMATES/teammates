# Development Guidelines

These are the common tasks involved when working on features, enhancements, bug fixes, etc. for TEAMMATES.

* [Building JavaScript files](#building-javascript-files)
* [Managing the dev server](#managing-the-dev-server)
* [Logging in to a TEAMMATES instance](#logging-in-to-a-teammates-instance)
* [Testing](#testing)
* [Deploying to a staging server](#deploying-to-a-staging-server)
* [Running client scripts](#running-client-scripts)
* [Config points](#config-points)

The instructions in all parts of this document work for Linux, OS X, and Windows, with the following pointers:
- Replace `./gradlew` to `gradlew.bat` if you are using Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.
- It is assumed that the development environment has been correctly set up. If this step has not been completed, refer to [this document](settingUp.md).

> If you encounter any problems during the any of the processes, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request on our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

## Building JavaScript files

Our JavaScript code is written in ECMAScript 6 (ES6) syntax, however many of the existing Web browsers today still have limited support for ES6.<br>
To resolve this, we need to *transpile* ("build" afterwards) these JavaScript files into ECMAScript 5 syntax which is supported by (almost) all browsers.

Run the following command to build the JavaScript files for the application's use:
```sh
npm run build
```

## Managing the dev server

> `Dev server` is the server run in your local machine.

### With command line

* Change the value of `org.gradle.daemon` in `gradle.properties` to `true`.

#### Starting the dev server

Run the following command:

```sh
./gradlew appengineRun
```

Wait until the task exits with a `BUILD SUCCESSFUL`.
The dev server URL will be `http://localhost:8888` as specified in `build.gradle`.

#### Stopping the dev server

Run the following command:

```sh
./gradlew appengineStop
```

### With Eclipse

#### Starting the dev server

Right-click on the project folder and choose `Run As → Web Application`.<br>
After some time, you should see this message (or similar) on the Eclipse console: `Dev App Server is now running`.
The dev server URL will be given at the console output, e.g `http://localhost:8888`.

#### Stopping the dev server

Click the "Terminate" icon on the Eclipse console.

### With IntelliJ

> If this is your first time running the dev server, you will need to set up the required `Run Configuration`.

#### Set up the Run Configuration

1. Go to `File → Project Structure...`.
1. Under `Artifacts → Gradle : <your-project-name>.war (exploded)`, check `Include in project build`.
1. Click `OK`.
1. Got to `Run → Edit Configurations...`.
1. Click `+ → Google AppEngine Dev Server`.
1. Name it `Dev Server`.
1. Click `Configure` next to `Application server`.
1. Click `+ → ...`. Select the App Engine SDK (`appengine-java-sdk-<version>` sub-folder) you downloaded in Step 3 of the [Setting up a development environment](settingUp.md) guide.
1. Under `Open browser`, uncheck `After launch`.
1. Set the `JRE` to `1.7`.
1. Set the `Port` to `8888`.
1. Under `Before launch`, click `+ → Run Gradle task`.
1. Click the folder icon, select the local repository as the Gradle project and type "assemble" into the `Tasks` field.
1. Click `OK`.
1. Remove "Build" by selecting it and clicking `-`.
1. Click `OK`.

#### Starting the dev server

Go to `Run → Run...` and select `Dev Server` in the pop-up box.

#### Stopping the dev server

Go to `Run → Stop` or hit `Ctrl + F2` (Windows).

## Logging in to a TEAMMATES instance

This instruction set applies for both dev server and production server, with slight differences explained where applicable.
- The local dev server is assumed to be accessible at `http://localhost:8888`.
- If a URL is given as relative, prepend the server URL to access the page, e.g `/page/somePage` is accessible in dev server at `http://localhost:8888/page/somePage`.

### As administrator

1. Go to any administrator page, e.g `/admin/adminHomePage`.
1. On the dev server, log in using any username, but remember to check the `Log in as administrator` check box. You will have the required access.
1. On the production server, you will be granted the access only if your account has administrator permission to the application.

### As instructor

You need an instructor account which can be created by administrators.

1. Log in to `/admin/adminHomePage` as an administrator.
1. Enter credentials for an instructor, e.g<br>
   Short Name: `Instructor`<br>
   Name: `John Dorian`<br>
   Email: `teammates.instructor@university.edu`<br>
   Institution: `National University of Singapore`<br>
1. The system will send an email containing the join link to the added instructor.<br>
   On the dev server, this email will not be sent. Instead, you can use the join link given after adding an instructor to complete the joining process.<br>
   Remember to change the base URL of the link if necessary, but keep the parameters,<br>
   e.g change **`https://teammates-john.appspot.com`**`/page/instructorCourseJoin?key=F2AD69F8994BA92C8D605BAEDB35949A41E71A573721C8D60521776714DE0BF8B0860F12DD19C6B955F735D8FBD0D289&instructorinstitution=NUS`<br>
   to **`http://localhost:8888`**`/page/instructorCourseJoin?key=F2AD69F8994BA92C8D605BAEDB35949A41E71A573721C8D60521776714DE0BF8B0860F12DD19C6B955F735D8FBD0D289&instructorinstitution=NUS`

### As student

You need a student account which can be created by instructors.

1. Log in as an instructor. Add a course for yourself and then add the students for the course.
1. The system will send an email containing the join link to each added student. Again, this will not happen on the dev server, so additional steps are required.
1. Log out and log in to `http://localhost:8888/admin/adminSearchPage` as administrator.
1. Search for the student you added in as instructor. From the search results, click anywhere on the desired row (except on the student name) to get the course join link for that student.
1. Log out and use that join link (again, change the base URL to `http://localhost:8888` if necessary) to log in as a student.

**Alternative**: Run the test cases, they create several student and instructor accounts in the datastore. Use one of them to log in.

## Testing

TEAMMATES automated testing requires Firefox or Chrome (works on Windows and OS X).
It is recommended to use Firefox 46.0 as this is the browser used in CI build (Travis/AppVeyor).

Before running the test suite, both the server and the test environment should be using the UTC time zone. If this has not been done yet, here is the procedure:
* Stop the dev server if it is running.
* Specify timezone as a VM argument:
  * Eclipse
    * Go to the run configuration Eclipse created when you started the dev server (`Run → Run configurations ...` and select the appropriate one).
    * Click on the `Arguments` tab and add `-Duser.timezone=UTC` to the `VM arguments` text box.
    * Save the configuration for future use: Go to the `Common` tab (the last one) and make sure you have selected `Save as → Local file` and `Display in favorites menu → Run, Debug`.
  * IntelliJ
    * Go to `Run → Edit Configurations...` and select `Dev Server`.
    * Add `-Duser.timezone=UTC` to the `VM options` text box. Click `OK`.
* Start the server again using the run configuration you created in the previous step.

### Using Firefox

* Only Firefox between versions 38.0.5 and 46.0.1 are supported.
  * To downgrade your Firefox version, obtain the executable from [here](https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/).
  * If you want to use a different path for this version, choose `Custom setup` during install.
  * Remember to disable the auto-updates (`Options → Advanced tab → Update`).

* If you want to use a Firefox version other than your computer's default, specify the custom path in `test.firefox.path` value in `test.properties`.

* If you are planning to test changes to JavaScript code, disable JavaScript caching for Firefox:
  * Enter `about:config` into the Firefox address bar and set `network.http.use-cache = false`.

### Using Chrome

* You need to use chromedriver for testing with Chrome.
  * Download the latest stable chromedriver from [here](https://sites.google.com/a/chromium.org/chromedriver/downloads).
    The site will also inform the versions of Chrome that can be used with the driver.
  * Specify the path to the chromedriver executable in `test.chromedriver.path` value in `test.properties`.

* If you are planning to test changes to JavaScript code, disable JavaScript caching for Chrome:
  * Press Ctrl+Shift+J to bring up the Web Console.
  * Click on the settings button at the bottom right corner.
  * Under the General tab, check "Disable Cache".

* The chromedriver process started by the test suite will not automatically get killed after the tests have finished executing.<br>
  You will need to manually kill these processes after the tests are done.
  * On Windows, use the Task Manager or `taskkill /f /im chromedriver.exe` command.
  * On OS X, use the Activity Monitor or `sudo killall chromedriver` command.

### Running the test suites

#### Test Configurations

Several configurations are provided by default:
* `CI tests` - Runs `src/test/testng-ci.xml`, all the tests that are run by CI (Travis/AppVeyor).
* `Local tests` - Runs `src/test/testng-local.xml`, all the tests that need to be run locally by developers. `Dev green` means passing all the tests in this configuration.
* `Failed tests` - Runs `test-output/testng-failed.xml`, which is generated if a test run results in some failures. This will run only the failed tests.

### Running the test suite with command line

Before running any test suite, it is important to have the dev server running locally first if you are testing against it.

Test suite | Command | Results can be viewed in
---|---|---
`CI tests` | `./gradlew ciTests` | `{project folder}/build/reports/test-try-{n}/index.html`, where `{n}` is the sequence number of the test run
`Local tests` | `./gradlew localTests` | `{project folder}/build/reports/test-local/index.html`
`Failed tests` | `./gradlew failedTests` | `{project folder}/build/reports/test-failed/index.html`
Any individual test | `./gradlew test -Dtest.single=TestClassName` | `{project folder}/build/reports/tests/index.html`

`CI tests` will be run once and the failed tests will be re-run a few times.
All other test suites will be run once and only once.

To run any test suite or individual test with [GodMode turned on](godmode.md), append `-Pgodmode=true` to the command, e.g.:
```sh
./gradlew ciTests -Pgodmode=true
./gradlew test -Dtest.single=InstructorFeedbackResultsPageUiTest -Pgodmode=true
```

### Running the test suite with an IDE

* An additional configuration `All tests` is provided, which will run `CI tests` and `Local tests`.
* Additionally, configurations that run the tests with `GodMode` turned on are also provided.
* When running the test cases, if a few cases fail (this can happen due to timing issues), re-run the failed cases using the `Run Failed Test` icon in the TestNG tab until they pass.

#### Eclipse

Run tests using the configurations available under the green `Run` button on the Eclipse toolbar.

Sometimes, Eclipse does not show these options immediately after you set up the project. "Refreshing" the project should fix that.

To run individual tests, right-click on the test files on the project explorer and choose `Run As → TestNG Test`.

#### IntelliJ

Run tests using the configurations available under `Run → Run...`.

To run individual tests, right-click on the test files on the project explorer and choose `Run`.

## Deploying to a staging server

> `Staging server` is the server instance you set up on Google App Engine for hosting the app for testing purposes.

This instruction set assumes that the app identifier is `teammates-john`.

1. Create your own app on GAE.<br>
   Suggested app identifier: `teammates-yourname` (e.g `teammates-john`).<br>
   The URL of the app will be like this: `https://teammates-john.appspot.com`.

1. Modify configuration files.
   * `src/main/resources/build.properties`<br>
     Edit the file as instructed in its comments.
   * `src/main/webapp/WEB-INF/appengine-web.xml`<br>
     Modify to match app name and app id of your own app, and the version number if you need to. Do not modify anything else.

1. Deploy the application to your staging server.
   * With command line
     * Run the following command:

       ```sh
       ./gradlew appengineUpdate
       ```
     * Follow the steps and wait until the command ends with a `BUILD SUCCESSFUL`.
   * With Eclipse
     * Choose `Deploy to App Engine...` from Eclipse (under the `Google` menu item) and follow the steps.
     * Wait until you see this message (or similar) in Eclipse console: `Deployment completed successfully`.
   * With IntelliJ
     * Refer to [this guide](https://www.jetbrains.com/help/idea/2016.3/getting-started-with-google-app-engine.html#deploy_googleapp_via_runConfig) to deploy your application.

1. (Optional) Set the version you deployed as the "default":
   * Go to App Engine dashboard: `https://console.cloud.google.com/appengine?project=teammates-john`.
   * Click `Versions` under `Main` menu on the left bar.
   * Tick the checkbox next to the deployed version and select `Migrate Traffic`. Wait for a few minutes.
   * If you do not wish to set the deployed version as the default, you can access the deployed app using
     `https://{version}-dot-teammates-john.appspot.com`, e.g `https://4-18-dot-teammates-john.appspot.com`.

1. (Optional) You can run the tests against the deployed app.
   * Edit `src/test/resources/test.properties` as instructed is in its comments.
   * Run the full test suite or any subset of it as how you would have done it in dev server.
     However, the GAE daily quota is usually not enough to run the full test suite, in particular for accounts with no billing enabled.

## Running client scripts

> Client scripts are scripts that remotely manipulate data on GAE via its Remote API. They are run as standard Java applications.

Most of developers may not need to write and/or run client scripts but if you are to do so *in a production environment*, additional steps are required:

1. Download and install Google Cloud SDK from [here](https://cloud.google.com/sdk/downloads).
1. Run `gcloud auth login` and choose your Google account for authentication.

You are now ready to run your scripts.

## Config points

There are several files used to configure various aspects of the system.

**Main**: These vary from developer to developer and are subjected to frequent changes.
* `build.properties`: Contains the general purpose configuration values to used by the web app.
* `test.properties`: Contains the configuration values for the test driver.
* `appengine-web.xml`: Contains the configuration for deploying the application on GAE.

**Tasks**: These do not concern the application directly, but rather the development process.
* `build.gradle`: Contains the server-side third-party dependencies specification, as well as configurations for automated tasks/routines to be run via Gradle.
* `gradle.properties`, `gradle-wrapper.properties`: Contains the Gradle and Gradle wrapper configuration.
* `package.json`: Contains the client-side third-party dependencies specification.
* `.travis.yml`: Contains the Travis CI job configuration.
* `appveyor.yml`: Contains the AppVeyor CI job configuration.

**Other**: These are rarely, if ever will be, subjected to changes.
* `logging.properties`: Contains the java.util.logging configuration.
* `log4j.properties`: Contains the log4j configuration. Not used by us.
* `web.xml`: Contains the web server configuration, e.g servlets to run, mapping from URLs to servlets/JSPs, security constraints, etc.
* `cron.xml`: Contains the cron jobs specification.
* `queue.xml`: Contains the task queues configuration.
* `jdoconfig.xml`: Contains the JDO configuration.
* `persistence.xml`: Contains the JPA configuration.
