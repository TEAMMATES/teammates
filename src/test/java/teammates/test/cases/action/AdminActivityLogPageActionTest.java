package teammates.test.cases.action;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.testng.annotations.Test;

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

import com.google.gson.reflect.TypeToken;

public class AdminActivityLogPageActionTest extends BaseActionTest {

    private static final String TYPICAL_LOG_MESSAGE = "/typicalLogMessage.json";

    private static final int LOG_MESSAGE_INDEX_TODAY = 0;
    private static final int LOG_MESSAGE_INDEX_YESTERDAY = 1;
    private static final int LOG_MESSAGE_INDEX_TWO_DAYS_AGO = 2;

    private static final int LOG_MESSAGE_INDEX_MANY_LOGS = 3;

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
            String pathToJsonFile = TestProperties.TEST_DATA_FOLDER + TYPICAL_LOG_MESSAGE;
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
    @Test
    public void testExecuteAndPostProcess() {
        gaeSimulation.loginAsAdmin("admin");

        // with typical log message
        removeAndRestoreLogMessage();

        testInvalidQuery();
        testShowTestingDataAndExcludedUri();
        testFilters();
        testFiltersCombination();
        testLogMessageInDifferentVersions();

        testStatusMessage();

        testLoadingLocalTimeAjax();

        testContinueSearch();

        // with many logs
        removeAndRestoreManyLogs();

        testManyLogs();
    }

    private void testInvalidQuery() {
        // execute default search when filterQuery is invalid
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

    private void testShowTestingDataAndExcludedUri() {
        // no test data, no excluded URI, default search
        int[][] expected = new int[][]{{0, 1, 3, 4, 5}};
        verifyActionResult(expected);

        // show test data, no excluded URI
        expected = new int[][]{{0, 1, 2, 3, 4, 5, 6}};
        verifyActionResult(expected, "testdata", "true");

        // not test data, show excluded URI
        expected = new int[][]{{0, 1, 3, 4, 5, 7, 8, 9}};
        verifyActionResult(expected, "all", "true");

        // show everything
        expected = new int[][]{{0, 1, 2, 3, 4, 5, 6, 7, 8, 9}};
        verifyActionResult(expected, "testdata", "true", "all", "true");
    }

    private void testFilters() {
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

    private void testFiltersCombination() {
        Date today = TimeHelper.getDateOffsetToCurrentTime(0);
        Date twoDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-2);

        // filterQuery with showing all URI
        int[][] expected = new int[][]{{0, 3, 5, 7, 8, 9}};
        String query = "info:keyword1";
        verifyActionResult(expected, "filterQuery", query, "all", "true");

        // another filterQuery with showing all URI
        expected = new int[][]{{0, 1, 3, 7, 8, 9}, {0, 1, 4}, {3}};
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

    private void testLogMessageInDifferentVersions() {
        // version query is controlled by GAE itself
        // so there is no need to write comprehensive test case for it

        int[][] expected = new int[][]{{}};
        String query = "version:2";
        verifyActionResult(expected, "filterQuery", query);

        expected = new int[][]{{0, 1, 3, 4, 5}};
        query = "version:2, 1";
        verifyActionResult(expected, "filterQuery", query);
    }

    private void testStatusMessage() {
        Date yesterday = TimeHelper.getDateOffsetToCurrentTime(-1);

        // test statusMessage for default search
        AdminActivityLogPageAction action = getAction();
        String statusMessage = getShowPageResult(action).getStatusMessage();
        verifyStatusMessage(statusMessage, 12, 5, yesterday);
        verifyLocalTimeInStatusMessage(statusMessage, yesterday, Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);

        // test statusMessage with filterQuery
        String query = "person:idOfInstructor1OfCourse1";
        action = getAction("filterQuery", query);
        statusMessage = getShowPageResult(action).getStatusMessage();
        verifyStatusMessage(statusMessage, 12, 1, yesterday);
        // TODO: fix the bug
        // Currently when the `person:xxx` like query is present,
        // status message is supposed to contain the time in that person's timezone.
        // But this function gets a huge bug: The query will be processed as lower case.
        // The person google id contains upper case. And thus the information could be lost.
        // In this case, person's google id will become `person:idofinstructor1ofcourse1`
        // verifyLocalTimeInStatusMessage(statusMessage, yesterday, 2);

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
        verifyStatusMessage(statusMessage, 18, 8, fromDate.getTime());
        verifyLocalTimeInStatusMessage(statusMessage, fromDate.getTime(), Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
    }

    private void testLoadingLocalTimeAjax() {
        Calendar now = TimeHelper.now(Const.SystemParams.ADMIN_TIME_ZONE_DOUBLE);
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // Unknown
        String failureMsg = "Local Time Unavailable";
        verifyLoadingLocalTimeAjaxResult(failureMsg, "Unregistered", "Unknown", now.getTimeInMillis());
        verifyLoadingLocalTimeAjaxResult(failureMsg, "Instructor", "instructorWithoutCourses", now.getTimeInMillis());
        verifyLoadingLocalTimeAjaxResult(failureMsg, "Student", "student1InUnregisteredCourse", now.getTimeInMillis());
        verifyLoadingLocalTimeAjaxResult(failureMsg, "Unregistered:unregisteredCourse", "Unregistered",
                                         now.getTimeInMillis());

        // Role: Admin
        verifyLoadingLocalTimeAjaxResult(sdf.format(now.getTime()), "Admin", "admin",
                                         now.getTimeInMillis());
        verifyLoadingLocalTimeAjaxResult(sdf.format(now.getTime()), "Student(M)", "admin",
                                         now.getTimeInMillis());

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

    private void testContinueSearch() {
        Date yesterday = TimeHelper.getDateOffsetToCurrentTime(-1);
        Date twoDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-2);

        // default search continue
        int[][] expected = new int[][]{{}, {0, 1, 2}};
        String[] params = new String[] {"searchTimeOffset", String.valueOf(yesterday.getTime())};
        verifyContinueSearch(params, expected, 6, 3, twoDaysAgo);

        // with some filters
        expected = new int[][]{{}, {0, 3}};
        params = new String[] {
                "searchTimeOffset", String.valueOf(yesterday.getTime()),
                "filterQuery", "info:keyword1",
                "testdata", "true"};
        verifyContinueSearch(params, expected, 6, 2, twoDaysAgo);

        // when `from` is present, will not do continue search
        expected = new int[][]{{0, 1, 3, 4, 5}, {0, 1, 2}};
        params = new String[] {
                "searchTimeOffset", String.valueOf(yesterday.getTime()),
                "filterQuery", String.format("from:%s", formatterAdminTime.format(yesterday))};
        Calendar yesterdayBegin = adminTimeZoneToUtc(getBeginOfTheDayOffsetNowInAdminTimeZone(-1));
        verifyContinueSearch(params, expected, 18, 8, yesterdayBegin.getTime());

        // `to` present, search with 1 day interval
        expected = new int[][]{{}, {}, {0, 1}};
        Calendar toDate = adminTimeZoneToUtc(getEndOfTheDayOffsetNowInAdminTimeZone(-2));
        params = new String[] {
                "searchTimeOffset", String.valueOf(toDate.getTimeInMillis()),
                "filterQuery", String.format("to:%s", formatterAdminTime.format(yesterday))};
        toDate = adminTimeZoneToUtc(getEndOfTheDayOffsetNowInAdminTimeZone(-3));
        verifyContinueSearch(params, expected, 4, 2, toDate.getTime());

    }

    private void testManyLogs() {
        Date today = TimeHelper.getDateOffsetToCurrentTime(0);

        // default search will stop at #logs around 50
        AdminActivityLogPageAction action = getAction();
        ShowPageResult result = getShowPageResult(action);
        Date earliestDateInUtc = new Date(today.getTime() - 54 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000);
        verifyManyLogs(55, 0, 54, result.data, result.getStatusMessage(), earliestDateInUtc);

        // continue search will get next #logs around 50
        long nextSearch = today.getTime() - 56 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("searchTimeOffset", String.valueOf(nextSearch));
        AjaxResult resultAjax = getAjaxResult(action);
        earliestDateInUtc = new Date(today.getTime() - 110 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000);
        verifyManyLogs(56, 55, 110, resultAjax.data, resultAjax.getStatusMessage(), earliestDateInUtc);

        // continue search will get logs until no logs
        nextSearch = today.getTime() - 112 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("searchTimeOffset", String.valueOf(nextSearch));
        resultAjax = getAjaxResult(action);
        earliestDateInUtc = new Date(nextSearch - 24 * 60 * 60 * 1000);
        verifyManyLogs(39, 111, 149, resultAjax.data, resultAjax.getStatusMessage(), earliestDateInUtc);

        // default search with filter stop at #logs around 50
        action = getAction("filterQuery", "request:testdata1");
        result = getShowPageResult(action);
        earliestDateInUtc = new Date(today.getTime() - 54 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000);
        verifyManyLogs(55, 0, 54, result.data, result.getStatusMessage(), earliestDateInUtc);

        // continue search with filter will get logs until no logs
        nextSearch = today.getTime() - 56 * LOG_MESSAGE_INTERVAL_MANY_LOGS * 1000;
        action = getAction("filterQuery", "request:testdata1", "searchTimeOffset", String.valueOf(nextSearch));
        resultAjax = getAjaxResult(action);
        earliestDateInUtc = new Date(nextSearch - 24 * 60 * 60 * 1000);
        verifyManyLogs(95, 55, 60, resultAjax.data, resultAjax.getStatusMessage(), earliestDateInUtc);
    }

    private void verifyContinueSearch(String[] params, int[][] expected, int totalLogs,
            int filteredLogs, Date earliestDateInUtc) {
        AdminActivityLogPageAction action = getAction(params);
        AjaxResult result = getAjaxResult(action);
        AdminActivityLogPageData pageData = (AdminActivityLogPageData) result.data;
        verifyStatusMessage(result.getStatusMessage(), totalLogs, filteredLogs, earliestDateInUtc);
        verifyLogs(expected, pageData.getLogs());
    }

    private void verifyActionResult(int[][] expectedLogs, String... params) {
        AdminActivityLogPageAction action = getAction(params);
        ShowPageResult result = getShowPageResult(action);
        AdminActivityLogPageData page = (AdminActivityLogPageData) result.data;
        List<ActivityLogEntry> actualLogs = page.getLogs();
        verifyLogs(expectedLogs, actualLogs);
    }

    /**
     * Verifies actualLogs contains expectedLogs.
     *
     * <p>expectedLogs is a 2D array, the outer indices correspond to {@link #LOG_MESSAGE_INDEX_TODAY}
     * {@link #LOG_MESSAGE_YESTDAY_INDEX} and {@link #LOG_MESSAGE_INDEX_TWO_DAYS_AGO}, the inner indices for
     * every {@code LOG_MESSAGE_*_INDEX} correspond to the orders in {@link #TYPICAL_LOG_MESSAGE} file.
     *
     * @param expectedLogs Expected logs
     * @param actualLogs Actual logs
     */
    private void verifyLogs(int[][] expectedLogs, List<ActivityLogEntry> actualLogs) {
        List<String> expectedMsgs = generateExpectedMsgFrom(expectedLogs);

        assertEquals(expectedMsgs.size(), actualLogs.size());
        for (int i = 0; i < expectedMsgs.size(); i++) {
            String actualMsg = actualLogs.get(i).generateLogMessage();
            actualMsg = actualMsg.replace("<mark>", "").replace("</mark>", "");
            assertTrue("expecte: " + expectedMsgs.get(i) + "to contain:" + actualMsg,
                       expectedMsgs.get(i).contains(actualMsg));
        }
    }

    private List<String> generateExpectedMsgFrom(int[][] expectedLogs) {
        List<String> result = new ArrayList<String>();
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
        // bug might be introduced when the time is 00:00 AM.
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
        List<ActivityLogEntry> actualLogs = ((AdminActivityLogPageData) pageData).getLogs();
        int numLogs = last - first + 1;

        verifyLogsIdInRange(actualLogs, first, last);
        verifyStatusMessage(statusMessage, totalLogs, numLogs, earliestDateInUtc);
    }

    private void verifyLogsIdInRange(List<ActivityLogEntry> actualLogs, int first, int last) {
        assertEquals(last - first + 1, actualLogs.size());
        for (int i = 0; i < actualLogs.size(); i++) {
            assertEquals(String.format("id4%02d", first + i), actualLogs.get(i).getId());
        }
    }

    private void removeAndRestoreLogMessage() {
        gaeSimulation.clearLogs();

        Date twoDaysAgo = TimeHelper.getDateOffsetToCurrentTime(-2);
        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_TWO_DAYS_AGO), twoDaysAgo.getTime());
        Date yesterday = TimeHelper.getDateOffsetToCurrentTime(-1);
        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_YESTERDAY), yesterday.getTime());
        Date today = TimeHelper.getDateOffsetToCurrentTime(0);
        insertLogMessagesAtTime(logMessages.get(LOG_MESSAGE_INDEX_TODAY), today.getTime());
    }

    private void removeAndRestoreManyLogs() {
        gaeSimulation.clearLogs();

        Date today = TimeHelper.getDateOffsetToCurrentTime(0);
        insertLogMessageAtTimeWithInterval(logMessages.get(LOG_MESSAGE_INDEX_MANY_LOGS),
                                           today.getTime(), LOG_MESSAGE_INTERVAL_MANY_LOGS);
    }

    private void insertLogMessagesAtTime(List<String> msgList, long timeMillis) {
        insertLogMessageAtTimeWithInterval(msgList, timeMillis, 1);
    }

    private void insertLogMessageAtTimeWithInterval(List<String> msgList, long timeMillis, int intervalInSecond) {
        int levelInfo = 1;
        // bug might be introduced when the time is 00:00 AM.
        // but this situation is really rare and can be solved by re-running the test case
        long logTimeInMillis = timeMillis - msgList.size() * intervalInSecond * 1000;
        for (int i = msgList.size() - 1; i >= 0; i--) {
            createTestDataRequestInfoAtTime(logTimeInMillis);
            gaeSimulation.addAppLogLine(String.valueOf(logTimeInMillis),
                                          logTimeInMillis * 1000, levelInfo, msgList.get(i));
            logTimeInMillis += intervalInSecond * 1000;
        }
    }

    private void createTestDataRequestInfoAtTime(long timeMillis) {
        String testStr = "TEST";
        String defaultVersion = "1";
        gaeSimulation.addLogRequestInfo(testStr, defaultVersion, String.valueOf(timeMillis), testStr,
                                        testStr, timeMillis * 1000, timeMillis * 1000, testStr,
                                        testStr, testStr, testStr, true, 200, testStr);
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

}
