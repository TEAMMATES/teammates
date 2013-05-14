package teammates.logic.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import teammates.common.Assumption;
import teammates.common.BuildProperties;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.datatransfer.TeamDetailsBundle;

import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.TeamResultBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.exception.NotImplementedException;
import teammates.logic.AccountsLogic;
import teammates.logic.CoursesLogic;
import teammates.logic.Emails;
import teammates.logic.EvaluationsLogic;
import teammates.logic.GateKeeper;
import teammates.logic.TeamEvalResult;

import com.google.appengine.api.datastore.Text; //TODO: remove this dependency

/**
 * This class represents the API to the business logic of the system. Please
 * refer to DevMan for general policies followed by Logic. As those policies
 * cover most of the behavior of the API, we use very short comments to describe
 * operations here.
 */
public class Logic {

	private static Logger log = Common.getLogger();

	public static final String ERROR_NULL_PARAMETER = "The supplied parameter was null\n";
	public static final String ERROR_UPDATE_NON_EXISTENT_COURSE = "Trying to update non-existent Course: ";
	public static final String ERROR_NO_INSTRUCTOR_LINES = "Course must have at lease one instructor\n";
	public static final String ERROR_COURSE_CREATOR_NO_ACCOUNT = "An instructor with no ACCOUNT was able to create a course\n";
	
	protected static GateKeeper gateKeeper = GateKeeper.inst();
	protected static AccountsLogic accountsLogic = AccountsLogic.inst();
	protected static CoursesLogic coursesLogic = CoursesLogic.inst();
	protected static EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();

	@SuppressWarnings("unused")
	private void ____USER_level_methods__________________________________() {
	}

	/**
	 * Produces the URL the user should use to login to the system
	 * 
	 * @param redirectUrl
	 *            This is the URL the user will be directed to after login.
	 */
	public static String getLoginUrl(String redirectUrl) {
		return gateKeeper.getLoginUrl(redirectUrl);
	}

	/**
	 * Produces the URL used to logout the user
	 * 
	 * @param redirectUrl
	 *            This is the URL the user will be directed to after logout.
	 */
	public static String getLogoutUrl(String redirectUrl) {
		return gateKeeper.getLogoutUrl(redirectUrl);
	}

	/**
	 * Verifies if the user is logged into his/her Google account
	 */
	public static boolean isUserLoggedIn() {
		return gateKeeper.isLoggedOn();
	}

	/**
	 * @return Returns null if the user is not logged in.
	 */
	public UserType getLoggedInUser() {
		return gateKeeper.getLoggedInUser();
	}
	
	public boolean isInstructor(String googleId) {
		return accountsLogic.isInstructor(googleId);
	}
	
	public boolean isInstructorOfCourse(String googleId, String courseId) {
		return accountsLogic.isInstructorOfCourse(googleId, courseId);
	}

	@SuppressWarnings("unused")
	private void ____ACCOUNT_level_methods____________________________________() {
	}

	/**
	 * Access: Admin only
	 * 
	 * @param googleId
	 * @param name
	 * @param isInstructor
	 * @param email
	 * @param institute
	 * @throws EntityAlreadyExistsException 
	 * @throws InvalidParametersException 
	 */
	public void createAccount(String googleId, String name, boolean isInstructor,
								String email, String institute) throws InvalidParametersException, EntityAlreadyExistsException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, name);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, isInstructor);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, institute);
		
		gateKeeper.verifyAdminLoggedIn();
		accountsLogic.createAccount(googleId, name, isInstructor, email, institute);
	}
	
	/**
	 * Access: any logged in user
	 */
	public AccountAttributes getAccount(String googleId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		gateKeeper.verifyLoggedInUserAndAbove();
		AccountAttributes account = accountsLogic.getAccount(googleId);
		return account;
	}
	
	/**
	 * Access: Admin only
	 */
	public List<AccountAttributes> getInstructorAccounts() {
		gateKeeper.verifyAdminLoggedIn();
		List<AccountAttributes> accounts = accountsLogic.getInstructorAccounts();
		return accounts;
	}
	
	/**
	 * 
	 * @param a
	 * @throws InvalidParametersException
	 */
	public void updateAccount(AccountAttributes a) throws InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, a);
		gateKeeper.verifyAdminLoggedIn();
		accountsLogic.updateAccount(a);
	}
	
	/**
	 * Access: Admin only
	 */
	public void deleteAccount(String googleId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		gateKeeper.verifyAdminLoggedIn();
		accountsLogic.deleteAccountCascade(googleId);
	}

	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_level_methods____________________________________() {
	}

	/**
	 * Access: admin only
	 */
	public void createInstructor(String googleId, String courseId, String name, String email, String institute)
			throws EntityAlreadyExistsException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, name);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, institute);

		gateKeeper.verifyAdminLoggedIn();
		accountsLogic.createInstructor(googleId, courseId, name, email, institute);
	}

	/**
	 * Access: any logged in user
	 */
	public InstructorAttributes getInstructor(String instructorId, String courseId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyLoggedInUserAndAbove();
		InstructorAttributes instructor = accountsLogic.getInstructorForGoogleId(courseId, instructorId);
		return instructor;
	}
	
	/**
	 * Access: admin only
	 */
	public List<InstructorAttributes> getAllInstructors() {
		gateKeeper.verifyAdminLoggedIn();
		return accountsLogic.getAllInstructors();
	}
	
	/**
	 * Returns ALL COURSE::ID for this INSTRUCTOR GoogleId
	 * 
	 * @param googleId
	 * @return List<InstructorData>
	 */
	public List<InstructorAttributes> getInstructorRolesForAccount(String googleId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		gateKeeper.verifyInstructorUsingOwnIdOrAbove(googleId);
		return accountsLogic.getInstructorsForGoogleId(googleId);
	}

	/**
	 * Returns ALL INSTRUCTORS for this COURSE
	 * 
	 * @param courseId
	 * @return List<InstructorData>
	 */
	public List<InstructorAttributes> getInstructorsOfCourse(String courseId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);
		return accountsLogic.getInstructorsForCourse(courseId);
	}

	/**
	 * To update Name and Email fields
	 * @throws InvalidParametersException 
	 * 
	 */
	public void editInstructor(InstructorAttributes instructor) throws InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructor);
		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructor.googleId);
		accountsLogic.updateInstructor(instructor);
	}

	/**
	 * Access: Admin only
	 */
	public void deleteInstructor(String instructorId, String courseId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyAdminLoggedIn();
		accountsLogic.deleteInstructor(courseId, instructorId);
	}

	/**
	 * Access: Admin only
	 * 
	 * Deletes all INSTRUCTOR-COURSE relations for this INSTRUCTOR
	 */
	public void deleteInstructor(String instructorId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);
		gateKeeper.verifyAdminLoggedIn();
		accountsLogic.deleteInstructorsForGoogleId(instructorId);
	}

	/**
	 * Access level: Admin, Instructor (for self)
	 * 
	 * @return Returns a less-detailed version of Instructor's course data
	 */
	public HashMap<String, CourseDetailsBundle> getCourseListForInstructor(
			String instructorId) throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorId);

		verifyInstructorExists(instructorId);

		HashMap<String, CourseDetailsBundle> courseSummaryListForInstructor = coursesLogic.getCourseSummaryListForInstructor(instructorId);

		return courseSummaryListForInstructor;
	}

	// TODO: To be modified to handle API for retrieve paginated results of Courses
	/**
	 * Access level: Admin, Instructor (for self)
	 * With 2 additional parameters
	 * 
	 * @return Returns a less-detailed version of Instructor's course data
	 */
	public HashMap<String, CourseDetailsBundle> getCourseListForInstructor(
			String instructorId, long lastRetrievedTime, int numberToRetrieve) 
					throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, lastRetrievedTime);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, numberToRetrieve);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorId);

		verifyInstructorExists(instructorId);

		HashMap<String, CourseDetailsBundle> courseSummaryListForInstructor = coursesLogic.getCourseSummaryListForInstructor(instructorId, lastRetrievedTime, numberToRetrieve);

		return courseSummaryListForInstructor;
	}

	/**
	 * Access level: Admin, Instructor (for self)
	 * 
	 * @return Returns a more-detailed version of Instructor's course data <br>
	 */
	public HashMap<String, CourseDetailsBundle> getCourseDetailsListForInstructor(
			String instructorId) throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorId);

		// TODO: using this method here may not be efficient as it retrieves
		// info not required
		HashMap<String, CourseDetailsBundle> courseList = coursesLogic.getCourseSummaryListForInstructor(instructorId);
		ArrayList<EvaluationDetailsBundle> evaluationList = getEvaluationsListForInstructor(instructorId);
		for (EvaluationDetailsBundle edd : evaluationList) {
			CourseDetailsBundle courseSummary = courseList.get(edd.evaluation.course);
			courseSummary.evaluations.add(edd);
		}
		return courseList;
	}

	/**
	 * Access level: Admin, Instructor (for self)
	 * 
	 * @return Returns a less-detailed version of Instructor's evaluations <br>
	 */
	public ArrayList<EvaluationDetailsBundle> getEvaluationsListForInstructor(
			String instructorId) throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorId);

		verifyInstructorExists(instructorId);

		ArrayList<EvaluationDetailsBundle> evaluationSummaryList = new ArrayList<EvaluationDetailsBundle>();

		List<InstructorAttributes> instructorList = accountsLogic.getInstructorsForGoogleId(instructorId);
		for (InstructorAttributes id : instructorList) {
			evaluationSummaryList.addAll(getEvaluationsListForCourse(id.courseId));
		}
		return evaluationSummaryList;
	}

	/**
	 * Access level: Admin, Instructor (for self), Student(in
	 * getCourseDetails(..))
	 * 
	 * @return Returns a less-detailed version of Instructor's evaluations <br>
	 */
	public ArrayList<EvaluationDetailsBundle> getEvaluationsListForCourse(String courseId)
			throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);

		ArrayList<EvaluationDetailsBundle> evaluationSummaryList = new ArrayList<EvaluationDetailsBundle>();

		List<EvaluationAttributes> evaluationsSummaryForCourse = evaluationsLogic.getEvaluationsForCourse(courseId);
		List<StudentAttributes> students = accountsLogic.getStudentsForCourse(courseId);

		for (EvaluationAttributes evaluation : evaluationsSummaryForCourse) {
			EvaluationDetailsBundle edd = getEvaluationDetails(students, evaluation);
			evaluationSummaryList.add(edd);
		}

		return evaluationSummaryList;
	}

	@SuppressWarnings("unused")
	private void ____COURSE_level_methods__________________________________() {
	}

	/**
	 * Access level: Instructor and above
	 * 
	 * If instructorId is null then only the course will be created with no instructor owning it
	 * (only used in restoring data bundle in test cases)
	 */
	public void createCourse(String instructorId, String courseId,
			String courseName) throws EntityAlreadyExistsException,
			InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseName);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorId);

		coursesLogic.createCourse(courseId, courseName);

		// Create an instructor relation for the INSTRUCTOR that created this course
		// The INSTRUCTOR relation is created here with NAME, EMAIL and INSTITUTION fields retrieved from his AccountData
		// Otherwise, createCourse() method will have to take in 3 extra parameters for them which is not a good idea
		AccountAttributes courseCreator = accountsLogic.getAccount(instructorId);
		Assumption.assertNotNull(ERROR_COURSE_CREATOR_NO_ACCOUNT + Common.getCurrentThreadStack(), courseCreator);
		accountsLogic.createInstructor(instructorId, courseId, courseCreator.name, courseCreator.email, courseCreator.institute);
	}
	
	/**
	 * AccessLevel : any registered user (because it is too expensive to check
-	 * if a student is in the course)
	 */
	public CourseAttributes getCourse(String courseId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		gateKeeper.verifyRegisteredUserOrAbove();
		CourseAttributes c = coursesLogic.getCourse(courseId);
		return c;
	}

	/**
	 * Returns a detailed version of course data, including evaluation data
	 * Access: course owner, student in course, admin
	 */
	public CourseDetailsBundle getCourseDetails(String courseId)
			throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);

		// TODO: very inefficient. Should be optimized. (sorta fixed)
		// Previously it calls a function which prepares ALL the courses for an
		// instructor,
		// then returns the selected course from the list.
		// Now it simply prepares the requesteed course
		CourseDetailsBundle courseSummary = coursesLogic.getCourseSummary(courseId);

		ArrayList<EvaluationDetailsBundle> evaluationList = getEvaluationsListForCourse(courseSummary.course.id);
		for (EvaluationDetailsBundle edd : evaluationList) {
			courseSummary.evaluations.add(edd);
		}

		return courseSummary;
	}

	public void editCourse(CourseAttributes course) throws NotImplementedException {
		throw new NotImplementedException("Not implemented because we do "
				+ "not allow editing courses");
	}
	
	/**
	 * Access level: Course Instructor and above
	 * 
	 * @param courseId
	 * @param instructorLines
	 * @throws InvalidParametersException
	 * 
	 * Pre-condition: instructorLines must have AT LEAST ONE instructor
	 */
	public void updateCourseInstructors(String courseId, String instructorLines, String courseInstitute) 
			throws InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorLines);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseInstitute);

		if (!coursesLogic.isCourseExists(courseId)) {
			Assumption.fail(ERROR_UPDATE_NON_EXISTENT_COURSE + courseId);
		}
		
		gateKeeper.verifyCourseOwnerOrAbove(courseId);
		
		// Prepare the list to be updated
		List<InstructorAttributes> instructorsList = parseInstructorLines(courseId, instructorLines);
		
		// Retrieve the current list of instructors
		// Remove those that are not in the list and persist the new ones
		// Edit the ones that are found in both lists
		List<InstructorAttributes> currentInstructors = accountsLogic.getInstructorsForCourse(courseId);
		
		List<InstructorAttributes> toAdd = new ArrayList<InstructorAttributes>();
		List<InstructorAttributes> toRemove = new ArrayList<InstructorAttributes>();
		List<InstructorAttributes> toEdit = new ArrayList<InstructorAttributes>();
		
		// Find new names
		for (InstructorAttributes id : instructorsList) {
			boolean found = false;
			for (InstructorAttributes currentInstructor : currentInstructors) {
				if (id.googleId.equals(currentInstructor.googleId)) {
					toEdit.add(id);
					found = true;
				}
			}
			if (!found) {
				toAdd.add(id);
			}
		}
		
		// Find lost names
		for (InstructorAttributes currentInstructor : currentInstructors) {
			boolean found = false;
			for (InstructorAttributes id : instructorsList) {
				if (id.googleId.equals(currentInstructor.googleId)) {
					found = true;
				}
			}
			if (!found) {
				toRemove.add(currentInstructor);
			}
		}
		
		// Operate on each of the lists respectively
		for (InstructorAttributes add : toAdd) {
			try {
				accountsLogic.createInstructor(add.googleId, courseId, add.name, add.email, courseInstitute);  
			} catch (EntityAlreadyExistsException e) {
				// This should happens when a row was accidentally entered twice
				// When that happens we continue silently
			}
		}
		for (InstructorAttributes remove : toRemove) {
			accountsLogic.deleteInstructor(remove.courseId, remove.googleId);
		}
		for (InstructorAttributes edit : toEdit) {
			accountsLogic.updateInstructor(edit);
		}
	}

	/**
	 * Access: course owner and above
	 */
	public void deleteCourse(String courseId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		evaluationsLogic.deleteEvaluationsForCourse(courseId);
		coursesLogic.deleteCourse(courseId);
	}

	/**
	 * Access: course owner and above
	 */
	public List<StudentAttributes> getStudentListForCourse(String courseId)
			throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		List<StudentAttributes> studentDataList = accountsLogic.getStudentsForCourse(courseId);

		if ((studentDataList.size() == 0) && (getCourse(courseId) == null)) {
			throw new EntityDoesNotExistException("Course does not exist :"
					+ courseId);
		}

		return studentDataList;
	}

	/**
	 * Access: course owner and above
	 * 
	 * @return Returns the list of emails sent. These can be used for
	 *         verification.
	 */
	public List<MimeMessage> sendRegistrationInviteForCourse(String courseId)
			throws InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		List<StudentAttributes> studentDataList = accountsLogic.getUnregisteredStudentsForCourse(courseId);

		ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();

		//TODO: sending mail should be moved to somewhere else.
		for (StudentAttributes s : studentDataList) {
			try {
				MimeMessage email = sendRegistrationInviteToStudent(courseId,
						s.email);
				emailsSent.add(email);
			} catch (EntityDoesNotExistException e) {
				Assumption
						.fail("Unexpected EntitiyDoesNotExistException thrown when sending registration email"
								+ Common.stackTraceToString(e));
			}
		}
		return emailsSent;
	}

	/**
	 * Enrolls new students in the course or modifies existing students. But it
	 * will not delete any students. It will not edit email address either. If
	 * an existing student was enrolled with a different email address, that
	 * student will be treated as a new student.<br>
	 * If there is an error in the enrollLines, there will be no changes to the
	 * datastore <br>
	 * Access: course owner and above
	 * 
	 * @return StudentData objects in the return value contains the status of
	 *         enrollment. It also includes data for other students in the
	 *         course that were not touched by the operation.
	 */
	public List<StudentAttributes> enrollStudents(String enrollLines, String courseId)
			throws EnrollException, EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		if (!coursesLogic.isCourseExists(courseId)) {
			throw new EntityDoesNotExistException("Course does not exist :" + courseId);
		}

		Assumption.assertNotNull(StudentAttributes.ERROR_ENROLL_LINE_NULL,
				enrollLines);

		ArrayList<StudentAttributes> returnList = new ArrayList<StudentAttributes>();
		String[] linesArray = enrollLines.split(Common.EOL);
		ArrayList<StudentAttributes> studentList = new ArrayList<StudentAttributes>();

		// check if all non-empty lines are formatted correctly
		for (int i = 0; i < linesArray.length; i++) {
			String line = linesArray[i];
			try {
				if (Common.isWhiteSpace(line))
					continue;
				studentList.add(new StudentAttributes(line, courseId));
			} catch (InvalidParametersException e) {
				throw new EnrollException(e.errorCode, "Problem in line : "
						+ line + Common.EOL + e.getMessage());
			}
		}

		//TODO: can we use a batch persist operation here?
		// enroll all students
		for (StudentAttributes student : studentList) {
			StudentAttributes studentInfo;
			studentInfo = enrollStudent(student);
			returnList.add(studentInfo);
		}

		// add to return list students not included in the enroll list.
		List<StudentAttributes> studentsInCourse = getStudentListForCourse(courseId);
		for (StudentAttributes student : studentsInCourse) {
			if (!isInEnrollList(student, returnList)) {
				student.updateStatus = StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST;
				returnList.add(student);
			}
		}

		return returnList;
	}

	/**
	 * Access: course owner, student in course, admin
	 * 
	 * @return The CourseData object that is returned will contain attributes
	 *         teams(type:TeamData) and loners(type:StudentData). We do not
	 *         expect any loners as the current system does not support students
	 *         without teams. This field is to be removed later. <br>
	 *         Access : course owner and above
	 */
	public CourseDetailsBundle getTeamsForCourse(String courseId)
			throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);

		List<StudentAttributes> students = getStudentListForCourse(courseId);
		CoursesLogic.sortByTeamName(students);

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

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods__________________________________() {
	}

	/**
	 * Creates a student and adjust existing evaluations to accommodate the new
	 * student Access: course owner and above
	 */
	public void createStudent(StudentAttributes studentData)
			throws EntityAlreadyExistsException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentData);

		gateKeeper.verifyCourseOwnerOrAbove(studentData.course);

		accountsLogic.createStudent(studentData);

		// adjust existing evaluations to accommodate new student
		evaluationsLogic.adjustSubmissionsForNewStudent(studentData.course, studentData.email, studentData.team);
	}

	/**
	 * Access: any registered user (to minimize cost of checking)
	 * 
	 * @return returns null if there is no such student.
	 */
	public StudentAttributes getStudent(String courseId, String email) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);

		gateKeeper.verifyRegisteredUserOrAbove();

		StudentAttributes studentData = accountsLogic.getStudentForEmail(courseId, email);
		return studentData;
	}

	/**
	 * Access: instructor of course and above.<br>
	 * All attributes except courseId be changed. Trying to change courseId will
	 * be treated as trying to edit a student in a different course.<br>
	 * Changing team name will not delete existing submissions under that team <br>
	 * Cascade logic: Email changed-> changes email in all existing submissions <br>
	 * Team changed-> creates new submissions for the new team, deletes
	 * submissions for previous team structure
	 */
	public void editStudent(String originalEmail, StudentAttributes student)
			throws InvalidParametersException, EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, originalEmail);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);

		gateKeeper.verifyCourseOwnerOrAbove(student.course);

		if (!accountsLogic.isStudentInCourse(student.course, originalEmail)) {
			throw new EntityDoesNotExistException("Non-existent student " + student.course + "/" + originalEmail);
		}

		StudentAttributes originalStudent = accountsLogic.getStudentForEmail(student.course, originalEmail);
		String originalTeam = originalStudent.team;

		accountsLogic.updateStudent(originalEmail, student);

		// cascade email change, if any
		if (!originalEmail.equals(student.email)) {
			evaluationsLogic.updateStudentEmailForSubmissionsInCourse(student.course, originalEmail, student.email);
		}

		// adjust submissions if moving to a different team
		if (isTeamChanged(originalTeam, student.team)) {
			evaluationsLogic.adjustSubmissionsForChangingTeam(student.course, student.email, originalTeam, student.team);
		}
	}

	/**
	 * Access: course owner and above
	 */
	public void deleteStudent(String courseId, String studentEmail) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		accountsLogic.deleteStudent(courseId, studentEmail);
		evaluationsLogic.deleteAllSubmissionsForStudent(courseId, studentEmail);
	}

	/**
	 * Access: course owner and above
	 */
	public MimeMessage sendRegistrationInviteToStudent(String courseId,
			String studentEmail) throws EntityDoesNotExistException,
			InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		if (!coursesLogic.isCourseExists(courseId)) {
			throw new EntityDoesNotExistException("Course does not exist [" + courseId + "], trying to send invite email to student [" + studentEmail + "]");
		}
		
		if (!accountsLogic.isStudentInCourse(courseId, studentEmail)) {
			throw new EntityDoesNotExistException("Student [" + studentEmail + "] does not exist in course [" + courseId + "]");
		}

		CourseAttributes course = coursesLogic.getCourse(courseId);
		StudentAttributes studentData = accountsLogic.getStudentForEmail(courseId, studentEmail);
		Emails emailMgr = new Emails();
		try {
			MimeMessage email = emailMgr.generateStudentCourseJoinEmail(course, studentData);
			emailMgr.sendEmail(email);
			return email;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error while sending email", e);
		}

	}

	/**
	 * Access: same student and admin only
	 * 
	 * @return Returns all StudentData objects associated with this Google ID.
	 *         Returns an empty list if no student has this Google ID.
	 */
	public List<StudentAttributes> getStudentsWithGoogleId(String googleId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		gateKeeper.verifySameStudentOrAdmin(googleId);
		return accountsLogic.getStudentsForGoogleId(googleId);
	}

	/**
	 * Access: same student and admin only
	 * 
	 * @return Returns the StudentData object that has the given courseId and is
	 *         in given course. Returns null if no such student in the course.
	 */
	public StudentAttributes getStudentInCourseForGoogleId(String courseId, String googleId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);

		gateKeeper.verifySameStudentOrCourseOwnerOrAdmin(courseId, googleId);
		StudentAttributes sd = accountsLogic.getStudentForGoogleId(courseId, googleId);
		return sd;
	}

	/**
	 * Access: owner of googleId
	 * @throws EntityAlreadyExistsException 
	 */
	public void joinCourse(String googleId, String key)
			throws JoinCourseException, InvalidParametersException, EntityAlreadyExistsException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, key);

		gateKeeper.verifyOwnerOfId(googleId);

		StudentAttributes newJoinedStudent = accountsLogic.joinCourse(key, googleId);

		// Create the Account if it does not exist
		if (accountsLogic.getAccount(googleId)==null) {
			// Need to retrieve the INSTITUTE of COURSE which this student is enrolling into, for creating his/her ACCOUNT
			CourseAttributes cd = coursesLogic.getCourse(newJoinedStudent.course);
			accountsLogic.createAccount(googleId, newJoinedStudent.name, false, newJoinedStudent.email, coursesLogic.getCourseInstitute(cd.id));
		}
	}

	/**
	 * Access: course owner and above
	 * 
	 * @return Returns registration key for a student in the given course.
	 */
	public String getKeyForStudent(String courseId, String email) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		StudentAttributes studentData = accountsLogic.getStudentForEmail(courseId, email);

		if (studentData == null) {
			return null;
		}

		// TODO: this should be pushed to lower levels
		return studentData.key;
	}

	/**
	 * Access: student who owns the googleId, admin
	 * 
	 * @return Returns Courses associated with the student with the given Google
	 *         ID
	 * @throws EntityDoesNotExistException
	 *             Thrown if no such student.
	 */
	public List<CourseAttributes> getCourseListForStudent(String googleId)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);

		gateKeeper.verifySameStudentOrAdmin(googleId);

		if (getStudentsWithGoogleId(googleId).size() == 0) {
			throw new EntityDoesNotExistException("Student with Google ID "
					+ googleId + " does not exist");
		}

		return coursesLogic.getCourseListForStudent(googleId);
	}

	/**
	 * Access: any logged in user (to minimize cost of checking)
	 */
	public boolean hasStudentSubmittedEvaluation(String courseId,
			String evaluationName, String studentEmail)
			throws InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

		gateKeeper.verifyLoggedInUserAndAbove();

		List<SubmissionAttributes> submissions = null;
		try {
			submissions = getSubmissionsFromStudent(courseId, evaluationName,
					studentEmail);
		} catch (EntityDoesNotExistException e) {
			return false;
		}

		if (submissions == null) {
			return false;
		}

		for (SubmissionAttributes sd : submissions) {
			if (sd.points != Common.POINTS_NOT_SUBMITTED) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Access: student who owns the googleId, admin
	 * 
	 * @return Returns details of courses the student is in. CourseData objects
	 *         returned contain details of evaluations too (except the ones
	 *         still AWAITING).
	 */
	public List<CourseDetailsBundle> getCourseDetailsListForStudent(String googleId)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);

		gateKeeper.verifySameStudentOrAdmin(googleId);

		// Get the list of courses that this student is in
		List<CourseAttributes> courseList = getCourseListForStudent(googleId);
		List<CourseDetailsBundle> courseDetailsList = new ArrayList<CourseDetailsBundle>();

		// For each course the student is in
		for (CourseAttributes c : courseList) {
			// Get the list of evaluations for the course
			List<EvaluationAttributes> evaluationDataList = evaluationsLogic.getEvaluationsForCourse(c.id);

			CourseDetailsBundle cdd = new CourseDetailsBundle(c);
			// For the list of evaluations for this course
			for (EvaluationAttributes ed : evaluationDataList) {
				EvaluationDetailsBundle edd = new EvaluationDetailsBundle(ed);
				// Add this evaluation to the course's list of evaluations.
				log.fine("Adding evaluation " + ed.name + " to course " + c.id);
				if (ed.getStatus() != EvalStatus.AWAITING) {
					cdd.evaluations.add(edd);
				}
			}
			courseDetailsList.add(cdd);
		}
		return courseDetailsList;
	}

	/**
	 * Access: owner of the course, owner of result (when PUBLISHED), admin
	 */
	public StudentResultBundle getEvaluationResultForStudent(String courseId,
			String evaluationName, String studentEmail)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

		gateKeeper.verfyCourseOwner_OR_EmailOwnerAndPublished(courseId, evaluationName,
				studentEmail);
		
		StudentAttributes student = getStudent(courseId, studentEmail);
		if (student == null) {
			throw new EntityDoesNotExistException("The student " + studentEmail
					+ " does not exist in course " + courseId);
		}

		EvaluationResultsBundle evaluationResults = getEvaluationResult(courseId,
				evaluationName);
		TeamResultBundle teamData = evaluationResults.teamResults.get(student.team);
		StudentResultBundle returnValue = null;

		returnValue = teamData.getStudentResult(studentEmail);

		for (StudentResultBundle srb : teamData.studentResults) {
			returnValue.selfEvaluations.add(teamData.getStudentResult(srb.student.email).getSelfEvaluation());
		}

		if (evaluationResults.evaluation.p2pEnabled) {
			returnValue.sortIncomingByFeedbackAscending();
		}
		
		return returnValue;
	}

	@SuppressWarnings("unused")
	private void ____EVALUATION_level_methods______________________________() {
	}

	/**
	 * Access: course owner and above
	 * 
	 * @throws InvalidParametersException
	 *             is thrown if any of the parameters puts the evaluation in an
	 *             invalid state (e.g., endTime is set before startTime).
	 *             However, setting start time to a past time is allowed.
	 */
	public void createEvaluation(EvaluationAttributes evaluation)
			throws EntityAlreadyExistsException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluation);

		gateKeeper.verifyCourseOwnerOrAbove(evaluation.course);

		if (!evaluation.isValid()) {
			throw new InvalidParametersException(
					evaluation.getInvalidStateInfo());
		}

		evaluationsLogic.createEvaluation(evaluation);
	}

	/**
	 * Access: all registered users
	 * 
	 */
	public EvaluationAttributes getEvaluation(String courseId, String evaluationName) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyRegisteredUserOrAbove();

		EvaluationAttributes e = evaluationsLogic.getEvaluation(courseId, evaluationName);

		return e;
	}

	/**
	 * Can be used to change all fields exception "activated" field <br>
	 * Access: owner and above
	 * 
	 * @throws InvalidParametersException
	 *             if new values bring the evaluation to an invalid state.
	 */
	public void editEvaluation(String courseId, String evaluationName,
			String instructions, Date start, Date end, double timeZone,
			int gracePeriod, boolean p2pEnabled)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructions);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, start);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, end);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, timeZone);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, gracePeriod);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, p2pEnabled);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		if (!evaluationsLogic.isEvaluationExists(courseId, evaluationName)) {
			throw new EntityDoesNotExistException("Trying to edit non-existent evaluation " + courseId + "/" + evaluationName);
		}
		
		EvaluationAttributes original = getEvaluation(courseId, evaluationName);

		EvaluationAttributes evaluation = new EvaluationAttributes();
		evaluation.course = courseId;
		evaluation.name = evaluationName;
		evaluation.instructions = instructions;
		evaluation.p2pEnabled = p2pEnabled;
		evaluation.startTime = start;
		evaluation.endTime = end;
		evaluation.gracePeriod = gracePeriod;
		evaluation.timeZone = timeZone;

		// this field cannot be changed via this method
		evaluation.activated = original.activated;
		evaluation.published = original.published;

		evaluationsLogic.updateEvaluation(evaluation);
	}

	/**
	 * Access: owner and above
	 */
	public void deleteEvaluation(String courseId, String evaluationName) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		evaluationsLogic.deleteEvaluation(courseId, evaluationName);
	}

	/**
	 * Access: course owner and above
	 * 
	 * @throws InvalidParametersException
	 *             if the evaluation is not ready to be published.
	 */
	public void publishEvaluation(String courseId, String evaluationName)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		if (!evaluationsLogic.isEvaluationExists(courseId, evaluationName)) {
			throw new EntityDoesNotExistException("Trying to edit non-existent evaluation " + courseId + "/" + evaluationName);
		}

		EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);
		if (evaluation.getStatus() != EvalStatus.CLOSED) {
			throw new InvalidParametersException(
					Common.ERRORCODE_PUBLISHED_BEFORE_CLOSING,
					"Cannot publish an evaluation unless it is CLOSED");
		}

		evaluationsLogic.setEvaluationPublishedStatus(courseId, evaluationName, true);
		sendEvaluationPublishedEmails(courseId, evaluationName);
	}

	/**
	 * Access: course owner and above
	 * 
	 * @throws InvalidParametersException
	 *             if the evaluation is not ready to be unpublished.
	 */
	public void unpublishEvaluation(String courseId, String evaluationName)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		if (!evaluationsLogic.isEvaluationExists(courseId, evaluationName)) {
			throw new EntityDoesNotExistException("Trying to edit non-existent evaluation " + courseId + "/" + evaluationName);
		}
		
		EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);
		if (evaluation.getStatus() != EvalStatus.PUBLISHED) {
			throw new InvalidParametersException(
					Common.ERRORCODE_UNPUBLISHED_BEFORE_PUBLISHING,
					"Cannot unpublish an evaluation unless it is PUBLISHED");
		}

		evaluationsLogic.setEvaluationPublishedStatus(courseId, evaluationName, false);
	}

	/**
	 * Sends reminders to students who haven't submitted yet. Access: course
	 * owner and above
	 */
	public List<MimeMessage> sendReminderForEvaluation(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		if (!evaluationsLogic.isEvaluationExists(courseId, evaluationName)) {
			throw new EntityDoesNotExistException("Trying to edit non-existent evaluation " + courseId + "/" + evaluationName);
		}

		EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);

		// Filter out students who have submitted the evaluation
		List<StudentAttributes> studentDataList = accountsLogic.getStudentsForCourse(courseId);

		List<StudentAttributes> studentsToRemindList = new ArrayList<StudentAttributes>();
		for (StudentAttributes sd : studentDataList) {
			if (!evaluationsLogic.isEvaluationSubmitted(evaluation,
					sd.email)) {
				studentsToRemindList.add(sd);
			}
		}

		CourseAttributes course = getCourse(courseId);

		List<MimeMessage> emails;

		Emails emailMgr = new Emails();
		try {
			emails = emailMgr.generateEvaluationReminderEmails(course,
					evaluation, studentsToRemindList);
			emailMgr.sendEmails(emails);
		} catch (Exception e) {
			throw new RuntimeException("Error while sending emails :", e);
		}

		return emails;
	}

	/**
	 * Access: course owner and above
	 */
	public EvaluationResultsBundle getEvaluationResult(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseOwnerOrAbove(courseId);

		//TODO: The datatype below is not a good fit for the data. ArralyList<TeamDetailsBundle>?
		List<TeamDetailsBundle> teams = getTeamsForCourse(courseId).teams;
		EvaluationResultsBundle returnValue = new EvaluationResultsBundle();
		returnValue.evaluation = getEvaluation(courseId, evaluationName);
		
		HashMap<String, SubmissionAttributes> submissionDataList = getSubmissionsForEvaluation(
				courseId, evaluationName);
		returnValue.teamResults = new TreeMap<String,TeamResultBundle>();
		
		for (TeamDetailsBundle team : teams) {
			TeamResultBundle teamResultBundle = new TeamResultBundle(team.students);
			
			for (StudentAttributes student : team.students) {
				// TODO: refactor this method. May be have a return value?
				populateSubmissionsAndNames(submissionDataList,
						teamResultBundle,
						teamResultBundle.getStudentResult(student.email));
			}
			
			TeamEvalResult teamResult = calculateTeamResult(teamResultBundle);
			populateTeamResult(teamResultBundle, teamResult);
			returnValue.teamResults.put(team.name, teamResultBundle);
		}
		return returnValue;
	}

	/**
	 * @return Returns all submissions by a student for the given evaluation
	 *         Access: course owner, reviewer, admin
	 * 
	 */
	public List<SubmissionAttributes> getSubmissionsFromStudent(String courseId,
			String evaluationName, String reviewerEmail)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, reviewerEmail);

		gateKeeper.verifyReviewerOrCourseOwnerOrAdmin(courseId, reviewerEmail);

		List<SubmissionAttributes> submissions = evaluationsLogic.getSubmissionsFromEvaluationFromStudent(courseId, evaluationName, reviewerEmail);

		boolean isSubmissionsExist = (submissions.size() > 0
				&& coursesLogic.isCourseExists(courseId)
				&& evaluationsLogic.isEvaluationExists(courseId,
						evaluationName) && accountsLogic.isStudentInCourse(courseId, reviewerEmail));

		if (!isSubmissionsExist) {
			throw new EntityDoesNotExistException(
					"Error getting submissions from student: " + courseId
							+ " / " + evaluationName + ", reviewer: "
							+ reviewerEmail);
		}

		StudentAttributes student = getStudent(courseId, reviewerEmail);
		ArrayList<SubmissionAttributes> returnList = new ArrayList<SubmissionAttributes>();
		for (SubmissionAttributes sd : submissions) {
			StudentAttributes reviewee = getStudent(courseId, sd.reviewee);
			if (!isOrphanSubmission(student, reviewee, sd)) {
				sd.reviewerName = student.name;
				sd.revieweeName = reviewee.name;
				returnList.add(sd);
			}
		}
		return returnList;
	}
	


	@SuppressWarnings("unused")
	private void ____SUBMISSION_level_methods_____________________________() {
	}

	public void createSubmission(SubmissionAttributes submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because submissions "
						+ "are created automatically");
	}

	/**
	 * Access: course owner, reviewer (if OPEN), admin
	 */
	public void editSubmissions(List<SubmissionAttributes> submissionDataList)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, submissionDataList);

		for (SubmissionAttributes sd : submissionDataList) {
			gateKeeper.verifySubmissionEditableForUser(sd);
			editSubmission(sd);
		}
	}

	public void deleteSubmission(SubmissionAttributes submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because submissions "
						+ "are deleted automatically");
	}

	@SuppressWarnings("unused")
	private void ____helper_methods________________________________________() {
	}

	private boolean isTeamChanged(String originalTeam, String newTeam) {
		return (newTeam != null) && (originalTeam != null)
				&& (!originalTeam.equals(newTeam));
	}

	private EvaluationDetailsBundle getEvaluationDetails(List<StudentAttributes> students, EvaluationAttributes evaluation)
			throws EntityDoesNotExistException {
		EvaluationDetailsBundle edd = new EvaluationDetailsBundle(evaluation);
		edd.stats.expectedTotal = students.size();
		HashMap<String, SubmissionAttributes> submissions = getSubmissionsForEvaluation(evaluation.course, evaluation.name);
		edd.stats.submittedTotal = countSubmittedStudents(submissions.values());
		return edd;
	}

	private void verifyInstructorExists(String instructorId)
			throws EntityDoesNotExistException {
		if (!accountsLogic.isInstructor(instructorId)) {
			throw new EntityDoesNotExistException("Instructor does not exist :"
					+ instructorId);
		}
	}

	/**
	 * Returns how many students have submitted at least one submission.
	 */
	private int countSubmittedStudents(Collection<SubmissionAttributes> submissions) {
		int count = 0;
		List<String> emailsOfSubmittedStudents = new ArrayList<String>();
		for (SubmissionAttributes s : submissions) {
			if (s.points != Common.POINTS_NOT_SUBMITTED
					&& !emailsOfSubmittedStudents.contains(s.reviewer)) {
				count++;
				emailsOfSubmittedStudents.add(s.reviewer);
			}
		}
		return count;
	}

	private List<MimeMessage> sendEvaluationPublishedEmails(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		List<MimeMessage> emailsSent;

		CourseAttributes c = getCourse(courseId);
		EvaluationAttributes e = getEvaluation(courseId, evaluationName);
		List<StudentAttributes> students = getStudentListForCourse(courseId);

		Emails emailMgr = new Emails();
		try {
			emailsSent = emailMgr.generateEvaluationPublishedEmails(c, e,
					students);
			emailMgr.sendEmails(emailsSent);
		} catch (Exception ex) {
			throw new RuntimeException(
					"Unexpected error while sending emails ", ex);
		}
		return emailsSent;
	}

	private void editSubmission(SubmissionAttributes submission)
			throws EntityDoesNotExistException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, submission);

		SubmissionAttributes original = evaluationsLogic.getSubmission(submission.course, submission.evaluation,submission.reviewee, submission.reviewer);

		if (original == null) {
			throw new EntityDoesNotExistException("The submission: "
					+ submission.course + ", " + submission.evaluation + ", "
					+ submission.reviewee + ", " + submission.reviewer + ", "
					+ " does not exist");
		}

		gateKeeper.verifySubmissionEditableForUser(submission);

		evaluationsLogic.updateSubmission(submission);
	}

	private StudentAttributes enrollStudent(StudentAttributes student) {
		StudentAttributes.UpdateStatus updateStatus = UpdateStatus.UNMODIFIED;
		try {
			if (isSameAsExistingStudent(student)) {
				updateStatus = UpdateStatus.UNMODIFIED;
			} else if (isModificationToExistingStudent(student)) {
				editStudent(student.email, student);
				updateStatus = UpdateStatus.MODIFIED;
			} else {
				createStudent(student);
				updateStatus = UpdateStatus.NEW;
			}
		} catch (Exception e) {
			updateStatus = UpdateStatus.ERROR;
			log.severe("Exception thrown unexpectedly" + "\n"
					+ Common.stackTraceToString(e));
		}
		student.updateStatus = updateStatus;
		return student;
	}

	/**
	 * Returns true if either of the three objects is null or if the team in
	 * submission is different from those in two students.
	 */
	private boolean isOrphanSubmission(StudentAttributes reviewer,
			StudentAttributes reviewee, SubmissionAttributes submission) {
		if ((reviewer == null) || (reviewee == null) || (submission == null)) {
			return true;
		}
		if (!submission.team.equals(reviewer.team)) {
			return true;
		}
		if (!submission.team.equals(reviewee.team)) {
			return true;
		}
		return false;
	}

	/**
	 * Returns submissions for the evaluation
	 */
	private HashMap<String, SubmissionAttributes> getSubmissionsForEvaluation(
			String courseId, String evaluationName)
			throws EntityDoesNotExistException {
		if (getEvaluation(courseId, evaluationName) == null) {
			throw new EntityDoesNotExistException(
					"There is no evaluation named [" + evaluationName
							+ "] under the course [" + courseId + "]");
		}

		List<SubmissionAttributes> submissionsList = evaluationsLogic.getSubmissionsForEvaluation(courseId, evaluationName);

		HashMap<String, SubmissionAttributes> submissionDataList = new HashMap<String, SubmissionAttributes>();
		for (SubmissionAttributes sd : submissionsList) {
			submissionDataList.put(sd.reviewer + "->" + sd.reviewee, sd);
		}
		return submissionDataList;
	}

	private TeamEvalResult calculateTeamResult(TeamResultBundle teamResultBundle) {

		int teamSize = teamResultBundle.studentResults.size();
		int[][] claimedFromStudents = new int[teamSize][teamSize];
		teamResultBundle.sortByStudentNameAscending();
		
		int i = 0;
		for (StudentResultBundle studentResult: teamResultBundle.studentResults) {
			studentResult.sortOutgoingByStudentNameAscending();
			for (int j = 0; j < teamSize; j++) {
				SubmissionAttributes submissionData = studentResult.outgoing.get(j);
					claimedFromStudents[i][j] = submissionData.points;
			}
			i++;
		}
		return new TeamEvalResult(claimedFromStudents);
	}

	private void populateTeamResult(TeamResultBundle teamResultBundle, TeamEvalResult teamResult) {
		teamResultBundle.sortByStudentNameAscending();
		int teamSize = teamResultBundle.studentResults.size();
		
		int i = 0;
		for (StudentResultBundle studentResult: teamResultBundle.studentResults) {
			
			studentResult.sortIncomingByStudentNameAscending();
			studentResult.sortOutgoingByStudentNameAscending();
			studentResult.summary.claimedFromStudent = teamResult.claimed[i][i];
			studentResult.summary.claimedToInstructor = teamResult.normalizedClaimed[i][i];
			studentResult.summary.perceivedToStudent = teamResult.denormalizedAveragePerceived[i][i];
			studentResult.summary.perceivedToInstructor = teamResult.normalizedAveragePerceived[i];

			// populate incoming and outgoing
			for (int j = 0; j < teamSize; j++) {
				SubmissionAttributes incomingSub = studentResult.incoming.get(j);
				int normalizedIncoming = teamResult.denormalizedAveragePerceived[i][j];
				incomingSub.normalizedToStudent = normalizedIncoming;
				incomingSub.normalizedToInstructor = teamResult.normalizedPeerContributionRatio[j][i];
				log.finer("Setting normalized incoming of " + studentResult.student.name + " from "
						+ incomingSub.reviewerName + " to "
						+ normalizedIncoming);

				SubmissionAttributes outgoingSub = studentResult.outgoing.get(j);
				int normalizedOutgoing = teamResult.normalizedClaimed[i][j];
				outgoingSub.normalizedToStudent = Common.UNINITIALIZED_INT;
				outgoingSub.normalizedToInstructor = normalizedOutgoing;
				log.finer("Setting normalized outgoing of " + studentResult.student.name + " to "
						+ outgoingSub.revieweeName + " to "
						+ normalizedOutgoing);
			}
			i++;
		}
		
	}

	//TODO: unit test this
	private void populateSubmissionsAndNames(
			HashMap<String, SubmissionAttributes> submissions, 
			TeamResultBundle teamResultBundle,
			StudentResultBundle studentResultBundle) {
		
		for (StudentResultBundle peerResult : teamResultBundle.studentResults) {
			
			StudentAttributes peer = peerResult.student;

			// get incoming submission from peer
			String key = peer.email + "->" + studentResultBundle.student.email;
			SubmissionAttributes submissionFromPeer = submissions.get(key);
			// this workaround is to cater for missing submissions in
			// legacy data.
			if (submissionFromPeer == null) {
				log.warning("Cannot find submission for" + key);
				submissionFromPeer = createEmptySubmission(peer.email,
						studentResultBundle.student.email);
			} else {
				// use a copy to prevent accidental overwriting of data
				submissionFromPeer = submissionFromPeer.getCopy();
			}

			// set names in incoming submission
			submissionFromPeer.revieweeName = studentResultBundle.student.name;
			submissionFromPeer.reviewerName = peer.name;

			// add incoming submission
			studentResultBundle.incoming.add(submissionFromPeer);

			// get outgoing submission to peer
			key = studentResultBundle.student.email + "->" + peer.email;
			SubmissionAttributes submissionToPeer = submissions.get(key);

			// this workaround is to cater for missing submissions in
			// legacy data.
			if (submissionToPeer == null) {
				log.warning("Cannot find submission for" + key);
				submissionToPeer = createEmptySubmission(studentResultBundle.student.email,
						peer.email);
			} else {
				// use a copy to prevent accidental overwriting of data
				submissionToPeer = submissionToPeer.getCopy();
			}

			// set names in outgoing submission
			submissionToPeer.reviewerName = studentResultBundle.student.name;
			submissionToPeer.revieweeName = peer.name;

			// add outgoing submission
			studentResultBundle.outgoing.add(submissionToPeer);

		}
	}

	private SubmissionAttributes createEmptySubmission(String reviewer,
			String reviewee) {
		SubmissionAttributes s;
		s = new SubmissionAttributes();
		s.reviewer = reviewer;
		s.reviewee = reviewee;
		s.points = Common.UNINITIALIZED_INT;
		s.justification = new Text("");
		s.p2pFeedback = new Text("");
		s.course = "";
		s.evaluation = "";
		return s;
	}

	protected SubmissionAttributes getSubmission(String courseId,
			String evaluationName, String reviewerEmail, String revieweeEmail) {
		SubmissionAttributes sd = evaluationsLogic.getSubmission(courseId, evaluationName, revieweeEmail, reviewerEmail);
		return sd;
	}

	private boolean isInEnrollList(StudentAttributes student,
			ArrayList<StudentAttributes> studentInfoList) {
		for (StudentAttributes studentInfo : studentInfoList) {
			if (studentInfo.email.equalsIgnoreCase(student.email))
				return true;
		}
		return false;
	}

	private boolean isSameAsExistingStudent(StudentAttributes student) {
		StudentAttributes existingStudent = getStudent(student.course, student.email);
		if (existingStudent == null)
			return false;
		return student.isEnrollInfoSameAs(existingStudent);
	}

	private boolean isModificationToExistingStudent(StudentAttributes student) {
		return accountsLogic.isStudentInCourse(student.course, student.email);
	}

	/**
	 * This method sends run-time error message to system support email
	 * 
	 * @param req
	 *            httpRequest that triggers the error
	 * @param error
	 *            the error object
	 */
	public MimeMessage emailErrorReport(String path, String params,
			Throwable error) {
		Emails emailMgr = new Emails();
		MimeMessage email = null;
		try {
			email = emailMgr.generateSystemErrorEmail(error, path, params,
					BuildProperties.getAppVersion());
			emailMgr.sendEmail(email);
			log.severe("Sent crash report: " + Emails.getEmailInfo(email));
		} catch (Exception e) {
			log.severe("Error in sending crash report: "
					+ (email == null ? "" : email.toString()));
		}

		return email;
	}

	/**
	 * Helper method for updateCourseInstructors
	 * Parses instructor lines and returns a List of InstructorData generated from parsed lines
	 * 
	 * @param courseId
	 * @param instructorLines
	 * @return
	 * @throws InvalidParametersException
	 */
	private List<InstructorAttributes> parseInstructorLines(String courseId, String instructorLines) 
			throws InvalidParametersException {
		String[] linesArray = instructorLines.split(Common.EOL);
		
		// check if all non-empty lines are formatted correctly
		List<InstructorAttributes> instructorsList = new ArrayList<InstructorAttributes>();
		for (int i = 0; i < linesArray.length; i++) {
			String information = linesArray[i];
			if (Common.isWhiteSpace(information)) {
				continue;
			}
			instructorsList.add(new InstructorAttributes(courseId, information));
		}
		
		if (instructorsList.size() < 1) {
			throw new InvalidParametersException(ERROR_NO_INSTRUCTOR_LINES);
		}
		
		return instructorsList;
	}

	/**
	 * Generates an CSV String to be appended to a response for download
	 * 
	 * @param courseID
	 * @param evalName
	 * @return
	 * @throws EntityDoesNotExistException
	 */
	public String getEvaluationExport(String courseId, String evalName) 
			throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evalName);
		
		gateKeeper.verifyCourseOwnerOrAbove(courseId);
		
		EvaluationResultsBundle evaluationResults = getEvaluationResult(courseId, evalName);
		
		String export = "";
		
		export += "Course" + ",," + evaluationResults.evaluation.course + Common.EOL
				+ "Evaluation Name" + ",," + evaluationResults.evaluation.name + Common.EOL
				+ Common.EOL;
		
		export += "Team" + ",," + "Student" + ",," + "Claimed" + ",," + "Perceived" + ",," + "Received" + Common.EOL;
		
		for (TeamResultBundle td : evaluationResults.teamResults.values()) {
			for (StudentResultBundle srb : td.studentResults) {
				String result = "";
				Collections.sort(srb.incoming, new Comparator<SubmissionAttributes>(){
					@Override
					public int compare(SubmissionAttributes s1, SubmissionAttributes s2){
							return Integer.valueOf(s2.normalizedToInstructor).compareTo(s1.normalizedToInstructor);
					}
				});
				for(SubmissionAttributes sub: srb.incoming){
					if(sub.reviewee.equals(sub.reviewer)) continue;
					if(result!="") result+=",";
					result += sub.normalizedToInstructor;
				}
				
				export += srb.student.team + ",," + srb.student.name + ",," + srb.summary.claimedToInstructor + ",," + srb.summary.perceivedToInstructor + ",," + result + Common.EOL;
			}
		}
		
		// Replace all Unset values
		export = export.replaceAll(Integer.toString(Common.UNINITIALIZED_INT), "N/A");
		export = export.replaceAll(Integer.toString(Common.POINTS_NOT_SURE), "Not Sure");
		export = export.replaceAll(Integer.toString(Common.POINTS_NOT_SUBMITTED), "Not Submitted");
		
		return export;
	}
	
	

	
}
