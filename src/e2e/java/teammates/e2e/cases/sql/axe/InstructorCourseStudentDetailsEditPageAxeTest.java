package teammates.e2e.cases.sql.axe;

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
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorCourseStudentDetailsEditPageE2ETestSql.json"));
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
