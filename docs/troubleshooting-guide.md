<frontmatter>
  title: "Troubleshooting Guide"
</frontmatter>

# Developer Troubleshooting Guide

This guide covers common problems encountered while setting up and contributing to TEAMMATES.

## Common setup errors and solutions

<panel header="The backend server fails with `java.io.FileNotFoundException`" no-close>

Due to framework limitation, this is an expected behavior if the working directory contains a space character in it.
This can be resolved by moving the repository to another directory.
</panel>

<panel header="The backend server is &quot;stuck&quot; at `85% EXECUTING` or alike" no-close>

This is the expected behaviour if you are running the server in the foreground.
</panel>

<panel header="`PSQLException: FATAL: password authentication failed for user 'teammates'` when connecting to database" no-close>

This is possible if a local PostgreSQL instance is already running on port 5432.
This can be fixed by manually killing the local PostgreSQL server process before running ./gradlew serverRun again.
</panel>

<panel header="Backend fails to start with `org.hibernate.tool.schema.spi.SchemaManagementException`" no-close>

Refer to troubleshooting steps for [Schema Validation Failures](./how-to/schema-migration.html#schema-validation-failures).
</panel>

<br>

## Common test errors and solutions

<panel header="A handful of failed E2E test cases (< 10)" no-close>

Re-run the failed tests with TestNG, all test cases should pass eventually (it may take a few runs). If there are tests that persistently fail and not addressed in other parts of this guide, you may [request for help in the discussion forum](https://github.com/TEAMMATES/teammates/discussions/new?category=help-requests).
</panel>

<panel header="`java.net.ConnectException: Connection refused` when running E2E tests" no-close>

Ensure that your dev server is started prior to running those tests.
</panel>

<panel header="`org.openqa.selenium.WebDriverException: Unable to bind to locking port 7054 within 45000 ms` when running tests with Browser" no-close>

Ensure compatible version of Firefox is installed as specified in the [E2E Testing Instructions](./how-to/testing.md#e2e-tests).
</panel>

<panel header='On Linux: `java.io.IOException: Directory "/tmpfiles" could not be created`' no-close>

Add `-Djava.io.tmpdir=/path/to/teammates/tmp` for the tests' run configurations. The "tmp" folder in the specified directory needs to be created before running the tests.
</panel>

<br/>

## Submitting help request

If none of the above helps, [post in GitHub Discussions](https://github.com/TEAMMATES/teammates/discussions/new?category=help-requests) with as much relevant information as possible.
