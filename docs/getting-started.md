<frontmatter>
  title: "Getting Started"
</frontmatter>

# Getting started

This guide will help you set up a local development environment for TEAMMATES.

<box type="info">
  <md>
If you encounter any issues, refer to the [Troubleshooting Guide](troubleshooting-guide.md) before seeking help in [GitHub Discussions](https://github.com/TEAMMATES/teammates/discussions).
  </md>
</box>

## Prerequisites

Before you begin, a basic familiarity with the following will help:

- Git
- Java
- Angular/TypeScript

## Step 1: Fork and Clone the Repository

1. Install Git.
2. Fork the project repository at [github.com/TEAMMATES/teammates](https://github.com/TEAMMATES/teammates) and clone your fork locally.
3. Add the main repository as a remote and fetch it:

```sh
git remote add upstream https://github.com/TEAMMATES/teammates.git
git fetch upstream
```

**Verification:** Run `git branch -r` and verify `upstream/master` is listed.

4. Track the main repo's `master` branch:

```sh
git checkout master
git branch -u upstream/master
```

## Step 2: Install Tools

1. Install **Java JDK 21**.
   - JDK 25 is also supported, as long as newer language features are not used.
2. Install **Node.js** (minimum version 24).
3. Install [**Docker**](https://www.docker.com/get-started/).

## Step 3: Configure the Project

1. Generate the main config files:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew createConfigs
```

</tab>
<tab header="Windows">

```sh
gradlew.bat createConfigs
```

</tab>
</tabs>

**Verification:** A `gradle.properties` file should appear in the project root.

1. Edit `gradle.properties` if needed:
   - Set `org.gradle.java.home` if you want to use a specific JDK.

2. Install frontend dependencies:

```sh
npm ci
```

**Verification:** A `node_modules` folder should appear in the project root.

## Step 3: Run the Application

1. Start the database:

```sh
docker compose up -d
```

2. Apply database migrations:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew liquibaseUpdate
```

</tab>
<tab header="Windows">

```sh
gradlew.bat liquibaseUpdate
```

</tab>
</tabs>

3. Start the backend server:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew serverRun
```

</tab>
<tab header="Windows">

```sh
gradlew.bat serverRun
```

</tab>
</tabs>

The backend will be available at `http://localhost:8080`.

4. Start the frontend server:

```sh
npm run start
```

The frontend will be available at `http://localhost:4200`.

## Step 4: Set up test accounts

To test TEAMMATES locally, you will need an instructor and a student account.

1. Visit [/web/admin/home](http://localhost:4200/web/admin/home) and log in as admin (`app_admin@gmail.com`).
2. Add an instructor account.
3. Use the admin search feature at [/web/admin/search](http://localhost:4200/web/admin/search) to find the account request, expand the row, and retrieve the account registration link to activate the instructor.
4. From the instructor home page at [/web/instructor/home](http://localhost:4200/web/instructor/home), enroll a test student.
5. Use the admin search feature to find the student, expand the row, and retrieve the course join link to activate the student account.
6. You now have access to all TEAMMATES features.

## Step 5: Next Steps

Your environment is now ready. Here's what to do next:

1. Read the [Contributing Guidelines](./contributing/guidelines.md) and [Development Workflow](./contributing/development-workflow.md).
2. Browse [good first issues](https://github.com/TEAMMATES/teammates/issues?q=is:issue+is:open+label:"good+first+issue") to find something to work on.
