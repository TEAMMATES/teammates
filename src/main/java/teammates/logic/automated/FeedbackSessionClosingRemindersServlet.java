package teammates.logic.automated;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.logic.Emails;
import teammates.logic.FeedbackSessionsLogic;
import teammates.ui.controller.ActivityLogEntry;
import teammates.ui.controller.Helper;

@SuppressWarnings("serial")
public class FeedbackSessionClosingRemindersServlet extends HttpServlet {
	
	private static Logger log = Common.getLogger();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}	
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
		FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
		
		try {
			ArrayList<MimeMessage> emails = feedbackSessionsLogic.sendFeedbackSessionOpenEmails();
			logActivity(req, emails);
		}  catch (Throwable e) {
			String reqParam = Common.printRequestParameters(req);
			new Emails().sendErrorReport(req.getServletPath(), reqParam, e);
			log.severe(e.getMessage());	
		} 
	}

	private void logActivity(HttpServletRequest req,
			ArrayList<MimeMessage> emails) {
		ArrayList<Object> data = Common.extractRecipientsList(emails);

		String url = req.getRequestURI();
		if (req.getQueryString() != null){
		    url += "?" + req.getQueryString();
		}    
		ActivityLogEntry activityLogEntry = instantiateActivityLogEntry(
				Common.FEEDBACK_SESSION_OPENING_REMINDERS_SERVLET, 
				Common.FEEDBACK_SESSION_OPENING_REMINDERS_SERVLET_SESSION_OPEN_REMINDER,
				true, null, url, data);
		log.log(Level.INFO, activityLogEntry.generateLogMessage());
	}
	
	protected ActivityLogEntry instantiateActivityLogEntry(
			String servletName, String action, boolean toShow, Helper helper, 
			String url, ArrayList<Object> data) {
		
		String message;

		if(action.equals(Common.FEEDBACK_SESSION_OPENING_REMINDERS_SERVLET_SESSION_OPEN_REMINDER)){
			try {
				message = "<span class=\"bold\">Emails sent to:</span><br>";
				for (int i = 0; i < data.size(); i++){
					message += data.get(i).toString() + "<br>";
				}
			} catch (Exception e) {
				message = "<span class=\"color_red\">Unable to retrieve email targets in " + servletName + ": " + action + ".</span>";
			}
		} else if (action.equals(Common.LOG_SERVLET_ACTION_FAILURE)) {
            String e = data.get(0).toString();
            message = "<span class=\"color_red\">Servlet Action failure in " + servletName + "<br>";
            message += e + "</span>";
        } else {
			message = "<span class=\"color_red\">Unknown Action - " + servletName + ": " + action + ".</span>";
		}
				
		return new ActivityLogEntry(servletName, action, null, message, url);
	}

}
