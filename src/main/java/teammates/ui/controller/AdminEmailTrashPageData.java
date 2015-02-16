package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Const.AdminEmailPageState;

public class AdminEmailTrashPageData extends AdminEmailPageData {

    protected AdminEmailTrashPageData(AccountAttributes account) {
        super(account);
        this.state = AdminEmailPageState.TRASH;
    }
    
    public List<AdminEmailAttributes> adminTrashEmailList = null;
}
