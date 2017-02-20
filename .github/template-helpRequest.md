**Help Request**

**Issue Title:** Help Request: [summary of your problem]

* **Operating System:**
* **JDK Version:**
* **Eclipse Version:**
* **Firefox / Chrome Version (where applicable):**

**Step in the `settingUp.md` Guide / Failing Tests**

*If you are setting up the project:*

> Tell us the step in the setting up guide at which you are stuck

*If you are resolving test failures:*

> Tell us which tests are failing

*Miscellaneous*

> Tell us what you were trying to to / what you need help with

**Error Messages Shown...**

*...in the TestNG tab, if you are resolving test failures:*

```
java.lang.AssertionError:
        at org.testng.AssertJUnit.fail(AssertJUnit.java:59)
        at org.testng.AssertJUnit.assertTrue(AssertJUnit.java:24)
        at org.testng.AssertJUnit.assertNotNull(AssertJUnit.java:267)
        at org.testng.AssertJUnit.assertNotNull(AssertJUnit.java:259)
        at teammates.test.cases.BaseTestCase.assertNotNull(BaseTestCase.java:177)
        at teammates.test.cases.action.AdminAccountDeletePageActionTest.testExecuteAndPostProcess(AdminAccountDeletePageActionTest.java:42)
```

*...in the Console, for other cases:*

```
java.lang.reflect.InvocationTargetException
        at com.google.appengine.runtime.Request.process-e60877bf1213698c(Request.java)
        at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
        at teammates.ui.controller.InstructorFeedbackQuestionEditAction.validateQuestionGiverRecipientVisibility(InstructorFeedbackQuestionEditAction.java:132)
        at teammates.ui.controller.InstructorFeedbackQuestionEditAction.editQuestion(InstructorFeedbackQuestionEditAction.java:80)
        at teammates.ui.controller.InstructorFeedbackQuestionEditAction.execute(InstructorFeedbackQuestionEditAction.java:52)
        at teammates.ui.controller.Action.executeAndPostProcess(Action.java:344)
        ...
Caused by: java.lang.AssertionError: Contrib Qn Invalid visibility options
        at teammates.common.util.Assumption.fail(Assumption.java:61)
        at teammates.common.util.Assumption.assertTrue(Assumption.java:29)
        at teammates.common.datatransfer.FeedbackContributionQuestionDetails.validateGiverRecipientVisibility(FeedbackContributionQuestionDetails.java:755)
        ... 36 more
```

**How You Tried Solving the Problem**

> Tell us the steps you have taken to solve the problem
>
> e.g. I deleted everything and started over, but the exact same problems occurred at step 3

**Screenshots (recommended)**

> Include a screenshot of what you are having trouble with to help us help you
