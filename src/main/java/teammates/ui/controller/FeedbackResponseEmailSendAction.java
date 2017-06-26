package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.Logger;

public class FeedbackResponseEmailSendAction extends Action {

    private String recieverAddress;
    private Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() {
        // ensuring only our users can send us error feedback
        gateKeeper.verifyLoggedInUserPrivileges();
        String emailContent = getRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_CONTENT);
        String emailSubject = getRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT);
        log.severe("Subject: " + emailSubject);
        log.severe("Content: " + emailContent);
        log.severe("URL: " + requestUrl);
        return null;
    }

}
