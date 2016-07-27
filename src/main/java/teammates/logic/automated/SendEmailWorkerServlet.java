package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HttpRequestHelper;
import teammates.logic.core.EmailSender;

@SuppressWarnings("serial")
public class SendEmailWorkerServlet extends WorkerServlet {
    
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String emailSubject = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.EMAIL_SUBJECT);
        Assumption.assertNotNull(emailSubject);
        
        String emailContent = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.EMAIL_CONTENT);
        Assumption.assertNotNull(emailContent);
        
        String emailSender = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.EMAIL_SENDER);
        Assumption.assertNotNull(emailSender);
        
        String emailSenderName = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.EMAIL_SENDERNAME);
        
        String emailReceiver = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.EMAIL_RECEIVER);
        Assumption.assertNotNull(emailReceiver);
        
        String emailReply = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.EMAIL_REPLY_TO_ADDRESS);
        Assumption.assertNotNull(emailReply);
        
        EmailWrapper message = new EmailWrapper();
        message.setRecipient(emailReceiver);
        message.setSenderEmail(emailSender);
        if (emailSenderName != null) {
            message.setSenderName(emailSenderName);
        }
        message.setContent(emailContent);
        message.setSubject(emailSubject);
        message.setReplyTo(emailReply);
        
        try {
            new EmailSender().sendEmail(message);
        } catch (Exception e) {
            log.severe("Error while sending email via servlet: " + TeammatesException.toStringWithStackTrace(e));
            
            // Sets an arbitrary retry code outside of the range 200-299 so GAE will automatically retry upon failure
            resp.setStatus(100);
        }
    }
}
