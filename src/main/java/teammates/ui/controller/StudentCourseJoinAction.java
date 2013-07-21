package teammates.ui.controller;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.logic.api.GateKeeper;

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
			setStatusForException(e, Sanitizer.sanitizeForHtml(e.getMessage()));
		}

		RedirectResult response = createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
		return response;
	}

}
