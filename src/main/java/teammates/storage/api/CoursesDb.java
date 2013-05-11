package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course;
import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;

public class CoursesDb {

	public static final String ERROR_CREATE_COURSE_ALREADY_EXISTS = "Trying to create a Course that exists: ";
	
	private static final Logger log = Common.getLogger();

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	/**
	 * CREATE Course
	 * 
	 * @throws EntityAlreadyExistsException
	 *             if a course with the specified ID already exists
	 */
	public void createCourse(CourseAttributes courseToAdd)
			throws EntityAlreadyExistsException {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseToAdd);
		
		Assumption.assertTrue(Common.toString(courseToAdd.getInvalidStateInfo()),
				courseToAdd.isValid());
		
		// Check if entity already exists
		if (getCourseEntity(courseToAdd.id) != null) {
			String error = ERROR_CREATE_COURSE_ALREADY_EXISTS + courseToAdd.id;
			log.warning(error);
			throw new EntityAlreadyExistsException(error);
		}

		// Entity is new, create and make persist
		Course newCourse = courseToAdd.toEntity();
		getPM().makePersistent(newCourse);
		getPM().flush();

		// Check insert operation persisted
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
	 * RETRIEVE Course
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return CourseData of the course that has the specified ID
	 */
	public CourseAttributes getCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		Course c = getCourseEntity(courseId);

		if (c == null) {
			log.warning("Trying to get non-existent Course: " + courseId);
			return null;
		}

		return new CourseAttributes(c);
	}
	
	/**
	 * UPDATE Course
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return CourseData of the course that has the specified ID
	 */
	public void updateCourse(CourseAttributes courseToUpdate) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseToUpdate);
		
		Course c = getCourseEntity(courseToUpdate.id);

		if (c == null) {
			Assumption.assertNotNull("Trying to update non-existent Course: " + courseToUpdate.id);
		}
		
		c.setName(courseToUpdate.name);
		c.setCreatedAt(courseToUpdate.createdAt);
		
		getPM().close();
	}
	
	// Used in DataMigration (will be removed after)
	// Takes a List input to use makePersistAll.
	public void updateCourses(List<CourseAttributes> courses) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courses);
		
		List<Course> updatedList = new ArrayList<Course>();
		for (CourseAttributes cd : courses) {
			Course updatedCourse = cd.toEntity();
			updatedCourse.setCreatedAt(cd.createdAt);
			updatedList.add(updatedCourse);
		}
		getPM().makePersistentAll(updatedList);
	}

	

	/**
	 * DELETE Course
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 */
	public void deleteCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		Course courseToDelete = getCourseEntity(courseId);

		if (courseToDelete == null) {
			return;
		}

		getPM().deletePersistent(courseToDelete);
		getPM().flush();

		// Check delete operation persisted
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

	/**
	 * Returns the actual Course Entity
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return Course
	 * 
	 */
	private Course getCourseEntity(String courseId) {
		String query = "select from " + Course.class.getName()
				+ " where ID == \"" + courseId + "\"";

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query)
				.execute();

		if (courseList.isEmpty() || JDOHelper.isDeleted(courseList.get(0))) {
			return null;
		}

		return courseList.get(0);
	}

	/**
	 * Returns all Course Entities 
	 */
	public List<CourseAttributes> getAllCourses() {
		String query = "select from " + Course.class.getName();

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query)
				.execute();

		List<CourseAttributes> courseDataList = new ArrayList<CourseAttributes>();
		for (Course c : courseList) {
			courseDataList.add(new CourseAttributes(c));
		}

		return courseDataList;
	}
}
