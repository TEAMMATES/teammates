package teammates.ui.webapi;

/**
 * Cron job: schedules feedback session closed emails to be sent.
 */
public class FeedbackSessionClosedRemindersAction extends AutomatedServiceAction {

    @Override
    public JsonResult execute() {
        logic.enqueueClosedReminderEmailsForEligibleSessions();
        return new JsonResult("Successful");
    }

}
