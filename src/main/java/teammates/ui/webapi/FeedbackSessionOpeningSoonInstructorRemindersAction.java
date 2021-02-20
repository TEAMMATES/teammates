package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;


/**
 * Cron job: schedules feedback session opening soon emails to be sent.
 */

public class FeedbackSessionOpeningSoonInstructorRemindersAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

    @Override
    JsonResult execute() {
        List<FeedbackSessionAttributes> sessions = logic.getFeedbackSessionsWhichNeedOpeningSoonEmailsToBeSent();

        for (FeedbackSessionAttributes session : sessions) {
            List<EmailWrapper> emailsToBeSent = emailGenerator.generateFeedbackSessionOpeningSoonEmails(session);
            try {
                taskQueuer.scheduleEmailsForSending(emailsToBeSent);
                logic.updateFeedbackSession(
                        FeedbackSessionAttributes
                                .updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                                .withSentOpeningSoonEmail(true)
                                .build());
            } catch (Exception e) {
                log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
            }
        }
        return new JsonResult("Successful");
    }

}
