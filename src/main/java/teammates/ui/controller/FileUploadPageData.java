package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

/**
 * Page data for uploaded file
 */
public class FileUploadPageData extends PageData {
    public boolean isFileUploaded;
    public String fileSrcUrl;
    public String ajaxStatus;

    public FileUploadPageData(AccountAttributes account) {
        super(account);
    }

}
