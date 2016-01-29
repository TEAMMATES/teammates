#GodMode User Notes

##What is GodMode?

Typically browser tests involve comparing the source of the webpage to an existing *expected* source. This serves to verify the DOM structure (HTML) of the page. However, creating new browser tests and updating outdated ones require considerable effort on the developer's part. In addition, ensuring that the generated *expected* source code works across computers, browsers, user accountsa, and execution times is harder still. To overcome this problem, GodMode provides a simple way to create and update *expected* source files for browser tests and ensure the necessary cross-compatibility.


##How does GodMode work?

The essential idea is to reverse the process of testing, whereby the actual source of the webpage during the test is used to overwrite the *expected* source for the test, as opposed to the usual way of using the *expected* source as the reference and checking the actual source of the webpage. In actual implementation we check if the test already passes and only if it fails do we update the *expected* source. Thus running GodMode on passing tests poses no danger.


##How do we use GodMode?

GodMode can be activated in two different ways. 

1. If we want to execute arbitrary tests using GodMode, then update BaseUiTestCase class and set `enableGodMode = true` at the top of the class implementation. Now all test runs would have GodMode enabled. Please remember to set it back to false when done.

2. If we want to run a particular test suite using GodMode, then go to `Run -> Run Configurations` and update the appropriate one with the `-Dgodmode=true` VM argument. Please remember to remove the argument before committing the changes

Note: The first option encompasses the functionality of the second. By updating the BaseUiTestClass and running the intended test suite, we achieve the second option's effect.


##When do we use GodMode?

GodMode can be used to create a new source file for a new browser test. This is done by simply assuming that the *expected* source is available and writing the test code. Then by executing the method using GodMode, the *expected* source will be generated and saved with the name specified in the test code. The following code snippet illustrates this:

The following code is written WITHOUT a studentHomeTypicalHTML.html file. Then when this test is executed using GodMode, studentHomeTypicalHTML.html is automatically generated with the expected source.

```
StudentHomePage studentHomePage = loginAdminToPage(browser, detailsPageUrl, StudentHomePage.class);
studentHomePage.verifyHtmlMainContent("/studentHomeTypicalHTML.html");
```

GodMode can also be used to update test cases when there are EXPECTED changes in the source of the webpage. This is done by executing those test cases with GodMode enabled. At the end of the test run, the expected source is updated with the new changes. 


##Best Practices##

1. Ensure that GodMode is only used when necessary, that is when there are new test cases being created or when an update to the existing tests is foreseen.
2. Please remember to disable GodMode once the necessary changes have been made, before committing the changes.
3. Please confirm that all the changes made by GodMode are EXPECTED. If any unexpected changes are made, please ask for assistance in the issue tracker or create a new issue if need be.
4. After all the necessary changes have been made, run the test suite once without GodMode enabled to ensure that the tests pass without GodMode. 


##Final Note

DO NOT create or modify the *expected* html pages in the tests manually. Use GodMode even for seemingly trivial changes. Also, note that the generated html may not reflect the browser's source identically. Some modifications have been made to achieve cross-compatibility (eg: white space standardization).

In general, only the lines that are modified should be changed by GodMode. However, since GodMode standardizes the white spacing, sometimes multiple (seemingly unrelated) lines may be affected due to changes in the indentation. An example of this is shown below:
```
<div>
  <span>
    <img>
  </span>
</div>
```
to
```
<div>
  <div>
    <span>
      <img>
    </span>
  </div>
</div>
```

Happy Testing!