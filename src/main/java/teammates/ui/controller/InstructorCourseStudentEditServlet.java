package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Instructor course student edit page.
 */
public class InstructorCourseStudentEditServlet extends
		ActionServlet<InstructorCourseStudentEditHelper> {

	@Override
	protected InstructorCourseStudentEditHelper instantiateHelper() {
		return new InstructorCourseStudentEditHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req,
			InstructorCourseStudentEditHelper helper)
			throws EntityDoesNotExistException {
		String url = getRequestedURL(req); 
		
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);

		boolean submit = (req.getParameter("submit") != null);
		String studentName = req.getParameter(Common.PARAM_STUDENT_NAME);
		String newEmail = req.getParameter(Common.PARAM_NEW_STUDENT_EMAIL);
		String teamName = req.getParameter(Common.PARAM_TEAM_NAME);
		String comments = req.getParameter(Common.PARAM_COMMENTS);

		helper.student = helper.server.getStudent(courseID, studentEmail);
		helper.regKey = helper.server.getKeyForStudent(courseID, studentEmail);

		if (submit) {
			helper.student.name = studentName;
			helper.student.email = newEmail;
			helper.student.team = teamName;
			helper.student.comments = comments;
			try {
				helper.server.editStudent(studentEmail, helper.student);
				helper.statusMessage = Common.MESSAGE_STUDENT_EDITED;
				helper.redirectUrl = helper.getInstructorCourseDetailsLink(courseID);
				
				ArrayList<Object> data = new ArrayList<Object>();
				data.add(courseID);
				data.add(helper.student);
				activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET, Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_EDIT_DETAILS,
		        		true, helper, url, data);
				
			} catch (InvalidParametersException e) {
				helper.statusMessage = e.getMessage();
				helper.error = true;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add(helper.statusMessage);		                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
		        		true, helper, url, data);
			}
		} else {
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);
			data.add(studentEmail);
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET, Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_PAGE_LOAD,
	        		true, helper, url, data);
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_COURSE_STUDENT_EDIT;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else if (action.equals(Common.INSTRUCTOR_COURSE_STUDENT_EDIT_SERVLET_EDIT_DETAILS)){
			message = generateEditDetailsMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorCourseStudentEdit Page Load<br>";
			message += "Editing Student <span class=\"bold\">" + (String)data.get(1) +"'s</span> details in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
	
	private String generateEditDetailsMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			StudentAttributes student = (StudentAttributes)data.get(1);
			message = "Student <span class=\"bold\">" + student.name + "'s</span> details in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> edited.<br>";
			message += "New Email: " + student.email + "<br>New Team: " + student.team + "<br>Comments: " + student.comments;
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
