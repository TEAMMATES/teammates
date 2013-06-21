package teammates.ui.controller;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.logic.GateKeeper;

public class StudentCourseJoinAction extends Action {
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String key = getRequestParam(Common.PARAM_REGKEY);
		Assumption.assertNotNull(key);

		new GateKeeper().verifyLoggedInUserAndAbove();
		try {
			logic.joinCourse(account.googleId, key);
		} catch (JoinCourseException 
				| InvalidParametersException
				| EntityAlreadyExistsException e) {
			isError = true;
			statusToUser.add(PageData.escapeForHTML(e.getMessage()));
			statusToAdmin = Common.LOG_SERVLET_ACTION_FAILURE + " : " + e.getMessage();
		}

		RedirectResult response = createRedirectResult(Common.PAGE_STUDENT_HOME);
		return response;
	}

}
