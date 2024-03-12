package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackSessionsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSIONS_PAGE}.
 */
public class InstructorFeedbackSessionsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackSessionsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorFeedbackSessionsPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSIONS_PAGE);
        InstructorFeedbackSessionsPage feedbackSessionsPage =
                loginToPage(url, InstructorFeedbackSessionsPage.class,
                testData.instructors.get("instructor").getGoogleId());

        Results results = getAxeBuilder().analyze(feedbackSessionsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
