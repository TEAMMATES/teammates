package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionSubmittedGiverSet;

/**
 * Get a set of givers that has given at least one response in the feedback session.
 */
public class GetFeedbackSessionSubmittedGiverSetAction extends Action {

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

            gateKeeper.verifyAccessible(instructor, feedbackSession);
        } else {
            FeedbackSessionAttributes feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());

            gateKeeper.verifyAccessible(instructor, feedbackSession);
        }
    }

    @Override
    public JsonResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (isCourseMigrated(courseId)) {
            FeedbackSessionSubmittedGiverSet output = new FeedbackSessionSubmittedGiverSet(
                    sqlLogic.getGiverSetThatAnsweredFeedbackSession(feedbackSessionName, courseId)
            );

            return new JsonResult(output);
        } else {
            FeedbackSessionSubmittedGiverSet output =
                    new FeedbackSessionSubmittedGiverSet(
                    logic.getGiverSetThatAnswerFeedbackSession(courseId, feedbackSessionName));

            return new JsonResult(output);
        }
    }

}
