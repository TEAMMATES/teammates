package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.PageData;

public class ErrorUserReportLogAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() {
        String emailContent = getRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_CONTENT);
        String emailSubject = getRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT);
        log.severe("====== USER FEEDBACK ABOUT ERROR ====== \n"
                + "ACCOUNT DETAILS: " + account.toString() + "\n"
                + "SUBJECT: " + emailSubject + "\n"
                + "FEEDBACK: " + emailContent);
        PageData data = new PageData(account, sessionToken);
        statusToUser.add(new StatusMessage(Const.StatusMessages.ERROR_FEEDBACK_SUBMIT_SUCCESS,
                StatusMessageColor.SUCCESS));
        return createAjaxResult(data);
    }

}
