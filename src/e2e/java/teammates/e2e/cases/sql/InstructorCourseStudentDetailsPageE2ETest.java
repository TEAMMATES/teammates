package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPage;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE}.
 */
public class InstructorCourseStudentDetailsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadSqlDataBundle("/InstructorCourseStudentDetailsPageE2ETest_SqlEntities.json"));
    }


    @Test
    @Override
    public void testAll() {
        ______TS("verify loaded details");
        Student student = testData.students.get("ICSDet.jose.tmms");
        AppUrl viewPageUrl = getStudentDetailsViewPageUrl(student.getEmail());
        InstructorCourseStudentDetailsViewPage viewPage =
                loginToPage(viewPageUrl, InstructorCourseStudentDetailsViewPage.class,
                        testData.instructors.get("ICSDet.instr").getGoogleId());

        viewPage.verifyStudentDetails(student);

        ______TS("verify loaded details - another student");
        student = testData.students.get("ICSDet.benny.c");
        viewPageUrl = getStudentDetailsViewPageUrl(student.getEmail());
        viewPage = getNewPageInstance(viewPageUrl, InstructorCourseStudentDetailsViewPage.class);

        viewPage.verifyStudentDetails(student);
    }

    private AppUrl getStudentDetailsViewPageUrl(String studentEmail) {
        return createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
                .withCourseId(testData.courses.get("ICSDet.CS2104").getId())
                .withStudentEmail(studentEmail);
    }
}
