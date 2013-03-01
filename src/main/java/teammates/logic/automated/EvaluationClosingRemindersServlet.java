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
		try {
			ArrayList<MimeMessage> emails = new BackDoorLogic().sendRemindersForClosingEvaluations();
			ArrayList<Object> data = Common.generateEmailRecipientListForAutomatedReminders(req, emails);

			String url = req.getRequestURI();
	        if (req.getQueryString() != null){
	            url += "?" + req.getQueryString();
	        }    
			activityLogEntry = instantiateActivityLogEntry(Common.EVALUATION_CLOSING_REMINDERS_SERVLET, Common.EVALUATION_CLOSING_REMINDERS_SERVLET_EVALUATION_CLOSE_REMINDER,
					true, null, url, data);
			log.log(Level.INFO, activityLogEntry.generateLogMessage());

		} catch (Exception e) {
			throw new RuntimeException("Unexpected exception while sending reminders for closing evaluations" + e);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	
	protected ActivityLogEntry instantiateActivityLogEntry(String servletName, String action, boolean toShows, Helper helper, String url, ArrayList<Object> data) {
		String params;

		if(action == Common.EVALUATION_CLOSING_REMINDERS_SERVLET_EVALUATION_CLOSE_REMINDER){
			try {
				params = "<span class=\"bold\">Emails sent to:</span><br>";
				for (int i = 0; i < data.size(); i++){
					params += (String)data.get(i) + "<br>";
				}
			} catch (NullPointerException e) {
				params = "<span class=\"colour_red\">Unable to retrieve email targets in " + servletName + ": " + action + ".</span>";
			}
		} else {
			params = "<span class=\"colour_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, true, null, params, url);
	}
}
