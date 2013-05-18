package teammates.logic.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.EvaluationDetailsBundle;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.SubmissionAttributes;
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
import teammates.logic.InstructorsLogic;
import teammates.logic.StudentsLogic;
import teammates.logic.SubmissionsLogic;
//TODO: remove this dependency

/**
 * This class represents the API to the business logic of the system. Please
 * refer to DevMan for general policies followed by Logic. As those policies
 * cover most of the behavior of the API, we use very short comments to describe
 * operations here.
 * Logic class is a Facade class. It simply forwards the method to internal classes.
 */
public class Logic {

	private static Logger log = Common.getLogger();

	public static final String ERROR_NULL_PARAMETER = "The supplied parameter was null\n";
	public static final String ERROR_UPDATE_NON_EXISTENT_COURSE = "Trying to update non-existent Course: ";
	public static final String ERROR_COURSE_CREATOR_NO_ACCOUNT = "An instructor with no ACCOUNT was able to create a course\n";
	
	/** Used for access control checking */
	protected static GateKeeper gateKeeper = GateKeeper.inst();
	
	protected static Emails emailManager = new Emails();
	
	//
	protected static AccountsLogic accountsLogic = AccountsLogic.inst();
	protected static StudentsLogic studentsLogic = StudentsLogic.inst();
	protected static InstructorsLogic instructorsLogic = InstructorsLogic.inst();
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
		return accountsLogic.isAccountAnInstructor(googleId);
	}
	
	public boolean isInstructorOfCourse(String googleId, String courseId) {
		return instructorsLogic.isInstructorOfCourse(googleId, courseId);
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
	public void createInstructorAccount(String googleId, String courseId, String name, String email, String institute)
			throws EntityAlreadyExistsException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, name);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, institute);

		gateKeeper.verifyAdminLoggedIn();
		
		accountsLogic.createInstructorAccount(googleId, courseId, name, email, institute);
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
		
		return instructorsLogic.getInstructorForGoogleId(courseId, googleId);
	}
	
	/**
	 * Access: admin only
	 * @deprecated Not scalable. Don't use unless in admin features.
	 */
	@Deprecated
	public List<InstructorAttributes> getAllInstructors() {
		
		gateKeeper.verifyAdminLoggedIn();
		
		return instructorsLogic.getAllInstructors();
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
		
		return instructorsLogic.getInstructorsForGoogleId(googleId);
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
		
		return instructorsLogic.getInstructorsForCourse(courseId);
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
		
		instructorsLogic.updateInstructor(instructor);
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
		
		instructorsLogic.deleteInstructor(courseId, googleId);
	}

	/**
	 * Fails silently if no match found.
	 * Access: admin. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 */
	public void downgradeInstructorToStudentCascade(String googleId) {

		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		
		gateKeeper.verifyAdminLoggedIn();
		
		
		accountsLogic.downgradeInstructorToStudentCascade(googleId);
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
		
		instructorsLogic.verifyInstructorExists(googleId);

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
		
		instructorsLogic.verifyInstructorExists(instructorId);
		
		return coursesLogic.getCourseDetailsListForInstructor(instructorId);

	}

	/**
	 * Access level: admin, instructors using own id.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 * 
	 * @return Details of Instructor's evaluations. <br>
	 * Returns an empty list if none found.
	 */
	public ArrayList<EvaluationDetailsBundle> getEvaluationsDetailsForInstructor(String instructorId) 
			throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);

		gateKeeper.verifyInstructorUsingOwnIdOrAbove(instructorId);

		instructorsLogic.verifyInstructorExists(instructorId);

		return evaluationsLogic.getEvaluationsDetailsForInstructor(instructorId);
	}

	/**
	 * Access level: admin, instructors of the course, students of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 * 
	 * @return Returns details of the course's evaluations. <br>
	 */
	public ArrayList<EvaluationDetailsBundle> getEvaluationDetailsForCourse(String courseId)
			throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

		gateKeeper.verifyCourseOwnerOrStudentInCourse(courseId);

		return evaluationsLogic.getEvaluationDetailsForCourse(courseId);
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
		
		return coursesLogic.getCourseDetails(courseId);

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
	 * Access: admin, instructors of the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * @throws EntityDoesNotExistException 
	 */
	public void updateCourseInstructors(String courseId, String instructorLines, String courseInstitute) 
			throws InvalidParametersException, EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorLines);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseInstitute);

		coursesLogic.verifyCourseIsPresent(courseId);
		
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		instructorsLogic.updateCourseInstructors(courseId, instructorLines, courseInstitute); 
		
	}

	/**
	 * Enrolls new students in the course or modifies existing students. But it
	 * will not delete any students. It will not edit email address either. If
	 * an existing student was enrolled with a different email address, that
	 * student will be treated as a new student.<br>
	 * If there is an error in the enrollLines, there will be no changes to the
	 * datastore <br>
	 * Access: course owner and above<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return StudentData objects in the return value contains the status of
	 *         enrollment. It also includes data for other students in the
	 *         course that were not touched by the operation.
	 */
	public List<StudentAttributes> enrollStudents(String enrollLines, String courseId)
			throws EnrollException, EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, enrollLines);
	
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		return studentsLogic.enrollStudents(enrollLines, courseId);
	
	}

	/**
	 * Sends the registration invite to unregistered students in the course.
	 * Access: admin, instructors of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return The list of emails sent. These can be used for
	 *         verification.
	 */
	public List<MimeMessage> sendRegistrationInviteForCourse(String courseId)
			throws InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
	
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		return studentsLogic.sendRegistrationInviteForCourse(courseId);
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
	 * Access: admin, instructors of the course. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public void createStudent(StudentAttributes student)
			throws EntityAlreadyExistsException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);

		gateKeeper.verifyCourseInstructorOrAbove(student.course);

		studentsLogic.createStudent(student);
		evaluationsLogic.adjustSubmissionsForNewStudent(student.course, student.email, student.team);
	}

	/**
	 * Access: any registered user (to minimize cost of checking). <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return Null if no match found.
	 */
	public StudentAttributes getStudentForEmail(String courseId, String email) {
		
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
	 * All attributes except courseId be changed. Trying to change courseId will
	 * be treated as trying to edit a student in a different course.<br>
	 * Changing team name will not delete existing submissions under that team <br>
	 * Cascade logic: Email changed-> changes email in all existing submissions <br>
	 * Team changed-> creates new submissions for the new team, deletes
	 * submissions for previous team structure. <br>
	 * Access: instructor of course and above.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public void updateStudent(String originalEmail, StudentAttributes student)
			throws InvalidParametersException, EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, originalEmail);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);

		gateKeeper.verifyCourseInstructorOrAbove(student.course);

		studentsLogic.updateStudent(originalEmail, student);
	}

	/**
	 * Access: admin, owner of id. <br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public void joinCourse(String googleId, String key)
			throws JoinCourseException, InvalidParametersException, EntityAlreadyExistsException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, key);
	
		gateKeeper.verifyOwnerOfId(googleId);
	
		accountsLogic.joinCourse(key, googleId);
	
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
		
		return studentsLogic.sendRegistrationInviteToStudent(courseId, studentEmail);
	}

	
	/**
	 * Access: any logged in user (to minimize cost of checking)<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public boolean hasStudentSubmittedEvaluation(
			String courseId, String evaluationName, String studentEmail)
			throws InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

		gateKeeper.verifyLoggedInUserAndAbove();
		
		return submissionsLogic.hasStudentSubmittedEvaluation(
				courseId, evaluationName, studentEmail);
	}

	/**
	 * Access: student who owns the googleId, admin<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 * @return Details of courses the student is in. CourseData objects
	 *         returned contain details of evaluations too (except the ones
	 *         still AWAITING).
	 */
	public List<CourseDetailsBundle> getCourseDetailsListForStudent(String googleId)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);

		gateKeeper.verifySameStudentOrAdmin(googleId);
		
		return coursesLogic.getCourseDetailsListForStudent(googleId);

	}

	/**
	 * Access: admin, instructors of the course, owner of result (when PUBLISHED).<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public StudentResultBundle getEvaluationResultForStudent(
			String courseId, String evaluationName, String studentEmail)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

		gateKeeper.verfyCourseOwner_OR_EmailOwnerAndPublished(courseId, evaluationName,
				studentEmail);
		
		return evaluationsLogic.getEvaluationResultForStudent(courseId, evaluationName, studentEmail);
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
	 * Can be used to change instructions, p2pEnabled, start/end times, grace period and time zone. <br>
	 * Access: admin, instructors of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 * 
	 */
	public void updateEvaluation(String courseId, String evaluationName,
			String instructions, Date start, Date end, double timeZone,
			int gracePeriod, boolean p2pEnabled)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructions);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, start);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, end);

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

		//these fields cannot be changed this way
		evaluation.activated = original.activated;
		evaluation.published = original.published;

		evaluationsLogic.updateEvaluation(evaluation);
	}

	/**
	 * Access: admin, instructors of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public void deleteEvaluation(String courseId, String evaluationName) {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseInstructorOrAbove(courseId);

		evaluationsLogic.deleteEvaluationCascade(courseId, evaluationName);
	}

	/**
	 * Publishes the evaluation and send email alerts to students.
	 * Access: admin, instructors of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 * @throws InvalidParametersException
	 *             if the evaluation is not ready to be published.
	 * @throws EntityDoesNotExistException
	 */
	public void publishEvaluation(String courseId, String evaluationName)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		evaluationsLogic.publishEvaluation(courseId, evaluationName);
		
	}

	/**
	 * Access: admin, instructors of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 * @throws InvalidParametersException
	 *             if the evaluation is not ready to be unpublished.
	 */
	public void unpublishEvaluation(String courseId, String evaluationName)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		evaluationsLogic.unpublishEvaluation(courseId, evaluationName);
	}

	/**
	 * Sends reminders to students who haven't submitted yet. <br>
	 * Access: admin, instructors of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 */
	public List<MimeMessage> sendReminderForEvaluation(String courseId,
			String evaluationName) throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		return evaluationsLogic.sendReminderForEvaluation(courseId, evaluationName);
	}

	/**
	 * Access: admin, instructors of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 */
	public EvaluationResultsBundle getEvaluationResult(
			String courseId, String evaluationName) 
					throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);

		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		return evaluationsLogic.getEvaluationResult(courseId, evaluationName);

	}

	/**
	 * Generates summary results (without comments) in CSV format. <br>
	 * Access: admin, instructors of the course.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 */
	public String getEvaluationResultSummaryAsCsv(String courseId, String evalName) 
			throws EntityDoesNotExistException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evalName);
		
		gateKeeper.verifyCourseInstructorOrAbove(courseId);
		
		return evaluationsLogic.getEvaluationResultSummaryAsCsv(courseId, evalName);
	}

	/**
	 * Access: admin, instructors of the course, student who submitted the submissions.<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 */
	public List<SubmissionAttributes> getSubmissionsForEvaluationFromStudent(String courseId,
			String evaluationName, String reviewerEmail)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, evaluationName);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, reviewerEmail);

		gateKeeper.verifyReviewerOrCourseOwnerOrAdmin(courseId, reviewerEmail);
		
		return submissionsLogic.getSubmissionsForEvaluationFromStudent(courseId, evaluationName, reviewerEmail);

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
	 * Access: admin, instructors of the course, student who submitted the submissions (if evaluation is OPEN).<br>
	 * Preconditions: <br>
	 * * All parameters are non-null. <br>
	 */
	public void updateSubmissions(List<SubmissionAttributes> submissionsList)
			throws EntityDoesNotExistException, InvalidParametersException {
		
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, submissionsList);

		gateKeeper.verifySubmissionsEditableForUser(submissionsList);
		
		submissionsLogic.updateSubmissions(submissionsList);
	}

	public void deleteSubmission(SubmissionAttributes submission)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Not implemented because submissions "
						+ "are deleted automatically");
	}
	
	@SuppressWarnings("unused")
	private void ____MISC_methods__________________________________________() {
	}

	public MimeMessage emailErrorReport(String path, String params,	Throwable error) {
		return emailManager.sendErrorReport(path, params, error);
	}

	@SuppressWarnings("unused")
	private void ____helper_methods________________________________________() {
	}


	
}
