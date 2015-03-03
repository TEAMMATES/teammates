package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreFailureException;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailTrashDeleteAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        boolean emptyTrashBin = getRequestParamAsBoolean(Const.ParamsNames.ADMIN_EMAIL_EMPTY_TRASH_BIN);
        
        if(emptyTrashBin){
            try {
                logic.deleteAllEmailsInTrashBin();
                statusToAdmin = "All emails in trash bin has been deleted";
                statusToUser.add("All emails in trash bin has been deleted");
            } catch (BlobstoreFailureException e){
                statusToAdmin = "Blobstore connection failure";
                statusToUser.add("Blobstore connection failure");
            }
        }     
        
        return createRedirectResult(Const.ActionURIs.ADMIN_EMAIL_TRASH_PAGE);
    }

}
