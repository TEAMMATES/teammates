package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.Logic;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * This action handles students that attempts to join a course.
 * It forces th student to log out from his google account and
 * re-authenticate himself before redirecting him to the actual
 * join action, {@link StudentCourseJoinAuthenticatedAction}.
 * <br/><br/>
 * This is done to prevent students from accidentally inking 
 * his registration key with another student's google account.
 */
public class StudentCourseJoinAction extends Action {
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {
		String key = getRequestParamValue(Const.ParamsNames.REGKEY);
		Assumption.assertNotNull(key);

		String joinUrl = Url.addParamToUrl(
				Const.ActionURIs.STUDENT_COURSE_JOIN_AUTHENTICATED,
				Const.ParamsNames.REGKEY, key);
		
		UserService userService = UserServiceFactory.getUserService();
		String loginUrl = userService.createLoginURL(joinUrl);

		// Get users to logout first then login again
		String redirectUrl = Logic.getLogoutUrl(loginUrl);

		RedirectResult response = createRedirectResult(redirectUrl);
		return response;
	}
}
