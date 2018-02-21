package teammates.ui.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.Logger;
import teammates.ui.pagedata.AdminEmailComposePageData;

public class AdminEmailGroupReceiverListUploadAction extends Action {

    private static final Logger log = Logger.getLogger();

    private AdminEmailComposePageData data;

    @Override
    protected ActionResult execute() {
        gateKeeper.verifyAdminPrivileges(account);

        data = new AdminEmailComposePageData(account, sessionToken);
        BlobInfo blobInfo = extractGroupReceiverListFileKey();

        if (blobInfo == null) {
            data.isFileUploaded = false;
            data.fileSrcUrl = null;

            log.info("Group Receiver List Upload Failed");
            statusToAdmin = "Group Receiver List Upload Failed";
            data.ajaxStatus = "Group receiver list upload failed. Please try again.";
            return createAjaxResult(data);
        }

        try {
            List<List<String>> groupReceiverList =
                    GoogleCloudStorageHelper.getGroupReceiverList(blobInfo.getBlobKey());

            // log all email addresses retrieved from the txt file
            int i = 0;

            for (List<String> list : groupReceiverList) {
                for (String str : list) {
                    log.info(str + " - " + i + " \n");
                    i++;
                }
            }
        } catch (IOException e) {
            data.isFileUploaded = false;
            data.fileSrcUrl = null;

            log.info("Group Receiver List Upload Failed: uploaded file is corrupted");
            statusToAdmin = "Group Receiver List Upload Failed: uploaded file is corrupted";
            data.ajaxStatus = "Group receiver list upload failed: uploaded file is corrupted. "
                              + "Please make sure the txt file contains only email addresses "
                              + "separated by comma";
            deleteGroupReceiverListFile(blobInfo.getBlobKey());
            return createAjaxResult(data);
        }

        BlobKey blobKey = blobInfo.getBlobKey();

        data.groupReceiverListFileKey = blobKey.getKeyString();

        data.isFileUploaded = true;
        statusToAdmin = "New Group Receiver List Uploaded";
        data.ajaxStatus = "Group receiver list successfully uploaded to Google Cloud Storage";

        return createAjaxResult(data);
    }

    private BlobInfo extractGroupReceiverListFileKey() {
        try {
            Map<String, List<BlobInfo>> blobsMap = BlobstoreServiceFactory.getBlobstoreService().getBlobInfos(request);
            List<BlobInfo> blobs = blobsMap.get(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_TO_UPLOAD);

            if (blobs == null || blobs.isEmpty()) {
                data.ajaxStatus = Const.StatusMessages.NO_GROUP_RECEIVER_LIST_FILE_GIVEN;
                isError = true;
                return null;
            }

            BlobInfo groupReceiverListFile = blobs.get(0);
            return validateGroupReceiverListFile(groupReceiverListFile);
        } catch (IllegalStateException e) {
            return null;
        }
    }

    private BlobInfo validateGroupReceiverListFile(BlobInfo groupReceiverListFile) {
        if (!groupReceiverListFile.getContentType().contains("text/")) {
            deleteGroupReceiverListFile(groupReceiverListFile.getBlobKey());
            isError = true;
            data.ajaxStatus = Const.StatusMessages.NOT_A_RECEIVER_LIST_FILE;
            return null;
        }

        return groupReceiverListFile;
    }

    private void deleteGroupReceiverListFile(BlobKey blobKey) {
        if (blobKey.equals(new BlobKey(""))) {
            return;
        }

        try {
            logic.deleteAdminEmailUploadedFile(blobKey);
        } catch (BlobstoreFailureException bfe) {
            statusToAdmin = Const.ACTION_RESULT_FAILURE
                    + " : Unable to delete group receiver list file (possible unused file with key: "
                    + blobKey.getKeyString()
                    + " || Error Message: "
                    + bfe.getMessage() + System.lineSeparator();
        }
    }

}
