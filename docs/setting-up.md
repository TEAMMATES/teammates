# Setting up a development environment

This is a step-by-step guide to setting up a development environment in your local machine.
You will use that environment to work on features, enhancements, bug fixes, etc. which ultimately allows you to contribute to the project.

The instructions in all parts of this document work for Linux, OS X, and Windows, with the following pointers:
- Replace `./gradlew` to `gradlew.bat` if you are using Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.
- When a version is specified for any tool, install that version instead of the latest version available.

> If you encounter any problems during the setting up process, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request in our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

## Step 1: Install necessary tools and languages

1. Install Source Tree or other similar Git Client, or at least Git.
1. Install JDK 1.8 and JRE 1.7.
1. Install Node.js (minimum version 4.x).

## Step 2: Obtain your own repository copy

1. Fork our repo at https://github.com/TEAMMATES/teammates. Clone that fork to your hard disk.

1. Add a remote name (e.g `upstream`) for the main repo for your repo to keep in sync with.
   ```sh
   git remote add upstream https://github.com/TEAMMATES/teammates.git
   ```
   **Verification:** Use the command `git remote -v` and the following lines should be part of the output:
   ```
    upstream        https://github.com/TEAMMATES/teammates.git (fetch)
    upstream        https://github.com/TEAMMATES/teammates.git (push)
    ```

1. Set your `master` branch to track the main repo's `master` branch.
   ```sh
   git checkout master
   git branch -u upstream/master
   ```

More information can be found at [this documentation](https://help.github.com/articles/fork-a-repo/).

## Step 3: Set up project-specific settings and dependencies

1. Run this command to download the correct version Google App Engine SDK as used in the project:
   ```sh
   ./gradlew appengineDownloadSdk
   ```
   **Verification:** Check your Gradle folder (the directory can be found with the command `./gradlew printUserHomeDir`). A folder named appengine-sdk` should be present.

1. Run this command to download the necessary tools for JavaScript development:
   ```sh
   npm install
   ```
   **Verification:** A folder named `node_modules` should be added to the project root directory.

1. Run this command to create the main config files (these are not under revision control because their contents vary from developer to developer):
   ```sh
   ./gradlew createConfigs
   ```
   **Verification:** The file named `gradle.properties` should be added to the project root directory.

1. Modify the following config file:
   * `gradle.properties`
      * Update the variable `org.gradle.java.home` to point to a valid **JDK 1.8** directory.<br>
        You may skip this step if JDK 1.8 is already your system default as specified in your PATH variable.
        > We use JDK 1.8 as our standard development and test environment.
      * Update the variable `JRE7_HOME` to point to a valid **JRE 1.7** directory.<br>
        > JRE 1.7 libraries are required for proper cross-compilation to Java 1.7-compatible bytecode from the Java 1.8 compiler.
      > **Windows users** should use a **forward slash**(`/`) instead of the Windows default **backward slash**(`\`) while specifying the above paths.

## Step 4: (Optional but recommended) Set up an IDE

You are encouraged, but not required, to use an IDE to assist many development tasks.

We currently support two IDEs: Eclipse IDE (full support) and IntelliJ IDEA (experimental; limited support).
Support requests related to other IDEs will not be entertained.

Refer to [this document](ide-usage.md) if you wish to set up an IDE for developing TEAMMATES.

## Step 5: Start developing

If you followed every step correctly, you should have successfully set up the development environment.

Proceed to the development routine as outlined in [this document](development.md).
