package teammates.logic.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Config;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.TaskQueue;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.TaskWrapper;
import teammates.logic.external.GoogleCloudTasksService;
import teammates.logic.external.LocalTaskQueueService;
import teammates.logic.external.TaskQueueService;
import teammates.ui.request.FeedbackSessionRemindRequest;
import teammates.ui.request.SendEmailRequest;

/**
 * Allows for adding specific type of tasks to the task queue.
 */
public class TaskQueuer {

    private static final Logger log = Logger.getLogger();

    private static final TaskQueuer instance = new TaskQueuer();
    private final TaskQueueService service;

    TaskQueuer() {
        if (Config.IS_DEV_SERVER) {
            service = new LocalTaskQueueService();
        } else {
            service = new GoogleCloudTasksService();
        }
    }

    public static TaskQueuer inst() {
        return instance;
    }

    // The following methods are facades to the actual logic for adding tasks to the queue.
    // Using this method, the actual logic can still be black-boxed
    // while at the same time allowing this API to be mocked during test.

    private void addTask(String queueName, String workerUrl, Map<String, String> paramMap, Object requestBody) {
        addDeferredTask(queueName, workerUrl, paramMap, requestBody, 0);
    }

    void addDeferredTask(String queueName, String workerUrl, Map<String, String> paramMap, Object requestBody,
                         long countdownTime) {
        TaskWrapper task = new TaskWrapper(queueName, workerUrl, paramMap, requestBody);
        service.addDeferredTask(task, countdownTime);
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
     * Schedules for feedback session reminders (i.e. student/instructor has not submitted responses yet)
     * for the specified feedback session for the specified group of users.
     *
     * @param courseId the course ID of the feedback session
     * @param feedbackSessionName the name of the feedback session
     * @param usersToRemind the group of users to send the reminders to
     * @param requestingInstructorId the ID of the instructor who sends the reminder
     * @param isSendingCopyToInstructor the indicator of whether to send an email copy to the requesting instructor
     */
    public void scheduleFeedbackSessionRemindersForParticularUsers(String courseId, String feedbackSessionName,
                                                                   String[] usersToRemind,
                                                                   String requestingInstructorId,
                                                                   boolean isSendingCopyToInstructor) {
        FeedbackSessionRemindRequest remindRequest =
                new FeedbackSessionRemindRequest(courseId, feedbackSessionName, requestingInstructorId, usersToRemind,
                        isSendingCopyToInstructor);

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
     * @param requestingInstructorId the ID of the instructor who sends the reminder
     */
    public void scheduleFeedbackSessionResendPublishedEmail(String courseId, String feedbackSessionName,
            String[] usersToEmail, String requestingInstructorId) {
        FeedbackSessionRemindRequest remindRequest =
                new FeedbackSessionRemindRequest(courseId, feedbackSessionName, requestingInstructorId, usersToEmail, true);

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
            String instructorEmail, String courseId, boolean isRejoining) {
        Map<String, String> paramMap = new HashMap<>();
        if (inviterGoogleId != null) {
            paramMap.put(ParamsNames.INVITER_ID, inviterGoogleId);
        }
        paramMap.put(ParamsNames.INSTRUCTOR_EMAIL, instructorEmail);
        paramMap.put(ParamsNames.COURSE_ID, courseId);
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

    /**
     * Schedules for the search indexing of the instructor identified by {@code courseId} and {@code email}.
     *
     * @param courseId the course ID of the instructor
     * @param email the email of the instructor
     */
    public void scheduleInstructorForSearchIndexing(String courseId, String email) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        paramMap.put(ParamsNames.INSTRUCTOR_EMAIL, email);

        addTask(TaskQueue.SEARCH_INDEXING_QUEUE_NAME, TaskQueue.INSTRUCTOR_SEARCH_INDEXING_WORKER_URL,
                paramMap, null);
    }

    /**
     * Schedules for the search indexing of the account request identified by {@code email} and {@code institute}.
     *
     * @param email the email associated with the account request
     * @param institute the institute associated with the account request
     */
    public void scheduleAccountRequestForSearchIndexing(String email, String institute) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ParamsNames.INSTRUCTOR_EMAIL, email);
        paramMap.put(ParamsNames.INSTRUCTOR_INSTITUTE, institute);

        addTask(TaskQueue.SEARCH_INDEXING_QUEUE_NAME, TaskQueue.ACCOUNT_REQUEST_SEARCH_INDEXING_WORKER_URL,
                paramMap, null);
    }

    /**
     * Schedules for the search indexing of the student identified by {@code courseId} and {@code email}.
     *
     * @param courseId the course ID of the student
     * @param email the email of the student
     */
    public void scheduleStudentForSearchIndexing(String courseId, String email) {
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put(ParamsNames.COURSE_ID, courseId);
        paramMap.put(ParamsNames.STUDENT_EMAIL, email);

        addTask(TaskQueue.SEARCH_INDEXING_QUEUE_NAME, TaskQueue.STUDENT_SEARCH_INDEXING_WORKER_URL,
                paramMap, null);
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
