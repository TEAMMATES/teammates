package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.attributes.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.AdminEmailComposePageData;

public class AdminEmailComposeSaveAction extends Action {

    private List<String> addressReceiver = new ArrayList<>();
    private List<String> groupReceiver = new ArrayList<>();

    @Override
    protected ActionResult execute() {

        gateKeeper.verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account, sessionToken);

        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);
        String addressReceiverListString = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS);

        String groupReceiverListFileKey = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);

        String emailId = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);

        addressReceiver.add(addressReceiverListString);

        if (groupReceiverListFileKey != null && !groupReceiverListFileKey.isEmpty()) {
            groupReceiver.add(groupReceiverListFileKey);
        }

        boolean isNewDraft = emailId == null;

        if (isNewDraft) {
            //this is a new email draft, so create a new admin email entity
            createAndSaveNewDraft(subject, addressReceiver, groupReceiver, emailContent);
        } else {
            //currently editing a previous email draft, so we need to update the previous draft
            //instead of creating a new admin email entity

            //retrieve the previous draft email
            AdminEmailAttributes previousDraft = logic.getAdminEmailById(emailId);

            if (previousDraft == null) {
                //the previous draft is not found (eg. deleted by accident when editing)
                createAndSaveNewDraft(subject, addressReceiver, groupReceiver, emailContent);
            } else {
                //the previous draft exists so simply update it with the latest email info
                updatePreviousEmailDraft(previousDraft.getEmailId(), subject, addressReceiver, groupReceiver, emailContent);
            }
        }

        if (isError) {
            data.emailToEdit = AdminEmailAttributes
                    .builder(subject, addressReceiver, groupReceiver, new Text(emailContent))
                    .withEmailId(emailId)
                    .build();
        } else {
            statusToAdmin = Const.StatusMessages.EMAIL_DRAFT_SAVED + ": <br>"
                    + "Subject: " + SanitizationHelper.sanitizeForHtml(subject);
            statusToUser.add(new StatusMessage(Const.StatusMessages.EMAIL_DRAFT_SAVED, StatusMessageColor.SUCCESS));
        }

        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }

    private void updatePreviousEmailDraft(String previousEmailId,
                                          String subject,
                                          List<String> addressReceiver,
                                          List<String> groupReceiver,
                                          String content
                                          ) {

        AdminEmailAttributes newDraft = AdminEmailAttributes
                .builder(subject, addressReceiver, groupReceiver, new Text(content))
                .build();
        try {
            logic.updateAdminEmailById(newDraft, previousEmailId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            isError = true;
            setStatusForException(e);
        }

    }

    private void createAndSaveNewDraft(String subject,
                                       List<String> addressReceiver,
                                       List<String> groupReceiver,
                                       String content) {

        AdminEmailAttributes newDraft = AdminEmailAttributes
                .builder(subject, addressReceiver, groupReceiver, new Text(content))
                .build();
        try {
            logic.createAdminEmail(newDraft);
        } catch (InvalidParametersException e) {
            isError = true;
            setStatusForException(e, e.getMessage());
        }
    }

}
