# Setting up a development environment

This is a step-by-step guide to setting up a development environment in your local machine.
You will use that environment to work on features, enhancements, bug fixes, etc. which ultimately allows you to contribute to the project.

The instructions in all parts of this document work for Linux, OS X, and Windows, with the following pointers:
- Replace `./gradlew` to `gradlew.bat` if you are using Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.
- When a version is specified for any tool, install that version instead of the latest version available.

> If you encounter any problems during the setting up process, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request in our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

## Step 1: Install necessary tools and languages

1. Install Git.
   1. (Optional but recommended) Install Sourcetree or other similar Git client.
1. Install JDK 1.8.
1. Install Python 2.7.
1. Install Node.js (minimum version 4.x).

## Step 2: Obtain your own repository copy

1. Fork our repo at https://github.com/TEAMMATES/teammates. Clone that fork to your hard disk.

1. Add a remote name (e.g `upstream`) for the main repo for your repo to keep in sync with, and then fetch the remote-tracking branches from the main repo.
   ```sh
   git remote add upstream https://github.com/TEAMMATES/teammates.git
   git fetch upstream
   ```
   **Verification:** Use the command `git branch -r` and the following lines should be part of the output:
   ```
    upstream/master
    upstream/release
    ```

1. Set your `master` branch to track the main repo's `master` branch.
   ```sh
   git checkout master
   git branch -u upstream/master
   ```

More information can be found at [this documentation](https://help.github.com/articles/fork-a-repo/).

## Step 3: Set up project-specific settings and dependencies

1. Install Google Cloud SDK. Follow the directions given [here](https://cloud.google.com/sdk/downloads).
   Note that you *do not* need to [initialize the SDK](https://cloud.google.com/sdk/docs/initializing).
   ```sh
   # This command is to be run at the Google Cloud SDK directory

   # Linux/OS X
   ./install.sh --path-update true
   # Windows
   install.bat --path-update true
   ```
   **Verification**: Run a `gcloud` command (e.g. `gcloud version`) in order to verify that you can access the SDK from the command line.

1. Run this command to install App Engine Java SDK bundled with the Cloud SDK:
   ```sh
   gcloud -q components install app-engine-java
   ```
   **Verification:** Run `gcloud version` and there should be an entry on `app-engine-java`.

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
   * `gradle.properties`<br>
      If you want to use a JDK other than the one specified in your PATH variable, add the value to the variable `org.gradle.java.home`.

## Step 4: (Optional but recommended) Set up an IDE

You are encouraged, but not required, to use an IDE to assist many development tasks.

We currently support two IDEs: Eclipse IDE and IntelliJ IDEA.
Support requests related to other IDEs will not be entertained.

Refer to [this document](ide-setup.md) if you wish to set up an IDE for developing TEAMMATES.

## Step 5: Start developing

If you followed every step correctly, you should have successfully set up the development environment.

Proceed to the development routine as outlined in [this document](development.md).
