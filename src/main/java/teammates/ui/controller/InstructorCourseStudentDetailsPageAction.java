package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Constants;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDetailsPageAction extends InstructorCoursePageAction {
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Constants.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Constants.PARAM_STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		InstructorCourseStudentDetailsPageData data = new InstructorCourseStudentDetailsPageData(account);
		
		data.student = logic.getStudentForEmail(courseId, studentEmail);
		data.regKey = logic.getKeyForStudent(courseId, studentEmail);
		
		
		statusToAdmin = "instructorCourseStudentDetails Page Load<br>" + 
				"Viewing details for Student <span class=\"bold\">" + studentEmail + 
				"</span> in Course <span class=\"bold\">[" + courseId + "]</span>"; 
		

		return createShowPageResult(Constants.VIEW_INSTRUCTOR_COURSE_STUDENT_DETAILS, data);

	}


}
