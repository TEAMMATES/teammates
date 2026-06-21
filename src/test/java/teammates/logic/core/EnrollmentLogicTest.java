package teammates.logic.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.testng.annotations.Test;

import teammates.common.datatransfer.EnrollResults;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Student;
import teammates.test.GroupNames;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Tests for {@link EnrollmentLogic}.
 */
public class EnrollmentLogicTest extends BaseLogicTestcase {
    private final EnrollmentLogic enrollmentLogic = EnrollmentLogic.inst();

    @Test(groups = GroupNames.LOGIC)
    public void enrollStudents_newStudents_createsStudentsInCourse() {
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()));
        persistGivenData(given);

        List<StudentEnrollRequest> requests = List.of(
                new StudentEnrollRequest("Alice", "alice@test.com", "Team 1", "Section 1", ""));

        EnrollResults results = inTransaction(() -> {
            Course c = getEntity(Course.class, course.id());
            return enrollmentLogic.enrollStudents(c, requests);
        });

        assertEquals(1, results.getEnrolledStudents().size());
        assertEquals(0, results.getUnsuccessfulEnrolls().size());
        Student enrolled = getEntityInTransaction(Student.class, results.getEnrolledStudents().get(0).getId());
        assertNotNull(enrolled);
        assertEquals("Alice", enrolled.getName());
        assertEquals("Section 1", enrolled.getSectionName());
        assertEquals("Team 1", enrolled.getTeamName());
    }

    @Test(groups = GroupNames.LOGIC)
    public void enrollStudents_existingStudent_updatesStudentDetails() {
        var course = given.course("course");
        var student = given.student("student", s -> s.course(course.alias())
                .name("Old Name").email("student@test.com"));
        given.instructor("instructor", i -> i.course(course.alias()));
        persistGivenData(given);

        List<StudentEnrollRequest> requests = List.of(
                new StudentEnrollRequest("New Name", "student@test.com", "New Team", "New Section", "updated"));

        EnrollResults results = inTransaction(() -> {
            Course c = getEntity(Course.class, course.id());
            return enrollmentLogic.enrollStudents(c, requests);
        });

        assertEquals(1, results.getEnrolledStudents().size());
        Student updated = getEntityInTransaction(Student.class, student.id());
        assertEquals("New Name", updated.getName());
        assertEquals("updated", updated.getComments());
        assertEquals("New Section", updated.getSectionName());
        assertEquals("New Team", updated.getTeamName());
    }

    @Test(groups = GroupNames.LOGIC)
    public void enrollStudents_emailMatchesInstructor_addsToUnsuccessfulEnrolls() {
        var course = given.course("course");
        var instructor = given.instructor("instructor", i -> i.course(course.alias())
                .email("instructor@test.com"));
        persistGivenData(given);

        List<StudentEnrollRequest> requests = List.of(
                new StudentEnrollRequest("Alice", instructor.email(), "Team 1", "Section 1", ""));

        EnrollResults results = inTransaction(() -> {
            Course c = getEntity(Course.class, course.id());
            return enrollmentLogic.enrollStudents(c, requests);
        });

        assertEquals(0, results.getEnrolledStudents().size());
        assertEquals(1, results.getUnsuccessfulEnrolls().size());
        assertTrue(results.getUnsuccessfulEnrolls().containsKey(instructor.email()));
    }

    @Test(groups = GroupNames.LOGIC)
    public void enrollStudents_sectionExceedsLimit_throwsEnrollException() {
        var course = given.course("course");
        given.instructor("instructor", i -> i.course(course.alias()));
        persistGivenData(given);

        List<StudentEnrollRequest> requests = new ArrayList<>();
        for (int i = 0; i <= Const.SECTION_SIZE_LIMIT; i++) {
            requests.add(new StudentEnrollRequest("Student " + i, "s" + i + "@test.com", "Team 1", "Section 1", ""));
        }

        assertThrowsInTransaction(EnrollException.class, () -> {
            Course c = getEntity(Course.class, course.id());
            enrollmentLogic.enrollStudents(c, requests);
        });
    }

    @Test(groups = GroupNames.LOGIC)
    public void updateStudentEnrollment_studentNotFound_throwsEntityDoesNotExistException() {
        persistGivenData(given);

        StudentUpdateRequest request = new StudentUpdateRequest(
                "Name", "email@test.com", "Team", "Section", "", false);

        assertThrowsInTransaction(EntityDoesNotExistException.class,
                () -> enrollmentLogic.updateStudentEnrollment(UUID.randomUUID(), request));
    }

    @Test(groups = GroupNames.LOGIC)
    public void updateStudentEnrollment_emailUsedByInstructor_throwsEntityAlreadyExistsException() {
        var course = given.course("course");
        given.student("student", s -> s.course(course.alias()).email("student@test.com"));
        var instructor = given.instructor("instructor", i -> i.course(course.alias())
                .email("instructor@test.com"));
        persistGivenData(given);

        StudentUpdateRequest request = new StudentUpdateRequest(
                "Name", instructor.email(), "Team", "Section", "", false);

        assertThrowsInTransaction(EntityAlreadyExistsException.class,
                () -> enrollmentLogic.updateStudentEnrollment(given.uuid("student"), request));
    }

    @Test(groups = GroupNames.LOGIC)
    public void updateStudentEnrollment_emailUsedByAnotherStudent_throwsEntityAlreadyExistsException() {
        var course = given.course("course");
        given.student("student", s -> s.course(course.alias()).email("student@test.com"));
        given.student("other", s -> s.course(course.alias()).email("other@test.com"));
        persistGivenData(given);

        StudentUpdateRequest request = new StudentUpdateRequest(
                "Name", "other@test.com", "Team", "Section", "", false);

        assertThrowsInTransaction(EntityAlreadyExistsException.class,
                () -> enrollmentLogic.updateStudentEnrollment(given.uuid("student"), request));
    }

    @Test(groups = GroupNames.LOGIC)
    public void updateStudentEnrollment_validRequest_updatesStudentTeamAndSection() {
        var course = given.course("course");
        var student = given.student("student", s -> s.course(course.alias())
                .name("Old Name").email("old@test.com"));
        persistGivenData(given);

        StudentUpdateRequest request = new StudentUpdateRequest(
                "New Name", "new@test.com", "New Team", "New Section", "new comments", false);

        inTransaction(() -> enrollmentLogic.updateStudentEnrollment(student.id(), request));

        Student updated = getEntityInTransaction(Student.class, student.id());
        assertEquals("New Name", updated.getName());
        assertEquals("new@test.com", updated.getEmail());
        assertEquals("new comments", updated.getComments());
        assertEquals("New Team", updated.getTeamName());
        assertEquals("New Section", updated.getSectionName());
    }
}
