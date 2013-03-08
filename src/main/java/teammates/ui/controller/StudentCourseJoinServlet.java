package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
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
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }
        
		helper.redirectUrl = Common.PAGE_STUDENT_HOME;		
		String regKey = req.getParameter(Common.PARAM_REGKEY);
		if(regKey==null){
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add("Registration key is null");
	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_JOIN_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE, true, helper, url, data);
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
	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_JOIN_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE, true, helper, url, data);
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add(helper.statusMessage);
	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_JOIN_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE, true, helper, url, data);
		}
		
		
		
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.STUDENT_COURSE_JOIN_SERVLET_JOIN_COURSE){
			try {
				params = "Student joined course with registration key: " + (String)data.get(0);     
			} catch (IndexOutOfBoundsException e) {
				params = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";    
			}
		} else if (action == Common.LOG_SERVLET_ACTION_FAILURE) {
			String e = (String)data.get(0);
	        params = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
	        params += e + "</span>";
		} else {
			params = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";   
		}
			
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}

}
