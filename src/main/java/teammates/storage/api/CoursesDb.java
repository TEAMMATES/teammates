package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.storage.entity.Course;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;

/**
 * Handles CRUD Operations for course entities.
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
 */
public class CoursesDb extends EntitiesDb {

	public static final String ERROR_UPDATE_NON_EXISTENT_COURSE = "Trying to update a Course that doesn't exist: ";
	
	private static final Logger log = Config.getLogger();

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
	
	
	/**
	 * @deprecated Not scalable. Use only in admin features. 
	 */
	@Deprecated
	public List<CourseAttributes> getAllCourses() {
		
		Query q = getPM().newQuery(Course.class);
		
		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) q.execute();
	
		List<CourseAttributes> courseDataList = new ArrayList<CourseAttributes>();
		for (Course c : courseList) {
			courseDataList.add(new CourseAttributes(c));
		}
	
		return courseDataList;
	}

	/**
	 * Course ID will not be changed. <br>
	 * Does not follow the 'Keep existing' policy. <br>
	 * Preconditions: <br> 
	 * * {@code courseToUpdate} is not null and has valid data.
	 */
	public void updateCourse(CourseAttributes courseToUpdate) 
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseToUpdate);
		
		if (!courseToUpdate.isValid()) {
			throw new InvalidParametersException(courseToUpdate.getInvalidityInfo());
		}
		
		Course c = getCourseEntity(courseToUpdate.id);

		if (c == null) {
			String error = ERROR_UPDATE_NON_EXISTENT_COURSE + courseToUpdate.id;
			log.warning(error);
			throw new EntityDoesNotExistException(error);
		}
		
		c.setName(courseToUpdate.name);
		c.setCreatedAt(courseToUpdate.createdAt);
		
		getPM().close();
	}
	

	/**
	 * Note: This is a non-cascade delete.<br>
	 *   <br> Fails silently if there is no such object.
	 * <br> Preconditions: 
	 * <br> * {@code courseId} is not null.
	 */
	public void deleteCourse(String courseId) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

		Course courseToDelete = getCourseEntity(courseId);

		if (courseToDelete == null) {
			return;
		}

		getPM().deletePersistent(courseToDelete);
		getPM().flush();

		// wait for the operation to persist
		int elapsedTime = 0;
		Course courseCheck = getCourseEntity(courseId);
		while ((courseCheck != null)
				&& (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
			ThreadHelper.waitBriefly();
			courseCheck = getCourseEntity(courseId);
			elapsedTime += ThreadHelper.WAIT_DURATION;
		}
		if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteCourse->"
					+ courseId);
		}

	}
	
	private Course getCourseEntity(String courseId) {
		
		Query q = getPM().newQuery(Course.class);
		q.declareParameters("String courseIdParam");
		q.setFilter("ID == courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) q.execute(courseId);
		
		if (courseList.isEmpty() || JDOHelper.isDeleted(courseList.get(0))) {
			return null;
		}
	
		return courseList.get(0);
	}

	@Override
	protected Object getEntity(EntityAttributes attributes) {
			
		return getCourseEntity( ((CourseAttributes) attributes).id );
	}
}
