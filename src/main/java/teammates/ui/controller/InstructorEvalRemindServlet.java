package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.exception.EntityDoesNotExistException;

@SuppressWarnings("serial")
/**
 * Servlet to handle Remind students for evaluation action
 */
public class InstructorEvalRemindServlet extends ActionServlet<Helper> {

	@Override
	protected Helper instantiateHelper() {
		return new Helper();
	}


	@Override
	protected void doAction(HttpServletRequest req, Helper helper) throws EntityDoesNotExistException {
		String url = getRequestedURL(req);
        
		String courseID = req.getParameter(Common.PARAM_COURSE_ID);
		String evalName = req.getParameter(Common.PARAM_EVALUATION_NAME);
		
		helper.server.sendReminderForEvaluation(courseID,evalName);
		helper.statusMessage = Common.MESSAGE_EVALUATION_REMINDERSSENT;
		helper.redirectUrl = Common.PAGE_INSTRUCTOR_EVAL;
		
		ArrayList<Object> data = new ArrayList<Object>();
		data.add(courseID);
		data.add(evalName);
		activityLogEntry = instantiateActivityLogEntry(Common.INSTRUCTOR_EVAL_REMIND_SERVLET, Common.INSTRUCTOR_EVAL_REMIND_SERVLET_SEND_EVAL_REMINDER,
				true, helper, url, data);
	}


	@Override
	protected String generateActivityLogEntryMessage(String servletName, String action, ArrayList<Object> data) {
		String message;
		
		if(action.equals(Common.INSTRUCTOR_EVAL_REMIND_SERVLET_SEND_EVAL_REMINDER)){
			message = generateSendEvalReminderMessage(servletName, action, data);
		} else {
			message = generateActivityLogEntryErrorMessage(servletName, action, data);
		}
				
		return message;
	}

	
	private String generateSendEvalReminderMessage(String servletName, String action, ArrayList<Object> data){
		String message;
		
		try {
			message = "Email sent out to all students who have not completed Evaluation <span class=\"bold\">(" + (String)data.get(1) + ")</span> of Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
		} catch (NullPointerException e) {
			message = "<span class=\"color_red\">Null variables detected in " + servletName + ": " + action + ".</span>";
		}
		
		return message;
	}
}
