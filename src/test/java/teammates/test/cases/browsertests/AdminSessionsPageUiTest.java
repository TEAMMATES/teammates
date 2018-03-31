package teammates.test.cases.browsertests;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.openqa.selenium.By;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.test.pageobjects.AdminSessionsPage;

/**
 * SUT: {@link Const.ActionURIs#ADMIN_SESSIONS_PAGE}.
 */
public class AdminSessionsPageUiTest extends BaseUiTestCase {
    private AdminSessionsPage sessionsPage;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @BeforeClass
    public void classSetup() {
        browser.driver.manage().deleteAllCookies();
    }

    @Test
    public void testAll() {
        testContent();
    }

    private void testContent() {

        ______TS("content: typical page");

        AppUrl sessionsUrl = createUrl(Const.ActionURIs.ADMIN_SESSIONS_PAGE);
        sessionsPage = loginAdminToPage(sessionsUrl, AdminSessionsPage.class);
        By timeFramePanel = By.id("timeFramePanel");
        sessionsPage.waitForElementToDisappear(timeFramePanel);
        assertTrue(isSessionDataDisplayCorrect());

        ______TS("content: show filter");

        sessionsPage.clickDetailButton();
        sessionsPage.waitForElementVisibility(browser.driver.findElement(timeFramePanel));
        assertTrue(isSessionDataDisplayCorrect());

        ______TS("content: hide filter");

        sessionsPage.clickDetailButton();
        sessionsPage.waitForElementToDisappear(timeFramePanel);
        assertTrue(isSessionDataDisplayCorrect());

    }

    /**
     * This method only checks if the session data tables are displayed correctly
     * i.e, table headers are correct, and appropriate message is displayed if no
     * session data is present.
     * It does not test for the table content
     */
    private boolean isSessionDataDisplayCorrect() {
        if (sessionsPage.isElementPresent(By.className("data-table"))) {
            int numSessionDataTables = browser.driver.findElements(By.className("data-table")).size();
            for (int i = 0; i < numSessionDataTables; i++) {
                if (!isSessionTableHeaderCorrect(i)) {
                    return false;
                }
            }
            return true;
        }
        sessionsPage.waitForTextsForAllStatusMessagesToUserEquals("Currently No Ongoing Sessions");
        return true;

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
        List<String> actualSessionTableHeaders = new ArrayList<>();
        for (int i = 0; i < numColumns; i++) {
            actualSessionTableHeaders.add(sessionsPage.getHeaderValueFromDataTable(tableNum, 0, i));
        }
        return actualSessionTableHeaders.equals(expectedSessionTableHeaders);
    }
}
