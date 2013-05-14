package teammates.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
 * Handles  operations related to courses.
 * This class does the field validation and sanitization before 
 * passing values to the Storage layer.
 */
public class CoursesLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, no need for header 
	//  comments.
	
	//TODO: add sanitization to this class.
	
	private static CoursesLogic instance = null;
	private static final Logger log = Common.getLogger();

	private static final CoursesDb coursesDb = new CoursesDb();
	private static final AccountsDb accountsDb = new AccountsDb();

	
	public static CoursesLogic inst() {
		if (instance == null)
			instance = new CoursesLogic();
		return instance;
	}

	public void createCourse(String courseId, String courseName) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		CourseAttributes courseToAdd = new CourseAttributes(courseId, courseName);
	
		if (!courseToAdd.isValid()) {
			throw new InvalidParametersException(Common.toString(courseToAdd.getInvalidStateInfo()));
		}
	
		coursesDb.createCourse(courseToAdd);
	}

	public CourseAttributes getCourse(String courseId) {
		return coursesDb.getCourse(courseId);
	}

	public boolean isCoursePresent(String courseId) {
		return coursesDb.getCourse(courseId) != null;
	}

	public int getNumberOfTeams(String courseID) {

		List<StudentAttributes> studentDataList = accountsDb
				.getStudentsForCourse(courseID);

		List<String> teamNameList = new ArrayList<String>();

		for (StudentAttributes sd : studentDataList) {
			if (!teamNameList.contains(sd.team)) {
				teamNameList.add(sd.team);
			}
		}

		return teamNameList.size();
	}

	public int getTotalEnrolledInCourse(String courseId) {
		return accountsDb.getStudentsForCourse(courseId).size();
	}

	public int getTotalUnregisteredInCourse(String courseID) {
		return accountsDb.getUnregisteredStudentsForCourse(courseID).size();
	}

	public String getCourseInstitute(String courseId) {
		CourseAttributes cd = coursesDb.getCourse(courseId);
		List<InstructorAttributes> instructorList = accountsDb.getInstructorsForCourse(cd.id);
		if (instructorList.isEmpty()) {
			Assumption.fail("Course has no instructors: " + cd.id);
		} 
		// Retrieve institute field from the first instructor of the course
		AccountAttributes instructorAcc = accountsDb.getAccount(instructorList.get(0).googleId);
		return instructorAcc.institute;
	
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
		cdd.studentsTotal = getTotalEnrolledInCourse(cd.id);
		cdd.unregisteredTotal = getTotalUnregisteredInCourse(cd.id);
		return cdd;
	}
	
	public List<CourseAttributes> getCoursesForGoogleId(String googleId) {
		List<StudentAttributes> studentDataList = accountsDb.getStudentsForGoogleId(googleId);
		ArrayList<CourseAttributes> courseList = new ArrayList<CourseAttributes>();

		for (StudentAttributes s : studentDataList) {
			CourseAttributes course = coursesDb.getCourse(s.course);
			if(course==null){
				log.warning(
						"Course was deleted but the Student still exists :"+Common.EOL 
						+ s.toString());
			}else{
				courseList.add(course);
			}
		}
		return courseList;
	}
	
	public HashMap<String, CourseDetailsBundle> getCourseSummaryListForInstructor(String instructorId) {
		List<InstructorAttributes> instructorAttributesList = accountsDb.getInstructorsForGoogleId(instructorId);
		
		HashMap<String, CourseDetailsBundle> courseSummaryList = new HashMap<String, CourseDetailsBundle>();
		
		for (InstructorAttributes ia : instructorAttributesList) {
			CourseAttributes course = coursesDb.getCourse(ia.courseId);
			
			try {
				courseSummaryList.put(course.id, getCourseSummary(course.id));
			} catch (EntityDoesNotExistException e) {
				log.warning("Course was deleted but the Instructor still exists: "+Common.EOL 
						+ ia.toString());
			}
		}
		
		return courseSummaryList;
	}
	
	public void updateCourse(CourseAttributes course) 
			throws InvalidParametersException, EntityDoesNotExistException {
		if (!course.isValid()) {
			throw new InvalidParametersException(course.getInvalidStateInfo());
		}
		coursesDb.updateCourse(course);
	}
	

	public void deleteCourseCascade(String courseId) {
		accountsDb.deleteStudentsForCourse(courseId);
		accountsDb.deleteInstructorsForCourse(courseId);
		coursesDb.deleteCourse(courseId);
		//TODO: cascade to evaluations too.
	}



}
