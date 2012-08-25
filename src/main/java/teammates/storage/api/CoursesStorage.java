package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityDoesNotExistException;


/**
 * Courses handles all operations related to a Teammates course. This is a
 * static class (singleton).
 * 
 */
public class CoursesStorage {
	private static CoursesStorage instance = null;
	private static final Logger log = Common.getLogger();
	
	private static final CoursesDb coursesDb = new CoursesDb();
	private static final AccountsDb accountsDb = new AccountsDb();

	/**
	 * Constructs a Courses object. Obtains an instance of PersistenceManager
	 * class to handle datastore transactions.
	 */
	private CoursesStorage() {
	}

	
	/**
	 * Retrieve singleton instance of CoursesStorage
	 * 
	 * @return
	 */
	public static CoursesStorage inst() {
		if (instance == null)
			instance = new CoursesStorage();
		return instance;
	}

	



	/**
	 * Atomically archives/unarchives a Course and Students that belong to the course
	 * 
	 * @param ID
	 *            the course ID (Precondition: Must not be null)
	 */
	public void archiveCourse(String courseId, boolean archiveStatus) throws EntityDoesNotExistException{
		
		if ( coursesDb.getCourse(courseId) != null ) {
			coursesDb.archiveCourse(courseId, archiveStatus);
			accountsDb.archiveCourse(courseId, archiveStatus);
		} else {
			throw new EntityDoesNotExistException("Trying to archive non-existent course: " + courseId);
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
	/*
	 * This function is not used in workspace, remove?
	public void deleteCoordinatorCourses(String coordinatorID){
		List<Course> courses = getCoordinatorCourseList(coordinatorID);
		Iterator<Course> it = courses.iterator();

		while (it.hasNext()) {
			deleteCoordinatorCourse(it.next().getID());

		}

	}
	*/

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
	/*
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
	*/
	public void deleteCourse(String courseId) {
		
		coursesDb.deleteCourse(courseId);
		accountsDb.deleteAllStudentsInCourse(courseId);
		
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
	// Can we remove this unused function?
	/*
	public void deleteStudentCourse(String courseID, String googleID) {
		Student student = getStudentWithID(courseID, googleID);
		student.setID("");
	}
	*/

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
	/* Can we remove this unused function?
	@Deprecated
	public void editStudent(String courseID, String email, String newName, String newEmail, String newGoogleID, String newComments) {
		Student student = getStudentWithEmail(courseID, email);

		student.setComments((newComments));
		student.setEmail(newEmail);
		student.setID(newGoogleID);
		student.setName(newName);
		
		getPM().close();
	}
	*/

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
	/* Can we remove this unused function?
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
	*/
	
	

	


	
	
	public HashMap<String, CourseData> getCourseSummaryListForCoord(String coordId){
		List<CourseData> courseList = coursesDb.getCoordinatorCourseList(coordId);
		HashMap<String, CourseData> courseSummaryList = new HashMap<String, CourseData>();

		for (CourseData cd : courseList) {
			cd.teamsTotal = getNumberOfTeams(cd.id);
			cd.studentsTotal = getTotalStudents(cd.id);
			cd.unregisteredTotal = 	getUnregistered(cd.id);
			courseSummaryList.put(cd.id,cd);
		}
		return courseSummaryList;
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
		// Get all students in the course
		List<StudentData> studentDataList = accountsDb.getStudentList(courseID);
		
		// The list of teams
		List<String> teamNameList = new ArrayList<String>();

		// Filter out unique team names
		for (StudentData sd : studentDataList) {
			if (!teamNameList.contains(sd.team)) {
				teamNameList.add(sd.team);
			}
		}

		return teamNameList.size();
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
	/* The function that uses this is unused, can we remove?
	public Student getStudentWithID(String courseID, String googleID) {
		String query = "select from " + Student.class.getName() + " where courseID == \"" + courseID + "\" && ID == \"" + googleID + "\"";

		@SuppressWarnings("unchecked")
		List<Student> studentList = (List<Student>) getPM().newQuery(query).execute();

		if (studentList.isEmpty()) {
			return null;
		}

		return studentList.get(0);
	}
	*/
	


	

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
	public String getTeamName(String courseId, String email) {
		
		return accountsDb.getStudent(courseId, email).team;
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
		return accountsDb.getStudentList(courseID).size();
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
		return accountsDb.getUnregisteredStudentList(courseID).size();
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
	/* Can we remove this unused function?
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
	*/

	

	

	
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
		
		// Get all Student entries with this googleId
		List<StudentData> studentDataList = accountsDb.getStudentsWithGoogleId(googleId);
		ArrayList<CourseData> courseList = new ArrayList<CourseData>();

		// Verify that the course in each entry is existent
		for (StudentData s : studentDataList) {
			
			CourseData course = coursesDb.getCourse(s.course);
			if (course == null) {
				log.severe("student exists, but the course does not:"+s.id+"/"+s.course);
			} else {
				courseList.add(course);
			}
		}
		return courseList;
	}

	public void verifyCourseExists(String courseId) throws EntityDoesNotExistException {
		if (coursesDb.getCourse(courseId)==null){
			throw new EntityDoesNotExistException("The course "+courseId+" does not exist");
		}
		
	}
	
	
	public CoursesDb getDb() {
		return coursesDb;
	}
	
	

}