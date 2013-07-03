package teammates.ui.controller;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class StudentCourseJoinAction extends Action {
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String key = getRequestParam(Constants.PARAM_REGKEY);
		Assumption.assertNotNull(key);

		new GateKeeper().verifyLoggedInUserPrivileges();
		try {
			logic.joinCourse(account.googleId, key);
		} catch (JoinCourseException 
				| InvalidParametersException
				| EntityAlreadyExistsException e) {
			isError = true;
			statusToUser.add(PageData.escapeForHTML(e.getMessage()));
			statusToAdmin = Constants.ACTION_RESULT_FAILURE + " : " + e.getMessage();
		}

		RedirectResult response = createRedirectResult(Constants.ACTION_STUDENT_HOME);
		return response;
	}

}
