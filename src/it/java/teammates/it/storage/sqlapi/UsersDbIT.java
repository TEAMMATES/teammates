package teammates.it.storage.sqlapi;

import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;

/**
 * SUT: {@link UsersDb}.
 */
public class UsersDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();

    private Course course;
    private Instructor instructor;
    private Student student;

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        coursesDb.createCourse(course);

        instructor = getTypicalInstructor();
        usersDb.createInstructor(instructor);

        student = getTypicalStudent();
        usersDb.createStudent(student);

        HibernateUtil.flushSession();
    }

    @Test
    public void testGetInstructor() {
        ______TS("success: gets an instructor that already exists");
        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor that does not exist");
        UUID nonExistentId = generateDifferentUuid(actualInstructor.getId());
        Instructor nonExistentInstructor = usersDb.getInstructor(nonExistentId);
        assertNull(nonExistentInstructor);
    }

    @Test
    public void testGetStudent() {
        ______TS("success: gets a student that already exists");
        Student actualstudent = usersDb.getStudent(student.getId());
        verifyEquals(student, actualstudent);

        ______TS("success: gets a student that does not exist");
        UUID nonExistentId = generateDifferentUuid(actualstudent.getId());
        Student nonExistentstudent = usersDb.getStudent(nonExistentId);
        assertNull(nonExistentstudent);
    }

    private Student getTypicalStudent() {
        return new Student(course, "student-name", "valid-student@email.tmt", "comments");
    }

    private Instructor getTypicalInstructor() {
        InstructorPrivileges instructorPrivileges =
                new InstructorPrivileges(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);
        InstructorPermissionRole role = InstructorPermissionRole
                .getEnum(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER);

        return new Instructor(course, "instructor-name", "valid-instructor@email.tmt",
                true, Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR, role, instructorPrivileges);
    }
}
