package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;

/**
 * Cron job: schedules feedback session published emails to be sent.
 */
class FeedbackSessionPublishedRemindersAction extends AdminOnlyAction {

    @Override
    JsonResult execute() {
        List<FeedbackSessionAttributes> sessions =
                logic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        for (FeedbackSessionAttributes session : sessions) {
            taskQueuer.scheduleFeedbackSessionPublishedEmail(session.getCourseId(), session.getFeedbackSessionName());
        }
        return new JsonResult("Successful");
    }

}
