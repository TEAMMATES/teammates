package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailSentPageAction extends Action {

    @Override
    protected ActionResult execute() {
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailSentPageData data = new AdminEmailSentPageData(account);      
        
        data.adminSentEmailList = logic.getSentAdminEmails();
            
        statusToAdmin = "adminEmailSentPage Page Load";
        
        data.init();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
            
    }

}
