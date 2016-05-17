
# Static Analysis

TEAMMATES uses a number of static analysis tools in order to maintain code quality and measure code coverage.
This document will cover an overview of these tools and how to run them in local environment.

- [Tool stack](#tool-stack)
- [Running static analysis](#running-static-analysis)
- [Running code coverage session](#running-code-coverage-session)

## Tool stack

### CheckStyle

[CheckStyle](http://checkstyle.sourceforge.net/index.html) helps to enforce coding standard in Java source code.
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-checkstyle.xml).
The plugin for Eclipse can be found [here](http://eclipse-cs.sourceforge.net/#!/).

#####Configuring Checkstyle Eclipse plugin  

1. In `Project > Properties`, go to the `Checkstyle` tab.
2. In the `Local Check Configurations tab`, create a new Check Configuration. Select `Project Relative Configuration` for its Type, enter any Name you wish and set the Location to the `teammates-checkstyle.xml` file in the Project Folder. Click OK.
3. In the `Main` tab, uncheck `Use simple configuration`.
4. Add a new File Set. It should include only the `.java$` file. Enter any name you wish for the `File Set Name`, and select the Check Configuration that you created earlier for `Check Configuration`. Click OK.
5. Ensure that only the newly created File Set is enabled. Disable all other File Sets if they are enabled. Click OK. You have successfully setup the Checkstyle Eclipse plugin.

### PMD

[PMD](https://pmd.github.io) analyses the Java source code for common programming flaws (e.g unused variables, empty catch block).
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-pmd.xml).
The plugin for Eclipse can be found [here](https://sourceforge.net/projects/pmd/files/pmd-eclipse/update-site/).

#####Suppressing PMD warnings
To introduce code that violates PMD rules, PMD provides several methods of [suppressing](http://pmd.sourceforge.net/snapshot/usage/suppressing.html) rule violations, such as 
the `SuppressWarnings` annotation or the `NOPMD` marker, which can be used to tell PMD to ignore specific parts of the code.
The suppression should be as specific as possible, and the reason for violating the rule should be explained.      

### FindBugs

[FindBugs](http://findbugs.sourceforge.net) analyses Java source code for potential bugs at bytecode level, thus able to find potential bugs that PMD cannot find.
In Gradle build, the rules are configured by specifying the classes in the `visitors` variable.
The plugin for Eclipse can be found [here](http://findbugs.cs.umd.edu/eclipse/).

### EclEmma/JaCoCo

[EclEmma/JaCoCo](http://eclemma.org/jacoco/) measures code coverage for Java test run.
Normally, the coverage will be run against all classes specified as the source code, but it can be configured to exclude classes matching certain name patterns.
The plugin for Eclipse can be found [here](http://eclemma.org).

### ESLint

[ESLint](http://eslint.org) functions both to enforce coding standard and also to find potential bugs in JavaScript source code.
The rules to be used are configured in a ruleset file; in TEAMMATES the file can be found [here](../static-analysis/teammates-eslint.yml).
ESLint is a node.js package, currently not supported for Eclipse Java EE project.
To set it up, [install node.js](https://nodejs.org/en/download/) if necessary and then install the ESLint package:
```
npm install -g eslint
```

##### Suppressing ESLint warnings

To introduce code that violates ESLint rules, wrap the violating code with `/* eslint-disable rule-name */` and re-enable it afterwards
with `/* eslint-enable rule-name */`. The suppression should be as specific as possible, and the reason for violating the rule should be explained.

An example to suppress the `camelcase` rule is as follows:
```javascript
/* eslint-disable camelcase */ // The variable name is provided by an external library, which does not follow camelcase.
// violating codes go here
/* eslint-enable camelcase */
```

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

Alternatively, run the tools via Gradle:
```
./gradlew -b travis.gradle {toolType}{sourceCodeType}
```
where `{toolType}` = checkstyle, pmd, findbugs (lowercase), and `{sourceCodeType}` = Main, Test (Pascal Case).
The reports can be found in the `build/reports/{toolType}/` directory.

To run ESLint analysis on all JavaScript source files, run the following command:
```
./gradlew -b travis.gradle eslint
```
The violations caught, if any, will be printed to the console itself.

To run all static analysis tasks in one sitting, run the following command:
```
./gradlew -b travis.gradle staticAnalysis
```

## Running code coverage session

### Travis CI

For JavaScript unit tests, coverage is done concurrently with the tests themselves.
A coverage lower bound is enforced via `AllJsTests.java`, lower than which the build will fail.

### Local build

For Java tests, choose `Coverage as TestNG Test` instead of the usual `Run as TestNG Test` to run the specified test or test suite.
The coverage will be reported in Eclipse after the test run is over.

Alternatively, use Gradle to run the tests, and obtain the coverage data with `jacocoTestReport` task, i.e:
```
./gradlew -b travis.gradle travisTests
./gradlew -b travis.gradle jacocoTestReport
```
The report can be found in the `build/reports/jacoco/test/` directory.

For JavaScript unit tests, simply open `allJsUnitTests.html` and tick `Enable coverage`, or run `AllJsTests.java`.
The coverage will be reported immediately in the test page.
