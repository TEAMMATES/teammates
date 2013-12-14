package teammates.ui.controller;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseInstructorAddAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
		Assumption.assertNotNull(instructorId);
		String instructorName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
		Assumption.assertNotNull(instructorName);
		String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
		Assumption.assertNotNull(instructorEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));

		String instructorInstitute = account.institute;
		try {
			logic.createInstructorAccount(instructorId, courseId, instructorName, 
									instructorEmail, instructorInstitute);
			
			statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_ADDED);
			statusToAdmin = "New instructor (<span class=\"bold\"> " + instructorId + "</span>)"
					+ " for Course <span class=\"bold\">[" + courseId + "]</span> created.<br>";
		} catch (EntityAlreadyExistsException e) {
			setStatusForException(e, Const.StatusMessages.COURSE_INSTRUCTOR_EXISTS);
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}
		
		RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
		result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
		return result;
	}

}
