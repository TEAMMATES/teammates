package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorFeedbackResultsPageSql;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_SESSION_REPORT_PAGE}.
 */
public class InstructorFeedbackReportPageAxeTest extends BaseAxeTestCase {

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
                .withFeedbackSessionId(testData.feedbackSessions.get("Open Session").getId().toString())
                .withSessionName(testData.feedbackSessions.get("Open Session").getName());
        InstructorFeedbackResultsPageSql resultsPage = loginToPage(resultsUrl, InstructorFeedbackResultsPageSql.class,
                testData.instructors.get("IFRep.instr.CS2104").getGoogleId());

        Results results = getAxeBuilder().analyze(resultsPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
