
package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
public class StudentHomeServlet extends ActionServlet<StudentHomeHelper> {

	@Override
	protected StudentHomeHelper instantiateHelper() {
		return new StudentHomeHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, StudentHomeHelper helper){
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }
        
		try{
			helper.courses = helper.server.getCourseDetailsListForStudent(helper.userId);
			sortCourses(helper.courses);
			for(CourseData course: helper.courses){
				sortEvaluationsByDeadline(course.evaluations);
			}
			
			activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_HOME_SERVLET, Common.STUDENT_HOME_SERVLET_PAGE_LOAD,
					true, helper, url, null);
		} catch (InvalidParametersException e){
			helper.statusMessage = e.getMessage();
			helper.error = true;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(e.getClass() + ": " + e.getMessage());
			activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_HOME_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
					true, helper, url, data);
		} catch (EntityDoesNotExistException e){
			helper.courses = new ArrayList<CourseData>();
			if (helper.statusMessage == null){
				helper.statusMessage = Common.MESSAGE_STUDENT_FIRST_TIME;
			}
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(e.getClass() + ": " + e.getMessage());
			activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_HOME_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
					true, helper, url, data);
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_HOME;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		StudentHomeHelper h = (StudentHomeHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.STUDENT_HOME_SERVLET_PAGE_LOAD){
			try {
				params = "studentHome Page Load<br>";
				params += "Total courses: " + h.courses.size();      
			} catch (NullPointerException e) {
				params = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";       
			}
		}else if (action == Common.LOG_SERVLET_ACTION_FAILURE) {
			String e = (String)data.get(0);
			params = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
			params += e + "</span>";
		} else {
			params = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";    
		}
			
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}

}
