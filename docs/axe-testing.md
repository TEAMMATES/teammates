<frontmatter>
  title: "Accessibility Testing"
</frontmatter>

# Accessibility Testing

## What is Accessibility Testing?
  
Accessibility testing is the practice of making the application accessible to users with disabilities, who may be navigating the website using various assistive technologies such as screen readers. TEAMMATES aims to be compliant to the [Web Content Accessibility Guidelines (WCAG)](https://www.w3.org/WAI/standards-guidelines/wcag/), and makes use of automated accessibility tests to identify a portion of WCAG rule violations.
  
Accessibility tests in TEAMMATES can be found in the package `teammates.e2e.cases.axe`.

## Running Accessibility Tests

TEAMMATES uses the [axe-core](https://github.com/dequelabs/axe-core-maven-html/blob/develop/selenium/README.md) Selenium Java API for accessibility testing. Accessibility tests are set up in the same way as E2E tests. Do refer to the [E2E testing guide](e2e-testing.md#configuring-browsers-for-e2e-testing) for instructions on how to set up the tests.

### Running the tests
Accessibility tests follow this configuration:

Test suite | Command | Results can be viewed in
---|---|---
`Accessibility tests` | `./gradlew axeTests` | `{project folder}/build/reports/axe-test/index.html`
Any individual accessibility test | `./gradlew axeTests --tests TestClassName` | `{project folder}/build/reports/axe-test/index.html`
 
- Before running `Accessibility tests`, it is important to have the dev server running locally first if you are testing against it.
- When running the test cases, a few cases may fail (this can happen due to timing issues). They can be re-run until they pass without affecting the accuracy of the tests.
   
## Creating Accessibility Tests

Accessibility test classes in TEAMMATES are similar to [E2E test classes](e2e-testing.md#creating-e2e-tests) in that they inherit from `BaseE2ETestCase` and make use of page objects. The difference is that testing is conducted by running `AxeUtil.AXE_BUILDER.analyze()` on the page object's underlying browser driver.
