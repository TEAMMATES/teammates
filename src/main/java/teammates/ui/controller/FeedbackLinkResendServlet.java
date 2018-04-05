package teammates.ui.controller;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.EmailSendingException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.logic.api.EmailGenerator;
import teammates.logic.api.EmailSender;

public class FeedbackLinkResendServlet extends HttpServlet {

    /**
     * This is a email regex to test whether the input is a valid email.
     */
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^(([^<>()[\\\\]\\\\.,;:\\s@\"]+(\\.[^<>()[\\\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@" +
                    "((\\\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\\\])|" +
                    "(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$", Pattern.CASE_INSENSITIVE);
    private static final Logger log = Logger.getLogger();

    private EmailSender emailSender;
    private StatusMessage statusMessage;

    @Override
    public final void doGet(HttpServletRequest req, HttpServletResponse resp) {
        this.doPost(req, resp);
    }

    @Override
    public final void doPost(HttpServletRequest req, HttpServletResponse resp) {
        try {
            String userEmailToResend = req.getParameter(Const.ParamsNames.STUDENT_EMAIL);
            EmailWrapper email = new EmailGenerator().generateFeedbackSessionResendEmail(userEmailToResend);
            setEmailSender(new EmailSender());

            if (isValidEmailAddress(userEmailToResend) && email.getContent().length() != 0) {
                emailSender.sendEmail(email);
                statusMessage = new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_LINK_RESENT,
                        StatusMessageColor.SUCCESS);
            } else if (!isValidEmailAddress(userEmailToResend)) {
                statusMessage =
                        new StatusMessage(Const.StatusMessages.FEEDBACK_SESSION_RESENDING_LINKS_INVALID_EMAIL,
                                StatusMessageColor.DANGER);
            } else {
                statusMessage = new StatusMessage(
                        Const.StatusMessages.FEEDBACK_SESSION_RESENDING_LINKS_NONE_IN_RECENT_ONE_MONTH,
                        StatusMessageColor.DANGER);
            }
            resp.getWriter().write(statusMessage.getText());
            resp.sendRedirect(Const.ViewURIs.FEEDBACK_LINKS_RESEND_SUCCESS);
        } catch (EmailSendingException e) {
            log.severe("Email of feedback session links failed to send: "
                    + TeammatesException.toStringWithStackTrace(e));
        } catch (IOException e) {
            log.severe("Email of feedback session links failed to send: "
                    + TeammatesException.toStringWithStackTrace(e));
        }
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
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
