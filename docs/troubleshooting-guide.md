# Developer Troubleshooting Guide

This document can help you to fix the common problems encountered while contributing to TEAMMATES.
Note that some of the screenshots might be outdated, but the instructions will remain the same and all necessary modifications will be explained.

* [Troubleshooting project setup](#troubleshooting-project-setup)
    * [Common setup errors and solutions](#common-setup-errors-and-solutions)
* [Troubleshooting test failures](#troubleshooting-test-failures)
    * [Common test errors and solutions](#common-test-errors-and-solutions)
* [Submitting help request](#submitting-help-request)

## Troubleshooting project setup

### Common setup errors and solutions

* **ERROR**: After downloading and installing Google Cloud SDK, running any `gcloud` command results in `gcloud: command not found` or alike.

  **REASON**: You did not choose to update the `PATH` variable when installing the SDK.

  **SOLUTION**: You can re-run the install command again without any side effect. Make sure to choose to update the `PATH` variable this time. Alternatively, you can use other appropriate methods to update your `PATH` variable to include the `/bin` sub-folder of the SDK folder.<br>
  To verify this, try running any `gcloud` command and it should now give you access to the SDK.

## Troubleshooting test failures

### Common test errors and solutions

* **ERROR**: Encountered `java.net.ConnectException: Connection refused` when running E2E tests.

  **SOLUTION**: Ensure that your dev server is started prior to running those tests.

* **ERROR**: Encountered `org.openqa.selenium.WebDriverException: Unable to bind to locking port 7054 within 45000 ms` when running tests with Browser.

  **SOLUTION**: Ensure compatible version of Firefox is installed as specified under [Development process document](development.md#testing).

* **ERROR**: Test failure message encountered when running full test suite: "Selenium cannot find Firefox binary in PATH".

  **REASON 1**: Path to Firefox executable on local machine is incorrect.

  **SOLUTION 1 (on Windows)**: Specify the correct folder in system PATH variable.

  Open Windows Explorer → Right-click on Computer → Advanced System Settings → "Advanced" tab → Environment Variables… → Select "PATH" from the list → Add directory of "Mozilla Firefox" folder to "Variable value" field.

  **REASON 2**: Incorrect custom path in `test.firefox.path`.

  **SOLUTION 2**: Make sure that the path is set correctly following the example from `test.template.properties`.

* **ERROR**: A handful of failed test cases (< 10).

  **SOLUTION**: Re-run the failed tests with TestNG, all test cases should pass eventually (it may take a few runs). If there are tests that persistently fail and not addressed in other parts of this guide, you may [request for help in the issue tracker](https://github.com/TEAMMATES/teammates/issues/new?template=help-request.md).

* **ERROR**: Tests fail due to accented characters.

  **SOLUTION**: Ensure that the text file encoding for your workspace has been set to `UTF-8` as specified under [Setting up guide](setting-up.md).

* **ERROR (on Linux)**: `java.io.IOException: Directory "/tmpfiles" could not be created`.

   **SOLUTION**: Add `-Djava.io.tmpdir=/path/to/teammates/tmp` for the tests' run configurations. The "tmp" folder in the specified directory needs to be created before running the tests.

## Submitting help request

If none of the items in this guide helps with the problem you face, you can [post in the issue tracker](https://github.com/TEAMMATES/teammates/issues/new?template=help-request.md) to request for help. Remember to supply as much relevant information as possible when requesting for help.
