
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

To update a library's version, simply change the version number declared in `build.gradle` file.

- If you are using command line, your next build will automatically reflect the change.
- If you are using Eclipse, right click on the project in the Project Explorer and select `Gradle → Refresh Gradle Project` for the changes to be reflected.

# Managing Client-Side Dependencies

We use [NPM registry](https://www.npmjs.com) as the source of library codes.

To add/update libraries for CSS/JS, modify the appropriate entry in `src/main/resources/package.json`.
Additionally, when adding new libraries, find the files from the library that are necessary to be loaded to webpages, and add a new entry(ies) in `FrontEndLibrary.java`.

If the library cannot be found in NPM, simply host a local copy in the repository.
