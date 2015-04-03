package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.util.Const.AdminEmailPageState;

public abstract class AdminEmailPageData extends PageData {

    protected AdminEmailPageData(AccountAttributes account) {
        super(account);
    }
    
    protected AdminEmailPageState state;
    
    public AdminEmailPageState getPageState(){
        return this.state;
    }
}
