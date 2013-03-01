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
		helper.redirectUrl = Common.PAGE_STUDENT_HOME;
		
		String regKey = req.getParameter(Common.PARAM_REGKEY);
		if(regKey==null) return;
		
		try {
			helper.server.joinCourse(helper.userId, regKey);
		} catch (JoinCourseException e) {
			helper.statusMessage = Helper.escapeForHTML(e.getMessage());
			helper.error = true;
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
		}
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(regKey);
		
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }
		activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_COURSE_JOIN_SERVLET, Common.STUDENT_COURSE_JOIN_SERVLET_JOIN_COURSE,
				true, helper, url, data);
		
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		Helper h = helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.STUDENT_COURSE_JOIN_SERVLET_JOIN_COURSE){
			try {
				params = "Student joined course with registration key: " + (String)data.get(0);     
			} catch (IndexOutOfBoundsException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";    
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";   
		}
			
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}

}
