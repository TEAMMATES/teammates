package teammates.ui.webapi;

import teammates.common.util.Logger;
import teammates.ui.request.ErrorReportRequest;

/**
 * Actions: sends an error report to the system admin.
 */
class SendErrorReportAction extends Action {
    private static final Logger log = Logger.getLogger();

    @Override
    AuthType getMinAuthLevel() {
        // Anyone can submit an error report
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        // Anyone can submit an error report
    }

    @Override
    JsonResult execute() {
        ErrorReportRequest report = getAndValidateRequestBody(ErrorReportRequest.class);

        // Severe logs will trigger email to the system admin
        log.severe(getUserErrorReportLogMessage(report));

        return new JsonResult("Error report successfully sent");
    }

    /**
     * Gets the user error report that will be sent to the system admin.
     */
    String getUserErrorReportLogMessage(ErrorReportRequest report) {
        String user = userInfo == null ? "Non-logged in user" : userInfo.id;
        return "====== USER FEEDBACK ABOUT ERROR ======" + System.lineSeparator()
                + "USER: " + user + System.lineSeparator()
                + "REQUEST ID: " + report.getRequestId() + System.lineSeparator()
                + "SUBJECT: " + report.getSubject() + System.lineSeparator()
                + "CONTENT: " + report.getContent();
    }

}
