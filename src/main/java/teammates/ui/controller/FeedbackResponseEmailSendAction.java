package teammates.ui.controller;

import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.PageData;

public class FeedbackResponseEmailSendAction extends Action {

    private String recieverAddress;
    private Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() {
        boolean isUserLoggedOn = true;
        // ensuring only our users can send us error feedback
        try {
            gateKeeper.verifyLoggedInUserPrivileges();
        } catch (UnauthorizedAccessException e) {
            isUserLoggedOn = false;
        }
        String emailContent = getRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_CONTENT);
        String emailSubject = getRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT);
        log.severe("Subject: " + emailSubject);
        log.severe("Content: " + emailContent);
        log.severe("URL: " + requestUrl);
        PageData data = new PageData(account, sessionToken);
        statusToUser.add(new StatusMessage(
                isUserLoggedOn ? Const.StatusMessages.ERROR_FEEDBACK_SUBMIT_SUCCESS
                        : Const.StatusMessages.ERROR_FEEDBACK_SUBMIT_FAILED, 
                isUserLoggedOn ? StatusMessageColor.SUCCESS : StatusMessageColor.DANGER));
        return createAjaxResult(data);
    }

}
