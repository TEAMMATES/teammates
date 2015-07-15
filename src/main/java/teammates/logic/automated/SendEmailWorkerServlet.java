package teammates.logic.automated;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;

import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.Emails;

@SuppressWarnings("serial")
public class SendEmailWorkerServlet extends WorkerServlet {
    
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        //Sets an arbitrary retry code outside of the range 200-299 so GAE will automatically retry upon failure
        int responseCodeForRetry = 100;
        try {
            String emailSubject = HttpRequestHelper
                    .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_SUBJECT);
            Assumption.assertNotNull(emailSubject);
            
            String emailContent = HttpRequestHelper
                    .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_CONTENT);
            Assumption.assertNotNull(emailContent);
            
            String emailSender = HttpRequestHelper
                    .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_SENDER);
            Assumption.assertNotNull(emailSender);
            
            String emailReceiver = HttpRequestHelper
                    .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_RECEIVER);
            Assumption.assertNotNull(emailReceiver);
            
            String emailReply = HttpRequestHelper
                    .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_REPLY_TO_ADDRESS);
            Assumption.assertNotNull(emailReply);
            
            Session session = Session.getDefaultInstance(new Properties(), null);
            MimeMessage message = new MimeMessage(session);
    
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    emailReceiver));
            message.setFrom(new InternetAddress(emailSender));
            message.setContent(emailContent, "text/html");
            message.setSubject(emailSubject);
            message.setReplyTo(new Address[] { new InternetAddress(emailReply) });
            
            Emails emailManager = new Emails();
            emailManager.sendEmailWithLogging(message);
        } catch (MessagingException | JSONException | IOException e) {
            log.severe("Error while sending emails via servlet: " + e.getMessage());
            resp.setStatus(responseCodeForRetry);
        }
    }
}
