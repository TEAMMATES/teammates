package teammates.test.cases.browsertests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.pageobjects.AppPage;

/**
 * Verifies that both timezone libraries, jodatime and momentjs are in sync.
 *
 * <p>Implemented as a browser test as both back-end and front-end methods are involved.
 */
public class TimezoneSyncerTest extends BaseUiTestCase {

    private AppPage page;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        loginAdmin();
        page = AppPage.getNewPageInstance(browser).navigateTo(createUrl(Const.ViewURIs.TIMEZONE));
    }

    @Test
    public void testAll() {
        Document pageSource = Jsoup.parse(page.getPageSource());
        assertEquals(pageSource.getElementById("jodatime").text().replace(" ", Const.EOL),
                     pageSource.getElementById("momentjs").text().replace(" ", Const.EOL));
    }

}
