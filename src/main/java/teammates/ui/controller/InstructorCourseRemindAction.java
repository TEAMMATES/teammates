package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.GateKeeper;

public class InstructorCourseRemindAction extends Action {
	protected static final Logger log = Common.getLogger();
	
	@Override
	public ActionResult execute()
			throws EntityDoesNotExistException, InvalidParametersException {
		
		String courseId = getRequestParam(Common.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Common.PARAM_STUDENT_EMAIL);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		RedirectResult response;
		try{
			if(studentEmail != null){
				logic.sendRegistrationInviteToStudent(courseId, studentEmail);
				statusToUser.add(Common.MESSAGE_COURSE_REMINDER_SENT_TO+studentEmail);
				statusToAdmin = "Registration Key sent to <span class=\"bold\">" + studentEmail + "</span> " +
									"in Course <span class=\"bold\">[" + courseId + "]</span>";
			} else {
				logic.sendRegistrationInviteForCourse(courseId);
				statusToUser.add(Common.MESSAGE_COURSE_REMINDERS_SENT);
				statusToAdmin = "Registration Key sent to all unregistered students " +
						"in Course <span class=\"bold\">[" + courseId + "]</span>";
			}
			
			
		} catch (InvalidParametersException e){
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			
		} finally {
			response = createRedirectResult(Common.PAGE_INSTRUCTOR_COURSE_DETAILS);
			response.addResponseParam(Common.PARAM_COURSE_ID,courseId); 
		}
		return response;

	}


}
