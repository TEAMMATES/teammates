package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

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
		String url = getRequestedURL(req);
        
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
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_DELETE_SERVLET_DELETE_COURSE)){
			message = generateDeleteCourseMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
		return message;
	}

	
	private String generateDeleteCourseMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> deleted";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
