package teammates.logic.email;

import java.util.Collections;
import java.util.List;

import teammates.common.util.EmailWrapper;
import teammates.logic.api.TaskQueuer;

/**
 * Handles queueing of outbound emails.
 */
public class EmailQueueService {

    private static final EmailQueueService instance = new EmailQueueService(TaskQueuer.inst());

    private final TaskQueuer taskQueuer;

    EmailQueueService(TaskQueuer taskQueuer) {
        this.taskQueuer = taskQueuer;
    }

    public static EmailQueueService inst() {
        return instance;
    }

    public static EmailQueueService withTaskQueuer(TaskQueuer taskQueuer) {
        return new EmailQueueService(taskQueuer);
    }

    /**
     * Enqueues the given email for priority sending. Priority emails are sent in a
     * separate queue from bulk emails and are meant for time-sensitive
     * notifications.
     */
    public void enqueuePriority(EmailWrapper email) {
        enqueuePriority(Collections.singletonList(email));
    }

    /**
     * Enqueues the given emails for priority sending. Priority emails are sent in a
     * separate queue from bulk emails and are meant for time-sensitive
     * notifications.
     */
    public void enqueuePriority(List<EmailWrapper> emails) {
        taskQueuer.scheduleEmailsForPrioritySending(emails);
    }

    /**
     * Enqueues the given email for standard sending. Standard emails are sent in a
     * separate queue from priority emails and are meant for non-time-sensitive
     * notifications.
     */
    public void enqueueStandard(EmailWrapper email) {
        enqueueStandard(Collections.singletonList(email));
    }

    /**
     * Enqueues the given emails for standard sending. Standard emails are sent in a
     * separate queue from priority emails and are meant for non-time-sensitive
     * notifications.
     */
    public void enqueueStandard(List<EmailWrapper> emails) {
        taskQueuer.scheduleEmailsForSending(emails);
    }

}
