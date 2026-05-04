package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.logic.entity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE}.
 */
public class InstructorCourseStudentDetailsPageE2ETest extends BaseE2ETestCase {
    private Student student;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                        loadDataBundle("/InstructorCourseStudentDetailsPageE2ETest.json"));

        student = testData.students.get("ICSDet.jose.tmms");
    }

    @Test
    @Override
    public void testAll() {
        ______TS("verify loaded details");
        AppUrl viewPageUrl = getStudentDetailsViewPageUrl(student.getEmail());
        InstructorCourseStudentDetailsViewPage viewPage =
                loginToPage(viewPageUrl, InstructorCourseStudentDetailsViewPage.class,
                        testData.instructors.get("ICSDet.instr").getGoogleId());

        viewPage.verifyStudentDetails(student);
    }

    private AppUrl getStudentDetailsViewPageUrl(String studentEmail) {
        return createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
                .withCourseId(testData.courses.get("ICSDet.CS2104").getId())
                .withStudentEmail(studentEmail);
    }
}
