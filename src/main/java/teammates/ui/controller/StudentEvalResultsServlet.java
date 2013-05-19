package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
public class StudentEvalResultsServlet extends ActionServlet<StudentEvalResultsHelper> {

	@Override
	protected StudentEvalResultsHelper instantiateHelper() {
		return new StudentEvalResultsHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, StudentEvalResultsHelper helper)
			throws EntityDoesNotExistException {
		String url = getRequestedURL(req);
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		if(courseID==null || evalName==null){
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add("Course Id or Evaluation Name is null");
			activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_EVAL_RESULTS_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
			return;
		}
		
		helper.student = helper.server.getStudentForGoogleId(courseID, helper.userId);
		if(helper.student==null){
			helper.statusMessage = "You are not registered in the course "+Helper.escapeForHTML(courseID);
			helper.error = true;
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.statusMessage);
			activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_EVAL_RESULTS_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
			return;
		}
		
		helper.eval = helper.server.getEvaluation(courseID, evalName);
		
		try{
			helper.evalResult = helper.server.getEvaluationResultForStudent(courseID, evalName, helper.student.email);
			sortSubmissionsByJustification(helper.evalResult.incoming);
			helper.incoming = organizeSubmissions(helper.evalResult.incoming, helper);
			sortSubmissionsByPoints(helper.evalResult.outgoing);
			helper.outgoing = organizeSubmissions(helper.evalResult.outgoing, helper);
			sortSubmissionsByReviewee(helper.evalResult.selfEvaluations);
			helper.selfEvaluations = organizeSubmissions(helper.evalResult.selfEvaluations, helper);
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);
			data.add(evalName);			    
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_EVAL_RESULTS_SERVLET, Common.STUDENT_EVAL_RESULTS_SERVLET_PAGE_LOAD,
	        		true, helper, url, data);
	        
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(e.getClass() + ": " + e.getMessage());
			activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_EVAL_RESULTS_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
		}
	}

	/**
	 * Put the self submission in front and return the
	 * list of submission excluding self submission
	 * @param subs
	 * @return
	 */
	private List<SubmissionAttributes> organizeSubmissions(List<SubmissionAttributes> subs, StudentEvalResultsHelper helper) {
		for(int i=0; i<subs.size(); i++){
			SubmissionAttributes sub = subs.get(i);
			if(sub.reviewee.equals(sub.reviewer) && sub.reviewee.equals(helper.student.email)){
				subs.remove(sub);
				subs.add(0,sub);
				break;
			}
		}
		return subs.subList(1,subs.size());
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_STUDENT_EVAL_RESULTS;
	}



	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.STUDENT_EVAL_RESULTS_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}

	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "studentEvalResults Page Load<br>";
			message += "Viewing evaluation results for Evaluation <span class=\"bold\">(" + (String)data.get(1) + ")</span> of Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>"; 
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
