package teammates.ui.automated;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;

/**
 * Task queue worker action: sends queued email.
 */
public class SendEmailWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        String emailSubject = getRequestParamValue(ParamsNames.EMAIL_SUBJECT);
        Assumption.assertPostParamNotNull(ParamsNames.EMAIL_SUBJECT, emailSubject);

        String emailContent = getRequestParamValue(ParamsNames.EMAIL_CONTENT);
        Assumption.assertPostParamNotNull(ParamsNames.EMAIL_CONTENT, emailContent);

        String emailSenderEmail = getRequestParamValue(ParamsNames.EMAIL_SENDER);
        Assumption.assertPostParamNotNull(ParamsNames.EMAIL_SENDER, emailSenderEmail);

        String emailSenderName = getRequestParamValue(ParamsNames.EMAIL_SENDERNAME);

        String emailReceiver = getRequestParamValue(ParamsNames.EMAIL_RECEIVER);
        Assumption.assertPostParamNotNull(ParamsNames.EMAIL_RECEIVER, emailReceiver);

        String emailReply = getRequestParamValue(ParamsNames.EMAIL_REPLY_TO_ADDRESS);
        Assumption.assertPostParamNotNull(ParamsNames.EMAIL_REPLY_TO_ADDRESS, emailReply);

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
