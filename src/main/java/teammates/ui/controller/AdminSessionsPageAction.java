package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.common.util.TimeHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.api.Logic;

public class AdminSessionsPageAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        
        Logic logic = new Logic();

        new GateKeeper().verifyAdminPrivileges(account);
        
        AdminSessionsPageData data = new AdminSessionsPageData(account);

        @SuppressWarnings("deprecation")
        // This method is deprecated to prevent unintended usage. This is an
        // intended usage.
        List<FeedbackSessionAttributes> allOpenFeedbackSessionsList = logic
                .getAllOpenFeedbackSessions();
        
        data.totalOngoingSessions = allOpenFeedbackSessionsList.size();
       
        HashMap<String,List<FeedbackSessionAttributes>> map = new HashMap<String,List<FeedbackSessionAttributes>>();
        
        data.hasUnknown = false;
       
        for (FeedbackSessionAttributes fs : allOpenFeedbackSessionsList) {

            List<InstructorAttributes> instructors = logic.getInstructorsForCourse(
                    fs.courseId);
            
            if(!instructors.isEmpty()){
                
                InstructorAttributes instructor = instructors.get(0);
          
                AccountAttributes account = logic.getAccount(instructor.googleId);
                
                
                if (map.get(account.institute) == null) {
                    List<FeedbackSessionAttributes> newList = new ArrayList<FeedbackSessionAttributes>();
                    newList.add(fs);
                    map.put(account.institute, newList);
                }else{
                    map.get(account.institute).add(fs);
                }
            
            }else{
                
                if (map.get("Unknown") == null) {
                    List<FeedbackSessionAttributes> newList = new ArrayList<FeedbackSessionAttributes>();
                    newList.add(fs);
                    map.put("Unknown", newList);
                }else{
                    map.get("Unknown").add(fs);
                }
                
                data.hasUnknown = true;
            }
        }
        
        
        //System.out.print(map.toString());
        System.out.print(map.keySet().size());
        
        for (String key : map.keySet()){
            System.out.print(key);
        }
        
        
        data.map = map;
        
        
        // data.instructorCoursesTable = new HashMap<String,
        // ArrayList<InstructorAttributes>>();
        // data.instructorAccountsTable = new HashMap<String,
        // AccountAttributes>();
        //
        // for(AccountAttributes acc : allInstructorAccountsList){
        // data.instructorAccountsTable.put(acc.googleId, acc);
        // }
        //
        // for(InstructorAttributes instructor : allInstructorsList){
        // ArrayList<InstructorAttributes> courseList =
        // data.instructorCoursesTable.get(instructor.googleId);
        // if (courseList == null){
        // courseList = new ArrayList<InstructorAttributes>();
        // data.instructorCoursesTable.put(instructor.googleId, courseList);
        // }
        // courseList.add(instructor);
        // }
        //
        // statusToAdmin = "Admin Account Management Page Load<br>" +
        // "<span class=\"bold\">Total Instructors:</span> " +
        // data.instructorAccountsTable.size();
        //
        // return createShowPageResult(Const.ViewURIs.ADMIN_ACCOUNT_MANAGEMENT,
        // data);
        //
        //
        //
        statusToAdmin = "Admin Sessions Page Load";
        
        return createShowPageResult(Const.ViewURIs.ADMIN_SESSIONS, data);
    }

}
