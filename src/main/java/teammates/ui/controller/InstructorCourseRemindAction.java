package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.logic.GateKeeper;

public class InstructorCourseRemindAction extends Action {
	protected static final Logger log = Config.getLogger();
	
	@Override
	public ActionResult execute()
			throws EntityDoesNotExistException, InvalidParametersException {
		
		String courseId = getRequestParam(Config.PARAM_COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Config.PARAM_STUDENT_EMAIL);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		RedirectResult response;
		try{
			if(studentEmail != null){
				logic.sendRegistrationInviteToStudent(courseId, studentEmail);
				statusToUser.add(Config.MESSAGE_COURSE_REMINDER_SENT_TO+studentEmail);
				statusToAdmin = "Registration Key sent to <span class=\"bold\">" + studentEmail + "</span> " +
									"in Course <span class=\"bold\">[" + courseId + "]</span>";
			} else {
				logic.sendRegistrationInviteForCourse(courseId);
				statusToUser.add(Config.MESSAGE_COURSE_REMINDERS_SENT);
				statusToAdmin = "Registration Key sent to all unregistered students " +
						"in Course <span class=\"bold\">[" + courseId + "]</span>";
			}
			
			
		} catch (InvalidParametersException e){
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			
		} finally {
			response = createRedirectResult(Config.PAGE_INSTRUCTOR_COURSE_DETAILS);
			response.addResponseParam(Config.PARAM_COURSE_ID,courseId); 
		}
		return response;

	}


}
