package teammates.ui.pagedata;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AdminEmailAttributes;

public class AdminEmailComposePageData extends AdminEmailPageData {
    public boolean isFileUploaded;
    public String fileSrcUrl;
    public String ajaxStatus;

    public String groupReceiverListFileKey;

    public AdminEmailAttributes emailToEdit;

    public AdminEmailComposePageData(AccountAttributes account, String sessionToken) {
        super(account, sessionToken);
        state = AdminEmailPageState.COMPOSE;
    }

    public AdminEmailAttributes getEmailToEdit() {
        return emailToEdit;
    }
}
