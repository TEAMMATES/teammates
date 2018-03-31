package teammates.ui.pagedata;

import java.util.List;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;

public class AdminEmailDraftPageData extends AdminEmailPageData {
    public List<AdminEmailAttributes> draftEmailList;

    public AdminEmailDraftPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
        this.state = AdminEmailPageState.DRAFT;
    }

}
