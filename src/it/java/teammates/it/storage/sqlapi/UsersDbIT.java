package teammates.it.storage.sqlapi;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.HibernateUtil;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlapi.UsersDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.User;

/**
 * SUT: {@link UsersDb}.
 */
public class UsersDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();
    private final AccountsDb accountsDb = AccountsDb.inst();

    private Course course;
    private Instructor instructor;
    private Student student;

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        coursesDb.createCourse(course);

        Section section = new Section(course, "test-section");
        course.addSection(section);
        Team team = new Team(section, "test-team");
        section.addTeam(team);
        coursesDb.updateCourse(course);

        Account instructorAccount = new Account("instructor-name", "valid-instructor@email.tmt");
        accountsDb.createAccount(instructorAccount);
        instructor = getTypicalInstructor();
        instructor.setCourse(course);
        usersDb.createInstructor(instructor);
        instructor.setAccount(instructorAccount);

        Account studentAccount = new Account("student-name", "valid-student@email.tmt");
        accountsDb.createAccount(studentAccount);
        student = getTypicalStudent();
        student.setCourse(course);
        student.setTeam(team);
        usersDb.createStudent(student);
        student.setAccount(studentAccount);

        HibernateUtil.flushSession();
    }

    @Test
    public void testGetInstructor() {
        ______TS("success: gets an instructor that already exists");
        Instructor actualInstructor = usersDb.getInstructor(instructor.getId());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor that does not exist");
        UUID nonExistentId = generateDifferentUuid(actualInstructor.getId());
        actualInstructor = usersDb.getInstructor(nonExistentId);
        assertNull(actualInstructor);

        ______TS("success: gets an instructor by courseId and email");
        actualInstructor = usersDb.getInstructorForEmail(instructor.getCourseId(), instructor.getEmail());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by courseId and email that does not exist");
        actualInstructor = usersDb.getInstructorForEmail(instructor.getCourseId(), "does-not-exist@teammates.tmt");
        assertNull(actualInstructor);

        ______TS("success: gets an instructor by regKey");
        actualInstructor = usersDb.getInstructorByRegKey(instructor.getRegKey());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by regKey that does not exist");
        actualInstructor = usersDb.getInstructorByRegKey("invalid-reg-key");
        assertNull(actualInstructor);

        ______TS("success: gets an instructor by accountId");
        actualInstructor = usersDb.getInstructorByAccountId(instructor.getCourseId(),
                instructor.getAccount().getAccountId());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by accountId that does not exist");
        actualInstructor = usersDb.getInstructorByAccountId(instructor.getCourseId(), "invalid-account-id");
        assertNull(actualInstructor);
    }

    @Test
    public void testGetStudent() {
        ______TS("success: gets a student that already exists");
        Student actualStudent = usersDb.getStudent(student.getId());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student that does not exist");
        UUID nonExistentId = generateDifferentUuid(actualStudent.getId());
        actualStudent = usersDb.getStudent(nonExistentId);
        assertNull(actualStudent);

        ______TS("success: gets a student by courseId and email");
        actualStudent = usersDb.getStudentForEmail(student.getCourseId(), student.getEmail());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student by courseId and email that does not exist");
        actualStudent = usersDb.getStudentForEmail(student.getCourseId(), "does-not-exist@teammates.tmt");
        assertNull(actualStudent);

        ______TS("success: gets a student by regKey");
        actualStudent = usersDb.getStudentByRegKey(student.getRegKey());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student by regKey that does not exist");
        actualStudent = usersDb.getStudentByRegKey("invalid-reg-key");
        assertNull(actualStudent);

        ______TS("success: gets a student by accountId");
        actualStudent = usersDb.getStudentByAccountId(student.getCourseId(), student.getAccount().getAccountId());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student by accountId that does not exist");
        actualStudent = usersDb.getStudentByAccountId(student.getCourseId(), "invalid-account-id");
        assertNull(actualStudent);
    }

    @Test
    public void testGetAllUsersByAccountId() throws InvalidParametersException, EntityAlreadyExistsException {
        ______TS("success: gets all instructors and students by accountId");
        Account userSharedAccount = new Account("user-name", "valid-user@email.tmt");
        accountsDb.createAccount(userSharedAccount);

        Instructor firstInstructor = getTypicalInstructor();
        firstInstructor.setEmail("valid-instructor-1@email.tmt");
        usersDb.createInstructor(firstInstructor);
        firstInstructor.setAccount(userSharedAccount);

        Instructor secondInstructor = getTypicalInstructor();
        secondInstructor.setEmail("valid-instructor-2@email.tmt");
        usersDb.createInstructor(secondInstructor);
        secondInstructor.setAccount(userSharedAccount);

        Student firstStudent = getTypicalStudent();
        firstStudent.setEmail("valid-student-1@email.tmt");
        usersDb.createStudent(firstStudent);
        firstStudent.setAccount(userSharedAccount);

        Student secondStudent = getTypicalStudent();
        secondStudent.setEmail("valid-student-2@email.tmt");
        usersDb.createStudent(secondStudent);
        secondStudent.setAccount(userSharedAccount);

        List<User> users = usersDb.getAllUsersByAccountId(userSharedAccount.getAccountId());
        assertEquals(4, users.size());
        assertTrue(List.of(firstInstructor, secondInstructor, firstStudent, secondStudent).containsAll(users));

        List<Instructor> instructors = usersDb.getAllInstructorsByAccountId(userSharedAccount.getAccountId());
        assertEquals(2, instructors.size());
        assertTrue(List.of(firstInstructor, secondInstructor).containsAll(instructors));

        List<Student> students = usersDb.getAllStudentsByAccountId(userSharedAccount.getAccountId());
        assertEquals(2, students.size());
        assertTrue(List.of(firstStudent, secondStudent).containsAll(students));

        ______TS("success: gets all instructors and students by account id that does not exist");
        List<User> emptyUsers = usersDb.getAllUsersByAccountId("00000000-0000-4000-8000-00000000dead");

        assertEquals(0, emptyUsers.size());
    }

    @Test
    public void testGetStudentsForSection()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        ______TS("success: typical case");
        Section firstSection = new Section(course, "section-name1");
        course.addSection(firstSection);
        Team firstTeam = new Team(firstSection, "team-name1");
        firstSection.addTeam(firstTeam);

        Section secondSection = new Section(course, "section-name2");
        course.addSection(secondSection);
        Team secondTeam = new Team(secondSection, "team-name2");
        secondSection.addTeam(secondTeam);

        coursesDb.updateCourse(course);

        Student firstStudent = getTypicalStudent();
        firstStudent.setEmail("valid-student-1@email.tmt");
        firstStudent.setTeam(firstTeam);
        usersDb.createStudent(firstStudent);

        Student secondStudent = getTypicalStudent();
        secondStudent.setEmail("valid-student-2@email.tmt");
        secondStudent.setTeam(firstTeam);
        usersDb.createStudent(secondStudent);

        Student thirdStudent = getTypicalStudent();
        thirdStudent.setEmail("valid-student-3@email.tmt");
        thirdStudent.setTeam(secondTeam);
        usersDb.createStudent(thirdStudent);

        List<Student> expectedStudents = List.of(firstStudent, secondStudent);

        List<Student> actualStudents = usersDb.getStudentsForSection(firstSection.getName(), course.getId());

        assertEquals(expectedStudents.size(), actualStudents.size());
        assertTrue(expectedStudents.containsAll(actualStudents));
    }

    @Test
    public void testGetStudentsForTeam()
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        ______TS("success: typical case");
        Section firstSection = new Section(course, "section-name1");
        course.addSection(firstSection);
        Team firstTeam = new Team(firstSection, "team-name1");
        firstSection.addTeam(firstTeam);

        Section secondSection = new Section(course, "section-name2");
        course.addSection(secondSection);
        Team secondTeam = new Team(secondSection, "team-name2");
        secondSection.addTeam(secondTeam);

        coursesDb.updateCourse(course);

        Student firstStudent = getTypicalStudent();
        firstStudent.setEmail("valid-student-1@email.tmt");
        firstStudent.setTeam(firstTeam);
        usersDb.createStudent(firstStudent);

        Student secondStudent = getTypicalStudent();
        secondStudent.setEmail("valid-student-2@email.tmt");
        secondStudent.setTeam(firstTeam);
        usersDb.createStudent(secondStudent);

        Student thirdStudent = getTypicalStudent();
        thirdStudent.setEmail("valid-student-3@email.tmt");
        thirdStudent.setTeam(secondTeam);
        usersDb.createStudent(thirdStudent);

        List<Student> expectedStudents = List.of(firstStudent, secondStudent);

        List<Student> actualStudents = usersDb.getStudentsForTeam(firstTeam.getName(), course.getId());

        assertEquals(expectedStudents.size(), actualStudents.size());
        assertTrue(expectedStudents.containsAll(actualStudents));
    }

    @Test
    public void testGetStudentsByAccountId()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Course course2 = new Course("course-id-2", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        Student student2 = getTypicalStudent();
        Account account = new Account(student.getName(), student.getEmail());

        accountsDb.createAccount(account);
        coursesDb.createCourse(course2);
        student.setAccount(account);
        student2.setAccount(account);
        student2.setCourse(course2);
        usersDb.createStudent(student2);

        List<Student> expectedStudents = List.of(student, student2);

        List<Student> actualStudents = usersDb.getStudentsByAccountId(student.getAccountId());

        assertEquals(expectedStudents.size(), actualStudents.size());
        assertTrue(expectedStudents.containsAll(actualStudents));
    }
}
