package teammates.storage.sqlapi;

import org.testng.annotations.Test;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.storage.sqlentity.Course;
import teammates.test.BaseTestCaseWithSqlDatabaseAccess;

/**
 * SUT: {@link CoursesDb}.
 */
public class CoursesDbIT extends BaseTestCaseWithSqlDatabaseAccess {

    private final CoursesDb coursesDb = CoursesDb.inst();

    @Test
    public void testCreateCourse() throws Exception {
        ______TS("Create course, does not exists, succeeds");

        Course course = new Course.CourseBuilder("course-id")
                .withName("course-name")
                .withInstitute("teammates")
                .build();

        coursesDb.createCourse(course);

        Course actualCourse = coursesDb.getCourse("course-id");
        verifyEquals(course, actualCourse);

        ______TS("Create course, already exists, execption thrown");

        Course identicalCourse = new Course.CourseBuilder("course-id")
                .withName("course-name")
                .withInstitute("teammates")
                .build();
        assertNotSame(course, identicalCourse);

        assertThrows(EntityAlreadyExistsException.class, () -> coursesDb.createCourse(identicalCourse));
    }

    @Test
    public void testUpdateCourse() throws Exception {
        ______TS("Update course, does not exists, exception thrown");

        Course course = new Course.CourseBuilder("course-id")
                .withName("course-name")
                .withInstitute("teammates")
                .build();

        assertThrows(EntityDoesNotExistException.class, () -> coursesDb.updateCourse(course));

        ______TS("Update course, already exists, update successful");

        coursesDb.createCourse(course);
        course.setName("new course name");

        coursesDb.updateCourse(course);
        Course actual = coursesDb.getCourse("course-id");
        verifyEquals(course, actual);

        ______TS("Update detached course, already exists, update successful");

        // same id, different name
        Course detachedCourse = new Course.CourseBuilder("course-id")
                .withName("course")
                .withInstitute("teammates")
                .build();

        coursesDb.updateCourse(detachedCourse);
        verifyEquals(course, detachedCourse);
    }
}
