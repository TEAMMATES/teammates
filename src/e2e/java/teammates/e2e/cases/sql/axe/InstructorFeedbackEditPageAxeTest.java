package teammates.e2e.cases.sql.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackEditPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}.
 */
public class InstructorFeedbackEditPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadSqlDataBundle("/InstructorFeedbackEditPageE2ETestSql.json");
        removeAndRestoreDataBundle(testData);
        putDocuments(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withCourseId(testData.courses.get("InstFEP.CS2104").getId())
                .withSessionName(testData.feedbackSessions.get("openSession").getName());

        InstructorFeedbackEditPageSql feedbackEditPage = loginToPage(url, InstructorFeedbackEditPageSql.class,
                testData.instructors.get("InstFEP.instr").getGoogleId());

        // landmark-unique might be caused by tinymce
        // aria-prohibited-attr is caused by https://github.com/tinymce/tinymce/issues/7346
        // label is caused by custom recipients fields missing labels
        // nested-interactive is caused by focusable elements in card headers
        Results results = getAxeBuilder("aria-prohibited-attr", "landmark-unique", "label", "nested-interactive")
                .analyze(feedbackEditPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }
}
