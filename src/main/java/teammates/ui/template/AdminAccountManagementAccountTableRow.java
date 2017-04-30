package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.ui.pagedata.AdminAccountManagementPageData;

public class AdminAccountManagementAccountTableRow {
    private AccountAttributes account;
    private List<InstructorAttributes> instructorList;

    public AdminAccountManagementAccountTableRow(AccountAttributes account, List<InstructorAttributes> instructorList) {
        this.account = account;
        this.instructorList = instructorList;
    }

    public AccountAttributes getAccount() {
        return account;
    }

    public List<InstructorAttributes> getInstructorList() {
        return instructorList;
    }

    public String getInstructorHomePageViewLink() {
        return AdminAccountManagementPageData.getInstructorHomePageViewLink(account.googleId);
    }

    public String getCreatedAt() {
        return AdminAccountManagementPageData.displayDateTime(account.createdAt);
    }

    public String getAdminViewAccountDetailsLink() {
        return AdminAccountManagementPageData.getAdminViewAccountDetailsLink(account.googleId);
    }

    public String getAdminDeleteInstructorStatusLink() {
        return AdminAccountManagementPageData.getAdminDeleteInstructorStatusLink(account.googleId);
    }

    public String getAdminDeleteAccountLink() {
        return AdminAccountManagementPageData.getAdminDeleteAccountLink(account.googleId);
    }
}
