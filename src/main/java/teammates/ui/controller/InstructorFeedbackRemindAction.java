package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.ui.pagedata.InstructorFeedbackRemindAjaxPageData;

public class InstructorFeedbackRemindAction extends Action {

    @Override
    protected ActionResult execute() {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String feedbackSessionName = getRequestParamValue(Const.ParamsNames.FEEDBACK_SESSION_NAME);
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);

        if (nextUrl == null) {
            nextUrl = Const.ActionURIs.INSTRUCTOR_FEEDBACKS_PAGE;
        }

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getFeedbackSession(feedbackSessionName, courseId),
                false, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);

        InstructorFeedbackRemindAjaxPageData data = new InstructorFeedbackRemindAjaxPageData(account);

        taskQueuer.scheduleFeedbackSessionReminders(courseId, feedbackSessionName);
        data.ajaxStatus = Const.StatusMessages.FEEDBACK_SESSION_REMINDERSSENT;

        statusToAdmin = "Email sent out to all students who have not completed "
                      + "Feedback Session <span class=\"bold\">(" + feedbackSessionName
                      + ")</span> " + "of Course <span class=\"bold\">[" + courseId + "]</span>";

        return createAjaxResult(data);
    }

}
