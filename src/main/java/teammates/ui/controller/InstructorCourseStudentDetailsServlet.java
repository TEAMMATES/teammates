package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor course student details page.
 */
public class InstructorCourseStudentDetailsServlet extends ActionServlet<InstructorCourseStudentDetailsHelper> {

	@Override
	protected InstructorCourseStudentDetailsHelper instantiateHelper() {
		return new InstructorCourseStudentDetailsHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, InstructorCourseStudentDetailsHelper helper) throws EntityDoesNotExistException{
		String url = getRequestedURL(req);  
        
        String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		helper.student = helper.server.getStudent(courseID, studentEmail);
		helper.regKey = helper.server.getKeyForStudent(courseID, studentEmail);
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(studentEmail);		  
        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET, Common.INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET_PAGE_LOAD,
        		true, helper, url, data);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_STUDENT_DETAILS;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_STUDENT_DETAILS_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorCourseStudentDetails Page Load<br>";
			message += "Viewing details for Student <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>"; 
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
