package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

public class InstructorFeedbackUnpublishAction extends InstructorFeedbacksPageAction {
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);

        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, courseId);
        Assumption.assertNotNull(Const.StatusCodes.NULL_PARAMETER, feedbackSessionName);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        FeedbackSessionAttributes session = logic.getFeedbackSession(feedbackSessionName, courseId);
        boolean isCreatorOnly = false;

        new GateKeeper().verifyAccessible(
                instructor, session, isCreatorOnly, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        try {
            logic.unpublishFeedbackSession(feedbackSessionName, courseId);
            statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_UNPUBLISHED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Feedback Session <span class=\"bold\">(" + feedbackSessionName + ")</span> "
                            + "for Course <span class=\"bold\">[" + courseId + "]</span> unpublished.";
        } catch (InvalidParametersException e) {
            setStatusForException(e);
        }

        nextUrl = nextUrl == null ? Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE : nextUrl;

        return createRedirectResult(nextUrl);
    }
}
