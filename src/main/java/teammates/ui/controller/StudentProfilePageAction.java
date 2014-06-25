package teammates.ui.controller;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class StudentProfilePageAction extends Action {

    private StudentProfilePageData data;

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        new GateKeeper().verifyLoggedInUserPrivileges();
        if(isUnregistered) { 
            // unregistered users cannot view the page
            throw new UnauthorizedAccessException("User is not registered");
        }
        
        account.studentProfile = logic.getStudentProfile(account.googleId); 
        data = new StudentProfilePageData(account);
        statusToAdmin = "studentProfile Page Load <br> Profile: " + account.studentProfile.toString();
        
        ShowPageResult response = createShowPageResult(Const.ViewURIs.STUDENT_PROFILE_PAGE, data);
        return response;
    }
}
