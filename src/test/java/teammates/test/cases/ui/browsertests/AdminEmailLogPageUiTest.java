package teammates.test.cases.ui.browsertests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.AdminEmailLogPage;
import teammates.test.pageobjects.Browser;
import teammates.test.pageobjects.BrowserPool;

public class AdminEmailLogPageUiTest extends BaseUiTestCase {
    
    private static final int ADMIN_EMAIL_LOG_TABLE_NUM_COLUMNS = 3;
    
    private static Browser browser;
    private static AdminEmailLogPage emailLogPage;
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        browser = BrowserPool.getBrowser();
    }
    
    @Test
    public void testAll() {
        testContent();
        testFilterReference();
    }

    private void testFilterReference() {
        emailLogPage.clickReferenceButton();
        assertTrue(emailLogPage.isFilterReferenceVisible());
    }

    public void testContent() {
        
        ______TS("content: typical page");
        
        AppUrl logPageUrl = createUrl(Const.ActionURIs.ADMIN_EMAIL_LOG_PAGE);
        emailLogPage = loginAdminToPage(browser, logPageUrl, AdminEmailLogPage.class);
        emailLogPage.verifyIsCorrectPage();
        assertTrue(isEmailLogDataDisplayCorrect());
    }

    @AfterClass
    public static void classTearDown() {
        BrowserPool.release(browser);
    }

    /**
     * This method only checks if the email log data table are displayed correctly
     * i.e, table headers are correct
     * It does not test for the table content
     */
    private boolean isEmailLogDataDisplayCorrect() {
        return emailLogPage.isElementPresent(By.className("table")) && isEmailLogTableHeaderCorrect();
    }

    private boolean isEmailLogTableHeaderCorrect() {
        int numColumns = emailLogPage.getNumberOfColumnsFromDataTable(0); // 1 table
        
        if (numColumns != ADMIN_EMAIL_LOG_TABLE_NUM_COLUMNS) {
            return false;
        }
        
        List<String> expectedSessionTableHeaders = Arrays.asList("Receiver",
                                                                 "Subject",
                                                                 "Date");
        List<String> actualSessionTableHeaders = new ArrayList<String>();
        
        for (int i = 0; i < numColumns; i++) {
            actualSessionTableHeaders.add(emailLogPage.getHeaderValueFromDataTable(0, 0, i));
        }
        
        return actualSessionTableHeaders.equals(expectedSessionTableHeaders);
    }
}
