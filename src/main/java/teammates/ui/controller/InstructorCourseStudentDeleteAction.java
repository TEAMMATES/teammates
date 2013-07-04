package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDeleteAction extends InstructorCoursePageAction {
	
	protected static Logger log = Config.getLogger();
	

	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Const.ParamsNames.STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		logic.deleteStudent(courseId, studentEmail);
		statusToUser.add(Const.StatusMessages.STUDENT_DELETED);
		statusToAdmin = "Student <span class=\"bold\">" + studentEmail + 
				"</span> in Course <span class=\"bold\">[" + courseId + "]</span> deleted.";
		

		RedirectResult result = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS);
		result.addResponseParam(Const.ParamsNames.COURSE_ID, courseId);
		return result;

	}


}
