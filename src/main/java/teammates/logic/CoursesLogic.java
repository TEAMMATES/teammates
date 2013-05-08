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
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
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

		List<StudentAttributes> studentDataList = accountsDb
				.getStudentListForCourse(courseID);

		// The list of teams
		List<String> teamNameList = new ArrayList<String>();

		// Filter out unique team names
		for (StudentAttributes sd : studentDataList) {
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

	public static void sortByTeamName(List<StudentAttributes> students) {
		Collections.sort(students, new Comparator<StudentAttributes>() {
			public int compare(StudentAttributes s1, StudentAttributes s2) {
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
		CourseAttributes courseToAdd = new CourseAttributes(courseId, courseName);

		if (!courseToAdd.isValid()) {
			throw new InvalidParametersException(courseToAdd.getInvalidStateInfo());
		}
	
		coursesDb.createCourse(courseToAdd);
	}

	//==========================================================================
	public CourseAttributes getCourse(String courseId) {
		return coursesDb.getCourse(courseId);
	}

	public CourseDetailsBundle getCourseSummary(String courseId)
			throws EntityDoesNotExistException {
		CourseAttributes cd = coursesDb.getCourse(courseId);

		if (cd == null) {
			throw new EntityDoesNotExistException("The course does not exist: "
					+ courseId);
		}

		CourseDetailsBundle cdd = new CourseDetailsBundle(cd);
		cdd.teamsTotal = getNumberOfTeams(cd.id);
		cdd.studentsTotal = getTotalStudents(cd.id);
		cdd.unregisteredTotal = getUnregistered(cd.id);
		return cdd;
	}
	
	public List<CourseAttributes> getCourseListForStudent(String googleId) {
		// Get all Student entries with this googleId
		List<StudentAttributes> studentDataList = accountsDb.getStudentsWithGoogleId(googleId);
		ArrayList<CourseAttributes> courseList = new ArrayList<CourseAttributes>();

		// Verify that the course in each entry is existent
		for (StudentAttributes s : studentDataList) {
			CourseAttributes course = coursesDb.getCourse(s.course);
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
		CourseAttributes cd = coursesDb.getCourse(courseId);
		List<InstructorAttributes> instructorList = accountsDb.getInstructorsByCourseId(cd.id);
		if (instructorList.isEmpty()) {
			Assumption.fail("Course has no instructors: " + cd.id);
		} 
		// Retrieve institute field from the first instructor of the course
		AccountAttributes instructorAcc = accountsDb.getAccount(instructorList.get(0).googleId);
		return instructorAcc.institute;

	}
	
	public HashMap<String, CourseDetailsBundle> getCourseSummaryListForInstructor(String instructorId) {
		List<InstructorAttributes> instructorDataList = accountsDb.getInstructorsByGoogleId(instructorId);
		
		HashMap<String, CourseDetailsBundle> courseSummaryList = new HashMap<String, CourseDetailsBundle>();
		for (InstructorAttributes id : instructorDataList) {
			CourseAttributes cd = coursesDb.getCourse(id.courseId);
			
			if (cd == null) {
				Assumption.fail("INSTRUCTOR RELATION EXISTED, BUT COURSE WAS NOT FOUND: " + instructorId + ", " + id.courseId);
			}
			
			CourseDetailsBundle cdd = new CourseDetailsBundle(cd);
			cdd.teamsTotal = getNumberOfTeams(cd.id);
			cdd.studentsTotal = getTotalStudents(cd.id);
			cdd.unregisteredTotal = getUnregistered(cd.id);
			courseSummaryList.put(cd.id, cdd);
		}
		
		return courseSummaryList;
	}
	
	// TODO: To be modified to handle API for retrieve paginated results of Courses
	public HashMap<String, CourseDetailsBundle> getCourseSummaryListForInstructor(String instructorId, long lastRetrievedTime, int numberToRetrieve) {
		List<InstructorAttributes> instructorDataList = accountsDb.getInstructorsByGoogleId(instructorId);
		
		int count = 0;
		HashMap<String, CourseDetailsBundle> courseSummaryList = new HashMap<String, CourseDetailsBundle>();
		for (InstructorAttributes id : instructorDataList) {
			CourseAttributes cd = coursesDb.getCourse(id.courseId);

			if (cd == null) {
				Assumption.fail("INSTRUCTOR RELATION EXISTED, BUT COURSE WAS NOT FOUND: " + instructorId + ", " + id.courseId);
			}
			if (cd.createdAt.before(new Date(lastRetrievedTime))) {
				// Discard
				continue;
			}
			
			CourseDetailsBundle cdd = new CourseDetailsBundle(cd);
			cdd.teamsTotal = getNumberOfTeams(cd.id);
			cdd.studentsTotal = getTotalStudents(cd.id);
			cdd.unregisteredTotal = getUnregistered(cd.id);
			courseSummaryList.put(cd.id, cdd);
			
			if (++count >= numberToRetrieve) {
				break;
			}
		}
		
		return courseSummaryList;
	}
	
	//==========================================================================
	// Not used
	public void updateCourse(CourseAttributes course) throws InvalidParametersException {
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
