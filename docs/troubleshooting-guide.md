<frontmatter>
  title: "Troubleshooting Guide"
</frontmatter>

# Developer Troubleshooting Guide

This document can help you to fix the common problems encountered while contributing to TEAMMATES.

## Common test errors and solutions

<panel header="A handful of failed test cases (< 10)" no-close>

Re-run the failed tests with TestNG, all test cases should pass eventually (it may take a few runs). If there are tests that persistently fail and not addressed in other parts of this guide, you may [request for help in the issue tracker](https://github.com/TEAMMATES/teammates/issues/new?template=help-request.md).
</panel>

<panel header="Tests fail due to accented characters" no-close>

Ensure that the text file encoding for your workspace has been set to `UTF-8` as specified under [Setting up guide](setting-up.md).
</panel>

<panel header="`java.net.ConnectException: Connection refused` when running E2E tests." no-close>

Ensure that your dev server is started prior to running those tests.
</panel>

<panel header="`org.openqa.selenium.WebDriverException: Unable to bind to locking port 7054 within 45000 ms` when running tests with Browser." no-close>

Ensure compatible version of Firefox is installed as specified under [Development process document](development.md#testing).
</panel>

<panel header="When running full test suite: `Selenium cannot find Firefox binary in PATH`" no-close>
  
**REASON 1**: Path to Firefox executable on local machine is incorrect.

**SOLUTION 1 (on Windows)**: Specify the correct folder in system PATH variable.

Open Windows Explorer → Right-click on Computer → Advanced System Settings → "Advanced" tab → Environment Variables… → Select "PATH" from the list → Add directory of "Mozilla Firefox" folder to "Variable value" field.

**REASON 2**: Incorrect custom path in `test.firefox.path`.

**SOLUTION 2**: Make sure that the path is set correctly following the example from `test.template.properties`.
</panel>

<panel header='On Linux: `java.io.IOException: Directory "/tmpfiles" could not be created`' no-close>

Add `-Djava.io.tmpdir=/path/to/teammates/tmp` for the tests' run configurations. The "tmp" folder in the specified directory needs to be created before running the tests.
</panel>

<br/>

## Submitting help request

If none of the items in this guide helps with the problem you face, you can [post in the issue tracker](https://github.com/TEAMMATES/teammates/issues/new?template=help-request.md) to request for help. Remember to supply as much relevant information as possible when requesting for help.
