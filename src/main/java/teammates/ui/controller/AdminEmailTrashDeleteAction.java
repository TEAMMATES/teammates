package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailTrashDeleteAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        boolean emptyTrashBin = getRequestParamAsBoolean(Const.ParamsNames.ADMIN_EMAIL_EMPTY_TRASH_BIN);
        
        if(emptyTrashBin){
            logic.deleteAllEmailsInTrashBin();
            statusToAdmin = "All emails in trash bin has been deleted";
            statusToUser.add("All emails in trash bin has been deleted");
        }     
        
        return createRedirectResult(Const.ActionURIs.ADMIN_EMAIL_TRASH_PAGE);
    }

}
