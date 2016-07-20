
# Managing Server-Side Dependencies

TEAMMATES uses Gradle to manage dependencies to external Java libraries.

- [Adding new libraries](#adding-new-libraries)
- [Updating libraries](#updating-libraries)

## Adding new libraries

There are two factors to consider:
- Whether the library is for production or non-production code
- Whether the library is needed for compile-time or runtime only

Add the library in the appropriate section in the `build.gradle` file.

## Updating libraries

> Note the following:
- Change `./gradlew` to `gradlew.bat` in Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.

To update a library's version, simply change the version number declared in `build.gradle` file.

To propagate the update of dependencies to your Eclipse configuration, run the following command:

`./gradlew resetEclipseDeps`

Sometimes, the changes from this command might not show up in Eclipse immediately. "Refreshing" the project or restarting Eclipse should fix that.

# Managing Client-Side Dependencies

Currently, the dependencies for CSS/JS are hosted in the repository and listed down in `src/main/webapp/package.json`.

When adding new libraries, try to find the library in the [npm registry](https://www.npmjs.com) and if it can be found, list it in the aforementioned package file, then download and include the necessary library files (usually in `/dist` or `/build` folder) in the repository.
Node.js developers can do this with the command `npm install --save package@version`. Remember to exclude any non-exact version syntax (e.g `^` and `~`) in the package file as we require specific versions.

Otherwise, simply host the necessary library files in the repository without updating the package file.
