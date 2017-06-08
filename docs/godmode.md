# GodMode User Notes

## What is GodMode?

Typically browser tests involve comparing the source of the webpage to an existing *expected* source. This serves to verify the DOM structure (HTML) of the page. However, creating new browser tests and updating outdated ones require considerable effort on the developer's part. In addition, ensuring that the generated *expected* source code works across computers, browsers, user accounts, and execution times is harder still. To overcome this problem, GodMode provides a simple way to create and update *expected* source files for browser tests and ensure the necessary cross-compatibility.

GodMode has been extended and now is also able to create and update *expected* source files for email content tests. It works with the same underlying principle as the one for browser tests.

## How does GodMode work?

The essential idea is to reverse the process of testing. We use the _actual_ source of the webpage to overwrite the _expected_ source in the test. To remove redundancy, even if GodMode is enabled, this overwriting procedure only happens when a test fails during the test run. Finally before the changes are committed, a *manual* (by the developer) verification to ensure only the intended changes have occurred is mandatory.

## How do we use GodMode?

GodMode can be activated by setting the value of `test.godmode.enabled` to `true` in `test.properties`. Now all test runs would have GodMode enabled. Please remember to set it back to `false` when done.

## When do we use GodMode?

GodMode is typically used in the following two situations:

1. To create a new source file for a (new) browser test or email content test.
2. To update existing source files to reflect intended changes to the UI of the web pages or the email content.

The following example describes the behaviour of GodMode and how it can be used in practice.
Let us consider the case where the following line of test code is executed with GodMode enabled:
```java
studentHomePage.verifyHtmlMainContent("/studentHomeTypicalHTML.html");
```

Here are three possible situations and the corresponding behaviours of GodMode when the test is executed with GodMode enabled:

1. If `studentHomeTypicalHTML.html` exists and has the correct content, GodMode will not make any updates to the source file.

2. If `studentHomeTypicalHTML.html` exists but has the wrong content, GodMode will update the source file with the correct content. The effect of this is that the test case will pass subsequent test runs with/without GodMode enabled.

3. If `studentHomeTypicalHTML.html` does not exist, GodMode will create a source file with the given name AND with the correct content. The effect of this is that the test case will pass subsequent test runs with/without GodMode enabled.

The same idea applies to email content test:
```java
EmailChecker.verifyEmailContent(email, recipient, subject, "/studentCourseJoinEmail.html");
```

## Best Practices

1. Ensure that GodMode is only used when necessary, that is when there are new test cases being created or when an update to the existing tests is foreseen.

2. Please remember to disable GodMode once the necessary changes have been made, before committing the changes.

3. Please confirm that all the changes made by GodMode are EXPECTED. If any unexpected changes are made, please ask for assistance in the issue tracker or create a new issue if need be.

4. After all the necessary changes have been made, run the test suite once without GodMode enabled to ensure that the tests pass without GodMode.

## Final Notes

DO NOT create or modify the *expected* html pages in the tests manually. Use GodMode even for seemingly trivial changes. Also, note that the generated html may not reflect the browser's source identically. Some modifications have been made to achieve cross-compatibility (eg: white space standardization).

Running browser tests with GodMode enabled can lead to false positive results since html comparison failures are suppressed when the test is run with GodMode enabled. This further underscores the need to run the test suite WITHOUT GodMode enabled to truly test the system.

In general, only the lines that are modified should be changed by GodMode. However, since GodMode standardizes the white spacing, sometimes multiple (seemingly unrelated) lines may be affected due to changes in the indentation, and as such it is not a cause for concern. An example of this is shown below:

```html
<div>
  <span>
    <img>
  </span>
</div>
```
to
```html
<div>
  <div>
    <span>
      <img>
    </span>
  </div>
</div>
```

Happy Testing!
