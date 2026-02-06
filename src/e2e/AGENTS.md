# TEAMMATES E2E Tests - AI Agent Guide

## Overview

End-to-end tests that test the application as a whole using Selenium WebDriver.

## Structure

```
e2e/
├── cases/           # Test cases (test scenarios)
├── pageobjects/     # Page Object Model classes
└── util/            # Test utilities and helpers
```

## Testing Framework

- **Selenium WebDriver** - Browser automation
- **TestNG** - Test framework
- Configuration: `src/e2e/resources/testng-e2e.xml`

## Prerequisites

1. Backend server running (`./gradlew serverRun` on `http://localhost:8080`)
2. Frontend dev server running (`npm run start` on `http://localhost:4200`)
3. Browser driver installed (ChromeDriver, GeckoDriver, or EdgeDriver)
4. Configure `src/e2e/resources/test.properties` with correct URLs and driver paths

## Running Tests

```bash
# Run all E2E tests
./gradlew e2eTests

# Run specific test class
./gradlew e2eTestTry1 --tests TestClassName
```

Results can be viewed in `{project folder}/build/reports/e2e-test-try-{n}/index.html`

## Page Object Model

### Page Object Pattern

TEAMMATES uses the Page Object Pattern to make tests resilient to UI changes.

```java
public class InstructorHomePage extends AppPage {
    public InstructorHomePage(Browser browser) {
        super(browser);
    }
    
    @Override
    protected boolean containsExpectedPageContents() {
        return getPageSource().contains("Instructor Home");
    }
    
    public InstructorCoursesPage clickCoursesTab() {
        click(browser.driver.findElement(By.id("courses-tab")));
        waitForPageToLoad();
        return changePageType(InstructorCoursesPage.class);
    }
}
```

### Creating Page Objects

- Public methods should focus on functionality, not UI elements
- Example: `searchForInstructor()` instead of `fillSearchBox()` + `clickSearchButton()`
- All Page Object classes inherit from `AppPage`

## Writing E2E Tests

### Test Structure

```java
@Test
public void testScenario_description() {
    // Navigate to page
    AppPage loginPage = loginToInstructorPage(browser, testData.instructors.get("instructor1OfCourse1"));
    
    // Perform actions
    InstructorHomePage homePage = loginPage.goToInstructorHomePage();
    InstructorCoursesPage coursesPage = homePage.clickCoursesTab();
    
    // Verify results
    assertTrue(coursesPage.isDisplayed());
}
```

### Test Organization

- Tests are organized by page
- Each test class typically has one `testAll()` method that bundles all test scenarios
- Each test case has its own JSON file with unique prefix to prevent database clashes

## Best Practices

1. **Use Page Objects** - Don't access DOM directly in tests
2. **Wait for elements** - Use explicit waits, not hard-coded sleeps
3. **Test user workflows** - Test complete user journeys
4. **Keep tests independent** - Each test should be self-contained
5. **Use test data** - Create test data as needed, clean up after
6. **Focus on happy paths** - E2E tests are expensive, leave edge cases to unit tests
7. **Follow "Tell Don't Ask"** - Tell page objects to do operations, don't ask for data

## Common Utilities

- `Browser` - Browser instance wrapper
- `BackDoor` - Direct API access for setup/teardown
- `AppPage` - Base page class
- `BaseE2ETestCase` - Base class for all E2E tests
- `TestData` - Test data management

## Browser Configuration

### Chrome
- Download ChromeDriver from [Chrome for Testing Dashboard](https://googlechromelabs.github.io/chrome-for-testing/)
- Specify path in `test.chromedriver.path`

### Firefox
- Download geckodriver from [GitHub Releases](https://github.com/mozilla/geckodriver/releases)
- Specify path in `test.geckodriver.path`

### Edge
- Download edgedriver from [Microsoft WebDriver site](https://developer.microsoft.com/en-us/microsoft-edge/tools/webdriver/)
- Specify path in `test.edgedriver.path`

## Things to Avoid

- Don't use `Thread.sleep()` - Use explicit waits
- Don't access DOM directly - Use Page Objects
- Don't test implementation details - Test user behavior
- Don't make tests dependent on each other
- Don't forget to clean up test data
- Don't test excessive edge cases - Focus on common scenarios
- Don't verify data in page objects - Do assertions in test cases
