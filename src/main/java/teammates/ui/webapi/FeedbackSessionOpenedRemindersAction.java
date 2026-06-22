package teammates.ui.webapi;

/**
 * Cron job: schedules feedback session opened emails to be sent.
 */
public class FeedbackSessionOpenedRemindersAction extends AutomatedServiceAction {

    @Override
    public JsonResult execute() {
        logic.enqueueOpenedReminderEmailsForEligibleSessions();
        return new JsonResult("Successful");
    }

}
