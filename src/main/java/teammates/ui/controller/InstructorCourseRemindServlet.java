package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Remind to join Course action
 */
public class InstructorCourseRemindServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) throws EntityDoesNotExistException {
		String url = getRequestedURL(req);  
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		try{
			if(studentEmail!=null){
				helper.server.sendRegistrationInviteToStudent(courseID, studentEmail);
				helper.statusMessage = Common.MESSAGE_COURSE_REMINDER_SENT_TO+studentEmail;
			} else {
				helper.server.sendRegistrationInviteForCourse(courseID);
				helper.statusMessage = Common.MESSAGE_COURSE_REMINDERS_SENT;
			}
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);
			data.add(studentEmail);						  
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_REMIND_SERVLET, Common.INSTRUCTOR_COURSE_REMIND_SERVLET_SEND_REGISTRATION,
	        		true, helper, url, data);
	        
		} catch (InvalidParametersException e){
			helper.statusMessage = e.getMessage();
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add(helper.statusMessage);	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_REMIND_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
	        
		} finally {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
			helper.redirectUrl = Common.addParamToUrl(helper.redirectUrl,Common.PARAM_COURSE_ID,courseID);
		}
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_REMIND_SERVLET_SEND_REGISTRATION)){
			message = generateSendRegistrationMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}

	
	private String generateSendRegistrationMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			if((String)data.get(1) != null){
				message = "Registration Key sent to <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
			} else {
				message = "Registration Key sent to all unregistered students in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
			}
			
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
