package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.logic.core.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackSessionPublishedRemindersServlet extends AutomatedRemindersServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
		FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
		feedbackSessionsLogic.scheduleFeedbackSessionPublishedEmails();
	}

}
