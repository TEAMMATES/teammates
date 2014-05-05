package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.logic.core.FeedbackSessionsLogic;

@SuppressWarnings("serial")
public class FeedbackSessionOpeningRemindersServlet extends AutomatedRemindersServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        servletName = "feedbackSessionOpeningReminders";
        action = "send opening reminders";
        
        String message = "Generating reminders for opening feedback sessions.";
        logMessage(req, message);
        
        FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
        feedbackSessionsLogic.scheduleFeedbackSessionOpeningEmails();
    }

    
}
