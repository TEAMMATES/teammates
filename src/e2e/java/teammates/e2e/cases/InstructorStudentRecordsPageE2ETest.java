package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_RECORDS_PAGE}.
 */
public class InstructorStudentRecordsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = removeAndRestoreDataBundle(
                loadDataBundle("/InstructorStudentRecordsPageE2ETest.json"));
    }

    @Test
    @Override
    public void testAll() {

        ______TS("verify loaded data: student details");

        Instructor instructor = testData.instructors.get("teammates.test.CS2104");
        Student student = testData.students.get("benny.c.tmms@ISR.CS2104");

        String instructorId = instructor.getGoogleId();
        String courseId = instructor.getCourseId();
        String studentEmail = student.getEmail();

        AppUrl recordsPageUrl = createFrontendUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                .withCourseId(courseId)
                .withStudentEmail(studentEmail);

        InstructorStudentRecordsPage recordsPage =
                loginToPage(recordsPageUrl, InstructorStudentRecordsPage.class, instructorId);

        recordsPage.verifyStudentDetails(student);
    }
}
