package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 *  Cron job: schedules feedback statistics count every minute.
 */
public class FeedbackResponseStatisticsCountVerifyAction extends AdminOnlyAction {
    private static final Logger log = Logger.getLogger();

    @Override
    public JsonResult execute() {

        /*         try {
        taskQueuer.scheduleEmailsForSending(emailsToBeSent);
        logic.updateFeedbackSession(
                FeedbackSessionAttributes
                        .updateOptionsBuilder(session.getFeedbackSessionName(), session.getCourseId())
                        .withSentPublishedEmail(false)
                        .build());
        } catch (Exception e) {
        log.severe("Unexpected error", e);
        }
        */
        // TO BE WRITTEN
        return new JsonResult("Successful");

    }
}
