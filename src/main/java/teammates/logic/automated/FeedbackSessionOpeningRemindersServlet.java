package teammates.logic.automated;

import java.util.ArrayList;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackSessionOpeningRemindersServlet extends AutomatedRemindersServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
		servletName = "feedbackSessionOpeningReminders";
		action = "send closing reminders";
		
		FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
		
		try {
			ArrayList<MimeMessage> emails = feedbackSessionsLogic.sendFeedbackSessionOpeningEmails();
			logActivitySuccess(req, emails);
		}  catch (Throwable e) {
			String reqParam = HttpRequestHelper.printRequestParameters(req);
			new Emails().sendErrorReport(req.getServletPath(), reqParam, e);
			logActivityFailure(req, e);	
		} 
	}

	
}
