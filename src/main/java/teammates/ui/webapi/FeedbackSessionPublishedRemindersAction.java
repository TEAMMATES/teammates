package teammates.ui.webapi;

/**
 * Cron job: schedules feedback session published emails to be sent.
 */
public class FeedbackSessionPublishedRemindersAction extends AutomatedServiceAction {

    @Override
    public JsonResult execute() {
        logic.enqueuePublishedEmailsForEligibleSessions();

        return new JsonResult("Successful");
    }

}
