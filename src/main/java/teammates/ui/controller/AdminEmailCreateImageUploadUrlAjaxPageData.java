package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class AdminEmailCreateImageUploadUrlAjaxPageData extends PageData {
    public String nextUploadUrl;
    public String ajaxStatus;

    public AdminEmailCreateImageUploadUrlAjaxPageData(AccountAttributes account) {
        super(account);
    }
    
}
