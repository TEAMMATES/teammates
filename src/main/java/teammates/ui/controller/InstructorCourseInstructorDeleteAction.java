package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorCourseInstructorDeleteAction extends Action {

	@Override
	protected ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
		Assumption.assertNotNull(instructorId);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));

		int numberOfInstructorsForCourse = logic.getInstructorsForCourse(courseId).size();
		
		// Only delete if it is not the last instructor in course
		if (numberOfInstructorsForCourse != 1) {
			logic.deleteInstructor(courseId, instructorId);
			
			statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_DELETED);
			statusToAdmin = "Instructor <span class=\"bold\"> " + instructorId + "</span>"
					+ " in Course <span class=\"bold\">[" + courseId + "]</span> deleted.<br>";
		} else {
			isError = true;
			statusToUser.add(Const.StatusMessages.COURSE_INSTRUCTOR_DELETE_NOT_ALLOWED);
			statusToAdmin = "Instructor <span class=\"bold\"> " + instructorId + "</span>"
					+ " in Course <span class=\"bold\">[" + courseId + "]</span> could not be deleted "
					+ "as there is only one instructor left.<br>";
		}
		
		RedirectResult result = null;
		if (logic.isInstructorOfCourse(account.googleId, courseId)) {
			result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_EDIT_PAGE);
			result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
		} else {
			result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
		}
		
		return result;
	}

}
