<frontmatter>
  title: "Frontend Development"
</frontmatter>

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

