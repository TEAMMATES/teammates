package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
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
			
			String url = req.getRequestURI();
	        if (req.getQueryString() != null){
	            url += "?" + req.getQueryString();
	        }    
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_REMIND_SERVLET, Common.INSTRUCTOR_COURSE_REMIND_SERVLET_SEND_REGISTRATION,
	        		true, helper, url, data);
		} catch (InvalidParametersException e){
			helper.statusMessage = e.getMessage();
		} finally {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
			helper.redirectUrl = Common.addParamToUrl(helper.redirectUrl,Common.PARAM_COURSE_ID,courseID);
		}
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		Helper h = helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_REMIND_SERVLET_SEND_REGISTRATION){
			try {
				if((String)data.get(1) != null){
					params = "Registration Key sent to <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
				} else {
					params = "Registration Key sent to all unregistered students in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
				}
				
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}

}
