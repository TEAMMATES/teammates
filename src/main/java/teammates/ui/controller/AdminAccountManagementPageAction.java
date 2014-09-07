package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminAccountManagementPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminAccountManagementPageData data = new AdminAccountManagementPageData(account);
        
        @SuppressWarnings("deprecation") //This method is deprecated to prevent unintended usage. This is an intended usage.
        List<InstructorAttributes> allInstructorsList = logic.getAllInstructors();
        @SuppressWarnings("deprecation") //This method is deprecated to prevent unintended usage. This is an intended usage.
        List<AccountAttributes> allInstructorAccountsList = logic.getInstructorAccounts();
        
        boolean isToShowAll = this.getRequestParamAsBoolean("all");
        
        data.isToShowAll = isToShowAll;
        
        data.instructorCoursesTable = new HashMap<String, ArrayList<InstructorAttributes>>();
        data.instructorAccountsTable = new HashMap<String, AccountAttributes>();
        
        for(AccountAttributes acc : allInstructorAccountsList){
            data.instructorAccountsTable.put(acc.googleId, acc);
        }
        
        for(InstructorAttributes instructor : allInstructorsList){
            ArrayList<InstructorAttributes> courseList = data.instructorCoursesTable.get(instructor.googleId);
            if (courseList == null){
                courseList = new ArrayList<InstructorAttributes>();
                data.instructorCoursesTable.put(instructor.googleId, courseList);
            }
            courseList.add(instructor);
        }
            
        statusToAdmin = "Admin Account Management Page Load<br>" + 
                "<span class=\"bold\">Total Instructors:</span> " + 
                data.instructorAccountsTable.size();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_ACCOUNT_MANAGEMENT, data);
    }
    
}
