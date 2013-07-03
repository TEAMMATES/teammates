package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDetailsEditPageAction extends InstructorCoursePageAction {
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Constants.PARAM_STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		InstructorCourseStudentDetailsEditPageData data = new InstructorCourseStudentDetailsEditPageData(account);
		
		data.student = logic.getStudentForEmail(courseId, studentEmail);
		data.regKey = logic.getKeyForStudent(courseId, studentEmail);
		
		statusToAdmin = "instructorCourseStudentEdit Page Load<br>" + 
				"Editing Student <span class=\"bold\">" + studentEmail +"'s</span> details " +
				"in Course <span class=\"bold\">[" + courseId + "]</span>"; 
		

		return createShowPageResult(Constants.VIEW_INSTRUCTOR_COURSE_STUDENT_EDIT, data);

	}


}
