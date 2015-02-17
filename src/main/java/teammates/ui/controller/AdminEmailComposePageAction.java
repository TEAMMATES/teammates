package teammates.ui.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
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
    
    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        
        String idOfEmailToEdit = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID); 
        
        boolean isEmailEdit = idOfEmailToEdit != null;
        
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

}
