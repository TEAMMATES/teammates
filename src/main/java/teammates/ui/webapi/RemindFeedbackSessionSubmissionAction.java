package teammates.ui.webapi;

import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Remind students about the feedback submission.
 */
public class RemindFeedbackSessionSubmissionAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(
                instructor,
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

        if (!feedbackSession.isOpened()) {
            throw new InvalidOperationException("Reminder email could not be sent out "
                    + "as the feedback session is not open for submissions.");
        }

        FeedbackSessionRespondentRemindRequest remindRequest =
                getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
        String[] usersToRemind = remindRequest.getUsersToRemind();
        boolean isSendingCopyToInstructor = remindRequest.getIsSendingCopyToInstructor();

        // Generate reminder emails for specified users
        List<Student> studentsToRemindList = new java.util.ArrayList<>();
        List<Instructor> instructorsToRemindList = new java.util.ArrayList<>();
        Instructor instructorToNotify = isSendingCopyToInstructor
                ? sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId())
                : null;

        for (String userEmail : usersToRemind) {
            Student student = sqlLogic.getStudentForEmail(courseId, userEmail);
            if (student != null) {
                studentsToRemindList.add(student);
            }

            Instructor instructor = sqlLogic.getInstructorForEmail(courseId, userEmail);
            if (instructor != null) {
                instructorsToRemindList.add(instructor);
            }
        }

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionReminderEmails(
                feedbackSession, studentsToRemindList, instructorsToRemindList, instructorToNotify);

        // Queue to priority queue for immediate sending (user-triggered)
        taskQueuer.scheduleEmailsForPrioritySending(emails);

        return new JsonResult("Reminders sent");
    }

}
