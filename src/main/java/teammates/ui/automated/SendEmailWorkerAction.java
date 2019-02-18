package teammates.ui.automated;

import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * Task queue worker action: sends queued email.
 */
public class SendEmailWorkerAction extends AutomatedAction {

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

        EmailSendingStatus status = emailSender.sendEmail(message);
        if (!status.isSuccess()) {
            setForRetry();
        }
    }

}
