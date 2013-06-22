package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDeleteAction extends InstructorCoursePageAction {
	
	protected static Logger log = Common.getLogger();
	

	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Common.PARAM_STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyCourseInstructorOrAbove(courseId);
		
		logic.deleteStudent(courseId, studentEmail);
		statusToUser.add(Common.MESSAGE_STUDENT_DELETED);
		statusToAdmin = "Student <span class=\"bold\">" + studentEmail + 
				"</span> in Course <span class=\"bold\">[" + courseId + "]</span> deleted.";
		

		RedirectResult result = createRedirectResult(Common.PAGE_INSTRUCTOR_COURSE_DETAILS);
		result.addResponseParam(Common.PARAM_COURSE_ID, courseId);
		return result;

	}


}
