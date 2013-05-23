package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
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
		String url = getRequestedURL(req);
        
		EvaluationAttributes newEval = InstructorEvalServlet.extractEvaluationData(req);

		if (newEval.courseId == null && newEval.name == null) {
			helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
			
			ArrayList<Object> data = new ArrayList<Object>();
	        data.add("Course Id or Evaluation name is null");	                        
	        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
	        		true, helper, url, data);
			return;
		}
		
		boolean isSubmit = isPost;

		if (isSubmit) {
			helper.newEvaluationToBeCreated = newEval;
			try {
				helper.server.updateEvaluation(newEval.courseId, newEval.name, newEval.instructions, newEval.startTime,
						newEval.endTime, newEval.timeZone, newEval.gracePeriod, newEval.p2pEnabled);
				helper.statusMessage = Common.MESSAGE_EVALUATION_EDITED;
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
				
				ArrayList<Object> data = new ArrayList<Object>();
				data.add(helper.newEvaluationToBeCreated);
				activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, Common.INSTRUCTOR_EVAL_EDIT_SERVLET_EDIT_EVALUATION,
		        		true, helper, url, data);
				
			} catch (InvalidParametersException ex) {
				helper.statusMessage = ex.getMessage();
				helper.error = true;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add(helper.statusMessage);		                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
		        		true, helper, url, data);
			}
		} else {
			helper.newEvaluationToBeCreated = helper.server.getEvaluation(newEval.courseId,
					newEval.name);
			if (helper.newEvaluationToBeCreated == null) {
				helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
				
				ArrayList<Object> data = new ArrayList<Object>();
		        data.add("Null evaluation cannot be created");		                        
		        activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, Common.LOG_SERVLET_ACTION_FAILURE,
		        		true, helper, url, data);
				return;
			}
			
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.newEvaluationToBeCreated);
			activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_EDIT_SERVLET, Common.INSTRUCTOR_EVAL_EDIT_SERVLET_PAGE_LOAD,
	        		true, helper, url, data);
		}
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL_EDIT;
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_EDIT_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else if (action.equals(Common.INSTRUCTOR_EVAL_EDIT_SERVLET_EDIT_EVALUATION)){
			message = generateEditEvaluationMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			EvaluationAttributes eval = (EvaluationAttributes)data.get(0);
			message = "Editing Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.courseId + "]</span>.<br>" +
					"<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
					"<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled== true ? "enabled" : "disabled") + "<br><br>" + 
					"<span class=\"bold\">Instructions:</span> " + eval.instructions;
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
	
	private String generateEditEvaluationMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			EvaluationAttributes eval = (EvaluationAttributes)data.get(0);
			message = "Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.courseId + "]</span> edited.<br>" +
					"<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
					"<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled== true ? "enabled" : "disabled") + "<br><br>" + 
					"<span class=\"bold\">Instructions:</span> " + eval.instructions;
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
