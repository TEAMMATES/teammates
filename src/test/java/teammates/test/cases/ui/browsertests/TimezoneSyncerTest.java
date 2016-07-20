package teammates.test.cases.ui.browsertests;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.Const;
import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class TimezoneSyncerTest extends BaseUiTestCase {
    
    private static Browser browser;
    private static AppPage page;
    
    @BeforeClass
    public static void classSetUp() {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
        page = AppPage.getNewPageInstance(browser).navigateTo(createUrl("/timezone.jsp"));
    }
    
    @Test
    public void testAll() {
        Document pageSource = Jsoup.parse(page.getPageSource());
        assertEquals(pageSource.getElementById("jodatime").text().replace(" ", Const.EOL),
                     pageSource.getElementById("momentjs").text().replace(" ", Const.EOL));
    }
    
    @AfterClass
    public static void classTearDown() {
        printTestClassFooter();
        BrowserPool.release(browser);
    }
    
}
