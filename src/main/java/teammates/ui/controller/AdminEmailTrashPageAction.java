package teammates.ui.controller;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
import teammates.logic.api.GateKeeper;

public class AdminEmailTrashPageAction extends Action {

    @Override
    protected ActionResult execute() {
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailTrashPageData data = new AdminEmailTrashPageData(account);      
        
        data.adminTrashEmailList = logic.getAdminEmailsInTrashBin();
        
        for(AdminEmailAttributes ae : data.adminTrashEmailList){
            System.out.print(ae.emailId+ "\n");
            System.out.print(ae.subject+ "\n");
            System.out.print(ae.sendDate.toString() + "\n");
            System.out.print(ae.getAddressReceiver().get(0) + "\n");
            System.out.print(ae.getGroupReceiver().get(0)+ "\n");
            System.out.print(StringHelper.recoverFromSanitizedText(ae.getContent().getValue())+ "\n");
            
            System.out.print("************************************\n");
        }
            
            statusToAdmin = "adminEmailTrashPage Page Load";
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
            
    }

}
