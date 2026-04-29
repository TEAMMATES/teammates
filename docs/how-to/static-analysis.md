<frontmatter>
  title: "Static Analysis"
</frontmatter>

# Static Analysis

TEAMMATES uses static analysis tools to maintain code quality. Tool versions are declared in `build.gradle` and `package.json`, and rulesets are listed below.

## Tool stack

| Tool name                                        | Ruleset                                                                                                                                                                                                                                          |
| ------------------------------------------------ | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| [CheckStyle](http://checkstyle.sourceforge.net/) | [`static-analysis/teammates-checkstyle.xml`](https://github.com/TEAMMATES/teammates/blob/master/static-analysis/teammates-checkstyle.xml)                                                                                                        |
| [PMD](https://pmd.github.io/)                    | [`static-analysis/teammates-pmd.xml`](https://github.com/TEAMMATES/teammates/blob/master/static-analysis/teammates-pmd.xml), [`teammates-pmdMain.xml`](https://github.com/TEAMMATES/teammates/blob/master/static-analysis/teammates-pmdMain.xml) |
| [SpotBugs](https://spotbugs.github.io/)          | [`static-analysis/teammates-spotbugs.xml`](https://github.com/TEAMMATES/teammates/blob/master/static-analysis/teammates-spotbugs.xml)                                                                                                            |
| [ArchUnit](https://github.com/TNG/ArchUnit)      | -                                                                                                                                                                                                                                                |
| [ESLint](https://eslint.org/)                    | [`eslint.config.js`](https://github.com/TEAMMATES/teammates/blob/master/eslint.config.js)                                                                                                                                                        |
| [stylelint](https://stylelint.io/)               | [`static-analysis/teammates-stylelint.yml`](https://github.com/TEAMMATES/teammates/blob/master/stylelint.config.mjs)                                                                                                                             |
| [prettier](https://prettier.io/)                 | -                                                                                                                                                                                                                                                |

## Suppressing rules

Rules can be suppressed when necessary. When doing so:

- Be as specific as possible — suppress only the specific rule and scope (lines/methods/classes) required.
- Re-enable the rule once the suppression is no longer necessary.
- Always explain why the rule is being suppressed.

## Running static analysis

To run all static analysis tools:

<tabs>
<tab header="Mac / Linux">

```sh
./gradlew lint --continue
npm run lint
```
| Tool name  | Command                                                |
| ---------- | ------------------------------------------------------ |
| CheckStyle | `./gradlew checkstyleMain`, `./gradlew checkstyleTest` |
| PMD        | `./gradlew pmdMain`, `./gradlew pmdTest`               |
| SpotBugs   | `./gradlew spotbugsMain`, `./gradlew spotbugsTest`     |
| ArchUnit   | `./gradlew architectureTest`                           |
| ESLint     | `npm run lint:ts`                                      |
| stylelint  | `npm run lint:css:syntax`                              |
| prettier   | `npm run lint:css:styles`                              |
</tab>
<tab header="Windows">

```sh
gradlew.bat lint --continue
npm run lint
```
| Tool name  | Command                                                    |
| ---------- | ---------------------------------------------------------- |
| CheckStyle | `gradlew.bat checkstyleMain`, `gradlew.bat checkstyleTest` |
| PMD        | `gradlew.bat pmdMain`, `gradlew.bat pmdTest`               |
| SpotBugs   | `gradlew.bat spotbugsMain`, `gradlew.bat spotbugsTest`     |
| ArchUnit   | `gradlew.bat architectureTest`                             |
| ESLint     | `npm run lint:ts`                                          |
| stylelint  | `npm run lint:css:syntax`                                  |
| prettier   | `npm run lint:css:styles`                                  |
</tab>
</tabs>
