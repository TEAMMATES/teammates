package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to display the evaluations page for instructors
 */
public class InstructorEvalServlet extends ActionServlet<InstructorEvalHelper> {

	@Override
	protected InstructorEvalHelper instantiateHelper() {
		return new InstructorEvalHelper();
	}

	@Override
	protected void doAction(HttpServletRequest req, InstructorEvalHelper helper)
			throws EntityDoesNotExistException {

		String url = getRequestedURL(req);

		helper.newEvaluationToBeCreated = null;
		helper.courseIdForNewEvaluation = null;

		helper.loadEvaluationsList();
		helper.setStatusMessage();

		activityLogEntry = instantiateActivityLogEntry(
				Common.INSTRUCTOR_EVAL_SERVLET,
				Common.INSTRUCTOR_EVAL_SERVLET_PAGE_LOAD,
				true, helper, url, null);
	}

	@Override
	protected String getDefaultForwardUrl() {
		return Common.JSP_INSTRUCTOR_EVAL;
	}

	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_SERVLET_PAGE_LOAD)){
			message = generatePageLoadMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}
	
	private String generatePageLoadMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "instructorEval Page Load<br>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}

}
