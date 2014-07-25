package teammates.ui.controller;


import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;

public class AdminSearchPageData extends PageData {
    
    public StudentSearchResultBundle studentResultBundle = new StudentSearchResultBundle();

    public AdminSearchPageData(AccountAttributes account) {
        super(account);
    }

}
