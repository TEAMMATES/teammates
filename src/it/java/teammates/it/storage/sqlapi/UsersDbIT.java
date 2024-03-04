package teammates.it.storage.sqlapi;

import java.util.ArrayList;
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
    private Section section;

    @BeforeMethod
    @Override
    public void setUp() throws Exception {
        super.setUp();

        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        coursesDb.createCourse(course);

        section = new Section(course, "test-section");
        course.addSection(section);
        Team team = new Team(section, "test-team");
        section.addTeam(team);
        coursesDb.updateCourse(course);

        Account instructorAccount = new Account("instructor-account", "instructor-name", "valid-instructor@email.tmt");
        accountsDb.createAccount(instructorAccount);
        instructor = getTypicalInstructor();
        instructor.setCourse(course);
        usersDb.createInstructor(instructor);
        instructor.setAccount(instructorAccount);

        Account studentAccount = new Account("student-account", "student-name", "valid-student@email.tmt");
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

        ______TS("success: gets an instructor by googleId");
        actualInstructor = usersDb.getInstructorByGoogleId(instructor.getCourseId(), instructor.getAccount().getGoogleId());
        verifyEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by googleId that does not exist");
        actualInstructor = usersDb.getInstructorByGoogleId(instructor.getCourseId(), "invalid-google id");
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

        ______TS("success: gets a student by googleId");
        actualStudent = usersDb.getStudentByGoogleId(student.getCourseId(), student.getAccount().getGoogleId());
        verifyEquals(student, actualStudent);

        ______TS("success: gets a student by googleId that does not exist");
        actualStudent = usersDb.getStudentByGoogleId(student.getCourseId(), "invalid-google id");
        assertNull(actualStudent);
    }

    @Test
    public void testGetAllUsersByGoogleId() throws InvalidParametersException, EntityAlreadyExistsException {
        ______TS("success: gets all instructors and students by googleId");
        Account userSharedAccount = new Account("user-account", "user-name", "valid-user@email.tmt");
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

        List<User> users = usersDb.getAllUsersByGoogleId(userSharedAccount.getGoogleId());
        assertEquals(4, users.size());
        assertTrue(List.of(firstInstructor, secondInstructor, firstStudent, secondStudent).containsAll(users));

        List<Instructor> instructors = usersDb.getAllInstructorsByGoogleId(userSharedAccount.getGoogleId());
        assertEquals(2, instructors.size());
        assertTrue(List.of(firstInstructor, secondInstructor).containsAll(instructors));

        List<Student> students = usersDb.getAllStudentsByGoogleId(userSharedAccount.getGoogleId());
        assertEquals(2, students.size());
        assertTrue(List.of(firstStudent, secondStudent).containsAll(students));

        ______TS("success: gets all instructors and students by googleId that does not exist");
        List<User> emptyUsers = usersDb.getAllUsersByGoogleId("non-exist-id");

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
    public void testGetStudentsByGoogleId()
            throws EntityAlreadyExistsException, InvalidParametersException {
        Course course2 = new Course("course-id-2", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        Student student2 = getTypicalStudent();
        Account account = new Account("google-id", student.getName(), student.getEmail());

        accountsDb.createAccount(account);
        coursesDb.createCourse(course2);
        student.setAccount(account);
        student2.setAccount(account);
        student2.setCourse(course2);
        usersDb.createStudent(student2);

        List<Student> expectedStudents = List.of(student, student2);

        List<Student> actualStudents = usersDb.getStudentsByGoogleId(student.getGoogleId());

        assertEquals(expectedStudents.size(), actualStudents.size());
        assertTrue(expectedStudents.containsAll(actualStudents));
    }

    @Test
    public void testSqlInjectionInCreateInstructor() throws Exception {
        ______TS("SQL Injection test in createInstructor email field");

        String email = "test';/**/DROP/**/TABLE/**/users;/**/--@gmail.com";
        Instructor instructorEmail = getTypicalInstructor();
        instructorEmail.setEmail(email);

        // The regex check should fail and throw an exception
        assertThrows(InvalidParametersException.class,
                () -> usersDb.createInstructor(instructorEmail));

        ______TS("SQL Injection test in createInstructor name field");
        Instructor instructorName = getTypicalInstructor();
        instructorName.setEmail("ins.usersdbit.1@gmail.com");
        String name = "test';/**/DROP/**/TABLE/**/accounts;/**/--";
        instructorName.setName(name);
        String instructorNameRegKey = "ins.usersdbit.regkey";
        instructorName.setRegKey(instructorNameRegKey);

        usersDb.createInstructor(instructorName);

        HibernateUtil.flushSession();

        // The system should treat the input as a plain text string
        Instructor actualInstructor = usersDb.getInstructorByRegKey(instructorNameRegKey);
        assertEquals(actualInstructor.getName(), name);

        ______TS("SQL Injection test in createInstructor displayName field");
        Instructor instructorDisplayName = getTypicalInstructor();
        instructorDisplayName.setEmail("ins.usersdbit.2@gmail.com");
        String displayName = "test';/**/DROP/**/TABLE/**/accounts;/**/--";
        instructorDisplayName.setDisplayName(displayName);
        String instructorRegKeyDisplayName = "ins.usersdbit.regkey2";
        instructorDisplayName.setRegKey(instructorRegKeyDisplayName);

        usersDb.createInstructor(instructorDisplayName);

        HibernateUtil.flushSession();

        // The system should treat the input as a plain text string
        Instructor actualInstructorDisplayName = usersDb.getInstructorByRegKey(instructorRegKeyDisplayName);
        assertEquals(actualInstructorDisplayName.getDisplayName(), displayName);
    }

    @Test
    public void testSqlInjectionInCreateStudent() throws Exception {
        ______TS("SQL Injection test in createStudent email field");

        String email = "test';/**/DROP/**/TABLE/**/users;/**/--@gmail.com";
        Student studentEmail = getTypicalStudent();
        studentEmail.setEmail(email);

        // The regex check should fail and throw an exception
        assertThrows(InvalidParametersException.class,
                () -> usersDb.createStudent(studentEmail));

        ______TS("SQL Injection test in createStudent name field");
        Student studentName = getTypicalStudent();
        studentName.setEmail("ins.usersdbit.3@gmail.com");
        String name = "test';/**/DROP/**/TABLE/**/accounts;/**/--";
        studentName.setName(name);
        String studentNameRegKey = "ins.usersdbit.regkey3";
        studentName.setRegKey(studentNameRegKey);

        usersDb.createStudent(studentName);

        HibernateUtil.flushSession();

        // The system should treat the input as a plain text string
        Student actualStudent = usersDb.getStudentByRegKey(studentNameRegKey);
        assertEquals(actualStudent.getName(), name);
    }

    @Test
    public void testSqlInjectionInGetInstructorByRegKey() throws Exception {
        ______TS("SQL Injection test in getInstructorByRegKey");

        Instructor instructor = getTypicalInstructor();
        instructor.setEmail("instructorregkey.usersdbit@gmail.com");

        usersDb.createInstructor(instructor);

        // The system should treat the input as a plain text string
        String regKey = "test' OR 1 = 1; --";
        Instructor actualInstructor = usersDb.getInstructorByRegKey(regKey);
        assertNull(actualInstructor);
    }

    @Test
    public void testSqlInjectionInGetInstructorByGoogleId() throws Exception {
        ______TS("SQL Injection test in getInstructorByGoogleId courseId field");
        String injection = "test' OR 1 = 1; --";
        assertNull(usersDb.getInstructorByGoogleId(injection, instructor.getAccount().getGoogleId()));

        ______TS("SQL Injection test in getInstructorByGoogleId googleId field");
        assertNull(usersDb.getInstructorByGoogleId(instructor.getCourseId(), injection));
    }

    @Test
    public void testSqlInjectionInGetInstructorsDisplayedToStudents() throws Exception {
        ______TS("SQL Injection test in getInstructorsDisplayedToStudents courseId field");
        String injection = "test' OR 1 = 1; --";
        assertEquals(usersDb.getInstructorsDisplayedToStudents(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetStudentByRegKey() throws Exception {
        ______TS("SQL Injection test in getStudentByRegKey");
        String regKey = "test' OR 1 = 1; --";
        Student student = getTypicalStudent();
        student.setEmail("studentregkey.usersdbit@gmail.com");
        student.setRegKey(regKey);

        usersDb.createStudent(student);

        // The system should treat the input as a plain text string
        Student actualStudent = usersDb.getStudentByRegKey(regKey);
        assertEquals(actualStudent.getRegKey(), regKey);
    }

    @Test
    public void testSqlInjectionInGetStudentByGoogleId() throws Exception {
        String injection = "test' OR 1 = 1; --";

        ______TS("SQL Injection test in getStudentByGoogleId courseId field");
        assertNull(usersDb.getStudentByGoogleId(injection, student.getAccount().getGoogleId()));

        ______TS("SQL Injection test in getStudentByGoogleId googleId field");
        assertNull(usersDb.getInstructorByGoogleId(student.getCourseId(), injection));
    }

    @Test
    public void testSqlInjectionInGetStudentsByGoogleId() throws Exception {
        String injection = "test' OR 1 = 1; --";

        ______TS("SQL Injection test in getStudentsByGoogleId googleId field");
        assertEquals(usersDb.getStudentsByGoogleId(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetStudentsByTeamName() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getStudentsByTeamName teamName field");
        assertEquals(usersDb.getStudentsByTeamName(injection, student.getCourseId()).size(), 0);

        ______TS("SQL Injection test in getStudentsByTeamName courseId field");
        assertEquals(usersDb.getStudentsByTeamName(student.getTeamName(), injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetAllUsersByGoogleId() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getAllUsersByGoogleId googleId field");
        assertEquals(usersDb.getAllUsersByGoogleId(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetAllInstructorsByGoogleId() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getAllInstructorsByGoogleId googleId field");
        assertEquals(usersDb.getAllInstructorsByGoogleId(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetAllStudentsByGoogleId() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getAllStudentsByGoogleId googleId field");
        assertEquals(usersDb.getAllStudentsByGoogleId(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetInstructorsForCourse() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getInstructorsForCourse courseId field");
        assertEquals(usersDb.getInstructorsForCourse(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetStudentsForCourse() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getStudentsForCourse courseId field");
        assertEquals(usersDb.getStudentsForCourse(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetInstructorForEmail() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getInstructorForEmail courseId field");
        assertNull(usersDb.getInstructorForEmail(injection, instructor.getEmail()));

        ______TS("SQL Injection test in getInstructorForEmail userEmail field");
        assertNull(usersDb.getInstructorForEmail(instructor.getCourseId(), injection));
    }

    @Test
    public void testSqlInjectionInGetInstructorsForEmails() throws Exception {
        String injection = "test' OR 1 = 1; --";
        List<String> emails = new ArrayList<>();
        emails.add(instructor.getEmail());
        ______TS("SQL Injection test in getInstructorsForEmails courseId field");
        assertEquals(usersDb.getInstructorsForEmails(injection, emails).size(), 0);

        List<String> injections = new ArrayList<>();
        injections.add("test' OR 1 = 1; --");
        ______TS("SQL Injection test in getInstructorsForEmails userEmails field");
        assertEquals(usersDb.getInstructorsForEmails(instructor.getCourseId(), injections).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetStudentForEmail() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getStudentForEmail courseId field");
        assertNull(usersDb.getStudentForEmail(injection, student.getEmail()));

        ______TS("SQL Injection test in getStudentForEmail userEmail field");
        assertNull(usersDb.getStudentForEmail(student.getCourseId(), injection));
    }

    @Test
    public void testSqlInjectionInGetStudentsForEmails() throws Exception {
        String injection = "test' OR 1 = 1; --";
        List<String> emails = new ArrayList<>();
        emails.add(student.getEmail());
        ______TS("SQL Injection test in getStudentsForEmails courseId field");
        assertEquals(usersDb.getStudentsForEmails(injection, emails).size(), 0);

        List<String> injections = new ArrayList<>();
        injections.add("test' OR 1 = 1; --");
        ______TS("SQL Injection test in getStudentsForEmails userEmails field");
        assertEquals(usersDb.getStudentsForEmails(student.getCourseId(), injections).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetAllStudentsForEmail() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getAllStudentsForEmail email field");
        assertEquals(usersDb.getAllStudentsForEmail(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetInstructorsForGoogleId() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getInstructorsForGoogleId googleId field");
        assertEquals(usersDb.getInstructorsForGoogleId(injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetStudentsForSection() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getStudentsForSection sectionName field");
        assertEquals(usersDb.getStudentsForSection(injection, student.getCourseId()).size(), 0);

        ______TS("SQL Injection test in getStudentsForSection courseId field");
        assertEquals(usersDb.getStudentsForSection(student.getSectionName(), injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetStudentsForTeam() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getStudentsForTeam teamName field");
        assertEquals(usersDb.getStudentsForTeam(injection, student.getCourseId()).size(), 0);

        ______TS("SQL Injection test in getStudentsForTeam courseId field");
        assertEquals(usersDb.getStudentsForTeam(student.getTeamName(), injection).size(), 0);
    }

    @Test
    public void testSqlInjectionInGetStudentCountForTeam() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getStudentCountForTeam teamName field");
        assertEquals(usersDb.getStudentCountForTeam(injection, student.getCourseId()), 0);

        ______TS("SQL Injection test in getStudentCountForTeam courseId field");
        assertEquals(usersDb.getStudentCountForTeam(student.getTeamName(), injection), 0);
    }

    @Test
    public void testSqlInjectionInGetSection() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getSection courseId field");
        assertNull(usersDb.getSection(injection, section.getName()));

        ______TS("SQL Injection test in getSection sectionName field");
        assertNull(usersDb.getSection(course.getId(), injection));
    }

    @Test
    public void testSqlInjectionInGetTeam() throws Exception {
        String injection = "test' OR 1 = 1; --";
        ______TS("SQL Injection test in getTeam teamName field");
        assertNull(usersDb.getTeam(section, injection));
    }

    @Test
    public void testSqlInjectionInGetSectionOrCreate() throws Exception {
        ______TS("SQL Injection test in getSection sectionName field");
        // Attempt to use SQL commands in teamName field
        String injection = "test'; DROP TABLE users; --";
        Section actualSection = usersDb.getSectionOrCreate(course.getId(), injection);

        // The system should treat teamName as a plain text string
        assertEquals(actualSection.getName(), injection);
    }

    @Test
    public void testSqlInjectionInGetTeamOrCreate() throws Exception {
        ______TS("SQL Injection test in getTeamOrCreate teamName field");
        // Attempt to use SQL commands in teamName field
        String injection = "test'; DROP TABLE users; --";
        Team actualTeam = usersDb.getTeamOrCreate(section, injection);

        // The system should treat teamName as a plain text string
        assertEquals(actualTeam.getName(), injection);
    }

    @Test
    public void testSqlInjectionInUpdateStudent() throws Exception {
        ______TS("SQL Injection test in updateStudent email field");

        String email = "test';/**/DROP/**/TABLE/**/users;/**/--@gmail.com";
        Student studentEmail = getTypicalStudent();
        studentEmail.setEmail(email);

        // The regex check should fail and throw an exception
        assertThrows(InvalidParametersException.class,
                () -> usersDb.updateStudent(studentEmail));

        ______TS("SQL Injection test in updateStudent name field");
        String injection = "newName'; DROP TABLE name; --";
        student.setName(injection);
        usersDb.updateStudent(student);

        HibernateUtil.flushSession();

        // The system should treat the input as a plain text string
        Student actualStudent = usersDb.getStudentByGoogleId(student.getCourseId(), student.getGoogleId());
        assertEquals(actualStudent.getName(), injection);

        ______TS("SQL Injection test in updateStudent comments field");
        student.setComments(injection);
        usersDb.updateStudent(student);

        HibernateUtil.flushSession();

        // The system should treat the input as a plain text string
        actualStudent = usersDb.getStudentByGoogleId(student.getCourseId(), student.getGoogleId());
        assertEquals(actualStudent.getComments(), injection);
    }
}
