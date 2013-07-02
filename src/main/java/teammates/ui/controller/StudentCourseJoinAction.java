package teammates.ui.controller;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

public class StudentCourseJoinAction extends Action {
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String key = getRequestParam(Config.PARAM_REGKEY);
		Assumption.assertNotNull(key);

		new GateKeeper().verifyLoggedInUserPrivileges();
		try {
			logic.joinCourse(account.googleId, key);
		} catch (JoinCourseException 
				| InvalidParametersException
				| EntityAlreadyExistsException e) {
			isError = true;
			statusToUser.add(PageData.escapeForHTML(e.getMessage()));
			statusToAdmin = Config.LOG_SERVLET_ACTION_FAILURE + " : " + e.getMessage();
		}

		RedirectResult response = createRedirectResult(Config.PAGE_STUDENT_HOME);
		return response;
	}

}
