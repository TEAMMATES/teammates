package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
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

        if (isCourseMigrated(courseId)) {
            FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);

            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(
                    instructor,
                    feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        } else {
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);

            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseId, userInfo.getId()),
                    feedbackSession,
                    Const.InstructorPermissions.CAN_MODIFY_SESSION);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (isCourseMigrated(courseId)) {
            FeedbackSession feedbackSession = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);

            if (!feedbackSession.isOpened()) {
                throw new InvalidOperationException("Reminder email could not be sent out "
                        + "as the feedback session is not open for submissions.");
            }

            FeedbackSessionRespondentRemindRequest remindRequest =
                    getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
            String[] usersToRemind = remindRequest.getUsersToRemind();
            boolean isSendingCopyToInstructor = remindRequest.getIsSendingCopyToInstructor();

            taskQueuer.scheduleFeedbackSessionRemindersForParticularUsers(courseId, feedbackSessionName,
                    usersToRemind, userInfo.getId(), isSendingCopyToInstructor);

            return new JsonResult("Reminders sent");
        } else {
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
            if (!feedbackSession.isOpened()) {
                throw new InvalidOperationException("Reminder email could not be sent out "
                        + "as the feedback session is not open for submissions.");
            }

            FeedbackSessionRespondentRemindRequest remindRequest =
                    getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
            String[] usersToRemind = remindRequest.getUsersToRemind();
            boolean isSendingCopyToInstructor = remindRequest.getIsSendingCopyToInstructor();

            taskQueuer.scheduleFeedbackSessionRemindersForParticularUsers(courseId, feedbackSessionName,
                    usersToRemind, userInfo.getId(), isSendingCopyToInstructor);

            return new JsonResult("Reminders sent");
        }
    }

}
