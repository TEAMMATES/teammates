package teammates.e2e.cases.axe;

import org.testng.annotations.Test;

import com.deque.html.axecore.results.Results;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE}.
 */
public class InstructorCourseStudentDetailsEditPageAxeTest extends BaseAxeTestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorCourseStudentDetailsEditPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
        sqlTestData = removeAndRestoreSqlDataBundle(
                loadSqlDataBundle("/InstructorCourseStudentDetailsEditPageE2ETest_SqlEntities.json"));
    }

    @Test
    @Override
    public void testAll() {
        AppUrl editPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE)
                .withCourseId(testData.courses.get("ICSDetEdit.CS2104").getId())
                .withStudentEmail(testData.students.get("ICSDetEdit.jose.tmms").getEmail());
        InstructorCourseStudentDetailsEditPage editPage =
                loginToPage(editPageUrl, InstructorCourseStudentDetailsEditPage.class,
                testData.instructors.get("ICSDetEdit.instr").getGoogleId());

        Results results = getAxeBuilder().analyze(editPage.getBrowser().getDriver());
        assertTrue(formatViolations(results), results.violationFree());
    }

}
