package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackAjaxStudentsListPageData;

public class InstructorFeedbackResendPublishedEmailPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        String feedbackSessionName = getNonNullRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);

        FeedbackSessionAttributes fsa = logic.getFeedbackSession(feedbackSessionName, courseId);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, fsa, false);

        FeedbackSessionResponseStatus fsResponseStatus =
                logic.getFeedbackSessionResponseStatus(feedbackSessionName, courseId);

        InstructorFeedbackAjaxStudentsListPageData data = new InstructorFeedbackAjaxStudentsListPageData(
                account, sessionToken, fsResponseStatus, courseId, feedbackSessionName);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_AJAX_RESEND_PUBLISHED_EMAIL_MODAL, data);
    }

}
