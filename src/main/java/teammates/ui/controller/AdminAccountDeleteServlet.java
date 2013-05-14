package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentAttributes;
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
		String account = req.getParameter("account");
		
		if(instructorId == null && studentId == null){
			helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_MANAGEMENT;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add("Instructor Id is null");
			activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		false, helper, url, null);
			return;
		}
		
		ArrayList<Object> data = new ArrayList<Object>();
		if(instructorId != null){
			data.add(instructorId);
		} else {
			data.add(studentId);
		}
		data.add(courseId);
		
		if(courseId == null && account == null){	
			deleteInstructorStatus(helper, instructorId);
					
			activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_STATUS,
		        	false, helper, url, data);
		} else if (courseId == null && account != null){
			deleteInstructorAccount(helper, instructorId);
				
			activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_ACCOUNT,
		       		false, helper, url, data);
		} else if (courseId != null && instructorId != null){
			removeInstructorFromCourse(helper, instructorId, courseId);	

			activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_FROM_COURSE,
		       		false, helper, url, data);
		} else if (courseId != null && studentId != null) {
			removeStudentFromCourse(helper, studentId, courseId);
			
			activityLogEntry = instantiateActivityLogEntry(Common.ADMIN_ACCOUNT_DELETE_SERVLET, Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_FROM_COURSE,
		       		false, helper, url, data);
		}		
	}

	private void deleteInstructorStatus(Helper helper, String instructorId){
		helper.server.deleteInstructorsForGoogleId(instructorId);
		helper.statusMessage = Common.MESSAGE_INSTRUCTOR_STATUS_DELETED;
		helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_MANAGEMENT;	
	}
	
	private void deleteInstructorAccount(Helper helper, String instructorId){
		helper.server.deleteAccount(instructorId);
		helper.statusMessage = Common.MESSAGE_INSTRUCTOR_ACCOUNT_DELETED;
		helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_MANAGEMENT;
	}
	
	private void removeInstructorFromCourse(Helper helper, String instructorId, String courseId){
		helper.server.deleteInstructor(courseId, instructorId);
		helper.statusMessage = Common.MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE;
		helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_DETAILS + "?instructorid=" + instructorId;
	}
	
	private void removeStudentFromCourse(Helper helper, String studentId, String courseId){
		StudentAttributes student = helper.server.getStudentForGoogleId(courseId, studentId);
		helper.server.deleteStudent(courseId, student.email);
		helper.statusMessage = Common.MESSAGE_INSTRUCTOR_REMOVED_FROM_COURSE;
		helper.redirectUrl = Common.PAGE_ADMIN_ACCOUNT_DETAILS + "?instructorid=" + studentId;
	}
	
	
	
	@Override
	protected String generateActivityLogEntryMessage(String servletName,
			String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_STATUS)){
			message = generateDeleteInstructorStatusMessage(servletName, action, data);
		} else if (action.equals(Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_FROM_COURSE)){
			message = generateDeleteInstructorFromCourseMessage(servletName, action, data); 
		} else if (action.equals(Common.ADMIN_ACCOUNT_DELETE_SERVLET_DELETE_INSTRUCTOR_ACCOUNT)){
			message = generateDeleteInstructorAccountMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_ADMIN_ACCOUNT_MANAGEMENT;
	}

	
	private String generateDeleteInstructorStatusMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Instructor Status for <span class=\"bold\">" + (String)data.get(0) + "</span> has been deleted.";   
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
	
	private String generateDeleteInstructorAccountMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Instructor Account for <span class=\"bold\">" + (String)data.get(0) + "</span> has been deleted.";   
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";  
		}
		
		return message;
	}
}
