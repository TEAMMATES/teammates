package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;

public class AdminEmailFileServeAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        //GateKeeper.inst().verifyAdminPrivileges(account);
        String blobKey = request.getParameter("blob-key");
        return createImageResult(blobKey);
    }

}
