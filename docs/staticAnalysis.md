
# Static Analysis

TEAMMATES uses a number of static analysis tools in order to maintain code quality and measure code coverage.
This document will cover an overview of these tools and how to run them in local environment.

- [Version numbers](#version-numbers)
- [Tool stack](#tool-stack)
- [Running static analysis](#running-static-analysis)
- [Running code coverage session](#running-code-coverage-session)

## Version numbers

The version number of all the tool stacks are declared in `build.gradle` or `package.json`.

When downloading the plugin for Eclipse, find the plugin version that uses the correct version of the tool, e.g if CheckStyle 6.19 is used find an Eclipse plugin that uses CheckStyle 6.19 as well.
If the exact version of the plugin cannot be found, using the latest version is allowed, however there is no guarantee that there will be no backward-incompatible changes.

Conversely, when updating any tool, ensure that the tool version is supported by the Eclipse plugin, e.g when upgrading CheckStyle to 6.19 ensure that there is an Eclipse plugin which supports that version as well.

## Tool stack

### CheckStyle

[CheckStyle](http://checkstyle.sourceforge.net/index.html) helps to enforce coding standard in Java source code.
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-checkstyle.xml).

##### Configuring Checkstyle Eclipse plugin

The plugin for Eclipse can be found [here](http://eclipse-cs.sourceforge.net/#!/).

1. In `Project > Properties`, go to the `Checkstyle` tab.
2. In the `Local Check Configurations tab`, create a new Check Configuration. Select `Project Relative Configuration` for its Type, enter any Name you wish and set the Location to the `teammates-checkstyle.xml` file in the Project Folder. Click OK.
3. In the `Main` tab, uncheck `Use simple configuration`.
4. Add a new File Set. It should include only the `.java$` file. Enter any name you wish for the `File Set Name`, and select the Check Configuration that you created earlier for `Check Configuration`. Click OK.
5. Ensure that only the newly created File Set is enabled. Disable all other File Sets if they are enabled. Click OK.

##### Configuring Checkstyle in IntelliJ IDEA

The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/1065-checkstyle-idea).

1. Go to `File → Settings → Other Settings → Checkstyle`.
1. Set `Scan Scope` to `Only Java sources (including tests)`.
1. Click the `+` to add a new configuration file. Click the `Browse` button, navigate to the `static-analysis` folder, and choose the `teammates-checkstyle.xml` file.
1. Fill in the `Description` field with the name of your project (e.g. teammates).
1. Click `Next`. Set the value of `basedir` to the path of your project folder.
1. Click `Finish`.
1. Check the box next to the newly added rule to activate it.

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

The plugin for Eclipse can be found [here](https://sourceforge.net/projects/pmd/files/pmd-eclipse/update-site/).

1. In `Project > Properties`, go to the `PMD` tab.
2. Check `Enable PMD`.
3. Under `Rule Source`, check `Use the ruleset configured in a project file`. Click `Browse`,
   navigate to the `static-analysis` directory of the project and select `teammates-pmd.xml`. Click OK.

##### Configuring PMD for IntelliJ

The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/1137-pmdplugin).

1. Go to `File → Settings → Other Settings → PMD`.
1. Click the `+` to add a new rule set. Browse for `teammates-pmd.xml`. Click OK.
1. In the `Options` tab, set `Target JDK` to 1.7.
1. Click `OK`.

##### Suppressing PMD warnings

To introduce code that violates PMD rules, use `@SuppressWarnings("PMD.RuleName")` annotation at the narrowest possible scope. PMD also provides several other methods of suppressing rule violations, which can be found in the [documentation here](http://pmd.sourceforge.net/snapshot/usage/suppressing.html).
The suppression should be as specific as possible, and the reason for violating the rule should be explained.

### FindBugs

[FindBugs](http://findbugs.sourceforge.net) analyses Java source code for potential bugs at bytecode level, thus able to find potential bugs that PMD cannot find.
In Gradle build, the rules are configured by specifying the classes in the `visitors` variable.
The plugin for Eclipse can be found [here](http://findbugs.cs.umd.edu/eclipse/).
The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/3847-findbugs-idea).

### Macker

[Macker](https://github.com/andrena/macker) checks the architectural integrity of Java source code.
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-macker.xml).

### EclEmma/JaCoCo

[EclEmma/JaCoCo](http://eclemma.org/jacoco/) measures code coverage for Java test run.
Normally, the coverage will be run against all classes specified as the source code, but it can be configured to exclude classes matching certain name patterns.
The plugin for Eclipse can be found [here](http://eclemma.org).

### ESLint

[ESLint](http://eslint.org) functions both to enforce coding standard and also to find potential bugs in JavaScript source code.
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-eslint.yml).
ESLint is a Node.js package, currently not supported for Eclipse Java EE project.

#### Installing ESLint from within IntelliJ

1. Ensure the [NodeJS Plugin](https://plugins.jetbrains.com/idea/plugin/6098-nodejs) is installed.
1. Refer to [this guide](https://www.jetbrains.com/help/idea/2016.3/using-javascript-code-quality-tools.html#ESLint) to install ESLint. Refer to `package.json` for the appropriate version to install.
1. Follow the same steps outlined in the guide above to install `eslint-plugin-json`.

#### Configuring ESLint for IntelliJ

1. Go to `File → Settings → Languages & Frameworks → JavaScript → Code Quality Tools → ESLint`.
1. Check the box next to `Enable`.
1. Point `Node Interpreter` to where you installed `node.exe` (NodeJS).
1. `ESLint Package` should already be filled in if you [installed ESLint from within IntelliJ](#installing-eslint-from-within-intellij).
1. Point `Configuration file` to the location of `teammates-eslint.yml`.
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
Stylelint is a Node.js package, currently not supported for Eclipse Java EE project or IntelliJ.

### blanket.js

[blanket.js](http://blanketjs.org) measures code coverage for JavaScript test run.
It is immediately enabled for all scripts with the `data-cover` attribute (configured via HTML) in a QUnit test run.

## Running static analysis

> Note the following:
- Change `./gradlew` to `gradlew.bat` in Windows.
- All the commands are assumed to be run from the root project folder, unless otherwise specified.

### Travis CI

Travis CI will run static analysis _before_ testing.
If violations are found, the build will be terminated with an error before any testing is done in order to save time and resources.

### Local build

Eclipse allows CheckStyle, PMD, and FindBugs analysis on the spot; just right-click on the source class or folder and select the appropriate commands.
Remember to configure the tools to use the ruleset provided.
The analysis results are immediately reported in Eclipse and you can traverse to the violating lines with just a click.

To run Checkstyle analysis on all Java source files with the Eclipse Checkstyle plugin, right click on the Project Folder in the `Project Explorer` window in Eclipse and select `Checkstyle > Check Code with Checkstyle`. The report can be found in the `Markers` window in Eclipse.

To run PMD analysis using the Eclipse PMD plugin, right click on the project under `Project Explorer` and select `PMD > Check Code`. The report can be viewed in the PMD Perspective view under `Violations Overview`.

Alternatively, run the tools via Gradle or NPM. The violations caught, if any, will be printed to the console itself.
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

## Running code coverage session

### Travis CI

For Java tests, if your build and run is successful, [Codecov](https://codecov.io) will pull the test coverage data and generate a report on their server.
The link to the report will be displayed in each PR, or by clicking the badge on the repository homepage.

For JavaScript unit tests, coverage is done concurrently with the tests themselves.
A coverage lower bound is enforced via `AllJsTests.java`, lower than which the build will fail.

### Local build

For Java tests, choose `Coverage as TestNG Test` instead of the usual `Run as TestNG Test` to run the specified test or test suite.
The coverage will be reported in Eclipse after the test run is over.

Alternatively, use Gradle to run the tests, and obtain the coverage data with `jacocoReport` task, i.e:
```sh
./gradlew ciTests
./gradlew jacocoReport
```
The report can be found in the `build/reports/jacoco/jacocoReport/` directory.

For JavaScript unit tests, simply open `allJsUnitTests.html` and tick `Enable coverage`, or run `AllJsTests.java`.
The coverage will be reported immediately in the test page.
