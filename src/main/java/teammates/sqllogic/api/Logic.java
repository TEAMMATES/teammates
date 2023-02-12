package teammates.sqllogic.api;

import java.time.Instant;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.sqllogic.core.CoursesLogic;
import teammates.storage.sqlentity.Course;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {
    private static final Logic instance = new Logic();

    final CoursesLogic coursesLogic = CoursesLogic.inst();
    // final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();

    Logic() {
        // prevent initialization
    }

    public static Logic inst() {
        return instance;
    }

    // Courses

    /**
     * Gets a course by course id.
     * @param courseId courseId of the course.
     * @return the specified course.
     */
    public Course getCourse(String courseId) {
        return coursesLogic.getCourse(courseId);
    }

    /**
     * Creates a course.
     * @param course the course to create.
     * @return the created course.
     * @throws InvalidParametersException if the course is not valid.
     * @throws EntityAlreadyExistsException if the course already exists.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createCourse(course);
    }

    /**
     * Deletes a course by course id.
     * @param courseId of course.
     */
    public void deleteCourseCascade(String courseId) {
        coursesLogic.deleteCourseCascade(courseId);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     * @return the deletion timestamp assigned to the course.
     */
    public Instant moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        return coursesLogic.moveCourseToRecycleBin(courseId);
    }
}
