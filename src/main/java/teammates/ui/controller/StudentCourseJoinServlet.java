package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Student Join Course action
 */
public class StudentCourseJoinServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper){
		String url = getRequestedURL(req);
        
		helper.redirectUrl = Common.PAGE_STUDENT_HOME;		
		String regKey = req.getParameter(Common.PARAM_REGKEY);
		if(regKey==null){
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add("Registration key is null");	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_JOIN_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
			return;
		}
		
		try {
			helper.server.joinCourse(helper.userId, regKey);
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(regKey);					
			activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_JOIN_SERVLET, Common.STUDENT_COURSE_JOIN_SERVLET_JOIN_COURSE,
					true, helper, url, data);
		} catch (JoinCourseException e) {
			helper.statusMessage = Helper.escapeForHTML(e.getMessage());
			helper.error = true;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add(helper.statusMessage);	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_JOIN_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add(helper.statusMessage);	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_JOIN_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
		}
	}
	

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.STUDENT_COURSE_JOIN_SERVLET_JOIN_COURSE)){
			message = generateJoinCourseMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
			
		return message;
	}

	
	private String generateJoinCourseMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Student joined course with registration key: " + (String)data.get(0);     
		} catch (IndexOutOfBoundsException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";    
		}
		
		return message;
	}
}
