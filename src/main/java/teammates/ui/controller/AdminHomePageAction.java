package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminHomePageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminHomePageData data = new AdminHomePageData(account);
        
        data.instructorShortName = "";
        data.instructorName = "";
        data.instructorEmail = "";
        data.instructorInstitution = "";
        
        statusToAdmin = "Admin Home Page Load";
        
        return createShowPageResult(Const.ViewURIs.ADMIN_HOME, data);
    }

}
