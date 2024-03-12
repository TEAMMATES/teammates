package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE}.
 */
public class InstructorCourseStudentDetailsPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseStudentDetailsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);

        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorCourseStudentDetailsPageE2ETest_SqlEntities.json"));
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

        Results results = getAxeBuilder().analyze(viewPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
