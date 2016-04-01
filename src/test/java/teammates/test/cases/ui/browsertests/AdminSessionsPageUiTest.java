package teammates.test.cases.ui.browsertests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertFalse;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.AdminSessionsPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

/**
 * Covers the home page for admins.
 * SUT: {@link AdminSessionsPage}
 */
public class AdminSessionsPageUiTest extends BaseUiTestCase {
    private static Browser browser;
    AdminSessionsPage sessionsPage;
    
    @BeforeClass
    public static void classSetup() throws Exception {
        printTestClassHeader();      
        browser = BrowserPool.getBrowser();
        browser.driver.manage().deleteAllCookies();
    }
    
    @Test
    public void testAll() throws InvalidParametersException, EntityDoesNotExistException, Exception {
        testContent();
    }

    @AfterClass
    public static void classTearDown() throws Exception {
        BrowserPool.release(browser);
    }
    
    private void testContent() {
        
        ______TS("content: typical page");
        
        AppUrl sessionsUrl = createUrl(Const.ActionURIs.ADMIN_SESSIONS_PAGE);
        sessionsPage = loginAdminToPage(browser, sessionsUrl, AdminSessionsPage.class);
        assertFalse(isTimeFramePanelVisible());
        assertTrue(isSessionDataDisplayCorrect());
        
        ______TS("content: show filter");
        
        sessionsPage.clickDetailButton();
        assertTrue(isTimeFramePanelVisible());
        assertTrue(isSessionDataDisplayCorrect());
        
        ______TS("content: hide filter");
        
        sessionsPage.clickDetailButton();
        assertFalse(isTimeFramePanelVisible());
        assertTrue(isSessionDataDisplayCorrect());
        
    }
    
    private boolean isTimeFramePanelVisible() {
        return sessionsPage.isElementVisible(By.id("timeFramePanel"));
    }
    
    /**
     * This method only checks if the session data tables are displayed correctly
     * i.e, table headers are correct, and appropriate message is displayed if no
     * session data is present.
     * It does not test for the table content
     */
    private boolean isSessionDataDisplayCorrect() {
        if (sessionsPage.isElementPresent(By.className("dataTable"))) {
            int numSessionDataTables = browser.driver.findElements(By.className("dataTable")).size();
            for (int i = 0 ; i < numSessionDataTables ; i++) {
                if (!isSessionTableHeaderCorrect(i)) {
                    return false;
                }
            }
            return true;
        } else {     
            String statusMessage = sessionsPage.getStatus();
            return statusMessage.equals("Currently No Ongoing Sessions");
        }
        
    }
    
    private boolean isSessionTableHeaderCorrect(int tableNum) {
        int numColumns = sessionsPage.getNumberOfColumnsFromDataTable(tableNum);
        if (numColumns != 6) {
            return false;
        }
        List<String> expectedSessionTableHeaders = Arrays.asList("Status",
                                                               "[Course ID] Session Name  ",
                                                               "Response Rate",
                                                               "Start Time ",
                                                               "End Time ",
                                                               "Creator");
        List<String> actualSessionTableHeaders = new ArrayList<String>();
        for (int i = 0 ; i < numColumns ; i++) {
            actualSessionTableHeaders.add(sessionsPage.getHeaderValueFromDataTable(tableNum, 0, i));
        }
        return actualSessionTableHeaders.equals(expectedSessionTableHeaders);
    }
}


