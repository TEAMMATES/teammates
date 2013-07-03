package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDeleteAction extends InstructorCoursePageAction {
	
	protected static Logger log = Constants.getLogger();
	

	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Constants.PARAM_STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		logic.deleteStudent(courseId, studentEmail);
		statusToUser.add(Constants.STATUS_STUDENT_DELETED);
		statusToAdmin = "Student <span class=\"bold\">" + studentEmail + 
				"</span> in Course <span class=\"bold\">[" + courseId + "]</span> deleted.";
		

		RedirectResult result = createRedirectResult(Constants.ACTION_INSTRUCTOR_COURSE_DETAILS);
		result.addResponseParam(Constants.PARAM_COURSE_ID, courseId);
		return result;

	}


}
