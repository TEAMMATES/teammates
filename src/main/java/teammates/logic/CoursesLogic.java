package teammates.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.storage.api.SubmissionsDb;

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
	private static final StudentsDb studentsDb = new StudentsDb();
	private static final InstructorsDb instructorsDb = new InstructorsDb();
	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static final SubmissionsDb submissionsDb = new SubmissionsDb();

	
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
	
	public void createCourseAndInstructor(String instructorGoogleId, String courseId, String courseName) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		createCourse(courseId, courseName);
		
		AccountAttributes courseCreator = accountsDb.getAccount(instructorGoogleId);

		Assumption.assertNotNull(
				"Trying to create a course for a person who doesn't have instructor privileges :"+ instructorGoogleId, 
				courseCreator);
		
		InstructorAttributes instructor = new InstructorAttributes();
		instructor.googleId = instructorGoogleId;
		instructor.courseId = courseId;
		instructor.email = courseCreator.email;
		instructor.name = courseCreator.name;
		
		instructorsDb.createInstructor(instructor);
		//TODO: Handle the orphan course in case instructor cannot be created
	}

	public CourseAttributes getCourse(String courseId) {
		return coursesDb.getCourse(courseId);
	}

	public boolean isCoursePresent(String courseId) {
		return coursesDb.getCourse(courseId) != null;
	}

	public int getNumberOfTeams(String courseID) {

		List<StudentAttributes> studentDataList = studentsDb
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
		return studentsDb.getStudentsForCourse(courseId).size();
	}

	public int getTotalUnregisteredInCourse(String courseID) {
		return studentsDb.getUnregisteredStudentsForCourse(courseID).size();
	}

	public String getCourseInstitute(String courseId) {
		CourseAttributes cd = coursesDb.getCourse(courseId);
		List<InstructorAttributes> instructorList = instructorsDb.getInstructorsForCourse(cd.id);
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
	
	public List<CourseAttributes> getCoursesForStudentAccount(String googleId) throws EntityDoesNotExistException {
		
		List<StudentAttributes> studentDataList = studentsDb.getStudentsForGoogleId(googleId);
		
		if (studentDataList.size() == 0) {
			throw new EntityDoesNotExistException("Student with Google ID "
					+ googleId + " does not exist");
		}
		
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
	
	public HashMap<String, CourseDetailsBundle> getCourseSummariesForInstructor(String googleId) 
			throws EntityDoesNotExistException {
		
		List<InstructorAttributes> instructorAttributesList = instructorsDb.getInstructorsForGoogleId(googleId);
		
		if (!isInstructorAccount(googleId)) {
			throw new EntityDoesNotExistException(
					"Instructor does not exist or account does not have instructor privileges:" + googleId);
		}
		
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

	public CourseDetailsBundle getTeamsForCourse(String courseId) 
			throws EntityDoesNotExistException {
		
		List<StudentAttributes> students = studentsDb.getStudentsForCourse(courseId);
		StudentAttributes.sortByTeamName(students);
	
		CourseAttributes course = getCourse(courseId);
	
		if (course == null) {
			throw new EntityDoesNotExistException("The course " + courseId
					+ " does not exist");
		}
		
		CourseDetailsBundle cdd = new CourseDetailsBundle(course);
	
		TeamDetailsBundle team = null;
		for (int i = 0; i < students.size(); i++) {
	
			StudentAttributes s = students.get(i);
	
			// if loner
			if (s.team.equals("")) {
				cdd.loners.add(s);
				// first student of first team
			} else if (team == null) {
				team = new TeamDetailsBundle();
				team.name = s.team;
				team.students.add(s);
				// student in the same team as the previous student
			} else if (s.team.equals(team.name)) {
				team.students.add(s);
				// first student of subsequent teams (not the first team)
			} else {
				cdd.teams.add(team);
				team = new TeamDetailsBundle();
				team.name = s.team;
				team.students.add(s);
			}
	
			// if last iteration
			if (i == (students.size() - 1)) {
				cdd.teams.add(team);
			}
		}
	
		return cdd;
	}

	public void updateCourse(CourseAttributes course) 
			throws InvalidParametersException, EntityDoesNotExistException {
		if (!course.isValid()) {
			throw new InvalidParametersException(course.getInvalidStateInfo());
		}
		coursesDb.updateCourse(course);
	}
	

	public MimeMessage sendRegistrationInviteToStudent(String courseId, String studentEmail) 
			throws EntityDoesNotExistException {
		
		CourseAttributes course = coursesDb.getCourse(courseId);
		if (course == null) {
			throw new EntityDoesNotExistException(
					"Course does not exist [" + courseId + "], trying to send invite email to student [" + studentEmail + "]");
		}
		
		StudentAttributes studentData = studentsDb.getStudentForEmail(courseId, studentEmail);
		if (studentData == null) {
			throw new EntityDoesNotExistException(
					"Student [" + studentEmail + "] does not exist in course [" + courseId + "]");
		}
		
		Emails emailMgr = new Emails();
		try {
			MimeMessage email = emailMgr.generateStudentCourseJoinEmail(course, studentData);
			emailMgr.sendEmail(email);
			return email;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error while sending email", e);
		}
		
	}

	public void deleteCourseCascade(String courseId) {
		evaluationsDb.deleteAllEvaluationsForCourse(courseId);
		submissionsDb.deleteAllSubmissionsForCourse(courseId);
		studentsDb.deleteStudentsForCourse(courseId);
		instructorsDb.deleteInstructorsForCourse(courseId);
		coursesDb.deleteCourse(courseId);
	}

	private boolean isInstructorAccount(String googleId) {
		AccountAttributes account = accountsDb.getAccount(googleId);
		return (account != null) && (account.isInstructor);
	}



}
