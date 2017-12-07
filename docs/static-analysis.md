
# Static Analysis

TEAMMATES uses a number of static analysis tools in order to maintain code quality and measure code coverage.
This document will cover an overview of these tools and how to run them in local environment.

- [Version numbers](#version-numbers)
- [Tool stack](#tool-stack)
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

Refer to [IDE Usage](ide-usage.md) for configuring CheckStyle in [Eclipse](ide-usage.md#configuring-checkstyle-eclipse-plugin) and [IntelliJ](ide-usage.md#configuring-checkstyle-in-intellij-idea).

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

Refer to [IDE Usage](ide-usage.md) for configuring CheckStyle in [Eclipse](ide-usage.md#configuring-pmd-eclipse-plugin) and [IntelliJ](ide-usage.md#configuring-pmd-for-intellij).

##### Suppressing PMD warnings

To introduce code that violates PMD rules, use `@SuppressWarnings("PMD.RuleName")` annotation at the narrowest possible scope. PMD also provides several other methods of suppressing rule violations, which can be found in the [documentation here](http://pmd.sourceforge.net/snapshot/usage/suppressing.html).
The suppression should be as specific as possible, and the reason for violating the rule should be explained.

### FindBugs

[FindBugs](http://findbugs.sourceforge.net) analyses Java source code for potential bugs at bytecode level, thus able to find potential bugs that PMD cannot find.
In Gradle build, the rules are configured by specifying the classes in the `visitors` variable.
The plugin for Eclipse can be found [here](http://findbugs.cs.umd.edu/eclipse/).
The plugin for IntelliJ can be found [here](https://plugins.jetbrains.com/idea/plugin/3847-findbugs-idea).

> You can also [configure all the static analysis tools automatically](ide-usage.md#intellij-automatic-setup) for IntelliJ IDEA.

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
> You can [configure all the static analysis tools automatically](ide-usage.md#intellij-automatic-setup) for IntelliJ IDEA or follow the [manual instructions](ide-usage.md#configuring-eslint-for-intellij).

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

> You can [configure all the static analysis tools automatically](ide-usage.md#intellij-automatic-setup) for IntelliJ IDEA or follow the [manual instructions](ide-usage.md#configuring-stylelint-for-intellij).

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

Refer to [IDE Usage](ide-usage.md) for running static analyis in [Eclipse](ide-usage.md#running-static-analysis-in-eclipse) and [IntelliJ](ide-usage.md#running-static-analysis-in-intellij).

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

Refer to [IDE Usage](ide-usage.md) for running code coverage session in [Eclipse](ide-usage.md#running-code-coverage-session-in-eclipse) and [IntelliJ](ide-usage.md#running-code-coverage-session-in-intellij).

