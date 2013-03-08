package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Course action
 */
public class InstructorCourseDeleteServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		helper.server.deleteCourse(courseID);
		helper.statusMessage = Common.MESSAGE_COURSE_DELETED;
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
			    
        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_DELETE_SERVLET, Common.INSTRUCTOR_COURSE_DELETE_SERVLET_DELETE_COURSE,
        		true, helper, url, data);
        
		if(helper.redirectUrl==null) {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
		}		
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;

		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_COURSE_DELETE_SERVLET_DELETE_COURSE){
			try {
				params = "Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> deleted";
			} catch (NullPointerException e) {
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
