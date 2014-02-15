package teammates.ui.controller;

import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

/**
 * This action handles students that attempts to join a course.
 * It asks the student for confirmation that the logged in account 
 * belongs to him before redirecting him to the actual join action, 
 * {@link StudentCourseJoinAuthenticatedAction}.
 * <br/><br/>
 * This is done to prevent students from accidentally linking 
 * his registration key with another student's google account.
 */
public class StudentCourseJoinAction extends Action {
	
	private StudentCourseJoinConfirmationPageData data;
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		//TODO Remove excessive logging from this method
		String key = getRequestParamValue(Const.ParamsNames.REGKEY);
		Assumption.assertNotNull(key);

		new GateKeeper().verifyLoggedInUserPrivileges();
		
		statusToAdmin = "Action Student Clicked Join Link"
				+ "<br/>Google ID: " + account.googleId
				+ "<br/>Key: " + key;
		
		// Bypass confirmation if student is already registered
		StudentAttributes student = logic.getStudentForRegistrationKey(key);
		if (student != null && student.isRegistered()) {
			String logMsg = "Student already registered with the following information:" 
					+ "<br/>Course: " + student.course
					+ "<br/>Name: " + student.name 
					+ "<br/>Email: " + student.email
					+ "<br/>Id: " + student.googleId
					+ "<br/>Bypassing confirmation page.";			
			log.info(logMsg);
			
			String redirectUrl = Url.addParamToUrl(
					Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED,
					Const.ParamsNames.REGKEY, key);
			
			return createRedirectResult(redirectUrl);
		} 
		
		data = new StudentCourseJoinConfirmationPageData(account);
		data.regkey = key;
		
		String logMsg = "Showing join confirmation page.";
		log.info(logMsg);
		
		return createShowPageResult(
				Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION, data);
	}
}
