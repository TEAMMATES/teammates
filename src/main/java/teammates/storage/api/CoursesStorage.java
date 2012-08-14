package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Course;
import teammates.storage.entity.Student;

import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;

/**
 * Courses handles all operations related to a Teammates course. This is a
 * static class (singleton).
 * 
 */
public class CoursesStorage {
	private static CoursesStorage instance = null;
	private static final Logger log = Common.getLogger();

	/**
	 * Constructs a Courses object. Obtains an instance of PersistenceManager
	 * class to handle datastore transactions.
	 */
	private CoursesStorage() {
	}

	public PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	/**
	 * Retrieve singleton instance of Courses
	 * 
	 * @return
	 */
	public static CoursesStorage inst() {
		if (instance == null)
			instance = new CoursesStorage();
		return instance;
	}

	/**
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
		
		Course course = new Course(courseId, courseName, coordId);

		if (getCourse(courseId) != null) {
			throw new EntityAlreadyExistsException("Course already exists : "+courseId);
		}


		try {
			getPM().makePersistent(course);
			getPM().flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		int elapsedTime = 0;
		course = getCourse(courseId);
		while ((course == null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			course = getCourse(courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: addCourse->"+courseId);
		}
	}



	/**
	 * Archives a Course for a Coordinator.
	 * 
	 * @param ID
	 *            the course ID (Precondition: Must not be null)
	 */
	public void archiveCoordinatorCourse(String ID) {
		getCourse(ID).setArchived(true);

	}

	/**
	 * Archives a Course for a Student.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param googleID
	 *            the Google ID of the student (Precondition: Must not be null)
	 */
	public void archiveStudentCourse(String courseID, String googleID) {
		Student student = getStudentWithID(courseID, googleID);
		student.setCourseArchived(true);
	}


	
	public void createStudent(Student student) throws EntityAlreadyExistsException {
		String courseID = student.getCourseID();
		String email = student.getEmail();
		if(getStudentWithEmail(courseID, email)!=null){
			throw new EntityAlreadyExistsException("This student already existis :"+ courseID + "/" + email);
		}
		getPM().makePersistent(student);
		getPM().flush();
		
		int elapsedTime = 0;
		Student created = getStudentWithEmail(courseID, email);
		while ((created == null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			created = getStudentWithEmail(courseID, email);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: createStudent->"+ courseID + "/" + email);
		}
	}


	/**
	 * Clean up courses, evaluations, submissions related to a course
	 * 
	 * @param coordinatorID
	 * @author wangsha
	 * @throws EntityDoesNotExistException 
	 * @throws CourseDoesNotExistException 
	 * @date Sep 8, 2011
	 */
	public void deleteCoordinatorCourses(String coordinatorID){
		List<Course> courses = getCoordinatorCourseList(coordinatorID);
		Iterator<Course> it = courses.iterator();

		while (it.hasNext()) {
			deleteCoordinatorCourse(it.next().getID());

		}

	}

	/**
	 * Deletes a Course object, along with all the Student objects that belong
	 * to the course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @throws CourseDoesNotExistException
	 *             if the course with the specified ID cannot be found
	 */
	//TODO: does this actually delete students, as the comment say?
	@Deprecated 
	public void deleteCoordinatorCourse(String courseID){
		Course course = getCourse(courseID);

		if (course == null) {
			String errorMessage = "Trying to delete non-existent course : "
					+ courseID;
			log.info(errorMessage);
		}

		getPM().deletePersistent(course);
		getPM().flush();

	}
	
	public void deleteCourse(String courseId) {
		Course course = getCourse(courseId);

		if (course == null) {
			String errorMessage = "Trying to delete non-existent course : "
					+ courseId;
			log.warning(errorMessage);
		}
		
		deleteAllStudents(courseId);
		getPM().deletePersistent(course);
		getPM().flush();
		
		int elapsedTime = 0;
		course = getCourse(courseId);
		while ((course != null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			course = getCourse(courseId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: deleteCourse->"+courseId);
		}
		
	}

	/**
	 * Deletes the Student objects in a particular Course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 */
	public void deleteAllStudents(String courseID) {
		List<Student> studentList = getStudentList(courseID);
		log.info("Deleting "+studentList.size()+" students from the course "+courseID);
		getPM().deletePersistentAll(studentList);
		getPM().flush();
	}

	/**
	 * Deletes a Student object from a specific Course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param email
	 *            the email of the student (Precondition: Must not be null)
	 * @throws EntityDoesNotExistException
	 */
	public void deleteStudent(String courseID, String email){
		Student s = getStudentWithEmail(courseID, email);
		if (s == null) {
			String errorMessage = "Trying to delete non-existent student : "
					+ courseID + "/" + email;
			log.warning(errorMessage);
		} else {
			getPM().deletePersistent(s);
			getPM().flush();
		}
		
		int elapsedTime = 0;
		Student created = getStudentWithEmail(courseID, email);
		while ((created != null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)){
			Common.waitBriefly();
			created = getStudentWithEmail(courseID, email);
			elapsedTime += Common.WAIT_DURATION;
		}
		if(elapsedTime==Common.PERSISTENCE_CHECK_DURATION){
			log.severe("Operation did not persist in time: createStudent->"+ courseID + "/" + email);
		}
	}

	/**
	 * Deletes a Course from a Student by setting the ID of the Student to an
	 * empty String.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param googleID
	 *            the Google ID of the student (Precondition: Must not be null)
	 */
	public void deleteStudentCourse(String courseID, String googleID) {
		Student student = getStudentWithID(courseID, googleID);
		student.setID("");

	}

	/**
	 * Edits a Student object of a specific Course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: The courseID and email pair must
	 *            be valid)
	 * 
	 * @param email
	 *            the email of the student (Precondition: The courseID and email
	 *            pair must be valid)
	 * 
	 * @param newName
	 *            the new name of the student (Precondition: Must not be null)
	 * 
	 * @param newEmail
	 *            the new email of the student (Precondition: Must not be null)
	 * 
	 * @param newGoogleID
	 *            the new Google ID of the student (Precondition: Must not be
	 *            null)
	 * 
	 * @param newComments
	 *            the new comments of the student (Precondition: Must not be
	 *            null)
	 */
	@Deprecated
	public void editStudent(String courseID, String email, String newName, String newEmail, String newGoogleID, String newComments) {
		Student student = getStudentWithEmail(courseID, email);

		student.setComments((newComments));
		student.setEmail(newEmail);
		student.setID(newGoogleID);
		student.setName(newName);
		
		getPM().close();
	}

	/**
	 * Edits a Student object of a specific Course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: The courseID and email pair must
	 *            be valid)
	 * 
	 * @param email
	 *            the email of the student (Precondition: The courseID and email
	 *            pair must be valid)
	 * 
	 * @param newName
	 *            the new name of the student (Precondition: Must not be null)
	 * 
	 * @param newTeamName
	 *            the new team name of the student (Precondition: Must not be
	 *            null)
	 * 
	 * @param newEmail
	 *            the new email of the student (Precondition: Must not be null)
	 * 
	 * @param newGoogleID
	 *            the new Google ID of the student (Precondition: Must not be
	 *            null)
	 * 
	 * @param newComments
	 *            the new comments of the student (Precondition: Must not be
	 *            null)
	 */ 
	@Deprecated
	public void editStudent(String courseID, String email, String newName, String newTeamName, String newEmail, String newGoogleID, String newComments) {
		Student student = getStudentWithEmail(courseID, email);

		student.setComments((newComments));
		student.setEmail(newEmail);
		student.setID(newGoogleID);
		student.setName(newName);
		student.setTeamName(newTeamName);
		
		getPM().close();
	}
	
	public void editStudent(String courseID, String email, String newName,
			String newTeamName, String newEmail, String newGoogleID,
			String newComments, Text newProfile)
			throws EntityDoesNotExistException {
		Student student = getStudentWithEmail(courseID, email);
		if (student == null)
			throw new EntityDoesNotExistException("Student " + email
					+ " does not exist in course " + courseID);
		student.setEmail(newEmail);
		if(newName!=null){
			student.setName(newName);
		}
		
		if(newComments!=null){
			student.setComments(newComments);
		}
		if (newGoogleID != null) {
			student.setID(newGoogleID);
		}
		if (newTeamName != null) {
			student.setTeamName(newTeamName);
		}
		if(newProfile != null) {
			student.setProfileDetail(newProfile);
		}

		getPM().close();
	}

	


	/**
	 * Returns the list of Course objects of a Coordinator
	 * 
	 * @param coordinatorID
	 *            the Google ID of the coordinator (Precondition: Must not be
	 *            null)
	 * 
	 * @return List<Course> the list of courses of the coordinator
	 */
	public List<Course> getCoordinatorCourseList(String coordinatorID) {
		String query = "select from " + Course.class.getName() + " where coordinatorID == '" + coordinatorID + "'";

		@SuppressWarnings("unchecked")
		List<Course> courseList = (List<Course>) getPM().newQuery(query).execute();
		return courseList;
	}
	
	public HashMap<String, CourseData> getCourseSummaryListForCoord(String coordId){
		List<Course> courseList = getCoordinatorCourseList(coordId);
		HashMap<String, CourseData> courseSummaryList = new HashMap<String, CourseData>();

		for (Course c : courseList) {
			CourseData cd = new CourseData();
					cd.id = c.getID();
					cd.name = c.getName();
					cd.coord = coordId;
					cd.teamsTotal = getNumberOfTeams(c.getID());
					cd.studentsTotal = getTotalStudents(c.getID());
					cd.unregisteredTotal = 	getUnregistered(c.getID());
			courseSummaryList.put(c.getID(),cd);
		}
		return courseSummaryList;
	}
	
	/**
	 * Returns Student objects of the specified googleID.
	 * 
	 * @param googleID
	 *            the Google ID of the student (Precondition: Must not be null)
	 * 
	 * @return List<Student> the list of students that have the specified Google
	 *         ID
	 */
	public List<Student> getStudentListForGoogleId(String googleID) {
		String query = "select from " + Student.class.getName()
				+ " where ID == \"" + googleID + "\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query)
				.execute();

		return studentList;
	}
	/**
	 * Returns a course.
	 * 
	 * @param courseId
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return Course the course that has the specified ID
	 */
	public Course getCourse(String courseId) {
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

	/**
	 * Returns the number of teams in a Course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must be valid)
	 * 
	 * @return the number of teams in the course
	 */
	public int getNumberOfTeams(String courseID) {
		List<Student> studentList = getStudentList(courseID);
		List<String> teamNameList = new ArrayList<String>();

		for (Student s : studentList) {
			if (!teamNameList.contains(s.getTeamName())) {
				teamNameList.add(s.getTeamName());
			}
		}

		return teamNameList.size();
	}

	/**
	 * Returns a Student object of the specified courseID and email.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param email
	 *            the email of the student (Precondition: Must not be null)
	 * 
	 * @return the student who has the specified email in the specified course
	 */
	public Student getStudentWithEmail(String courseID, String email) {
		String query = "select from " + Student.class.getName() + " where courseID == \"" + courseID + "\" && email == \"" + email + "\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query).execute();

		if (studentList.isEmpty()) {
			return null;
		}

		return studentList.get(0);
	}

	/**
	 * Returns a Student object of the specified courseID and googleID.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param googleID
	 *            the Google ID of the student (Precondition: Must not be null)
	 * 
	 * @return Student the student who has the specified Google ID in the
	 *         specified course
	 */
	public Student getStudentWithID(String courseID, String googleID) {
		String query = "select from " + Student.class.getName() + " where courseID == \"" + courseID + "\" && ID == \"" + googleID + "\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query).execute();

		if (studentList.isEmpty()) {
			return null;
		}

		return studentList.get(0);
	}
	


	/**
	 * Returns a list of Student objects that matches the specified courseID.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return List<Student> the list of students that are in the course
	 */
	public List<Student> getStudentList(String courseID) {
		String query = "select from " + Student.class.getName() + " where courseID == \'" + courseID + "\'";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query).execute();

		return studentList;
	}

	/**
	 * Returns the team name of a Student in a particular Course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param email
	 *            the email of the student (Precondition: Must not be null)
	 * 
	 * @return the team name of the student in the course
	 */
	public String getTeamName(String courseID, String email) {
		String query = "select from " + Student.class.getName() + " where courseID == \"" + courseID + "\" && email == \"" + email + "\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query).execute();

		if (studentList.isEmpty()) {
			return null;
		}

		return studentList.get(0).getTeamName();
	}

	/**
	 * Returns the number of students in a Course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must be valid)
	 * 
	 * @return the number of students in the course
	 */
	public int getTotalStudents(String courseID) {
		List<Student> studentList = getStudentList(courseID);
		return studentList.size();
	}

	/**
	 * Returns the number of unregistered students in a Course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must be valid)
	 * 
	 * @return the number of unregistered students in the course
	 */
	public int getUnregistered(String courseID) {
		List<Student> unregistered = getUnregisteredStudentList(courseID);
		return unregistered.size();
	}

	/**
	 * Returns a list of Student objects that matches the specified courseID and
	 * which do not have a Google ID associated with it
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @return List<Student> the list of unregistered students that are in the
	 *         course
	 */
	public List<Student> getUnregisteredStudentList(String courseID) {
		String query = "select from " + Student.class.getName() + " where courseID == \"" + courseID + "\"" + " && ID == \"\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query).execute();

		return studentList;
	}

	/**
	 * Sets the ID of a particular Student object having the specified
	 * registration key.
	 * 
	 * @param registrationKey
	 *            the registration key of the student (Precondition: Must not be
	 *            null)
	 * 
	 * @param googleID
	 *            the Google ID of the student (Precondition: Must not be null)
	 * 
	 * @throws JoinCourseException
	 *             if the registration key does not exist
	 *             if the student has already registered in the course
	 *             if the registration key has been used by another student
	 */
	public void joinCourse(String registrationKey, String googleID) throws JoinCourseException {
		Student student = null;

		try {
			student = getPM().getObjectById(Student.class, KeyFactory.stringToKey(registrationKey));
		}catch (Exception e) {
			throw new JoinCourseException(Common.ERRORCODE_INVALID_KEY,
					"Invalid key :" + registrationKey);
		}
		
		if(alreadyHasGoogleId(student)){
			if(student.getID().equals(googleID)){
				throw new JoinCourseException(Common.ERRORCODE_ALREADY_JOINED,
						googleID + " is already joined this course");
			}else {
				throw new JoinCourseException(
						Common.ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER, googleID
								+ " belongs to a different user");
			}
		}
		
		student.setID(googleID);
		
		//TODO: using this to help unit testing, might not work in live server
		getPM().close();
	}

	private boolean alreadyHasGoogleId(Student student) {
		return !student.getID().equals("");
	}

	/**
	 * Sends registration keys to Students.
	 * 
	 * @param studentList
	 *            the list of students to send registration keys to
	 *            (Precondition: Must not be null)
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param courseName
	 *            the course name (Precondition: Must not be null)
	 * 
	 * @param coordinatorName
	 *            the name of the coordinator (Precondition: Must not be null)
	 */
	public void sendRegistrationKeys(List<Student> studentList, String courseID, String courseName, String coordinatorName, String coordinatorEmail) {
		Queue queue = QueueFactory.getQueue("email-queue");
		List<TaskOptions> taskOptionsList = new ArrayList<TaskOptions>();

		for (Student s : studentList) {
			// There is a limit of 100 tasks per batch addition to Queue in
			// Google App
			// Engine
			if (taskOptionsList.size() == 100) {
				queue.add(taskOptionsList);
				taskOptionsList = new ArrayList<TaskOptions>();
			}

			TaskOptions emailTask = TaskOptions.Builder.withUrl("/email").param("operation", "sendregistrationkey").param("email", s.getEmail())
					.param("regkey", KeyFactory.createKeyString(Student.class.getSimpleName(), s.getRegistrationKey())).param("courseid", courseID).param("coursename", courseName)
					.param("name", s.getName()).param("coordinatorname", coordinatorName).param("coordinatoremail", coordinatorEmail);
			taskOptionsList.add(emailTask);
		}

		if (!taskOptionsList.isEmpty()) {
			queue.add(taskOptionsList);
		}

	}

	/**
	 * Unarchives a Course belonging to a Coordinator.
	 * 
	 * @param ID
	 *            the course ID (Precondition: Must be valid)
	 */
	public void unarchiveCoordinatorCourse(String ID) {
		getCourse(ID).setArchived(false);
	}

	/**
	 * Unarchives a Course belonging to a Student.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: The courseID and email pair must
	 *            be valid)
	 * 
	 * @param email
	 *            the email of the student (Precondition: The courseID and email
	 *            pair must be valid)
	 */
	public void unarchiveStudentCourse(String courseID, String email) {
		Student student = getStudentWithEmail(courseID, email);
		student.setCourseArchived(false);

	}

	/**
	 * Retrieve all courses
	 * 
	 * @return
	 * @author huy
	 */
	@SuppressWarnings("unchecked")
	public List<Course> getAllCourses() {
		return (List<Course>) getPM().newQuery(Course.class).execute();
	}
	
	public static void sortByTeamName(List<StudentData> students) {
		Collections.sort(students, new Comparator<StudentData>() {
			public int compare(StudentData s1, StudentData s2) {
				String t1 = s1.team;
				String t2 = s2.team;
				if ((t1 == null) && (t2==null)){
					return 0;
				}else if (t1==null){
					return 1;
				}else if (t2==null){
					return -1;
				}
				return t1.compareTo(t2);
			}
		});
	}

	public List<CourseData> getCourseListForStudent(String googleId) {
		List<Student> studentList = getStudentListForGoogleId(googleId);
		ArrayList<CourseData> courseList = new ArrayList<CourseData>();

		for (Student s : studentList) {
			CourseData c = new CourseData();
			c.id = s.getCourseID();
			Course course = CoursesStorage.inst().getCourse(c.id);
			if (course == null) {
				log.severe("student exists, but the course does not:"+s.getID()+"/"+s.getCourseID());
			} else {
				c.name = course.getName();
				courseList.add(c);
			}
		}
		return courseList;
	}

	public void verifyCourseExists(String courseId) throws EntityDoesNotExistException {
		if(getCourse(courseId)==null){
			throw new EntityDoesNotExistException("The course "+courseId+" does not exist");
		}
		
	}

}