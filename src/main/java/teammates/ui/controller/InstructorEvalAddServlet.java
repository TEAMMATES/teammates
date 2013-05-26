package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle 'add evaluation' action for instructors
 */
public class InstructorEvalAddServlet extends ActionServlet<InstructorEvalHelper> {

	@Override
	protected InstructorEvalHelper instantiateHelper() {
		return new InstructorEvalHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, InstructorEvalHelper helper)
			throws EntityDoesNotExistException {
		
		String url = ActionServlet.getRequestedURL(req);
		
		try {
			EvaluationAttributes newEvaluationCreated= helper.createEvaluation(req);
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(newEvaluationCreated);
			activityLogEntry = instantiateActivityLogEntry(
					Common.INSTRUCTOR_EVAL_SERVLET,
					Common.INSTRUCTOR_EVAL_SERVLET_NEW_EVALUATION,
					true, helper, url, data);
		} catch (EntityAlreadyExistsException e) {
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.statusMessage);
			activityLogEntry = instantiateActivityLogEntry(
					Common.INSTRUCTOR_EVAL_SERVLET,
					Common.LOG_SERVLET_ACTION_FAILURE,
					true, helper, url, data);
			
		} catch (InvalidParametersException e) {
			ArrayList<Object> data = new ArrayList<Object>();
			data.add(helper.statusMessage);
			activityLogEntry = instantiateActivityLogEntry(
					Common.INSTRUCTOR_EVAL_SERVLET,
					Common.LOG_SERVLET_ACTION_FAILURE,
					true, helper, url, data);
		}
		
		helper.loadEvaluationsList();
		helper.setStatusMessage();
		
	}


	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if (action.equals(Common.INSTRUCTOR_EVAL_SERVLET_NEW_EVALUATION)){
			message = generateNewEvaluationMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	
	private String generateNewEvaluationMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			EvaluationAttributes eval = (EvaluationAttributes)data.get(0);
			message = "New Evaluation <span class=\"bold\">(" + eval.name + ")</span> for Course <span class=\"bold\">[" + eval.courseId + "]</span> created.<br>" +
					"<span class=\"bold\">From:</span> " + eval.startTime + "<span class=\"bold\"> to</span> " + eval.endTime + "<br>" +
					"<span class=\"bold\">Peer feedback:</span> " + (eval.p2pEnabled== true ? "enabled" : "disabled") + "<br><br>" + 
					"<span class=\"bold\">Instructions:</span> " + eval.instructions;
		} catch (NullPointerException e){
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
