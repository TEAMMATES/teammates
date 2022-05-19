package teammates.test;

import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.logic.api.BaseLogicTest;
import teammates.storage.api.StudentsDb;

/**
 * Test whether a student can be deleted from a course && database.
 */
public class DeleteStudent extends BaseLogicTest {

    private final StudentsDb studentsDb = StudentsDb.inst();

    @Test
    public void createStudentTest() throws Exception {

        StudentAttributes student = StudentAttributes
                .builder("valid-course", "validEmail@gmail.com")
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

        ______TS("delete student not in database");
        studentsDb.deleteStudent(student.getCourse() + "not in database", student.getEmail());
        verifyPresentInDatabase(student);

        ______TS("delete student not in database");
        studentsDb.deleteStudent(student.getCourse(), student.getEmail() + "not in database");
        verifyPresentInDatabase(student);

        ______TS("delete existing student");
        studentsDb.deleteStudent(student.getCourse(), student.getEmail());
        verifyAbsentInDatabase(student);

    }
}
