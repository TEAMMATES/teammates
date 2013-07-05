package teammates.logic.automated;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.ActivityLogEntry;
import teammates.common.util.Utils;
import teammates.logic.core.Emails;

@SuppressWarnings("serial")
public abstract class AutomatedRemindersServlet extends HttpServlet{
	protected static Logger log = Utils.getLogger();
	protected String servletName = "unspecified";
	protected String action = "unspecified";
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
	
	public abstract void doGet(HttpServletRequest req, HttpServletResponse resp);

	protected String getFullUrl(HttpServletRequest req) {
		String url = req.getRequestURI();
		if (req.getQueryString() != null) {
			url += "?" + req.getQueryString();
		}
		return url;
	}

	protected void logActivitySuccess(HttpServletRequest req, ArrayList<MimeMessage> emails) {
				
		String url = getFullUrl(req);
		
		String message;
		
			try {
				ArrayList<Object> data = Emails.extractRecipientsList(emails);
				message = "<span class=\"bold\">Emails sent to:</span><br>";
				for (int i = 0; i < data.size(); i++){
					message += data.get(i).toString() + "<br>";
				}
			} catch (Exception e) {
				message = "<span class=\"color_red\">Unable to retrieve email targets in " + servletName + ": " + action + ".</span>";
			}
		
		ActivityLogEntry activityLogEntry = new ActivityLogEntry(servletName, action, null, message, url);
		log.log(Level.INFO, activityLogEntry.generateLogMessage());
	}

	protected void logActivityFailure(HttpServletRequest req, Throwable e) {
				
		String url = getFullUrl(req);
	
		String message = "<span class=\"color_red\">Servlet Action failure in "	+ servletName + "<br>";
		message += e.getMessage() + "</span>";
		ActivityLogEntry activityLogEntry = new ActivityLogEntry(servletName, action, null, message, url);
		log.log(Level.INFO, activityLogEntry.generateLogMessage());
		log.severe(e.getMessage());
	}

}
