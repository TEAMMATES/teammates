package teammates.ui.controller;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDetailsEditPageAction extends InstructorCoursePageAction {
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Common.PARAM_STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyCourseInstructorOrAbove(courseId);
		
		InstructorCourseStudentDetailsEditPageData data = new InstructorCourseStudentDetailsEditPageData(account);
		
		data.student = logic.getStudentForEmail(courseId, studentEmail);
		data.regKey = logic.getKeyForStudent(courseId, studentEmail);
		
		statusToAdmin = "instructorCourseStudentEdit Page Load<br>" + 
				"Editing Student <span class=\"bold\">" + studentEmail +"'s</span> details " +
				"in Course <span class=\"bold\">[" + courseId + "]</span>"; 
		

		return createShowPageResult(Common.JSP_INSTRUCTOR_COURSE_STUDENT_EDIT, data);

	}


}
