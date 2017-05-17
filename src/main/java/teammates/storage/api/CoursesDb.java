package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.QueryKeys;

import static com.googlecode.objectify.ObjectifyService.ofy;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.entity.Course;

/**
 * Handles CRUD operations for courses.
 *
 * @see Course
 * @see CourseAttributes
 */
public class CoursesDb extends OfyEntitiesDb<Course, CourseAttributes> {

    /*
     * Explanation: Based on our policies for the storage component, this class does not handle cascading.
     * It treats invalid values as an exception.
     */

    public static final String ERROR_UPDATE_NON_EXISTENT_COURSE = "Trying to update a Course that doesn't exist: ";

    private static final Logger log = Logger.getLogger();

    public void createCourses(Collection<CourseAttributes> coursesToAdd) throws InvalidParametersException {
        List<CourseAttributes> coursesToUpdate = createEntities(coursesToAdd);
        for (CourseAttributes course : coursesToUpdate) {
            try {
                updateCourse(course);
            } catch (EntityDoesNotExistException e) {
                // This situation is not tested as replicating such a situation is
                // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public CourseAttributes getCourse(String courseId) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        Course c = getCourseEntity(courseId);

        if (c == null) {
            return null;
        }

        return new CourseAttributes(c);
    }

    public List<CourseAttributes> getCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        List<Course> courses = getCourseEntities(courseIds);
        List<CourseAttributes> courseAttributes = new ArrayList<CourseAttributes>();
        // TODO add method to get List<CourseAttributes> from List<Course>
        for (Course c : courses) {
            courseAttributes.add(new CourseAttributes(c));
        }
        return courseAttributes;
    }

    /**
     * Gets all courses in the Datastore.
     *
     * @deprecated Not scalable. Use only in admin features.
     */
    @Deprecated
    public List<CourseAttributes> getAllCourses() {
        List<Course> courseList = ofy().load().type(Course.class).list();

        List<CourseAttributes> courseDataList = new ArrayList<CourseAttributes>();
        for (Course c : courseList) {
            courseDataList.add(new CourseAttributes(c));
        }

        return courseDataList;
    }

    /**
     * Updates the course.<br>
     * Updates only name and course archive status.<br>
     * Preconditions: <br>
     * * {@code courseToUpdate} is non-null.<br>
     * @throws InvalidParametersException, EntityDoesNotExistException
     */
    public void updateCourse(CourseAttributes courseToUpdate) throws InvalidParametersException,
                                                                     EntityDoesNotExistException {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseToUpdate);

        courseToUpdate.sanitizeForSaving();

        if (!courseToUpdate.isValid()) {
            throw new InvalidParametersException(courseToUpdate.getInvalidityInfo());
        }

        Course courseEntityToUpdate = getCourseEntity(courseToUpdate.getId());

        if (courseEntityToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_COURSE);
        }

        courseEntityToUpdate.setName(courseToUpdate.getName());
        courseEntityToUpdate.setTimeZone(courseToUpdate.getTimeZone());

        ofy().save().entity(courseEntityToUpdate).now();

        log.info(courseToUpdate.getBackupIdentifier());
    }

    /**
     * Note: This is a non-cascade delete.<br>
     *   <br> Fails silently if there is no such object.
     * <br> Preconditions:
     * <br> * {@code courseId} is not null.
     */
    public void deleteCourse(String courseId) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        // only the courseId is important here, everything else are placeholders
        CourseAttributes entityToDelete = new CourseAttributes(courseId, "Non-existent course", "UTC");

        deleteEntity(entityToDelete);
    }

    @Override
    protected Course getEntity(CourseAttributes attributes) {
        return getCourseEntity(attributes.getId());
    }

    @Override
    public boolean hasEntity(CourseAttributes attributes) {
        Key<Course> keyToFind = Key.create(Course.class, attributes.getId());
        QueryKeys<Course> keysOnlyQuery = ofy().load().type(Course.class).filterKey(keyToFind).keys();
        return keysOnlyQuery.first().now() != null;
    }

    private Course getCourseEntity(String courseId) {
        return ofy().load().type(Course.class).id(courseId).now();
    }

    private List<Course> getCourseEntities(List<String> courseIds) {
        if (courseIds.isEmpty()) {
            return new ArrayList<Course>();
        }

        return new ArrayList<Course>(ofy().load().type(Course.class).ids(courseIds).values());
    }
}
