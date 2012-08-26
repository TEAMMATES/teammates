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
	 * Creates a Course under a specific Coordinator.
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
	 *            
	 * @throws InvalidParametersException 
	 * 
	 * @throws EntityAlreadyExistsException
	 *             if a course with the specified ID already exists
	 */
	public void createCourse(String courseId, String courseName, String coordId) throws InvalidParametersException, EntityAlreadyExistsException {
		
		// Check if entity already exists
		if (getCourseEntity(courseId) != null) {
			throw new EntityAlreadyExistsException("Course already exists : "+courseId);
		}

		// Entity is new, create and make persist
		Course course = new Course(courseId, courseName, coordId);

		getPM().makePersistent(course);
		getPM().flush();
		
		// Check insert operation persisted
		int elapsedTime = 0;
		Course courseCheck = getCourseEntity(courseId);
		while ((courseCheck == null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			courseCheck = getCourseEntity(courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: createCourse->"+courseId);
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
		
		Course c = getCourseEntity(courseId);
		
		return c == null ? null : new CourseData(c);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * RETRIEVE List<Course>
	 * 
	 * Returns the list of Course objects of a Coordinator
	 * 
	 * @param coordId
	 *            the Google ID of the coordinator (Precondition: Must not be
	 *            null)
	 * 
	 * @return List<Course> the list of courses of the coordinator
	 */
	public List<CourseData> getCourseListForCoordinator(String coordId) {
		String query = "select from " + Course.class.getName() + " where coordinatorID == '" + coordId + "'";

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query).execute();
		List<CourseData> courseDataList = new ArrayList<CourseData>();
		
		for (Course c : courseList) {
			courseDataList.add(new CourseData(c));
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
		
		Course courseToDelete = getCourseEntity(courseId);
		
		if (courseToDelete == null) {
			log.warning("Trying to delete non-existent Course: " + courseId);
			return;
		}
		
		getPM().deletePersistent(courseToDelete);
		getPM().flush();
		
		// Check delete operation persisted
		int elapsedTime = 0;
		Course courseCheck = getCourseEntity(courseId);
		while ((courseCheck != null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			courseCheck = getCourseEntity(courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: deleteCourse->"+courseId);
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
		String query = "select from " + Course.class.getName() + " where ID == \"" + courseId + "\"";

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query).execute();

		if (courseList.isEmpty()){
			String errorMessage = "Trying to get non-existent Course : " + courseId;
			log.fine(errorMessage);
			return null;
		}

		return courseList.get(0);
	}
	
	
	

}
