# Development Guidelines

These are the common tasks involved when working on features, enhancements, bug fixes, etc. for TEAMMATES.

* [Managing the dev server](#managing-the-dev-server)
* [Logging in to a TEAMMATES instance](#logging-in-to-a-teammates-instance)
* [Testing](#testing)
* [Deploying to a staging server](#deploying-to-a-staging-server)
* [Running client scripts](#running-client-scripts)
* [Config points](#config-points)

The instructions in all parts of this document work for Linux, OS X, and Windows, with the following pointers:
- Replace `./gradlew` to `gradlew.bat` if you are using Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.
- It is assumed that the development environment has been correctly set up. If this step has not been completed, refer to [this document](setting-up.md).

> If you encounter any problems during the any of the processes, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request on our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

## Managing the dev server

> `Dev server` is the server run in your local machine.

### Building JavaScript files

Our JavaScript code is written in modular ECMAScript 6 (ES6) syntax, which is not supported in many of the existing Web browsers today.<br>
To resolve this, we need to *bundle and transpile* ("build" afterwards) them into standard ECMAScript 5 which is supported by (almost) all browsers.

Run the following command to build the JavaScript files for the application's use:
```sh
npm run build
```

In addition, the command will also *minify* the JavaScript files to reduce the size of scripts that need to be downloaded.

This command needs to be run before starting the dev server if there are updates to the JavaScript files.

### With command line

#### Starting the dev server

To start the server in the background, run the following command
and wait until the task exits with a `BUILD SUCCESSFUL`:
```sh
./gradlew appengineStart
```

To start the server in the foreground (e.g. if you want the console output to be visible),
run the following command instead:
```sh
./gradlew appengineRun
```

The dev server URL will be `http://localhost:8080` as specified in `build.gradle`.

#### Stopping the dev server

If you started the server in the background, run the following command to stop it:
```sh
./gradlew appengineStop
```

If the server is running in the foreground, press `Ctrl + C` to stop it or run the above command in a new console.

### With Eclipse

#### Starting the dev server

Right-click on the project folder and choose `Run As → App Engine`.<br>
After some time, you should see this message (or similar) on the Eclipse console: `Dev App Server is now running`.
The dev server URL will be given at the console output, e.g `http://localhost:8080`.

#### Stopping the dev server

Click the "Terminate" icon on the Eclipse console.

### With IntelliJ

#### Starting the dev server

Go to `Run → Run...` and select `Google App Engine Standard Local Server` in the pop-up box.

#### Stopping the dev server

Go to `Run → Stop 'Google App Engine Standard Local Server'`.

## Logging in to a TEAMMATES instance

This instruction set applies for both dev server and production server, with slight differences explained where applicable.
- The local dev server is assumed to be accessible at `http://localhost:8080`.
- If a URL is given as relative, prepend the server URL to access the page, e.g `/page/somePage` is accessible in dev server at `http://localhost:8080/page/somePage`.

### As administrator

1. Go to any administrator page, e.g `/admin/adminHomePage`.
1. On the dev server, log in using any username, but remember to check the `Log in as administrator` check box. You will have the required access.
1. On the production server, you will be granted the access only if your account has administrator permission to the application.
1. When logged in as administrator, ***masquerade mode*** can also be used to impersonate instructors and students by adding `user=username` to the URL
 e.g `http://localhost:8080/page/studentHomePage?user=johnKent`.

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
   to **`http://localhost:8080`**`/page/instructorCourseJoin?key=F2AD69F8994BA92C8D605BAEDB35949A41E71A573721C8D60521776714DE0BF8B0860F12DD19C6B955F735D8FBD0D289&instructorinstitution=NUS`

### As student

You need a student account which can be created by instructors.

1. Log in as an instructor. Add a course for yourself and then add the students for the course.
1. The system will send an email containing the join link to each added student. Again, this will not happen on the dev server, so additional steps are required.
1. Log out and log in to `http://localhost:8080/admin/adminSearchPage` as administrator.
1. Search for the student you added in as instructor. From the search results, click anywhere on the desired row (except on the student name) to get the course join link for that student.
1. Log out and use that join link (again, change the base URL to `http://localhost:8080` if necessary) to log in as a student.

**Alternative**: Run the test cases, they create several student and instructor accounts in the datastore. Use one of them to log in.

## Testing

TEAMMATES automated testing requires Firefox or Chrome (works on Windows and OS X).
It is recommended to use Firefox 46.0 as this is the browser used in CI build (Travis/AppVeyor).

**NOTE**
> The dev server sets its time zone to UTC at startup.

### Using Firefox

* Only Firefox between versions 38.0.5 and 46.0.1 are supported.
  * To downgrade your Firefox version, obtain the executable from [here](https://ftp.mozilla.org/pub/mozilla.org/firefox/releases/).
  * If you want to use a different path for this version, choose `Custom setup` during install.
  * Remember to disable the auto-updates (`Options → Advanced tab → Update`).

* If you want to use a Firefox version other than your computer's default, specify the custom path in `test.firefox.path` value in `test.properties`.

* If you are planning to test changes to JavaScript code, disable JavaScript caching for Firefox:
  * Enter `about:config` into the Firefox address bar and set `network.http.use-cache` (or `browser.cache.disk.enable` in newer versions of Firefox) to `false`.

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

#### God Mode

To ensure that the UI generated by the application is the same as what is expected, some tests work by comparing the raw HTML text of Web pages generated by the application against the corresponding content of a pre-recorded expected HTML text files. These files need to be updated every time a UI-related change is observed (e.g. when adding a new feature to the UI).
In order to save the time and effort for such update, we have a tool called ["God Mode"](godmode.md). This can be activated by setting the value of `test.godmode.enabled` in `test.properties` to `true`.

Assuming you have already done the UI-related change and tested it manually, the procedure to update the expected HTML files of the relevant tests is as follows:

- Run the test which does the HTML files comparison with God Mode activated. Under this mode, the test runner will capture the Web pages being generated by the application and use a post-processed version of it to overwrite the existing expected HTML files.
- Manually examine the diff of the updated expected HTML files to ensure that the changes are as expected.
- Run the test again without God Mode activated and ensure that it passes.

The same God Mode technique is also used to ensure the content of emails generated by the application are as expected.

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

1. [Authorize your Google account to be used by the Google Cloud SDK](https://cloud.google.com/sdk/docs/authorizing) if you have not done so.
   ```sh
   gcloud auth login
   ```
   Follow the steps until you see `You are now logged in as [...]` on the console.

1. Modify configuration files.
   * `src/main/resources/build.properties`<br>
     Edit the file as instructed in its comments.
   * `src/main/webapp/WEB-INF/appengine-web.xml`<br>
     Modify to match app name and app id of your own app, and the version number if you need to. Do not modify anything else.

1. Deploy the application to your staging server.
   * With command line
     * Run the following command:

       ```sh
       ./gradlew appengineDeployAll
       ```
     * Wait until you see all the following messages or similar on the console:
       * `Deployed service [default] to [https://6-0-0-dot-teammates-john.appspot.com]`
       * `Cron jobs have been updated.`
       * `Indexes are being rebuilt. This may take a moment.`
       * `Task queues have been updated.`
   * With Eclipse
     * Refer to [this guide](https://cloud.google.com/eclipse/docs/deploying) to deploy your application.
   * With IntelliJ
     * Refer to [this guide](https://cloud.google.com/tools/intellij/docs/deploy-std#deploying_to_the_standard_environment) to deploy your application.

1. (Optional) Set the version you deployed as the "default":
   * Go to App Engine dashboard: `https://console.cloud.google.com/appengine?project=teammates-john`.
   * Click `Versions` under `Main` menu on the left bar.
   * Tick the checkbox next to the deployed version and select `Migrate Traffic`. Wait for a few minutes.
   * If you do not wish to set the deployed version as the default, you can access the deployed app using
     `https://{version}-dot-teammates-john.appspot.com`, e.g `https://6-0-0-dot-teammates-john.appspot.com`.

1. (Optional) You can run the tests against the deployed app.
   * You need to setup `Gmail API` for the project as follows:
   
     **NOTE**
     > Setup of Gmail API is required when testing against the staging server.\
     See [Notes on Gmail API](#notes-on-gmail-api) for more details.
     
     * Go to [Google Cloud Console](https://console.cloud.google.com/), select your TEAMMATES project if it is not selected
       and click `API Manager`.\
       Click `ENABLE API`.\
       Click `Gmail API` under `G Suite APIs` and then click `ENABLE`.
     * Alternatively, you can use [Gmail API Wizard](https://console.cloud.google.com/start/api?id=gmail) to enable
       `Gmail API`.
     * Click `Credentials` in the menu of the `API Manager`.
     * Click `Create credentials` and then select `OAuth client ID`.
     * Choose `Other`, give it a name, e.g. `teammates` and click `Create`. You will then get shown your client ID details,
       click `OK`.
     * Click the `Download JSON` icon.
     * Copy the file to `src/test/resources/gmail-api` (create the `gmail-api` folder) of your project and rename it to
       `client_secret.json`.
   * Edit `src/test/resources/test.properties` as instructed is in its comments.
   * Run the full test suite or any subset of it as how you would have done it in dev server. You may want to run
     `InstructorCourseDetailsPageUiTest` standalone first because you would need to login to test accounts for the first
     time. Do note that the GAE daily quota is usually not enough to run the full test suite, in particular for accounts with
     no billing enabled.

#### Notes on Gmail API
1. We need to set up the Gmail API because our test suite uses the Gmail API to access Gmail accounts used for testing (these
accounts are specified in `test.properties`) to confirm that those accounts receive the expected emails from TEAMMATES.
1. This setup is needed only when testing against the staging server because no actual emails are sent by the dev server and
therefore delivery of emails is not tested when testing against the dev server.
1. While we only show how to use the Gmail API with the TEAMMATES project in Google Cloud Console, it is also possible to use
   the Gmail API with any other Google Cloud Platform project and use the credentials in our project.

## Running client scripts

> Client scripts are scripts that remotely manipulate data on GAE via its Remote API. They are run as standard Java applications.

Most of developers may not need to write and/or run client scripts but if you are to do so, take note of the following:

* In order to run any script *in a production environment*, you need to authorize your account for [Application Default Credentials](https://developers.google.com/identity/protocols/application-default-credentials).
  ```sh
  gcloud auth application-default login
  ```
  Follow the steps until you see `Credentials saved to file: [...].` printed on the console.

* It is not encouraged to compile and run any script via command line; use any of the supported IDEs to significantly ease this task.

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

**Static Analysis**: These are used to maintain code quality and measure code coverage. See [Static Analysis](static-analysis.md).
* `static-analysis/*`: Contains most of the configuration files for all the different static analysis tools.
* `.stylelintrc`: Equivalent to `static-analysis/teammates-stylelint.yml`, currently only used for Stylelint integration in IntelliJ.

**Other**: These are rarely, if ever will be, subjected to changes.
* `logging.properties`: Contains the java.util.logging configuration.
* `log4j.properties`: Contains the log4j configuration. Not used by us.
* `web.xml`: Contains the web server configuration, e.g servlets to run, mapping from URLs to servlets/JSPs, security constraints, etc.
* `cron.xml`: Contains the cron jobs specification.
* `queue.xml`: Contains the task queues configuration.
* `datastore-indexes.xml`: Contains the Datastore indexes configuration.
