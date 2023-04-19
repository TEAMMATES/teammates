<frontmatter>
  title: "Setting Up"
</frontmatter>

# Setting up a development environment

This is a step-by-step guide for setting up a development environment on your local machine.
Using this environment, you can contribute to the project by working on features, enhancements, bug fixes, etc.

All the instructions in this document work for Linux, OS X, and Windows, with the following pointers:
- Replace `./gradlew` to `gradlew.bat` if you are using Windows.
- All the commands are assumed to be run from the root project folder, unless specified otherwise.
- When a version is specified for any tool, install that version instead of the latest version available.

> If you encounter any problems during the setup process, please refer to our [troubleshooting guide](troubleshooting-guide.md) before posting a help request in our [issue tracker](https://github.com/TEAMMATES/teammates/issues).

## Step 1: Obtain your own copy of the repository

1. Install Git.
   * (Optional but recommended) Install Sourcetree or other similar Git client.

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

## Step 2: Install necessary tools and languages

These tools are necessary regardless of whether you are developing front-end or back-end:

1. Install Java JDK 11.
   * Alternatively, it is possible to use JDK 17, as long as newer language features are not used.

If you want to develop front-end, you need to install the following:

1. Install Node.js (minimum version 16).
1. (Optional but highly recommended) Install Angular CLI version 14 globally.
   ```sh
   npm install -g @angular/cli@14
   ```
   **Verification:** Run `ng` and you should see a list of available Angular CLI commands.

## Step 3: Set up project-specific settings and dependencies

1. Run this command to create the main config files (these are not under revision control because their contents vary from developer to developer):
   ```sh
   ./gradlew createConfigs
   ```
   **Verification:** The file named `gradle.properties` should be added to the project root directory.

1. Modify the following config file:
   * `gradle.properties`
     * If you want to use a JDK other than the one specified in your PATH variable, add the value to the variable `org.gradle.java.home`.
     * If you want to use a specific Google Cloud SDK installation, add the value to the variable `cloud.sdk.home`. A minimum version of `274.0.0` is required.

1. Run this command to download the necessary tools for front-end development (if you are going to be involved):
   ```sh
   npm ci
   ```
   **Verification:** A folder named `node_modules` should be added to the project root directory.

1. If you plan on making documentation changes to the developer guide, you can **[install and set up MarkBind](documentation.md)** in order to preview your changes.

**Q:** Can I set up the project in IDEs, e.g. Eclipse, IntelliJ?<br>
**A:** You are welcome to; the core team have been using IntelliJ to a varying degree of success, and it is expected that any IDE that support Gradle-based Java project will work. However, IDE-based development (even with IntelliJ) is not actively supported/maintained by the team.

## Step 4: Start developing

If you have followed every step correctly, your development environment should be set up successfully.

Proceed to the development routine as outlined in [this document](development.md).
