package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.testng.annotations.Test;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
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
        var team = given.team("team", t -> t.course(course.alias()).name("Team Name"));
        var anotherTeam = given.team("another-team", t -> t.course(course.alias()).name("Another Team Name"));
        var anotherCourse = given.course("another-course");
        var anotherCourseTeam = given.team("another-course-team",
                t -> t.course(anotherCourse.alias()).name("Team Name"));
        var student = given.student("student", s -> s.course(course.alias()).team(team.alias()));
        given.student("another-team-student", s -> s.course(course.alias()).team(anotherTeam.alias()));
        given.student("another-course-student",
                s -> s.course(anotherCourse.alias()).team(anotherCourseTeam.alias()));
        persistGivenData(given);

        List<Student> actual = inTransaction(() -> usersDb.getStudentsForTeam("Team Name", course.id()));

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

    // TODO: add tests for search related methods in UsersDb
    // This was not done initially as the search functionality requires additional clean up.
    // Tests will be added once search functionality is refactored.
    // The old deprecated search tests can be found in StudentSearchIT and InstructorSearchIT and will
    // be removed once the new search tests are added.

    private static Instructor buildDefaultInstructor(Course course, UUID instructorId) {
        assertNotNull(course);
        InstructorPermissionRole role = InstructorPermissionRole.INSTRUCTOR_PERMISSION_ROLE_COOWNER;
        Instructor instructor = new Instructor(
                "Instructor Name",
                "instructor@example.com",
                true,
                "Instructor Display Name",
                role,
                new InstructorPrivileges(role.getRoleName()));
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
