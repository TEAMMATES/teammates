package teammates.ui.template;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.ui.pagedata.AdminAccountManagementPageData;

public class AdminAccountManagementAccountTableRow {
    private AccountAttributes account;
    private List<InstructorAttributes> instructorList;
    private String sessionToken;

    public AdminAccountManagementAccountTableRow(AccountAttributes account, List<InstructorAttributes> instructorList,
            String sessionToken) {
        this.account = account;
        this.instructorList = instructorList;
        this.sessionToken = sessionToken;
    }

    public AccountAttributes getAccount() {
        return account;
    }

    public List<InstructorAttributes> getInstructorList() {
        return instructorList;
    }

    public String getInstructorHomePageViewLink() {
        String link = Const.ActionURIs.INSTRUCTOR_HOME_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.USER_ID, account.googleId);
        return link;
    }

    public String getCreatedAt() {
        return AdminAccountManagementPageData.displayDateTime(account.createdAt);
    }

    public String getAdminViewAccountDetailsLink() {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DETAILS_PAGE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, account.googleId);
        return link;
    }

    public String getAdminDeleteInstructorStatusLink() {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, account.googleId);
        link = Url.addParamToUrl(link, Const.ParamsNames.SESSION_TOKEN, sessionToken);
        return link;
    }

    public String getAdminDeleteAccountLink() {
        String link = Const.ActionURIs.ADMIN_ACCOUNT_DELETE;
        link = Url.addParamToUrl(link, Const.ParamsNames.INSTRUCTOR_ID, account.googleId);
        link = Url.addParamToUrl(link, "account", "true");
        link = Url.addParamToUrl(link, Const.ParamsNames.SESSION_TOKEN, sessionToken);
        return link;
    }
}
