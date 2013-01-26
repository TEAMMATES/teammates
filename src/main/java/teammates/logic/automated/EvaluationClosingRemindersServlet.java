package teammates.logic.automated;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.UserType;
import teammates.logic.api.Logic;
import teammates.logic.backdoor.BackDoorLogic;

@SuppressWarnings("serial")
public class EvaluationClosingRemindersServlet extends HttpServlet {
	private static Logger log = Common.getLogger();
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		try {
			ArrayList<MimeMessage> emails = new BackDoorLogic().sendRemindersForClosingEvaluations();
			
			
			UserType user = new Logic().getLoggedInUser();
			
			StringBuilder sb = new StringBuilder("[TEAMMATES_LOG]|||");

			//log action
			String[] actionTkn = req.getServletPath().split("/");
			String action = req.getServletPath();
			if(actionTkn.length > 0) {
				action = actionTkn[actionTkn.length-1]; //retrieve last segment in path
			}
			
			sb.append(action+"|||");			
			sb.append("Automated Reminder|||Automated Reminder|||N/A|||N/A|||");

			String emailTargets = "{targets::";
			for (int i = 0; i < emails.size(); i++){
				Address[] recipients = emails.get(i).getRecipients(Message.RecipientType.TO);
				for (int j = 0; j < recipients.length; j++){
					emailTargets += recipients[j] + "//";
				}
			}
			if (emailTargets != "{targets::") {
				emailTargets = emailTargets.substring(0, emailTargets.length() - 2);
			} else {
				emailTargets += "none";
			}
			emailTargets += "}";
			sb.append(emailTargets);
			
			log.log(Level.INFO, sb.toString());

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
