package teammates.logic.automated;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.logic.backdoor.BackDoorLogic;
import teammates.ui.controller.ActivityLogEntry;
import teammates.ui.controller.Helper;

@SuppressWarnings("serial")
public class EvaluationClosingRemindersServlet extends HttpServlet {
	private static Logger log = Common.getLogger();
	private ActivityLogEntry activityLogEntry;
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		BackDoorLogic backdoorlogic = new BackDoorLogic();
		try {
			ArrayList<MimeMessage> emails = backdoorlogic.sendRemindersForClosingEvaluations();
			ArrayList<Object> data = Common.generateEmailRecipientListForAutomatedReminders(req, emails);

			String url = req.getRequestURI();
	        if (req.getQueryString() != null){
	            url += "?" + req.getQueryString();
	        }    
			activityLogEntry = instantiateActivityLogEntry(Common.EVALUATION_CLOSING_REMINDERS_SERVLET, Common.EVALUATION_CLOSING_REMINDERS_SERVLET_EVALUATION_CLOSE_REMINDER,
					true, null, url, data);
			log.log(Level.INFO, activityLogEntry.generateLogMessage());

		}  catch (Throwable e) {
			String reqParam = Common.printRequestParameters(req);
			MimeMessage email = backdoorlogic.emailErrorReport(req.getServletPath(), reqParam, e);
			log.severe(e.getMessage());	
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShow, Helper helper, String url, ArrayList<Object> data) {
		String message;

		if(action.equals(Common.EVALUATION_CLOSING_REMINDERS_SERVLET_EVALUATION_CLOSE_REMINDER)){
			try {
				message = "<span class=\"bold\">Emails sent to:</span><br>";
				for (int i = 0; i < data.size(); i++){
					message += data.get(i).toString() + "<br>";
				}
			} catch (NullPointerException e) {
				message = "<span class=\"color_red\">Unable to retrieve email targets in " + servletName + ": " + action + ".</span>";
			}
		} else if (action.equals(Common.LOG_SERVLET_ACTION_FAILURE)) {
            String e = data.get(0).toString();
            message = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
            message += e + "</span>";
        } else {
			message = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, toShow, null, message, url);
	}
}
