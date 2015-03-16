package teammates.logic.automated;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.StringHelper;
import teammates.logic.core.AdminEmailsLogic;

import com.google.appengine.api.datastore.Text;

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
        
        AdminEmailAttributes adminEmail = AdminEmailsLogic.inst().getAdminEmailById(emailId);      
        Assumption.assertNotNull(receiverEmail);
        
        Text emailContent = adminEmail.getContent();
        String emailSubject = adminEmail.getSubject();
        
        Assumption.assertNotNull(emailContent);
        Assumption.assertNotNull(emailSubject); 
        
        try {
            sendAdminEmail(emailContent, emailSubject, receiverEmail);
            log.info("email sent to " + receiverEmail);
        } catch (UnsupportedEncodingException | MessagingException e) {
            log.severe("Unexpected error while sending admin emails " + e.getMessage());
        }

    }
    
    private void sendAdminEmail(Text emailContent, String subject, String receiverEmail) throws UnsupportedEncodingException, MessagingException{
        
        Emails emailsManager = new Emails();
        
        MimeMessage email = emailsManager.generateAdminEmail(StringHelper.recoverFromSanitizedText(emailContent.getValue()), subject, receiverEmail);
        emailsManager.sendEmail(email);
       
    }

}
