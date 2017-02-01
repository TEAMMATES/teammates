package teammates.ui.pagedata;

import teammates.common.datatransfer.AccountAttributes;

public class AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData extends
        PageData {
    public String nextUploadUrl;
    public String ajaxStatus;

    public AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData(
            AccountAttributes account) {
        super(account);
    }

}
