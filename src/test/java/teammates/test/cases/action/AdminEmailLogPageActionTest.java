package teammates.test.cases.action;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.gson.reflect.TypeToken;

import teammates.common.util.Const;
import teammates.common.util.EmailLogEntry;
import teammates.common.util.JsonUtils;
import teammates.common.util.TimeHelper;
import teammates.test.driver.FileHelper;
import teammates.test.driver.TestProperties;
import teammates.test.driver.TimeHelperExtension;
import teammates.ui.controller.AdminEmailLogPageAction;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminEmailLogPageData;
import teammates.ui.pagedata.PageData;
import teammates.ui.template.AdminEmailTableRow;

/**
 * SUT: {@link AdminEmailLogPageAction}.
 *
 * <p>The test will inject predefined GAE logs using {@link teammates.test.driver.GaeSimulation} and
 * then test the correct execution of the action.
 *
 * <p>Logs will be injected to GAE with time relative to now. Typically, today, yesterday
 * and two days ago are the time. It is possible that when the test is run at
 * midnight(around 12:00 PM) in UTC, some logs that belong to today will become
 * yesterday's logs as each log occupies a period of time. However, this situation
 * can be solved by rerunning the test cases at a different time.
 */
public class AdminEmailLogPageActionTest extends BaseActionTest {

    // The test data will be a List<List<String>>. These constants are indexes
    // for the list of log messages in the outer list.
    private static final int LOG_MESSAGE_INDEX_TODAY = 0;
    private static final int LOG_MESSAGE_INDEX_YESTERDAY = 1;
    private static final int LOG_MESSAGE_INDEX_TWO_DAYS_AGO = 2;
    private static final int LOG_MESSAGE_INDEX_MANY_LOGS = 3;

    // In the case of many logs, the query will first look at logs within 2 hours before now,
    // if the number of logs exceeds 50, it will stop the query and return logs from the first 2 hours.
    // 130 seconds is chosen so that it will be around 50 logs within 2 hours before now.
    private static final int LOG_MESSAGE_INTERVAL_MANY_LOGS = 130;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final Instant TODAY = Instant.now();
    private static final Instant YESTERDAY = TimeHelper.getInstantDaysOffsetFromNow(-1);
    private static final Instant TWO_DAYS_AGO = TimeHelper.getInstantDaysOffsetFromNow(-2);

    private List<List<String>> logMessages;

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_EMAIL_LOG_PAGE;
    }

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
        loadLogMessages();
    }

    private void loadLogMessages() {
        try {
            String pathToJsonFile = TestProperties.TEST_DATA_FOLDER + "/typicalEmailLogMessage.json";
            String jsonString = FileHelper.readFile(pathToJsonFile);
            Type listType = new TypeToken<List<List<String>>>(){}.getType();

            logMessages = JsonUtils.fromJson(jsonString, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String formatInstantAsDateInAdminTimeZone(Instant instant) {
        return DATE_FORMATTER.format(
                TimeHelper.convertInstantToLocalDateTime(instant, Const.SystemParams.ADMIN_TIME_ZONE));
    }

    @Override
    public void testExecuteAndPostProcess() {
        // See each independent test case
    }

    @BeforeGroups("typicalEmailLogs")
    public void removeAndRestoreLogMessage() {
        gaeSimulation.loginAsAdmin("admin");
        gaeSimulation.clearLogs();

        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_TWO_DAYS_AGO), TWO_DAYS_AGO.toEpochMilli());
        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_YESTERDAY), YESTERDAY.toEpochMilli());
        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_TODAY), TODAY.toEpochMilli());
    }

    @Test(groups = "typicalEmailLogs")
    public void filterQuery_invalidQuery_defaultSearchPerformed() {
        int[][] expected = new int[][] { {0, 1, 2, 3} };

        String query = "unknown";
        verifyActionResult(expected, "filterQuery", query);

        query = "";
        verifyActionResult(expected, "filterQuery", query);

        query = "info";
        verifyActionResult(expected, "filterQuery", query);

        query = "info:";
        verifyActionResult(expected, "filterQuery", query);

        query = "receiver unknown_connector info:content";
        verifyActionResult(expected, "filterQuery", query);

        query = "unknown:subject1 | info:keyword1";
        verifyActionResult(expected, "filterQuery", query);

        // invalid filterQuery with showing testing data
        expected = new int[][] { {0, 1, 2, 3, 4, 5, 6} };
        query = "information:unknown";
        verifyActionResult(expected, "filterQuery", query, "all", "true");
    }

    @Test(groups = "typicalEmailLogs")
    public void filterLogs_withUrlParams_showTestData() {
        // Besides filterQuery, logs can also be filtered by appending URL parameters (`all`)
        // to decide whether to show logs from test data.

        // not show test data, default search
        int[][] expected = new int[][] { {0, 1, 2, 3} };
        verifyActionResult(expected);

        // show test data, show all email log
        expected = new int[][] { {0, 1, 2, 3, 4, 5, 6} };
        verifyActionResult(expected, "all", "true");
    }

    @Test(groups = "typicalEmailLogs")
    public void filterQuery_validQuery() {
        // after
        int[][] expected = new int[][] { {0, 1, 2, 3}, {0, 1} };
        String query = String.format(" after:%s", formatInstantAsDateInAdminTimeZone(YESTERDAY));
        verifyActionResult(expected, "filterQuery", query);

        // before
        expected = new int[][] { {}, {}, {0, 1} };
        query = String.format("before :%s", formatInstantAsDateInAdminTimeZone(TWO_DAYS_AGO));
        verifyActionResult(expected, "filterQuery", query);

        // after-before
        expected = new int[][] { {}, {0, 1}, {0, 1} };
        query = String.format("after: %s  and  before:%s",
                formatInstantAsDateInAdminTimeZone(TWO_DAYS_AGO), formatInstantAsDateInAdminTimeZone(YESTERDAY));
        verifyActionResult(expected, "filterQuery", query);

        expected = new int[][] { {0, 1, 2, 3}, {0, 1}, {0, 1} };
        query = String.format("after : %s | before: %s ",
                formatInstantAsDateInAdminTimeZone(TWO_DAYS_AGO), formatInstantAsDateInAdminTimeZone(TODAY));
        verifyActionResult(expected, "filterQuery", query);

        // receiver
        query = "receiver: email1@email.com ";
        expected = new int[][] { {1, 2} };
        verifyActionResult(expected, "filterQuery", query);

        // subject
        query = String.format("  subject:subject2   | before:%s  ", formatInstantAsDateInAdminTimeZone(YESTERDAY));
        expected = new int[][] { {}, {1} };
        verifyActionResult(expected, "filterQuery", query);

        // info
        query = "info: keyword3";
        expected = new int[][] { {1, 3} };
        verifyActionResult(expected, "filterQuery", query);

        query = String.format("info:keyword4   |   after:%s", formatInstantAsDateInAdminTimeZone(YESTERDAY));
        expected = new int[][] { {2}, {0} };
        verifyActionResult(expected, "filterQuery", query);
    }

    @Test(groups = "typicalEmailLogs")
    public void filterQueryAndUrlParams_combinationWithEachOther_querySuccessful() {

        int[][] expected = new int[][] { {0, 3, 4, 6}, {1, 2} };
        String query = String.format("info:keyword1 | after:%s", formatInstantAsDateInAdminTimeZone(YESTERDAY));
        verifyActionResult(expected, "filterQuery", query, "all", "true");

        expected = new int[][] { {}, {}, {1, 2} };
        query = String.format("subject:subject1 | before:%s", formatInstantAsDateInAdminTimeZone(TWO_DAYS_AGO));
        verifyActionResult(expected, "filterQuery", query, "all", "true");
    }

    @Test(groups = "typicalEmailLogs")
    public void filterQuery_queryDifferentAppVersions_querySuccessful() {
        // version query is controlled by GAE itself
        // so there is no need to write comprehensive test case for it

        int[][] expected = new int[][] { {} };
        String query = "version:2";
        verifyActionResult(expected, "filterQuery", query);

        expected = new int[][] { {0, 1, 2, 3} };
        query = "version:2, 1";
        verifyActionResult(expected, "filterQuery", query);
    }

    @Test(groups = "typicalEmailLogs")
    public void statusMessage_validQuery_generatedCorrectly() {

        // test statusMessage for default search
        AdminEmailLogPageAction action = getAction();
        String statusMessage = getShowPageResult(action).getStatusMessage();
        verifyStatusMessage(statusMessage, 8);

        // test statusMessage with `after` filter
        String query = "after:" + formatInstantAsDateInAdminTimeZone(TWO_DAYS_AGO);
        action = getAction("filterQuery", query);
        statusMessage = getShowPageResult(action).getStatusMessage();
        verifyStatusMessage(statusMessage, 16);
    }

    @Test(groups = "typicalEmailLogs")
    public void continueSearch_searchFromDifferentTime_searchCorrectly() {
        Instant threeDaysAgo = TimeHelper.getInstantDaysOffsetFromNow(-3);

        // default continue search
        int[][] expected = new int[][] { {}, {0, 1} };
        String[] params = new String[] {"offset", String.valueOf(YESTERDAY.toEpochMilli())};
        verifyContinueSearch(params, expected, 5);

        // continue search and no more logs
        expected = new int[][] {};
        params = new String[] {"offset", String.valueOf(threeDaysAgo.toEpochMilli())};
        verifyContinueSearch(params, expected, 0);

        // continue search with some filters
        expected = new int[][] { {}, {1, 2} };
        params = new String[] {
                "offset", String.valueOf(YESTERDAY.toEpochMilli()),
                "filterQuery", "subject:subject2", "all", "true"
        };
        verifyContinueSearch(params, expected, 5);

        // when `after` is present, will do search between `after` and `offset`
        // This is important as if there are a lot of logs `after` certain Instant, the App
        // will only display the first 50 logs. Continue search will help to get more logs.
        expected = new int[][] { {}, {0, 1}, {0, 1} };
        params = new String[] {
                "offset", String.valueOf(YESTERDAY.toEpochMilli()),
                "filterQuery", String.format("after:%s", formatInstantAsDateInAdminTimeZone(threeDaysAgo))
        };
        verifyContinueSearch(params, expected, 8);

        // `before` present, search with 1 day interval
        expected = new int[][] { {}, {}, {0, 1} };
        params = new String[] {
                "offset", String.valueOf(TimeHelperExtension.getEndOfTheDayOffsetNowInAdminTimeZone(-2).toEpochMilli()),
                "filterQuery", String.format("before:%s", formatInstantAsDateInAdminTimeZone(YESTERDAY))
        };
        verifyContinueSearch(params, expected, 3);
    }

    @BeforeGroups("manyEmailLogs")
    public void removeAndRestoreManyLogs() {
        gaeSimulation.loginAsAdmin("admin");
        gaeSimulation.clearLogs();

        insertLogMessageAtTimeWithInterval(logMessages.get(LOG_MESSAGE_INDEX_MANY_LOGS),
                TODAY.toEpochMilli(), LOG_MESSAGE_INTERVAL_MANY_LOGS);
    }

    // The two test groups should have different 'priority' so that they can run separately
    // as they depend on different sets of log messages
    @Test(groups = "manyEmailLogs", priority = 2)
    public void statusMessageAndContinueSearch_withManyLogs_searchCorrectly() {
        Instant today = Instant.now();

        // default search will stop at #logs around 50
        AdminEmailLogPageAction action = getAction();
        ShowPageResult result = getShowPageResult(action);
        verifyManyLogs(55, 0, 54, result.data, result.getStatusMessage());

        // continue search will get next #logs around 50
        long nextSearch = today.toEpochMilli() - 56 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("offset", String.valueOf(nextSearch));
        result = getShowPageResult(action);
        verifyManyLogs(56, 55, 110, result.data, result.getStatusMessage());

        // continue search will get logs until no logs
        nextSearch = today.toEpochMilli() - 112 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("offset", String.valueOf(nextSearch));
        result = getShowPageResult(action);
        verifyManyLogs(39, 111, 149, result.data, result.getStatusMessage());

        // default search with filter stop at #logs around 50
        action = getAction("filterQuery", "receiver:email1@email.com");
        result = getShowPageResult(action);
        verifyManyLogs(55, 0, 54, result.data, result.getStatusMessage());

        // continue search with filter will get logs until no logs
        nextSearch = today.toEpochMilli() - 56 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("filterQuery", "receiver:email1@email.com", "offset", String.valueOf(nextSearch));
        result = getShowPageResult(action);
        verifyManyLogs(95, 55, 69, result.data, result.getStatusMessage());
    }

    private void verifyContinueSearch(String[] params, int[][] expected, int totalLogs) {
        AdminEmailLogPageAction action = getAction(params);
        ShowPageResult result = getShowPageResult(action);
        AdminEmailLogPageData pageData = (AdminEmailLogPageData) result.data;
        verifyStatusMessage(result.getStatusMessage(), totalLogs);
        verifyLogs(expected, getLogsFromLogTemplateRows(pageData.getLogs()));
    }

    private void verifyActionResult(int[][] expectedLogs, String... params) {
        AdminEmailLogPageAction action = getAction(params);
        ShowPageResult result = getShowPageResult(action);
        AdminEmailLogPageData pageData = (AdminEmailLogPageData) result.data;
        List<EmailLogEntry> actualLogs = getLogsFromLogTemplateRows(pageData.getLogs());
        verifyLogs(expectedLogs, actualLogs);
    }

    /**
     * Verifies actualLogs contains expectedLogs.
     *
     * <p>expectedLogs is a 2D array, the outer indices correspond to {@link #LOG_MESSAGE_INDEX_TODAY}
     * {@link #LOG_MESSAGE_INDEX_YESTERDAY} and {@link #LOG_MESSAGE_INDEX_TWO_DAYS_AGO}, the inner indices for
     * every {@code LOG_MESSAGE_INDEX_*} correspond to the orders in the test data.
     */
    private void verifyLogs(int[][] expectedLogs, List<EmailLogEntry> actualLogs) {
        List<String> expectedMsgs = generateExpectedMsgFrom(expectedLogs);

        assertEquals(expectedMsgs.size(), actualLogs.size());
        for (int i = 0; i < expectedMsgs.size(); i++) {
            assertEquals(expectedMsgs.get(i), actualLogs.get(i).generateLogMessage());
        }
    }

    private List<String> generateExpectedMsgFrom(int[][] expectedLogs) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < expectedLogs.length; i++) {
            for (int j = 0; j < expectedLogs[i].length; j++) {
                result.add(logMessages.get(i).get(expectedLogs[i][j]));
            }
        }
        return result;
    }

    private void verifyStatusMessage(String message, int totalLogs) {
        assertTrue(message, message.contains("Total Logs gone through in last search: " + totalLogs));
    }

    private void verifyManyLogs(int totalLogs, int first, int last,
            PageData pageData, String statusMessage) {
        List<EmailLogEntry> actualLogs =
                getLogsFromLogTemplateRows(((AdminEmailLogPageData) pageData).getLogs());

        verifyLogsIdInRange(actualLogs, first, last);
        verifyStatusMessage(statusMessage, totalLogs);
    }

    private void verifyLogsIdInRange(List<EmailLogEntry> actualLogs, int first, int last) {
        assertEquals(last - first + 1, actualLogs.size());
        for (int i = 0; i < actualLogs.size(); i++) {
            assertTrue(actualLogs.get(i).getContent().contains(String.format("id4%02d", first + i)));
        }
    }

    private void insertLogMessagesAtTime(List<String> msgList, long timeMillis) {
        insertLogMessageAtTimeWithInterval(msgList, timeMillis, 1);
    }

    private void insertLogMessageAtTimeWithInterval(List<String> msgList, long timeMillis, int intervalInSecond) {
        int levelInfo = 1;
        long logTimeInMillis = timeMillis - msgList.size() * intervalInSecond * 1000;
        for (int i = msgList.size() - 1; i >= 0; i--) {
            createTestDataRequestInfoAtTime(logTimeInMillis);
            gaeSimulation.addAppLogLine(String.valueOf(logTimeInMillis), logTimeInMillis * 1000,
                    levelInfo, msgList.get(i));
            logTimeInMillis += intervalInSecond * 1000;
        }
    }

    private void createTestDataRequestInfoAtTime(long timeMillis) {
        String testStr = "TEST";
        String defaultVersion = "1";
        gaeSimulation.addLogRequestInfo(testStr, defaultVersion, String.valueOf(timeMillis), testStr,
                testStr, timeMillis * 1000, timeMillis * 1000, testStr, testStr, testStr, testStr,
                true, 200, testStr);
    }

    private List<EmailLogEntry> getLogsFromLogTemplateRows(List<AdminEmailTableRow> rows) {
        List<EmailLogEntry> logs = new ArrayList<>();
        for (AdminEmailTableRow row : rows) {
            logs.add(row.getLogEntry());
        }
        return logs;
    }

    @Override
    protected AdminEmailLogPageAction getAction(String... params) {
        return (AdminEmailLogPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[] {};
        verifyOnlyAdminsCanAccess(submissionParams);
    }

}
