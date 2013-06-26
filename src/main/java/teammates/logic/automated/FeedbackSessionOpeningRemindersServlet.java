package teammates.logic.automated;

import java.util.ArrayList;

import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.Common;
import teammates.logic.Emails;
import teammates.logic.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackSessionOpeningRemindersServlet extends AutomatedRemindersServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
		FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
		
		try {
			ArrayList<MimeMessage> emails = feedbackSessionsLogic.sendFeedbackSessionOpenEmails();
			logActivitySuccess(req, emails);
		}  catch (Throwable e) {
			String reqParam = Common.printRequestParameters(req);
			new Emails().sendErrorReport(req.getServletPath(), reqParam, e);
			logActivityFailure(req, e);	
		} 
	}

	
}
