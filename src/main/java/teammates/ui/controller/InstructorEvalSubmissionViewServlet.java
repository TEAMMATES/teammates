package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.EvalResultData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Evaluation Results action
 */
public class InstructorEvalSubmissionViewServlet extends ActionServlet<InstructorEvalSubmissionViewHelper> {

	@Override
	protected InstructorEvalSubmissionViewHelper instantiateHelper() {
		return new InstructorEvalSubmissionViewHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorEvalSubmissionViewHelper helper) throws EntityDoesNotExistException{
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		if(courseID==null || evalName==null || studentEmail==null){
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
			return;
		}
		
		try {
			helper.student = helper.server.getStudent(courseID, studentEmail);
			helper.evaluation = helper.server.getEvaluation(courseID, evalName);
			helper.result = helper.server.getEvaluationResultForStudent(courseID, evalName, studentEmail);
		} catch (InvalidParametersException e) {
			helper.result = new EvalResultData();
			helper.statusMessage = e.getMessage();
			helper.error = true;
			return;
		}
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(evalName);
		data.add(studentEmail);
		
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }    
		activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET, Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET_PAGE_LOAD,
				true, helper, url, data);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_SUBMISSION_VIEW;
	}


	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		InstructorEvalSubmissionViewHelper h = (InstructorEvalSubmissionViewHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET_PAGE_LOAD){
			try {
				params = "instructorEvalSubmissionView Page Load<br>";
				params += "Viewing <span class=\"bold\">" + (String)data.get(2) + "'s</span> submission for Evaluation <span class=\"bold\">" + (String)data.get(1) + "</span> for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}

}
