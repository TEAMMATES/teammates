package teammates.ui.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
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
		String url = req.getRequestURI();
        if (req.getQueryString() != null){
            url += "?" + req.getQueryString();
        }
        
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
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;
		
		UserType user = helper.server.getLoggedInUser();
		AccountData account = helper.server.getAccount(user.id);
		
		if(action == Common.INSTRUCTOR_EVAL_REMIND_SERVLET_SEND_EVAL_REMINDER){
			try {
				params = "Email sent out to all students who have not completed Evaluation <span class=\"bold\">(" + (String)data.get(1) + ")</span> of Course <span class=\"bold\">[" + (String)data.get(0) + "]</span>";
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
