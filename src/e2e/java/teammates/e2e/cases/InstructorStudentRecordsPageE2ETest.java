package teammates.e2e.cases;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.util.AppUrl;
import teammates.common.util.Const;
import teammates.e2e.pageobjects.InstructorStudentRecordsPage;

/**
 * SUT: {@link Const.WebPageURIs#INSTRUCTOR_STUDENT_RECORDS_PAGE}.
 */
public class InstructorStudentRecordsPageE2ETest extends BaseE2ETestCase {

    @Override
    protected void prepareTestData() {
        testData = loadDataBundle("/InstructorStudentRecordsPageE2ETest.json");
        removeAndRestoreDataBundle(testData);
    }

    @Test
    @Override
    public void testAll() {

        ______TS("verify loaded data: student details");

        InstructorAttributes instructor = testData.instructors.get("teammates.test.CS2104");
        StudentAttributes student = testData.students.get("benny.c.tmms@ISR.CS2104");
        StudentProfileAttributes studentProfile = testData.profiles.get("benny.c.tmms@ISR.CS2104");

        String instructorId = instructor.googleId;
        String courseId = instructor.courseId;
        String studentEmail = student.email;

        AppUrl recordsPageUrl = createUrl(Const.WebPageURIs.INSTRUCTOR_STUDENT_RECORDS_PAGE)
                .withUserId(instructorId)
                .withCourseId(courseId)
                .withStudentEmail(studentEmail);

        InstructorStudentRecordsPage recordsPage = loginAdminToPage(recordsPageUrl, InstructorStudentRecordsPage.class);

        recordsPage.verifyStudentDetails(studentProfile, student);

        // TODO add tests for feedback responses

    }

}
