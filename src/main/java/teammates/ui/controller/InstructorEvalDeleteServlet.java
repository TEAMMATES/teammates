package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;

@SuppressWarnings("serial")
/**
 * Servlet to handle Delete Evaluation action
 */
public class InstructorEvalDeleteServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}

	@Override
	protected void doAction(HttpServletRequest req, Helper helper) {
		String url = getRequestedURL(req);
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		helper.server.deleteEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_DELETED;
		if(helper.redirectUrl==null) helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(evalName);	    
		activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_DELETE_SERVLET, Common.INSTRUCTOR_EVAL_DELETE_SERVLET_DELETE_EVALUATION,
				true, helper, url, data);
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_DELETE_SERVLET_DELETE_EVALUATION)){
			message = generateDeleteEvaluationMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}

	
	private String generateDeleteEvaluationMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Evaluation <span class=\"bold\">" + (String)data.get(1) + "</span> in Course <span class=\"bold\"[" + (String)data.get(0) + "]/span> deleted";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
