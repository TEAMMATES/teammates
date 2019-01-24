package teammates.ui.newcontroller;

import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;

/**
 * Action: gets the response stats (submitted / total) of a feedback session.
 */
public class GetSessionResponseStatsAction extends Action {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (userInfo.isAdmin()) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        FeedbackSessionAttributes fsa = logic.getFeedbackSession(feedbackSessionName, courseId);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(instructor, fsa);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        try {
            FeedbackSessionDetailsBundle fsdb = logic.getFeedbackSessionDetails(feedbackSessionName, courseId);
            FeedbackSessionStats output = new FeedbackSessionStats(fsdb.stats.submittedTotal, fsdb.stats.expectedTotal);
            return new JsonResult(output);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

    /**
     * Output format for {@link GetSessionResponseStatsAction}.
     */
    public static class FeedbackSessionStats extends ActionResult.ActionOutput {

        private final int submittedTotal;
        private final int expectedTotal;

        FeedbackSessionStats(int submittedTotal, int expectedTotal) {
            this.submittedTotal = submittedTotal;
            this.expectedTotal = expectedTotal;
        }

        public int getSubmittedTotal() {
            return submittedTotal;
        }

        public int getExpectedTotal() {
            return expectedTotal;
        }

    }

}
