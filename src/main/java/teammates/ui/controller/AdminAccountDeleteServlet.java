package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
public class AdminAccountDeleteServlet extends ActionServlet<AdminAccountDeleteHelper> {

	@Override
	protected AdminAccountDeleteHelper instantiateHelper() {
		return new AdminAccountDeleteHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, AdminAccountDeleteHelper helper) throws EntityDoesNotExistException, InvalidParametersException {
		String url = getRequestedURL(req);
		
		String instructorId = req.getParameter(Common.PARAM_INSTRUCTOR_ID);
		String studentId = req.getParameter(Common.PARAM_STUDENT_ID);
		String courseId = req.getParameter(Common.PARAM_COURSE_ID);
		
		if(instructorId == null && studentId == null){
			helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_MANAGEMENT;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add("Instructor Id is null");
			activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		false, helper, url, null);
			return;
		}
		if(courseId == null){	//Delete the ENTIRE instructor account
			helper.server.deleteInstructor(instructorId);
			helper.statusMessage = Common.MESSAGE_INSTRUCTOR_ACCOUNT_DELETED;
			helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_MANAGEMENT;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(instructorId);
			activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_ACCOUNT,
	        		false, helper, url, data);
		} else{
			if (instructorId != null){  //Delete Instructor from a specific course
				helper.server.deleteInstructor(instructorId, courseId);
				helper.statusMessage = Common.MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE;
				helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_DETAILS + "?instructorid=" + instructorId;
				
				ArrayList<Object> data = new ArrayList<Object>();
				data.add(instructorId);
				data.add(courseId);
				activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_FROM_COURSE,
		        		false, helper, url, data);
			} else if (studentId != null) {	//Delete Student from a specific course
				StudentData student = helper.server.getStudentInCourseForGoogleId(courseId, studentId);
				helper.server.deleteStudent(courseId, student.email);
				helper.statusMessage = Common.MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE;
				helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_DETAILS + "?instructorid=" + studentId;
				
				ArrayList<Object> data = new ArrayList<Object>();
				data.add(instructorId);
				data.add(courseId);
				activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_FROM_COURSE,
		        		false, helper, url, data);
			}
		}
		
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName,
			String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_ACCOUNT)){
			message = generateDeleteInstructorAccountMessage(servletName, action, data);
		} else if (action.equals(Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_FROM_COURSE)){
			message = generateDeleteInstructorFromCourseMessage(servletName, action, data); 
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACCOUNT_MANAGEMENT;
	}

	
	private String generateDeleteInstructorAccountMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Instructor Account for <span class=\"bold\">" + (String)data.get(0) + "</span> has been deleted.";   
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";  
		}
		
		return message;
	}
	
	
	private String generateDeleteInstructorFromCourseMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Instructor <span class=\"bold\">" + (String)data.get(0) + "</span> has been deleted from Course<span class=\"bold\">[" + (String)data.get(1) + "]</span>";   
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";  
		}
		
		return message;
	}
	
}
