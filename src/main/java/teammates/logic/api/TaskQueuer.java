package teammates.logic.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Config;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.TaskWrapper;
import teammates.logic.external.GoogleCloudTasksService;
import teammates.logic.external.LocalTaskQueueService;
import teammates.logic.external.TaskQueueService;
import teammates.ui.request.SendEmailRequest;

/**
 * Allows for adding specific type of tasks to the task queue.
 */
public class TaskQueuer {

    private static final Logger log = Logger.getLogger();

    private static final TaskQueuer instance = new TaskQueuer();
    private final TaskQueueService service;

    TaskQueuer() {
        switch (Config.TASKQUEUE_SERVICE) {
        case "local":
            service = new LocalTaskQueueService();
            break;
        case "google-cloud-tasks":
            service = new GoogleCloudTasksService();
            break;
        default:
            throw new IllegalStateException("Unknown app.taskqueue.service value: " + Config.TASKQUEUE_SERVICE
                    + ". Acceptable values: local, google-cloud-tasks.");
        }
    }

    public static TaskQueuer inst() {
        return instance;
    }

    // The following methods are facades to the actual logic for adding tasks to the queue.
    // Using this method, the actual logic can still be black-boxed
    // while at the same time allowing this API to be mocked during test.

    void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap, Object requestBody,
                         long countdownTime) {
        TaskWrapper task = new TaskWrapper(queueName, workerUrl, paramMap, requestBody);
        service.addDeferredTask(task, countdownTime);
    }

    // The following methods are the actual API methods to be used by the client classes

    /**
     * Schedules for the given list of emails to be sent via priority queue (user-triggered, immediate).
     *
     * @param emails the list of emails to be sent
     */
    public void scheduleEmailsForPrioritySending(List<EmailWrapper> emails) {
        if (emails.isEmpty()) {
            return;
        }

        // Send priority emails immediately with minimal delay
        long numberOfEmailsSent = 0L;
        for (EmailWrapper email : emails) {
            scheduleEmailForPrioritySending(email, numberOfEmailsSent * 100); // 100ms delay between emails
            numberOfEmailsSent++;
        }
    }

    private void scheduleEmailForPrioritySending(EmailWrapper email, long emailDelayTimer) {
        try {
            SendEmailRequest request = new SendEmailRequest(email);

            addDeferredTask(TaskQueue.PRIORITY_EMAIL_QUEUE_NAME, TaskQueue.SEND_EMAIL_WORKER_URL,
                            new HashMap<>(), request, emailDelayTimer);
        } catch (Exception e) {
            log.severe("Error when adding email to priority queue: " + e.getMessage());
        }
    }

    /**
     * Schedules for the given list of emails to be sent via standard queue (system/cron, deferred).
     *
     * @param emails the list of emails to be sent
     */
    public void scheduleEmailsForSending(List<EmailWrapper> emails) {
        if (emails.isEmpty()) {
            return;
        }

        // Equally spread out the emails to be sent over 1 hour
        // Sets interval to a maximum of 5 seconds if the interval is too large
        int oneHourInMillis = 60 * 60 * 1000;
        int emailIntervalMillis = Math.min(5000, oneHourInMillis / emails.size());

        long numberOfEmailsSent = 0L;
        for (EmailWrapper email : emails) {
            long emailDelayTimer = numberOfEmailsSent * emailIntervalMillis;
            scheduleEmailForSending(email, emailDelayTimer);
            numberOfEmailsSent++;
        }
    }

    private void scheduleEmailForSending(EmailWrapper email, long emailDelayTimer) {
        try {
            SendEmailRequest request = new SendEmailRequest(email);

            addDeferredTask(TaskQueue.SEND_EMAIL_QUEUE_NAME, TaskQueue.SEND_EMAIL_WORKER_URL,
                            new HashMap<>(), request, emailDelayTimer);
        } catch (Exception e) {
            String emailSubject = email.getSubject();
            String emailSenderName = email.getSenderName();
            String emailSender = email.getSenderEmail();
            String emailReceiver = email.getRecipient();
            String emailReplyToAddress = email.getReplyTo();

            log.severe("Error when adding email to task queue: " + e.getMessage() + "\n"
                       + "Email sender: " + emailSender + "\n"
                       + "Email sender name: " + emailSenderName + "\n"
                       + "Email receiver: " + emailReceiver + "\n"
                       + "Email subject: " + emailSubject + "\n"
                       + "Email reply-to address: " + emailReplyToAddress);
        }
    }

}
