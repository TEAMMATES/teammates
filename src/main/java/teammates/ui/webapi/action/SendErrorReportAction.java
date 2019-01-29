package teammates.ui.webapi.action;

import teammates.common.util.Const;
import teammates.common.util.Logger;

/**
 * Actions: sends an error report to the system admin.
 */
public class SendErrorReportAction extends Action {

    private static final Logger log = Logger.getLogger();
    private String content;
    private String subject;
    private String requestId;

    @Override
    protected AuthType getMinAuthLevel() {
        // Anyone can submit an error report
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        // Anyone can submit an error report
    }

    @Override
    public JsonResult execute() {
        content = getRequestBody();
        subject = getNonNullRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_EMAIL_SUBJECT);
        requestId = getNonNullRequestParamValue(Const.ParamsNames.ERROR_FEEDBACK_REQUEST_ID);

        // Severe logs will trigger email to the system admin
        log.severe(getUserErrorReportLogMessage());

        return new JsonResult("Error email successfully sent");
    }

    /**
     * Gets the user error report that will be sent to the system admin.
     */
    public String getUserErrorReportLogMessage() {
        String user = userInfo == null ? "Non-logged in user" : userInfo.id;
        return "====== USER FEEDBACK ABOUT ERROR ======" + System.lineSeparator()
                + "USER: " + user + System.lineSeparator()
                + "REQUEST ID: " + requestId + System.lineSeparator()
                + "SUBJECT: " + subject + System.lineSeparator()
                + "CONTENT: " + content;
    }

}
