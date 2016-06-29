package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackSessionUnpublishedRemindersServlet extends AutomatedRemindersServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletName = Const.AutomatedActionNames.AUTOMATED_FEEDBACK_UNPUBLISHED_REMINDERS;
        action = "send unpublished alert";
        
        String message = "Generating reminders for unpublished feedback sessions.";
        logMessage(req, message);
        
        FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
        feedbackSessionsLogic.scheduleFeedbackSessionUnpublishedEmails();
    }

}
