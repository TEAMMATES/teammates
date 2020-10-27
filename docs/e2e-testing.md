# E2E Testing  
  
## What is E2E Testing?
  
E2E (End-to-end) testing is a testing methodology where the objective is to test the application as a whole.   
- It aims to ensure all integrated components of the application work together as expected when it is being used by the end user.   
- This is done by simulating user scenarios on the fully built product.  
  
E2E tests in TEAMMATES can be found in the package `teammates.e2e`. 

To set up and run E2E tests, refer to this [document](https://github.com/TEAMMATES/teammates/blob/master/docs/development.md#testing).  

## Creating E2E tests
  
As E2E tests should be written from the end user perspective, each test case should reflect some user workflow.   
  
In TEAMMATES, E2E test cases are organized by page. For each page, we:  
1. Identify the important user workflows  
1. Simulate the user actions involved in the workflow by interacting with the UI elements.  
1. Assert the expected conditions are present after the interaction.  
  
[Selenium](https://www.selenium.dev/) is used to locate and interact with elements in the UI.   
  
All E2E test classes inherit from `BaseE2ETestCase` which contains methods that are common to most test cases, such as preparing the `Browser` object used for testing.   
  
To help verify the state of the datastore, `BackDoor` contains methods to create API calls to the back-end without going through the UI. 
  
## Page Object Pattern
  
In order to make E2E testing more robust to UI changes, the [Page Object Pattern](https://martinfowler.com/bliki/PageObject.html) is adopted.  
  
Each page in TEAMMATES is represented by a page object class. The page object class abstracts interactions with UI elements and only exposes the functionality of each page as methods.   
- This way only the page object classes require updating when there are UI changes  
- Without Page Object Pattern, all test cases that use the changed UI element would require updating  
  
To maximise the effectiveness of Page Object Pattern, interaction with UI elements should not occur outside the page objects.   
  
  
## Creating Page Objects
  
The page object should have methods to represent the main functionality of the page that testers can use to simulate user actions.   
- The public methods for page objects should avoid exposing the UI elements it interacts with and instead focus on the functionality of the webpage.   
- For example, instead of having methods like `fillSearchBox` and `clickSearchButton`, it is better to have a method `searchForInstructor` which hides the UI elements used.    

All Page Object classes inherit from `AppPage` which contains methods that are common for interacting with the web elements such as filling in textboxes.   
  
  
## Things to avoid when writing E2E tests
  
1. **Testing based on implementation** - The focus should be on user actions instead of implementation details. Therefore, black box testing should be adopted and test cases should be designed around use cases.   
1. **Excessive exception testing** - Testing edge cases with E2E tests should be avoided. This is because E2E tests are expensive to run and not that effective for isolating bugs. Hence we should focus on the happy path and exception paths that are more common. We can test more exhaustively with lower-level unit or integration tests.   
1. **Not following “Tell Don’t Ask" Principle** - Instead of “asking” for data from the page objects and performing operations on them, “tell” the page object to do the operations. This is mostly seen in the verification methods where assertions are done in the page object instead of in the test case. This improves readability and maintainability as data and behavior are placed together.  
  
## FAQ  
  
**Why are all the tests done in one `testAll()` method?**  
We bundle together everything as one test case instead of having multiple test cases. The advantage is that the time for the whole test class will be reduced because we minimize repetitive per-method setup/teardown. The downside is that it increases the time spent on re-running failed tests as the whole class has to be re-run. We opt for this approach because we expect tests to pass more frequently than to fail.  
  
**Why is there one JSON file for each test case?**  
Each test case has its own JSON file and the data inside has a unique prefix to prevent clashes in the database that may cause test failure, since tests are run concurrently.