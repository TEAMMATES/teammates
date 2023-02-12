package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorStudentActivityLogsPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_ACTIVITY_LOGS_PAGE}.
 */
public class InstructorStudentActivityLogsPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorStudentActivityLogsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl url = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_ACTIVITY_LOGS_PAGE)
                .withCourseId("tm.e2e.ISActLogs.CS2104");
        InstructorStudentActivityLogsPage studentActivityLogsPage =
                loginToPage(url, InstructorStudentActivityLogsPage.class,
                testData.instructors.get("instructor").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(studentActivityLogsPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }
}
