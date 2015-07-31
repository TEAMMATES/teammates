package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminAccountDetailsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException{
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        String googleId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        AccountAttributes accountInformation = logic.getAccount(googleId);

        List<CourseDetailsBundle> instructorCourseList;
        try{
            instructorCourseList = new ArrayList<CourseDetailsBundle>(logic.getCourseSummariesForInstructor(googleId).values());
        } catch (EntityDoesNotExistException e){
            //Not an instructor of any course
            instructorCourseList = null;
        }
        
        List<CourseAttributes> studentCourseList;
        try{
            studentCourseList = logic.getCoursesForStudentAccount(googleId);
        } catch(EntityDoesNotExistException e){
            //Not a student of any course
            studentCourseList = null;
        }
        
        AdminAccountDetailsPageData data = new AdminAccountDetailsPageData(account, accountInformation, 
                                                                           instructorCourseList, studentCourseList);
        statusToAdmin = "adminAccountDetails Page Load<br>"+ 
                "Viewing details for " + data.getAccountInformation().name + "(" + googleId + ")";
        
        return createShowPageResult(Const.ViewURIs.ADMIN_ACCOUNT_DETAILS, data);
    }

}
