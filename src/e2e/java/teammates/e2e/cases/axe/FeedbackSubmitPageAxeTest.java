package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackSubmitPage;

/**
 * SUT: {@link Const.WebPageURIs#STUDENT_SESSION_SUBMISSION_PAGE}.
 */
public class FeedbackSubmitPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackSubmitPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        sqlTestData = removeAndRestoreSqlDataBundle(loadSqlDataBundle("/FeedbackSubmitPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_SUBMISSION_PAGE)
                .withCourseId(testData.courses.get("FSubmit.CS2104").getId())
                .withSessionName(testData.feedbackSessions.get("Open Session").getFeedbackSessionName());

        FeedbackSubmitPage feedbackSubmitPage = loginToPage(url, FeedbackSubmitPage.class,
                testData.students.get("Alice").getGoogleId());

        Results results = getAxeBuilder().analyze(feedbackSubmitPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
