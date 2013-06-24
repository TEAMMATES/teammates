package teammates.logic;

import java.util.List;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.datatransfer.UserType;
import teammates.common.datatransfer.EvaluationAttributes.EvalStatus;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.StudentsDb;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class GateKeeper {
	private static UserService userService = UserServiceFactory.getUserService();
	
	private static CoursesDb coursesDb = new CoursesDb();
	private static final StudentsDb studentsDb = new StudentsDb();
	private static EvaluationsDb evaluationsDb = new EvaluationsDb();

	private static GateKeeper instance = null;
	public static GateKeeper inst() {
		if (instance == null)
			instance = new GateKeeper();
		return instance;
	}
	
	@SuppressWarnings("unused")
	private void ____USER_related_methods________________________________() {
	}
	
	public boolean isUserLoggedOn() {
		return userService.getCurrentUser() != null;
	}
	
	public UserType getCurrentUser() {
		User user = getCurrentGoogleUser();
		if (user == null) {
			return null;
		}

		//TODO: instead of just taking nickname, we can keep the whole object.
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

	public String getLoginUrl(String redirectPage) {
		
		User user = userService.getCurrentUser();

		if (user != null) {
			return redirectPage;
		} else {
			return userService.createLoginURL(redirectPage);
		}
	}

	public String getLogoutUrl(String redirectPage) {
		return userService.createLogoutURL(redirectPage);
	}
	
	@SuppressWarnings("unused")
	private void ____ACCESS_control_methods________________________________() {
	}

	// @formatter:off
	public void verifyInstructorUsingOwnIdOrAbove(String instructorId) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isOwnId(instructorId) && isInstructor(instructorId))
			return;
		throw new UnauthorizedAccessException(
				"NOT  InstructorUsingOwnIdOrAbove: "+ instructorId);
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

	public void verifyCourseInstructorOrAbove(String courseId) {
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
		if (isUserLoggedOn())
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

	public void verifyStudentOfCourse(String googleId, String courseId) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if(isStudentOfCourse(courseId))
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
				&& isEvaluationOpen(submission.course, submission.evaluation))
			return;
		throw new UnauthorizedAccessException();
	}

	public void verifySubmissionsEditableForUser(List<SubmissionAttributes> submissions) {
		if (isInternalCall())
			return;
		for(SubmissionAttributes s: submissions){
			if (isAdministrator())
				return;
			if (isInstructorOfCourse(s.course))
				return;
			if (isOwnEmail(s.course, s.reviewer)
					&& isEvaluationOpen(s.course, s.evaluation)) 
				return;
			throw new UnauthorizedAccessException();
		}
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
				&& isEvaluationInState(courseId, evaluationName, EvalStatus.PUBLISHED)) 
			return;
		throw new UnauthorizedAccessException();
	}
	
	public void verifyEmailOwnerAndEvalInState(String courseId, String evaluationName, String studentEmail, EvalStatus expectedStatus) {
		if (isInternalCall())
			return;
		if (isAdministrator())
			return;
		if (isOwnEmail(courseId, studentEmail)
				&& isEvaluationInState(courseId, evaluationName, expectedStatus)) 
			return;
		throw new UnauthorizedAccessException();
	}

	// @formatter:on
	
	@SuppressWarnings("unused")
	private void ____PRIVATE_methods________________________________() {
	}

	private User getCurrentGoogleUser() {		
		return userService.getCurrentUser();
	}


	private boolean isEvaluationOpen(String course, String evaluation) {
		EvaluationAttributes e = evaluationsDb.getEvaluation(course, evaluation);
		return (e != null) && (e.getStatus() == EvalStatus.OPEN);
	}

	private boolean isEvaluationInState(String courseId, String evaluationName, EvalStatus expectedStatus) {
		EvaluationAttributes evaluation = evaluationsDb.getEvaluation(courseId, evaluationName);
		return evaluation != null && evaluation.getStatus() == expectedStatus;
	}

	// @formatter:on
	
	private boolean isInternalCall() {
		String callerClassName = Thread.currentThread().getStackTrace()[4]
				.getClassName();
		return callerClassName.equals("teammates.logic.api.Logic") ||
				callerClassName.equals("teammates.logic.backdoor.BackDoorLogic") ||
				callerClassName.equals("teammates.logic.backdoor.BackDoorServlet");
	}

	private boolean isOwnEmail(String courseId, String studentEmail) {
		UserType user = getCurrentUser();
		if (user == null) {
			return false;
		}
		CourseAttributes course = coursesDb.getCourse(courseId);
		if (course == null) {
			return false;
		}
		StudentAttributes student = studentsDb.getStudentForEmail(courseId, studentEmail);
		return student == null ? false : user.id.equals(student.googleId);
	}

	private boolean isOwnId(String userId) {
		UserType loggedInUser = getCurrentUser();
		return loggedInUser == null ? false : loggedInUser.id.equalsIgnoreCase(userId);
	}
	
	//===========================================================================
	private boolean isAdministrator() {
		return isUserLoggedOn() && userService.isUserAdmin();
	}

	//===========================================================================
	private boolean isInstructor() {
		User user = userService.getCurrentUser();
		return isUserLoggedOn() &&  AccountsLogic.inst().isAccountAnInstructor(user.getNickname());
	}
	
	private boolean isInstructor(String googleId) {
		return AccountsLogic.inst().isAccountAnInstructor(googleId);
	}
	
	private boolean isInstructorOfCourse(String courseId) {
		User user = userService.getCurrentUser();
		return isUserLoggedOn() && InstructorsLogic.inst().isInstructorOfCourse(user.getNickname(), courseId);
	}

	//===========================================================================
	private boolean isStudent() {
		User user = userService.getCurrentUser();
		return isUserLoggedOn() && studentsDb.getStudentsForGoogleId(user.getNickname()).size()!=0;
	}
	
	private boolean isStudentOfCourse(String courseId) {
		User user = userService.getCurrentUser();
		return isUserLoggedOn() && studentsDb.getStudentForGoogleId(courseId, user.getNickname()) != null;
	}
	

}
