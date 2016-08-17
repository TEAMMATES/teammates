package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Const;
import teammates.logic.core.FeedbackSessionsLogic;

/**
 * Handle requests for closed sessions and send emails to instructors and students.
 */
@SuppressWarnings("serial")
public class FeedbackSessionClosedRemindersServlet extends AutomatedRemindersServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletName = Const.AutomatedActionNames.AUTOMATED_FEEDBACK_CLOSED_REMINDERS;
        action = "send closed reminders";

        String message = "Generating reminders for closed feedback sessions.";
        logMessage(req, message);
        
        FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
        feedbackSessionsLogic.scheduleFeedbackSessionClosedEmails();
    }

}
