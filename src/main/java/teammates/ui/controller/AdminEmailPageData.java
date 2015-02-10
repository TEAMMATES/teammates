package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;

public class AdminEmailPageData extends PageData {

    public AdminEmailPageData(AccountAttributes account) {
        super(account);
    }
    
    public boolean isFileUploaded;
    public String fileSrcUrl;
    public String nextUploadUrl;
}
