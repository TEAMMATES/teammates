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
import teammates.common.datatransfer.CourseData;
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
	public void createCourse(CourseData courseToAdd)
			throws EntityAlreadyExistsException {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseToAdd);
		
		Assumption.assertTrue(courseToAdd.getInvalidStateInfo(),
				courseToAdd.isValid());
		
		// Check if entity already exists
		if (getCourseEntity(courseToAdd.id) != null) {
			String error = ERROR_CREATE_COURSE_ALREADY_EXISTS
					+ courseToAdd.id;

			log.warning(error + "\n" + Common.getCurrentThreadStack());

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
	public CourseData getCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		Course c = getCourseEntity(courseId);

		if (c == null) {
			log.warning("Trying to get non-existent Course: " + courseId
					+ Common.getCurrentThreadStack());
			return null;
		}

		return new CourseData(c);
	}

	/**
	 * RETRIEVE List<Course>
	 * 
	 * Returns the list of Course objects of a Instructor
	 * 
	 * @param instructorId
	 *            the Google ID of the instructor (Precondition: Must not be
	 *            null)
	 * 
	 * @return List<Course> the list of courses of the instructor
	 */
	public List<CourseData> getCourseListForInstructor(String instructorId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, instructorId);
		
		String query = "select from " + Course.class.getName()
				+ " where coordinatorID == '" + instructorId + "'";

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query)
				.execute();
		List<CourseData> courseDataList = new ArrayList<CourseData>();

		for (Course c : courseList) {
			if (!JDOHelper.isDeleted(c)) {
				courseDataList.add(new CourseData(c));
			}
		}

		return courseDataList;
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
	public List<CourseData> getAllCourses() {
		String query = "select from " + Course.class.getName();

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query)
				.execute();

		List<CourseData> courseDataList = new ArrayList<CourseData>();
		for (Course c : courseList) {
			courseDataList.add(new CourseData(c));
		}

		return courseDataList;
	}
}
