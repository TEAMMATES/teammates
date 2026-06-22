package teammates.ui.webapi;

/**
 * Cron job: schedules feedback session closing soon emails to be sent.
 */
public class FeedbackSessionClosingSoonRemindersAction extends AutomatedServiceAction {

    @Override
    public JsonResult execute() {
        logic.enqueueClosingSoonReminderEmailsForEligibleSessions();
        return new JsonResult("Successful");
    }
}
