package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.ui.output.FeedbackSessionStatsData;

/**
 * Action: gets the response stats (submitted / total) of a feedback session.
 */
class GetSessionResponseStatsAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes fsa = getNonNullFeedbackSession(feedbackSessionName, courseId);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(instructor, fsa);
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes fsa = getNonNullFeedbackSession(feedbackSessionName, courseId);
        if (fsa == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Feedback session is not found"));
        }

        int expectedTotal = logic.getExpectedTotalSubmission(fsa);
        int actualTotal = logic.getActualTotalSubmission(fsa);
        FeedbackSessionStatsData output = new FeedbackSessionStatsData(actualTotal, expectedTotal);
        return new JsonResult(output);
    }

}
