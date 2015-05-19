package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class StudentProfileCreateFormUrlAjaxPageData extends PageData {

    public String formUrl;
    boolean isError;

    public StudentProfileCreateFormUrlAjaxPageData(AccountAttributes account, String url, boolean hasError) {
        super(account);
        account = null;
        formUrl = url;
        isError = hasError;
    }

}
