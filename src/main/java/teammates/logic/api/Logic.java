package teammates.logic.api;

import java.util.ArrayList;
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
import teammates.logic.StudentsLogic;
import teammates.logic.SubmissionsLogic;
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
	protected static StudentsLogic studentsLogic = StudentsLogic.inst();
	protected static CoursesLogic coursesLogic = CoursesLogic.inst();
	protected static EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
	protected static SubmissionsLogic submissionsLogic = SubmissionsLogic.inst();

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
	
	/**
	 * @return true if this user has instructor privileges.
	 */
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
	 * Access: Admin only <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
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
	 * Access: any logged in user <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public AccountAttributes getAccount(String googleId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		
		gateKeeper.verifyLoggedInUserAndAbove();
		
		return accountsLogic.getAccount(googleId);
	}
	
	/**
	 * Access: Admin only.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return Details of accounts with instruction privileges. Returns empty
	 *         list if no such accounts are found.
	 */
	public List<AccountAttributes> getInstructorAccounts() {
		
		gateKeeper.verifyAdminLoggedIn();
		
		return accountsLogic.getInstructorAccounts();
	}
	
	/**
	 * Access: Admin only. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.<br>
	 * * {@code newAccountAttributes} represents an existing account.
	 */
	public void updateAccount(AccountAttributes newAccountAttributes) throws InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, newAccountAttributes);
		
		gateKeeper.verifyAdminLoggedIn();
		
		accountsLogic.updateAccount(newAccountAttributes);
	}
	
	/**
	 * Fails silently if no such account. <br>
	 * Access: Admin only. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
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
	 * Creates an account and an instructor. <br>
	 * Access: admin only. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
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
	 * Access: any logged in user. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * @return null if not found.
	 */
	public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyLoggedInUserAndAbove();
		
		return accountsLogic.getInstructorForGoogleId(courseId, googleId);
	}
	
	/**
	 * Access: admin only
	 * @deprecated Not scalable. Don't use unless in admin features.
	 */
	@Deprecated
	public List<InstructorAttributes> getAllInstructors() {
		
		gateKeeper.verifyAdminLoggedIn();
		
		return accountsLogic.getAllInstructors();
	}
	
	/**
	 * Access: admin, instructor using own google id. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Empty list if none found.
	 */
	public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		
		gateKeeper.verifyInstructorUsingOwnIdOrAbove(googleId);
		
		return accountsLogic.getInstructorsForGoogleId(googleId);
	}

	/**
	 * Access: admin, course owner, student in course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Empty list if none found.
	 */
	public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		
		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);
		
		return accountsLogic.getInstructorsForCourse(courseId);
	}

	/**
	 * Access: admin, instructor using own google id. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 */
	public void updateInstructor(InstructorAttributes instructor) 
			throws InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructor);
		
		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructor.googleId);
		
		accountsLogic.updateInstructor(instructor);
	}

	/**
	 * Fails silently if no match found.
	 * Access: admin. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 */
	public void deleteInstructor(String courseId, String googleId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyAdminLoggedIn();
		
		accountsLogic.deleteInstructor(courseId, googleId);
	}

	/**
	 * Fails silently if no match found.
	 * Access: admin. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 */
	public void deleteInstructorsForGoogleId(String googleId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		
		gateKeeper.verifyAdminLoggedIn();
		
		accountsLogic.deleteInstructorsForGoogleId(googleId);
	}

	/**
	 * Access: admin, instructor using own google id. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return A less deatailed version of courses for this instructor. 
	 *   Returns an empty list if none found.
	 */
	public HashMap<String, CourseDetailsBundle> getCourseSummariesForInstructor(String googleId) 
			throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(googleId);

		return coursesLogic.getCourseSummariesForInstructor(googleId);
	}


	/**
	 * Access: admin, instructor using own google id. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return A more deatailed version of courses for this instructor. 
	 *   Returns an empty list if none found.
	 */
	public HashMap<String, CourseDetailsBundle> getCourseDetailsListForInstructor(
			String instructorId) throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorId);

		HashMap<String, CourseDetailsBundle> courseList = coursesLogic.getCourseSummariesForInstructor(instructorId);
		ArrayList<EvaluationDetailsBundle> evaluationList = getEvaluationsDetailsForInstructor(instructorId);
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
	public ArrayList<EvaluationDetailsBundle> getEvaluationsDetailsForInstructor(
			String instructorId) throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorId);

		verifyInstructorExists(instructorId);

		ArrayList<EvaluationDetailsBundle> evaluationSummaryList = new ArrayList<EvaluationDetailsBundle>();

		List<InstructorAttributes> instructorList = accountsLogic.getInstructorsForGoogleId(instructorId);
		for (InstructorAttributes id : instructorList) {
			evaluationSummaryList.addAll(getEvaluationDetailsForCourse(id.courseId));
		}
		return evaluationSummaryList;
	}

	/**
	 * Access level: Admin, Instructor (for self), Student(in
	 * getCourseDetails(..))
	 * 
	 * @return Returns a less-detailed version of Instructor's evaluations <br>
	 */
	public ArrayList<EvaluationDetailsBundle> getEvaluationDetailsForCourse(String courseId)
			throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);

		ArrayList<EvaluationDetailsBundle> evaluationSummaryList = new ArrayList<EvaluationDetailsBundle>();

		List<EvaluationAttributes> evaluationsSummaryForCourse = evaluationsLogic.getEvaluationsForCourse(courseId);
		List<StudentAttributes> students = studentsLogic.getStudentsForCourse(courseId);

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
	 * Creates a course and an instructor for it. <br>
	 * Access: admin, instructor. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 * * {@code instructorGoogleId} already has instructor privileges.
	 */
	public void createCourseAndInstructor(String instructorGoogleId, String courseId, String courseName) 
			throws EntityAlreadyExistsException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorGoogleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseName);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorGoogleId);

		coursesLogic.createCourseAndInstructor(instructorGoogleId, courseId, courseName);
	}
	
	/**
	 * Access: any registered in user (because it is too expensive to check
-	 * if a student is in the course). <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * @return null if not found.
	 */
	public CourseAttributes getCourse(String courseId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		
		gateKeeper.verifyRegisteredUserOrAbove();
		
		return coursesLogic.getCourse(courseId);
	}

	/**
	 * Returns a detailed version of course data, including evaluation data. <br>
	 * Access: admin, instructors of the course, students in the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public CourseDetailsBundle getCourseDetails(String courseId)
			throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);

		CourseDetailsBundle courseSummary = coursesLogic.getCourseSummary(courseId);

		ArrayList<EvaluationDetailsBundle> evaluationList = getEvaluationDetailsForCourse(courseSummary.course.id);
		for (EvaluationDetailsBundle edd : evaluationList) {
			courseSummary.evaluations.add(edd);
		}

		return courseSummary;
	}

	/**
	 * Access: admin, instructors of the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * @return Empty list if none found.
	 */
	public List<StudentAttributes> getStudentsForCourse(String courseId)
			throws EntityDoesNotExistException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
	
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		return studentsLogic.getStudentsForCourse(courseId);
	
	}

	/**
	 * Access: admin, instructors of the course, students in the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public CourseDetailsBundle getTeamsForCourse(String courseId)
			throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
	
		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);
	
		return coursesLogic.getTeamsForCourse(courseId);
		
	}

	public void updateCourse(CourseAttributes course) throws NotImplementedException {
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

		if (!coursesLogic.isCoursePresent(courseId)) {
			Assumption.fail(ERROR_UPDATE_NON_EXISTENT_COURSE + courseId);
		}
		
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
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
	
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
	
		if (!coursesLogic.isCoursePresent(courseId)) {
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
		List<StudentAttributes> studentsInCourse = getStudentsForCourse(courseId);
		for (StudentAttributes student : studentsInCourse) {
			if (!isInEnrollList(student, returnList)) {
				student.updateStatus = StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST;
				returnList.add(student);
			}
		}
	
		return returnList;
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
	
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
	
		List<StudentAttributes> studentDataList = studentsLogic.getUnregisteredStudentsForCourse(courseId);
	
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
	 * Deletes the course and all data related to the course.
	 * Fails silently if no such account. <br>
	 * Access: admin, instructors of the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public void deleteCourse(String courseId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

		coursesLogic.deleteCourseCascade(courseId);
	}

	@SuppressWarnings("unused")
	private void ____STUDENT_level_methods__________________________________() {
	}

	/**
	 * Creates a student and adjust existing evaluations to accommodate the new student. <br> 
	 * Access: admin, course owner. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	/**
	 * Creates a student and adjust existing evaluations to accommodate the new
	 * student Access: course owner and above
	 */
	public void createStudent(StudentAttributes student)
			throws EntityAlreadyExistsException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);

		gateKeeper.verifyCourseInstructorOrAbove(student.course);

		studentsLogic.createStudent(student);
		submissionsLogic.adjustSubmissionsForNewStudent(student.course, student.email, student.team);
	}

	/**
	 * Access: any registered user (to minimize cost of checking). <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return Null if no match found.
	 */
	public StudentAttributes getStudent(String courseId, String email) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);

		gateKeeper.verifyRegisteredUserOrAbove();

		return studentsLogic.getStudentForEmail(courseId, email);
	}

	
	/**
	 * Access: admin, same student. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return Null if no match found.
	 */
	public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
	
		gateKeeper.verifySameStudentOrCourseOwnerOrAdmin(courseId, googleId);
		
		return studentsLogic.getStudentForGoogleId(courseId, googleId);
	}

	/**
	 * Access: admin, same student. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return Empty list if no match found.
	 */
	public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		
		gateKeeper.verifySameStudentOrAdmin(googleId);
		
		return studentsLogic.getStudentsForGoogleId(googleId);
	}

	
	/**
	 * Access: admin, a student who owns the googleId. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public List<CourseAttributes> getCoursesForStudentAccount(String googleId)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
	
		gateKeeper.verifySameStudentOrAdmin(googleId);
	
		return coursesLogic.getCoursesForStudentAccount(googleId);
	}

	/**
	 * Access: admin, instructor of course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return null if no match found.
	 */
	public String getKeyForStudent(String courseId, String email) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
	
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
	
		return studentsLogic.getKeyForStudent(courseId, email);
	}

	/**
	 * Access: instructor of course and above.<br>
	 * All attributes except courseId be changed. Trying to change courseId will
	 * be treated as trying to edit a student in a different course.<br>
	 * Changing team name will not delete existing submissions under that team <br>
	 * Cascade logic: Email changed-> changes email in all existing submissions <br>
	 * Team changed-> creates new submissions for the new team, deletes
	 * submissions for previous team structure. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public void updateStudent(String originalEmail, StudentAttributes student)
			throws InvalidParametersException, EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, originalEmail);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);

		gateKeeper.verifyCourseInstructorOrAbove(student.course);

		studentsLogic.confirmStudentExists(student.course, originalEmail);

		StudentAttributes originalStudent = studentsLogic.getStudentForEmail(student.course, originalEmail);
		String originalTeam = originalStudent.team;

		studentsLogic.updateStudent(originalEmail, student);

		// cascade email change, if any
		if (!originalEmail.equals(student.email)) {
			evaluationsLogic.updateStudentEmailForSubmissionsInCourse(student.course, originalEmail, student.email);
		}

		// adjust submissions if moving to a different team
		if (isTeamChanged(originalTeam, student.team)) {
			submissionsLogic.adjustSubmissionsForChangingTeam(student.course, student.email, originalTeam, student.team);
		}
	}

	/**
	 * Access: admin, owner of id. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 */
	public void joinCourse(String googleId, String key)
			throws JoinCourseException, InvalidParametersException, EntityAlreadyExistsException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, key);
	
		gateKeeper.verifyOwnerOfId(googleId);
	
		studentsLogic.joinCourse(key, googleId);
	
	}

	/**
	 * Fails silently if no match found. <br>
	 * Access: admin, an instructor of the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public void deleteStudent(String courseId, String studentEmail) {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

		studentsLogic.deleteStudentCascade(courseId, studentEmail);
	}

	/**
	 * Access: admin, an instructor of the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public MimeMessage sendRegistrationInviteToStudent(String courseId,	String studentEmail) 
			throws EntityDoesNotExistException,	InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		return coursesLogic.sendRegistrationInviteToStudent(courseId, studentEmail);
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
		List<CourseAttributes> courseList = getCoursesForStudentAccount(googleId);
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
	 * Access: admin, an instructor of the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public void createEvaluation(EvaluationAttributes evaluation)
			throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluation);

		gateKeeper.verifyCourseInstructorOrAbove(evaluation.course);

		evaluationsLogic.createEvaluationCascade(evaluation);
	}

	/**
	 * Access: all registered users. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public EvaluationAttributes getEvaluation(String courseId, String evaluationName) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyRegisteredUserOrAbove();

		return evaluationsLogic.getEvaluation(courseId, evaluationName);
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

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

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

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

		evaluationsLogic.deleteEvaluationCascade(courseId, evaluationName);
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

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

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

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

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

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

		if (!evaluationsLogic.isEvaluationExists(courseId, evaluationName)) {
			throw new EntityDoesNotExistException("Trying to edit non-existent evaluation " + courseId + "/" + evaluationName);
		}

		EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);

		// Filter out students who have submitted the evaluation
		List<StudentAttributes> studentDataList = studentsLogic.getStudentsForCourse(courseId);

		List<StudentAttributes> studentsToRemindList = new ArrayList<StudentAttributes>();
		for (StudentAttributes sd : studentDataList) {
			if (!evaluationsLogic.isEvaluationCompletedByStudent(evaluation,
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

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

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

		List<SubmissionAttributes> submissions = submissionsLogic.getSubmissionsFromEvaluationFromStudent(courseId, evaluationName, reviewerEmail);

		boolean isSubmissionsExist = (submissions.size() > 0
				&& coursesLogic.isCoursePresent(courseId)
				&& evaluationsLogic.isEvaluationExists(courseId,
						evaluationName) && studentsLogic.isStudentInCourse(courseId, reviewerEmail));

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
		List<StudentAttributes> students = getStudentsForCourse(courseId);

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

		SubmissionAttributes original = submissionsLogic.getSubmission(submission.course, submission.evaluation,submission.reviewee, submission.reviewer);

		if (original == null) {
			throw new EntityDoesNotExistException("The submission: "
					+ submission.course + ", " + submission.evaluation + ", "
					+ submission.reviewee + ", " + submission.reviewer + ", "
					+ " does not exist");
		}

		gateKeeper.verifySubmissionEditableForUser(submission);

		submissionsLogic.updateSubmission(submission);
	}

	private StudentAttributes enrollStudent(StudentAttributes student) {
		StudentAttributes.UpdateStatus updateStatus = UpdateStatus.UNMODIFIED;
		try {
			if (isSameAsExistingStudent(student)) {
				updateStatus = UpdateStatus.UNMODIFIED;
			} else if (isModificationToExistingStudent(student)) {
				updateStudent(student.email, student);
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

		List<SubmissionAttributes> submissionsList = submissionsLogic.getSubmissionsForEvaluation(courseId, evaluationName);

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
		SubmissionAttributes sd = submissionsLogic.getSubmission(courseId, evaluationName, revieweeEmail, reviewerEmail);
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
		return studentsLogic.isStudentInCourse(student.course, student.email);
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
		
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
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
