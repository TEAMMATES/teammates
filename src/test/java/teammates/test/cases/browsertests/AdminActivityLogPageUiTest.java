package teammates.test.cases.browsertests;

import java.time.Instant;

import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.test.pageobjects.AdminActivityLogPage;
import teammates.test.pageobjects.AdminSearchPage;

/**
 * SUT: {@link Const.ActionURIs#ADMIN_ACTIVITY_LOG_PAGE}.
 */
public class AdminActivityLogPageUiTest extends BaseUiTestCase {

    private AdminActivityLogPage logPage;

    @Override
    protected void prepareTestData() {
        // no test data used in this test
    }

    @Test
    public void testAll() throws Exception {
        testContent();
        testUserTimezone();
        testReference();
        testViewActionsLink();
        testInputValidation();
        testSanitization();
    }

    private void testUserTimezone() {
        logPage.clickUserTimezoneAtFirstRow();
        logPage.waitForAjaxLoaderGifToDisappear();
        assertTrue(logPage.isUserTimezoneAtFirstRowClicked());
    }

    private void testReference() {
        ______TS("content: show reference");
        logPage.clickReferenceButton();
        assertTrue(logPage.isFilterReferenceVisible());

    }

    private void testContent() throws Exception {

        ______TS("content: typical page");

        AppUrl logPageUrl = createUrl(Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE);
        logPage = loginAdminToPage(logPageUrl, AdminActivityLogPage.class);
        logPage.verifyIsCorrectPage();

        ______TS("content: navigate to other pages to get some logs");
        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_HOME_PAGE));
        logPage.waitForPageToLoad();
        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_ACCOUNT_MANAGEMENT_PAGE));
        logPage.waitForPageToLoad();
        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_SEARCH_PAGE));
        logPage.waitForPageToLoad();
        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE));
        logPage.waitForPageToLoad();
        assertNotNull(logPage.getFirstActivityLogRow());
        assertTrue(logPage.isLogsTableVisible());
        assertEquals(2, logPage.getNumberOfTableHeaders());

        ______TS("content: ensure default search period is not more than one day");
        Instant yesterday = TimeHelper.getInstantDaysOffsetFromNow(-1);
        assertTrue(logPage.getDateOfEarliestLog().isAfter(yesterday));

        ______TS("content: show the earliest log's date in both Admin Time Zone and local Time Zone");
        String statusMessageText = logPage.getTextsForAllStatusMessagesToUser().get(0);
        assertTrue(statusMessageText.contains("The earliest log entry checked on"));
        assertTrue(statusMessageText.contains("in Admin Time Zone"));
        assertTrue(statusMessageText.contains("in Local Time Zone")
                   || statusMessageText.contains("Local Time Unavailable"));
    }

    private void testViewActionsLink() {

        ______TS("Link: recent actions link");

        try {
            String expectedPersonInfo = logPage.getPersonInfoOfFirstEntry();
            logPage.clickViewActionsButtonOfFirstEntry();
            String actualPersonInfo = logPage.getFilterBoxString();
            assertEqualsIfQueryStringNotEmpty(expectedPersonInfo, actualPersonInfo);
        } catch (NoSuchElementException exceptionFromEmptyLogs) {
            /*
             * This can happen if this test is run right after the server is started.
             * In this case, no view actions can be done.
             */
            ignorePossibleException();
        } catch (IndexOutOfBoundsException exceptionFromInvisibleTmtLogs) {
            /*
             * This can happen if all the log entries are from test accounts
             * (i.e emails ending with .tmt) because they are invisible.
             * In this case, no view actions can be done.
             */
            ignorePossibleException();
        }
    }

    private void testInputValidation() {

        ______TS("invalid query format");

        logPage.fillQueryBoxWithText("this is a invalid query");
        logPage.clickSearchSubmitButton();

        assertEquals("Error with the query: Invalid format", logPage.getQueryMessage());

        ______TS("valid query format");

        logPage.fillQueryBoxWithText("role:instructor");
        logPage.clickSearchSubmitButton();
        String statusMessageText = logPage.getTextsForAllStatusMessagesToUser().get(0);
        assertTrue(statusMessageText.contains("Total Logs gone through in last search:"));

    }

    private void testSanitization() {
        ______TS("safe against injection from admin search page");

        AdminSearchPage searchPageForInjection = logPage
                .navigateTo(createUrl(Const.ActionURIs.ADMIN_SEARCH_PAGE))
                .changePageType(AdminSearchPage.class);

        String injectedScript = "Test Injected Script<script>alert('This is not good.');</script>";
        searchPageForInjection.inputSearchContent(injectedScript);
        searchPageForInjection.clickSearchButton();
        searchPageForInjection.waitForPageToLoad();

        logPage.navigateTo(createUrl(Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE));
        logPage.waitForPageToLoad();

        try {
            browser.driver.switchTo().alert();
            signalFailureToDetectException("Script managed to get injected");
        } catch (NoAlertPresentException e) {
            // this is what we expect, since we expect the script injection to fail
        }
    }

    private void assertEqualsIfQueryStringNotEmpty(String expected, String actual) {
        String emptyQuery = "person:";
        if (!expected.equals(emptyQuery)) {
            assertEquals(expected, actual);
        }
    }

}
