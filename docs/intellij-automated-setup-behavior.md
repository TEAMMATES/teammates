# IntelliJ IDEA Static Analysis Tools Setup Behavior

When the following command is executed,
```sh
./gradlew setupIntellijStaticAnalysis
```
The project will be configured with using the pre-configured static analysis tools settings from `.templates/ideaPlugins`.

The syntax for the pre-configured settings found below are as follows:
* `${buildDir}` refers to the build directory specified in Gradle
* A `XPath` like syntax is used to refer to the XML nodes

## CheckStyle

| Settings Info |                               |
|---------------|-------------------------------|
| File name     | `checkstyle-idea.xml`         |
| Settings      | `Other Settings → Checkstyle` |

**Parent Node:** `/project/component[@name='CheckStyle-IDEA']/option[@name='configuration']/map`

| IntelliJ Setting                      | Node                                 | Value               |
|---------------------------------------|--------------------------------------|---------------------|
| `Active` `Configuration File`         | `entry[@key='active-configuration']` | `PROJECT_RELATIVE:$PRJ_DIR$/static-analysis/teammates-checkstyle.xml:teammates` |
| `Checkstyle version:`                 | `entry[@key='checkstyle-version']`   | `Automatic replacement: `checkStyleVersion` in build script` [[1]](#versions-sync-with-tools-used-in-build-script) |
| `basedir` `Value`                     | `entry[@key='property-1.basedir']`   | `$PROJECT_DIR$`     |
| `Scan Scope:`                         | `entry[@key='scanscope']`            | `JavaOnlyWithTests` |
| `Treat Checkstyle errors as warnings` | `entry[@key='suppress-errors']`      | `false`             |
| `Use a local Checkstyle file`  `Store relative to project location`  | `entry[@key='location-1']` | `Git`           |

## Inspection Profile To Use

| Settings Info |                                            |
|---------------|--------------------------------------------|
| File name     | `inspectionProfiles/profiles_settings.xml` |
| Settings      | `Editor → Inspections`                     |

**Parent Node:** `/component/settings/option`

| IntelliJ Setting | Node                                                       | Value       |
|------------------|------------------------------------------------------------|-------------|
| `Profile:`       | `[@name='projectProfile']` and `[@name='PROJECT_PROFILE']` | `teammates` |

## Inspection Profile

| Settings Info |                                    |
|---------------|------------------------------------|
| File name     | `inspectionProfiles/teammates.xml` |
| Settings      | Depends on setting below           |

**Parent Node:** `/component/profile`

| IntelliJ Setting | Node                     | Value       |
|------------------|--------------------------|-------------|
| Profile Name     | `option[@name='myName']` | `teammates` |

### ESLint

**Parent Node:** `/component/profile`
**Settings:** `Languages & Frameworks → Javascript → Code Quality Tools → ESLint`

| IntelliJ Setting          | Node                                         | Value  |
|---------------------------|----------------------------------------------|--------|
| Whether ESLint is enabled | `inspection_tool[@class='Eslint'][@enabled]` | `true` |

### Stylelint

**Parent Node:** `/component/profile`
**Settings:** `Languages & Frameworks → Stylesheets → Stylelint`

| IntelliJ Setting          | Node                                            | Value  |
|---------------------------|-------------------------------------------------|--------|
| Whether ESLint is enabled | `inspection_tool[@class='Stylelint'][@enabled]` | `true` |

## ESLint Inspection Settings

| Settings Info |                                                                     |
|---------------|---------------------------------------------------------------------|
| File name     | `jsLinters/eslint.xml`                                              |
| Settings      | `Languages & Frameworks → Javascript → Code Quality Tools → ESLint` |

**Parent Node:** `/project/component[@name='EslintConfiguration']`

| IntelliJ Setting        | Node                                              | Value                                                |
|-------------------------|---------------------------------------------------|------------------------------------------------------|
| `Configuration file:`   | `/custom-configuration-file[@used='true'][@path]` | `$PROJECT_DIR$/static-analysis/teammates-eslint.yml` |
| `Extra eslint options:` | `/extra-options[@value]`                          | `--ignore-pattern 'src/main/webapp/js/*.js' --ignore-pattern 'src/main/webapp/test/*.js' --ignore-pattern 'test-output/**/*.js'` |

## PMD

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
| `Skip Test Sources`    | `/option[@name='skipTestSources']`                      | `false`                                               |

# Versions sync with tools used in build script

The `CheckStyle-IDEA` plugin has the ability to select versions so its specified version will be automatically synced with the build script. This is achieved by the task `syncIntelliJCheckStyleVersion` which runs after `setupIntellijStaticAnalysis`. The task also executes automatically during configuration phase in which IntelliJ IDEA automatically goes through when `Use auto-import` is enabled.

For `ESLint` and `Stylelint`, the installed packages in `node_modules` are directly referenced in IntelliJ IDEA.

For `FindBugs-IDEA` and `PMD`, they are to be manually kept in sync by the user.
