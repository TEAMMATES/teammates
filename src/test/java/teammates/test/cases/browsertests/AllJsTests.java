package teammates.test.cases.browsertests;

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

    private QUnitPage page;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        loginAdmin();
        page = AppPage.getNewPageInstance(browser)
                .navigateTo(createUrl(Const.ViewURIs.JS_UNIT_TEST))
                .changePageType(QUnitPage.class);
        page.waitForPageToLoad();
    }

    @Test
    public void executeJsTests() {
        int totalCases = page.getTotalCases();
        int failedCases = page.getFailedCases();

        print("Executed " + totalCases + " JavaScript Unit tests...");

        // Some tests such as date-checking behave differently in Firefox and Chrome.
        int expectedFailedCases = "firefox".equals(TestProperties.BROWSER) ? 0 : 4;
        assertEquals(expectedFailedCases, failedCases);
        assertTrue(totalCases > 0);

        print("As expected, " + expectedFailedCases + " failed tests out of " + totalCases + " tests.");

        if (!TestProperties.isDevServer()) {
            return;
        }

        page.navigateTo(createUrl(Const.ViewURIs.JS_UNIT_TEST + "?coverage"));
        page.waitForCoverageVisibility();

        float coverage = page.getCoverage();

        print(coverage + "% of scripts covered");
    }

}
