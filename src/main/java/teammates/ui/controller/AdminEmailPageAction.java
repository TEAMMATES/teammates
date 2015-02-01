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
        
        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        
        Logic logic = new Logic();
        
        Emails emailsManager = new Emails();
        
        try {
            MimeMessage email = emailsManager.generateAdminEmail(emailContent);
            emailsManager.sendEmail(email);
        } catch (UnsupportedEncodingException | MessagingException e) {
            e.printStackTrace();
        }

        
        AdminEmailPageData data = new AdminEmailPageData(account);
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
