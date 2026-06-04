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
        UUID studentId = given.student("student");
        persistGivenData(given);

        User actual = inTransaction(() -> usersDb.getUser(studentId));

        assertNotNull(actual);
        assertEquals(studentId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void getUserByRegKey_userExists_returnsUser() {
        UUID instructorId = given.instructor("instructor");
        persistGivenData(given);
        String regKey = getEntityInTransaction(Instructor.class, instructorId).getRegKey();

        User actual = inTransaction(() -> usersDb.getUserByRegKey(regKey));

        assertNotNull(actual);
        assertEquals(instructorId, actual.getId());
    }

    @Test(groups = GroupNames.DB)
    public void persistInstructor_instructorIsNew_instructorIsPersisted() {
        String courseId = given.course("course");
        persistGivenData(given);
        UUID instructorId = given.uuid("instructor");

        Instructor actual = inTransaction(() -> {
            Course course = getEntity(Course.class, courseId);
            Instructor instructor = buildDefaultInstructor(course, instructorId);
            return usersDb.persistInstructor(instructor);
        });

        assertEquals(instructorId, actual.getId());
        verifyPresentInDatabase(Instructor.class, instructorId);
    }

    @Test(groups = GroupNames.DB)
    public void persistStudent_studentIsNew_studentIsPersisted() {
        UUID teamId = given.team("team");
        persistGivenData(given);
        UUID studentId = given.uuid("student");

        Student actual = inTransaction(() -> {
            Team team = getEntity(Team.class, teamId);
            Student student = buildDefaultStudent(team, studentId);
            return usersDb.persistStudent(student);
        });

        assertEquals(studentId, actual.getId());
        verifyPresentInDatabase(Student.class, studentId);
    }

    @Test(groups = GroupNames.DB)
    public void removeUser_userExists_userIsRemoved() {
        UUID studentId = given.student("student");
        persistGivenData(given);

        inTransaction(() -> usersDb.removeUser(usersDb.getStudent(studentId)));

        verifyAbsentInDatabase(Student.class, studentId);
    }

    @Test(groups = GroupNames.DB)
    public void getUsersForCourse_usersExist_returnsInstructorsAndStudentsForCourse() {
        String courseId = given.course("course");
        UUID instructorId = given.instructor("instructor", i -> i.course("course"));
        UUID studentId = given.student("student", s -> s.course("course"));
        given.instructor("another-instructor", i -> i.course("another-course"));
        given.student("another-student", s -> s.course("another-course"));
        persistGivenData(given);

        List<User> actual = inTransaction(() -> usersDb.getUsersForCourse(courseId));

        assertEquals(Set.of(instructorId, studentId), actual.stream().map(User::getId).collect(Collectors.toSet()));
    }

    @Test(groups = GroupNames.DB)
    public void getStudentsForTeam_studentsExist_returnsStudentsInMatchingCourseAndTeam() {
        String courseId = given.course("course");
        given.team("team", t -> {
            t.course("course");
            t.name("Team Name");
        });
        given.team("another-team", t -> {
            t.course("course");
            t.name("Another Team Name");
        });
        given.team("another-course-team", t -> {
            t.course("another-course");
            t.name("Team Name");
        });
        UUID studentId = given.student("student", s -> {
            s.course("course");
            s.team("team");
        });
        given.student("another-team-student", s -> {
            s.course("course");
            s.team("another-team");
        });
        given.student("another-course-student", s -> {
            s.course("another-course");
            s.team("another-course-team");
        });
        persistGivenData(given);

        List<Student> actual = inTransaction(() -> usersDb.getStudentsForTeam("Team Name", courseId));

        assertEquals(List.of(studentId), actual.stream().map(Student::getId).toList());
    }

    @Test(groups = GroupNames.DB)
    public void deleteStudentsInCourse_studentsExist_deletesOnlyStudentsInCourse() {
        String courseId = given.course("course");
        UUID studentId1 = given.student("student-1", s -> s.course("course"));
        UUID studentId2 = given.student("student-2", s -> s.course("course"));
        UUID anotherCourseStudentId = given.student("another-course-student", s -> s.course("another-course"));
        persistGivenData(given);

        inTransaction(() -> usersDb.deleteStudentsInCourse(courseId));

        verifyAbsentInDatabase(Student.class, studentId1);
        verifyAbsentInDatabase(Student.class, studentId2);
        verifyPresentInDatabase(Student.class, anotherCourseStudentId);
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
