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
import teammates.logic.api.Logic;
import teammates.logic.core.Emails;

public class AdminEmailPageAction extends Action {
    
    List<String> emailReceiver = new ArrayList<String>();
    List<String> groupReceiver = new ArrayList<String>();
    
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailPageData data = new AdminEmailPageData(account);
        
        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);
        String receiver = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_RECEVIER);
        
        if(emailContent == null){
            statusToAdmin = "adminEmailPage Page Load";
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
        
        List<AdminEmailAttributes> list = logic.getAllAdminEmails();
        
        for(AdminEmailAttributes ae : list){
            System.out.print(ae.emailId+ "\n");
            System.out.print(ae.subject+ "\n");
            System.out.print(ae.sendDate.toString() + "\n");
            System.out.print(ae.getEmailReceiver().get(0) + "\n");
            System.out.print(ae.getGroupReceiver().get(0)+ "\n");
            System.out.print(StringHelper.recoverFromSanitizedText(ae.getContent().getValue())+ "\n");
            
            System.out.print("************************************\n");
        }
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
