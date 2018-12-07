package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;

/**
 * Page data for a page with uploaded file.
 */
public class FileUploadPageData extends PageData {
    public boolean isFileUploaded;
    public String fileSrcUrl;
    public String ajaxStatus;

    public FileUploadPageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
    }

}
