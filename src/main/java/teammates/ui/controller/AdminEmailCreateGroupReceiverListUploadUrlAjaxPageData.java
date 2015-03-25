package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData extends
        PageData {

    public AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData(
            AccountAttributes account) {
        super(account);
    }
    
    public String nextUploadUrl;
    public String ajaxStatus = null;

}
