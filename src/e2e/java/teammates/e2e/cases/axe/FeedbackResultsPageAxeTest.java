package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.FeedbackResultsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#SESSION_RESULTS_PAGE}.
 */
public class FeedbackResultsPageAxeTest extends BaseE2ETestCase {
    private FeedbackSessionAttributes openSession;
    private StudentAttributes student;

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackResultsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        student = testData.students.get("Alice");
        openSession = testData.feedbackSessions.get("Open Session");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_RESULTS_PAGE)
                .withCourseId(openSession.getCourseId())
                .withSessionName(openSession.getFeedbackSessionName());
        FeedbackResultsPage resultsPage = loginToPage(url, FeedbackResultsPage.class, student.getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(resultsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
