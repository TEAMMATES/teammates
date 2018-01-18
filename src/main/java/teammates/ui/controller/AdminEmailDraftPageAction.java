package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.ui.pagedata.AdminEmailDraftPageData;

public class AdminEmailDraftPageAction extends Action {

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyAdminPrivileges(account);
        AdminEmailDraftPageData data = new AdminEmailDraftPageData(account, sessionToken);

        data.draftEmailList = logic.getAdminEmailDrafts();
        statusToAdmin = "adminEmailDraftPage Page Load";
        data.init();

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
