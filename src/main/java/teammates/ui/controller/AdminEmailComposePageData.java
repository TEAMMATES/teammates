package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.AdminEmailAttributes;

public class AdminEmailComposePageData extends AdminEmailPageData {
    public boolean isFileUploaded;
    public String fileSrcUrl;
    public String ajaxStatus;
    
    public String groupReceiverListFileKey;
    
    public AdminEmailAttributes emailToEdit;
    
    public AdminEmailComposePageData(AccountAttributes account) {
        super(account);
        state = AdminEmailPageState.COMPOSE;
    }

    public AdminEmailComposePageData(AccountAttributes account, FileUploadPageData data) {
        super(account);
        this.isFileUploaded = data.isFileUploaded;
        this.fileSrcUrl = data.fileSrcUrl;
        this.ajaxStatus = data.ajaxStatus;
        state = AdminEmailPageState.COMPOSE;
    }

    public AdminEmailAttributes getEmailToEdit() {
        return emailToEdit;
    }
}
