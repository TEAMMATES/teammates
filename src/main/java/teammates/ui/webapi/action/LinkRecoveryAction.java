package teammates.ui.webapi.action;

import teammates.common.exception.EmailSendingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;
import teammates.ui.webapi.output.ApiOutput;


/**
 * Action specifically created for confirming email and sending recovery link.
 */
public class LinkRecoveryAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    public void checkSpecificAccessControl() {
        // no specific access control needed.
    }

    @Override
    public ActionResult execute() {
        String requestedEmail = getNonNullRequestParamValue(Const.ParamsNames.RESTORE_EMAIL);
        boolean hasStudentsWithRestoreEmail = !logic.getAllStudentForEmail(requestedEmail).isEmpty();

        if (hasStudentsWithRestoreEmail) {

            EmailWrapper email = new EmailGenerator().generateLinkRecoveryEmail(requestedEmail);

            if (email != null) {
                try {
                    emailSender.sendEmail(email);
                } catch (EmailSendingException e) {
                    log.severe("Link recovery email failed to send.: "
                            + TeammatesException.toStringWithStackTrace(e));
                    return new JsonResult(new EmailRestoreResponse(EmailResponseResult.SUCCESS_BUT_EMAIL_FAIL_TO_SEND,
                            "Link recovery email failed to send"));
                }
            }

            return new JsonResult(new EmailRestoreResponse(EmailResponseResult.SUCCESS,
                    "An recovery link has just been sent to the given email."));
        } else {
            return new JsonResult(new EmailRestoreResponse(EmailResponseResult.FAIL,
                    "No response found with given email."));
        }
    }


    /**
     * The result of link recovery.
     */
    enum EmailResponseResult {
        SUCCESS,
        SUCCESS_BUT_EMAIL_FAIL_TO_SEND,
        FAIL
    }

    /**
     * The output format of {@link LinkRecoveryAction}.
     */
    public static class EmailRestoreResponse extends ApiOutput {
        private final EmailResponseResult result;
        private final String message;

        public EmailRestoreResponse(EmailResponseResult result, String message) {
            this.result = result;
            this.message = message;
        }

        public EmailResponseResult getResult() {
            return result;
        }

        public String getMessage() {
            return message;
        }
    }
}
