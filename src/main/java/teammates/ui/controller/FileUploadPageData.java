package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class FileUploadPageData extends PageData {
    public boolean isFileUploaded;
    public String fileSrcUrl;
    public String ajaxStatus;
    
    public FileUploadPageData(AccountAttributes account) {
        super(account);
    }

}
