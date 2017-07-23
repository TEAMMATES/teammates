package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.PageData;

public class ErrorUserReportLogAction extends Action {

    private static final Logger log = Logger.getLogger();
    private String emailSubject;
    private String emailContent;

    @Override
    protected ActionResult execute() {
        emailContent = getRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_CONTENT);
        emailSubject = getRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT);
        log.severe(getUserErrorReportLogMessage());
        PageData data = new PageData(account, sessionToken);
        statusToUser.add(new StatusMessage(Const.StatusMessages.ERROR_FEEDBACK_SUBMIT_SUCCESS,
                StatusMessageColor.SUCCESS));
        return createAjaxResult(data);
    }

    /**
     * Returns the formatted log message with {@code emailSubject} & {@code emailContent}.
     */
    public String getUserErrorReportLogMessage() {
        return "====== USER FEEDBACK ABOUT ERROR ====== \n"
                + "ACCOUNT DETAILS: " + account.toString() + "\n"
                + "SUBJECT: " + emailSubject + "\n"
                + "FEEDBACK: " + emailContent;
    }

}
