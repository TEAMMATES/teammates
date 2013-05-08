package teammates.logic;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EvaluationsDb;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GateKeeper {
	private static GateKeeper instance = null;
	private static UserService userService = UserServiceFactory.getUserService();
	
	private static AccountsDb accountsDb = new AccountsDb();
	private static CoursesDb coursesDb = new CoursesDb();
	private static EvaluationsDb evaluationsDb = new EvaluationsDb();

	/**
	 * Retrieve singleton instance of GateKeeper
	 * 
	 * @return GateKeeper
	 */
	public static GateKeeper inst() {
		if (instance == null)
			instance = new GateKeeper();
		return instance;
	}
	
	//===========================================================================
	/**
	 * See if the current user is authenticated
	 * 
	 * @return
	 */
	public boolean isLoggedOn() {
		return userService.getCurrentUser() != null;
	}
	
	//===========================================================================
	/**
	 * Returns the Google User object.
	 * 
	 * @return the user
	 */
	public User getUser() {		
		return userService.getCurrentUser();
	}

	//===========================================================================
	/**
	 * Returns the Teammates User object (Google User + teammates privilege)
	 * @return
	 */
	public UserType getLoggedInUser() {
		User user = getUser();
		if (user == null) {
			return null;
		}

		UserType userType = new UserType(user.getNickname());

		if (isAdministrator()) {
			userType.isAdmin = true;
		}
		if (isInstructor()) {
			userType.isInstructor = true;
		}

		if (isStudent()) {
			userType.isStudent = true;
		}
		
		return userType;
	}

	//===========================================================================
	/**
	 * Returns the login page to the user, or the designated redirect page if
	 * the user is already logged in.
	 * 
	 * @param redirectPage
	 *            the page to redirect the user (Precondition: Must not be null)
	 * 
	 * @return the login page or redirect page if the user is already logged in
	 */
	public String getLoginUrl(String redirectPage) {
		// Check if user is already logged in
		User user = userService.getCurrentUser();

		if (user != null) {
			// Direct user to his chosen page
			return redirectPage;
		}

		else {
			// Direct user to Google Login first before chosen page
			return userService.createLoginURL(redirectPage);
		}
	}

	//===========================================================================
	/**
	 * Returns the logout page to the user.
	 * 
	 * @param redirectPage
	 *            the page to redirect the user (Precondition: Must not be null)
	 * 
	 * @return the redirect page after logout
	 */
	public String getLogoutUrl(String redirectPage) {
		return userService.createLogoutURL(redirectPage);
	}
	
	//===========================================================================
	@SuppressWarnings("unused")
	private void ____ACCESS_control_methods________________________________() {
	}

	// @formatter:off
	public void verifyInstructorUsingOwnIdOrAbove(String instructorId) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isOwnId(instructorId))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifyOwnerOfId(String googleId) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isOwnId(googleId))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifyRegisteredUserOrAbove() {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isInstructor())
			return;
		if (isStudent())
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifyCourseOwnerOrAbove(String courseId) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isInstructorOfCourse(courseId))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifyCourseOwnerOrStudentInCourse(String courseId) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isInstructorOfCourse(courseId))
			return;
		if (isStudentOfCourse(courseId))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifyAdminLoggedIn() {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifyLoggedInUserAndAbove() {
		if (isInternalCall())
			return;
		if (isLoggedOn())
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifySameStudentOrAdmin(String googleId) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isOwnId(googleId))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifySameStudentOrCourseOwnerOrAdmin(String courseId,
			String googleId) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isOwnId(googleId))
			return;
		if (isInstructorOfCourse(courseId))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifyReviewerOrCourseOwnerOrAdmin(String courseId,
			String reviewerEmail) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isInstructorOfCourse(courseId))
			return;
		if (isOwnEmail(courseId, reviewerEmail))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifySubmissionEditableForUser(SubmissionAttributes submission) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isInstructorOfCourse(submission.course))
			return;
		if (isOwnEmail(submission.course, submission.reviewer)
				&& evaluationsDb.isEvaluationOpen(submission.course, submission.evaluation))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verfyCourseOwner_OR_EmailOwnerAndPublished(String courseId,
			String evaluationName, String studentEmail) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isInstructorOfCourse(courseId))
			return;
		if (isOwnEmail(courseId, studentEmail)
				&& evaluationsDb.isEvaluationPublished(courseId, evaluationName)) 
			return;
		throw new UnauthorizedAccessException();
	}

	// @formatter:on
	//==========================================================================
	private boolean isInternalCall() {
		String callerClassName = Thread.currentThread().getStackTrace()[4]
				.getClassName();
		return callerClassName.equals("teammates.logic.api.Logic") ||
				callerClassName.equals("teammates.logic.backdoor.BackDoorLogic") ||
				callerClassName.equals("teammates.logic.backdoor.BackDoorServlet");
	}

	private boolean isOwnEmail(String courseId, String studentEmail) {
		UserType user = getLoggedInUser();
		if (user == null) {
			return false;
		}
		CourseAttributes course = coursesDb.getCourse(courseId);
		if (course == null) {
			return false;
		}
		StudentAttributes student = accountsDb.getStudent(courseId, studentEmail);
		return student == null ? false : user.id.equals(student.id);
	}

	private boolean isOwnId(String userId) {
		UserType loggedInUser = getLoggedInUser();
		return loggedInUser == null ? false : loggedInUser.id.equalsIgnoreCase(userId);
	}
	
	//===========================================================================
	private boolean isAdministrator() {
		return isLoggedOn() && userService.isUserAdmin();
	}

	//===========================================================================
	private boolean isInstructor() {
		User user = userService.getCurrentUser();
		return isLoggedOn() && accountsDb.isInstructor(user.getNickname());
	}
	
	private boolean isInstructorOfCourse(String courseId) {
		User user = userService.getCurrentUser();
		return isLoggedOn() && accountsDb.isInstructorOfCourse(user.getNickname(), courseId);
	}

	//===========================================================================
	private boolean isStudent() {
		User user = userService.getCurrentUser();
		return isLoggedOn() && accountsDb.getStudentsWithGoogleId(user.getNickname()).size()!=0;
	}
	
	private boolean isStudentOfCourse(String courseId) {
		User user = userService.getCurrentUser();
		return isLoggedOn() && accountsDb.isStudentOfCourse(user.getNickname(), courseId);
	}
	

}
