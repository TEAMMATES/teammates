package teammates.it.storage.sqlapi;

import static org.mockito.Mockito.mock;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;

/**
 * SUT: {@link UsersDb}.
 */
public class UsersDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private static final int USER_ID = 1;

    private final UsersDb usersDb = UsersDb.inst();

    @Test
    public void testGetInstructor() throws InvalidParametersException, EntityAlreadyExistsException {
        ______TS("success: gets an instructor that already exists");

        Course course = mock(Course.class);
        Team team = mock(Team.class);
        InstructorPrivileges instructorPrivileges = mock(InstructorPrivileges.class);
        Instructor newInstructor = new Instructor(course, team, "instructor-name", "instructor-email",
                false, "instructor-display-name",
                InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER, instructorPrivileges);

        newInstructor.setId(USER_ID);
        usersDb.createInstructor(newInstructor);

        Integer instructorId = newInstructor.getId();
        Instructor actualInstructor = usersDb.getInstructor(instructorId);
        verifyEquals(newInstructor, actualInstructor);

        ______TS("success: gets an instructor that does not exist");
        Integer nonExistentId = Integer.MIN_VALUE;
        Instructor nonExistentInstructor = usersDb.getInstructor(nonExistentId);
        assertNull(nonExistentInstructor);
    }

    @Test
    public void testGetStudent() throws InvalidParametersException, EntityAlreadyExistsException {
        ______TS("success: gets a student that already exists");

        Course course = mock(Course.class);
        Team team = mock(Team.class);
        Student newStudent = new Student(course, team, "student-name", "student-email", "comments");

        newStudent.setId(USER_ID);
        usersDb.createStudent(newStudent);

        Integer studentId = newStudent.getId();
        Student actualstudent = usersDb.getStudent(studentId);
        verifyEquals(newStudent, actualstudent);

        ______TS("success: gets an student that does not exist");
        Integer nonExistentId = Integer.MIN_VALUE;
        Student nonExistentstudent = usersDb.getStudent(nonExistentId);
        assertNull(nonExistentstudent);
    }
}
