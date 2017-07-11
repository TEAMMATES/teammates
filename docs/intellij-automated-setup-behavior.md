# IntelliJ IDEA Automated Setup Behavior

* [Project Setup Behavior](#project-setup-behavior)
* [Static Analysis Tools Setup Behavior](#static-analysis-tools-setup-behavior)

## Project Setup Behavior
When the following command is executed,
```sh
./gradlew setupIntellijProject
```
A pre-configured IntelliJ IDEA project will be created from `.templates/.idea`.

The syntax for the pre-configured settings found below are as follows:
* `${buildDir}` refers to the build directory specified in Gradle
* A `XPath` like syntax is used to refer to the XML nodes

### Artifacts

| Settings Info |                                        |
|---------------|----------------------------------------|
| File name     | `artifacts/Gradle___teammates_war.xml` |
| Settings      | `Project Structure → Artifacts`        |

**Parent Node:** `/component`

| IntelliJ Setting    | Node                    | Value                            |
|---------------------|-------------------------|----------------------------------|
| Artifact name       | `/artifact[@name]`      | `Gradle : teammates.war`         |
| Artifact type       | `/artifact[@type]`      | `war`                            |
| `Output directory:` | `/artifact/output-path` | `$PROJECT_DIR$/${buildDir}/libs` |

**Parent Node:** `/component/artifact/properties[@id='gradle-properties']`

| IntelliJ Setting           | Node                              | Value           |
|----------------------------|-----------------------------------|-----------------|
| Linked Gradle Project Path | `/options[@external-project-path` | `$PROJECT_DIR$` |

**Parent Node:** None

| IntelliJ Setting | Node                                             | Value           |
|------------------|--------------------------------------------------|-----------------|
| Archive name     | `/component/artifact/root[@id='archive'][@name]` | `teammates.war` |

**Parent Node:** `/artifact/root[@name='teammates.war']`

| IntelliJ Setting | Node                                       | Value                               |
|------------------|--------------------------------------------|-------------------------------------|
| Artifact element | `/element[@id='artifact'][@artifact-name]` | `Gradle : teammates.war (exploded)` |

**NOTE**
> The libraries are not specified and will be generated with the Gradle Integration by IntelliJ.

| Settings Info |                                         |
|---------------|-----------------------------------------|
| File name     | `Gradle___teammates_war__exploded_.xml` |
| Settings      | `Project Structure → Artifacts`         |

**Parent Node:** `/component`

| IntelliJ Setting           | Node                        | Value                                                   |
|----------------------------|-----------------------------|---------------------------------------------------------|
| Artifact name              | `/artifact[@name]`          | `Gradle : teammates.war (exploded)`                     |
| Artifact type              | `/artifact[@type]`          | `exploded-war`                                          |
| `Include in project build` | `/artifact[@build-on-make]` | `true`                                                  |
| `Output directory:`        | `/artifact/output-path`     | `$PROJECT_DIR$/${buildDir}/libs/exploded/teammates.war` |

**Parent Node:** `/artifact/properties[@id='gradle-properties']`

| IntelliJ Setting           | Node                               | Value           |
|----------------------------|------------------------------------|-----------------|
| Linked Gradle Project Path | `/options[@external-project-path]` | `$PROJECT_DIR$` |

**Parent Node:** `/artifact/root[@id='root']`

| IntelliJ Setting  | Node                      | Value                                                                        |
|-------------------|---------------------------|------------------------------------------------------------------------------|
| Artifact elements | The various element nodes | Contains paths to `META-INF` files, Web facet resources, and`WEB-INF` folder |

**NOTE**
> The libraries are not specified and will be generated with the Gradle Integration by IntelliJ.

### Code Style

| Settings Info |                         |
|---------------|-------------------------|
| File name     | `codeStyleSettings.xml` |
| Settings      | `Editor → Code Style`   |

**Parent Node:** `/project/component[@name='ProjectCodeStyleSettingsManager]']`

| IntelliJ Setting           | Node                                      | Value  |
|----------------------------|-------------------------------------------|--------|
| Use per project settings   | `option[@name='USE_PER_PROJECT_SETTINGS'` | `true` |

**Parent Node:** `/project/component[@name='ProjectCodeStyleSettingsManager']/option[@name='PER_PROJECT_SETTINGS'/value`

| IntelliJ Setting                                              | Node                                                                         | Value |
|---------------------------------------------------------------|------------------------------------------------------------------------------|-------|
| `Java → Imports → Class count to use import with ‘*’:`        | `option[@name='CLASS_COUNT_TO_USE_IMPORT_ON_DEMAND'][@value]`                | `999` |
| `Java → Imports → Names count to use static import with ‘*’:` | `option[@name='NAMES_COUNT_TO_USE_IMPORT_ON_DEMAND'][@value]`                | `999` |
| `Java → Imports → Packages to Use Import with ‘*’`            | `option[@name='PACKAGES_TO_USE_IMPORT_ON_DEMAND']/value`                     | Empty |
| `Java → Imports → Import Layout`                              | `option[@name='IMPORT_LAYOUT_TABLE']/value`                                  | [1]   |
| `HTML → Tabs and Indents → Indent:`                           | `codeStyleSettings[@language='HTML']/indentOptions/INDENT_SIZE`              | `2`   |
| `HTML → Tabs and Indents → Continuation Indent:`              | `codeStyleSettings[@language='HTML']/indentOptions/CONTINUATION_INDENT_SIZE` | `4`   |
| `HTML → Tabs and Indents → Tab size:`                         | `codeStyleSettings[@language='HTML']/indentOptions/TAB_SIZE`                 | `2`   |
| `JSP → Tabs and Indents → Indent:`                            | `codeStyleSettings[@language='JSP']/indentOptions/INDENT_SIZE`               | `2`   |
| `JSP → Tabs and Indents → Continuation Indent:`               | `codeStyleSettings[@language='JSP']/indentOptions/CONTINUATION_INDENT_SIZE`  | `4`   |
| `JSP → Tabs and Indents → Tab size:`                          | `codeStyleSettings[@language='JSP']/indentOptions/TAB_SIZE`                  | `2`   |

[1] Layout as defined in the [Checkstyle rules](../static-analysis/teammates-checkstyle.xml#L161).

**NOTE**
> Tab characters are not used for indentation if no `USE_TAB_CHARACTER` attribute is set.\
> Indentation settings are also left as the default for Java, JavaScript, JSON, CSS and XML (shown in the table below).

| Language   | Tab size | Indent | Continuation indent |
|------------|----------|--------|---------------------|
| Java       | 4        | 4      | 8                   |
| JavaScript | 4        | 4      | 4                   |
| JSON       | 4        | 2      | 8                   |
| CSS        | 4        | 4      | 8                   |
| XML        | 4        | 4      | 8                   |

### Java Compiler

| Settings Info |                                                             |
|---------------|-------------------------------------------------------------|
| File name     | `compiler.xml`                                              |
| Settings      | `Build, Execution, Deployment → Compiler → Java Compiler`   |

| IntelliJ Setting          | Node                                                               | Value |
|---------------------------|--------------------------------------------------------------------|-------|
| `Target bytecode version` | `/project/component/byteCodeTargetLevel/module[@name='teammates']` | `1.7` |

### Encoding

| Settings Info |                                                             |
|---------------|-------------------------------------------------------------|
| File name     | `encodings.xml`                                             |
| Settings      | `Editor → File Encodings`                                   |

| IntelliJ Setting                         | Node                                                    | Value   |
|------------------------------------------|---------------------------------------------------------|---------|
| `Default encoding for properties files:` | `/project/component[@defaultCharsetForPropertiesFiles]` | `UTF-8` |
| `Project Encoding:`                      | `/project/component/file[@url='PROJECT'][@charset]`     | `UTF-8` |

### Gradle

| Settings Info |                                                         |
|---------------|---------------------------------------------------------|
| File name     | `gradle.xml`                                            |
| Settings      | `Build, Execution, Deployment → Build Tools → Gradle`   |

**Parent Node:** `/component/option/GradleProjectSettings/`

| IntelliJ Setting                           | Node                                 | Value             |
|--------------------------------------------|--------------------------------------|-------------------|
| `Use default gradle wrapper (recommended)` | `option[@distributionType]`          | `DEFAULT_WRAPPED` |
| External project path                      | `option[@externalProjectPath]`       | `$PROJECT_DIR$`   |
| `Gradle JVM:`                              | `option[@gradleJvm]`                 | `1.7`             |
| `Create separate module per source set`    | `option[@resolveModulePerSourceSet]` | `false`           |
| `Use auto-import`                          | `option[@useAutoImport]`             | `true`            |

### Inspections

| Settings Info |                                            |
|---------------|--------------------------------------------|
| File name     | `inspectionProfiles/profiles_settings.xml` |
| Settings      | `Editor → Inspections`                     |

**Parent Node:** `/component/settings/option`

| IntelliJ Setting | Node                                                       | Value       |
|------------------|------------------------------------------------------------|-------------|
| `Profile:`       | `[@name='projectProfile']` and `[@name='PROJECT_PROFILE']` | `teammates` |

### Javascript

| Settings Info |                                        |
|---------------|----------------------------------------|
| File name     | `misc.xml`                             |
| Settings      | `Languages & Frameworks → Javascript`  |

| IntelliJ Setting              | Node                                                                                   | Value |
|-------------------------------|----------------------------------------------------------------------------------------|-------|
| `Javascript language version` | `/project/component[@name='JavaScriptSettings']/option[@name='languageLevel'][@value]` | `ES6` |

### Schemas and DTDs

| Settings Info |                                              |
|---------------|----------------------------------------------|
| File name     | `misc.xml`                                   |
| Settings      | `Languages & Frameworks → Schemas and DTDs`  |

**Parent Node:** `project/component[@name='ProjectResources']`

| IntelliJ Setting             | Node                                                                                 | Value                                                            |
|------------------------------|--------------------------------------------------------------------------------------|------------------------------------------------------------------|
| PMD XSD                      | `/resource/[@url='http://pmd.sourceforge.net/ruleset_2_0_0.xsd'][@location]`         | `$PROJECT_DIR$/static-analysis/pmd_ruleset_2_0_0.xsd`            |
| CheckStyle Configuration DTD | `/resource/[@url='http://www.puppycrawl.com/dtds/configuration_1_3.dtd'][@location]` | `$PROJECT_DIR$/static-analysis/checkstyle_configuration_1_3.dtd` |
| CheckStyle Suppressions DTD  | `/resource/[@url='http://www.puppycrawl.com/dtds/suppressions_1_1.dtd'][@location]`  | `$PROJECT_DIR$/static-analysis/checkstyle_suppressions_1_1.dtd`  |

### Project Settings

| Settings Info |                                |
|---------------|--------------------------------|
| File name     | `misc.xml`                     |
| Settings      | `Project Structure → Project`  |

**Parent node:** `/project/component[@name='ProjectRootManager']`

| IntelliJ Setting           | Node                                        | Value                      |
|----------------------------|---------------------------------------------|----------------------------|
| `Project language level:`  | `[@languageLevel]`                          | `JDK_1_7`                  |
| `Project SDK:`             | `[@project-jdk-name]`,`[@project-jdk-type]` | `1.7`, `JavaSDK`           |
| `Project compiler output:` | `output[@url]`                              | `file://$PROJECT_DIR$/out` |

### Modules

| Settings Info |                                |
|---------------|--------------------------------|
| File name     | `modules.xml`                  |
| Settings      | `Project Structure → Modules`  |

**Parent Node:** `/project/component[@name='ProjectRootManager']`

| IntelliJ Setting            | Node        | Value                                              |
|-----------------------------|-------------|----------------------------------------------------|
| Module in project file url  | `@fileurl`  | `file://$PROJECT_DIR$/.idea/modules/teammates.iml` |
| Module in project file path | `@filepath` | `$PROJECT_DIR$/.idea/modules/teammates.iml`        |

---

| Settings Info |                                |
|---------------|--------------------------------|
| File name     | `teammates.iml`                |
| Settings      | `Project Structure → Modules`  |

**Parent Node:** None

| IntelliJ Setting             | Node                                     | Value                |
|------------------------------|------------------------------------------|----------------------|
| External linked project id   | `/module[@external.linked.project.id]`   | `teammates`          |
| External linked project path | `/module[@external.linked.project.path]` | `$MODULE_DIR$/../..` |
| External root project path   | `/module[@external.root.project.path]`   | `$MODULE_DIR$/../..` |
| External system id           | `/module[@external.system.id]`           | `GRADLE`             |

**Parent Node:** `/module/component[@name='FacetManager']`

| IntelliJ Setting                   | Node                                | Value                                                 |
|------------------------------------|-------------------------------------|-------------------------------------------------------|
| `Google App Engine` Facet          | `/facet[@type='google-app-engine']` | SDK home path is set                                  |
| `Web Gradle : teammates.war` Facet | `/facet[@type='web']`               | Web and source folders and deployment descriptor path |

**Parent Node:** `/module/component[@name='NewModuleRootManager']`

| IntelliJ Setting    | Node                 | Value                                                 |
|---------------------|----------------------|-------------------------------------------------------|
| `Output path:`      | `/output[@url]`      | `file://$MODULE_DIR$/../../${buildDir}/classes/main"` |
| `Test output path:` | `/output-test[@url]` | `file://$MODULE_DIR$/../../${buildDir}/classes/test`  |

**Parent Node:** `/module/component[@name='NewModuleRootManager']/content`

| IntelliJ Setting        | Node                                             | Value                                                                                  |
|-------------------------|--------------------------------------------------|----------------------------------------------------------------------------------------|
| `Source Folders`        | `sourceFolder[@isTestSource='false'][@url]`      | `file://$MODULE_DIR$/../../src/main/java`                                              |
| `Test Source Folders`   | `sourceFolder[@isTestSource='true'][@url]`       | `file://$MODULE_DIR$/../../src/client/java`  `file://$MODULE_DIR$/../../src/test/java` |
| `Resource Folders`      | `sourceFolder[@type='java-resource'][@url]`      | `file://$MODULE_DIR$/../../src/main/resources`                                         |
| `Test Resource Folders` | `sourceFolder[@type='java-test-resource'][@url]` | `file://$MODULE_DIR$/../../src/test/resources`                                         |
| `Excluded Folders`      | `excludeFolder[@url]`                            | `file://$MODULE_DIR$/../../.gradle\|build\|buildIdea`                                  |
| JDK                     | `orderEntry[@type]`                              | `inheritedJdk`                                                                         |
| `<Module Source>`       | `orderEntry[@type]`, `orderEntry[@forTests]`     | `sourceFolder`, `false`                                                                |

### Version Control

| Settings Info |                   |
|---------------|-------------------|
| File name     | `vcs.xml`         |
| Settings      | `Version Control` |

**Parent Node:** `/project/component[@name='VcsDirectoryMappings']`

| IntelliJ Setting | Node                   | Value           |
|------------------|------------------------|-----------------|
| `Directory`      | `/mapping[@directory]` | `$PROJECT_DIR$` |
| `vcs`            | `/mapping[@vcs]`       | `Git`           |

## Static Analysis Tools Setup Behavior
When the following command is executed,
```sh
./gradlew setupIntellijStaticAnalysis
```
The project will be configured with using the pre-configured static analysis tools settings from `.templates/ideaPlugins`.

The syntax for the pre-configured settings found below are as follows:
* `${buildDir}` refers to the build directory specified in Gradle
* A `XPath` like syntax is used to refer to the XML nodes

### CheckStyle

| Settings Info |                               |
|---------------|-------------------------------|
| File name     | `checkstyle-idea.xml`         |
| Settings      | `Other Settings → Checkstyle` |

**Parent Node:** `/project/component[@name='CheckStyle-IDEA']/option[@name='configuration']/map`

| IntelliJ Setting                      | Node                                 | Value               |
|---------------------------------------|--------------------------------------|---------------------|
| `Active` `Configuration File`         | `entry[@key='active-configuration']` | `PROJECT_RELATIVE:$PRJ_DIR$/static-analysis/teammates-checkstyle.xml:teammates` |
| `Checkstyle version:`                 | `entry[@key='checkstyle-version']`   | `7.7`               |
| `basedir` `Value`                     | `entry[@key='property-1.basedir']`   | `$PROJECT_DIR$`     |
| `Scan Scope:`                         | `entry[@key='scanscope']`            | `JavaOnlyWithTests` |
| `Treat Checkstyle errors as warnings` | `entry[@key='suppress-errors']`      | `false`             |
| `Use a local Checkstyle file`  `Store relative to project location`  | `entry[@key='location-1']` | `Git`           |

### Inspections

| Settings Info |                                    |
|---------------|------------------------------------|
| File name     | `inspectionProfiles/teammates.xml` |
| Settings      | Depends on setting below           |

**Parent Node:** `/component/profile`

| IntelliJ Setting | Node                    | Value       |
|------------------|-------------------------|-------------|
| Profile Name     | `option[@name='myName'` | `teammates` |

#### ESLint

**Parent Node:** `/component/profile`
**Settings:** `Languages & Frameworks → Javascript → Code Quality Tools → ESLint`

| IntelliJ Setting          | Node                                         | Value  |
|---------------------------|----------------------------------------------|--------|
| Whether ESLint is enabled | `inspection_tool[@class='Eslint'][@enabled]` | `true` |

#### Stylelint

**Parent Node:** `/component/profile`
**Settings:** `Languages & Frameworks → Stylesheets → Stylelint`

| IntelliJ Setting          | Node                                            | Value  |
|---------------------------|-------------------------------------------------|--------|
| Whether ESLint is enabled | `inspection_tool[@class='Stylelint'][@enabled]` | `true` |

### ESLint Inspection Setting

| Settings Info |                                                                     |
|---------------|---------------------------------------------------------------------|
| File name     | `jsLinters/eslint.xml`                                              |
| Settings      | `Languages & Frameworks → Javascript → Code Quality Tools → ESLint` |

**Parent Node:** `/project/component[@name='EslintConfiguration']`

| IntelliJ Setting        | Node                                              | Value                                                |
|-------------------------|---------------------------------------------------|------------------------------------------------------|
| `Configuration file:`   | `/custom-configuration-file[@used='true'][@path]` | `$PROJECT_DIR$/static-analysis/teammates-eslint.yml` |
| `Extra eslint options:` | `/extra-options[@value]`                          | `--ext .es6 --ignore-pattern '**/*.js'`                                         |

### PMD

| Settings Info |                        |
|---------------|------------------------|
| File name     | `misc.xml`             |
| Settings      | `Other Settings → PMD` |

**Parent Node:** `/project/component[@name='PDMPlugin']`

| IntelliJ Setting       | Node                                                    | Value                                                 |
|------------------------|---------------------------------------------------------|-------------------------------------------------------|
| `RuleSets`             | `/option[@name='customRuleSets']/list/option[@value]`   | `$PROJECT_DIR$/static-analysis/teammates-pmd.xml`     |
| `RuleSets`             | `/option[@name='customRuleSets']/list/option[@value]`   | `$PROJECT_DIR$/static-analysis/teammates-pmdMain.xml` |
| `Target JDK` `Options` | `/option[@name='options']/map/entry[@key='Target JDK']` | `1.7`                                                 |
| `Skip Test Sources`    | `/option[@name='skipTestSources']`                      | `false`                                               |
