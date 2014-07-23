package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class StudentCourseJoinConfirmationPageData extends PageData {

    public String confirmUrl;
    public String logoutUrl;
    
    public StudentCourseJoinConfirmationPageData(AccountAttributes account, String regkey) {
        super(account, regkey);
    }
}
