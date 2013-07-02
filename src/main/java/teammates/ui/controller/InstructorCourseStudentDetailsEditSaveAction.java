package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

public class InstructorCourseStudentDetailsEditSaveAction extends InstructorCoursePageAction {
	
	
	@Override
	public ActionResult execute() throws EntityDoesNotExistException {

		String courseId = getRequestParam(Config.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Config.PARAM_STUDENT_EMAIL);
		Assumption.assertNotNull(studentEmail);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		InstructorCourseStudentDetailsEditPageData data = new InstructorCourseStudentDetailsEditPageData(account);
		
		data.student = logic.getStudentForEmail(courseId, studentEmail);
		data.regKey = logic.getKeyForStudent(courseId, studentEmail);
		
		data.student.name = getRequestParam(Config.PARAM_STUDENT_NAME);
		data.student.email = getRequestParam(Config.PARAM_NEW_STUDENT_EMAIL);
		data.student.team = getRequestParam(Config.PARAM_TEAM_NAME);
		data.student.comments = getRequestParam(Config.PARAM_COMMENTS);	
		
		try {
			logic.updateStudent(studentEmail, data.student);
			statusToUser.add(Config.MESSAGE_STUDENT_EDITED);
			statusToAdmin = "Student <span class=\"bold\">" + data.student.email + 
					"'s</span> details in Course <span class=\"bold\">[" + courseId + "]</span> edited.<br>"+ 
					"New Email: " + data.student.email + "<br>New Team: " + 
					data.student.team + "<br>Comments: " + data.student.comments;
			
			RedirectResult result = createRedirectResult(Config.PAGE_INSTRUCTOR_COURSE_DETAILS);
			result.addResponseParam(Config.PARAM_COURSE_ID, courseId);
			return result;
			
		} catch (InvalidParametersException e) {
			isError = true;
			statusToUser.add(e.getMessage());
			statusToAdmin = Config.LOG_SERVLET_ACTION_FAILURE+ " : " + e.getMessage();
			ShowPageResult result = createShowPageResult(Config.JSP_INSTRUCTOR_COURSE_STUDENT_EDIT, data);
			return result;
		}
		
	}


}
