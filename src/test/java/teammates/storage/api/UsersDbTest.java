package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorQuery;
import teammates.common.datatransfer.StudentQuery;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.User;
import teammates.test.GroupNames;

/**
 * Tests for {@link UsersDb}.
 */
public class UsersDbTest extends BaseDbTestcase {
    private final UsersDb usersDb = UsersDb.inst();

    @Test(groups = GroupNames.DB)
    public void getUser_userExists_returnsUser() {
        var student = given.student("student");
        persistGivenData(given);

        User actual = inTransaction(() -> usersDb.getUser(student.id()));

        assertNotNull(actual);
        assertEquals(student.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getUserByRegKey_userExists_returnsUser() {
        var instructor = given.instructor("instructor");
        persistGivenData(given);

        User actual = inTransaction(() -> usersDb.getUserByRegKey(instructor.regKey()));

        assertNotNull(actual);
        assertEquals(instructor.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getInstructorByAccountId_instructorExists_returnsInstructor() {
        var account = given.account("account");
        var course = given.course("course");
        var instructor = given.instructor("instructor", i -> i.account(account.alias()).course(course.alias()));
        persistGivenData(given);

        Instructor actual = inTransaction(() -> usersDb.getInstructorByAccountId(account.id(), course.id()));

        assertNotNull(actual);
        assertEquals(instructor.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getInstructorByAccountId_instructorWrongCourse_returnsNull() {
        var account = given.account("account");
        var course = given.course("course");
        given.instructor("instructor", i -> i.account(account.alias()).course("another-course"));
        persistGivenData(given);

        Instructor actual = inTransaction(() -> usersDb.getInstructorByAccountId(account.id(), course.id()));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getInstructorsByAccountId_instructorsExist_returnsInstructorsForAccount() {
        var account = given.account("account");
        var anotherAccount = given.account("another-account");
        var instructor1 = given.instructor("instructor-1", i -> i.account(account.alias()).course("course-1"));
        var instructor2 = given.instructor("instructor-2", i -> i.account(account.alias()).course("course-2"));
        given.instructor("another-account-instructor", i -> i.account(anotherAccount.alias()).course("course-1"));
        given.instructor("unregistered-instructor", i -> i.course("course-1"));
        persistGivenData(given);

        List<Instructor> actual = inTransaction(() -> usersDb.getInstructorsByAccountId(account.id()));

        assertEquals(Set.of(instructor1.id(), instructor2.id()),
                actual.stream().map(Instructor::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getStudentByAccountId_studentExists_returnsStudent() {
        var account = given.account("account");
        var course = given.course("course");
        var student = given.student("student", s -> s.account(account.alias()).course(course.alias()));
        persistGivenData(given);

        Student actual = inTransaction(() -> usersDb.getStudentByAccountId(account.id(), course.id()));

        assertNotNull(actual);
        assertEquals(student.id(), actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getStudentByAccountId_studentWrongCourse_returnsNull() {
        var account = given.account("account");
        var course = given.course("course");
        given.student("student", s -> s.account(account.alias()).course("another-course"));
        persistGivenData(given);

        Student actual = inTransaction(() -> usersDb.getStudentByAccountId(account.id(), course.id()));

        assertNull(actual);
    }

    @Test(groups = GroupNames.DB)
    public void getStudentsByAccountId_studentsExist_returnsStudentsForAccount() {
        var account = given.account("account");
        var anotherAccount = given.account("another-account");
        var student1 = given.student("student-1", s -> s.account(account.alias()).course("course-1"));
        var student2 = given.student("student-2", s -> s.account(account.alias()).course("course-2"));
        given.student("another-account-student", s -> s.account(anotherAccount.alias()).course("course-1"));
        given.student("unregistered-student", s -> s.course("course-1"));
        persistGivenData(given);

        List<Student> actual = inTransaction(() -> usersDb.getStudentsByAccountId(account.id()));

        assertEquals(Set.of(student1.id(), student2.id()),
                actual.stream().map(Student::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getStudents_courseIdsProvided_returnsStudentsOnlyFromMatchingCourses() {
        var course1 = given.course("course-1");
        var course2 = given.course("course-2");
        var anotherCourse = given.course("course-3");
        var course1Student = given.student("course-1-student", s -> s.course(course1.alias()));
        var course2Student = given.student("course-2-student", s -> s.course(course2.alias()));
        given.student("another-course-student", s -> s.course(anotherCourse.alias()));
        persistGivenData(given);

        List<Student> actual = inTransaction(
                () -> usersDb.getStudents(new StudentQuery(List.of(course1.id(), course2.id()), null, null)));

        assertEquals(List.of(course1Student.id(), course2Student.id()),
                actual.stream().map(Student::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getStudents_searchKeyProvided_searchesAcrossRequestedCoursesAndLimitsResults() {
        var course1 = given.course("course-1", c -> c.name("Shared Course 1"));
        var course2 = given.course("course-2", c -> c.name("Shared Course 2"));
        var firstMatch = given.student("first-match", s -> s.course(course1.alias()).name("Shared Alice"));
        given.student("second-match", s -> s.course(course2.alias()).name("Shared Bob"));
        given.student("different-course-match", s -> s.course("course-3").name("Shared Charlie"));
        persistGivenData(given);

        List<Student> actual = inTransaction(() -> usersDb.getStudents(
                new StudentQuery(List.of(course1.id(), course2.id()), "shared", 1)));

        assertEquals(1, actual.size());
        assertEquals(firstMatch.id(), actual.get(0).getId());
    }

    @Test(groups = GroupNames.DB)
    public void getStudents_searchKeyMatchesSectionAndTeam_returnsMatchingStudents() {
        var course = given.course("course");
        var section = given.section("section", s -> s.course(course.alias()).name("Shared Section"));
        var matchingStudent = given.student("matching-student",
                s -> s.course(course.alias()).section(section.alias()).name("Alice"));
        given.student("non-matching-student", s -> s.course(course.alias()).name("Bob"));
        persistGivenData(given);

        List<Student> actual = inTransaction(
                () -> usersDb.getStudents(new StudentQuery(List.of(course.id()), "shared", null)));

        assertEquals(List.of(matchingStudent.id()), actual.stream().map(Student::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void persistInstructor_instructorIsNew_instructorIsPersisted() {
        var course = given.course("course");
        persistGivenData(given);
        var instructorId = given.uuid("instructor");

        Instructor actual = inTransaction(() -> {
            Course courseEntity = getEntity(Course.class, course.id());
            Instructor instructor = buildDefaultInstructor(courseEntity, instructorId);
            return usersDb.persistInstructor(instructor);
        });

        assertEquals(instructorId, actual.getId());
        verifyPresentInDatabase(Instructor.class, instructorId);
    }

    @Test(groups = GroupNames.DB)
    public void persistStudent_studentIsNew_studentIsPersisted() {
        var teamRef = given.team("team");
        persistGivenData(given);
        var studentId = given.uuid("student");

        Student actual = inTransaction(() -> {
            Team team = getEntity(Team.class, teamRef.id());
            Student student = buildDefaultStudent(team, studentId);
            return usersDb.persistStudent(student);
        });

        assertEquals(studentId, actual.getId());
        verifyPresentInDatabase(Student.class, studentId);
    }

    @Test(groups = GroupNames.DB)
    public void removeUser_userExists_userIsRemoved() {
        var student = given.student("student");
        persistGivenData(given);

        inTransaction(() -> usersDb.removeUser(usersDb.getStudent(student.id())));

        verifyAbsentInDatabase(Student.class, student.id());
    }

    @Test(groups = GroupNames.DB)
    public void getUsersForCourse_usersExist_returnsInstructorsAndStudentsForCourse() {
        var course = given.course("course");
        var instructor = given.instructor("instructor", i -> i.course(course.alias()));
        var student = given.student("student", s -> s.course(course.alias()));
        given.instructor("another-instructor", i -> i.course("another-course"));
        given.student("another-student", s -> s.course("another-course"));
        persistGivenData(given);

        List<User> actual = inTransaction(() -> usersDb.getUsersForCourse(course.id()));

        assertEquals(Set.of(instructor.id(), student.id()), actual.stream().map(User::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getStudentsForTeam_studentsExist_returnsStudentsInMatchingCourseAndTeam() {
        var course = given.course("course");
        var team = given.team("team", t -> t.course(course.alias()));
        var anotherTeam = given.team("another-team", t -> t.course(course.alias()));
        var anotherCourse = given.course("another-course");
        var anotherCourseTeam = given.team("another-course-team",
                t -> t.course(anotherCourse.alias()));
        var student = given.student("student", s -> s.course(course.alias()).team(team.alias()));
        given.student("another-team-student", s -> s.course(course.alias()).team(anotherTeam.alias()));
        given.student("another-course-student",
                s -> s.course(anotherCourse.alias()).team(anotherCourseTeam.alias()));
        persistGivenData(given);

        List<Student> actual = inTransaction(() -> usersDb.getStudentsForTeam(team.id(), course.id()));

        assertEquals(List.of(student.id()), actual.stream().map(Student::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void deleteStudentsInCourse_studentsExist_deletesOnlyStudentsInCourse() {
        var course = given.course("course");
        var student1 = given.student("student-1", s -> s.course(course.alias()));
        var student2 = given.student("student-2", s -> s.course(course.alias()));
        var anotherCourse = given.course("another-course");
        var anotherCourseStudent = given.student("another-course-student", s -> s.course(anotherCourse.alias()));
        persistGivenData(given);

        inTransaction(() -> usersDb.deleteStudentsInCourse(course.id()));

        verifyAbsentInDatabase(Student.class, student1.id());
        verifyAbsentInDatabase(Student.class, student2.id());
        verifyPresentInDatabase(Student.class, anotherCourseStudent.id());
    }

    @Test(groups = GroupNames.DB)
    public void getStudentCreatedAtTimestampsForTimeRange_studentsExist_returnsTimestampsInRange() {
        given.student("student-1");
        given.student("student-2");
        given.instructor("instructor-1");
        persistGivenData(given);

        Instant start = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant end = Instant.now().plus(1, ChronoUnit.HOURS);

        List<Instant> actual = inTransaction(
                () -> usersDb.getStudentCreatedAtTimestampsForTimeRange(start, end));

        assertEquals(2, actual.size());
    }

    @Test(groups = GroupNames.DB)
    public void getInstructorCreatedAtTimestampsForTimeRange_instructorsExist_returnsTimestampsInRange() {
        given.instructor("instructor-1");
        given.instructor("instructor-2");
        given.student("student-1");
        persistGivenData(given);

        Instant start = Instant.now().minus(1, ChronoUnit.HOURS);
        Instant end = Instant.now().plus(1, ChronoUnit.HOURS);

        List<Instant> actual = inTransaction(
                () -> usersDb.getInstructorCreatedAtTimestampsForTimeRange(start, end));

        assertEquals(2, actual.size());
    }

    @Test(groups = GroupNames.DB)
    public void getInstructors_courseIdProvided_returnsOnlyCourseInstructorsInNameOrder() {
        var course = given.course("course");
        var firstInstructor = given.instructor("first-instructor", i -> i.course(course.alias()).name("Adam"));
        var secondInstructor = given.instructor("second-instructor", i -> i.course(course.alias()).name("Zed"));
        given.instructor("another-course-instructor", i -> i.course("another-course"));
        persistGivenData(given);

        List<Instructor> actual = inTransaction(() -> usersDb.getInstructors(
                new InstructorQuery(course.id(), null, null)));

        assertEquals(List.of(firstInstructor.id(), secondInstructor.id()),
                actual.stream().map(Instructor::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getInstructors_searchKeyProvided_returnsMatchingInstructorsAcrossCourses() {
        var firstCourse = given.course("course-1", c -> c.name("Shared Course 1"));
        var secondCourse = given.course("course-2", c -> c.name("Shared Course 2"));
        var firstMatch = given.instructor("first-match", i -> i.course(firstCourse.alias()).name("Alpha"));
        var secondMatch = given.instructor("second-match", i -> i.course(secondCourse.alias()).name("Beta"));
        given.instructor("non-match", i -> i.course("course-3").name("Gamma"));
        persistGivenData(given);

        List<Instructor> actual = inTransaction(() -> usersDb.getInstructors(
                new InstructorQuery(null, "shared", null)));

        assertEquals(List.of(firstMatch.id(), secondMatch.id()),
                actual.stream().map(Instructor::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getInstructors_courseIdAndSearchKeyProvided_returnsOnlyMatchingInstructorsInCourse() {
        var course = given.course("course", c -> c.name("Course Name"));
        var matchingInstructor = given.instructor("matching-instructor",
                i -> i.course(course.alias()).name("Shared Match"));
        given.instructor("non-matching-instructor", i -> i.course(course.alias()).name("Different Name"));
        given.instructor("other-course-match", i -> i.course("other-course").name("Shared Match"));
        persistGivenData(given);

        List<Instructor> actual = inTransaction(() -> usersDb.getInstructors(
                new InstructorQuery(course.id(), "shared", null)));

        assertEquals(List.of(matchingInstructor.id()),
                actual.stream().map(Instructor::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void getInstructors_blankSearchKey_returnsEmptyList() {
        given.instructor("instructor");
        persistGivenData(given);

        List<Instructor> actual = inTransaction(() -> usersDb.getInstructors(
                new InstructorQuery(null, "   ", null)));

        assertTrue(actual.isEmpty());
    }

    @Test(groups = GroupNames.DB)
    public void getInstructors_limitProvided_returnsLimitedResultsInConsistentOrder() {
        given.instructor("first-instructor", i -> i.course("course-1").name("Alpha"));
        given.instructor("second-instructor", i -> i.course("course-2").name("Beta"));
        given.instructor("third-instructor", i -> i.course("course-3").name("Gamma"));
        persistGivenData(given);

        List<Instructor> actual = inTransaction(() -> usersDb.getInstructors(
                new InstructorQuery(null, null, 2)));

        assertEquals(2, actual.size());
        assertEquals(List.of(given.uuid("first-instructor"), given.uuid("second-instructor")),
                actual.stream().map(Instructor::getId).toList());
    }

    private static Instructor buildDefaultInstructor(Course course, UUID instructorId) {
        assertNotNull(course);
        InstructorPermissionRole role = InstructorPermissionRole.COOWNER;
        Instructor instructor = new Instructor(
                "Instructor Name",
                "instructor@example.com",
                true,
                "Instructor Display Name",
                role);
        instructor.setId(instructorId);
        instructor.setCourse(course);
        return instructor;
    }

    private static Student buildDefaultStudent(Team team, UUID studentId) {
        assertNotNull(team);
        Student student = new Student("Student Name", "student@example.com", "");
        student.setId(studentId);
        student.setCourse(team.getSection().getCourse());
        team.addUser(student);
        return student;
    }
}
