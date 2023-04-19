<frontmatter>
  title: "Troubleshooting Guide"
</frontmatter>

# Developer Troubleshooting Guide

This document can help you to fix the common problems encountered while contributing to TEAMMATES.

## Common setup errors and solutions

<panel header="The back-end server fails with `java.io.FileNotFoundException`" no-close>

Due to framework limitation, this is an expected behavior if the working directory contains a space character in it.
This can be resolved by moving the repository to another directory.
</panel>

<panel header="The back-end server is &quot;stuck&quot; at `85% EXECUTING` or alike" no-close>

This is the expected behaviour if you are running the server in the foreground.
</panel>

<panel header="The front-end dev server fails with `Cannot find module '../api-output'` or similar" no-close>

[The front-end type definitions need to be built](development.md#managing-the-dev-server-front-end) before running the dev server.
</panel>

<panel header="After pulling changes from the `master` branch, the previously working front-end dev server fails to start" no-close>

This is possible if a part of API input/output definition changes.
Simply rerun the command to build the type definitions to resolve the problem.
</panel>

<br>

## Common test errors and solutions

<panel header="A handful of failed E2E test cases (< 10)" no-close>

Re-run the failed tests with TestNG, all test cases should pass eventually (it may take a few runs). If there are tests that persistently fail and not addressed in other parts of this guide, you may [request for help in the discussion forum](https://github.com/TEAMMATES/teammates/discussions/new?category=help-requests).
</panel>

<panel header="Tests fail due to accented characters" no-close>

Ensure that the text file encoding for your workspace has been set to `UTF-8` as specified under [Setting up guide](setting-up.md).
</panel>

<panel header="`java.net.ConnectException: Connection refused` when running E2E tests" no-close>

Ensure that your dev server is started prior to running those tests.
</panel>

<panel header="`org.openqa.selenium.WebDriverException: Unable to bind to locking port 7054 within 45000 ms` when running tests with Browser" no-close>

Ensure compatible version of Firefox is installed as specified under [Development process document](development.md#testing).
</panel>

<panel header="When running E2E tests: `Selenium cannot find Firefox binary in PATH`" no-close>
  
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
