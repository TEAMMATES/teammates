package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * Task queue worker action: sends queued email.
 */
public class SendEmailWorkerAction extends AdminOnlyAction {

    @Override
    public JsonResult execute() {
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
            // Set an arbitrary retry code outside of the range 200-299 so GAE will automatically retry upon failure
            return new JsonResult("Failure", HttpStatus.SC_BAD_GATEWAY);
        }
        return new JsonResult("Successful");
    }

}
