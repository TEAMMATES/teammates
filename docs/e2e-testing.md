<frontmatter>
  title: "End-to-End Testing"
</frontmatter>

# E2E Testing  

## What is E2E Testing?
  
<tooltip content="End-to-end">E2E</tooltip> testing is a testing methodology where the objective is to test the application as a whole.   
- It aims to ensure all integrated components of the application work together as expected when it is being used by the end user.   
- This is done by simulating user scenarios on the fully built product.  
  
E2E tests in TEAMMATES can be found in the package `teammates.e2e`.

## Running E2E tests

### Configuring browsers for E2E Testing

TEAMMATES E2E testing requires Firefox, Chrome, or Edge (Chromium-based).

Before running tests, modify `src/e2e/resources/test.properties` if necessary, e.g. to configure which browser and test accounts to use.

<panel header="#### Using Firefox" no-close>

* You need to use geckodriver for testing with Firefox.
  * Download the latest stable geckodriver from [here](https://github.com/mozilla/geckodriver/releases).
    The site will also inform the versions of Firefox that can be used with the driver.
  * Specify the path to the geckodriver executable in `test.geckodriver.path` value in `test.properties`.

* If you want to use a Firefox version other than your computer's default, specify the custom path in `test.firefox.path` value in `test.properties`.

* If the test suite or any test leaves the browser open (e.g. due to failure), you will have a dangling geckodriver process.<br>
  You may want to manually kill these processes after the tests are done.
  * On Windows, use the Task Manager or `taskkill /f /im geckodriver.exe` command.
  * On OS X, use the Activity Monitor or `sudo killall geckodriver` command.

</panel>

<panel header="#### Using Chrome" no-close>

* You need to use chromedriver for testing with Chrome.
  * Download the latest stable chromedriver from [here](https://sites.google.com/a/chromium.org/chromedriver/downloads).
    The site will also inform the versions of Chrome that can be used with the driver.
  * Specify the path to the chromedriver executable in `test.chromedriver.path` value in `test.properties`.

* If the test suite or any test leaves the browser open (e.g. due to failure), you will have a dangling chromedriver process.<br>
  You may want to manually kill these processes after the tests are done.
  * On Windows, use the Task Manager or `taskkill /f /im chromedriver.exe` command.
  * On OS X, use the Activity Monitor or `sudo killall chromedriver` command.

</panel>

<panel header="#### Using Edge" no-close>

Only modern Edge (Chromium-based) is supported.

* You need to use edgedriver for testing with Edge.
  * Download the latest stable edgedriver from [here](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/).
    The site will also inform the versions of Edge that can be used with the driver.
  * Specify the path to the edgedriver executable in `test.edgedriver.path` value in `test.properties`.

* If the test suite or any test leaves the browser open (e.g. due to failure), you will have a dangling edgedriver process.<br>
  You may want to manually kill these processes after the tests are done.
  * On Windows, use the Task Manager or `taskkill /f /im msedgedriver.exe` command.
  * On OS X, use the Activity Monitor or `sudo killall msedgedriver` command.

</panel>

<br>

### Running the tests
E2E tests follow this configuration:

Test suite | Command | Results can be viewed in
---|---|---
`E2E tests` | `./gradlew e2eTests` | `{project folder}/build/reports/e2e-test-try-{n}/index.html`, where `{n}` is the sequence number of the test run
Any individual E2E test | `./gradlew e2eTestTry1 --tests TestClassName` | `{project folder}/build/reports/e2e-test-try-1/index.html`

- `E2E tests` will be run in their entirety once and the failed tests will be re-run a few times. 
- Before running `E2E tests`, it is important to have the dev server running locally first if you are testing against it.
- When running the test cases, a few cases may fail (this can happen due to timing issues). They can be re-run until they pass without affecting the accuracy of the tests.

### Testing against production server

If you are testing against a production server (staging server or live server), some additional tasks need to be done.

1. Edit `src/e2e/resources/test.properties` as instructed is in its comments.
   * In particular, you will need a legitimate Gmail account to be used for testing.

1. If you are testing email sending, you need to setup a `Gmail API` as follows:
   * [Obtain a Gmail API credentials](https://github.com/TEAMMATES/teammates-ops/blob/master/platform-guide.md#setting-up-gmail-api-credentials) and download it.
   * Copy the file to `src/e2e/resources/gmail-api` (create the `gmail-api` folder) of your project and rename it to `client_secret.json`.
   * It is also possible to use the Gmail API credentials from any other Google Cloud Platform project for this purpose.
   * Run `EmailAccountTest` to confirm that the setup works. For the first run, it is expected that you will need to grant access from the test Gmail account to the above API.

1. Run the full test suite or any subset of it as how you would have done it in dev server. 
   * Do note that the GAE daily quota is usually not enough to run the full test suite, in particular for accounts with no billing enabled.
   
## Creating E2E tests
  
As E2E tests should be written from the end user perspective, each test case should reflect some user workflow.
  
In TEAMMATES, E2E test cases are organized by page. For each page, we:

1. Identify the important user workflows  
1. Simulate the user actions involved in the workflow by interacting with the UI elements.  
1. Assert the expected conditions are present after the interaction.  
  
[Selenium](https://www.selenium.dev/) is used to locate and interact with elements in the UI.
  
All E2E test classes inherit from `BaseE2ETestCase` which contains methods that are common to most test cases, such as preparing the `Browser` object used for testing.

To help verify the state of the database, `BackDoor` contains methods to create API calls to the back-end without going through the UI.
  
### Page Object Pattern
  
In order to make E2E testing more robust to UI changes, the [Page Object Pattern](https://martinfowler.com/bliki/PageObject.html) is adopted.  
  
Each page in TEAMMATES is represented by a page object class. The page object class abstracts interactions with UI elements and only exposes the functionality of each page as methods.

- This way only the page object classes require updating when there are UI changes
- Without Page Object Pattern, all test cases that use the changed UI element would require updating  
  
<box type="tip">

To maximise the effectiveness of Page Object Pattern, interaction with UI elements should not occur outside the page objects.
</box>

### Creating Page Objects
  
The page object should have methods to represent the main functionality of the page that testers can use to simulate user actions.

- The public methods for page objects should avoid exposing the UI elements it interacts with and instead focus on the functionality of the webpage.
- For example, instead of having methods like `fillSearchBox` and `clickSearchButton`, it is better to have a method `searchForInstructor` which hides the UI elements used.

All Page Object classes inherit from `AppPage` which contains methods that are common for interacting with the web elements such as filling in textboxes.

### Things to avoid when writing E2E tests
  
1. **Testing based on implementation** - The focus should be on user actions instead of implementation details. Therefore, black box testing should be adopted and test cases should be designed around use cases.   
1. **Excessive exception testing** - Testing edge cases with E2E tests should be avoided. This is because E2E tests are expensive to run and not that effective for isolating bugs. Hence we should focus on the happy path and exception paths that are more common. We should leave more exhaustive testing to lower-level unit or integration tests.   
1. **Not following "Tell Don't Ask" Principle** - Instead of "asking" for data from the page objects and performing operations on them, "tell" the page object to do the operations. This is mostly seen in the verification methods where assertions are done in the page object instead of in the test case. This improves readability and maintainability as data and behavior are placed together.  
  
### FAQ  
  
**Why are all the tests done in one `testAll()` method?**  
We bundle together everything as one test case instead of having multiple test cases. The advantage is that the time for the whole test class will be reduced because we minimize repetitive per-method setup/teardown. The downside is that it increases the time spent on re-running failed tests as the whole class has to be re-run. We opt for this approach because we expect tests to pass more frequently than to fail.  
  
**Why is there one JSON file for each test case?**  
Each test case has its own JSON file and the data inside has a unique prefix to prevent clashes in the database that may cause test failure, since tests are run concurrently.
