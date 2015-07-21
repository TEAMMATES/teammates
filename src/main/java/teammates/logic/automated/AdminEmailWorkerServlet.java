package teammates.logic.automated;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.labs.repackaged.org.json.JSONException;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.StringHelper;
import teammates.logic.core.AdminEmailsLogic;
import teammates.logic.core.Emails;

/**
 * Retrieves admin email content and subject by email id and sends email to the receiver 
 */
@SuppressWarnings("serial")
public class AdminEmailWorkerServlet extends WorkerServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) {
        
        
        String emailId =  HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_ID);        
        Assumption.assertNotNull(emailId);
        
        String receiverEmail =  HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_RECEVIER);       
        Assumption.assertNotNull(receiverEmail);
        

        
        String emailContent = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_CONTENT);
        String emailSubject = HttpRequestHelper.getValueFromRequestParameterMap(req, ParamsNames.ADMIN_EMAIL_SUBJECT);
        
        if(emailContent == null || emailSubject == null){
          log.info("Sending large email. Going to retrieve email content and subject from datastore.");
          AdminEmailAttributes adminEmail = AdminEmailsLogic.inst().getAdminEmailById(emailId);      
          Assumption.assertNotNull(adminEmail);
          
          emailContent = adminEmail.getContent().getValue();
          emailSubject = adminEmail.getSubject();
        }
        
        Assumption.assertNotNull(emailContent);
        Assumption.assertNotNull(emailSubject); 
        
        try {
            sendAdminEmail(emailContent, emailSubject, receiverEmail);
            log.info("email sent to " + receiverEmail);
        } catch (MessagingException | JSONException | IOException e) {
            log.severe("Unexpected error while sending admin emails " + e.getMessage());
        }

    }
    
    private void sendAdminEmail(String emailContent, String subject, String receiverEmail) throws MessagingException, JSONException, IOException{
        
        Emails emailsManager = new Emails();
        
        MimeMessage email = emailsManager.generateAdminEmail(StringHelper.recoverFromSanitizedText(emailContent), subject, receiverEmail);
        emailsManager.sendEmailWithoutLogging(email);
       
    }

}
