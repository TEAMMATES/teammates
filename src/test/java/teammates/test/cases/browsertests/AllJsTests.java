package teammates.test.cases.browsertests;

import java.io.File;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.driver.TestProperties;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.QUnitPage;

/**
 * Loads all JavaScript unit tests (done with QUnit) into a browser and ensures all tests passed.
 */
public class AllJsTests extends BaseUiTestCase {

    private static final float MIN_COVERAGE_REQUIREMENT = 24;
    private QUnitPage page;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        loginAdmin();
        page = AppPage.getNewPageInstance(browser)
                .navigateTo(createUrl(Const.ViewURIs.JS_UNIT_TEST + (TestProperties.isDevServer() ? "?coverage" : "")))
                .changePageType(QUnitPage.class);
        page.waitForPageToLoad();
    }

    @Test
    public void verifyAllJsTestFilesIncluded() {
        Document pageSource = Jsoup.parse(page.getPageSource());
        String testScripts = pageSource.getElementById("test-scripts").html();

        File folder = new File("./src/main/webapp/test");
        File[] listOfFiles = folder.listFiles();
        for (File f : listOfFiles) {
            String fileName = f.getName();
            if (fileName.endsWith("Test.js")) {
                assertTrue(fileName + " is not present in JS test file",
                           testScripts.contains(getSrcStringForJsTestFile(fileName)));
            }
        }
    }

    private String getSrcStringForJsTestFile(String fileName) {
        return "src=\"/test/" + fileName + "\"";
    }

    @Test
    public void executeJsTests() {
        int totalCases = page.getTotalCases();
        int failedCases = page.getFailedCases();

        print("Executed " + totalCases + " JavaScript Unit tests...");

        // Some tests such as date-checking behave differently in Firefox and Chrome.
        int expectedFailedCases = "firefox".equals(TestProperties.BROWSER) ? 0 : 4;
        assertEquals(expectedFailedCases, failedCases);
        assertTrue(totalCases != 0);

        print("As expected, " + expectedFailedCases + " failed tests out of " + totalCases + " tests.");

        if (!TestProperties.isDevServer()) {
            return;
        }

        float coverage = page.getCoverage();

        print(coverage + "% of scripts covered, the minimum requirement is " + MIN_COVERAGE_REQUIREMENT + "%");
        assertTrue(coverage >= MIN_COVERAGE_REQUIREMENT);
    }

}
