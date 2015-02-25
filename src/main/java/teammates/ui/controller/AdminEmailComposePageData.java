package teammates.ui.controller;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.util.Const.AdminEmailPageState;
import com.google.appengine.api.datastore.Text;

public class AdminEmailComposePageData extends AdminEmailPageData {

    public AdminEmailComposePageData(AccountAttributes account) {     
        super(account);
        state = AdminEmailPageState.COMPOSE;
    }

    public boolean isFileUploaded;
    public String fileSrcUrl;
    public String ajaxStatus = null;
    
    public String groupReceiverListFileKey = null;
    public String groupReceiverListFileSize = null;
    
    public AdminEmailAttributes emailToEdit = null;
}
