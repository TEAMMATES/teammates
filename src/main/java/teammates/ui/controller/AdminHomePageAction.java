package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.ui.pagedata.AdminHomePageData;

public class AdminHomePageAction extends Action {

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);

        AdminHomePageData data = new AdminHomePageData(account, sessionToken);

        data.instructorName = "";
        data.instructorEmail = "";
        data.instructorInstitution = "";

        statusToAdmin = "Admin Home Page Load";

        return createShowPageResult(Const.ViewURIs.ADMIN_HOME, data);
    }

}
