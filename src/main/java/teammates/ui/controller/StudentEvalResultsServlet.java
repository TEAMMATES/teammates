package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.datatransfer.UserType;
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
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		if(courseID==null || evalName==null){
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
		System.out.println(helper.userId + courseID);
		helper.student = helper.server.getStudentInCourseForGoogleId(courseID, helper.userId);
		if(helper.student==null){
			helper.statusMessage = "You are not registered in the course "+Helper.escapeForHTML(courseID);
			helper.error = true;
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
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
			
			String url = req.getRequestURI();
	        if (req.getQueryString() != null){
	            url += "?" + req.getQueryString();
	        }    
	        activityLogEntry = instantiateActivityLogEntry(Common.STUDENT_EVAL_RESULTS_SERVLET, Common.STUDENT_EVAL_RESULTS_SERVLET_PAGE_LOAD,
	        		true, helper, url, data);
		} catch (InvalidParametersException e) {
			helper.statusMessage = e.getMessage();
			helper.error = true;
			helper.redirectUrl = Common.PAGE_STUDENT_HOME;
			return;
		}
	}

	/**
	 * Put the self submission in front and return the
	 * list of submission excluding self submission
	 * @param subs
	 * @return
	 */
	private List<SubmissionData> organizeSubmissions(List<SubmissionData> subs, StudentEvalResultsHelper helper) {
		for(int i=0; i<subs.size(); i++){
			SubmissionData sub = subs.get(i);
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
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		StudentEvalResultsHelper h = (StudentEvalResultsHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.STUDENT_EVAL_RESULTS_SERVLET_PAGE_LOAD){
			try {
				params = "studentEvalResults Page Load<br>";
				params += "Viewing evaluation results for Evaluation <span class=\"bold\">(" + (String)data.get(1) + ")</span> of Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>"; 
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, account, params, url);
	}

}
