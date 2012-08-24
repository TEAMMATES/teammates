package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course;
import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;


public class CoursesDb {
	
	private static final Logger log = Common.getLogger();
	
	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}
	
	/**
	 * CREATE Course
	 * 
	 * Adds a Course under a specific Coordinator.
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param courseName
	 *            the course name (Precondition: Must not be null)
	 * 
	 * @param coordId
	 *            the Google ID of the coordinator (Precondition: Must not be
	 *            null)
	 * @throws InvalidParametersException 
	 * 
	 * @throws EntityAlreadyExistsException
	 *             if a course with the specified ID already exists
	 */
	public void addCourse(String courseId, String courseName, String coordId) throws InvalidParametersException, EntityAlreadyExistsException {
		
		// Check if entity already exists
		if (getCourse(courseId) != null) {
			throw new EntityAlreadyExistsException("Course already exists : "+courseId);
		}

		// Entity is new, create and make persist
		Course course = new Course(courseId, courseName, coordId);

		try {
			getPM().makePersistent(course);
			getPM().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Check insert operation persisted
		int elapsedTime = 0;
		CourseData courseCheck = getCourse(courseId);
		while ((courseCheck == null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			courseCheck = getCourse(courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: addCourse->"+courseId);
		}
	}
	
	
	

	
	
	
	
	
	
	/**
	 * RETRIEVE List<Course>
	 * 
	 * Retrieve all courses
	 * 
	 * @return
	 * @author huy / kenny
	 */
	@SuppressWarnings("unchecked")
	public List<CourseData> getAllCourses() {
		 
		List<Course> courseList = (List<Course>) getPM().newQuery(Course.class).execute();
		
		List<CourseData> courseDataList = new ArrayList<CourseData>();
		
		for (Course course : courseList) {
			courseDataList.add(new CourseData(course));
		}
		
		return courseDataList;
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
		String query = "select from " + Course.class.getName() + " where ID == \"" + courseId + "\"";

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query).execute();

		if (courseList.isEmpty()){
			String errorMessage = "Trying to get non-existent Course : " + courseId;
			log.fine(errorMessage);
			return null;
		}

		return new CourseData(courseList.get(0));
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * RETRIEVE List<Course>
	 * 
	 * Returns the list of Course objects of a Coordinator
	 * 
	 * @param coordinatorID
	 *            the Google ID of the coordinator (Precondition: Must not be
	 *            null)
	 * 
	 * @return List<Course> the list of courses of the coordinator
	 */
	public List<CourseData> getCoordinatorCourseList(String coordinatorID) {
		String query = "select from " + Course.class.getName() + " where coordinatorID == '" + coordinatorID + "'";

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query).execute();
		List<CourseData> courseDataList = new ArrayList<CourseData>();
		
		for (Course c : courseList) {
			courseDataList.add(new CourseData(c));
		}
		
		return courseDataList;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * UPDATE Course
	 * 
	 * Sets the archived attribute of this course to be TRUE
	 * 
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 */
	public void archiveCourse(String courseId, boolean archiveStatus) {
		String query = "select from " + Course.class.getName() + " where ID == \"" + courseId + "\"";
		
		@SuppressWarnings("unchecked")
		List<Course> c = (List<Course>) getPM().newQuery(query).execute();
		
		c.get(0).setArchived(archiveStatus);
	}

	
	
	
	
	
	
	
		
	
	/**
	 * DELETE Course 
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 */
	public void deleteCourse(String courseId) {
		
		String query = "select from " + Course.class.getName() + " where ID == \"" + courseId + "\"";
		
		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query).execute();
		
		if (courseList.isEmpty()) {
			String errorMessage = "Trying to delete non-existent course : "
					+ courseId;
			log.warning(errorMessage);
			return;
		}
		
		getPM().deletePersistent(courseList.get(0));
		getPM().flush();
		
		// Check delete operation persisted
		int elapsedTime = 0;
		CourseData courseCheck = getCourse(courseId);
		while ((courseCheck != null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			courseCheck = getCourse(courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: deleteCourse->"+courseId);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	

}
