package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseInstructorEditSaveAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		String instructorId = getRequestParam(Const.ParamsNames.INSTRUCTOR_ID);
		Assumption.assertNotNull(instructorId);
		String instructorName = getRequestParam(Const.ParamsNames.INSTRUCTOR_NAME);
		Assumption.assertNotNull(instructorName);
		String instructorEmail = getRequestParam(Const.ParamsNames.INSTRUCTOR_EMAIL);
		Assumption.assertNotNull(instructorEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));

		try {
			logic.updateInstructor(courseId, instructorId, instructorName, instructorEmail);
			
			statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_EDITED);
			statusToAdmin = "Instructor <span class=\"bold\"> " + instructorName + "</span>"
					+ " for Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"
					+ "New Name: " + instructorName + "<br>New Email: " + instructorEmail;
		} catch (InvalidParametersException e) {
			setStatusForException(e);
		}
		
		RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
		result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
		return result;
	}

}
