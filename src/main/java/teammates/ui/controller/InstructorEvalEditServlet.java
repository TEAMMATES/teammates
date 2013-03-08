package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Edit Evaluation action
 */
public class InstructorEvalEditServlet extends ActionServlet<InstructorEvalEditHelper> {

	@Override
	protected InstructorEvalEditHelper instantiateHelper() {
		return new InstructorEvalEditHelper();
	}


	@Override
	protected void doAction(HttpServletRequest req, InstructorEvalEditHelper helper)
			throws EntityDoesNotExistException {
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }
        
		String action = Common.INSTRUCTOR_EVAL_EDIT_SERVLET_PAGE_LOAD;
		EvaluationData newEval = InstructorEvalServlet.extractEvaluationData(req);

		if (newEval.course == null && newEval.name == null) {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add("Course Id or Evaluation name is null");
	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE, true, helper, url, data);
			return;
		}
		
		boolean isSubmit = isPost;

		if (isSubmit) {
			helper.newEvaluationToBeCreated = newEval;
			try {
				helper.server.editEvaluation(newEval.course, newEval.name, newEval.instructions, newEval.startTime,
						newEval.endTime, newEval.timeZone, newEval.gracePeriod, newEval.p2pEnabled);
				helper.statusMessage = Common.MESSAGE_EVALUATION_EDITED;
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
				action = Common.INSTRUCTOR_EVAL_EDIT_SERVLET_EDIT_EVALUATION;
				
				activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, action,
		        		true, helper, url, null);
			} catch (InvalidParametersException ex) {
				helper.statusMessage = ex.getMessage();
				helper.error = true;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add(helper.statusMessage);
		                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE, true, helper, url, data);
			}
		} else {
			helper.newEvaluationToBeCreated = helper.server.getEvaluation(newEval.course,
					newEval.name);
			if (helper.newEvaluationToBeCreated == null) {
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add("Null evaluation cannot be created");
		                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE, true, helper, url, data);
				return;
			}
			
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, action,
	        		true, helper, url, null);
		}
		
		    
        

	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_EDIT;
	}


	@Override
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		InstructorEvalEditHelper h = (InstructorEvalEditHelper) helper;
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_EVAL_EDIT_SERVLET_PAGE_LOAD){
			try {
				EvaluationData eval = h.newEvaluationToBeCreated;
				params = "Editing Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.course + "]</span>.<br>" +
						"<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
						"<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled== true ? "enabled" : "disabled") + "<br><br>" + 
						"<span class=\"bold\">Instructions:</span> " + eval.instructions;
			} catch (NullPointerException e) {
				params = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
			}
		} else if (action == Common.INSTRUCTOR_EVAL_EDIT_SERVLET_EDIT_EVALUATION){
			try {
				EvaluationData eval = h.newEvaluationToBeCreated;
				params = "Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.course + "]</span> edited.<br>" +
						"<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
						"<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled== true ? "enabled" : "disabled") + "<br><br>" + 
						"<span class=\"bold\">Instructions:</span> " + eval.instructions;
			} catch (NullPointerException e){
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
