package teammates.ui.controller;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

/**
 * This action handles students who attempt to join a course after
 * the student has been forced to re-authenticate himself by 
 * {@link StudentCourseJoinAction}. This action does the actual
 * joining of the student to the course.
 */
public class StudentCourseJoinAuthenticatedAction extends Action {
	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {
		String key = getRequestParamValue(Const.ParamsNames.REGKEY);
		Assumption.assertNotNull(key);
		
		new GateKeeper().verifyLoggedInUserPrivileges();
		try {
			logic.joinCourse(account.googleId, key);
		} catch (InvalidParametersException
				| EntityAlreadyExistsException e) {
			setStatusForException(e, Sanitizer.sanitizeForHtml(e.getMessage()));
		} catch (JoinCourseException e) {
			// Does not sanitize for html to allow insertion of mailto link
			setStatusForException(e, e.getMessage());
		}
		
		final String studentInfo = "Action Student joins course<br>" +
				"Student (GoogleID): " + account.googleId + "<br>" +
				"With Key : " + key;
		if (statusToAdmin != null && !StringHelper.isWhiteSpace(statusToAdmin)){
			statusToAdmin += ("<br>" + studentInfo);
		} else {
			statusToAdmin = studentInfo;	
		}
		
		RedirectResult response = createRedirectResult(Const.ActionURIs.STUDENT_HOME_PAGE);
		return response;
	}
}
