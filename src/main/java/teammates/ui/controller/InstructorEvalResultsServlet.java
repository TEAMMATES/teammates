package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.TeamData;
import teammates.common.datatransfer.UserType;
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
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }    
		
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
	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_RESULTS_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE, true, helper, url, data);
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_RESULTS;
	}

	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_EVAL_RESULTS_SERVLET_PAGE_LOAD){
			try {
				params = "instructorEvalResults Page Load<br>";
				params += "Viewing Results for Evaluation <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
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
