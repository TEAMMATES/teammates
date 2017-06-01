package teammates.ui.automated;

import java.io.IOException;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.apphosting.api.ApiProxy;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.Logger;

/**
 * Task queue worker action: prepares admin email to be sent via task queue in group mode,
 * i.e. using the group receiver list retrieved from the Google Cloud Storage (GCS).
 */
public class AdminPrepareEmailGroupModeWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        log.info("Preparing admin email task queue in group mode...");

        String emailId = getRequestParamValue(ParamsNames.ADMIN_EMAIL_ID);
        Assumption.assertPostParamNotNull(ParamsNames.ADMIN_EMAIL_ID, emailId);

        String groupReceiverListFileKey = getRequestParamValue(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);
        Assumption.assertPostParamNotNull(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY, groupReceiverListFileKey);

        String indexOfEmailListToResumeAsString =
                getRequestParamValue(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_LIST_INDEX);
        String indexOfEmailToResumeAsString = getRequestParamValue(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_INDEX);

        int indexOfEmailListToResume = indexOfEmailListToResumeAsString == null
                                       ? 0
                                       : Integer.parseInt(indexOfEmailListToResumeAsString);
        int indexOfEmailToResume = indexOfEmailToResumeAsString == null
                                   ? 0
                                   : Integer.parseInt(indexOfEmailToResumeAsString);

        try {
            List<List<String>> processedReceiverEmails =
                    GoogleCloudStorageHelper.getGroupReceiverList(new BlobKey(groupReceiverListFileKey));
            addAdminEmailToTaskQueue(emailId, groupReceiverListFileKey, processedReceiverEmails,
                                     indexOfEmailListToResume, indexOfEmailToResume);
        } catch (IOException e) {
            log.severe("Unexpected error while adding admin email tasks: "
                       + TeammatesException.toStringWithStackTrace(e));
        }
    }

    private boolean isNearDeadline() {
        long timeLeftInMillis = ApiProxy.getCurrentEnvironment().getRemainingMillis();
        return timeLeftInMillis / 1000 < 100;
    }

    private void addAdminEmailToTaskQueue(String emailId, String groupReceiverListFileKey,
            List<List<String>> processedReceiverEmails,
            int indexOfEmailListToResume, int indexOfEmailToResume) {
        AdminEmailAttributes adminEmail = logic.getAdminEmailById(emailId);
        Assumption.assertNotNull(adminEmail);

        log.info("Resume adding group mail tasks for mail with id " + emailId + " from list index: "
                 + indexOfEmailListToResume + " and email index: " + indexOfEmailToResume);

        int indexOfLastEmailList = 0;
        int indexOfLastEmail = 0;

        for (int i = indexOfEmailListToResume; i < processedReceiverEmails.size(); i++) {
            List<String> currentEmailList = processedReceiverEmails.get(i);
            for (int j = indexOfEmailToResume; j < currentEmailList.size(); j++) {
                String receiverEmail = currentEmailList.get(j);
                taskQueuer.scheduleAdminEmailForSending(emailId, receiverEmail, adminEmail.getSubject(),
                                                        adminEmail.getContentValue());
                if (isNearDeadline()) {
                    taskQueuer.scheduleAdminEmailPreparationInGroupMode(emailId, groupReceiverListFileKey, i, j);
                    log.info("Adding group mail tasks for mail with id " + emailId
                             + " have been paused with list index: " + i + " and email index: " + j);
                    return;
                }
                indexOfLastEmail = j;
            }
            indexOfLastEmailList = i;
        }

        log.info("Adding group mail tasks for mail with id " + emailId
                 + "was complete with last reached list index: " + indexOfLastEmailList
                 + " and last reached email index: " + indexOfLastEmail);
    }

}
