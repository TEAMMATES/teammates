package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorFeedbackEditPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_EDIT_PAGE}.
 */
public class InstructorFeedbackEditPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackEditPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_EDIT_PAGE)
                .withCourseId(testData.courses.get("course").getId())
                .withSessionName(testData.feedbackSessions.get("openSession").getFeedbackSessionName());

        InstructorFeedbackEditPage feedbackEditPage = loginToPage(url, InstructorFeedbackEditPage.class,
                testData.instructors.get("instructor").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(feedbackEditPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }
}
