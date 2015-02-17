package teammates.ui.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails;

public class AdminEmailComposeSendAction extends Action {
    
    List<String> addressReceiver = new ArrayList<String>();
    List<String> groupReceiver = new ArrayList<String>();
    
    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        
        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);
        String receiver = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_RECEVIER);
        
        String emailId = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);
        
        boolean isEmailDraft = emailId != null && !emailId.isEmpty();
        
        addressReceiver.add(receiver);
        groupReceiver.add(receiver);
        
        Emails emailsManager = new Emails();
        
       
        try{
            
            if(!isEmailDraft) {
                recordNewSentEmail(subject, addressReceiver, groupReceiver, emailContent);
            } else {
                updateDraftEmailToSent(emailId, subject, addressReceiver, groupReceiver, emailContent);
            }
            
            MimeMessage email = emailsManager.generateAdminEmail(emailContent, subject, receiver);
            emailsManager.sendEmail(email);
            
            addressReceiver.add(receiver);
            groupReceiver.add("all user");
            
            
            
        } catch (UnsupportedEncodingException | MessagingException e) {
            isError = true;
            setStatusForException(e, "An error has occurred when sending emails");
        } 

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }
    
    
    private void recordNewSentEmail(String subject,
                                    List<String> addressReceiver,
                                    List<String> groupReceiver,
                                    String content) {

        AdminEmailAttributes newDraft = new AdminEmailAttributes(subject,
                                                                 addressReceiver,
                                                                 groupReceiver,
                                                                 new Text(content),
                                                                 new Date());
        try {
            logic.createAdminEmail(newDraft);
        } catch (InvalidParametersException e) {
            isError = true;
            setStatusForException(e, e.getMessage());
        }
    }
    
    
    private void updateDraftEmailToSent(String emailId,
                                        String subject,
                                        List<String> addressReceiver,
                                        List<String> groupReceiver,
                                        String content){
        
        AdminEmailAttributes fanalisedEmail = new AdminEmailAttributes(subject,
                                            addressReceiver,
                                            groupReceiver,
                                            new Text(content),
                                            new Date());
        
        try {
            logic.updateAdminEmailById(fanalisedEmail, emailId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            setStatusForException(e);
        }
        
    }

}
