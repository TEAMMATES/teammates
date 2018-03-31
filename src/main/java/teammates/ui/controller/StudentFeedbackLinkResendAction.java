package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

public class StudentFeedbackLinkResendAction extends Action {

    @Override
    public ActionResult execute() {
        String userEmailToResend = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        //Todo: validate email address
        // if (nextUrl == null) {
        //    nextUrl = Const.ActionURIs.STUDENT_FEEDBACK_LINK_RESEND_PAGE;
        //}

        String userToResend = getRequestParamValue(Const.ParamsNames.SUBMISSION_RESEND_LINK_USER);
        taskQueuer.scheduleFeedbackSessionResendEmail(userToResend);

        statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_LINK_RESENT, StatusMessageColor.SUCCESS));
        statusToAdmin = "Email sent out to the user: " + "<br>" + userToResend;

        return createRedirectResult(Const.ActionURIs.STUDENT_FEEDBACK_LINK_RESEND);
    }

}
