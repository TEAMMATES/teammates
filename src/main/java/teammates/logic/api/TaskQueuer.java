package teammates.logic.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.TaskWrapper;
import teammates.logic.core.TaskQueuesLogic;
import teammates.ui.request.FeedbackSessionRemindRequest;
import teammates.ui.request.SendEmailRequest;

/**
 * Allows for adding specific type of tasks to the task queue.
 */
public class TaskQueuer {

    private static final Logger log = Logger.getLogger();

    // The following methods are facades to the actual logic for adding tasks to the queue.
    // Using this method, the actual logic can still be black-boxed
    // while at the same time allowing this API to be mocked during test.

    protected void addTask(String queueName, String workerUrl, Map<String, String> paramMap, Object requestBody) {
        TaskWrapper task = new TaskWrapper(queueName, workerUrl, paramMap, requestBody);
        new TaskQueuesLogic().addTask(task);
    }

    protected void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap, Object requestBody,
                                   long countdownTime) {
        TaskWrapper task = new TaskWrapper(queueName, workerUrl, paramMap, requestBody);
        new TaskQueuesLogic().addDeferredTask(task, countdownTime);
    }

    /**
     * Gets the tasks added to the queue.
     * This method is used only for testing, where it is overridden.
     *
     * @throws UnsupportedOperationException if used in production, where it is not meant to be
     */
    public List<TaskWrapper> getTasksAdded() {
        throw new UnsupportedOperationException("Method is used only for testing");
    }

    /**
     * Gets the number of tasks added for each queue name.
     * This method is used only for testing, where it is overridden.
     *
     * @throws UnsupportedOperationException if used in production, where it is not meant to be
     */
    public Map<String, Integer> getNumberOfTasksAdded() {
        throw new UnsupportedOperationException("Method is used only for testing");
    }

    // The following methods are the actual API methods to be used by the client classes

    /**
     * Schedules for feedback session reminders (i.e. student has not submitted responses yet)
     * for the specified feedback session.
     *
     * @param courseId the course ID of the feedback session
     * @param feedbackSessionName the name of the feedback session
     */
    public void scheduleFeedbackSessionReminders(String courseId, String feedbackSessionName,
                                                 String googleIdOfRequestingInstructor) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ParamsNames.INSTRUCTOR_ID, googleIdOfRequestingInstructor);
        paramMap.put(ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);
        paramMap.put(ParamsNames.COURSE_ID, courseId);

        addTask(TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_QUEUE_NAME,
                TaskQueue.FEEDBACK_SESSION_REMIND_EMAIL_WORKER_URL, paramMap, null);
    }

    /**
     * Schedules for feedback session reminders (i.e. student has not submitted responses yet)
     * for the specified feedback session for the specified group of users.
     *
     * @param courseId the course ID of the feedback session
     * @param feedbackSessionName the name of the feedback session
     * @param usersToRemind the group of users to send the reminders to
     */
    public void scheduleFeedbackSessionRemindersForParticularUsers(String courseId, String feedbackSessionName,
                                                                   String[] usersToRemind,
                                                                   String requestingInstructorId) {
        FeedbackSessionRemindRequest remindRequest =
                new FeedbackSessionRemindRequest(courseId, feedbackSessionName, requestingInstructorId, usersToRemind);

        addTask(TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_QUEUE_NAME,
                TaskQueue.FEEDBACK_SESSION_REMIND_PARTICULAR_USERS_EMAIL_WORKER_URL, new HashMap<>(), remindRequest);
    }

    /**
     * Schedules for feedback session published email to be sent.
     *
     * @param courseId the course ID of the feedback session
     * @param feedbackSessionName the name of the feedback session
     */
    public void scheduleFeedbackSessionPublishedEmail(String courseId, String feedbackSessionName) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        paramMap.put(ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        addTask(TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_QUEUE_NAME,
                TaskQueue.FEEDBACK_SESSION_PUBLISHED_EMAIL_WORKER_URL, paramMap, null);
    }

    /**
     * Schedules for feedback session publication reminders
     * for the specified feedback session for the specified group of users.
     *
     * @param courseId the course ID of the feedback session
     * @param feedbackSessionName the name of the feedback session
     * @param usersToEmail the group of users to send the reminders to
     */
    public void scheduleFeedbackSessionResendPublishedEmail(String courseId, String feedbackSessionName,
            String[] usersToEmail) {
        FeedbackSessionRemindRequest remindRequest =
                new FeedbackSessionRemindRequest(courseId, feedbackSessionName, null, usersToEmail);

        addTask(TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_QUEUE_NAME,
                TaskQueue.FEEDBACK_SESSION_RESEND_PUBLISHED_EMAIL_WORKER_URL, new HashMap<>(), remindRequest);
    }

    /**
     * Schedules for feedback session unpublished email to be sent.
     *
     * @param courseId the course ID of the feedback session
     * @param feedbackSessionName the name of the feedback session
     */
    public void scheduleFeedbackSessionUnpublishedEmail(String courseId, String feedbackSessionName) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        paramMap.put(ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        addTask(TaskQueue.FEEDBACK_SESSION_UNPUBLISHED_EMAIL_QUEUE_NAME,
                TaskQueue.FEEDBACK_SESSION_UNPUBLISHED_EMAIL_WORKER_URL, paramMap, null);
    }

    /**
     * Schedules for course registration to be sent to the specified instructor.
     *
     * @param inviterGoogleId googleId of instructor or administrator who sends the invitation
     * @param courseId the target course ID
     * @param instructorEmail the email address of the invited instructor
     */
    public void scheduleCourseRegistrationInviteToInstructor(String inviterGoogleId,
            String instructorEmail, String courseId, String institute, boolean isRejoining) {
        Map<String, String> paramMap = new HashMap<>();
        if (inviterGoogleId != null) {
            paramMap.put(ParamsNames.INVITER_ID, inviterGoogleId);
        }
        paramMap.put(ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        if (institute != null) {
            paramMap.put(ParamsNames.INSTRUCTOR_INSTITUTION, institute);
        }
        paramMap.put(ParamsNames.IS_INSTRUCTOR_REJOINING, String.valueOf(isRejoining));

        addTask(TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_QUEUE_NAME,
                TaskQueue.INSTRUCTOR_COURSE_JOIN_EMAIL_WORKER_URL, paramMap, null);
    }

    /**
     * Schedules for course registration to be sent to the specified student.
     *
     * @param courseId the target course ID
     * @param studentEmail the email address of the student
     */
    public void scheduleCourseRegistrationInviteToStudent(String courseId, String studentEmail, boolean isRejoining) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        paramMap.put(ParamsNames.STUDENT_EMAIL, studentEmail);
        paramMap.put(ParamsNames.IS_STUDENT_REJOINING, String.valueOf(isRejoining));

        addTask(TaskQueue.STUDENT_COURSE_JOIN_EMAIL_QUEUE_NAME,
                TaskQueue.STUDENT_COURSE_JOIN_EMAIL_WORKER_URL, paramMap, null);
    }

    /**
     * Schedules for the given list of emails to be sent.
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

        int numberOfEmailsSent = 0;
        for (EmailWrapper email : emails) {
            long emailDelayTimer = (long) numberOfEmailsSent * (long) emailIntervalMillis;
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
