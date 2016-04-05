package teammates.test.cases.ui.browsertests;

import static org.testng.AssertJUnit.assertTrue;

import java.io.IOException;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.test.pageobjects.AppPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/**
 * Loads all JavaScript unit tests (done in QUnit) into a browser window and
 * ensures all tests passed. This class is not using the PageObject pattern
 * because it is not a regular UI test.
 */
public class AllJsTests extends BaseUiTestCase{
    
    private static Browser browser;
    
    @BeforeClass
    public static void setUp() throws IOException {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
        AppPage.getNewPageInstance(browser).navigateTo(createLocalUrl("/allJsUnitTests.html"));
    }

    @Test
    public void executeJsTests() {
        String totalCasesXpathQuery = "//span[@class='total']",
               failedCasesXpathQuery = "//span[@class='failed']";
        
        int totalCases = Integer.parseInt(browser.driver
                .findElement(By.xpath(totalCasesXpathQuery)).getText());
        int failedCases = Integer.parseInt(browser.driver
                .findElement(By.xpath(failedCasesXpathQuery)).getText());
        
        print("Executed "+totalCases+" JavaScript Unit tests...");

        assertTrue(failedCases == 0);
        assertTrue(totalCases != 0);
        
        print("As expected, 0 failed tests out of " + totalCases + " tests.");

    }

    @AfterClass
    public static void tearDown() {
        BrowserPool.release(browser);
    }
}
