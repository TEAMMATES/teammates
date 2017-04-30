package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;

public class AdminEmailSentPageData extends AdminEmailPageData {
    public List<AdminEmailAttributes> adminSentEmailList;

    public AdminEmailSentPageData(AccountAttributes account) {
        super(account);
        this.state = AdminEmailPageState.SENT;
    }

}
