package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class StudentCourseJoinConfirmationPageData extends PageData {

    public String nextUrl;
    
    
    public StudentCourseJoinConfirmationPageData(AccountAttributes account, String regkey,
            String nextUrl) {
        super(account, regkey);
        this.nextUrl = nextUrl;
    }

}
