package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackSessionPublishedRemindersServlet extends AutomatedRemindersServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletName = Const.AutomatedActionNames.AUTOMATED_FEEDBACK_PUBLISHED_REMINDERS;
        action = "send published alert";
        
        String message = "Generating reminders for published feedback sessions.";
        logMessage(req, message);
        
        FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
        feedbackSessionsLogic.scheduleFeedbackSessionPublishedEmails();
    }

}
