package teammates.ui.controller;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDetailsPageAction extends InstructorCoursePageAction {
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Common.PARAM_STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyCourseInstructorOrAbove(courseId);
		
		InstructorCourseStudentDetailsPageData data = new InstructorCourseStudentDetailsPageData(account);
		
		data.student = logic.getStudentForEmail(courseId, studentEmail);
		data.regKey = logic.getKeyForStudent(courseId, studentEmail);
		
		
		statusToAdmin = "instructorCourseStudentDetails Page Load<br>" + 
				"Viewing details for Student <span class=\"bold\">" + studentEmail + 
				"</span> in Course <span class=\"bold\">[" + courseId + "]</span>"; 
		

		return createShowPageResult(Common.JSP_INSTRUCTOR_COURSE_STUDENT_DETAILS, data);

	}


}
