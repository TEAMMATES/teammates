<frontmatter>
  title: "End-to-End Testing"
</frontmatter>

# E2E Testing  

## What is E2E Testing?
  
<tooltip content="End-to-end">E2E</tooltip> testing is a testing methodology where the objective is to test the application as a whole.   
- It aims to ensure all integrated components of the application work together as expected when it is being used by the end user.   
- This is done by simulating user scenarios on the fully built product.  
  
E2E tests in TEAMMATES are located in the package `teammates.e2e` and configured in `src/e2e/resources/testng-e2e.xml`.

---

## Prerequisites for Local Testing

Before running E2E tests against your local development environment, ensure you have completed the following:

1. **Start both development servers**
   - **Backend server**: Run `./gradlew serverRun` (runs on `http://localhost:8080`)
   - **Frontend dev server**: Run `npm run start` (runs on `http://localhost:4200`)
   - Both servers must be running before you execute any E2E tests

2. **Configure test.properties**
   - Edit `src/e2e/resources/test.properties` to specify:
     - Which browser to use (`test.selenium.browser`)
     - Correct URLs for your local servers
     - Path to your browser driver executable
     - Test account credentials

3. **Download the appropriate browser driver**
   - See the browser configuration section below for detailed instructions

> **Important**:  
> If you run the frontend and backend separately (which is the standard setup for development), update your URLs in `test.properties`:  
> ```properties
> test.app.frontendurl=http://localhost:4200
> test.app.backendurl=http://localhost:8080
> ```

---

## Running E2E tests

### Configuring browsers for E2E Testing

TEAMMATES E2E testing requires **Firefox**, **Chrome**, or **Edge (Chromium-based)**.

Before running tests, modify `src/e2e/resources/test.properties` as needed — for example, to configure which browser and test accounts to use.

<panel header="#### Using Firefox" no-close>

* You need to use **geckodriver** for testing with Firefox.
  * Download the latest stable geckodriver from [GitHub Releases](https://github.com/mozilla/geckodriver/releases). The site will inform which versions of Firefox can be used with the driver.
  * Specify the path to the geckodriver executable in `test.geckodriver.path` in `test.properties`.

* If you want to use a Firefox version other than your computer’s default, specify its path in `test.firefox.path`value in `test.properties`.

* **Handling dangling processes:**  
  If a test leaves Firefox open (e.g., due to failure), kill any leftover processes manually:
  * Windows: `taskkill /f /im geckodriver.exe`
  * macOS: `sudo killall geckodriver`

</panel>

<panel header="#### Using Chrome" no-close>

* You need to use **chromedriver** for testing with Chrome.
  * Check your Chrome version (`chrome://settings/help`).
  * **For Chrome 115 and later (most users)**: Download from the [Chrome for Testing Dashboard](https://googlechromelabs.github.io/chrome-for-testing/).  
    Select your Chrome version and OS, then download **`chromedriver`** (not `chrome` or `chrome-headless-shell`).
  * **For Chrome 114 and earlier:** use the [old ChromeDriver downloads page](https://chromedriver.storage.googleapis.com/index.html).
  * Specify the path to the **chromedriver executable** (not just its folder) in `test.chromedriver.path`.

* **Mac users:**  
  If chromedriver is blocked as “unverified software,” go to **System Preferences → Security & Privacy → General** and click **“Allow Anyway”**,  
  or run `xattr -d com.apple.quarantine /path/to/chromedriver`.

* **Handling dangling processes:**  
  If a test leaves Chrome open, kill leftover processes manually:
  * Windows: `taskkill /f /im chromedriver.exe`
  * macOS: `sudo killall chromedriver`

</panel>

<panel header="#### Using Edge" no-close>

Only modern **Edge (Chromium-based)** is supported.

* You need to use **edgedriver** for testing with Edge.
  * Download the version matching your Edge installation from the [Microsoft WebDriver site](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/).
  * Specify the path to the edgedriver executable in `test.edgedriver.path`.

* **Handling dangling processes:**  
  * Windows: `taskkill /f /im msedgedriver.exe`
  * macOS: `sudo killall msedgedriver`

</panel>

---

### Running the tests

E2E tests follow this configuration:

| Test suite | Command | Results can be viewed in |
|-------------|----------|---------------------------|
| E2E tests | `./gradlew e2eTests` | `{project folder}/build/reports/e2e-test-try-{n}/index.html`, where `{n}` is the test run number |
| Any individual E2E test | `./gradlew e2eTestTry1 --tests TestClassName` | `{project folder}/build/reports/e2e-test-try-1/index.html` |

- `E2ETryTest1` to `E2ETryTest5` correspond to the initial and subsequent retry attempts for failed tests. The SQL version of E2E tests can be run with: `./gradlew e2eTestsSql`.

- Before running `E2E tests`, make sure both **frontend and backend servers** are running locally.
- Some test cases may fail intermittently due to timing issues — rerun them until they pass.

---

### Testing against production server

If you are testing against a production server (staging server or live server), some additional tasks need to be done.

1. Edit `src/e2e/resources/test.properties` as instructed in its comments.  
   * In particular, you will need a legitimate Gmail account to be used for testing.
2. If you are testing email sending, you need to setup a `Gmail API` as follows:
   * [Obtain Gmail API credentials](https://github.com/TEAMMATES/teammates-ops/blob/master/platform-guide.md#setting-up-gmail-api-credentials) and download them.
   * Copy the file to  `src/e2e/resources/gmail-api` (create the  `gmail-api`  folder) of your project and rename it to  `client_secret.json`.
   * It is also possible to use the Gmail API credentials from any other Google Cloud Platform project for this purpose.
   * Run  `EmailAccountTest`  to confirm that the setup works. For the first run, it is expected that you will need to grant access from the test Gmail account to the above API.
3. Run the full test suite or any subset of it as how you would have done it in dev server.  
   * Do note that the GAE daily quota is usually not enough to run the full test suite, in particular for accounts with no billing enabled.

---

## Creating E2E tests
  
As E2E tests should be written from the end user perspective, each test case should reflect some user workflow.

In TEAMMATES, E2E test cases are organized by page. For each page, we:

1. Identify the important user workflows. 
2. Simulate the user actions involved in the workflow by interacting with the UI elements.
3. Assert the expected conditions are present after the interaction.
  
[Selenium](https://www.selenium.dev/) is used to locate and interact with elements in the UI.
  
All E2E test classes inherit from `BaseE2ETestCase` which contains methods that are common to most test cases, such as preparing the `Browser` object used for testing.

To help verify the state of the database, `BackDoor` contains methods to create API calls to the back-end without going through the UI.

---

### Page Object Pattern
  
To make E2E tests resilient to UI changes, TEAMMATES adopts the [Page Object Pattern](https://martinfowler.com/bliki/PageObject.html).  

Each page in TEAMMATES is represented by a page object class. The page object class abstracts interactions with UI elements and only exposes the functionality of each page as methods.

- This way only the page object classes require updating when there are UI changes.
- Without Page Object Pattern, all test cases that use the changed UI element would require updating.

To maximise the effectiveness of Page Object Pattern, interaction with UI elements should not occur outside the page objects.

### Creating Page Objects

The page object should have methods to represent the main functionality of the page that testers can use to simulate user actions.

- The public methods for page objects should avoid exposing the UI elements it interacts with and instead focus on the functionality of the webpage.
- For example, instead of having methods like  `fillSearchBox`  and  `clickSearchButton`, it is better to have a method  `searchForInstructor`  which hides the UI elements used.

All Page Object classes inherit from `AppPage` which contains methods that are common for interacting with the web elements such as filling in textboxes.

---

### Things to avoid when writing E2E tests
  
1. **Testing based on implementation** — The focus should be on user actions instead of implementation details. Therefore, black box testing should be adopted and test cases should be designed around use cases.
2. **Excessive exception testing** — Testing edge cases with E2E tests should be avoided. This is because E2E tests are expensive to run and not that effective for isolating bugs. Hence we should focus on the happy path and exception paths that are more common. We should leave more exhaustive testing to lower-level unit or integration tests.
3. **Not following "Tell Don't Ask" Principle** — Instead of "asking" for data from the page objects and performing operations on them, "tell" the page object to do the operations. This is mostly seen in the verification methods where assertions are done in the page object instead of in the test case. This improves readability and maintainability as data and behavior are placed together.

---

### FAQ  
  
**Why are all the tests done in one `testAll()` method?**  
We bundle together everything as one test case instead of having multiple test cases. The advantage is that the time for the whole test class will be reduced because we minimize repetitive per-method setup/teardown. The downside is that it increases the time spent on re-running failed tests as the whole class has to be re-run. We opt for this approach because we expect tests to pass more frequently than to fail.

**Why is there one JSON file per test case?**  
Each test case has its own JSON file and the data inside has a unique prefix to prevent clashes in the database that may cause test failure, since tests are run concurrently.

---

### Troubleshooting

If you encounter issues, refer to the [E2E Troubleshooting Guide](troubleshooting-guide.html), which covers:
- Connection errors  
- WebDriver binding or path issues  
- Browser/driver compatibility mismatches  
- Missing binaries  
- And more  

If issues persist, [open a discussion thread](https://github.com/TEAMMATES/teammates/discussions/new?category=help-requests) and include:
- OS version  
- Browser + driver versions  
- Full error messages  
- Relevant `test.properties` snippets (without credentials)