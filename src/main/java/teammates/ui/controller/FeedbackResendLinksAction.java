package teammates.ui.controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import teammates.common.exception.EmailSendingException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.logic.api.EmailGenerator;
import teammates.ui.pagedata.FeedbackResendLinksPageData;
import teammates.ui.pagedata.PageData;

public class FeedbackResendLinksAction extends Action {

    /**
     * This is a email regex to test whether the input is a valid email.
     */
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^(([^<>()[\\\\]\\\\.,;:\\s@\"]+(\\.[^<>()[\\\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@"
                    + "((\\\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\\\])|"
                    + "(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$", Pattern.CASE_INSENSITIVE);
    private static final Logger log = Logger.getLogger();

    @Override
    protected void authenticateUser() {
        // This feature does not require to authenticate
    }

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        try {
            String userEmailToResend = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
            Assumption.assertNotNull(userEmailToResend);
            statusToAdmin = "Action Student Clicked Request Resending Links"
                    + "<br>Email: " + userEmailToResend;
            boolean isValid = isValidEmailAddress(userEmailToResend);

            if (isValid) {
                EmailWrapper email = new EmailGenerator().generateFeedbackSessionResendLinksEmail(userEmailToResend);
                emailSender.sendEmail(email);
                statusToUser.add(new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_ACCESS_LINKS_RESENT,
                        StatusMessageColor.SUCCESS));
            } else {
                statusToUser.add(new StatusMessage(Const.StatusMessages
                        .FEEDBACK_SESSION_RESEND_ACCESS_LINKS_INVALID_EMAIL, StatusMessageColor.DANGER));
            }

            PageData data = new FeedbackResendLinksPageData(account, sessionToken, isValid);

            return createAjaxResult(data);
        } catch (EmailSendingException e) {
            log.severe("Email of feedback session links failed to send: "
                    + TeammatesException.toStringWithStackTrace(e));
        }

        return null;
    }

    /**
     * Verify whether the input email address is valid.
     * @param emailAddress a string containing the email address of the user
     * @return a boolean indicating whether the input email is valid
     */
    public boolean isValidEmailAddress(String emailAddress) {
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailAddress);
        return matcher.find();
    }
}
