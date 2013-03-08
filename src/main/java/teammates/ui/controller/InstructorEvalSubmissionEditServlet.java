package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.UserType;

@SuppressWarnings("serial")
/**
 * Servlet to handle Edit evaluation submission page.
 * This is for instructor to edit student's submission
 */
public class InstructorEvalSubmissionEditServlet extends EvalSubmissionEditServlet {

	@Override
	protected StudentData getStudentObject(HttpServletRequest req, EvalSubmissionEditHelper helper){
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
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		EvalSubmissionEditHelper h = (EvalSubmissionEditHelper)helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		servletName = servletName.equals("Edit") ? Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SERVLET : "";
		action = action.equals("Edit") ? Common.INSTRUCTOR_EVAL_SUBMISSION_EDIT_SERVLET_PAGE_LOAD : "";
		
		if(action == Common.STUDENT_EVAL_EDIT_SERVLET_PAGE_LOAD){
			try {
				params = "instructorEvalSubmissionEdit Page Load<br>";
				params += "Editing <span class=\"bold\">" + h.student.name + "'s</span> Evaluation <span class=\"bold\">("+ (String)data.get(1)+")</span> for Course <span class=\"bold\">[" + data.get(0) + "]</span>";
			} catch (NullPointerException e) {
				params = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.LOG_SERVLET_ACTION_FAILURE) {
			String e = (String)data.get(0);
	        params = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
	        params += e + "</span>";
		} else {
			params = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}
}
