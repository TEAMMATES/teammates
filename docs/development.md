<frontmatter>
  title: "Development"
</frontmatter>

# Development Guidelines

These are the common tasks involved when working on features, enhancements, bug fixes, etc. for TEAMMATES.

The instructions in all parts of this document work for Linux, OS X, and Windows, with the following pointers:

- Replace `./gradlew` to `gradlew.bat` if you are using Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.
- It is assumed that the development environment has been correctly set up. If this step has not been completed, refer to [this document](setting-up.md).

> If you encounter any problems during the any of the processes, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request on our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

## Managing the dev server: front-end

<box type="definition">

Dev server is the server run in your local machine.
</box>

<box type="definition">

Front-end dev server is the Angular-based server handling the user interface.
</box>

First, you need to compile some type definitions from the back-end to be used in this dev server. Run the following command:

```sh
./gradlew generateTypes
```

To start the dev server, run the following command until you see something like `Angular Live Development Server is listening on localhost`:

```sh
npm run start
```

The dev server URL will be given at the console output, e.g. `http://localhost:4200`.

To stop the dev server, press `Ctrl + C`.

- The dev server is run in _watch mode_ by default, i.e. any saved change to the front-end code will be propagated to the server immediately.
- The dev server is also run in _live reload mode_ by default, i.e. any saved change to the front-end code will automatically load all dev server web pages currently being opened.
  To disable this behaviour, run the dev server as follows instead:

  ```sh
  npm run start -- --live-reload=false
  ```

## Managing the dev server: back-end

<box type="definition">

Back-end dev server is the Jetty-based server handling all the business logic, including data storage.
</box>

### Pre-requisites

In order for the back-end to properly work, you need to have a running database instance and a full-text search service instance (if you are supporting one). The instances can either be a local emulator or a running production instance.

The details on how to run them locally can be found [here (for local Datastore emulator)](#running-the-datastore-emulator) and [here (for full-text search service)](search.md).

If you have access to Docker, we have a Docker compose definition to run those services:

```sh
docker-compose up -d
```

### Starting the dev server

<box type="wrong">

Some IDEs may offer a shortcut to run the Application main class directly. Do not run the server this way.
</box>

To start the server in the background, run the following command
and wait until the task exits with a `BUILD SUCCESSFUL`:

```sh
./gradlew serverRun &
```

To start the server in the foreground (e.g. if you want the console output to be visible),
run the following command instead:

```sh
./gradlew serverRun
```

The dev server URL will be `http://localhost:8080`.

### Stopping the dev server

If you started the server in the background, use any method available in your OS to stop the process at port `8080`.

If the server is running in the foreground, press `Ctrl + C` (or equivalent in your OS) to stop it.

## Building front-end files

In order for the dev server to be able to serve both the front-end and the back-end of the application, the front-end files need to be *bundled and transpiled* (afterwards `built`).

Run the following commands to build the front-end files for the application's use in production mode:

```sh
# Generate type definition file from back-end
./gradlew generateTypes

# Bundle, transpile, and minify front-end files
npm run build
```

After this, the back-end dev server will also be able to serve the front-end.

## Logging in to a TEAMMATES instance

This instruction set applies for both dev server and production server, with slight differences explained where applicable.

- The local dev server is assumed to be accessible at `http://localhost:8080`.
  - This instruction also works when the local front-end dev server and back-end dev server are separate. In that case, the dev server address will be the front-end's, e.g. `http://localhost:4200`. However, a back-end server needs to be running in order for the authentication logic to work.
- If a URL is given as relative, prepend the server URL to access the page, e.g `/web/page/somePage` is accessible in dev server at `http://localhost:8080/web/page/somePage`.

<panel header="**As administrator**">

1. Go to any administrator page, e.g `/web/admin/home`. You may be prompted to log in.
  You will be granted access only if your account has admin permission as defined in `build.properties`.

1. When logged in as administrator, ***masquerade mode*** can also be used to impersonate instructors and students by adding `user=username` to the URL
  e.g `http://localhost:8080/web/student/home?user=johnKent`.

</panel>

<panel header="**As instructor**">

You need an instructor account which can be created by administrators.

1. Log in to `/web/admin/home` as an administrator.
1. Enter credentials for an instructor, e.g<br>
   Name: `John Dorian`<br>
   Email: `teammates.instructor@university.edu`<br>
   Institution: `National University of Singapore`<br>
1. The system will send an email containing the join link to the added instructor.<br>
   On the dev server, this email will not be sent. Instead, you can use the join link given after adding an instructor to complete the joining process.

Alternatively, an instructor can create other instructors for a course if s/he has sufficient privileges. A course co-owner, for example, will have such a privilege.

1. Ensure that there is a course to add instructors to and an instructor in that course with the privilege to add instructors.
1. Log in as that instructor.
1. Add the instructors for the course (`Instructors` → `View/Edit`).
1. The system will send an email containing the join link to each added instructor. Again, this will not happen on the dev server, so additional steps are required.
1. Log out and log in to `http://localhost:8080/web/admin/search` as administrator.
1. Search for the instructor you added in. From the search results, click anywhere on the desired row to get the course join link for that instructor.
1. Log out and use that join link to log in as the new instructor.

</panel>

<panel header="**As student**">

You need a student account which can be created by instructors (with sufficient privileges).

The steps for adding a student is almost identical to the steps for adding instructors by another instructor:

- Where appropriate, change the reference to "instructor" to "student".
- `Students` → `Enroll` to add students for the course.

</panel>
</br>

### Logging in without UI

In dev server, it is also possible to "log in" without UI (e.g. when only testing API endpoints). In order to do that, you need to submit the following API call:

```sh
POST http://localhost:8080/devServerLogin?email=test@example.com
```

where `email=test@example.com` can be replaced as appropriate.

The back-end server will return cookies which will subsequently be used to authenticate your requests.

To "log out", submit the following API call:

```sh
GET http://localhost:8080/logout
```

## Running the Datastore emulator

The Datastore emulator is an essential tool that we use to locally simulate production Datastore environment during development and testing of relevant features. For more information about the Datastore emulator, refer to [Google's official documentation](https://cloud.google.com/datastore/docs/tools/datastore-emulator).

<panel header="**Using quickstart script**">

You can use the pre-provided quickstart script which will run a local Datastore emulator instance sufficient for development use cases. The script is run via the following command:

```sh
./gradlew runDatastoreEmulator
```

The Datastore emulator will be running in the port specified in the `build.properties` file.

</panel>

<panel header="**Using Docker-based tooling**">

We have a Docker compose definition to run dependent services, including local Datastore emulator. Run it under the `datastore` service name and bind to the container port `8484`:

```sh
docker-compose run -p 8484:8484 datastore
```

</panel>

<panel header="**Using Cloud SDK**">

Alternatively, you can use `gcloud` command to manage the local Datastore emulator instance directly. For this, you need a working [Google Cloud SDK](https://cloud.google.com/sdk/docs) in your development environment.

1. Install the Datastore emulator component if you have not done so:

   ```sh
   gcloud components install cloud-datastore-emulator
   ```

1. To run the emulator in port `8484`:

   ```sh
   gcloud beta emulators datastore start --host-port=localhost:8484
   ```

   Wait until you see the following message:

   ```
   [datastore] Dev App Server is now running.
   ```

</panel>
<br>

### Stopping the emulator

To stop the Datastore emulator, use any method available in your OS to locate and stop the process at the port used for the emulator.

If you are using the Cloud SDK method, you can use `Ctrl + C` in the console to stop the process. If the emulator fails to stop gracefully, use the previously described method.

## Testing

There are two big categories of testing in TEAMMATES:

- **Component tests**: white-box unit and integration tests, i.e. they test the application components with full knowledge of the components' internal workings. This is configured in `src/test/resources/testng-component.xml` (back-end) and `src/web/jest.config.js` (front-end).
- **E2E (end-to-end) tests**: black-box tests, i.e. they test the application as a whole without knowing any internal working. This is configured in `src/e2e/resources/testng-e2e.xml`. To learn more about E2E tests, refer to this [document](e2e-testing.md).  

#### Running the tests

To run all front-end component tests in watch mode (i.e. any change to source code will automatically reload the tests), run the following command:

```sh
npm run test
```

To run all front-end component tests once and generate coverage data afterwards, run the following command:

```sh
npm run coverage
```

To run an individual test in a test file, change `it` in the `*.spec.ts` file to `fit`.

To run all tests in a test file (or all test files matching a pattern), you can use Jest's watch mode and filter by filename pattern.

Back-end component tests follow this configuration:

Test suite | Command | Results can be viewed in
---|---|---
`Component tests` | `./gradlew componentTests` | `{project folder}/build/reports/tests/componentTests/index.html`
Any individual component test | `./gradlew componentTests --tests TestClassName` | `{project folder}/build/reports/tests/componentTests/index.html`

You can generate the coverage data with `jacocoReport` task after running tests, e.g.:

```sh
./gradlew componentTests jacocoReport
```

The report can be found in the `build/reports/jacoco/jacocoReport/` directory.

## Deploying to a staging server

> `Staging server` is the server instance you set up on Google App Engine for hosting the app for testing purposes.

For most cases, you do not need a staging server as the dev server has covered almost all of the application's functionality.
If you need to deploy your application to a staging server, refer to [this guide](https://github.com/TEAMMATES/teammates-ops/blob/master/platform-guide.md#deploying-to-a-staging-server).

## Running client scripts

> Client scripts are scripts that remotely manipulate data on a Google Cloud Datastore instance. They are run as standard Java applications.

Most of developers may not need to write and/or run client scripts but if you are to do so, take note of the following:

* If you are to run a script in a production environment, there are additional steps to follow. Refer to [this guide](https://github.com/TEAMMATES/teammates-ops/blob/master/platform-guide.md#running-client-scripts).
* It is not encouraged to compile and run any script via command line; use any of the supported IDEs to significantly ease this task.

## Config points

There are several files used to configure various aspects of the system.

**Main**: These vary from developer to developer and are subjected to frequent changes.

* `build.properties`: Contains the general purpose configuration values to be used by the web API.
* `config.ts`: Contains the general purpose configuration values to be used by the web application.
* `test.properties`: Contains the configuration values for the test driver.
  * There are two separate `test.properties`; one for component tests and one for E2E tests.
* `client.properties`: Contains some configuration values used in client scripts.
* `app.yaml`: Contains the configuration for deploying the application on GAE.

**Tasks**: These do not concern the application directly, but rather the development process.

* `build.gradle`: Contains the back-end third-party dependencies specification, as well as configurations for automated tasks/routines to be run via Gradle.
* `gradle.properties`, `gradle-wrapper.properties`: Contains the Gradle and Gradle wrapper configuration.
* `package.json`: Contains the front-end third-party dependencies specification, as well as configurations for automated tasks/routines to be run via NPM.
* `angular.json`: Contains the Angular application configuration.

**GitHub Actions**: These are workflow files for GitHub Actions. They are placed under `.github/workflows` directory.

* `component.yml`: Configuration for component tests.
* `e2e.yml`: Configuration for E2E tests.
* `e2e-cross.yml`: Configuration for cross-browser E2E tests.
* `lnp.yml`: Configuration for load & performance tests.
* `dev-docs.yml`: Configuration for developer documentation site.

**Static Analysis**: These are used to maintain code quality and measure code coverage. See [Static Analysis](static-analysis.md).
* `static-analysis/*`: Contains most of the configuration files for all the different static analysis tools.

**Other**: These are rarely, if ever will be, subjected to changes.
* `logging.properties`: Contains the java.util.logging configuration.
* `web.xml`: Contains the web server configuration, e.g servlets to run, mapping from URLs to servlets, security constraints, etc.
* `cron.yaml`: Contains the cron jobs specification.
* `queue.yaml`: Contains the task queues configuration.
* `index.yaml`: Contains the Google Cloud Datastore indexes configuration.
