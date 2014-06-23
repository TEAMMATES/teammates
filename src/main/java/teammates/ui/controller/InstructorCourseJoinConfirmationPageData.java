package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class InstructorCourseJoinConfirmationPageData extends PageData {

    public String regkey;
    public String institute;
    public String isSampleDataImported;
    
    public InstructorCourseJoinConfirmationPageData(AccountAttributes account) {
        super(account);
    }

}
