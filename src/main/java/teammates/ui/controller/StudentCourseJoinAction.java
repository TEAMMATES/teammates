package teammates.ui.controller;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.GateKeeper;

public class StudentCourseJoinAction extends Action {
	

	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String key = getRequestParam(Const.ParamsNames.REGKEY);
		Assumption.assertNotNull(key);

		new GateKeeper().verifyLoggedInUserPrivileges();
		try {
			logic.joinCourse(account.googleId, key);
		} catch (JoinCourseException 
				| InvalidParametersException
				| EntityAlreadyExistsException e) {
			isError = true;
			statusToUser.add(Sanitizer.sanitizeForHtml(e.getMessage()));
			statusToAdmin = Const.ACTION_RESULT_FAILURE + " : " + e.getMessage();
		}

		RedirectResult response = createRedirectResult(Const.ActionURIs.STUDENT_HOME);
		return response;
	}

}
