package teammates.ui.controller;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails;

public class AdminEmailSentPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailSentPageData data = new AdminEmailSentPageData(account);      
        
        data.adminSentEmailList = logic.getAllAdminEmails();
        
        for(AdminEmailAttributes ae : data.adminSentEmailList){
            System.out.print(ae.emailId+ "\n");
            System.out.print(ae.subject+ "\n");
            System.out.print(ae.sendDate.toString() + "\n");
            System.out.print(ae.getAddressReceiver().get(0) + "\n");
            System.out.print(ae.getGroupReceiver().get(0)+ "\n");
            System.out.print(StringHelper.recoverFromSanitizedText(ae.getContent().getValue())+ "\n");
            
            System.out.print("************************************\n");
        }
            
            statusToAdmin = "adminEmailSentPage Page Load";
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
            
    }

}
