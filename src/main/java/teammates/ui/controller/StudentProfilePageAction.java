package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentProfilePageAction extends Action {

    private PageData data;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyLoggedInUserPrivileges();
        
        this.account.studentProfile = logic.getStudentProfile(account.googleId); 
        data = new PageData(account);
        statusToAdmin = "studentProfile Page Load <br> Account: " + account.googleId;
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_PROFILE_PAGE, data);
        return response;
    }
}
