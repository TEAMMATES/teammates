package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor View Course Details action
 *
 */
public class InstructorCourseDetailsServlet extends ActionServlet<InstructorCourseDetailsHelper> {

	@Override
	protected InstructorCourseDetailsHelper instantiateHelper() {
		return new InstructorCourseDetailsHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseDetailsHelper helper) throws EntityDoesNotExistException{
		String url = getRequestedURL(req);
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		
		if(courseID!=null){
			helper.course = helper.server.getCourseDetails(courseID);
			helper.students = helper.server.getStudentListForCourse(courseID);
			helper.instructors = helper.server.getInstructorsOfCourse(courseID);
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);			 
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_DETAILS_SERVLET, Common.INSTRUCTOR_COURSE_DETAILS_SERVLET_PAGE_LOAD,
	        		true, helper, url, data);
	        
		} else {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add("Course Id is null");	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_DETAILS_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
		}
		
		sortStudents(helper.students);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_DETAILS;
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_DETAILS_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		try {
			message = "instructorCourseDetails Page Load<br>";
			message += "Viewing Course Details for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
