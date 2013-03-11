
package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseData;
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
		String url = getRequestedURL(req);
        
		try{
			helper.courses = helper.server.getCourseDetailsListForStudent(helper.userId);
			sortCourses(helper.courses);
			for(CourseData course: helper.courses){
				sortEvaluationsByDeadline(course.evaluations);
			}
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.courses.size());
			activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_HOME_SERVLET, Common.STUDENT_HOME_SERVLET_PAGE_LOAD,
					true, helper, url, data);
			
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
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.STUDENT_HOME_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		}else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
			
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "studentHome Page Load<br>";
			message += "Total courses: " + (Integer)data.get(0);      
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";       
		}
		
		return message;
	}

}
