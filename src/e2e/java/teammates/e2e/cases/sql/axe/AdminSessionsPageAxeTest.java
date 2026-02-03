package teammates.e2e.cases.sql.axe;

import java.time.Instant;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.e2e.pageobjects.AdminSessionsPage;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SESSIONS_PAGE}.
 */
public class AdminSessionsPageAxeTest extends BaseAxeTestCase {

    private static final Instant INSTANT_3_DAYS_AGO = TimeHelper.getInstantDaysOffsetFromNow(-3);
    private static final Instant INSTANT_TOMORROW = TimeHelper.getInstantDaysOffsetFromNow(1);
    private static final Instant INSTANT_3_DAYS_LATER = TimeHelper.getInstantDaysOffsetFromNow(3);
    private static final Instant INSTANT_10_DAYS_LATER = TimeHelper.getInstantDaysOffsetFromNow(10);
    private static final Instant INSTANT_24_DAYS_LATER = TimeHelper.getInstantDaysOffsetFromNow(24);

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/AdminSessionsPageE2ETestSql.json");

        // To guarantee that there will always be some "ongoing sessions" listed,
        // the test data is injected with date/time values relative to the time where the test runs.
        FeedbackSession openFeedbackSession = testData.feedbackSessions.get("session1InCourse1");
        openFeedbackSession.setStartTime(INSTANT_3_DAYS_AGO);
        openFeedbackSession.setCreatedAt(INSTANT_3_DAYS_AGO);
        openFeedbackSession.setSessionVisibleFromTime(INSTANT_3_DAYS_AGO);
        openFeedbackSession.setEndTime(INSTANT_3_DAYS_LATER);
        openFeedbackSession.setResultsVisibleFromTime(INSTANT_3_DAYS_LATER);

        FeedbackSession awaitingFeedbackSession = testData.feedbackSessions.get("session2InCourse1");
        awaitingFeedbackSession.setStartTime(INSTANT_TOMORROW);
        awaitingFeedbackSession.setCreatedAt(INSTANT_3_DAYS_AGO);
        awaitingFeedbackSession.setSessionVisibleFromTime(INSTANT_TOMORROW);
        awaitingFeedbackSession.setEndTime(INSTANT_3_DAYS_LATER);
        awaitingFeedbackSession.setResultsVisibleFromTime(INSTANT_3_DAYS_LATER);

        FeedbackSession futureFeedbackSession = testData.feedbackSessions.get("session3InCourse1");
        futureFeedbackSession.setStartTime(INSTANT_10_DAYS_LATER);
        futureFeedbackSession.setCreatedAt(INSTANT_3_DAYS_AGO);
        futureFeedbackSession.setSessionVisibleFromTime(INSTANT_10_DAYS_LATER);
        futureFeedbackSession.setEndTime(INSTANT_24_DAYS_LATER);
        futureFeedbackSession.setResultsVisibleFromTime(INSTANT_24_DAYS_LATER);

        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl sessionsUrl = createFrontendUrl(Const.WebPageURIs.ADMIN_SESSIONS_PAGE);
        AdminSessionsPage sessionsPage = loginAdminToPage(sessionsUrl, AdminSessionsPage.class);

        Results results = getAxeBuilder().analyze(sessionsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
