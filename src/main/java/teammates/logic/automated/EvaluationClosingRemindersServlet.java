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

@SuppressWarnings("serial")
public class EvaluationClosingRemindersServlet extends HttpServlet {
	private static Logger log = Common.getLogger();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			ArrayList<MimeMessage> emails = new BackDoorLogic().sendRemindersForClosingEvaluations();
			
			String logMessage = Common.generateLogMessagesForAutomatedReminders(req, emails);
			log.log(Level.INFO, logMessage);

		} catch (Exception e) {
			throw new RuntimeException(
					"Unexpected exception while sending reminders for closing evaluations"
							+ e);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) {
		doGet(req, resp);
	}
}
