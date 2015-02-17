package teammates.ui.controller;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

public class AdminEmailSentPageAction extends Action {

    @Override
    protected ActionResult execute() {
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailSentPageData data = new AdminEmailSentPageData(account);      
        
        data.adminSentEmailList = logic.getSentAdminEmails();
            
        statusToAdmin = "adminEmailSentPage Page Load";
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
            
    }

}
