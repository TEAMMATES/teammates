package teammates.ui.controller;

import java.util.logging.Logger;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.logic.GateKeeper;

public class InstructorCourseRemindAction extends Action {
	protected static final Logger log = Utils.getLogger();
	
	@Override
	public ActionResult execute()
			throws EntityDoesNotExistException, InvalidParametersException {
		
		String courseId = getRequestParam(Const.ParamsNames.COURSE_ID);
		Assumption.assertNotNull(courseId);
		
		String studentEmail = getRequestParam(Const.ParamsNames.STUDENT_EMAIL);
		
		new GateKeeper().verifyAccessible(
				logic.getInstructorForGoogleId(courseId, account.googleId),
				logic.getCourse(courseId));
		
		RedirectResult response;
		try{
			if(studentEmail != null){
				logic.sendRegistrationInviteToStudent(courseId, studentEmail);
				statusToUser.add(Const.StatusMessages.COURSE_REMINDER_SENT_TO+studentEmail);
				statusToAdmin = "Registration Key sent to <span class=\"bold\">" + studentEmail + "</span> " +
									"in Course <span class=\"bold\">[" + courseId + "]</span>";
			} else {
				logic.sendRegistrationInviteForCourse(courseId);
				statusToUser.add(Const.StatusMessages.COURSE_REMINDERS_SENT);
				statusToAdmin = "Registration Key sent to all unregistered students " +
						"in Course <span class=\"bold\">[" + courseId + "]</span>";
			}
			
			
		} catch (InvalidParametersException e){
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			
		} finally {
			response = createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSE_DETAILS_PAGE);
			response.addResponseParam(Const.ParamsNames.COURSE_ID,courseId); 
		}
		return response;

	}


}
