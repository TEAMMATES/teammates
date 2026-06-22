package teammates.ui.webapi;

/**
 * Cron job: schedules feedback session opening soon emails to be sent.
 */
public class FeedbackSessionOpeningSoonRemindersAction extends AutomatedServiceAction {
    @Override
    public JsonResult execute() {
        logic.enqueueOpeningSoonReminderEmailsForEligibleSessions();
        return new JsonResult("Successful");
    }
}
