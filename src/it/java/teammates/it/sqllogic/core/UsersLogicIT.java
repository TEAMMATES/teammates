package teammates.it.sqllogic.core;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.InstructorPermissions;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.sqllogic.core.AccountsLogic;
import teammates.sqllogic.core.CoursesLogic;
import teammates.sqllogic.core.UsersLogic;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.ui.request.InstructorCreateRequest;

/**
 * SUT: {@link UsersLogic}.
 */
public class UsersLogicIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final UsersLogic usersLogic = UsersLogic.inst();

    private final AccountsLogic accountsLogic = AccountsLogic.inst();

    private final CoursesLogic coursesLogic = CoursesLogic.inst();

    private Course course;

    private Account account;

    @BeforeMethod
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        course = getTypicalCourse();
        coursesLogic.createCourse(course);

        account = getTypicalAccount();
        accountsLogic.createAccount(account);
    }

    @Test
    public void testResetInstructorGoogleId()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        instructor.setAccount(account);

        String email = instructor.getEmail();
        String courseId = instructor.getCourseId();
        String googleId = instructor.getGoogleId();

        ______TS("success: reset instructor that does not exist");
        assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetInstructorGoogleId(email, courseId, googleId));

        ______TS("success: reset instructor that exists");
        usersLogic.createInstructor(instructor);
        usersLogic.resetInstructorGoogleId(email, courseId, googleId);

        assertNull(instructor.getAccount());
        assertEquals(0, accountsLogic.getAccountsForEmail(email).size());

        ______TS("found at least one other user with same googleId, should not delete account");
        Account anotherAccount = getTypicalAccount();
        accountsLogic.createAccount(anotherAccount);

        instructor.setCourse(course);
        instructor.setAccount(anotherAccount);

        Student anotherUser = getTypicalStudent();
        anotherUser.setCourse(course);
        anotherUser.setAccount(anotherAccount);

        usersLogic.createStudent(anotherUser);
        usersLogic.resetInstructorGoogleId(email, courseId, googleId);

        assertNull(instructor.getAccount());
        assertEquals(anotherAccount, accountsLogic.getAccountForGoogleId(googleId));
    }

    @Test
    public void testResetStudentGoogleId()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Student student = getTypicalStudent();
        student.setCourse(course);
        student.setAccount(account);

        String email = student.getEmail();
        String courseId = student.getCourseId();
        String googleId = student.getGoogleId();

        ______TS("success: reset student that does not exist");
        assertThrows(EntityDoesNotExistException.class,
                () -> usersLogic.resetStudentGoogleId(email, courseId, googleId));

        ______TS("success: reset student that exists");
        usersLogic.createStudent(student);
        usersLogic.resetStudentGoogleId(email, courseId, googleId);

        assertNull(student.getAccount());
        assertEquals(0, accountsLogic.getAccountsForEmail(email).size());

        ______TS("found at least one other user with same googleId, should not delete account");
        Account anotherAccount = getTypicalAccount();
        accountsLogic.createAccount(anotherAccount);

        student.setCourse(course);
        student.setAccount(anotherAccount);

        Instructor anotherUser = getTypicalInstructor();
        anotherUser.setCourse(course);
        anotherUser.setAccount(anotherAccount);

        usersLogic.createInstructor(anotherUser);
        usersLogic.resetStudentGoogleId(email, courseId, googleId);

        assertNull(student.getAccount());
        assertEquals(anotherAccount, accountsLogic.getAccountForGoogleId(googleId));
    }

    @Test
    public void testUpdateToEnsureValidityOfInstructorsForTheCourse() {
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        instructor.setAccount(account);

        ______TS("success: preserves modify instructor privilege if last instructor in course with privilege");
        InstructorPrivileges privileges = instructor.getPrivileges();
        privileges.updatePrivilege(InstructorPermissions.CAN_MODIFY_INSTRUCTOR, false);
        instructor.setPrivileges(privileges);
        usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(course.getId(), instructor);

        assertFalse(instructor.getPrivileges().isAllowedForPrivilege(
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR));
    }

    @Test
    public void testUpdateInstructorCascade() throws InvalidParametersException, InstructorUpdateException,
            EntityAlreadyExistsException, EntityDoesNotExistException {
        Instructor instructor = getTypicalInstructor();
        instructor.setCourse(course);
        instructor.setAccount(account);

        ______TS("success: typical case");
        usersLogic.createInstructor(instructor);

        String newName = "new name";
        String newEmail = "new_inst_email@newmail.com";
        String newRoleName = "Manager";
        String newDisplayName = "new display name";
        boolean newIsDisplayedToStudent = true;
        InstructorCreateRequest request = new InstructorCreateRequest(
                instructor.getGoogleId(), newName, newEmail, newRoleName, newDisplayName, newIsDisplayedToStudent);

        Instructor updatedInstructor = usersLogic.updateInstructorCascade(instructor.getCourseId(), request);
        assertEquals(newName, updatedInstructor.getName());
        assertEquals(newEmail, updatedInstructor.getEmail());
        assertEquals(newRoleName, updatedInstructor.getRole().getRoleName());
        assertEquals(newDisplayName, updatedInstructor.getDisplayName());
        assertEquals(newIsDisplayedToStudent, updatedInstructor.isDisplayedToStudents());

        ______TS("failure: invalid parameter, original unchanged");
        String originalName = instructor.getName();

        String invalidLongName = "somelongname".repeat(10);
        InstructorCreateRequest requestWithInvalidName = new InstructorCreateRequest(
                instructor.getGoogleId(), invalidLongName, instructor.getEmail(),
                instructor.getRole().getRoleName(), instructor.getDisplayName(), true);

        assertThrows(InvalidParametersException.class,
                () -> usersLogic.updateInstructorCascade(instructor.getCourseId(), requestWithInvalidName));
        assertEquals(originalName, usersLogic.getInstructor(instructor.getId()).getName());
    }

    @Test
    public void testUpdateStudentCascade()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        Student student = getTypicalStudent();
        student.setCourse(course);
        student.setAccount(account);
        Section originalSection = usersLogic.getSectionOrCreate(course.getId(), "section name");
        student.setTeam(usersLogic.getTeamOrCreate(originalSection, "team name"));

        ______TS("success: typical case");
        usersLogic.createStudent(student);

        String newName = "new name";
        String newEmail = "new_stu_email@newmail.com";
        String newComments = "new comments";
        Section newSection = usersLogic.getSectionOrCreate(course.getId(), "new section name");
        Team newTeam = usersLogic.getTeamOrCreate(newSection, "new team name");
        Student studentData = new Student(course, newName, newEmail, newComments, newTeam);
        studentData.setId(student.getId());

        Student updatedStudent = usersLogic.updateStudentCascade(studentData);

        assertEquals(newName, updatedStudent.getName());
        assertEquals(newEmail, updatedStudent.getEmail());
        assertEquals(newComments, updatedStudent.getComments());
        assertEquals(newSection.getId(), updatedStudent.getSection().getId());
        assertEquals(newTeam.getId(), updatedStudent.getTeam().getId());

        ______TS("failure: invalid parameter, original unchanged");
        String originalName = student.getName();

        String invalidLongName = "somelongname".repeat(10);
        Student studentDataWithInvalidName =
                new Student(course, invalidLongName, student.getEmail(), student.getComments(), student.getTeam());
        studentDataWithInvalidName.setId(student.getId());

        assertThrows(InvalidParametersException.class,
                () -> usersLogic.updateStudentCascade(studentDataWithInvalidName));
        assertEquals(originalName, usersLogic.getStudent(student.getId()).getName());
    }

}
