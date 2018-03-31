package teammates.ui.controller;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.FeedbackSessionStatsPageData;

public class FeedbackSessionStatsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        FeedbackSessionStatsPageData data = new FeedbackSessionStatsPageData(account, sessionToken);

        FeedbackSessionAttributes fsa = logic.getFeedbackSession(feedbackSessionName, courseId);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);

        gateKeeper.verifyAccessible(instructor, fsa, false);

        data.sessionDetails = logic.getFeedbackSessionDetails(feedbackSessionName, courseId);

        return createAjaxResult(data);
    }
}
