package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class StudentCourseJoinConfirmationPageData extends PageData {
    private String confirmUrl;
    private String logoutUrl;
    
    public StudentCourseJoinConfirmationPageData(AccountAttributes account, StudentAttributes student,
                                                 String confirmUrl, String logoutUrl) {
        super(account, student);
        this.confirmUrl = confirmUrl;
        this.logoutUrl = logoutUrl;
    }

    public String getConfirmUrl() {
        return confirmUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }
}
