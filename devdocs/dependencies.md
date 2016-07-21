
# Managing Server-Side Dependencies

TEAMMATES uses Gradle to manage dependencies to external Java libraries.

- [Adding new libraries](#adding-new-libraries)
- [Updating libraries](#updating-libraries)

## Adding new libraries

There are two factors to consider:
- Whether the library is for production or non-production code
- Whether the library is needed for compile-time or runtime only

Add the library based on the name listed in [Maven Central](http://search.maven.org) in the appropriate section in the `build.gradle` file.

## Updating libraries

> Note the following:
- Change `./gradlew` to `gradlew.bat` in Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.

To update a library's version, simply change the version number declared in `build.gradle` file.

To propagate the update of dependencies to your Eclipse configuration, run the following command:

`./gradlew resetEclipseDeps`

Sometimes, the changes from this command might not show up in Eclipse immediately. "Refreshing" the project or restarting Eclipse should fix that.

# Managing Client-Side Dependencies

The dependencies for CSS/JS are hosted in the repository and additionally listed down in `src/main/webapp/package.json` for versioning purpose.

The recommended way to add/update client-side dependencies is using [NPM](https://nodejs.org).
Obtain the package name and version number as listed in [NPM registry](https://www.npmjs.com), then run the following command to obtain a local copy of the package and simultaneously update the package file:

```sh
npm install --prefix src/main/webapp --save --save-exact package@version

# Alternatively, navigate to the src/main/webapp directory and run the shorter version of the command:
npm install --save --save-exact package@version
```

Afterwards, copy the necessary package files (usually in the `/dist` or `/build` folder of the package) into the appropriate directory in the repository.

If the library cannot be found in NPM, simply host a local copy in the repository without updating the package file.
