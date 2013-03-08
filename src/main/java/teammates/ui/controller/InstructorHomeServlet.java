package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Home actions
 */
public class InstructorHomeServlet extends ActionServlet<InstructorHomeHelper> {

	@Override
	protected InstructorHomeHelper instantiateHelper() {
		return new InstructorHomeHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorHomeHelper helper) throws EntityDoesNotExistException{
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        } 
        
		HashMap<String, CourseData> courses = helper.server.getCourseDetailsListForInstructor(helper.userId);
		helper.courses = new ArrayList<CourseData>(courses.values());
		sortCourses(helper.courses);
		for(CourseData course: helper.courses){
			sortEvaluationsByDeadline(course.evaluations);
		}
		   
		activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_HOME_SERVLET, Common.INSTRUCTOR_HOME_SERVLET_PAGE_LOAD,
				true, helper, url, null);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_HOME;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		InstructorHomeHelper h = (InstructorHomeHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		
		if(action == Common.INSTRUCTOR_HOME_SERVLET_PAGE_LOAD){
			try {
				params = "instructorHome Page Load<br>";
				params += "Total Courses: " + h.courses.size();
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
