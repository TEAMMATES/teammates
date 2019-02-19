package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.DateTimeException;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

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

    /*
     * Explanation: Based on our policies for the storage component, this class does not handle cascading.
     * It treats invalid values as an exception.
     */

    public static final String ERROR_UPDATE_NON_EXISTENT_COURSE = "Trying to update a Course that doesn't exist: ";

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public CourseAttributes getCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributesOrNull(getCourseEntity(courseId));
    }

    public List<CourseAttributes> getCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

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
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_COURSE);
        }

        CourseAttributes newAttributes = makeAttributes(course);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        course.setName(newAttributes.getName());
        course.setDeletedAt(newAttributes.deletedAt);
        course.setTimeZone(newAttributes.getTimeZone().getId());

        saveEntity(course, newAttributes);

        return makeAttributes(course);
    }

    /**
     * Permanently deletes the course from the Datastore.
     *
     * <p>Note: This is a non-cascade delete.<br>
     *   <br> Fails silently if there is no such object.
     * <br> Preconditions:
     * <br> * {@code courseId} is not null.
     */
    public void deleteCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        // only the courseId is important here, everything else are placeholders
        deleteEntity(CourseAttributes
                .builder(courseId, "Non-existent course", Const.DEFAULT_TIME_ZONE)
                .build());
    }

    /**
     * Soft-deletes a course by its given corresponding ID.
     * @return Soft-deletion time of the course.
     */
    public Instant softDeleteCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Course courseEntity = getCourseEntity(courseId);

        if (courseEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_COURSE);
        }

        courseEntity.setDeletedAt(Instant.now());
        saveEntity(courseEntity);

        return courseEntity.getDeletedAt();
    }

    /**
     * Restores a soft deleted course by its given corresponding ID.
     */
    public void restoreDeletedCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Course courseEntity = getCourseEntity(courseId);

        if (courseEntity == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_COURSE);
        }

        courseEntity.setDeletedAt(null);
        saveEntity(courseEntity);
    }

    @Override
    protected LoadType<Course> load() {
        return ofy().load().type(Course.class);
    }

    @Override
    protected Course getEntity(CourseAttributes attributes) {
        return getCourseEntity(attributes.getId());
    }

    @Override
    protected QueryKeys<Course> getEntityQueryKeys(CourseAttributes attributes) {
        Key<Course> keyToFind = Key.create(Course.class, attributes.getId());
        return load().filterKey(keyToFind).keys();
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
    protected CourseAttributes makeAttributes(Course entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        ZoneId courseTimeZone;
        try {
            courseTimeZone = ZoneId.of(entity.getTimeZone());
        } catch (DateTimeException e) {
            log.severe("Timezone '" + entity.getTimeZone() + "' of course '" + entity.getUniqueId()
                    + "' is not supported. UTC will be used instead.");
            courseTimeZone = Const.DEFAULT_TIME_ZONE;
        }
        return CourseAttributes.builder(entity.getUniqueId(), entity.getName(), courseTimeZone)
                .withCreatedAt(entity.getCreatedAt())
                .withDeletedAt(entity.getDeletedAt())
                .build();
    }
}
