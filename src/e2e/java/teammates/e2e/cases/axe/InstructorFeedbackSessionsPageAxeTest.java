package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorFeedbackSessionsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSIONS_PAGE}.
 */
public class InstructorFeedbackSessionsPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackSessionsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSIONS_PAGE);
        InstructorFeedbackSessionsPage feedbackSessionsPage =
                loginToPage(url, InstructorFeedbackSessionsPage.class,
                testData.instructors.get("instructor").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(feedbackSessionsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
