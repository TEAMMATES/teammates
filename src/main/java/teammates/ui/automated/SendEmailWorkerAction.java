package teammates.ui.automated;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;

/**
 * Task queue worker action: sends queued email.
 */
public class SendEmailWorkerAction extends AutomatedAction {
    
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
        Assumption.assertNotNull(emailSubject);
        
        String emailContent = getRequestParamValue(ParamsNames.EMAIL_CONTENT);
        Assumption.assertNotNull(emailContent);
        
        String emailSenderEmail = getRequestParamValue(ParamsNames.EMAIL_SENDER);
        Assumption.assertNotNull(emailSenderEmail);
        
        String emailSenderName = getRequestParamValue(ParamsNames.EMAIL_SENDERNAME);
        
        String emailReceiver = getRequestParamValue(ParamsNames.EMAIL_RECEIVER);
        Assumption.assertNotNull(emailReceiver);
        
        String emailReply = getRequestParamValue(ParamsNames.EMAIL_REPLY_TO_ADDRESS);
        Assumption.assertNotNull(emailReply);
        
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
