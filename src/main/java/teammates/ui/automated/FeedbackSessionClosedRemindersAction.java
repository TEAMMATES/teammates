package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;

/**
 * Cron job: schedules feedback session closed emails to be sent.
 */
public class FeedbackSessionClosedRemindersAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return "send closed reminders";
    }

    @Override
    protected String getActionMessage() {
        return "Generating reminders for closed feedback sessions.";
    }

    @Override
    public void execute() {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsClosedWithinThePastHour();

        for (FeedbackSessionAttributes session : sessions) {
            List<EmailWrapper> emailsToBeSent = new EmailGenerator().generateFeedbackSessionClosedEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                session.setSentClosedEmail(true);
                logic.updateFeedbackSession(session);
            } catch (Exception e) {
                log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
            }
        }
    }

}
