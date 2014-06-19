package teammates.ui.controller;

import java.util.ArrayList;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminAccountDetailsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminAccountDetailsPageData data = new AdminAccountDetailsPageData(account);
        
        String googleId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        
        data.accountInformation = logic.getAccount(googleId);
        
        try{
            data.instructorCourseList = new ArrayList<CourseDetailsBundle>(logic.getCourseSummariesForInstructor(googleId).values());
        } catch (EntityDoesNotExistException e){
            //Not an instructor of any course
            data.instructorCourseList = null;
        }
        try{
            data.studentCourseList = logic.getCoursesForStudentAccount(googleId);
        } catch(EntityDoesNotExistException e){
            //Not a student of any course
            data.studentCourseList = null;
        }
        
        statusToAdmin = "adminAccountDetails Page Load<br>"+ 
                "Viewing details for " + data.accountInformation.name + "(" + googleId + ")";
        
        return createShowPageResult(Const.ViewURIs.ADMIN_ACCOUNT_DETAILS, data);
    }

}
