package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

public class AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData extends
        PageData {
    public String nextUploadUrl;
    public String ajaxStatus;

    public AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData(
            AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

}
