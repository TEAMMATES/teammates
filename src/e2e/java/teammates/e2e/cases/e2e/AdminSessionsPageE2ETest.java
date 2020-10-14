package teammates.e2e.cases.e2e;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
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
    private Instant instant3DaysAgo = TimeHelper.getInstantDaysOffsetFromNow(-3);
    private Instant instantTomorrow = TimeHelper.getInstantDaysOffsetFromNow(1);
    private Instant instant3DaysLater = TimeHelper.getInstantDaysOffsetFromNow(3);

    private String formatDateTime(Instant instant, ZoneId timeZone) {
        return DateTimeFormatter
                .ofPattern("EEE, dd MMM YYYY, hh:mm a")
                .format(instant.atZone(timeZone))
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

        removeAndRestoreDataBundle(testData);
    }

    @Test
    public void testAll() {

        ______TS("verify loaded data");

        AppUrl sessionsUrl = createUrl(Const.WebPageURIs.ADMIN_SESSIONS_PAGE);
        AdminSessionsPage sessionsPage = loginAdminToPage(sessionsUrl, AdminSessionsPage.class);
        sessionsPage.waitUntilAnimationFinish();

        List<WebElement> ongoingSessionRows = sessionsPage.getOngoingSessionsRows();

        String[] openSessionCells = {
                "[Opened]",
                String.format("[%s] %s", openFeedbackSession.getCourseId(),
                        openFeedbackSession.getFeedbackSessionName()),
                "Show",
                formatDateTime(instant3DaysAgo, openFeedbackSession.getTimeZone()),
                formatDateTime(instant3DaysLater, openFeedbackSession.getTimeZone()),
                openFeedbackSession.getCreatorEmail(),
        };
        boolean hasOpenSession = false;

        String[] awaitingSessionCells = {
                "[Waiting To Open]",
                String.format("[%s] %s", awaitingFeedbackSession.getCourseId(),
                        awaitingFeedbackSession.getFeedbackSessionName()),
                "Show",
                formatDateTime(instantTomorrow, awaitingFeedbackSession.getTimeZone()),
                formatDateTime(instant3DaysLater, awaitingFeedbackSession.getTimeZone()),
                awaitingFeedbackSession.getCreatorEmail(),
        };
        boolean hasAwaitingSession = false;

        for (WebElement sessionRow : ongoingSessionRows) {
            List<WebElement> cells = sessionRow.findElements(By.tagName("td"));

            // Only validate for the preset ongoing sessions
            // This is because the page will display all ongoing sessions in the database, which is not predictable

            if (openSessionCells[1].equals(cells.get(1).getText())) {
                sessionsPage.verifySessionRow(sessionRow, openSessionCells);
                hasOpenSession = true;
            } else if (awaitingSessionCells[1].equals(cells.get(1).getText())) {
                sessionsPage.verifySessionRow(sessionRow, awaitingSessionCells);
                hasAwaitingSession = true;
            }
        }

        assertTrue(hasOpenSession);
        assertTrue(hasAwaitingSession);

    }

}
