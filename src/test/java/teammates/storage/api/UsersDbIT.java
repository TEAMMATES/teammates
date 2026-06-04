package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.UUID;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import teammates.common.datatransfer.Provider;
import teammates.common.util.Const;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;
import teammates.test.BaseTestCaseWithDatabaseAccess;

/**
 * SUT: {@link UsersDb}.
 */
public class UsersDbIT extends BaseTestCaseWithDatabaseAccess {

    private final UsersDb usersDb = UsersDb.inst();
    private final CoursesDb coursesDb = CoursesDb.inst();
    private final AccountsDb accountsDb = AccountsDb.inst();

    private Course course;
    private Instructor instructor;
    private Student student;

    @BeforeMethod
    public void setUp() {
        course = new Course("course-id", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        Section section = new Section("test-section");
        Team team = new Team("test-team");

        Account instructorAccount = new Account(
                "instructor-account", Provider.TEAMMATES_DEV, "validInstructorSubject",
                "typicalTenantId", "instructor-name", "valid-instructor@email.tmt");
        instructor = getTypicalInstructor();

        Account studentAccount = new Account(
                "student-account", Provider.TEAMMATES_DEV, "validStudentSubject",
                "typicalTenantId", "student-name", "valid-student@email.tmt");
        student = getTypicalStudent();
        inTransaction(() -> {
            coursesDb.persistCourse(course);
            coursesDb.persistSection(section);
            course.addSection(section);
            coursesDb.persistTeam(team);
            section.addTeam(team);

            accountsDb.persistAccount(instructorAccount);
            instructor.setCourse(course);
            usersDb.persistInstructor(instructor);
            instructor.setAccount(instructorAccount);

            accountsDb.persistAccount(studentAccount);
            student.setCourse(course);
            student.setTeam(team);
            usersDb.persistStudent(student);
            student.setAccount(studentAccount);
        });
    }

    @Test
    public void testGetInstructor() {
        ______TS("success: gets an instructor that already exists");
        Instructor actualInstructor = inTransaction(() -> usersDb.getInstructor(instructor.getId()));
        assertEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor that does not exist");
        UUID nonExistentId = generateDifferentUuid(actualInstructor.getId());
        actualInstructor = inTransaction(() -> usersDb.getInstructor(nonExistentId));
        assertNull(actualInstructor);

        ______TS("success: gets an instructor by courseId and email");
        actualInstructor = inTransaction(() -> usersDb.getInstructorForEmail(
                instructor.getCourseId(), instructor.getEmail()));
        assertEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by courseId and email that does not exist");
        actualInstructor = inTransaction(() -> usersDb.getInstructorForEmail(
                instructor.getCourseId(), "does-not-exist@teammates.tmt"));
        assertNull(actualInstructor);

        ______TS("success: gets an instructor by googleId");
        actualInstructor = inTransaction(() -> usersDb.getInstructorByGoogleId(
                instructor.getCourseId(), instructor.getAccount().getGoogleId()));
        assertEquals(instructor, actualInstructor);

        ______TS("success: gets an instructor by googleId that does not exist");
        actualInstructor = inTransaction(() -> usersDb.getInstructorByGoogleId(
                instructor.getCourseId(), "invalid-google id"));
        assertNull(actualInstructor);
    }

    @Test
    public void testGetStudent() {
        ______TS("success: gets a student that already exists");
        Student actualStudent = inTransaction(() -> usersDb.getStudent(student.getId()));
        assertEquals(student, actualStudent);

        ______TS("success: gets a student that does not exist");
        UUID nonExistentId = generateDifferentUuid(actualStudent.getId());
        actualStudent = inTransaction(() -> usersDb.getStudent(nonExistentId));
        assertNull(actualStudent);

        ______TS("success: gets a student by courseId and email");
        actualStudent = inTransaction(() -> usersDb.getStudentForEmail(student.getCourseId(), student.getEmail()));
        assertEquals(student, actualStudent);

        ______TS("success: gets a student by courseId and email that does not exist");
        actualStudent = inTransaction(() -> usersDb.getStudentForEmail(
                student.getCourseId(), "does-not-exist@teammates.tmt"));
        assertNull(actualStudent);

        ______TS("success: gets a student by googleId");
        actualStudent = inTransaction(() -> usersDb.getStudentByGoogleId(
                student.getCourseId(), student.getAccount().getGoogleId()));
        assertEquals(student, actualStudent);

        ______TS("success: gets a student by googleId that does not exist");
        actualStudent = inTransaction(() -> usersDb.getStudentByGoogleId(student.getCourseId(), "invalid-google id"));
        assertNull(actualStudent);
    }

    @Test
    public void testGetAllUsersByGoogleId() {
        ______TS("success: gets all instructors and students by googleId");
        Account userSharedAccount = new Account(
                "user-account", Provider.TEAMMATES_DEV, "valid-user@email.com",
                "typicalTenantId", "user-name", "valid-user@email.tmt");

        Instructor firstInstructor = getTypicalInstructor();
        firstInstructor.setEmail("valid-instructor-1@email.tmt");

        Instructor secondInstructor = getTypicalInstructor();
        secondInstructor.setEmail("valid-instructor-2@email.tmt");

        Section section = new Section("section-name");
        Team team = new Team("team-name");

        Student firstStudent = getTypicalStudent();
        firstStudent.setEmail("valid-student-1@email.tmt");

        Student secondStudent = getTypicalStudent();
        secondStudent.setEmail("valid-student-2@email.tmt");
        inTransaction(() -> {
            accountsDb.persistAccount(userSharedAccount);

            usersDb.persistInstructor(firstInstructor);
            firstInstructor.setAccount(userSharedAccount);
            usersDb.persistInstructor(secondInstructor);
            secondInstructor.setAccount(userSharedAccount);

            coursesDb.persistSection(section);
            course.addSection(section);
            coursesDb.persistTeam(team);
            section.addTeam(team);

            team.addUser(firstStudent);
            usersDb.persistStudent(firstStudent);
            firstStudent.setAccount(userSharedAccount);

            team.addUser(secondStudent);
            usersDb.persistStudent(secondStudent);
            secondStudent.setAccount(userSharedAccount);
        });

        List<User> users = inTransaction(() -> usersDb.getAllUsersByGoogleId(userSharedAccount.getGoogleId()));
        assertEquals(4, users.size());
        assertTrue(List.of(firstInstructor, secondInstructor, firstStudent, secondStudent).containsAll(users));

        List<Instructor> instructors =
                inTransaction(() -> usersDb.getAllInstructorsByGoogleId(userSharedAccount.getGoogleId()));
        assertEquals(2, instructors.size());
        assertTrue(List.of(firstInstructor, secondInstructor).containsAll(instructors));

        List<Student> students = inTransaction(() -> usersDb.getAllStudentsByGoogleId(userSharedAccount.getGoogleId()));
        assertEquals(2, students.size());
        assertTrue(List.of(firstStudent, secondStudent).containsAll(students));

        ______TS("success: gets all instructors and students by googleId that does not exist");
        List<User> emptyUsers = inTransaction(() -> usersDb.getAllUsersByGoogleId("non-exist-id"));

        assertEquals(0, emptyUsers.size());
    }

    @Test
    public void testGetStudentsForSection() {
        ______TS("success: typical case");
        Section firstSection = new Section("section-name1");
        Team firstTeam = new Team("team-name1");

        Section secondSection = new Section("section-name2");
        Team secondTeam = new Team("team-name2");

        Student firstStudent = getTypicalStudent();
        firstStudent.setEmail("valid-student-1@email.tmt");
        firstStudent.setTeam(firstTeam);

        Student secondStudent = getTypicalStudent();
        secondStudent.setEmail("valid-student-2@email.tmt");
        secondStudent.setTeam(firstTeam);

        Student thirdStudent = getTypicalStudent();
        thirdStudent.setEmail("valid-student-3@email.tmt");
        thirdStudent.setTeam(secondTeam);
        inTransaction(() -> {
            coursesDb.persistSection(firstSection);
            course.addSection(firstSection);
            coursesDb.persistTeam(firstTeam);
            firstSection.addTeam(firstTeam);

            coursesDb.persistSection(secondSection);
            course.addSection(secondSection);
            coursesDb.persistTeam(secondTeam);
            secondSection.addTeam(secondTeam);

            usersDb.persistStudent(firstStudent);
            usersDb.persistStudent(secondStudent);
            usersDb.persistStudent(thirdStudent);
        });

        List<Student> expectedStudents = List.of(firstStudent, secondStudent);

        List<Student> actualStudents = inTransaction(() -> usersDb.getStudentsForSection(
                firstSection.getName(), course.getId()));

        assertEquals(expectedStudents.size(), actualStudents.size());
        assertTrue(expectedStudents.containsAll(actualStudents));
    }

    @Test
    public void testGetStudentsForTeam() {
        ______TS("success: typical case");
        Section firstSection = new Section("section-name1");
        Team firstTeam = new Team("team-name1");

        Section secondSection = new Section("section-name2");
        Team secondTeam = new Team("team-name2");

        Student firstStudent = getTypicalStudent();
        firstStudent.setEmail("valid-student-1@email.tmt");
        firstStudent.setTeam(firstTeam);

        Student secondStudent = getTypicalStudent();
        secondStudent.setEmail("valid-student-2@email.tmt");
        secondStudent.setTeam(firstTeam);

        Student thirdStudent = getTypicalStudent();
        thirdStudent.setEmail("valid-student-3@email.tmt");
        thirdStudent.setTeam(secondTeam);
        inTransaction(() -> {
            coursesDb.persistSection(firstSection);
            course.addSection(firstSection);
            coursesDb.persistTeam(firstTeam);
            firstSection.addTeam(firstTeam);

            coursesDb.persistSection(secondSection);
            course.addSection(secondSection);
            coursesDb.persistTeam(secondTeam);
            secondSection.addTeam(secondTeam);

            usersDb.persistStudent(firstStudent);
            usersDb.persistStudent(secondStudent);
            usersDb.persistStudent(thirdStudent);
        });

        List<Student> expectedStudents = List.of(firstStudent, secondStudent);

        List<Student> actualStudents = inTransaction(() -> usersDb.getStudentsForTeam(
                firstTeam.getName(), course.getId()));

        assertEquals(expectedStudents.size(), actualStudents.size());
        assertTrue(expectedStudents.containsAll(actualStudents));
    }

    @Test
    public void testGetStudentsByGoogleId() {
        Course course2 = new Course("course-id-2", "course-name", Const.DEFAULT_TIME_ZONE, "institute");
        Section section = new Section("section-name");
        Team team = new Team("team-name");

        Student student2 = getTypicalStudent();
        Account account = new Account(
                "google-id", Provider.TEAMMATES_DEV, "typicalStudentSubject",
                "typicalTenantId", student.getName(), student.getEmail());

        inTransaction(() -> {
            coursesDb.persistCourse(course2);
            coursesDb.persistSection(section);
            course2.addSection(section);
            coursesDb.persistTeam(team);
            section.addTeam(team);

            team.addUser(student2);
            accountsDb.persistAccount(account);
            usersDb.getStudent(student.getId()).setAccount(account);
            student.setAccount(account);
            student2.setAccount(account);
            student2.setCourse(course2);
            usersDb.persistStudent(student2);
        });

        List<Student> expectedStudents = List.of(student, student2);

        List<Student> actualStudents = inTransaction(() -> usersDb.getStudentsByGoogleId(student.getGoogleId()));

        assertEquals(expectedStudents.size(), actualStudents.size());
        assertTrue(expectedStudents.containsAll(actualStudents));
    }
}
