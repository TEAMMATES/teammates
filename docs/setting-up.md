# Setting up a development environment

This is a step-by-step guide for setting up a development environment on your local machine.
Using this environment, you can contribute to the project by working on features, enhancements, bug fixes, etc.

All the instructions in this document work for Linux, OS X, and Windows, with the following pointers:
- Replace `./gradlew` to `gradlew.bat` if you are using Windows.
- All the commands are assumed to be run from the root project folder, unless specified otherwise.
- When a version is specified for any tool, install that version instead of the latest version available.

> If you encounter any problems during the setup process, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request in our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

## Step 1: Install necessary tools and languages

1. Install Git.
   1. (Optional but recommended) Install Sourcetree or other similar Git client.
1. Install JDK 1.8.
1. Install Python 2.7.
1. Install Node.js (minimum version 6.11.5).

## Step 2: Obtain your own copy of the repository

1. Fork our repo at https://github.com/TEAMMATES/teammates. Clone the fork to your hard disk.

1. Add a remote name (e.g `upstream`) for your copy of the main repo. Fetch the remote-tracking branches from the main repo to keep it in sync with your copy.
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
   # Run the following command at the Google Cloud SDK directory

   # Linux/OS X
   ./install.sh --path-update true

   # Windows
   install.bat --path-update true
   ```
   If you are installing in Red Hat, CentOS, Fedora, Debian or Ubuntu, refer to the quick start of Google Cloud SDK for [Debian/Ubuntu](https://cloud.google.com/sdk/docs/quickstart-debian-ubuntu) or [Red Hat/CentOS/Fedora](https://cloud.google.com/sdk/docs/quickstart-redhat-centos) respectively.

   **Verification**: Run a `gcloud` command (e.g. `gcloud version`) in order to verify that you can access the SDK from the command line.

1. Run the following command to install App Engine Java SDK bundled with the Cloud SDK:
   ```sh
   # Linux/OS X/Windows
   gcloud -q components install app-engine-java
   
   # Red Hat/CentOS/Fedora
   sudo yum install google-cloud-sdk-app-engine-java
   
   # Debian/Ubuntu
   sudo apt-get install google-cloud-sdk-app-engine-java
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

## Step 4: Set up an IDE (Recommended)

You are encouraged, but not required, to use an IDE to assist development tasks.

We currently support two IDEs: Eclipse IDE and IntelliJ IDEA.
Support requests related to other IDEs will not be entertained.

Refer to [this document](ide-setup.md) if you wish to set up an IDE for developing TEAMMATES.

## Step 5: Start developing

If you have followed every step correctly, your development environment should be set up successfully.

Proceed to the development routine as outlined in [this document](development.md).
