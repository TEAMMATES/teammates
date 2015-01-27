package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class AdminStudentGoogleIdResetPageData extends PageData {
    public AdminStudentGoogleIdResetPageData(AccountAttributes account) {
        super(account);
    }
    
    /*
     * Data used for reset student google id using ajax
     */
    public boolean isGoogleIdReset;
    public String statusForAjax;
}
