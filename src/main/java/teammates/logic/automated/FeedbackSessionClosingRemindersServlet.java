package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.logic.core.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackSessionClosingRemindersServlet extends AutomatedRemindersServlet {
	
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) {
		
		FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
		feedbackSessionsLogic.scheduleFeedbackSessionClosingEmails();
	}

}
