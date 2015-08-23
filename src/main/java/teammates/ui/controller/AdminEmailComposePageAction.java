package teammates.ui.controller;


import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;


public class AdminEmailComposePageAction extends Action {
    
    @Override
    protected ActionResult execute() {
        
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
                statusToUser.add(new StatusMessage("The requested email was not found", StatusMessageColor.WARNING));
            }
            
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
        }
        statusToAdmin = "adminEmailComposePage Page Load";
        data.init();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);     
    }

}
