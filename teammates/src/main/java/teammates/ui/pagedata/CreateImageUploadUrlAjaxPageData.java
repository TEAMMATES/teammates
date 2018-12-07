package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Page data for a page with created image URL.
 */
public class CreateImageUploadUrlAjaxPageData extends PageData {
    public String nextUploadUrl;
    public String ajaxStatus;

    public CreateImageUploadUrlAjaxPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }
}
