package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.FeedbackResultsPage;

/**
 * SUT: {@link Const.WebPageURIs#SESSION_RESULTS_PAGE}.
 */
public class FeedbackResultsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/FeedbackResultsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(loadSqlDataBundle("/FeedbackResultsPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.STUDENT_SESSION_RESULTS_PAGE)
                .withCourseId(testData.courses.get("FRes.CS2104").getId())
                .withSessionName(testData.feedbackSessions.get("Open Session").getFeedbackSessionName());
        FeedbackResultsPage resultsPage = loginToPage(url, FeedbackResultsPage.class,
                testData.students.get("Alice").getGoogleId());

        Results results = getAxeBuilder().analyze(resultsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
