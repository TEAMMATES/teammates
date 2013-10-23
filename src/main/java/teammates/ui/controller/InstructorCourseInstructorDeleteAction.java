package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseInstructorDeleteAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		String instructorId = getRequestParam(Const.ParamsNames.INSTRUCTOR_ID);
		Assumption.assertNotNull(instructorId);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));

		logic.deleteInstructor(courseId, instructorId);
		
		statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED);
		statusToAdmin = "Instructor <span class=\"bold\"> " + instructorId + "</span>"
				+ " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
		
		RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
		result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
		return result;
	}

}
