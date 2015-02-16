package teammates.ui.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails;

public class AdminEmailComposePageAction extends Action {
    
    List<String> emailReceiver = new ArrayList<String>();
    List<String> groupReceiver = new ArrayList<String>();
    
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        
        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);
        String receiver = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_RECEVIER);
        
        String idOfEmailToEdit = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID); 
        
        boolean isNewPageLoad = emailContent == null &&
                                subject == null &&
                                receiver == null;
        boolean isEmailEdit = idOfEmailToEdit != null;
        
        if(isNewPageLoad){
            
            if(isEmailEdit){
                
                data.emailToEdit = logic.getAdminEmailById(idOfEmailToEdit);             
                statusToAdmin = data.emailToEdit == null? 
                                "adminEmailComposePage Page Load : Requested Email for editing was not found":
                                "adminEmailComposePage Page Load : Edit Email " + "[" + data.emailToEdit.getSubject() +"]";
                
                if(data.emailToEdit == null){
                    isError = true;
                    statusToUser.add("The requested email was not found");
                }
                
                return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
            }
            statusToAdmin = "adminEmailComposePage Page Load";
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
        }
        
        
        
        Emails emailsManager = new Emails();
        
        if(!emailContent.isEmpty()){
        
            try {
                
                
                MimeMessage email = emailsManager.generateAdminEmail(emailContent, subject, receiver);
                emailsManager.sendEmail(email);
                
                emailReceiver.add(receiver);
                groupReceiver.add("all user");
                
                AdminEmailAttributes newAdminEmail = new AdminEmailAttributes(subject, 
                                                                              emailReceiver,
                                                                              groupReceiver,
                                                                              new Text(emailContent));
                logic.createAdminEmail(newAdminEmail);
                
            } catch (UnsupportedEncodingException | MessagingException e) {
                isError = true;
                setStatusForException(e, "An error has occurred when sending emails");
            } catch (InvalidParametersException e) {
                isError = true;
                setStatusForException(e, e.getMessage());
            }
            
            
        }
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
