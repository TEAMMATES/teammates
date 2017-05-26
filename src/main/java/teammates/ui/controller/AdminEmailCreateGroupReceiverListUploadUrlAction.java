package teammates.ui.controller;

import com.google.appengine.api.blobstore.BlobstoreFailureException;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.Url;
import teammates.ui.pagedata.AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData;

public class AdminEmailCreateGroupReceiverListUploadUrlAction extends Action {

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);

        AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData data =
                new AdminEmailCreateGroupReceiverListUploadUrlAjaxPageData(account, sessionToken);

        try {
            String callbackUrl = Url.addParamToUrl(Const.ActionURIs.ADMIN_EMAIL_GROUP_RECEIVER_LIST_UPLOAD,
                                                   Const.ParamsNames.SESSION_TOKEN,
                                                   sessionToken);
            data.nextUploadUrl =
                    GoogleCloudStorageHelper.getNewUploadUrl(callbackUrl);
            data.ajaxStatus = "Group receiver list upload url created, proceed to uploading";
        } catch (BlobstoreFailureException | IllegalArgumentException e) {
            data.nextUploadUrl = null;
            isError = true;
            data.ajaxStatus = "An error occurred when creating upload URL, please try again";
        }

        return createAjaxResult(data);

    }

}
