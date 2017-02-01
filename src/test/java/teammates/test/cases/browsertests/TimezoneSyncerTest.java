package teammates.test.cases.browsertests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.pageobjects.AppPage;

public class TimezoneSyncerTest extends BaseUiTestCase {
    
    private static AppPage page;
    
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
