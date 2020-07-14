# Static Analysis

TEAMMATES uses a number of static analysis tools in order to maintain code quality.
This document will cover an overview of these tools and how to run them in local environment.

- [Version numbers](#version-numbers)
- [Tool stack](#tool-stack)
- [Suppressing rules](#suppressing-rules)
- [Running static analysis](#running-static-analysis)

## Version numbers

The version number of all the tool stacks are declared in `build.gradle` or `package.json`.

## Tool stack

| Tool name | Ruleset |
| --- | --- |
| [CheckStyle](http://checkstyle.sourceforge.net/) | [`teammates-checkstyle.xml`]((../static-analysis/teammates-checkstyle.xml)) |
| [PMD](https://pmd.github.io/) | [`teammates-pmd.xml`](../static-analysis/teammates-pmd.xml), [`teammates-pmdMain.xml`](../static-analysis/teammates-pmdMain.xml) |
| [SpotBugs](https://spotbugs.github.io/) | [`teammates-spotbugs.xml`](../static-analysis/teammates-spotbugs.xml) |
| [ArchUnit](https://github.com/TNG/ArchUnit) | - |
| [TSLint](https://palantir.github.io/tslint/) | [`teammates-tslint.yml`](../static-analysis/teammates-tslint.yml) |
| [JSONlint](https://github.com/marionebl/jsonlint-cli) | - |
| [stylelint](http://stylelint.io) | [`teammates-stylelint.yml`](../static-analysis/teammates-stylelint.yml) |
| [lintspaces](https://github.com/evanshortiss/lintspaces-cli) | - |

## Suppressing rules

Not all rules from all static analysis tools need to be followed 100% of the time; there are times where some rules need to be broken for various reasons.
Most of the static analysis tools we use allow for such leeway through various means.

General rule of thumb when suppressing rules:
- The suppression should be as specific as possible, e.g. specific rule, specific scope (lines/methods/classes)
- The rule must be re-enabled after the suppression is no longer necessary
- The reason for violating the rule should be explained

## Running static analysis

> Note the following:
> - Change `./gradlew` to `gradlew.bat` in Windows.
> - All the commands are assumed to be run from the root project folder, unless otherwise specified.

| Tool name | Command |
| --- | --- |
| CheckStyle | `./gradlew checkstyleMain`, `./gradlew checkstyleTest` |
| PMD | `./gradlew pmdMain`, `./gradlew pmdTest` |
| SpotBugs | `./gradlew spotbugsMain`, `./gradlew spotbugsTest` |
| ArchUnit | `./gradlew architectureTest` |
| TSLint | `npm run lint:ts` |
| JSONlint | `npm run lint:json` |
| stylelint | `npm run lint:css` |
| lintspaces | `npm run lint:spaces` |

To run all static analysis tasks in one sitting, run the following two commands:
```sh
./gradlew lint --continue
npm run lint
```
