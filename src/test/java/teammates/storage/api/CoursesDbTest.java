package teammates.storage.api;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.hibernate.exception.ConstraintViolationException;
import org.testng.annotations.Test;

import teammates.storage.entity.Course;
import teammates.test.GroupNames;

public class CoursesDbTest extends BaseDbTest {
    CoursesDb coursesDb = CoursesDb.inst();

    @Test(groups = GroupNames.INTEGRATION)
    public void getCourse_courseExists_returnsCourse() {
        String courseAlias = given.course("course-id");
        persistGivenData(given);

        Course actual = inTransaction(() -> coursesDb.getCourse(given.getCourseId(courseAlias)));

        assertNotNull(actual);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void getCourse_courseDoesNotExist_returnsNull() {
        Course actual = inTransaction(() -> coursesDb.getCourse("non-existent-course-id"));

        assertNull(actual);
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void persistCourse_courseIsNew_courseIsPersisted() {
        Course course = buildDefaultCourse("new-course-id");

        Course actual = inTransaction(() -> coursesDb.persistCourse(course));

        verifyPresentInDatabase(actual.getId());
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void persistCourse_courseIdExists_throwsException() {
        String existingCourseAlias = given.course("existing-course-id");
        persistGivenData(given);
        Course course = buildDefaultCourse(given.getCourseId(existingCourseAlias));

        assertThrowsInTransaction(ConstraintViolationException.class, () -> coursesDb.persistCourse(course));
    }

    @Test(groups = GroupNames.INTEGRATION)
    public void removeCourse_courseExists_courseIsRemoved() {
        String existingCourseAlias = given.course("existing-course-id");
        persistGivenData(given);

        inTransaction(() -> coursesDb.removeCourse(given.getCourse(existingCourseAlias)));

        verifyAbsentInDatabase(given.getCourseId(existingCourseAlias));
    }

    private void verifyPresentInDatabase(String courseId) {
        Course actual = inTransaction(() -> coursesDb.getCourse(courseId));
        assertNotNull(actual);
    }

    private void verifyAbsentInDatabase(String courseId) {
        Course actual = inTransaction(() -> coursesDb.getCourse(courseId));
        assertNull(actual);
    }

    private static Course buildDefaultCourse(String courseId) {
        return new Course(courseId, "Course Name", "UTC", "Institute");
    }
}
