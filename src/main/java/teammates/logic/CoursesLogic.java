package teammates.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;

/**
 * Courses handles all operations related to a Teammates course. This is a
 * static class (singleton).
 * 
 */
public class CoursesLogic {
	private static CoursesLogic instance = null;
	private static final Logger log = Common.getLogger();

	private static final CoursesDb coursesDb = new CoursesDb();
	private static final AccountsDb accountsDb = new AccountsDb();

	/**
	 * Retrieve singleton instance of CoursesStorage
	 * 
	 * @return CoursesStorage
	 */
	public static CoursesLogic inst() {
		if (instance == null)
			instance = new CoursesLogic();
		return instance;
	}

	//==========================================================================
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

		List<StudentData> studentDataList = accountsDb
				.getStudentListForCourse(courseID);

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
				if ((t1 == null) && (t2 == null)) {
					return 0;
				} else if (t1 == null) {
					return 1;
				} else if (t2 == null) {
					return -1;
				}
				return t1.compareTo(t2);
			}
		});
	}

	public boolean isCourseExists(String courseId) {
		return coursesDb.getCourse(courseId) != null;
	}

	//==========================================================================
	public void createCourse(String courseId, String courseName) throws InvalidParametersException, EntityAlreadyExistsException {
		CourseData courseToAdd = new CourseData(courseId, courseName);

		if (!courseToAdd.isValid()) {
			throw new InvalidParametersException(courseToAdd.getInvalidStateInfo());
		}
	
		coursesDb.createCourse(courseToAdd);
	}

	//==========================================================================
	public CourseData getCourse(String courseId) {
		return coursesDb.getCourse(courseId);
	}

	public CourseData getCourseSummary(String courseId)
			throws EntityDoesNotExistException {
		CourseData cd = coursesDb.getCourse(courseId);

		if (cd == null) {
			throw new EntityDoesNotExistException("The course does not exist: "
					+ courseId);
		}

		cd.teamsTotal = getNumberOfTeams(cd.id);
		cd.studentsTotal = getTotalStudents(cd.id);
		cd.unregisteredTotal = getUnregistered(cd.id);
		return cd;
	}
	
	public List<CourseData> getCourseListForStudent(String googleId) {
		// Get all Student entries with this googleId
		List<StudentData> studentDataList = accountsDb.getStudentsWithGoogleId(googleId);
		ArrayList<CourseData> courseList = new ArrayList<CourseData>();

		// Verify that the course in each entry is existent
		for (StudentData s : studentDataList) {
			CourseData course = coursesDb.getCourse(s.course);
			Assumption.assertNotNull("Course was deleted but Student entry still exists", course);
			courseList.add(course);
		}
		return courseList;
	}
	
	/**
	 * Returns the Institute string which the specified Course belongs to
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must be valid)
	 * 
	 * @return String institute
	 */
	public String getCourseInstitute(String courseId) {
		CourseData cd = coursesDb.getCourse(courseId);
		List<InstructorData> instructorList = accountsDb.getInstructorsByCourseId(cd.id);
		if (instructorList.isEmpty()) {
			Assumption.fail("Course has no instructors: " + cd.id);
		} 
		// Retrieve institute field from the first instructor of the course
		AccountData instructorAcc = accountsDb.getAccount(instructorList.get(0).googleId);
		return instructorAcc.institute;

	}
	
	public HashMap<String, CourseData> getCourseSummaryListForInstructor(String instructorId) {
		List<InstructorData> instructorDataList = accountsDb.getInstructorsByGoogleId(instructorId);
		
		HashMap<String, CourseData> courseSummaryList = new HashMap<String, CourseData>();
		for (InstructorData id : instructorDataList) {
			CourseData cd = coursesDb.getCourse(id.courseId);
			
			if (cd == null) {
				Assumption.fail("INSTRUCTOR RELATION EXISTED, BUT COURSE WAS NOT FOUND: " + instructorId + ", " + id.courseId);
			}
			
			cd.teamsTotal = getNumberOfTeams(cd.id);
			cd.studentsTotal = getTotalStudents(cd.id);
			cd.unregisteredTotal = getUnregistered(cd.id);
			courseSummaryList.put(cd.id, cd);
		}
		
		return courseSummaryList;
	}
	
	// TODO: To be modified to handle API for retrieve paginated results of Courses
	public HashMap<String, CourseData> getCourseSummaryListForInstructor(String instructorId, long lastRetrievedTime, int numberToRetrieve) {
		List<InstructorData> instructorDataList = accountsDb.getInstructorsByGoogleId(instructorId);
		
		int count = 0;
		HashMap<String, CourseData> courseSummaryList = new HashMap<String, CourseData>();
		for (InstructorData id : instructorDataList) {
			CourseData cd = coursesDb.getCourse(id.courseId);

			if (cd == null) {
				Assumption.fail("INSTRUCTOR RELATION EXISTED, BUT COURSE WAS NOT FOUND: " + instructorId + ", " + id.courseId);
			}
			//System.out.println(cd.createdAt + ", " + (new Date(lastRetrievedTime)));
			if (cd.createdAt.before(new Date(lastRetrievedTime))) {
				// Discard
				continue;
			}
			
			cd.teamsTotal = getNumberOfTeams(cd.id);
			cd.studentsTotal = getTotalStudents(cd.id);
			cd.unregisteredTotal = getUnregistered(cd.id);
			courseSummaryList.put(cd.id, cd);
			
			if (++count >= numberToRetrieve) {
				break;
			}
		}
		
		return courseSummaryList;
	}
	
	//==========================================================================
	// Not used
	public void updateCourse(CourseData course) throws InvalidParametersException {
		if (!course.isValid()) {
			throw new InvalidParametersException(course.getInvalidStateInfo());
		}
		coursesDb.updateCourse(course);
	}
	
	//==========================================================================
	/**
	 * Atomically deletes a Course object, along with all the Student objects
	 * that belong to the course.
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 */
	public void deleteCourse(String courseId) {
		accountsDb.deleteAllStudentsInCourse(courseId);
		accountsDb.deleteInstructorsByCourseId(courseId);
		coursesDb.deleteCourse(courseId);

	}

}
