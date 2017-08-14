# Using an IDE

- [Eclipse IDE](#eclipse-ide)
- [IntelliJ IDEA](#intellij-idea)

## Eclipse IDE

> - Replace all references of `Eclipse → Preferences → ...` to `Window → Preferences → ...` if you are using Windows or Linux.
> - If you worry that these settings will interfere with your other projects, you can use a separate Eclipse instance for TEAMMATES.

Supported Eclipse versions: [Eclipse IDE for Java EE Developers version Luna, Mars, or Neon](http://www.eclipse.org/downloads/).

The following plugins are needed:
* [Google Plugin for Eclipse](https://developers.google.com/eclipse/docs/download): the correct version for your Eclipse IDE.
* [TestNG Eclipse plugin](http://testng.org/doc/download.html): latest stable.

![setupguide-1.png](images/setupguide-1.png)

1. Run this command to get necessary configuration files for Eclipse:

   ```sh
   ./gradlew setupEclipse
   ```

   **Verification:** The files `.project` and `.classpath` should be added to the project root directory.

1. Start Eclipse and do the following works before importing the project:
   * Google App Engine: Go to `Eclipse → Preferences → Google → App Engine`, click the `Add` button, and point it to where Gradle keeps the downloaded SDK.<br>
     This directory can be found by running the command `./gradlew printUserHomeDir`.<br>
     Further instructions for installing can be found [here](https://developers.google.com/eclipse/docs/using_sdks).

     ![setupguide-2.png](images/setupguide-2.png)

   * JRE: Go to `Eclipse → Preferences → Java → Installed JRE` and ensure a **JDK 1.7** (not JRE, not JDK 1.8) entry exists.

     ![setupguide-3.png](images/setupguide-3.png)

   Note that none of the App Engine SDK or JDK to be used are required to be the `default`.

1. Import the project to your Eclipse instance.
   * Go to `File → Import...`.
   * Select `Existing Projects into Workspace` under `General`.
   * Set the `root directory` to the location where the repo is cloned.
   * Click `Finish`.

1. Configure the following project-specific settings (all can be found in `Project → Properties → ...`, except for the HTML, CSS and XML settings which can be found in `Eclipse → Preferences → ...`):
   * Text encoding: `Resources` → change the `Text file encoding` setting from `Default` to `Other: UTF-8`.

     ![setupguide-4.png](images/setupguide-4.png)

   * Google App Engine: set up the following by going to `Google → ...`:
     * Disable Datanucleus: `App Engine` → uncheck `Use Datanucleus JDO/JPA to access the datastore`.
     * Validation exclusion: `App Engine → Validation` → add two entries: `src/test/java` and `src/client/java`.
     * WAR directory: `Web Application` → tick both `This project has a WAR directory` and `Launch and deploy from this directory`, and enter `src/main/webapp` as `WAR directory`.
   * JDK: `Java Build Path → Libraries` → ensure that the system library used is JDK 7.

       ![setupguide-7.png](images/setupguide-7.png)

   * Compiler compliance: `Java Compiler` → tick `Use compliance from execution environment 'JavaSE-1.7' on the 'Java Build Path'`.

       ![setupguide-8.png](images/setupguide-8.png)

   * Indentation: In TEAMMATES, we use 4 spaces in place of tabs for indentations.
     Configure for all the languages used in TEAMMATES:
     * Java: `Java → Code Style → Formatter → Edit → Tab policy → Spaces only`.
     * JavaScript: `JavaScript → Code Style → Formatter → Edit → Tab policy → Spaces only`.
     
     You can find the Web and XML options in `Eclipse → Preferences → ...`.
     * HTML: `Web → HTML Files → Editor → Indent using spaces`.
     * CSS: `Web → CSS Files → Editor → Indent using spaces`.
     * XML: `XML → XML Files → Editor → Indent using spaces`.
   * Validation:
     * We do not validate HTML, JSP, and XML. `Validation` → uncheck the `Build` option for `HTML Syntax Validator`, `JSP Content Validator`, `JSP Syntax Validator`, and `XML Validator`.
     * Disable JavaScript validation for `node_modules` folder. `Validation` → click the `...` settings button for `JavaScript Validation` → if `Exclude Group` is not already in the list then click `Add Exclude Group...` → `Exclude Group` → `Add Rule...` → `Folder or file name` → `Next` → `Browse Folder...` → navigate to the `node_modules` folder and confirm → `Finish`.
	 * Disable JSON validation for `node_modules` folder. `Validation` → click the `...` settings button for `JSON Validator` → if `Exclude Group` is not already in the list then click `Add Exclude Group...` → `Exclude Group` → `Add Rule...` → `Folder or file name` → `Next` → `Browse Folder...` → navigate to the `node_modules` folder and confirm → `Finish`.

1. `Clean` the project for all changes to take effect. Ensure that there are no errors. Warnings are generally fine and can be ignored.

   ![setupguide-6.png](images/setupguide-6.png)

1. To set up some static analysis tools, refer to [this document](staticAnalysis.md).

## IntelliJ IDEA

Supported IntelliJ versions: IntelliJ IDEA Ultimate Edition (required to work with Google App Engine).
You can sign up for the free [JetBrains student license](https://www.jetbrains.com/student/) if you are a student registered in an educational institution.

**NOTE**
> If you are migrating from Eclipse, you should delete `.project`, `.classpath`, `.launches`,
> `src/main/webapp/WEB-INF/classes`, `src/main/webapp/WEB-INF/lib` and `src/main/webapp/WEB-INF/appengine-generated`.\
> You can also copy `local_db.bin` from `src/main/webapp/WEB-INF/appengine-generated` to the location your dev server in
> IntelliJ reads the datastore from after your IntelliJ setup. See [dev server setup](development.md#with-intellij) for
> more details.

### Prerequisites
1. You need a Java 7 SDK with the name `1.7` defined in IntelliJ IDEA as follows:

    1. If you have no current projects open, click `Configure → Project Defaults → Project Structure`.\
       **OR**\
       If you currently have projects open, click `File → Project Structure`.
    1. Select SDKs in Platform Settings and check if there is a SDK named `1.7` with a JDK home path pointing to a
       JDK 7 path. Otherwise add a new SDK using JDK 7 with a name of `1.7`.

1. You need the system property `ide=idea` for `Gradle VM options:` under `Global Gradle settings` as follows:

    1. If you have no current projects open, click `Configure → Settings/Preferences`.\
       **OR**\
       If you currently have projects open, click `File → Settings` or `IntelliJ IDEA → Preferences`.
    1. Go to `Build, Execution, Deployment → Build Tools → Gradle`. Under `Global Gradle settings`,
       add `-Dide=idea` to `Gradle VM options:`.

1. You need to have an application server named `AppEngine Dev <version>` pointing to the SDK you downloaded previously as follows: 
    
    1. If you have no current projects open, click `Configure → Settings/Preferences`.\
       **OR**\
       If you currently have projects open, click `File → Settings` or `IntelliJ IDEA → Preferences`.
    1. Go to `Build, Execution, Deployment → Application Servers`.
    1. Click `+ → Google App Engine Dev Server → ... `. Select the App Engine SDK (`appengine-java-sdk-<version>` sub-folder)
       you downloaded in Step 3 of the [Setting up a development environment](setting-up.md) guide.
    1. Ensure the `Name:` is of `AppEngine Dev <version>`.

### Automated Setup

If you do not wish to use the automated setup, you can follow the [manual setup](#manual-setup) below.

1. Run this command to create a pre-configured IntelliJ IDEA project:
   ```sh
   ./gradlew setupIntellijProject
   ```

1. You can now open the project in IntelliJ IDEA.

1. To set up some static analysis tools, refer to [this document](staticAnalysis.md).

**NOTE**
>The behavior of the automated setup is described [here](intellij-automated-setup-behavior.md#project-setup-behavior).

### Manual Setup

1. Import the project as a Gradle project as follows:
   1. If you have no current projects open, click `Import Project`.\
       **OR**\
       If you currently have projects open, click `New | Project from Existing Sources...`.
   1. Select the local repository folder and click `Open`.
   1. Select `Import project from external model` and then `Gradle`.
   1. Click `Next`.
   1. Check `Use auto-import` and uncheck `Create separate module per source set`.
   1. Ensure `Create directories for empty content root automatically` is unchecked.
   1. Ensure `Use default gradle wrapper` is selected.
   1. Ensure for `Gradle JVM:` that a JDK 7 with a name of `1.7` is selected.
   1. Click `Finish`.
   1. Create a `build` folder in your project root if it does not exist while waiting for IntelliJ to finish indexing.
   1. If you used a different project root directory other than `teammates`, you will receive a dialog box with the
      message `The modules below are not imported from Gradle anymore. Check those to be removed from the ide project too:`.\
      You should see a module with your project root directory name being checked. Click `OK`.
   1. Go to `File → Project Structure... → Modules`.
      Click on the `teammates` module, then under `Sources`, click on the `build` folder and click `Excluded` and then `OK`.
   1. You should see a dialog box with the message:\
      `Frameworks detected: Google App Engine, Web, JPA frameworks are detected in the project`.\
      **OR**\
      `Frameworks detected: OSGi, Google App Engine, Web, JPA frameworks are detected in the project`.\
      Click `Configure` and ensure that only `Google App Engine` and `JPA` frameworks are shown, otherwise make
      sure you have excluded the `build` folder in your `teammates` module. Then click `OK`.
	  > If you missed the dialog box, go to `View → Tool Windows → Event Log`.
        You should see the same message as the dialog box, click `Configure` and then `OK`.

1. Configure the project settings as follows:

   #### Indentation
   In TEAMMATES, we have standards defined for indentation.
   See [Coding standards in Supplementary documents](README.md#supplementary-documents).
   1. Open `File → Settings` or `IntelliJ IDEA → Preferences`.
   1. Go to `Editor → Code Style` and ensure that `Use tab character` is unchecked for `Java`, `JavaScript`, `JSON`, `CSS` and `XML`.
   1. Ensure that `Tab size:`, `Indent:` and `Continuation indent:` are `4`, `4` and `8` respectively for the different languages.
   1. Ensure `HTML` has `Use tab character` unchecked and set `Tab Size:`, `Indent:` and `Continuation indent:` to `2`, `2` and `4` respectively.
   1. Ensure `JSP` has `Use tab character` unchecked and set `Tab Size:`, `Indent:` and `Continuation indent:` to `2`, `2` and `4` respectively.

   #### Text Encoding
   Go to `Editor → File Encodings` and ensure that  `Project Encoding` and
      `Default Encoding for properties files` is set to `UTF-8`.

   #### Javascript
   Go to `Languages & Frameworks → JavaScript` and select `ECMAScript 6` for the `JavaScript language version`.

1. Click `OK`.

1. Run this command to set up the run configurations for IntelliJ:

   ```sh
   ./gradlew setupIntellijRunConfigs
   ```

1. To set up some static analysis tools, refer to [this document](staticAnalysis.md).
