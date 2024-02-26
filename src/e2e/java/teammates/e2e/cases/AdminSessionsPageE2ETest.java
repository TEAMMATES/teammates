package teammates.e2e.cases;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.e2e.pageobjects.AdminSessionsPage;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SESSIONS_PAGE}.
 */
public class AdminSessionsPageE2ETest extends BaseE2ETestCase {
    private FeedbackSessionAttributes openFeedbackSession;
    private FeedbackSessionAttributes awaitingFeedbackSession;
    private FeedbackSessionAttributes futureFeedbackSession;
    private Instant instant3DaysAgo = TimeHelper.getInstantDaysOffsetFromNow(-3);
    private Instant instantTomorrow = TimeHelper.getInstantDaysOffsetFromNow(1);
    private Instant instant3DaysLater = TimeHelper.getInstantDaysOffsetFromNow(3);
    private Instant instantNextWeek = TimeHelper.getInstantDaysOffsetFromNow(7);
    private Instant instant10DaysLater = TimeHelper.getInstantDaysOffsetFromNow(10);
    private Instant instant14DaysLater = TimeHelper.getInstantDaysOffsetFromNow(14);
    private Instant instant24DaysLater = TimeHelper.getInstantDaysOffsetFromNow(24);

    private String formatDateTime(Instant instant, String timeZone) {
        return DateTimeFormatter
                .ofPattern("EEE, dd MMM yyyy, hh:mm a")
                .format(instant.atZone(ZoneId.of(timeZone)))
                .replaceFirst(" AM$", " am")
                .replaceFirst(" PM$", " pm");
    }

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminSessionsPageE2ETest.json");

        // To guarantee that there will always be some "ongoing sessions" listed,
        // the test data is injected with date/time values relative to the time where the test takes place

        openFeedbackSession = testData.feedbackSessions.get("session1InCourse1");
        openFeedbackSession.setStartTime(instant3DaysAgo);
        openFeedbackSession.setCreatedTime(instant3DaysAgo);
        openFeedbackSession.setSessionVisibleFromTime(instant3DaysAgo);
        openFeedbackSession.setEndTime(instant3DaysLater);
        openFeedbackSession.setResultsVisibleFromTime(instant3DaysLater);

        awaitingFeedbackSession = testData.feedbackSessions.get("session2InCourse1");
        awaitingFeedbackSession.setStartTime(instantTomorrow);
        awaitingFeedbackSession.setCreatedTime(instant3DaysAgo);
        awaitingFeedbackSession.setSessionVisibleFromTime(instantTomorrow);
        awaitingFeedbackSession.setEndTime(instant3DaysLater);
        awaitingFeedbackSession.setResultsVisibleFromTime(instant3DaysLater);

        futureFeedbackSession = testData.feedbackSessions.get("session3InCourse1");
        futureFeedbackSession.setStartTime(instant10DaysLater);
        futureFeedbackSession.setCreatedTime(instant3DaysAgo);
        futureFeedbackSession.setSessionVisibleFromTime(instant10DaysLater);
        futureFeedbackSession.setEndTime(instant24DaysLater);
        futureFeedbackSession.setResultsVisibleFromTime(instant24DaysLater);

        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(loadSqlDataBundle("/AdminSessionsPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {

        ______TS("verify loaded data");

        AppUrl sessionsUrl = createFrontendUrl(Const.WebPageURIs.ADMIN_SESSIONS_PAGE);
        AdminSessionsPage sessionsPage = loginAdminToPage(sessionsUrl, AdminSessionsPage.class);

        String tableTimezone = sessionsPage.getSessionsTableTimezone();

        String[] openSessionCells = {
                "[Opened]",
                String.format("[%s] %s", openFeedbackSession.getCourseId(),
                        openFeedbackSession.getFeedbackSessionName()),
                "Show",
                formatDateTime(instant3DaysAgo, tableTimezone),
                formatDateTime(instant3DaysLater, tableTimezone),
                openFeedbackSession.getCreatorEmail(),
        };

        String[] awaitingSessionCells = {
                "[Waiting To Open]",
                String.format("[%s] %s", awaitingFeedbackSession.getCourseId(),
                        awaitingFeedbackSession.getFeedbackSessionName()),
                "Show",
                formatDateTime(instantTomorrow, tableTimezone),
                formatDateTime(instant3DaysLater, tableTimezone),
                awaitingFeedbackSession.getCreatorEmail(),
        };

        String[] futureSessionCells = {
                "[Waiting To Open]",
                String.format("[%s] %s", futureFeedbackSession.getCourseId(),
                        futureFeedbackSession.getFeedbackSessionName()),
                "Show",
                formatDateTime(instant10DaysLater, tableTimezone),
                formatDateTime(instant24DaysLater, tableTimezone),
                futureFeedbackSession.getCreatorEmail(),
        };

        String[][] sessionsCells = {
                openSessionCells, awaitingSessionCells, futureSessionCells,
        };

        // Open and awaiting session should be displayed with the appropriate status
        // Future session should not be displayed yet

        boolean[] expectedSessionShownStatus = { true, true, false };

        sessionsPage.verifySessionRows(sessionsCells, expectedSessionShownStatus);

        ______TS("query future session");

        sessionsPage.toggleSessionFilter();
        sessionsPage.waitForSessionFilterVisibility();

        sessionsPage.setFilterStartDate(instantNextWeek);
        sessionsPage.setFilterEndDate(instant14DaysLater);
        sessionsPage.filterSessions();

        // This time, only future session should be displayed
        // The previous open and awaiting session would have closed by this date

        expectedSessionShownStatus = new boolean[] { false, false, true };

        sessionsPage.verifySessionRows(sessionsCells, expectedSessionShownStatus);

    }

}
