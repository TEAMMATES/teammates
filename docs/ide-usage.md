# Using an IDE

- [Eclipse IDE](#eclipse-ide)
    * [Prerequisites](#prerequisites)
    * [Project Setup](#project-setup)
    * [Updating Libraries](#updating-libraries-in-eclipse)
    * [Managing the dev server](#managing-the-dev-server-with-eclipse)
    * [Static Analysis](#static-analysis)
    * [Developer Troubleshooting Guide](#developer-troubleshooting-guide)
- [IntelliJ IDEA](#intellij-idea)
    * [Prerequisites](#prerequisites)
    * [Project Setup](#project-setup)
    * [Updating Libraries](#updating-libraries-in-intellij)
    * [Managing the dev server](#managing-the-dev-server-with-intellij)
    * [Static Analysis](#static-analysis)

## Eclipse IDE

> - Replace all references of `Eclipse → Preferences → ...` to `Window → Preferences → ...` if you are using Windows or Linux.
> - If you worry that these settings will interfere with your other projects, you can use a separate Eclipse instance for TEAMMATES.

Supported Eclipse versions: [Eclipse IDE for Java EE Developers version Neon or Oxygen](http://www.eclipse.org/downloads/).

### Prerequisites

1. You need the following plugins:
   * [Buildship Gradle Integration](https://marketplace.eclipse.org/content/buildship-gradle-integration)
   * [Google Cloud Tools for Eclipse](http://marketplace.eclipse.org/content/google-cloud-tools-eclipse)
   * [TestNG for Eclipse](https://marketplace.eclipse.org/content/testng-eclipse)

   ![eclipsesetupguide-1.png](images/eclipsesetupguide-1.png)

   ![eclipsesetupguide-2.png](images/eclipsesetupguide-2.png)

1. In Eclipse, do the following works before importing the project:
   * Google Cloud Tools: Go to `Eclipse → Preferences → Google Cloud Tools` and fill the `SDK location` box with the directory of your installed Google Cloud SDK.

     ![eclipsesetupguide-3.png](images/eclipsesetupguide-3.png)

   * JRE: Go to `Eclipse → Preferences → Java → Installed JRE` and ensure a **JDK 1.8** (not JRE) entry exists.

     ![eclipsesetupguide-4.png](images/eclipsesetupguide-4.png)

     Note that the JDK to be used is not required to be the `default`.

### Project Setup

1. Run this command to get necessary configuration files for Eclipse:

   ```sh
   ./gradlew setupEclipse
   ```

   **Verification:** The folder `.launches` should be added to the project root directory.

1. Import the project to your Eclipse instance.
   * Go to `File → Import...`.
   * Select `Existing Gradle Project` under `Gradle`. Click `Next >`.
   * Set the `Project root directory` to the location where the repo is cloned. Click `Next >`.
   * If necessary, tick `Override workspace settings` and choose `Gradle wrapper`. Click `Finish`.

   After importing the project, you may see many errors/warnings on your marker tab.
   You need not be alarmed as these will be resolved in the next step.

1. Configure the following project-specific settings (all can be found in `Project → Properties → ...`, except for the HTML, CSS and XML settings which can be found in `Eclipse → Preferences → ...`):
   * Text encoding: `Resources` → change the `Text file encoding` setting from `Default` to `Other: UTF-8`.

     ![eclipsesetupguide-5.png](images/eclipsesetupguide-5.png)

   * JDK: `Java Build Path → Libraries` → ensure that the system library used is JDK 8.

     ![eclipsesetupguide-6.png](images/eclipsesetupguide-6.png)

   * Compiler compliance: `Java Compiler` → tick `Use compliance from execution environment 'JavaSE-1.8' on the 'Java Build Path'`.

     ![eclipsesetupguide-7.png](images/eclipsesetupguide-7.png)

   * Indentation: In TEAMMATES, we use 4 spaces in place of tabs for indentations.
     Configure for all the languages used in TEAMMATES:
     * Java: `Java → Code Style → Formatter → Edit → Tab policy → Spaces only`.
     * JavaScript: `JavaScript → Code Style → Formatter → Edit → Tab policy → Spaces only`.
     
     You can find the Web and XML options in `Eclipse → Preferences → ...`.
     * HTML: `Web → HTML Files → Editor → Indent using spaces`.
     * CSS: `Web → CSS Files → Editor → Indent using spaces`.
     * XML: `XML → XML Files → Editor → Indent using spaces`.
   * Validation: Go to `Validation → ...`
     * Disable validation for HTML, JSP, and XML: Uncheck the `Build` option for `HTML Syntax Validator`, `JSP Content Validator`, `JSP Syntax Validator`, `XML Schema Validator`, and `XML Validator`.
     * Disable validation for JavaScript and JSON files in `node_modules` folder: Click the `...` settings button for `JavaScript Validation` → if `Exclude Group` is not already in the list then click `Add Exclude Group...` → `Exclude Group` → `Add Rule...` → `Folder or file name` → `Next` → `Browse Folder...` → navigate to the `node_modules` folder and confirm → `Finish`. Similarly for `JSON Validator`.

1. `Clean` the project for all changes to take effect. Ensure that there are no errors. Warnings are generally fine and can be ignored.

   ![eclipsesetupguide-8.png](images/eclipsesetupguide-8.png)

   > If you are using Eclipse Neon, you will find that all declarations of `import` and `export` in JavaScript files are marked as errors. This is fine and is not a cause of concern.

1. To set up some static analysis tools, refer to [this document](static-analysis.md).

1. To move on to the development phase, refer to [this document](development.md)

> Note: It is not encouraged to run Gradle tasks via Eclipse.

### Updating Libraries in Eclipse

To update a library's version, simply change the version number declared in `build.gradle` file. Right click on the project in the Project Explorer and select `Gradle → Refresh Gradle Project` for the changes to be reflected.

### Managing the dev server with Eclipse

> `Dev server` is the server run in your local machine.

#### Starting the dev server

Right-click on the project folder and choose `Run As → App Engine`.<br>
After some time, you should see this message (or similar) on the Eclipse console: `Dev App Server is now running`.
The dev server URL will be given at the console output, e.g `http://localhost:8080`.

#### Stopping the dev server

Click the "Terminate" icon on the Eclipse console.

### Static Analysis

##### Configuring Checkstyle Eclipse plugin

The plugin for Eclipse can be found [here](http://eclipse-cs.sourceforge.net/#!/).

1. In `Project > Properties`, go to the `Checkstyle` tab.
2. In the `Local Check Configurations tab`, create a new Check Configuration. Select `Project Relative Configuration` for its Type, enter any Name you wish and set the Location to the `teammates-checkstyle.xml` file in the Project Folder. Click OK.
3. In the `Main` tab, uncheck `Use simple configuration`.
4. Add a new File Set. It should include only the `.java$` file. Enter any name you wish for the `File Set Name`, and select the Check Configuration that you created earlier for `Check Configuration`. Click OK.
5. Ensure that only the newly created File Set is enabled. Disable all other File Sets if they are enabled. Click OK.

##### Configuring PMD Eclipse plugin

The plugin for Eclipse can be found [here](http://www.acanda.ch/eclipse-pmd/release/latest).

1. In `Project > Properties`, go to the `PMD` tab.
1. Check `Enable PMD for this project`.
1. Click `Add`, select `Project` and click `Next >`.
1. Click `Browse` next to the `Location` bar, navigate to the `static-analysis` directory of the project and select `teammates-pmd.xml`.
1. Enter any name you wish for the ruleset. Click `OK`.
1. Repeat the last two steps for `teammates-pmdMain.xml`.

#### Running static analysis in Eclipse

Eclipse allows CheckStyle, PMD, and FindBugs analysis on the spot;
just right-click on the source class or folder and select the appropriate commands.
Remember to configure the tools to use the ruleset provided.
The analysis results are immediately reported in Eclipse and you can traverse to the violating lines with just a click.

To run Checkstyle analysis on all Java source files with the Eclipse Checkstyle plugin,
right click on the Project Folder in the `Project Explorer` window in Eclipse and select `Checkstyle > Check Code with Checkstyle`.
The report can be found in the `Markers` window in Eclipse.

To run PMD analysis using the Eclipse PMD plugin, right click on the project under `Project Explorer` and select `PMD > Check Code`.
The report can be viewed in the PMD Perspective view under `Violations Overview`.

#### Running code coverage session in Eclipse

For Java tests, choose `Coverage as TestNG Test` instead of the usual `Run as TestNG Test` to run the specified test or test suite.
The coverage will be reported in Eclipse after the test run is over.

For JavaScript unit tests, simply open `allJsUnitTests.html` and tick `Enable coverage`, or run `AllJsTests.java`.
The coverage will be reported immediately in the test page.

### Developer Troubleshooting Guide

#### Troubleshooting project setup

##### Common setup errors and solutions

* **ERROR**: Eclipse complains "...your project must be configured to use a JDK in order to use JSP".

  **REASON**: This happens because Eclipse is only aware of JRE, not JDK (Compiling JSP requires the JDK).

  Go to `Window → Preferences → Java → Installed JREs`. You will note that a JRE path is the one selected, not a JDK path.

  **SOLUTION**: To fix this, Click `Add → Standard VM`, then for the JRE Path enter the path of the JRE folder inside your JDK installation folder, e.g. `C:/jdk1.8/jre`. Now you should see all of the JARs added to the library section.

* **ERROR**: When trying to deploy, Eclipse complains "... Cannot get the System Java Compiler. Please use a JDK, not a JRE.".

  **SOLUTION**: You can force Eclipse to use the JDK (instead of JRE) by modifying the `eclipse.ini` file. See [here](http://stackoverflow.com/questions/13913019/changing-jdk-in-eclipse) for more details.

* **ERROR (on Windows)**: Dev server launched by Eclipse keeps running even after closing Eclipse. After restarting Eclipse, you will be able to relaunch dev server on the same port but requests will be received by the previous server instance.

  **REASON**: If Eclipse crashes while dev server is running inside Eclipse, the server might keep running even after Eclipse is closed.

  **SOLUTION**: Go to Windows Task Manager and kill processes named `javaw.exe`.
  
* **ERROR**: Eclipse complains "file out of sync".

  **SOLUTION**: "Refresh" the project in Eclipse.

* **ERROR**: Eclipse complains "There are no JREs installed in the workplace that are strictly compatible with this environment.".

  **REASON**: Eclipse may be using an incompatible version of the JRE Library (with respect to TEAMMATES) for the current JRE definition. System Library for JRE should be set to the workspace default, after an appropriate JRE definition has been added (covered in existing point#2 of the troubleshooting section).

  **SOLUTION**: Right-click on project → Properties → Java Build Path → "Libraries" tab → Select JRE System Library from the list → Edit… → Select and mark radio button for "Workspace default JRE".

* **ERROR**: Eclipse complains "NewClass cannot be resolved to a type", "The import some.package.NewClass cannot be resolved", or "The method someMethod() from the type ExistingClass refers to the missing type NewClass" after syncing with `master` branch.

  **SOLUTION**: This is likely because the dependencies have changed. Refer to [this document](dependencies.md) for steps to update your local dependencies configuration.
* **ERROR**: Some characters are displayed incorrectly in the browser. For example, `Charlés's` is displayed as `CharlÃ©s`.

  **REASON**: Page encoding is not set to UTF-8.

  **SOLUTION**: In Eclipse, go to `Window` → `Preferences` → `Resources` → change the `Text file encoding` setting from `Default` to `Other: UTF-8`. If this does not fix the error, you can try the methods in [this link](https://z0ltan.wordpress.com/2011/12/25/changing-the-encoding-in-eclipse-to-utf-8-howto/).

#### Troubleshooting test failures

##### Optimizing IDE layout for testing

The default place for the TestNG tab is alongside the Console tab.

![troubleshooting-test-1.png](images/troubleshooting-test-1.png)

Here is a better place for it. Just drag the tab and drop it alongside the Project Explorer tab.

![troubleshooting-test-2.png](images/troubleshooting-test-2.png)

##### Common test errors and solutions

* **ERROR**: Tests fail due to accented characters.

  **SOLUTION**: Ensure that the text file encoding for your Eclipse workspace has been set to `UTF-8` as specified under [Setting up guide](setting-up.md).

## IntelliJ IDEA

> - Replace all references of `IntelliJ IDEA → Preferences` to `File → Settings` if you are using Windows or Linux.

Supported IntelliJ versions: IntelliJ IDEA Ultimate Edition (required to work with Google App Engine).
You can sign up for the free [JetBrains student license](https://www.jetbrains.com/student/) if you are a student registered in an educational institution.

### Prerequisites

1. You need a Java 8 SDK with the name `1.8` defined in IntelliJ IDEA as follows:

   * Click `Configure → Project Defaults → Project Structure` (or `File → Project Structure` if a project is currently open).
     Select SDKs in Platform Settings and check if there is an SDK named `1.8` with a JDK home path pointing to a JDK 8 path.
     Otherwise add a new SDK using JDK 8 with a name of `1.8`.
     ![intellijsetupguide-1.png](images/intellijsetupguide-1.png)

1. You need the [Google Cloud Tools](https://cloud.google.com/tools/intellij/docs/quickstart-IDEA#install) plugin installed and configured:

   ![intellijsetupguide-2.png](images/intellijsetupguide-2.png)
   * During installation, you may encounter two prompts: one to install the `Google Account` plugin dependency and
     another to disable the obsolete `Google App Engine Integration` plugin. Answer `Yes` to both prompts.
   * After installation, restart IntelliJ IDEA and configure the plugin.
     Click `Configure → Settings/Preferences` (or `IntelliJ IDEA → Preferences` if a project is currently open),
     go to `Other Settings → Google → Cloud SDK`, and select your Google Cloud SDK directory.
     ![intellijsetupguide-3.png](images/intellijsetupguide-3.png)

### Project Setup

1. Import the project as a Gradle project as follows:
   1. Click `Import Project` (or `File → New → Project from Existing Sources...` if a project is currently open).
   1. Select the local repository folder and click `Open`.
   1. Select `Import project from external model` and then `Gradle`.
   1. Click `Next`.
   1. Check `Use auto-import` and uncheck `Create separate module per source set`.
   1. Ensure `Create directories for empty content root automatically` is unchecked.
   1. Ensure `Use default gradle wrapper` is selected.
   1. Ensure for `Gradle JVM:` that a JDK 8 with a name of `1.8` is selected.
   1. Click `Finish`. Wait for the indexing process to complete.
   1. You should see a dialog box with the message:\
      `Framework detected: Google App Engine Standard framework is detected.`.\
      **OR**\
      `Frameworks detected: Web, Google App Engine Standard frameworks are detected`.\
      Click `Configure` and ensure that only the `Google App Engine Standard` framework is shown, then click `OK`.
      If there are other frameworks shown, click `Cancel` and wait until indexing completes, then try again.
      > If you closed or missed the dialog box, go to `View → Tool Windows → Event Log`.
        You should see the same message as the dialog box, click `Configure` and then `OK`.

1. Configure the project settings as follows:

   #### Indentation
   In TEAMMATES, we have standards defined for indentation.
   See [Coding standards in Supplementary documents](README.md#supplementary-documents).
   1. Open `IntelliJ IDEA → Preferences`.
   1. Go to `Editor → Code Style` and ensure that `Use tab character` is unchecked for `Java`, `JavaScript`, `JSON`, `CSS` and `XML`.
   1. Ensure that `Tab size:`, `Indent:` and `Continuation indent:` are `4`, `4` and `8` respectively for the different languages.
   1. Ensure `HTML` has `Use tab character` unchecked and set `Tab Size:`, `Indent:` and `Continuation indent:` to `2`, `2` and `4` respectively.
   1. Ensure `JSP` has `Use tab character` unchecked and set `Tab Size:`, `Indent:` and `Continuation indent:` to `2`, `2` and `4` respectively.

   #### Text Encoding
   Go to `Editor → File Encodings` and ensure that `Project Encoding` and `Default Encoding for properties files` is set to `UTF-8`.

   #### Javascript
   Go to `Languages & Frameworks → JavaScript` and select `ECMAScript 6` for the `JavaScript language version`.

1. Click `OK`.

1. Run this command to set up the run configurations for IntelliJ:

   ```sh
   ./gradlew setupIntellij
   ```

1. To set up some static analysis tools, refer to [this document](static-analysis.md).

1. To move on to the development phase, refer to [this document](development.md).

### Updating Libraries in IntelliJ
To update a library's version, simply change the version number declared in `build.gradle. dependencies are automatically refreshed as soon as changes to the file are detected (assuming auto-import is enabled).

### Managing the dev server with IntelliJ

> `Dev server` is the server run in your local machine.

#### Starting the dev server

Go to `Run → Run...` and select `Google App Engine Standard Local Server` in the pop-up box.

#### Stopping the dev server

Go to `Run → Stop 'Google App Engine Standard Local Server'`.

### Static Analysis

##### Configuring Checkstyle in IntelliJ IDEA

The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/1065-checkstyle-idea).

> You can [configure all the static analysis tools automatically](#intellij-automatic-setup) or follow the manual instructions.

1. Go to `File → Settings → Other Settings → Checkstyle`.
1. Set `Scan Scope` to `Only Java sources (including tests)`.
1. Click the `+` to add a new configuration file. Click the `Browse` button, navigate to the `static-analysis` folder, and choose the `teammates-checkstyle.xml` file.
1. Fill in the `Description` field with the name of your project (e.g. teammates).
1. Click `Next`. Set the value of `basedir` to the path of your project folder.
1. Click `Finish`.
1. Check the box next to the newly added rule to activate it.

##### Configuring PMD for IntelliJ

The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/1137-pmdplugin).

> You can [configure all the static analysis tools automatically](#intellij-automatic-setup) or follow the manual instructions.

1. Go to `File → Settings → Other Settings → PMD`.
1. Click the `+` to add a new rule set. Browse for `teammates-pmd.xml`. Click OK.
1. In the `Options` tab, set `Target JDK` to 1.7.

##### Configuring ESLint for IntelliJ

**NOTE**
> You can [configure all the static analysis tools automatically](#intellij-automatic-setup) for IntelliJ IDEA or follow the manual instructions.

1. If you have not installed Node.js and ESLint, please refer to
[install necessary tools and languages](setting-up.md#step-1-install-necessary-tools-and-languages)
and [set up project specific settings and dependencies](setting-up.md#step-3-set-up-project-specific-settings-and-dependencies).
1. Open `File → Settings` or `IntelliJ IDEA → Preferences`.
1. Go to `Languages & Frameworks → JavaScript → Code Quality Tools → ESLint`.
1. Check the box next to `Enable`.
1. The `Node interpreter` and `Stylelint package` should have been auto-filled to your locally installed NodeJS and
   `$PROJECT_DIR$/node_modules/stylelint` respectively. Point them to the right locations if they are not.
1. Point `Configuration file:` to the location of `teammates-eslint.yml`.
1. Under 'Extra eslint options:', add `--ignore-pattern 'src/main/webapp/js/*.js' --ignore-pattern 'src/main/webapp/test/*.js' --ignore-pattern 'test-output/**/*.js'`.
1. Click `OK`.

##### Configuring Stylelint for IntelliJ

1. If you have not installed Node.js and ESLint, please refer to
[install necessary tools and languages](setting-up.md#step-1-install-necessary-tools-and-languages)
and [set up project specific settings and dependencies](setting-up.md#step-3-set-up-project-specific-settings-and-dependencies).
1. Open `File → Settings` or `IntelliJ IDEA → Preferences`.
1. Go to `Languages & Frameworks → Stylesheets → Stylelint`.
1. Check the box next to `Enable`.
1. The `Node interpreter` and `Stylelint package` should have been auto-filled to your locally installed NodeJS and
   `$PROJECT_DIR$/node_modules/stylelint` respectively. Point them to the right locations if they are not.
1. Click `OK`.
1. Copy `$PROJECT_DIR$/static-analysis/teammates-stylelint.yml` to `$PROJECT_DIR$/.stylelintrc.yml`.

#### IntelliJ automatic setup
1. Ensure the following plugins are installed. [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea),
[PMDPlugin](https://plugins.jetbrains.com/plugin/1137-pmdplugin),
[FindBugs-IDEA](https://plugins.jetbrains.com/plugin/3847-findbugs-idea),
[NodeJS](https://plugins.jetbrains.com/plugin/6098-nodejs) (Optional)

1. Run the command to setup the settings for the various plugins:
   ```sh
   ./gradlew setupIntellijStaticAnalysis
   ```

1. Restart IntelliJ IDEA.

**NOTE**
>The behavior of the automated setup is described [here](#intellij-idea-static-analysis-tools-setup-behavior).

#### Running static analysis in IntelliJ

`CheckStyle`, `ESLint` and `Stylelint` are configured as inspection tools in IntelliJ IDEA.\
This means they automatically run on every open file in the editor. Any code issues will show up on the right of the
editor and you can also see the analysis status of the whole file from by hovering over the icon on the top-right of the
editor. You may also be able to see some code inspection status on the line itself (e.g. wriggly red lines).

`FindBugs` is also an inspection tool but it does not run automatically. You can run it by going to `Analyze → FindBugs`
and choosing the option you want. Alternatively, you can select a number of files and right click, select `FindBugs` and
the option you want.

If you wish to run inspections for the whole project, you can do `Analyze → Inspect Code... → Whole project`. You may
also wish to learn more about code inspections by referring to
[IntelliJ IDEA's documentation](https://www.jetbrains.com/help/idea/2017.1/code-inspection.html).

**NOTE**
> `FindBugs` will only appear in the inspection results if you ran it manually before running
> `Analyze → Inspect Code... → Whole project`.

`PMD` is provided as a plugin and does not run automatically. You can run it by selecting a number of files, right clicking,
selecting `Run PMD` and then choosing the option you want.

`Macker` is not available in IntelliJ IDEA and you have to run it on the command line.

#### Running code coverage session in Intellij

For Java tests, you can measure code coverage for the project using `Run → Run... → CI Tests → ▶ → Cover `.

**NOTE**
> There are some packages and classes that are excluded when using Jacoco on the comamnd line which are not excluded
when you run them in IntelliJ, thus the coverage statistics you see in IntelliJ may not match what you see on `Codecov`. 

Alternatively, you can right click a class with TestNG test(s) and click `Run '$FileClass$' with Coverage`, this will use
IntelliJ IDEA's code coverage runner. You can further configure your code coverage settings by referring to
[IntelliJ IDEA's documentation](https://www.jetbrains.com/help/idea/2017.1/code-coverage.html).

For JavaScript unit tests, simply open `allJsUnitTests.html` and tick `Enable coverage`, or run `AllJsTests.java`.
The coverage will be reported immediately in the test page.



### IntelliJ IDEA Static Analysis Tools Setup Behavior

When the following command is executed,
```sh
./gradlew setupIntellijStaticAnalysis
```
The project will be configured with using the pre-configured static analysis tools settings from `.templates/ideaPlugins`.

The syntax for the pre-configured settings found below are as follows:
* `${buildDir}` refers to the build directory specified in Gradle
* A `XPath` like syntax is used to refer to the XML nodes

#### Checkstyle

| Settings Info |                               |
|---------------|-------------------------------|
| File name     | `checkstyle-idea.xml`         |
| Settings      | `Other Settings → Checkstyle` |

**Parent Node:** `/project/component[@name='CheckStyle-IDEA']/option[@name='configuration']/map`

| IntelliJ Setting                      | Node                                 | Value               |
|---------------------------------------|--------------------------------------|---------------------|
| `Active` `Configuration File`         | `entry[@key='active-configuration']` | `PROJECT_RELATIVE:$PRJ_DIR$/static-analysis/teammates-checkstyle.xml:teammates` |
| `Checkstyle version:`                 | `entry[@key='checkstyle-version']`   | `8.0`               |
| `basedir` `Value`                     | `entry[@key='property-1.basedir']`   | `$PROJECT_DIR$`     |
| `Scan Scope:`                         | `entry[@key='scanscope']`            | `JavaOnlyWithTests` |
| `Treat Checkstyle errors as warnings` | `entry[@key='suppress-errors']`      | `false`             |
| `Use a local Checkstyle file`  `Store relative to project location`  | `entry[@key='location-1']` | `Git`  

#### Inspection Profile To Use
     
     | Settings Info |                                            |
     |---------------|--------------------------------------------|
     | File name     | `inspectionProfiles/profiles_settings.xml` |
     | Settings      | `Editor → Inspections`                     |
     
     **Parent Node:** `/component/settings/option`
     
     | IntelliJ Setting | Node                                                       | Value       |
     |------------------|------------------------------------------------------------|-------------|
     | `Profile:`       | `[@name='projectProfile']` and `[@name='PROJECT_PROFILE']` | `teammates` |
     
#### Inspection Profile

| Settings Info |                                    |
|---------------|------------------------------------|
| File name     | `inspectionProfiles/teammates.xml` |
| Settings      | Depends on setting below           |

**Parent Node:** `/component/profile`

| IntelliJ Setting | Node                     | Value       |
|------------------|--------------------------|-------------|
| Profile Name     | `option[@name='myName']` | `teammates` |

##### ESLint

**Parent Node:** `/component/profile`
**Settings:** `Languages & Frameworks → Javascript → Code Quality Tools → ESLint`

| IntelliJ Setting          | Node                                         | Value  |
|---------------------------|----------------------------------------------|--------|
| Whether ESLint is enabled | `inspection_tool[@class='Eslint'][@enabled]` | `true` |

##### Stylelint

**Parent Node:** `/component/profile`
**Settings:** `Languages & Frameworks → Stylesheets → Stylelint`

| IntelliJ Setting          | Node                                            | Value  |
|---------------------------|-------------------------------------------------|--------|
| Whether ESLint is enabled | `inspection_tool[@class='Stylelint'][@enabled]` | `true` |

#### ESLint Inspection Settings

| Settings Info |                                                                     |
|---------------|---------------------------------------------------------------------|
| File name     | `jsLinters/eslint.xml`                                              |
| Settings      | `Languages & Frameworks → Javascript → Code Quality Tools → ESLint` |

**Parent Node:** `/project/component[@name='EslintConfiguration']`

| IntelliJ Setting        | Node                                              | Value                                                |
|-------------------------|---------------------------------------------------|------------------------------------------------------|
| `Configuration file:`   | `/custom-configuration-file[@used='true'][@path]` | `$PROJECT_DIR$/static-analysis/teammates-eslint.yml` |
| `Extra eslint options:` | `/extra-options[@value]`                          | `--ignore-pattern 'src/main/webapp/js/*.js' --ignore-pattern 'src/main/webapp/test/*.js' --ignore-pattern 'test-output/**/*.js'` |

#### PMD

| Settings Info |                        |
|---------------|------------------------|
| File name     | `misc.xml`             |
| Settings      | `Other Settings → PMD` |

**Parent Node:** `/project/component[@name='PDMPlugin']`

| IntelliJ Setting       | Node                                                    | Value                                                 |
|------------------------|---------------------------------------------------------|-------------------------------------------------------|
| `RuleSets`             | `/option[@name='customRuleSets']/list/option[@value]`   | `$PROJECT_DIR$/static-analysis/teammates-pmd.xml`     |
| `RuleSets`             | `/option[@name='customRuleSets']/list/option[@value]`   | `$PROJECT_DIR$/static-analysis/teammates-pmdMain.xml` |
| `Target JDK` `Options` | `/option[@name='options']/map/entry[@key='Target JDK']` | `1.8`                                                 |
| `Skip Test Sources`    | `/option[@name='skipTestSources']`                      | `false`        
