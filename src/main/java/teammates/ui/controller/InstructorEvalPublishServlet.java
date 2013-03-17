package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Publish evaluation action
 */
public class InstructorEvalPublishServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}


	@Override
	protected void doAction(HttpServletRequest req, Helper helper) throws EntityDoesNotExistException, InvalidParametersException {
		String url = getRequestedURL(req);
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		helper.server.publishEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_PUBLISHED;
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(evalName);
		activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_PUBLISH_SERVLET, Common.INSTRUCTOR_EVAL_PUBLISH_SERVLET_PUBLISH_EVALUATION,
				true, helper, url, data);
	}



	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_PUBLISH_SERVLET_PUBLISH_EVALUATION)){
			message = generatePublishEvaluationMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}


	private String generatePublishEvaluationMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Evaluation <span class=\"bold\">(" + (String)data.get(1) + ")</span> for Course <span class=\"bold\">[" + (String)data.get(0) + "]</span> published.";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
