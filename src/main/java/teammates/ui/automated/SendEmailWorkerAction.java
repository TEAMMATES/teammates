package teammates.ui.automated;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Task queue worker action: sends queued email.
 */
public class SendEmailWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    public void execute() {
        String emailSubject = getNonNullRequestParamValue(ParamsNames.EMAIL_SUBJECT);
        String emailContent = getNonNullRequestParamValue(ParamsNames.EMAIL_CONTENT);
        String emailSenderEmail = getNonNullRequestParamValue(ParamsNames.EMAIL_SENDER);
        String emailSenderName = getRequestParamValue(ParamsNames.EMAIL_SENDERNAME);
        String emailReceiver = getNonNullRequestParamValue(ParamsNames.EMAIL_RECEIVER);
        String emailReply = getNonNullRequestParamValue(ParamsNames.EMAIL_REPLY_TO_ADDRESS);

        EmailWrapper message = new EmailWrapper();
        message.setRecipient(emailReceiver);
        message.setSenderEmail(emailSenderEmail);
        if (emailSenderName != null) {
            message.setSenderName(emailSenderName);
        }
        message.setContent(emailContent);
        message.setSubject(emailSubject);
        message.setReplyTo(emailReply);

        try {
            emailSender.sendEmail(message);
        } catch (Exception e) {
            log.severe("Error while sending email via servlet: " + TeammatesException.toStringWithStackTrace(e));
            setForRetry();
        }
    }

}
