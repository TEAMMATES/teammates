package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.pagedata.AdminAccountManagementPageData;

public class AdminAccountManagementPageAction extends Action {

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyAdminPrivileges(account);

        String instructorGoogleId = this.getRequestParamValue("googleId");
        if (instructorGoogleId == null) {
            instructorGoogleId = "";
        }

        Map<String, ArrayList<InstructorAttributes>> instructorCoursesTable = new HashMap<>();
        Map<String, AccountAttributes> instructorAccountsTable = new HashMap<>();

        List<InstructorAttributes> instructorsList = logic.getInstructorsForGoogleId(instructorGoogleId);
        AccountAttributes instructorAccount = logic.getAccount(instructorGoogleId);

        boolean isToShowAll = this.getRequestParamAsBoolean("all");
        boolean isAccountExisting = instructorAccount != null;
        if (isAccountExisting) {
            instructorAccountsTable.put(instructorAccount.googleId, instructorAccount);

            for (InstructorAttributes instructor : instructorsList) {
                ArrayList<InstructorAttributes> courseList = instructorCoursesTable.get(instructor.googleId);
                if (courseList == null) {
                    courseList = new ArrayList<>();
                    instructorCoursesTable.put(instructor.googleId, courseList);
                }

                courseList.add(instructor);
            }
        }

        AdminAccountManagementPageData data = new AdminAccountManagementPageData(account, sessionToken,
                instructorAccountsTable, instructorCoursesTable, isToShowAll);

        statusToAdmin = "Admin Account Management Page Load<br>"
                        + "<span class=\"bold\">Total Instructors:</span> " + instructorAccountsTable.size();

        return createShowPageResult(Const.ViewURIs.ADMIN_ACCOUNT_MANAGEMENT, data);
    }

}
