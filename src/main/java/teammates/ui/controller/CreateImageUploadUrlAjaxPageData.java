package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

/**
 * Page data for created image URL
 */
public class CreateImageUploadUrlAjaxPageData extends PageData {
    public String nextUploadUrl;
    public String ajaxStatus;

    public CreateImageUploadUrlAjaxPageData(AccountAttributes account) {
        super(account);
    }
}
