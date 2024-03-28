package teammates.ui.webapi;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.RequestTracer;
import teammates.storage.sqlentity.FeedbackSession;

/**
 * Cron job: schedules feedback session published emails to be sent.
 */
public class FeedbackSessionPublishedRemindersAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
        List<FeedbackSessionAttributes> sessionAttributes =
                logic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        for (FeedbackSessionAttributes session : sessionAttributes) {
            // If course has been migrated, use sql email logic instead.
            if (isCourseMigrated(session.getCourseId())) {
                continue;
            }

            RequestTracer.checkRemainingTime();
            taskQueuer.scheduleFeedbackSessionPublishedEmail(session.getCourseId(), session.getFeedbackSessionName());
        }

        List<FeedbackSession> sessions = sqlLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
        for (FeedbackSession session : sessions) {
            RequestTracer.checkRemainingTime();
            taskQueuer.scheduleFeedbackSessionPublishedEmail(session.getCourse().getId(), session.getName());
        }

        return new JsonResult("Successful");
    }

}
