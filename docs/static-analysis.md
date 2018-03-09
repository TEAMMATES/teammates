
# Static Analysis

TEAMMATES uses a number of static analysis tools in order to maintain code quality and measure code coverage.
This document will cover an overview of these tools and how to run them in local environment.

- [Version numbers](#version-numbers)
- [Tool stack](#tool-stack)
- [IntelliJ automatic setup](#intellij-automatic-setup)
- [Running static analysis](#running-static-analysis)
- [Running code coverage session](#running-code-coverage-session)

## Version numbers

The version number of all the tool stacks are declared in `build.gradle` or `package.json`.

When downloading the plugin for Eclipse/IntelliJ, find the plugin version that uses the correct version of the tool, e.g if CheckStyle 8.0 is used find an Eclipse/IntelliJ plugin that uses CheckStyle 8.0 as well.
If the exact version of the plugin cannot be found, using the latest version is allowed, however there is no guarantee that there will be no backward-incompatible changes.

Conversely, when updating any tool, ensure that the tool version is supported by the Eclipse/IntelliJ plugin, e.g when upgrading CheckStyle to 8.0 ensure that there is an Eclipse/IntelliJ plugin which supports that version as well.

## Tool stack

### CheckStyle

[CheckStyle](http://checkstyle.sourceforge.net/index.html) helps to enforce coding standard in Java source code.
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-checkstyle.xml).

##### Configuring Checkstyle Eclipse plugin

The plugin for Eclipse can be found [here](https://marketplace.eclipse.org/content/checkstyle-plug). **Install version 8.8.0**.

1. In `Project > Properties`, go to the `Checkstyle` tab.
2. In the `Local Check Configurations tab`, create a new Check Configuration. Select `Project Relative Configuration` for its Type, enter any Name you wish and set the Location to the `teammates-checkstyle.xml` file in the Project Folder. Click OK.
3. In the `Main` tab, uncheck `Use simple configuration`.
4. Add a new File Set. It should include only the `.java$` file. Enter any name you wish for the `File Set Name`, and select the Check Configuration that you created earlier for `Check Configuration`. Click OK.
5. Ensure that only the newly created File Set is enabled. Disable all other File Sets if they are enabled. Click OK.

##### Configuring Checkstyle in IntelliJ IDEA

The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/1065-checkstyle-idea).

> You can [configure all the static analysis tools automatically](#intellij-automatic-setup) or follow the manual instructions.

1. Go to `File → Settings → Other Settings → Checkstyle`.
1. Set `CheckStyle version` as specified in the build script (`build.gradle`).
1. Set `Scan Scope` to `Only Java sources (including tests)`.
1. Click the `+` to add a new configuration file. Click the `Browse` button, navigate to the `static-analysis` folder, and choose the `teammates-checkstyle.xml` file.
1. Fill in the `Description` field with the name of your project (e.g. teammates).
1. Click `Next`. Set the value of `basedir` to the path of your project folder.
1. Click `Finish`.
1. Check the box next to the newly added rule to activate it.

**NOTE**
> Once CheckStyle is set-up, the version used will be kept in sync with the build script. See [Versions sync with tools used in build script](intellij-automated-setup-behavior.md#versions-sync-with-tools-used-in-build-script).

##### Suppressing Checkstyle warnings

To introduce code that violates Checkstyle rules, wrap the violating code with `// CHECKSTYLE.OFF:RuleName` and re-enable it afterwards with `// CHECKSTYLE.ON:RuleName` (note the absence of space around `.` and `:`). Checkstyle also provides several other methods of suppressing rule violations, which can be found in the [documentation here](http://checkstyle.sourceforge.net/config_filters.html).
The suppression should be as specific as possible, and the reason for violating the rule should be explained.

An example for suppressing the `Avoid star imports` rule is as follows:
```java
// CHECKSTYLE.OFF:AvoidStarImports as there would be many (>100) import lines added if we were to import all of the ActionURIs
import static teammates.common.util.Const.ActionURIs.*;
// CHECKSTYLE.ON:AvoidStarImports
```

To suppress multiple violations at once, separate the rules with vertical bar `|`:
```java
// CHECKSTYLE.OFF:AbbreviationAsWordInName|MemberName the database uses ID
private String ID;
// CHECKSTYLE.ON:AbbreviationAsWordInName|MemberName
```

### PMD

[PMD](https://pmd.github.io) analyses the Java source code for common programming flaws (e.g unused variables, empty catch block).
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-pmd.xml).

##### Configuring PMD Eclipse plugin

The plugin for Eclipse can be found [here](https://marketplace.eclipse.org/content/eclipse-pmd).

1. In `Project > Properties`, go to the `PMD` tab.
1. Check `Enable PMD for this project`.
1. Click `Add`, select `Project` and click `Next >`.
1. Click `Browse` next to the `Location` bar, navigate to the `static-analysis` directory of the project and select `teammates-pmd-5.xml`.
1. Enter any name you wish for the ruleset. Click `OK`.
1. Repeat the last two steps for `teammates-pmdMain-5.xml`.

##### Configuring PMD for IntelliJ

The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/1137-pmdplugin).

> You can [configure all the static analysis tools automatically](#intellij-automatic-setup) or follow the manual instructions.

1. Go to `File → Settings → Other Settings → PMD`.
1. Click the `+` to add a new rule set. Browse for `teammates-pmd.xml`. Click OK.
1. In the `Options` tab, set `Target JDK` to 1.7.
1. Click `OK`.

##### Suppressing PMD warnings

To introduce code that violates PMD rules, use `@SuppressWarnings("PMD.RuleName")` annotation at the narrowest possible scope. PMD also provides several other methods of suppressing rule violations, which can be found in the [documentation here](https://pmd.github.io/pmd-6.1.0/pmd_userdocs_suppressing_warnings.html).
The suppression should be as specific as possible, and the reason for violating the rule should be explained.

### FindBugs

[FindBugs](http://findbugs.sourceforge.net) analyses Java source code for potential bugs at bytecode level, thus able to find potential bugs that PMD cannot find.
In Gradle build, the rules are configured by specifying the classes in the `visitors` variable.
The plugin for Eclipse can be found [here](http://findbugs.cs.umd.edu/eclipse/).
The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/3847-findbugs-idea).

> You can also [configure all the static analysis tools automatically](#intellij-automatic-setup) for IntelliJ IDEA.

### Macker

[Macker](https://github.com/andrena/macker) checks the architectural integrity of Java source code.
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-macker.xml).

### EclEmma/JaCoCo

[EclEmma/JaCoCo](http://eclemma.org/jacoco/) measures code coverage for Java test run.
Normally, the coverage will be run against all classes specified as the source code, but it can be configured to exclude classes matching certain name patterns.
The plugin for Eclipse can be found [here](http://eclemma.org).

### NodeJS

NodeJS integration is supported in IntelliJ. You can use it to manage your dependencies (**optional**).
The plugin can be found [here](https://plugins.jetbrains.com/idea/plugin/6098-nodejs).

### ESLint

[ESLint](http://eslint.org) functions both to enforce coding standard and also to find potential bugs in JavaScript source code.
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-eslint.yml).
ESLint integration is currently not supported for Eclipse.

**NOTE**
> You can [configure all the static analysis tools automatically](#intellij-automatic-setup) for IntelliJ IDEA or follow the manual instructions.

#### Configuring ESLint for IntelliJ

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

##### Suppressing ESLint warnings

To introduce code that violates ESLint rules, wrap the violating code with `/* eslint-disable rule-name */` and re-enable it afterwards
with `/* eslint-enable rule-name */`. The suppression should be as specific as possible, and the reason for violating the rule should be explained.

An example to suppress the `camelcase` rule is as follows:
```javascript
/* eslint-disable camelcase */ // The variable name is provided by an external library, which does not follow camelcase.
// violating codes go here
/* eslint-enable camelcase */
```

### Stylelint

[Stylelint](http://stylelint.io) functions both to enforce coding standard and also to find potential bugs and sub-optimal practices in stylesheets (CSS, SCSS).
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-stylelint.yml).
Stylelint integration is currently not supported for Eclipse.

> You can [configure all the static analysis tools automatically](#intellij-automatic-setup) for IntelliJ IDEA or follow the manual instructions.

#### Configuring Stylelint for IntelliJ

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

### blanket.js

[blanket.js](http://blanketjs.org) measures code coverage for JavaScript test run.
It is immediately enabled for all scripts with the `data-cover` attribute (configured via HTML) in a QUnit test run.

## IntelliJ automatic setup
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
>The behavior of the automated setup is described [here](intellij-automated-setup-behavior.md#static-analysis-tools-setup-behavior).

## Running static analysis

> Note the following:
- Change `./gradlew` to `gradlew.bat` in Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.

### Travis CI

Travis CI will run static analysis _before_ testing.
If violations are found, the build will be terminated with an error before any testing is done in order to save time and resources.

### CLI

You can run the static analysis tools via Gradle or NPM. The violations caught, if any, will be printed to the console itself.
```
./gradlew {toolType}{sourceCodeType}
```
where `{toolType}` = checkstyle, pmd, findbugs (lowercase), and `{sourceCodeType}` = Main, Test (Pascal Case).

To run Macker analysis on all Java source files, run the following command:
```
./gradlew macker
```

To run ESLint and Stylelint analysis on all JavaScript, JSON, and CSS source files, run the following command:
```
npm run lint
```

To run all static analysis tasks in one sitting, run the following two commands:
```
./gradlew lint --continue
npm run lint
```

### Eclipse

Eclipse allows CheckStyle, PMD, and FindBugs analysis on the spot;
just right-click on the source class or folder and select the appropriate commands.
Remember to configure the tools to use the ruleset provided.
The analysis results are immediately reported in Eclipse and you can traverse to the violating lines with just a click.

To run Checkstyle analysis on all Java source files with the Eclipse Checkstyle plugin,
right click on the Project Folder in the `Project Explorer` window in Eclipse and select `Checkstyle > Check Code with Checkstyle`.
The report can be found in the `Markers` window in Eclipse.

PMD analysis is automated when Eclipse PMD plugin is installed successfully. All discovered problems can be viewed under the `Problems View` or the `Markers View`.

### IntelliJ IDEA

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

## Running code coverage session

### Travis CI

For Java tests, if your build and run is successful, [Codecov](https://codecov.io) will pull the test coverage data and generate a report on their server.
The link to the report will be displayed in each PR, or by clicking the badge on the repository homepage.

For JavaScript unit tests, coverage is done concurrently with the tests themselves.
A coverage lower bound is enforced via `AllJsTests.java`, lower than which the build will fail.

### CLI

You can use Gradle to run tests and obtain the coverage data with `jacocoReport` task, i.e:
```sh
./gradlew appengineRun ciTests
./gradlew jacocoReport appengineStop
```
The report can be found in the `build/reports/jacoco/jacocoReport/` directory.

For JavaScript unit tests, coverage is done concurrently with the tests themselves.
A coverage lower bound is enforced via `AllJsTests.java`, lower than which the build will fail.

### Eclipse

For Java tests, choose `Coverage as TestNG Test` instead of the usual `Run as TestNG Test` to run the specified test or test suite.
The coverage will be reported in Eclipse after the test run is over.

For JavaScript unit tests, simply open `allJsUnitTests.html` and tick `Enable coverage`, or run `AllJsTests.java`.
The coverage will be reported immediately in the test page.

### IntelliJ IDEA

For Java tests, you can measure code coverage for the project using `Run → Run... → CI Tests → ▶ → Cover `.

**NOTE**
> There are some packages and classes that are excluded when using Jacoco on the comamnd line which are not excluded
when you run them in IntelliJ, thus the coverage statistics you see in IntelliJ may not match what you see on `Codecov`. 

Alternatively, you can right click a class with TestNG test(s) and click `Run '$FileClass$' with Coverage`, this will use
IntelliJ IDEA's code coverage runner. You can further configure your code coverage settings by referring to
[IntelliJ IDEA's documentation](https://www.jetbrains.com/help/idea/2017.1/code-coverage.html).

For JavaScript unit tests, simply open `allJsUnitTests.html` and tick `Enable coverage`, or run `AllJsTests.java`.
The coverage will be reported immediately in the test page.
