package teammates.ui.controller;


import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;

public class AdminSearchPageData extends PageData {
    
    public String searchKey = "";
    public StudentSearchResultBundle studentResultBundle = new StudentSearchResultBundle();
    public HashMap<String, List<String>> studentfeedbackSessionLinksMap = new HashMap<String, List<String>>();
    
    public AdminSearchPageData(AccountAttributes account) {
        super(account);
    }
    
    
}
