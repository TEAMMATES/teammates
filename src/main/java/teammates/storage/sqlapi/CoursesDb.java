package teammates.storage.sqlapi;

import java.time.Instant;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlentity.Course;

/**
 * Handles CRUD operations for courses.
 *
 * @see Course
 */
public final class CoursesDb extends EntitiesDb<Course> {

    private static final CoursesDb instance = new CoursesDb();

    private CoursesDb() {
        // prevent initialization
    }

    public static CoursesDb inst() {
        return instance;
    }

    /**
     * Returns a course with the {@code courseID} or null if it does not exist.
     */
    public Course getCourse(String courseId) {
        assert courseId != null;

        return HibernateUtil.getSessionFactory().getCurrentSession().get(Course.class, courseId);
    }

    /**
     * Creates a course.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        assert course != null;

        course.sanitizeForSaving();
        if (!course.isValid()) {
            throw new InvalidParametersException(course.getInvalidityInfo());
        }

        if (getCourse(course.getId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, course.toString()));
        }

        persist(course);
        return course;
    }

    /**
     * Saves an updated {@code Course} to the db.
     */
    public Course updateCourse(Course course) throws InvalidParametersException, EntityDoesNotExistException {
        assert course != null;

        course.sanitizeForSaving();
        if (!course.isValid()) {
            throw new InvalidParametersException(course.getInvalidityInfo());
        }

        if (getCourse(course.getId()) == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        return merge(course);
    }

    /**
     * Deletes a course.
     */
    public void deleteCourse(String courseId) {
        assert courseId != null;

        Course course = getCourse(courseId);
        if (course != null) {
            delete(course);
        }
    }

    /**
     * Soft-deletes a course by its given corresponding ID.
     *
     * @return Soft-deletion time of the course.
     */
    public Instant softDeleteCourse(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;

        Course course = getCourse(courseId);
        if (course == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        course.setDeletedAt(Instant.now());

        return course.getDeletedAt();
    }

    /**
     * Restores a soft-deleted course by its given corresponding ID.
     */
    public void restoreDeletedCourse(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;

        Course course = getCourse(courseId);

        if (course == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        course.setDeletedAt(null);
    }
}
