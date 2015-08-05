package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminAccountManagementPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        new GateKeeper().verifyAdminPrivileges(account);
        
        @SuppressWarnings("deprecation") //This method is deprecated to prevent unintended usage. This is an intended usage.
        List<InstructorAttributes> allInstructorsList = logic.getAllInstructors();
        @SuppressWarnings("deprecation") //This method is deprecated to prevent unintended usage. This is an intended usage.
        List<AccountAttributes> allInstructorAccountsList = logic.getInstructorAccounts();
        
        boolean isToShowAll = this.getRequestParamAsBoolean("all");
        
        Map<String, ArrayList<InstructorAttributes>> instructorCoursesTable = new HashMap<String, ArrayList<InstructorAttributes>>();
        Map<String, AccountAttributes> instructorAccountsTable = new HashMap<String, AccountAttributes>();
        
        for(AccountAttributes acc : allInstructorAccountsList){
            instructorAccountsTable.put(acc.googleId, acc);
            System.out.println(acc.googleId);
        }
        
        for(InstructorAttributes instructor : allInstructorsList){
            ArrayList<InstructorAttributes> courseList = instructorCoursesTable.get(instructor.googleId);
            if (courseList == null){
                courseList = new ArrayList<InstructorAttributes>();
                instructorCoursesTable.put(instructor.googleId, courseList);
            }
            
            courseList.add(instructor);
        }
            
        AdminAccountManagementPageData data = new AdminAccountManagementPageData(account, instructorAccountsTable,
                                                                                 instructorCoursesTable, isToShowAll);
        
        statusToAdmin = "Admin Account Management Page Load<br>" + 
                "<span class=\"bold\">Total Instructors:</span> " + 
                instructorAccountsTable.size();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_ACCOUNT_MANAGEMENT, data);
    }
    
}
