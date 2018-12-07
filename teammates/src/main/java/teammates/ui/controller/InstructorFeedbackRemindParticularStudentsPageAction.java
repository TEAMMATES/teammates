package teammates.ui.controller;

import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackAjaxStudentsListPageData;

public class InstructorFeedbackRemindParticularStudentsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertPostParamNotNull(Const.ParamsNames.COURSE_ID, courseId);

        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        Assumption.assertPostParamNotNull(Const.ParamsNames.FEEDBACK_SESSION_NAME, feedbackSessionName);

        FeedbackSessionAttributes fsa = logic.getFeedbackSession(feedbackSessionName, courseId);
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        gateKeeper.verifyAccessible(instructor, fsa, false);

        FeedbackSessionResponseStatus fsResponseStatus =
                logic.getFeedbackSessionResponseStatus(feedbackSessionName, courseId);

        InstructorFeedbackAjaxStudentsListPageData data = new InstructorFeedbackAjaxStudentsListPageData(
                account, sessionToken, fsResponseStatus, courseId, feedbackSessionName);

        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_FEEDBACK_AJAX_REMIND_PARTICULAR_STUDENTS_MODAL, data);
    }

}
