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
		String key = getRequestParamValue(Const.ParamsNames.REGKEY);
		Assumption.assertNotNull(key);

		new GateKeeper().verifyLoggedInUserPrivileges();
		
		// Bypass confirmation if student is already registered
		StudentAttributes student = logic.getStudentForRegistrationKey(key);
		if (student != null && student.isRegistered()) {
			String redirectUrl = Url.addParamToUrl(
					Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED,
					Const.ParamsNames.REGKEY, key);
			
			return createRedirectResult(redirectUrl);
		} 
		
		data = new StudentCourseJoinConfirmationPageData(account);
		data.regkey = key;
	
		return createShowPageResult(
				Const.ViewURIs.STUDENT_COURSE_JOIN_CONFIRMATION, data);
	}
}
