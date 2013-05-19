package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course;
import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

/**
 * Handles CRUD Operations for course entities.
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
 */
public class CoursesDb {

	public static final String ERROR_CREATE_COURSE_ALREADY_EXISTS = "Trying to create a Course that exists: ";
	public static final String ERROR_UPDATE_NON_EXISTENT_COURSE = "Trying to update a Course that doesn't exist: ";
	
	private static final Logger log = Common.getLogger();

	/**
	 * Preconditions: <br>
	 * * {@code courseToAdd} is not null and has valid data.
	 */
	public void createCourse(CourseAttributes courseToAdd)
			throws EntityAlreadyExistsException, InvalidParametersException {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseToAdd);

		if (!courseToAdd.isValid()) {
			throw new InvalidParametersException(Common.toString(courseToAdd.getInvalidStateInfo()));
		}
		
		if (getCourseEntity(courseToAdd.id) != null) {
			String error = ERROR_CREATE_COURSE_ALREADY_EXISTS + courseToAdd.id;
			log.warning(error);
			throw new EntityAlreadyExistsException(error);
		}

		Course newCourse = courseToAdd.toEntity();
		getPM().makePersistent(newCourse);
		getPM().flush();

		// Wait for the operation to persist
		int elapsedTime = 0;
		Course courseCheck = getCourseEntity(courseToAdd.id);
		while ((courseCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			courseCheck = getCourseEntity(courseToAdd.id);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createCourse->"
					+ courseToAdd.id);
		}
	}

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public CourseAttributes getCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
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
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseToUpdate);
		
		if (!courseToUpdate.isValid()) {
			throw new InvalidParametersException(courseToUpdate.getInvalidStateInfo());
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
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

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
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			courseCheck = getCourseEntity(courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteCourse->"
					+ courseId);
		}

	}

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
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
}
