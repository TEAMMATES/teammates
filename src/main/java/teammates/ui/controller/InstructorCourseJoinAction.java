package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

/**
 * This action handles instructors that attempts to join a course.
 * It asks the instructor for confirmation that the logged in account 
 * belongs to him before redirecting him to the actual join action, 
 * {@link InstructorCourseJoinAuthenticatedAction}.
 * <br/><br/>
 * This is done to prevent instructor from accidentally linking 
 * his registration key with another instructor's google account.
 */
public class InstructorCourseJoinAction extends Action {
	
	private InstructorCourseJoinConfirmationPageData data;
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		
		String key = getRequestParamValue(Const.ParamsNames.REGKEY);
		Assumption.assertNotNull(key);

		new GateKeeper().verifyLoggedInUserPrivileges();
		
		statusToAdmin = "Action Instructor Clicked Join Link"
				+ "<br/>Google ID: " + account.googleId
				+ "<br/>Key: " + key;
		
		InstructorAttributes instructor = logic.getInstructorForRegistrationKey(key);
		if (instructor != null && instructor.isRegistered()) {
			// Bypass confirmation if instructor is already registered
			String redirectUrl = Url.addParamToUrl(
					Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED,
					Const.ParamsNames.REGKEY, key);
			
			return createRedirectResult(redirectUrl);
		} 
		
		data = new InstructorCourseJoinConfirmationPageData(account);
		data.regkey = key;
		
		return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_JOIN_CONFIRMATION, data);
	}
}
