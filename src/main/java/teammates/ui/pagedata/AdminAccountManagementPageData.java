package teammates.ui.pagedata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.ui.template.AdminAccountManagementAccountTableRow;

public class AdminAccountManagementPageData extends PageData {
    private List<AdminAccountManagementAccountTableRow> accountTable;

    // By default the testing accounts should not be shown
    private boolean isToShowAll;

    public AdminAccountManagementPageData(AccountAttributes account, String sessionToken,
                                          Map<String, AccountAttributes> instructorAccountsTable,
                                          Map<String, ArrayList<InstructorAttributes>> instructorCoursesTable,
                                          boolean isToShowAll) {
        super(account, sessionToken);
        this.isToShowAll = isToShowAll;
        accountTable = createAccountTable(instructorAccountsTable, instructorCoursesTable);
    }

    private List<AdminAccountManagementAccountTableRow> createAccountTable(
            Map<String, AccountAttributes> instructorAccountsTable,
            Map<String, ArrayList<InstructorAttributes>> instructorCoursesTable) {
        List<AdminAccountManagementAccountTableRow> table = new ArrayList<>();

        for (Map.Entry<String, AccountAttributes> entry : instructorAccountsTable.entrySet()) {
            String key = entry.getKey();
            AccountAttributes acc = entry.getValue();

            if (isTestingAccount(acc) && !isToShowAll) {
                continue;
            }

            ArrayList<InstructorAttributes> coursesList = instructorCoursesTable.get(key);

            AdminAccountManagementAccountTableRow row =
                    new AdminAccountManagementAccountTableRow(acc, coursesList, getSessionToken());
            table.add(row);
        }

        return table;
    }

    public List<AdminAccountManagementAccountTableRow> getAccountTable() {
        return accountTable;
    }

    public boolean isTestingAccount(AccountAttributes account) {
        return account.email.endsWith(".tmt") || account.institute.contains("TEAMMATES Test Institute");
    }
}
