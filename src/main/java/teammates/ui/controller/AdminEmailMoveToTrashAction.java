package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailMoveToTrashAction extends Action {

    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
            
        String emailToTrashId = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);
        
        try {
            logic.moveAdminEmailToTrashBin(emailToTrashId);
            statusToAdmin = "Email with id" + emailToTrashId + " has been moved to trash bin";
            statusToUser.add("The item has been moved to trash bin");
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            setStatusForException(e, "An error has occurred when moving email to trash bin");
        }
   
        return createRedirectResult(Const.ActionURIs.ADMIN_EMAIL_SENT_PAGE);     
            
    }

}
