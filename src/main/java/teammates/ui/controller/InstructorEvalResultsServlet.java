package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.TeamData;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Evaluation Results action
 */
public class InstructorEvalResultsServlet extends
		ActionServlet<InstructorEvalResultsHelper> {

	@Override
	protected InstructorEvalResultsHelper instantiateHelper() {
		return new InstructorEvalResultsHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req,
			InstructorEvalResultsHelper helper) throws EntityDoesNotExistException {
		String url = getRequestedURL(req);    
		
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);

		if (courseID != null && evalName != null) {
			helper.evaluation = helper.server.getEvaluationResult(courseID,
					evalName);
			long start = System.currentTimeMillis();
			sortTeams(helper.evaluation.teams);
			for (TeamData team : helper.evaluation.teams) {
				team.sortByStudentNameAscending();
				for (StudentData student : team.students) {
					sortSubmissionsByFeedback(student.result.incoming);
					sortSubmissionsByReviewee(student.result.outgoing);
				}
			}
			log.fine("Time to sort evaluation, teams, students, and results: "
					+ (System.currentTimeMillis() - start) + " ms");
			helper.statusMessage = Common.MESSAGE_LOADING;
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(courseID);
			data.add(evalName);					
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_RESULTS_SERVLET, Common.INSTRUCTOR_EVAL_RESULTS_SERVLET_PAGE_LOAD,
					true, helper, url, data);
			
		} else { // Incomplete request, just go back to Evaluations Page
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add("Course Id or Evaluation name is null");	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_RESULTS_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_RESULTS;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_RESULTS_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorEvalResults Page Load<br>";
			message += "Viewing Results for Evaluation <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
