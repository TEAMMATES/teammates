# Static Analysis

TEAMMATES uses a number of static analysis tools in order to maintain code quality.
This document will cover an overview of these tools and how to run them in local environment.

IDE integrations will be listed down where available, but no further information e.g. setting up will be provided.

- [Version numbers](#version-numbers)
- [Tool stack](#tool-stack)
- [Suppressing rules](#suppressing-rules)
- [IntelliJ automatic setup](#intellij-automatic-setup)
- [Running static analysis](#running-static-analysis)

## Version numbers

The version number of all the tool stacks are declared in `build.gradle` or `package.json`.

When downloading the plugin for Eclipse/IntelliJ, find the plugin version that uses the correct version of the tool, e.g if CheckStyle 8.0 is used find an Eclipse/IntelliJ plugin that uses CheckStyle 8.0 as well.
If the exact version of the plugin cannot be found, using the latest version is allowed, however there is no guarantee that there will be no backward-incompatible changes.

Conversely, when updating any tool, try to ensure that the tool version is supported by the Eclipse/IntelliJ plugin, e.g when upgrading CheckStyle to 8.0 try to ensure that there is an Eclipse/IntelliJ plugin which supports that version as well.

## Tool stack

| Tool name | Eclipse integration | IntelliJ integration | Ruleset |
| --- | --- | --- | --- |
| [CheckStyle](http://checkstyle.sourceforge.net/) | [Checkstyle Plug-in](https://marketplace.eclipse.org/content/checkstyle-plug) | [CheckStyle-IDEA](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea) | [`teammates-checkstyle.xml`]((../static-analysis/teammates-checkstyle.xml)) |
| [PMD](https://pmd.github.io/) | [eclipse-pmd](https://marketplace.eclipse.org/content/eclipse-pmd) | [PMDPlugin](https://plugins.jetbrains.com/idea/plugin/1137-pmdplugin) | [`teammates-pmd.xml`](../static-analysis/teammates-pmd.xml), [`teammates-pmdMain.xml`](../static-analysis/teammates-pmdMain.xml) |
| [SpotBugs](https://spotbugs.github.io/) | [SpotBugs Eclipse Plugin](https://marketplace.eclipse.org/content/spotbugs-eclipse-plugin) | -<sup>1</sup> | [`teammates-spotbugs.xml`](../static-analysis/teammates-spotbugs.xml) |
| [Macker](https://github.com/andrena/macker) | - | - | [`teammates-macker.xml`](../static-analysis/teammates-macker.xml) |
| [TSLint](https://palantir.github.io/tslint/) | - | Built-in<sup>2</sup> | [`teammates-tslint.yml`](../static-analysis/teammates-tslint.yml) |
| [JSONlint](https://github.com/marionebl/jsonlint-cli) | - | - | - |
| [stylelint](http://stylelint.io) | - | Built-in<sup>2,3</sup> | [`teammates-stylelint.yml`](../static-analysis/teammates-stylelint.yml) |
| [lintspaces](https://github.com/evanshortiss/lintspaces-cli) | - | - | - |

<sup>1</sup> You may be able to use [FindBugs-IDEA](https://plugins.jetbrains.com/idea/plugin/3847-findbugs-idea) plugin instead.

<sup>2</sup> Integrations are built-in and can be found under `Languages & Frameworks` under `File → Settings` or `IntelliJ IDEA → Preferences`.

<sup>3</sup> During setup: Copy `$PROJECT_DIR$/static-analysis/teammates-stylelint.yml` to `$PROJECT_DIR$/.stylelintrc.yml`.

## Suppressing rules

Not all rules from all static analysis tools need to be followed 100% of the time; there are times where some rules need to be broken for various reasons.
Most of the static analysis tools we use allow for such leeway through various means.

General rule of thumb when suppressing rules:
- The suppression should be as specific as possible, e.g. specific rule, specific scope (lines/methods/classes)
- The rule must be re-enabled after the suppression is no longer necessary
- The reason for violating the rule should be explained

## IntelliJ automatic setup

An automated setup for some of the static analysis tools is provided for IntelliJ users.

1. Ensure the following plugins are installed:
   - [`CheckStyle-IDEA`](https://plugins.jetbrains.com/plugin/1065-checkstyle-idea)
   - [`PMDPlugin`](https://plugins.jetbrains.com/plugin/1137-pmdplugin)
   - (Optional) [`NodeJS`](https://plugins.jetbrains.com/plugin/6098-nodejs): You can this plugin to manage dependencies.

1. Run the command to setup the settings for the various plugins:
   ```sh
   ./gradlew setupIntellijStaticAnalysis
   ```

1. Restart IntelliJ IDEA.

> - Once `CheckStyle-IDEA` is set-up, the version used will be kept in sync with the build script when `Use auto-import` is enabled. This is achieved by the Gradle task `syncIntelliJCheckStyleVersion` which runs after `setupIntellijStaticAnalysis`.
> - For `stylelint`, the installed packages in `node_modules` are directly referenced in IntelliJ IDEA.

## Running static analysis

### CLI

> Note the following:
> - Change `./gradlew` to `gradlew.bat` in Windows.
> - All the commands are assumed to be run from the root project folder, unless otherwise specified.

| Tool name | Command |
| --- | --- |
| CheckStyle | `./gradlew checkstyleMain`, `./gradlew checkstyleTest` |
| PMD | `./gradlew pmdMain`, `./gradlew pmdTest` |
| SpotBugs | `./gradlew spotbugsMain`, `./gradlew spotbugsTest` |
| Macker | `./gradlew macker` |
| TSLint | `npm run lint:ts` |
| JSONlint | `npm run lint:json` |
| stylelint | `npm run lint:css` |
| lintspaces | `npm run lint:spaces` |

To run all static analysis tasks in one sitting, run the following two commands:
```sh
./gradlew lint --continue
npm run lint
```

### CI

Travis CI and AppVeyor CI will run static analysis _before_ testing.
If violations are found, the build will be terminated with an error before any testing is done in order to save time and resources.
