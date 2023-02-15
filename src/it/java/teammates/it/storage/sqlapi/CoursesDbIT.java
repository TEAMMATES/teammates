package teammates.it.storage.sqlapi;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.it.test.BaseTestCaseWithSqlDatabaseAccess;
import teammates.storage.sqlapi.CoursesDb;
import teammates.storage.sqlentity.Course;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursesDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testCreateCourse() throws Exception {
        ______TS("Create course, does not exists, succeeds");

        Course course = new Course("course-id", "course-name", null, "teammates");

        coursesDb.createCourse(course);

        Course actualCourse = coursesDb.getCourse("course-id");
        verifyEquals(course, actualCourse);

        ______TS("Create course, already exists, execption thrown");

        Course identicalCourse = new Course("course-id", "course-name", null, "teammates");
        assertNotSame(course, identicalCourse);

        assertThrows(EntityAlreadyExistsException.class, () -> coursesDb.createCourse(identicalCourse));
    }

    @Test
    public void testUpdateCourse() throws Exception {
        ______TS("Update course, does not exists, exception thrown");

        Course course = new Course("course-id", "course-name", null, "teammates");

        assertThrows(EntityDoesNotExistException.class, () -> coursesDb.updateCourse(course));

        ______TS("Update course, already exists, update successful");

        coursesDb.createCourse(course);
        course.setName("new course name");

        coursesDb.updateCourse(course);
        Course actual = coursesDb.getCourse("course-id");
        verifyEquals(course, actual);

        ______TS("Update detached course, already exists, update successful");

        // same id, different name
        Course detachedCourse = new Course("course-id", "different-name", null, "teammates");

        coursesDb.updateCourse(detachedCourse);
        verifyEquals(course, detachedCourse);
    }
}
