package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

public class AdminStudentGoogleIdResetPageData extends PageData {
    /*
     * Data used for reset student google id using ajax
     */
    public boolean isGoogleIdReset;
    public String statusForAjax;

    public AdminStudentGoogleIdResetPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

}
