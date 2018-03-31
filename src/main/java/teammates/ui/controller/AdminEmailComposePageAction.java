package teammates.ui.controller;

import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.AdminEmailComposePageData;

public class AdminEmailComposePageAction extends Action {

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account, sessionToken);

        String idOfEmailToEdit = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);

        boolean isEmailEdit = idOfEmailToEdit != null;

        if (isEmailEdit) {

            data.emailToEdit = logic.getAdminEmailById(idOfEmailToEdit);
            statusToAdmin =
                    data.emailToEdit == null
                    ? "adminEmailComposePage Page Load : " + Const.StatusMessages.EMAIL_NOT_FOUND
                    : "adminEmailComposePage Page Load : Edit Email "
                      + "[" + SanitizationHelper.sanitizeForHtml(data.emailToEdit.getSubject()) + "]";

            if (data.emailToEdit == null) {
                isError = true;
                statusToUser.add(new StatusMessage(Const.StatusMessages.EMAIL_NOT_FOUND, StatusMessageColor.WARNING));
            }

            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }
        statusToAdmin = "adminEmailComposePage Page Load";
        data.init();

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

}
