package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * Cron job: schedules feedback session published emails to be sent.
 */
public class FeedbackSessionPublishedRemindersAction extends AutomatedAction {

    @Override
    protected String getActionDescription() {
        return "send published reminders";
    }

    @Override
    protected String getActionMessage() {
        return "Generating reminders for published feedback sessions.";
    }

    @Override
    public void execute() {
        List<FeedbackSessionAttributes> sessions =
                logic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        for (FeedbackSessionAttributes session : sessions) {
            taskQueuer.scheduleFeedbackSessionPublishedEmail(session.getCourseId(), session.getFeedbackSessionName());
        }
    }

}
