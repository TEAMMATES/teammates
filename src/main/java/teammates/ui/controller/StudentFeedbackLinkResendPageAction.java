package teammates.ui.controller;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.ui.pagedata.StudentFeedbackLinkResendPageData;

public class StudentFeedbackLinkResendPageAction extends Action {

    @Override
    public ActionResult execute() {
        String userToResend = getRequestParamValue(Const.ParamsNames.SUBMISSION_RESEND_LINK_USER);
        Assumption.assertNotNull(Const.ParamsNames.SUBMISSION_RESEND_LINK_USER, userToResend);

        AccountAttributes account = logic.getAccount(userToResend);
        if (account == null) {
            account = new AccountAttributes();
        }
        StudentFeedbackLinkResendPageData data = new StudentFeedbackLinkResendPageData();

        return createShowPageResult();
    }

}
