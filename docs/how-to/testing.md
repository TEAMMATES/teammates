<frontmatter>
  title: "Testing"
</frontmatter>

# Testing

## Component Tests

Component tests are white-box tests that test the application at two levels:

- **Unit tests**: Test individual components in isolation using mocks to simulate dependencies.
- **Integration tests**: Test components against an actual database instance using Testcontainers.

| Type                      | Location                 | Configuration                             |
| ------------------------- | ------------------------ | ----------------------------------------- |
| Frontend unit tests       | `*.spec.ts` files        | `src/web/jest.config.js`                  |
| Backend unit tests        | `teammates.test` package | `src/test/resources/testng-component.xml` |
| Backend integration tests | `teammates.it` package   | `src/it/resources/testng-it.xml`          |

### Running Component Tests

**Frontend:**

```sh
npm run test
```

This runs tests in watch mode — any changes to source code will automatically reload the tests.

To run tests once and generate coverage data:

```sh
npm run coverage
```

To run an individual test, change `it` to `fit` in the relevant `*.spec.ts` file.

<tabs>
<tab header="Mac / Linux">

**Backend:**

| Test suite          | Command                                          | Results                                         |
| ------------------- | ------------------------------------------------ | ----------------------------------------------- |
| All component tests | `./gradlew componentTests --continue`            | `build/reports/tests/componentTests/index.html` |
| Individual test     | `./gradlew componentTests --tests TestClassName` | `build/reports/tests/componentTests/index.html` |

To generate coverage data:

```sh
./gradlew componentTests jacocoReport
```

</tab>
<tab header="Windows">

**Backend:**

| Test suite          | Command                                            | Results                                         |
| ------------------- | -------------------------------------------------- | ----------------------------------------------- |
| All component tests | `gradlew.bat componentTests --continue`            | `build/reports/tests/componentTests/index.html` |
| Individual test     | `gradlew.bat componentTests --tests TestClassName` | `build/reports/tests/componentTests/index.html` |

To generate coverage data:

```sh
gradlew.bat componentTests jacocoReport
```

</tab>
</tabs>

The report can be found in `build/reports/jacoco/jacocoReport/`.

### Writing Component Tests

#### Naming

Frontend tests should follow the format: `"<function-name>: should ... when/if ..."`

```javascript
it('hasSection: should return false when there are no sections in the course');
```

Backend tests should follow the format: `test<functionName>_<scenario>_<outcome>`

```java
public void testGetComment_commentDoesNotExist_returnsNull()
public void testCreateComment_commentDoesNotExist_success()
public void testCreateComment_commentAlreadyExists_throwsEntityAlreadyExistsException()
```

#### Test Data

**Frontend:** Use the builder in `src/web/test-helpers/generic-builder.ts` to include only relevant details:

```javascript
const instructorModelBuilder =
  createBuilder <
  InstructorListInfoTableRowModel >
  {
    email: 'instructor@gmail.com',
    name: 'Instructor',
    hasSubmittedSession: false,
    isSelected: false,
  };

it('isAllInstructorsSelected: should return false if at least one instructor is not selected', () => {
  component.instructorListInfoTableRowModels = [
    instructorModelBuilder.isSelected(true).build(),
    instructorModelBuilder.isSelected(false).build(),
  ];
  expect(component.isAllInstructorsSelected).toBeFalsy();
});
```

**Backend:** Use the `getTypicalX` functions in `BaseTestCase`:

```java
Account account = getTypicalAccount();
account.setEmail("newemail@teammates.com");

Student student = getTypicalStudent();
student.setName("New Student Name");
```

### Snapshot Testing

Snapshot testing compares large expected outputs (e.g. rendered HTML, email content, CSV files) against stored snapshots. It runs in two modes:

- **Verification mode** (default): compares the actual output against the stored snapshot.
- **Auto-update mode**: overwrites the stored snapshot with the actual output. Use this when intentional changes are made, then manually verify that only the intended changes occurred.

**Frontend:** Auto-update mode is activated by pressing `u` in Jest watch mode.

**Backend:** Auto-update mode is activated by setting `test.snapshot.update=true` in `test.properties`.

A few things to keep in mind:

- Always run tests without auto-update mode after making changes to confirm nothing unexpected changed.
- Avoid creating or modifying snapshot files manually — always use auto-update mode.
- Snapshot testing complements unit tests but does not replace them.

---

## E2E Tests

E2E tests are black-box tests that simulate user workflows on the fully built application. Accessibility tests are a subset of E2E tests that check for WCAG compliance using [axe-core](https://github.com/dequelabs/axe-core-maven-html/blob/develop/selenium/README.md).

- E2E tests are located in the package `teammates.e2e`, configured in `src/e2e/resources/testng-e2e.xml`.
- Accessibility tests are located in the package `teammates.e2e.cases.axe`.

### Prerequisites

Before running E2E tests:

1. Start server:

<tabs>
<tab header="Mac / Linux">

```sh
npm run build
./gradlew serverRun
```

<box type="important">
If you run the frontend and backend separately update your URLs in test.properties:

```
test.app.frontend.url=http://localhost:4200
test.app.backend.url=http://localhost:8080
```

Then start both the frontend and backend servers.

```
npm run start
./gradlew serverRun
```

</box>
</tab>
<tab header="Windows">

```sh
npm run build
gradlew.bat serverRun
```

<box type="important">
If you run the frontend and backend separately update your URLs in test.properties:

```
test.app.frontend.url=http://localhost:4200
test.app.backend.url=http://localhost:8080
```

Then start both the frontend and backend servers.

```
npm run start
gradlew.bat serverRun
```

</box>
</tab>
</tabs>

2. Configure `src/e2e/resources/test.properties`:
   - Browser to use (`test.selenium.browser`)
   - Server URLs (`test.app.frontend.url`, `test.app.backend.url`)
   - Path to browser driver executable
   - Test account credentials

3. Download the appropriate browser driver (see below).

### Configuring Browsers

<panel header="#### Using Firefox" no-close>

- You need to use **geckodriver** for testing with Firefox.
  - Download the latest stable geckodriver from [GitHub Releases](https://github.com/mozilla/geckodriver/releases). The site will inform which versions of Firefox can be used with the driver.
  - Specify the path to the geckodriver executable in `test.geckodriver.path` in `test.properties`.

- If you want to use a Firefox version other than your computer’s default, specify its path in `test.firefox.path`value in `test.properties`.

- **Handling dangling processes:**  
  If a test leaves Firefox open (e.g., due to failure), kill any leftover processes manually:
  - Windows: `taskkill /f /im geckodriver.exe`
  - macOS: `sudo killall geckodriver`

</panel>

<panel header="#### Using Chrome" no-close>

- You need to use **chromedriver** for testing with Chrome.
  - Check your Chrome version (`chrome://settings/help`).
  - **For Chrome 115 and later (most users)**: Download from the [Chrome for Testing Dashboard](https://googlechromelabs.github.io/chrome-for-testing/).  
    Select your Chrome version and OS, then download **`chromedriver`** (not `chrome` or `chrome-headless-shell`).
  - **For Chrome 114 and earlier:** use the [old ChromeDriver downloads page](https://chromedriver.storage.googleapis.com/index.html).
  - Specify the path to the **chromedriver executable** (not just its folder) in `test.chromedriver.path`.

- **Mac users:**  
  If chromedriver is blocked as “unverified software,” go to **System Preferences → Security & Privacy → General** and click **“Allow Anyway”**,  
  or run `xattr -d com.apple.quarantine /path/to/chromedriver`.

- **Handling dangling processes:**  
  If a test leaves Chrome open, kill leftover processes manually:
  - Windows: `taskkill /f /im chromedriver.exe`
  - macOS: `sudo killall chromedriver`

</panel>

<panel header="#### Using Edge" no-close>

Only modern **Edge (Chromium-based)** is supported.

- You need to use **edgedriver** for testing with Edge.
  - Download the version matching your Edge installation from the [Microsoft WebDriver site](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/).
  - Specify the path to the edgedriver executable in `test.edgedriver.path`.

- **Handling dangling processes:**
  - Windows: `taskkill /f /im msedgedriver.exe`
  - macOS: `sudo killall msedgedriver`

</panel>

<br>

### Running E2E Tests

<tabs>
<tab header="Mac / Linux">

| Test suite                    | Command                                       | Results                                     |
| ----------------------------- | --------------------------------------------- | ------------------------------------------- |
| All E2E tests                 | `./gradlew e2eTests`                          | `build/reports/e2e-test-try-{n}/index.html` |
| Individual test               | `./gradlew e2eTestTry1 --tests TestClassName` | `build/reports/e2e-test-try-1/index.html`   |
| Accessibility tests           | `./gradlew axeTests`                          | `build/reports/axe-test/index.html`         |
| Individual accessibility test | `./gradlew axeTests --tests TestClassName`    | `build/reports/axe-test/index.html`         |

</tab>
<tab header="Windows">

| Test suite                    | Command                                         | Results                                     |
| ----------------------------- | ----------------------------------------------- | ------------------------------------------- |
| All E2E tests                 | `gradlew.bat e2eTests`                          | `build/reports/e2e-test-try-{n}/index.html` |
| Individual test               | `gradlew.bat e2eTestTry1 --tests TestClassName` | `build/reports/e2e-test-try-1/index.html`   |
| Accessibility tests           | `gradlew.bat axeTests`                          | `build/reports/axe-test/index.html`         |
| Individual accessibility test | `gradlew.bat axeTests --tests TestClassName`    | `build/reports/axe-test/index.html`         |

</tab>
</tabs>

Some tests may fail intermittently due to timing issues — rerun them until they pass.

### Testing Against a Production Server

1. Edit `src/e2e/resources/test.properties` as instructed in its comments. You will need a legitimate Gmail account for testing.
2. If testing email sending, set up the Gmail API:
   - [Obtain Gmail API credentials](https://github.com/TEAMMATES/teammates-ops/blob/master/platform-guide.md#setting-up-gmail-api-credentials) and download them.
   - Copy the file to `src/e2e/resources/gmail-api/client_secret.json`.
   - Run `EmailAccountTest` to confirm the setup works.
3. Run the full test suite or any subset as you would on the dev server.

### Writing E2E Tests

Each test case should reflect a user workflow. For each page:

1. Identify the important user workflows.
2. Simulate user actions by interacting with UI elements via [Selenium](https://www.selenium.dev/).
3. Assert the expected conditions after each interaction.

All E2E test classes inherit from `BaseE2ETestCase`. Use `BackDoor` to verify database state without going through the UI.

#### Page Object Pattern

TEAMMATES uses the [Page Object Pattern](https://martinfowler.com/bliki/PageObject.html) to make tests resilient to UI changes. Each page is represented by a page object class that exposes page functionality as methods, hiding the underlying UI elements.

- All page object classes inherit from `AppPage`.
- Interaction with UI elements should not occur outside page objects.
- Prefer methods like `searchForInstructor` over exposing lower-level methods like `fillSearchBox` and `clickSearchButton`.

#### Things to Avoid

- **Testing implementation details** — focus on user actions and workflows, not internal code.
- **Excessive edge case testing** — leave exhaustive testing to component tests. E2E tests should focus on the happy path and common exception paths.
- **Asking instead of telling** — follow the "Tell Don't Ask" principle. Perform assertions inside page objects rather than extracting data and asserting in the test case.
