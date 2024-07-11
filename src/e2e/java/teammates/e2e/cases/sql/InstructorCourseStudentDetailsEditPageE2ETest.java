package teammates.e2e.cases.sql;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorCourseStudentDetailsEditPageSql;
import teammates.e2e.util.TestProperties;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE}.
 */
public class InstructorCourseStudentDetailsEditPageE2ETest extends BaseE2ETestCase {
    private Student student;
    private Student otherStudent;
    private Course course;

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                        loadSqlDataBundle("/InstructorCourseStudentDetailsEditPageE2ETestSql.json"));

        student = testData.students.get("ICSDetEdit.jose.tmms");
        otherStudent = testData.students.get("ICSDetEdit.benny.c");
        course = testData.courses.get("ICSDetEdit.CS2104");
    }

    @Test
    @Override
    public void testAll() {
        AppUrl editPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_EDIT_PAGE)
                .withCourseId(course.getId())
                .withStudentEmail(student.getEmail());
        InstructorCourseStudentDetailsEditPageSql editPage =
                loginToPage(editPageUrl, InstructorCourseStudentDetailsEditPageSql.class,
                        testData.instructors.get("ICSDetEdit.instr").getGoogleId());

        ______TS("verify loaded data");
        editPage.verifyStudentDetails(student);

        ______TS("edit student details");
        Team otherTeam = testData.teams.get("tm.e2e.ICSDetEdit.CS2104-SectionB-Team100");
        student.setName("edited name");
        student.setTeam(otherTeam);
        student.setComments("edited comment");
        editPage.editStudentDetails(student);

        editPage.verifyStatusMessage("Student has been updated");
        verifyPresentInDatabase(student);

        ______TS("cannot edit to an existing email");
        editPage = getNewPageInstance(editPageUrl, InstructorCourseStudentDetailsEditPageSql.class);
        editPage.editStudentEmailAndResendLinks(otherStudent.getEmail());

        editPage.verifyStatusMessage("Trying to update to an email that is already in use");

        ______TS("edit email and resend links");
        String newEmail = TestProperties.TEST_EMAIL;
        student.setEmail(newEmail);
        student.setGoogleId(null);
        editPage.editStudentEmailAndResendLinks(newEmail);

        editPage.verifyStatusMessage("Student has been updated and email sent");
        verifyPresentInDatabase(student);
        verifyEmailSent(newEmail, "TEAMMATES: Summary of course ["
                + course.getName() + "][Course ID: " + course.getId() + "]");
    }
}
