package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Course action
 * @author Aldrian Obaja
 *
 */
public class InstructorCourseStudentDeleteServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		String url = getRequestedURL(req); 
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		helper.server.deleteStudent(courseID, studentEmail);
		helper.statusMessage = Common.MESSAGE_STUDENT_DELETED;
		helper.redirectUrl = Common.PAGE_INSTRUCTOR_COURSE_DETAILS;
		helper.redirectUrl = Common.addParamToUrl(helper.redirectUrl,Common.PARAM_COURSE_ID,courseID);
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(studentEmail);				   
        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET, Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET_DELETE_STUDENT,
        		true, helper, url, data);
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_STUDENT_DELETE_SERVLET_DELETE_STUDENT)){
			message = generateDeleteStudentMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}

	
	private String generateDeleteStudentMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Student <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> deleted.";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
