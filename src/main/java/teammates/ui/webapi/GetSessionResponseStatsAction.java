package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.FeedbackSessionStatsData;

/**
 * Action: gets the response stats (submitted / total) of a feedback session.
 */
public class GetSessionResponseStatsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        if (isCourseMigrated(courseId)) {
            FeedbackSession fs = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, fs);
        } else {
            FeedbackSessionAttributes fsa = getNonNullFeedbackSession(feedbackSessionName, courseId);
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(instructor, fsa);
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        if (isCourseMigrated(courseId)) {
            FeedbackSession fsa = getNonNullSqlFeedbackSession(feedbackSessionName, courseId);
            int expectedTotal = sqlLogic.getExpectedTotalSubmission(fsa);
            int actualTotal = sqlLogic.getActualTotalSubmission(fsa);
            FeedbackSessionStatsData output = new FeedbackSessionStatsData(actualTotal, expectedTotal);
            return new JsonResult(output);
        } else {
            FeedbackSessionAttributes fsa = getNonNullFeedbackSession(feedbackSessionName, courseId);
            int expectedTotal = logic.getExpectedTotalSubmission(fsa);
            int actualTotal = logic.getActualTotalSubmission(fsa);
            FeedbackSessionStatsData output = new FeedbackSessionStatsData(actualTotal, expectedTotal);
            return new JsonResult(output);
        }
    }

}
