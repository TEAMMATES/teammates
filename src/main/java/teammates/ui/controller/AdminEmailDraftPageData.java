package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Const.AdminEmailPageState;

public class AdminEmailDraftPageData extends AdminEmailPageData {

    protected AdminEmailDraftPageData(AccountAttributes account) {
        super(account);
        this.state = AdminEmailPageState.DRAFT;
    }
    
    public List<AdminEmailAttributes> draftEmailList = null;
}
