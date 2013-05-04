package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
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
		String url = getRequestedURL(req); 
        
		HashMap<String, CourseDetailsBundle> courses = helper.server.getCourseDetailsListForInstructor(helper.userId);
		helper.courses = new ArrayList<CourseDetailsBundle>(courses.values());
		sortDetailedCourses(helper.courses);
		for(CourseDetailsBundle course: helper.courses){
			sortEvaluationsByDeadline(course.evaluations);
		}
		   
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(helper.courses.size());
		activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_HOME_SERVLET, Common.INSTRUCTOR_HOME_SERVLET_PAGE_LOAD,
				true, helper, url, data);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_HOME;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_HOME_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}

		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorHome Page Load<br>";
			message += "Total Courses: " + (Integer)data.get(0);
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
