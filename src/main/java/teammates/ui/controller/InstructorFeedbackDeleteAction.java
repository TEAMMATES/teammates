package teammates.ui.controller;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class InstructorFeedbackDeleteAction extends Action {

    @Override
    protected ActionResult execute() {

        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);

        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        if (nextUrl == null) {
            nextUrl = Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE;
        }

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false,
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        try {
            logic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);

            if (nextUrl.equals(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE)) {
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_MOVED_TO_RECYCLE_BIN,
                        StatusMessageColor.SUCCESS));
            } else {
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_MOVED_TO_RECYCLE_BIN_FROM_HOMEPAGE,
                        StatusMessageColor.SUCCESS));
            }
            statusToAdmin = "Feedback Session <span class=\"bold\">[" + feedbackSessionName + "]</span> "
                    + "from Course: <span class=\"bold\">[" + courseId + " deleted.";
        } catch (Exception e) {
            setStatusForException(e);
        }

        return createRedirectResult(nextUrl);
    }

}
