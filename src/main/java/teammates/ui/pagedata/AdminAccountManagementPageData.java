package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.template.AdminAccountManagementAccountTableRow;

public class AdminAccountManagementPageData extends PageData {
    private List<AdminAccountManagementAccountTableRow> accountTable;

    // By default the testing accounts should not be shown
    private boolean isToShowAll;

    public AdminAccountManagementPageData(AccountAttributes account,
                                          Map<String, AccountAttributes> instructorAccountsTable,
                                          Map<String, ArrayList<InstructorAttributes>> instructorCoursesTable,
                                          boolean isToShowAll) {
        super(account);
        this.isToShowAll = isToShowAll;
        accountTable = createAccountTable(instructorAccountsTable, instructorCoursesTable);
    }

    private List<AdminAccountManagementAccountTableRow> createAccountTable(
            Map<String, AccountAttributes> instructorAccountsTable,
            Map<String, ArrayList<InstructorAttributes>> instructorCoursesTable) {
        List<AdminAccountManagementAccountTableRow> table = new ArrayList<AdminAccountManagementAccountTableRow>();

        for (Map.Entry<String, AccountAttributes> entry : instructorAccountsTable.entrySet()) {
            String key = entry.getKey();
            AccountAttributes acc = entry.getValue();

            if (isTestingAccount(acc) && !isToShowAll) {
                continue;
            }

            ArrayList<InstructorAttributes> coursesList = instructorCoursesTable.get(key);

            AdminAccountManagementAccountTableRow row = new AdminAccountManagementAccountTableRow(acc, coursesList);
            table.add(row);
        }

        return table;
    }

    public List<AdminAccountManagementAccountTableRow> getAccountTable() {
        return accountTable;
    }

    public static String getAdminViewAccountDetailsLink(String googleId) {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, googleId);
        return link;
    }

    public static String getAdminDeleteInstructorStatusLink(String googleId) {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, googleId);
        return link;
    }

    public static String getAdminDeleteAccountLink(String googleId) {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, googleId);
        link = Url.addParamToUrl(link, "account", "true");
        return link;
    }

    public static String getInstructorHomePageViewLink(String googleId) {
        String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, googleId);
        return link;
    }

    public boolean isTestingAccount(AccountAttributes account) {
        return account.email.endsWith(".tmt") || account.institute.contains("TEAMMATES Test Institute");
    }
}
