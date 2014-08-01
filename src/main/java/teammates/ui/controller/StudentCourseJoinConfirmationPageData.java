package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class StudentCourseJoinConfirmationPageData extends PageData {

    public String confirmUrl;
    public String logoutUrl;
    
    public StudentCourseJoinConfirmationPageData(AccountAttributes account, StudentAttributes student) {
        super(account, student);
    }
}
