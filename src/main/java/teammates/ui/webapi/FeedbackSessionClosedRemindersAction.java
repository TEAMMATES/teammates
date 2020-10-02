package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Cron job: schedules feedback session closed emails to be sent.
 */
class FeedbackSessionClosedRemindersAction extends AdminOnlyAction {

    private static final Logger log = Logger.getLogger();

    @Override
    JsonResult execute() {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsClosedWithinThePastHour();

        for (FeedbackSessionAttributes session : sessions) {
            List<EmailWrapper> emailsToBeSent = emailGenerator.generateFeedbackSessionClosedEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                logic.updateFeedbackSession(
                        FeedbackSessionAttributes
                                .updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                                .withSentClosedEmail(true)
                                .build());
            } catch (Exception e) {
                log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
            }
        }
        return new JsonResult("Successful");
    }

}
