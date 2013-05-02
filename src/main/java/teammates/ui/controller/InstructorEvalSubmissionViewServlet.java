package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationResultsBundle;
import teammates.common.datatransfer.StudentResultBundle;
import teammates.common.datatransfer.EvaluationDetailsBundle;
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
		String url = getRequestedURL(req);
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		String studentEmail = req.getParameter(Common.PARAM_STUDENT_EMAIL);
		
		if(courseID==null || evalName==null || studentEmail==null){
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add("Course Id or Evaluation name or Student Email is null");                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
			return;
		}
		
		try {
			helper.student = helper.server.getStudent(courseID, studentEmail);
			helper.evaluationResults = new EvaluationResultsBundle();
			helper.evaluationResults.evaluation = helper.server.getEvaluation(courseID, evalName);
			helper.result = helper.server.getEvaluationResultForStudent(courseID, evalName, studentEmail);
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);
			data.add(evalName);
			data.add(studentEmail);
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET, Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET_PAGE_LOAD,
					true, helper, url, data);
		} catch (InvalidParametersException e) {
			helper.result = new StudentResultBundle(helper.student);
			helper.statusMessage = e.getMessage();
			helper.error = true;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add(helper.statusMessage);	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
		}
		
		
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_SUBMISSION_VIEW;
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_SUBMISSION_VIEW_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}

	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorEvalSubmissionView Page Load<br>";
			message += "Viewing <span class=\"bold\">" + (String)data.get(2) + "'s</span> submission for Evaluation <span class=\"bold\">" + (String)data.get(1) + "</span> for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
