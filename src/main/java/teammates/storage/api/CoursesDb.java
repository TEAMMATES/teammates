package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.storage.entity.Course;

/**
 * Handles CRUD operations for courses.
 *
 * @see Course
 * @see CourseAttributes
 */
public class CoursesDb extends EntitiesDb<Course, CourseAttributes> {

    /**
     * Gets a course.
     */
    public CourseAttributes getCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributesOrNull(getCourseEntity(courseId));
    }

    /**
     * Gets a list of courses.
     */
    public List<CourseAttributes> getCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds.toArray());

        return makeAttributes(getCourseEntities(courseIds));
    }

    /**
     * Updates a course by {@link CourseAttributes.UpdateOptions}.
     *
     * @return updated course
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public CourseAttributes updateCourse(CourseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updateOptions);

        Course course = getCourseEntity(updateOptions.getCourseId());

        if (course == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        CourseAttributes newAttributes = makeAttributes(course);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<String>hasSameValue(course.getName(), newAttributes.getName())
                && this.<String>hasSameValue(course.getTimeZone(), newAttributes.getTimeZone().getId());
        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, Course.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        course.setName(newAttributes.getName());
        course.setTimeZone(newAttributes.getTimeZone().getId());

        saveEntity(course);

        return makeAttributes(course);
    }

    /**
     * Deletes a course.
     */
    public void deleteCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        deleteEntity(Key.create(Course.class, courseId));
    }

    /**
     * Soft-deletes a course by its given corresponding ID.
     * @return Soft-deletion time of the course.
     */
    public Instant softDeleteCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Course courseEntity = getCourseEntity(courseId);

        if (courseEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        courseEntity.setDeletedAt(Instant.now());
        saveEntity(courseEntity);

        return courseEntity.getDeletedAt();
    }

    /**
     * Restores a soft-deleted course by its given corresponding ID.
     */
    public void restoreDeletedCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Course courseEntity = getCourseEntity(courseId);

        if (courseEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        courseEntity.setDeletedAt(null);
        saveEntity(courseEntity);
    }

    @Override
    LoadType<Course> load() {
        return ofy().load().type(Course.class);
    }

    @Override
    boolean hasExistingEntities(CourseAttributes entityToCreate) {
        Key<Course> keyToFind = Key.create(Course.class, entityToCreate.getId());
        return !load().filterKey(keyToFind).keys().list().isEmpty();
    }

    private Course getCourseEntity(String courseId) {
        return load().id(courseId).now();
    }

    private List<Course> getCourseEntities(List<String> courseIds) {
        if (courseIds.isEmpty()) {
            return new ArrayList<>();
        }

        return new ArrayList<>(
                load().ids(courseIds).values());
    }

    @Override
    CourseAttributes makeAttributes(Course entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return CourseAttributes.valueOf(entity);
    }
}
