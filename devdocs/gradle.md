# Gradle Tasks Reference

The following tasks can be run using the command `./gradlew` on OS X and Linux and the command `gradlew.bat` on Windows.

To run the task `taskToRun`, the command will be `./gradlew taskToRun` (on Windows, `gradlew.bat taskToRun`)

## Tasks

### Default tasks

Running the gradle command without any task name runs the default tasks. The default tasks for Teammates are `appengineRun`, `travisTests` and `appengineStop`

### General tasks
- `appengineRun` : Start the local dev server at `localhost:8888`

- `appengineStop` : Stop the local dev server

- `appengineExplodeApp` : Build the application and generate the exploded war to be used by the local server

- `clean` : Clean up all build related files and folders

- `appengineUpdate` : Upload application to Google App Engine using settings from `src/main/webapp/WEB-INF/appengine-web.xml`


### Testing related tasks
- `travisTests` : Run all the tests defined in `src/test/testng-travis.xml` with retries. This task is used to run the tests on Travis CI.

- `localTests` : Run all the tests defined in `src/test/testng-local.xml` without any retries. This includes tests that are unstable on Travis and some smoke tests.

- `stagingTests` : Run tests under the `component-tests`, `sequential-ui-tests` and `parallel-ui-tests` categories defined in `src/test/testng-travis.xml`.

- `manualTests` : Run an ad-hoc set of classes. The classes to be run are passed in as arguments. An example is as follows:
  ```
  ./gradlew -Ptest_classes=teammates.test.cases.ui.browsertests.AppPageUiTest,teammates.test.cases.common.SanitizerTest manualTests
  ```

- `failedTests` : Run failed tests from a previous run. It relies on the file `test-output/testng-failed.xml` that is generated when a local run has failed tests. 

- `failedTestsFromTravis` : Runs failed tests from a build on Travis CI. This task requires the parameter `gist`. This value can be found in the build log on Travis CI. An example of this is:

  ```
  Run failed tests locally: ./gradlew -Pgist=f749834665732f040bfa failedTestsFromTravis
  ```

  **Note** : God mode can be enabled for the tasks `manualTests`, `failedTests` and `failedTestsFromTravis` by using the `god_mode` argument. An example run is as follows:

  ```
  ./gradlew -Pgod_mode failedTests
  ```
