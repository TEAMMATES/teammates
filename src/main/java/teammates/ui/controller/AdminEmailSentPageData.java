package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.AdminEmailAttributes;

public class AdminEmailSentPageData extends AdminEmailPageData {
    public List<AdminEmailAttributes> adminSentEmailList;

    protected AdminEmailSentPageData(AccountAttributes account) {
        super(account);
        this.state = AdminEmailPageState.SENT;
    }
    
}
