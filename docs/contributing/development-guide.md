<frontmatter>
  title: "Development Guide"
</frontmatter>

# Development Guide

This document describes the common development tasks for TEAMMATES. It is assumed that you have already [set up your development environment](../getting-started.md).

<box type=info>
<md>
If you encounter any issues, refer to the [Troubleshooting Guide](../troubleshooting-guide.md) before seeking help in [GitHub Discussions](https://github.com/TEAMMATES/teammates/discussions).
</md>
</box>

## Running the Frontend Server

Start the frontend server:

```sh
npm run start
```

The server will be available at `http://localhost:4200`. It runs in watch and live reload mode by default — any saved changes will be reflected immediately.

To disable live reload:

```sh
npm run start -- --no-live-reload
```

Type definitions for the frontend are generated from backend API types and must be kept in sync. Run the following after making changes to any backend API types:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew generateTypes
```

</tab>
<tab header="Windows">

```sh
gradlew.bat generateTypes
```

</tab>
</tabs>

## Running the Backend Server

Start the database if it is not already running:

```sh
docker compose up -d
```

Apply database migrations if necessary:

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

Then start the backend server:

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

The server will be available at `http://localhost:8080`.

To stop the server, press `Ctrl + C`.

<box type="info">
<md>
To run the backend server in the background, append `&` to the command. To stop it, kill the process on port `8080`.
</md>
</box>

## Serving frontend files from the backend

To serve the frontend from the backend server (e.g. for production environments or E2E tests):

```sh
npm run build
```

The frontend can now be accessed from `http://localhost:8080` or your configured backend URL.

## Running Tests

To run component tests:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew componentTests
npm run test
```

</tab>
<tab header="Windows">

```sh
gradlew.bat componentTests
npm run test
```

</tab>
</tabs>

To run static analysis tools:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew lint --continue
npm run lint
```

</tab>
<tab header="Windows">

```sh
gradlew.bat lint --continue
npm run lint
```

</tab>
</tabs>

TEAMMATES also includes end-to-end and accessibility tests. For more information, refer to the [testing guide](../how-to/testing.md).

## Config Points

**Main config files** — vary per developer and change frequently:

| File                   | Purpose                                                                        |
| ---------------------- | ------------------------------------------------------------------------------ |
| `build.properties`     | Configuration for the Backend                                                  |
| `build-dev.properties` | Local development overrides for `build.properties`                             |
| `config.ts`            | Configuration for the Frontend                                                 |
| `test.properties`      | Configuration for the test driver (separate files for component and E2E tests) |

**Build config files** — manage dependencies and automated tasks:

| File           | Purpose                               |
| -------------- | ------------------------------------- |
| `build.gradle` | Backend dependencies and Gradle tasks |
| `package.json` | Frontend dependencies and NPM tasks   |
| `angular.json` | Angular application configuration     |

**Static analysis** — configuration files under `static-analysis/`. See [static analysis guide](../how-to/static-analysis.md).
