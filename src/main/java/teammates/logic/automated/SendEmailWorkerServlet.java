package teammates.logic.automated;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.HttpRequestHelper;
import teammates.googleSendgridJava.Sendgrid;
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
            
            Sendgrid message = new Sendgrid(Const.SystemParams.SENDGRID_USERNAME, Const.SystemParams.SENDGRID_PASSWORD);
            
            message.addTo(emailReceiver);
            message.setFrom(emailSender);
            message.setHtml(emailContent);
            message.setSubject(emailSubject);
            message.setReplyTo(emailReply);
            
            Emails emailManager = new Emails();
            emailManager.sendAndLogEmail(message);
        } catch (Exception e) {
            log.severe("Error while sending emails via servlet: " + e.getMessage());
            resp.setStatus(responseCodeForRetry);
        }
    }
}
