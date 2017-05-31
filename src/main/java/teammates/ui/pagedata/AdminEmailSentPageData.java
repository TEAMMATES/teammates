package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;

public class AdminEmailSentPageData extends AdminEmailPageData {
    public List<AdminEmailAttributes> adminSentEmailList;

    public AdminEmailSentPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
        this.state = AdminEmailPageState.SENT;
    }

}
