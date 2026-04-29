package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;
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
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        Instructor instructor = sqlLogic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId());
        gateKeeper.verifyAccessible(
                instructor,
                feedbackSession,
                Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        UUID feedbackSessionId = getUuidRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_ID);

        FeedbackSession feedbackSession = sqlLogic.getFeedbackSession(feedbackSessionId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException("Feedback session not found");
        }

        if (!feedbackSession.isOpened()) {
            throw new InvalidOperationException("Reminder email could not be sent out "
                    + "as the feedback session is not open for submissions.");
        }

        FeedbackSessionRespondentRemindRequest remindRequest =
                getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
        UUID[] usersToRemind = remindRequest.getUsersToRemind();
        boolean isSendingCopyToInstructor = remindRequest.getIsSendingCopyToInstructor();

        // Generate reminder emails for specified users
        List<Student> studentsToRemindList = new ArrayList<>();
        List<Instructor> instructorsToRemindList = new ArrayList<>();
        Instructor instructorToNotify = isSendingCopyToInstructor
                ? sqlLogic.getInstructorByGoogleId(feedbackSession.getCourseId(), userInfo.getId())
                : null;

        for (UUID userId : usersToRemind) {
            User user = sqlLogic.getUser(userId);
            if (user == null) {
                throw new EntityNotFoundException("User with ID " + userId + " not found");
            }

            if (!Objects.equals(user.getCourseId(), feedbackSession.getCourseId())) {
                throw new InvalidOperationException("User with ID "
                    + userId + " does not belong to the same course as the feedback session");
            }

            if (user instanceof Student student) {
                studentsToRemindList.add(student);
            } else if (user instanceof Instructor instructor) {
                instructorsToRemindList.add(instructor);
            }
        }

        List<EmailWrapper> emails = sqlEmailGenerator.generateFeedbackSessionReminderEmails(
                feedbackSession, studentsToRemindList, instructorsToRemindList, instructorToNotify);

        taskQueuer.scheduleEmailsForPrioritySending(emails);

        return new JsonResult("Reminders sent");
    }

}
