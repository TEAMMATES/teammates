package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class StudentProfileCreateFormUrlAjaxPageData extends PageData {

    String formUrl;
    boolean isError;
    
    public StudentProfileCreateFormUrlAjaxPageData(AccountAttributes account,
            String url) {
        super(account);
        account = null;
        formUrl = url;
        isError = false;
    }

}
