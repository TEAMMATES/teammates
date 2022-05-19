
package teammates.test;

import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.api.BaseLogicTest;
import teammates.storage.api.StudentsDb;

/**
 * Test whether a student can be added to a course && database
 */
public class CreateStudentInCourseTest extends BaseLogicTest {

    private final StudentsDb studentsDb = StudentsDb.inst();

    @Test
    public void CreateStudentTest() throws Exception {
        StudentAttributes student = StudentAttributes
                .builder("valid_course", "validEmail@gmail.com")
                .withName("valid_student_name")
                .withComment("")
                .withTeamName("valid_team_name")
                .withSectionName("valid_section_name")
                .withGoogleId("valid_google_id")
                .build();

        studentsDb.deleteStudent(student.getCourse(), student.getEmail());
        studentsDb.createEntity(student);

        ______TS("assert whether a student has been created and added to the database");
        verifyPresentInDatabase(student);

        ______TS("check if the student we just created is in the database");
        StudentAttributes retrievedStudent = studentsDb.getStudentForGoogleId(student.getCourse(), student.getGoogleId());
        assertNotNull(retrievedStudent);

        ______TS("fail: can't add a null object as a student");
        assertThrows(AssertionError.class, () -> studentsDb.createEntity(null));
    }
}


