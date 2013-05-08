package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentAttributes;

@SuppressWarnings("serial")
/**
 * Servlet to handle Edit evaluation submission page.
 * This is for instructor to edit student's submission
 */
public class InstructorEvalSubmissionEditServlet extends EvalSubmissionEditServlet {

	@Override
	protected StudentAttributes getStudentObject(HttpServletRequest req, EvalSubmissionEditHelper helper){
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		return helper.server.getStudent(courseID, studentEmail);
	}
	
	@Override
	protected String getMessageOnNullStudent(HttpServletRequest req,
			EvalSubmissionEditHelper helper) {
		return "There is no student with e-mail: " +
				EvalSubmissionEditHelper.escapeForHTML(req.getParameter(Common.PARAM_STUDENT_EMAIL)) +
				" registered in the course " + req.getParameter(Common.PARAM_COURSE_ID);
	}

	@Override
	protected InstructorEvalSubmissionEditHelper instantiateHelper() {
		return new InstructorEvalSubmissionEditHelper();
	}


	@Override
	protected String getDefaultRedirectUrl() {
		return Common.PAGE_INSTRUCTOR_EVAL;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_SUBMISSION_EDIT;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {		
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorEvalSubmissionEdit Page Load<br>";
			message += "Editing <span class=\"bold\">" + (String)data.get(2) + "'s</span> Submission <span class=\"bold\">("+ (String)data.get(1)+")</span> for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
