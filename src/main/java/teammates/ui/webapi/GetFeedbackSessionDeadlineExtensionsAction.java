package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.ui.output.FeedbackSessionDeadlineExtensionsData;

public class GetFeedbackSessionDeadlineExtensionsAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        gateKeeper.verifyLoggedInUserPrivileges(userInfo);
        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId()),
                feedbackSession, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSession feedbackSession = getNonNullFeedbackSession(feedbackSessionName, courseId);
        FeedbackSessionDeadlineExtensionsData response = new FeedbackSessionDeadlineExtensionsData(feedbackSession);
        return new JsonResult(response);
    }
  
}
