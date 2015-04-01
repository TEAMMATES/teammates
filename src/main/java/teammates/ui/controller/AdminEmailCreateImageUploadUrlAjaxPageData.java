package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class AdminEmailCreateImageUploadUrlAjaxPageData extends PageData {

    public AdminEmailCreateImageUploadUrlAjaxPageData(AccountAttributes account) {
        super(account);
    }
    
    public String nextUploadUrl;
    public String ajaxStatus = null;

}
