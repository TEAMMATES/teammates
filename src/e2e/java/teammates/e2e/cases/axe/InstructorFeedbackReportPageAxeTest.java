package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorFeedbackResultsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_REPORT_PAGE}.
 */
public class InstructorFeedbackReportPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorFeedbackReportPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl resultsUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_SESSION_REPORT_PAGE)
                .withCourseId(testData.courses.get("tm.e2e.IFRep.CS2104").getId())
                .withSessionName(testData.feedbackSessions.get("Open Session").getFeedbackSessionName());
        InstructorFeedbackResultsPage resultsPage = loginToPage(resultsUrl, InstructorFeedbackResultsPage.class,
                testData.instructors.get("tm.e2e.IFRep.instr").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(resultsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
