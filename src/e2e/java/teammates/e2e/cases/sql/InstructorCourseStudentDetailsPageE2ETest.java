package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsViewPageSql;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE}.
 */
public class InstructorCourseStudentDetailsPageE2ETest extends BaseE2ETestCase {
    private Student student;
    private Student otherStudent;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                        loadSqlDataBundle("/InstructorCourseStudentDetailsPageE2ETestSql.json"));

        student = testData.students.get("ICSDet.jose.tmms");
        otherStudent = testData.students.get("ICSDet.benny.c");
    }

    @Test
    @Override
    public void testAll() {
        ______TS("verify loaded details");
        AppUrl viewPageUrl = getStudentDetailsViewPageUrl(student.getEmail());
        InstructorCourseStudentDetailsViewPageSql viewPage =
                loginToPage(viewPageUrl, InstructorCourseStudentDetailsViewPageSql.class,
                        testData.instructors.get("ICSDet.instr").getGoogleId());

        viewPage.verifyStudentDetails(student);

        ______TS("verify loaded details - another student");
        viewPageUrl = getStudentDetailsViewPageUrl(otherStudent.getEmail());
        viewPage = getNewPageInstance(viewPageUrl, InstructorCourseStudentDetailsViewPageSql.class);

        viewPage.verifyStudentDetails(otherStudent);
    }

    private AppUrl getStudentDetailsViewPageUrl(String studentEmail) {
        return createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE)
                .withCourseId(testData.courses.get("ICSDet.CS2104").getId())
                .withStudentEmail(studentEmail);
    }
}
