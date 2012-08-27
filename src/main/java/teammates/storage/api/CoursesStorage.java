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
	 * Retrieve singleton instance of CoursesStorage
	 * 
	 * @return CoursesStorage
	 */
	public static CoursesStorage inst() {
		if (instance == null)
			instance = new CoursesStorage();
		return instance;
	}











	/**
	 * Atomically deletes a Course object, along with all the Student objects that belong
	 * to the course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 */
	public void deleteCourse(String courseId) {
		
		accountsDb.deleteAllStudentsInCourse(courseId);
		coursesDb.deleteCourse(courseId);
		
	}


	

	

	

	

	

	
	
	

	


	

	
	public HashMap<String, CourseData> getCourseSummaryListForCoord(String coordId){

		List<CourseData> courseList = coursesDb.getCourseListForCoordinator(coordId);
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

		List<StudentData> studentDataList = accountsDb.getStudentListForCourse(courseID);
		
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

		return accountsDb.getStudentListForCourse(courseID).size();

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

		return accountsDb.getUnregisteredStudentListForCourse(courseID).size();
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
