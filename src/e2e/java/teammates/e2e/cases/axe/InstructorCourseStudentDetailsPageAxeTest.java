package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.cases.BaseE2ETestCase;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.e2e.util.AxeUtil;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE}.
 */
public class InstructorCourseStudentDetailsPageAxeTest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseStudentDetailsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {
        AppUrl viewPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
                .withCourseId(testData.courses.get("ICSDet.CS2104").getId())
                .withStudentEmail(testData.students.get("ICSDet.jose.tmms").getEmail());
        InstructorCourseStudentDetailsViewPage viewPage =
                loginToPage(viewPageUrl, InstructorCourseStudentDetailsViewPage.class,
                testData.instructors.get("ICSDet.instr").getGoogleId());

        Results results = AxeUtil.AXE_BUILDER.analyze(viewPage.getBrowser().getDriver());
        assertTrue(AxeUtil.formatViolations(results), results.violationFree());
    }

}
