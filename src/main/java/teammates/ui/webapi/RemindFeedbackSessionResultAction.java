package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.request.FeedbackSessionRespondentRemindRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Remind the student about the published result of a feedback session.
 */
public class RemindFeedbackSessionResultAction extends Action {
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
            gateKeeper.verifyAccessible(instructor, feedbackSession, Const.InstructorPermissions.CAN_MODIFY_SESSION);
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
            if (!feedbackSession.isPublished()) {
                throw new InvalidOperationException("Published email could not be resent "
                        + "as the feedback session is not published.");
            }

            FeedbackSessionRespondentRemindRequest remindRequest =
                    getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
            String[] usersToEmail = remindRequest.getUsersToRemind();

            taskQueuer.scheduleFeedbackSessionResendPublishedEmail(
                    courseId, feedbackSessionName, usersToEmail, userInfo.getId());

            return new JsonResult("Reminders sent");
        } else {
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
            if (!feedbackSession.isPublished()) {
                throw new InvalidOperationException("Published email could not be resent "
                        + "as the feedback session is not published.");
            }

            FeedbackSessionRespondentRemindRequest remindRequest =
                    getAndValidateRequestBody(FeedbackSessionRespondentRemindRequest.class);
            String[] usersToEmail = remindRequest.getUsersToRemind();

            taskQueuer.scheduleFeedbackSessionResendPublishedEmail(
                    courseId, feedbackSessionName, usersToEmail, userInfo.getId());

            return new JsonResult("Reminders sent");
        }
    }
}
