package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackSessionClosingRemindersServlet extends AutomatedRemindersServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletName = Const.AutomatedActionNames.AUTOMATED_FEEDBACK_CLOSING_REMINDERS;
        action = "send closing reminders";

        String message = "Generating reminders for closing feedback sessions.";
        logMessage(req, message);
        
        FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
        feedbackSessionsLogic.scheduleFeedbackSessionClosingEmails();
    }

}
