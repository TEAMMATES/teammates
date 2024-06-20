package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}.
 */
public class InstructorFeedbackEditPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackEditPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorFeedbackEditPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withCourseId(testData.courses.get("course").getId())
                .withSessionName(testData.feedbackSessions.get("openSession").getFeedbackSessionName());

        InstructorFeedbackEditPage feedbackEditPage = loginToPage(url, InstructorFeedbackEditPage.class,
                testData.instructors.get("instructor").getGoogleId());

        // landmark-unique might be caused by tinymce
        // aria-prohibited-attr is caused by https://github.com/tinymce/tinymce/issues/7346
        Results results = getAxeBuilder("aria-prohibited-attr", "landmark-unique")
                .analyze(feedbackEditPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }
}
