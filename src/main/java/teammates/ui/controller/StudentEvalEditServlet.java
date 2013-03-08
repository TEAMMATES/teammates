package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.UserType;

@SuppressWarnings("serial")
public class StudentEvalEditServlet extends EvalSubmissionEditServlet {

	@Override
	protected StudentData getStudentObject(HttpServletRequest req,
			EvalSubmissionEditHelper helper) {
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		return helper.server.getStudentInCourseForGoogleId(courseID, helper.userId);
	}
	
	@Override
	protected String getMessageOnNullStudent(HttpServletRequest req,
			EvalSubmissionEditHelper helper) {
		return "You are not registered in the course "+req.getParameter(Common.PARAM_COURSE_ID);
	}
	
	@Override
	protected StudentEvalEditHelper instantiateHelper() {
		return new StudentEvalEditHelper();
	}


	@Override
	protected String getDefaultRedirectUrl() {
		return Common.PAGE_STUDENT_HOME;
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_EVAL_SUBMISSION_EDIT;
	}


	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		StudentEvalEditHelper h = (StudentEvalEditHelper)helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		servletName = servletName.equals("Edit") ? Common.STUDENT_EVAL_EDIT_SERVLET : "";
		action = action.equals("Edit") ? Common.STUDENT_EVAL_EDIT_SERVLET_PAGE_LOAD : action;
		
		if(action == Common.STUDENT_EVAL_EDIT_SERVLET_PAGE_LOAD){
			try {
				params = "studentEvalEdit Page Load<br>";
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
