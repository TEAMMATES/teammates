package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailDraftPageAction extends Action {

    @Override
    protected ActionResult execute() {
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailDraftPageData data = new AdminEmailDraftPageData(account);
        
        data.draftEmailList = logic.getAdminEmailDrafts();
        statusToAdmin = "adminEmailDraftPage Page Load";
        data.init();
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
