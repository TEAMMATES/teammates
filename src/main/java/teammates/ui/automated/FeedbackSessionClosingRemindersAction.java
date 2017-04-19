package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;

/**
 * Cron job: schedules feedback session closing emails to be sent.
 */
public class FeedbackSessionClosingRemindersAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return "send closing reminders";
    }

    @Override
    protected String getActionMessage() {
        return "Generating reminders for closing feedback sessions.";
    }

    @Override
    public void execute() {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsClosingWithinTimeLimit();

        for (FeedbackSessionAttributes session : sessions) {
            List<EmailWrapper> emailsToBeSent = new EmailGenerator().generateFeedbackSessionClosingEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                session.setSentClosingEmail(true);
                logic.updateFeedbackSession(session);
            } catch (Exception e) {
                log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
            }
        }
    }

}
