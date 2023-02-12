package teammates.e2e.cases.axe;

import java.time.Instant;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.AdminSessionsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#ADMIN_SESSIONS_PAGE}.
 */
public class AdminSessionsPageAxeTest extends BaseE2ETestCase {
    private Instant instant3DaysAgo = TimeHelper.getInstantDaysOffsetFromNow(-3);
    private Instant instantTomorrow = TimeHelper.getInstantDaysOffsetFromNow(1);
    private Instant instant3DaysLater = TimeHelper.getInstantDaysOffsetFromNow(3);
    private Instant instant10DaysLater = TimeHelper.getInstantDaysOffsetFromNow(10);
    private Instant instant24DaysLater = TimeHelper.getInstantDaysOffsetFromNow(24);

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/AdminSessionsPageE2ETest.json");

        // To guarantee that there will always be some "ongoing sessions" listed,
        // the test data is injected with date/time values relative to the time where the test takes place

        FeedbackSessionAttributes openFeedbackSession = testData.feedbackSessions.get("session1InCourse1");
        openFeedbackSession.setStartTime(instant3DaysAgo);
        openFeedbackSession.setCreatedTime(instant3DaysAgo);
        openFeedbackSession.setSessionVisibleFromTime(instant3DaysAgo);
        openFeedbackSession.setEndTime(instant3DaysLater);
        openFeedbackSession.setResultsVisibleFromTime(instant3DaysLater);

        FeedbackSessionAttributes awaitingFeedbackSession = testData.feedbackSessions.get("session2InCourse1");
        awaitingFeedbackSession.setStartTime(instantTomorrow);
        awaitingFeedbackSession.setCreatedTime(instant3DaysAgo);
        awaitingFeedbackSession.setSessionVisibleFromTime(instantTomorrow);
        awaitingFeedbackSession.setEndTime(instant3DaysLater);
        awaitingFeedbackSession.setResultsVisibleFromTime(instant3DaysLater);

        FeedbackSessionAttributes futureFeedbackSession = testData.feedbackSessions.get("session3InCourse1");
        futureFeedbackSession.setStartTime(instant10DaysLater);
        futureFeedbackSession.setCreatedTime(instant3DaysAgo);
        futureFeedbackSession.setSessionVisibleFromTime(instant10DaysLater);
        futureFeedbackSession.setEndTime(instant24DaysLater);
        futureFeedbackSession.setResultsVisibleFromTime(instant24DaysLater);

        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl sessionsUrl = createFrontendUrl(Const.WebPageURIs.ADMIN_SESSIONS_PAGE);
        AdminSessionsPage sessionsPage = loginAdminToPage(sessionsUrl, AdminSessionsPage.class);

        Results results = AxeUtil.AXE_BUILDER.analyze(sessionsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());

    }

}
