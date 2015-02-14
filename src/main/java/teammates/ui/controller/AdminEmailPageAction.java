package teammates.ui.controller;

import java.io.UnsupportedEncodingException;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;
import teammates.logic.core.Emails;

public class AdminEmailPageAction extends Action {

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
            } catch (UnsupportedEncodingException | MessagingException e) {
                isError = true;
                setStatusForException(e, "An error has occurred when sending emails");
            }
        
        }
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
