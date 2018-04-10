package teammates.ui.controller;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.AdminEmailComposePageData;

public class AdminEmailComposeSendAction extends Action {

    private List<String> addressReceiver = new ArrayList<>();
    private List<String> groupReceiver = new ArrayList<>();

    private boolean isAddressModeOn;
    private boolean isGroupModeOn;

    //params needed to move heavy jobs into a address mode task
    private String addressReceiverListString;

    //params needed to move heavy jobs into a group mode task
    private String groupReceiverListFileKey;
    private String emailId;

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account, sessionToken);

        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);

        addressReceiverListString = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS);
        isAddressModeOn = addressReceiverListString != null && !addressReceiverListString.isEmpty();
        emailId = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);
        groupReceiverListFileKey = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);
        isGroupModeOn = groupReceiverListFileKey != null && !groupReceiverListFileKey.isEmpty();

        if (isGroupModeOn) {
            try {
                groupReceiver.add(groupReceiverListFileKey);
                GoogleCloudStorageHelper.getGroupReceiverList(new BlobKey(groupReceiverListFileKey));
            } catch (Exception e) {
                isError = true;
                setStatusForException(e, "An error occurred when retrieving receiver list, please try again");
            }
        }

        if (isAddressModeOn) {
            addressReceiver.add(addressReceiverListString);
            try {
                checkAddressReceiverString(addressReceiverListString);
            } catch (InvalidParametersException e) {
                isError = true;
                setStatusForException(e);
            }
        }

        if (!isAddressModeOn && !isGroupModeOn) {
            isError = true;
            statusToAdmin = "Error : No receiver address or file given";
            statusToUser.add(new StatusMessage("Error : No receiver address or file given", StatusMessageColor.DANGER));
        }

        if (isError) {
            data.emailToEdit = AdminEmailAttributes
                    .builder(subject, addressReceiver, groupReceiver, new Text(emailContent))
                    .withEmailId(emailId)
                    .build();

            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }

        boolean isEmailDraft = emailId != null && !emailId.isEmpty();

        if (isEmailDraft) {
            updateDraftEmailToSent(emailId, subject, addressReceiver, groupReceiver, emailContent);
        } else {
            recordNewSentEmail(subject, addressReceiver, groupReceiver, emailContent);
        }

        if (isError) {
            data.emailToEdit = AdminEmailAttributes
                    .builder(subject, addressReceiver, groupReceiver, new Text(emailContent))
                    .withEmailId(emailId)
                    .build();
        }

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

    private void checkAddressReceiverString(String addressReceiverString) throws InvalidParametersException {
        FieldValidator validator = new FieldValidator();

        String[] emails = addressReceiverString.split(",");
        for (String email : emails) {
            String error = validator.getInvalidityInfoForEmail(email);
            if (!error.isEmpty()) {
                isError = true;
                statusToUser.add(new StatusMessage(error, StatusMessageColor.DANGER));
                throw new InvalidParametersException("<strong>Email Format Error</strong>");
            }
        }

    }

    private void moveJobToGroupModeTaskQueue() {
        if (!isGroupModeOn) {
            return;
        }
        taskQueuer.scheduleAdminEmailPreparationInGroupMode(emailId, groupReceiverListFileKey, 0, 0);

        statusToAdmin += "<br/>" + "Group receiver's list " + groupReceiverListFileKey;
        statusToUser.add(new StatusMessage("Email will be sent within an hour to uploaded group receiver's list.",
                     StatusMessageColor.SUCCESS));
    }

    private void moveJobToAddressModeTaskQueue() {
        if (!isAddressModeOn) {
            return;
        }
        taskQueuer.scheduleAdminEmailPreparationInAddressMode(emailId, addressReceiverListString);

        statusToAdmin += "<br/>" + "Recipient: " + addressReceiverListString;
        statusToUser.add(new StatusMessage("Email will be sent within an hour to " + addressReceiverListString,
                     StatusMessageColor.SUCCESS));
    }

    private void recordNewSentEmail(String subject,
                                    List<String> addressReceiver,
                                    List<String> groupReceiver,
                                    String content) {

        AdminEmailAttributes newDraft = AdminEmailAttributes
                .builder(subject, addressReceiver, groupReceiver, new Text(content))
                .withSendDate(Instant.now())
                .build();
        try {
            Instant createDate = logic.createAdminEmail(newDraft);
            emailId = logic.getAdminEmail(subject, createDate).getEmailId();
        } catch (Exception e) {
            isError = true;
            setStatusForException(e, e.getMessage());
            return;
        }
        statusToAdmin = "Email queued for sending.";

        moveJobToGroupModeTaskQueue();
        moveJobToAddressModeTaskQueue();
    }

    private void updateDraftEmailToSent(String emailId,
                                        String subject,
                                        List<String> addressReceiver,
                                        List<String> groupReceiver,
                                        String content) {

        AdminEmailAttributes finalisedEmail = AdminEmailAttributes
                .builder(subject, addressReceiver, groupReceiver, new Text(content))
                .withSendDate(Instant.now())
                .build();

        try {
            logic.updateAdminEmailById(finalisedEmail, emailId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            isError = true;
            setStatusForException(e);
            return;
        }
        moveJobToGroupModeTaskQueue();
        moveJobToAddressModeTaskQueue();
    }

}
