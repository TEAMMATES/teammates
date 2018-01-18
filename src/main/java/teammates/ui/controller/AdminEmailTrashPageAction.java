package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.ui.pagedata.AdminEmailTrashPageData;

public class AdminEmailTrashPageAction extends Action {

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyAdminPrivileges(account);
        AdminEmailTrashPageData data = new AdminEmailTrashPageData(account, sessionToken);

        data.adminTrashEmailList = logic.getAdminEmailsInTrashBin();
        statusToAdmin = "adminEmailTrashPage Page Load";
        data.init();

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
