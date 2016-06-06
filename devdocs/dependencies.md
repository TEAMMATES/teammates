
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

Updating libraries has immediate effect on two items: Eclipse `.classpath` and project's output directory, specifically the `WEB-INF/lib` folder.

### Updating Eclipse `.classpath`

The Eclipse `.classpath` needs to be updated whenever there is a change to compile-time libraries.
Here are the steps:

1. Delete the existing `.classpath` file.
2. Run the command `./gradlew eclipseClasspath`.
3. If there is an Eclipse instance running the project, you need to refresh it for the changes to take effect.

### Updating output directory, `WEB-INF/lib`

This directory needs to be updated whenever there is a change to production libraries.
Here are the steps:

1. Delete any previous version of the updated library. For example, if you are updating App Engine SDK to `1.9.27`, the existing App Engine SDK libraries should be deleted.
   Alternatively, if the previous version of the library is too difficult to be tracked (e.g due to too many transitive dependencies), simply delete the entire `WEB-INF/lib` folder.
   This step can be skipped if there are no outdated libraries (i.e adding previously non-existing libraries).
2. Run the command `./gradlew copyDepsToWebInfLib`.
