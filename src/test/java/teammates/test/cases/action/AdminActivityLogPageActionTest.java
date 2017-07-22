package teammates.test.cases.action;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.testng.annotations.BeforeGroups;
import org.testng.annotations.Test;

import com.google.gson.reflect.TypeToken;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.common.util.TimeHelper;
import teammates.test.driver.FileHelper;
import teammates.test.driver.TestProperties;
import teammates.ui.controller.AdminActivityLogPageAction;
import teammates.ui.controller.AjaxResult;
import teammates.ui.controller.ShowPageResult;
import teammates.ui.pagedata.AdminActivityLogPageData;
import teammates.ui.pagedata.PageData;
import teammates.ui.template.AdminActivityLogTableRow;

/**
 * SUT: {@link AdminActivityLogPageAction}.
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
public class AdminActivityLogPageActionTest extends BaseActionTest {

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

    private List<List<String>> logMessages;

    private SimpleDateFormat formatterAdminTime;

    @Override
    protected String getActionUri() {
        return Const.ActionURIs.ADMIN_ACTIVITY_LOG_PAGE;
    }

    @Override
    protected void prepareTestData() {
        super.prepareTestData();
        loadLogMessages();
        initVariable();
    }

    private void loadLogMessages() {
        try {
            String pathToJsonFile = TestProperties.TEST_DATA_FOLDER + "/typicalLogMessage.json";
            String jsonString = FileHelper.readFile(pathToJsonFile);
            Type listType = new TypeToken<List<List<String>>>(){}.getType();

            logMessages = JsonUtils.fromJson(jsonString, listType);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initVariable() {
        formatterAdminTime = new SimpleDateFormat("dd/MM/yy");
        formatterAdminTime.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));
    }

    @Override
    public void testExecuteAndPostProcess() {
        // See each independent test case
    }

    @BeforeGroups("typicalActivityLogs")
    public void removeAndRestoreLogMessage() {
        gaeSimulation.loginAsAdmin("admin");
        gaeSimulation.clearLogs();

        Date twoDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-2);
        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_TWO_DAYS_AGO), twoDaysAgo.getTime());
        Date yesterday = TimeHelper.getDateOffsetToCurrentTime(-1);
        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_YESTERDAY), yesterday.getTime());
        Date today = TimeHelper.getDateOffsetToCurrentTime(0);
        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_TODAY), today.getTime());
    }

    @Test(groups = "typicalActivityLogs")
    public void filterQuery_invalidQuery_defaultSearchPerformed() {
        int[][] expected = new int[][]{{0, 1, 3, 4, 5}};
        String query = "unknown";
        verifyActionResult(expected, "filterQuery", query);
        query = "";
        verifyActionResult(expected, "filterQuery", query);
        query = "info";
        verifyActionResult(expected, "filterQuery", query);
        query = "info:";
        verifyActionResult(expected, "filterQuery", query);
        query = "request:servlet3 unknown_connector role:Student";
        verifyActionResult(expected, "filterQuery", query);
        query = "unknown:servlet3 | role:Student";
        verifyActionResult(expected, "filterQuery", query);

        // invalid filterQuery with showing testing data
        expected = new int[][]{{0, 1, 2, 3, 4, 5, 6}};
        query = "information:unkown";
        verifyActionResult(expected, "filterQuery", query, "testdata", "true");
    }

    @Test(groups = "typicalActivityLogs")
    public void filterLogs_withUrlParams_showTestDataAndExcludedUriAccordingly() {
        // Besides filterQuery, logs can also be filtered by appending URL parameters (`testdata` or `all`)
        // to decide whether to show logs from test data or log contains excluded Uri.

        // not show test data, not show excluded URI, default search
        int[][] expected = new int[][]{{0, 1, 3, 4, 5}};
        verifyActionResult(expected);

        // show test data, not show excluded URI
        expected = new int[][]{{0, 1, 2, 3, 4, 5, 6}};
        verifyActionResult(expected, "testdata", "true");

        // not show test data, show excluded URI
        expected = new int[][]{{0, 1, 3, 4, 5, 7, 8}};
        verifyActionResult(expected, "all", "true");

        // show everything
        expected = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7, 8}};
        verifyActionResult(expected, "testdata", "true", "all", "true");
    }

    @Test(groups = "typicalActivityLogs")
    public void filterQuery_validQuery() {
        // from
        int[][] expected = new int[][]{{0, 1, 3, 4, 5}, {0, 1, 2}};
        Date yesterday = TimeHelper.getDateOffsetToCurrentTime(-1);
        String query = String.format(" from:%s", formatterAdminTime.format(yesterday));
        verifyActionResult(expected, "filterQuery", query);

        // to
        expected = new int[][]{{}, {}, {0, 1}};
        Date twoDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-2);
        query = String.format("to :%s", formatterAdminTime.format(twoDaysAgo));
        verifyActionResult(expected, "filterQuery", query);

        // from-to
        expected = new int[][]{{}, {0, 1, 2}, {0, 1}};
        query = String.format("from: %s  and  to:%s",
                formatterAdminTime.format(twoDaysAgo), formatterAdminTime.format(yesterday));
        verifyActionResult(expected, "filterQuery", query);

        Date today = TimeHelper.getDateOffsetToCurrentTime(0);
        expected = new int[][]{{0, 1, 3, 4, 5}, {0, 1, 2}, {0, 1}};
        query = String.format("from : %s | to: %s ",
                formatterAdminTime.format(twoDaysAgo), formatterAdminTime.format(today));
        verifyActionResult(expected, "filterQuery", query);

        // person: name
        query = "person: Name1 ";
        expected = new int[][]{{0, 1, 3}};
        verifyActionResult(expected, "filterQuery", query);

        // person: googleId
        query = String.format("  person:id1@google.com   | to:%s  ", formatterAdminTime.format(yesterday));
        expected = new int[][]{{}, {0, 1}};
        verifyActionResult(expected, "filterQuery", query);

        // person: email
        query = String.format("person:  email2@email.com | from:%s | to:%s",
                formatterAdminTime.format(twoDaysAgo), formatterAdminTime.format(yesterday));
        expected = new int[][]{{}, {2}, {0, 1}};
        verifyActionResult(expected, "filterQuery", query);

        // role
        query = "role:  Admin | person  :id1@google.com.sg";
        expected = new int[][]{{1}};
        verifyActionResult(expected, "filterQuery", query);

        // request
        query = "request  :servlet3 | role:Student";
        expected = new int[][]{{4}};
        verifyActionResult(expected, "filterQuery", query);

        // response
        query = "response:action1 | request:servlet1 ";
        expected = new int[][]{{0}};
        verifyActionResult(expected, "filterQuery", query);

        // time
        query = "    time :50";
        expected = new int[][]{{4, 5}};
        verifyActionResult(expected, "filterQuery", query);

        // info
        query = "info: keyword1";
        expected = new int[][]{{0, 3, 5}};
        verifyActionResult(expected, "filterQuery", query);

        query = String.format("info:keyword2   |   from:%s", formatterAdminTime.format(yesterday));
        expected = new int[][]{{0, 1, 4}, {0, 1, 2}};
        verifyActionResult(expected, "filterQuery", query);

        // id
        expected = new int[][]{{2}};
        query = "id:id02   ";
        verifyActionResult(expected, "testdata", "true", "filterQuery", query);
    }

    @Test(groups = "typicalActivityLogs")
    public void filterQueryAndUrlParams_combinationWithEachOther_querySuccessful() {
        Date today = TimeHelper.getDateOffsetToCurrentTime(0);
        Date twoDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-2);

        // filterQuery with showing all URI
        int[][] expected = new int[][]{{0, 3, 5, 7, 8}};
        String query = "info:keyword1";
        verifyActionResult(expected, "filterQuery", query, "all", "true");

        // another filterQuery with showing all URI
        expected = new int[][]{{0, 1, 3, 7, 8}, {0, 1, 4}, {3}};
        query = String.format("person:Name1 | from:%s and to:%s",
                formatterAdminTime.format(twoDaysAgo), formatterAdminTime.format(today));
        verifyActionResult(expected, "filterQuery", query, "all", "true");

        // filterQuery with showing test data
        expected = new int[][]{{0, 1, 2}};
        query = "role:Admin";
        verifyActionResult(expected, "filterQuery", query, "testdata", "true");

        // filterQuery with showing everything
        expected = new int[][]{{2, 4, 5, 6, 7}, {1, 3, 4}, {0, 1, 2}};
        query = String.format("time:50 | from:%s and to:%s",
                formatterAdminTime.format(twoDaysAgo), formatterAdminTime.format(today));
        verifyActionResult(expected, "filterQuery", query, "testdata", "true", "all", "true");
    }

    @Test(groups = "typicalActivityLogs")
    public void filterQuery_queryDifferentAppVersions_querySuccessful() {
        // version query is controlled by GAE itself
        // so there is no need to write comprehensive test case for it

        int[][] expected = new int[][]{{}};
        String query = "version:2";
        verifyActionResult(expected, "filterQuery", query);

        expected = new int[][]{{0, 1, 3, 4, 5}};
        query = "version:2, 1";
        verifyActionResult(expected, "filterQuery", query);
    }

    @Test(groups = "typicalActivityLogs")
    public void statusMessage_validQuery_generatedCorrectly() {
        Date yesterday = TimeHelper.getDateOffsetToCurrentTime(-1);

        // test statusMessage for default search
        AdminActivityLogPageAction action = getAction();
        String statusMessage = getShowPageResult(action).getStatusMessage();
        verifyStatusMessage(statusMessage, 11, 5, yesterday);
        verifyLocalTimeInStatusMessage(statusMessage, yesterday, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);

        // test statusMessage with filterQuery
        String query = "person:idOfInstructor1OfCourse1";
        action = getAction("filterQuery", query);
        statusMessage = getShowPageResult(action).getStatusMessage();
        verifyStatusMessage(statusMessage, 11, 1, yesterday);

        // test statusMessage with `to`
        query = "to:" + formatterAdminTime.format(yesterday);
        action = getAction("filterQuery", query);
        Calendar toDate = adminTimeZoneToUtc(getEndOfTheDayOffsetNowInAdminTimeZone(-2));
        statusMessage = getShowPageResult(action).getStatusMessage();
        verifyStatusMessage(statusMessage, 6, 3, toDate.getTime());
        verifyLocalTimeInStatusMessage(statusMessage, toDate.getTime(), Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);

        // test statusMessage with `from`
        query = "from:" + formatterAdminTime.format(yesterday);
        action = getAction("filterQuery", query);
        Calendar fromDate = adminTimeZoneToUtc(getBeginOfTheDayOffsetNowInAdminTimeZone(-1));
        statusMessage = getShowPageResult(action).getStatusMessage();
        verifyStatusMessage(statusMessage, 17, 8, fromDate.getTime());
        verifyLocalTimeInStatusMessage(statusMessage, fromDate.getTime(), Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
    }

    @Test(groups = "typicalActivityLogs")
    public void loadingLocalTimeAjaxQuery_validAndInvalidInputs_returnCorrectly() {
        Calendar now = TimeHelper.now(Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // Unknown
        String failureMsg = "Local Time Unavailable";
        verifyLoadingLocalTimeAjaxResult(failureMsg, "Unregistered", "Unknown", now.getTimeInMillis());
        verifyLoadingLocalTimeAjaxResult(failureMsg, "Instructor", "instructorWithoutCourses", now.getTimeInMillis());
        verifyLoadingLocalTimeAjaxResult(failureMsg, "Student", "student1InUnregisteredCourse", now.getTimeInMillis());
        verifyLoadingLocalTimeAjaxResult(failureMsg, "Unregistered:unregisteredCourse",
                "Unregistered", now.getTimeInMillis());

        // Role: Admin
        verifyLoadingLocalTimeAjaxResult(sdf.format(now.getTime()), "Admin", "admin", now.getTimeInMillis());
        verifyLoadingLocalTimeAjaxResult(sdf.format(now.getTime()), "Student(M)", "admin", now.getTimeInMillis());

        // Role: Instructor
        verifyLoadingLocalTimeAjaxResult(sdf.format(TimeHelper.convertToUserTimeZone(now, -6).getTime()),
                "Instructor", "idOfInstructor1OfCourse1", now.getTimeInMillis());

        // Role: Student
        verifyLoadingLocalTimeAjaxResult(sdf.format(TimeHelper.convertToUserTimeZone(now, -8).getTime()),
                "Student", "student1InArchivedCourse", now.getTimeInMillis());

        // Role: Unregistered:idOfTypicalCourse1
        verifyLoadingLocalTimeAjaxResult(sdf.format(TimeHelper.convertToUserTimeZone(now, -6).getTime()),
                "Unregistered:idOfTypicalCourse1", "Unregistered", now.getTimeInMillis());
    }

    @Test(groups = "typicalActivityLogs")
    public void continueSearch_searchFromDifferentTime_searchCorrectly() {
        Date yesterday = TimeHelper.getDateOffsetToCurrentTime(-1);
        Date twoDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-2);
        Date threeDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-3);
        Date fourDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-4);

        // default continue search
        int[][] expected = new int[][]{{}, {0, 1, 2}};
        String[] params = new String[] {"searchTimeOffset", String.valueOf(yesterday.getTime())};
        verifyContinueSearch(params, expected, 6, 3, twoDaysAgo);

        // continue search and no more logs
        expected = new int[][]{};
        params = new String[] {"searchTimeOffset", String.valueOf(threeDaysAgo.getTime())};
        verifyContinueSearch(params, expected, 0, 0, fourDaysAgo);

        // with some filters
        expected = new int[][]{{}, {0, 3}};
        params = new String[] {"searchTimeOffset", String.valueOf(yesterday.getTime()),
                "filterQuery", "info:keyword1", "testdata", "true"};
        verifyContinueSearch(params, expected, 6, 2, twoDaysAgo);

        // when `from` is present, will not do continue search
        expected = new int[][]{{0, 1, 3, 4, 5}, {0, 1, 2}};
        params = new String[] {"searchTimeOffset", String.valueOf(yesterday.getTime()),
                "filterQuery", String.format("from:%s", formatterAdminTime.format(yesterday))};
        Calendar yesterdayBegin = adminTimeZoneToUtc(getBeginOfTheDayOffsetNowInAdminTimeZone(-1));
        verifyContinueSearch(params, expected, 17, 8, yesterdayBegin.getTime());

        // `to` present, search with 1 day interval
        expected = new int[][]{{}, {}, {0, 1}};
        Calendar toDate = adminTimeZoneToUtc(getEndOfTheDayOffsetNowInAdminTimeZone(-2));
        params = new String[] {"searchTimeOffset", String.valueOf(toDate.getTimeInMillis()),
                "filterQuery", String.format("to:%s", formatterAdminTime.format(yesterday))};
        toDate = adminTimeZoneToUtc(getEndOfTheDayOffsetNowInAdminTimeZone(-3));
        verifyContinueSearch(params, expected, 4, 2, toDate.getTime());

    }

    @BeforeGroups("manyActivityLogs")
    public void removeAndRestoreManyLogs() {
        gaeSimulation.loginAsAdmin("admin");
        gaeSimulation.clearLogs();

        Date today = TimeHelper.getDateOffsetToCurrentTime(0);
        insertLogMessageAtTimeWithInterval(logMessages.get(LOG_MESSAGE_INDEX_MANY_LOGS),
                today.getTime(), LOG_MESSAGE_INTERVAL_MANY_LOGS);
    }

    // The two test groups should have different 'priority' so that they can run separately
    // as they depend on different sets of log messages
    @Test(groups = "manyActivityLogs", priority = 2)
    public void statusMessageAndContinueSearch_withManyLogs_searchCorrectly() {
        Date today = TimeHelper.getDateOffsetToCurrentTime(0);

        // default search will stop at #logs around 50
        AdminActivityLogPageAction action = getAction();
        ShowPageResult result = getShowPageResult(action);
        Date earliestDateInUtc = new Date(today.getTime() - 54 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000);
        verifyManyLogs(55, 0, 54, result.data, result.getStatusMessage(), earliestDateInUtc);

        // continue search will get next #logs around 50
        long nextSearch = today.getTime() - 56 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("searchTimeOffset", String.valueOf(nextSearch));
        result = getShowPageResult(action);
        earliestDateInUtc = new Date(today.getTime() - 110 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000);
        verifyManyLogs(56, 55, 110, result.data, result.getStatusMessage(), earliestDateInUtc);

        // continue search will get logs until no logs
        nextSearch = today.getTime() - 112 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("searchTimeOffset", String.valueOf(nextSearch));
        result = getShowPageResult(action);
        earliestDateInUtc = new Date(nextSearch - 24 * 60 * 60 * 1000);
        verifyManyLogs(39, 111, 149, result.data, result.getStatusMessage(), earliestDateInUtc);

        // default search with filter stop at #logs around 50
        action = getAction("filterQuery", "request:testdata1");
        result = getShowPageResult(action);
        earliestDateInUtc = new Date(today.getTime() - 54 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000);
        verifyManyLogs(55, 0, 54, result.data, result.getStatusMessage(), earliestDateInUtc);

        // continue search with filter will get logs until no logs
        nextSearch = today.getTime() - 56 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("filterQuery", "request:testdata1", "searchTimeOffset", String.valueOf(nextSearch));
        result = getShowPageResult(action);
        earliestDateInUtc = new Date(nextSearch - 24 * 60 * 60 * 1000);
        verifyManyLogs(95, 55, 60, result.data, result.getStatusMessage(), earliestDateInUtc);
    }

    private void verifyContinueSearch(String[] params, int[][] expected, int totalLogs,
            int filteredLogs, Date earliestDateInUtc) {
        AdminActivityLogPageAction action = getAction(params);
        ShowPageResult result = getShowPageResult(action);
        AdminActivityLogPageData pageData = (AdminActivityLogPageData) result.data;
        verifyStatusMessage(result.getStatusMessage(), totalLogs, filteredLogs, earliestDateInUtc);
        verifyLogs(expected, getLogsFromLogTemplateRows(pageData.getLogs()));
    }

    private void verifyActionResult(int[][] expectedLogs, String... params) {
        AdminActivityLogPageAction action = getAction(params);
        ShowPageResult result = getShowPageResult(action);
        AdminActivityLogPageData pageData = (AdminActivityLogPageData) result.data;
        List<ActivityLogEntry> actualLogs = getLogsFromLogTemplateRows(pageData.getLogs());
        verifyLogs(expectedLogs, actualLogs);
    }

    /**
     * Verifies actualLogs contains expectedLogs.
     *
     * <p>expectedLogs is a 2D array, the outer indices correspond to {@link #LOG_MESSAGE_INDEX_TODAY}
     * {@link #LOG_MESSAGE_INDEX_YESTERDAY} and {@link #LOG_MESSAGE_INDEX_TWO_DAYS_AGO}, the inner indices for
     * every {@code LOG_MESSAGE_INDEX_*} correspond to the orders in the test data.
     */
    private void verifyLogs(int[][] expectedLogs, List<ActivityLogEntry> actualLogs) {
        List<String> expectedMsgs = generateExpectedMsgFrom(expectedLogs);

        assertEquals(expectedMsgs.size(), actualLogs.size());
        for (int i = 0; i < expectedMsgs.size(); i++) {
            String actualMsg = actualLogs.get(i).generateLogMessage();
            assertTrue("expect: " + expectedMsgs.get(i) + "to contain:" + actualMsg,
                    expectedMsgs.get(i).contains(actualMsg));
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

    private void verifyStatusMessage(String message, int totalLogs, int filteredLogs, Date earliestDateInUtc) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH");
        sdf.setTimeZone(TimeZone.getTimeZone(Const.SystemParams.ADMIN_TIME_ZONE));

        assertTrue(message.contains("Total Logs gone through in last search: " + totalLogs));
        assertTrue(message.contains("Total Relevant Logs found in last search: " + filteredLogs));
        assertTrue(message.contains("Logs are from following version(s): 1"));
        assertTrue(message.contains("All available version(s): 1"));
        assertTrue(message.contains("The earliest log entry checked on <b>" + sdf.format(earliestDateInUtc.getTime())));
    }

    private void verifyLocalTimeInStatusMessage(String message, Date timeInUtc, double localTimeZone) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH");
        Calendar timeLocal = TimeHelper.convertToUserTimeZone(TimeHelper.dateToCalendar(timeInUtc), localTimeZone);
        assertTrue(message.contains(sdf.format(timeLocal.getTime())));
        assertTrue(message.contains(String.format("in Local Time Zone (%.1f)", localTimeZone)));
    }

    private void verifyLoadingLocalTimeAjaxResult(String expected, String role, String googleId, long timeInMillis) {
        String[] params = new String[]{"logRole", role, "logGoogleId", googleId,
                "logTimeInAdminTimeZone", String.valueOf(timeInMillis)};

        AdminActivityLogPageAction action = getAction(params);
        AjaxResult result = getAjaxResult(action);
        AdminActivityLogPageData pageData = (AdminActivityLogPageData) result.data;
        assertEquals(expected, pageData.getLogLocalTime());
    }

    private void verifyManyLogs(int totalLogs, int first, int last,
            PageData pageData, String statusMessage, Date earliestDateInUtc) {
        List<ActivityLogEntry> actualLogs =
                getLogsFromLogTemplateRows(((AdminActivityLogPageData) pageData).getLogs());
        int numLogs = last - first + 1;

        verifyLogsIdInRange(actualLogs, first, last);
        verifyStatusMessage(statusMessage, totalLogs, numLogs, earliestDateInUtc);
    }

    private void verifyLogsIdInRange(List<ActivityLogEntry> actualLogs, int first, int last) {
        assertEquals(last - first + 1, actualLogs.size());
        for (int i = 0; i < actualLogs.size(); i++) {
            assertEquals(String.format("id4%02d", first + i), actualLogs.get(i).getLogId());
        }
    }

    private List<ActivityLogEntry> getLogsFromLogTemplateRows(List<AdminActivityLogTableRow> rows) {
        List<ActivityLogEntry> logs = new ArrayList<>();
        for (AdminActivityLogTableRow row : rows) {
            logs.add(row.getLogEntry());
        }
        return logs;
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

    private Calendar getBeginOfTheDayOffsetNowInAdminTimeZone(int dayOffset) {
        Calendar calendar = TimeHelper.now(Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.add(Calendar.DATE, dayOffset);
        return calendar;
    }

    private Calendar getEndOfTheDayOffsetNowInAdminTimeZone(int dayOffset) {
        Calendar calendar = getBeginOfTheDayOffsetNowInAdminTimeZone(dayOffset);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar;
    }

    private Calendar adminTimeZoneToUtc(Calendar calendar) {
        return TimeHelper.convertToUserTimeZone(calendar, -Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
    }

    @Override
    protected AdminActivityLogPageAction getAction(String... params) {
        return (AdminActivityLogPageAction) gaeSimulation.getActionObject(getActionUri(), params);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        String[] submissionParams = new String[]{};
        verifyOnlyAdminsCanAccess(submissionParams);
    }

}
