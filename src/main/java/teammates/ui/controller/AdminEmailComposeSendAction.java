package teammates.ui.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.Const.StatusMessageColor;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.FieldValidator;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.common.util.StatusMessage;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.TaskQueuesLogic;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

public class AdminEmailComposeSendAction extends Action {
    
    private List<String> addressReceiver = new ArrayList<String>();
    private List<String> groupReceiver = new ArrayList<String>();
    
    private boolean addressModeOn;
    private boolean groupModeOn;
    
    //params needed to move heavy jobs into a address mode task
    private String addressReceiverListString;
    
    //params needed to move heavy jobs into a group mode task
    private String groupReceiverListFileKey;
    private String emailId;
    
    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        
        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);
        
        addressReceiverListString = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS);
        addressModeOn = addressReceiverListString != null && !addressReceiverListString.isEmpty();
        emailId = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);
        groupReceiverListFileKey = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);
        groupModeOn = groupReceiverListFileKey != null && !groupReceiverListFileKey.isEmpty();
        
        if (groupModeOn) {
            try {
                groupReceiver.add(groupReceiverListFileKey);
                GoogleCloudStorageHelper.getGroupReceiverList(new BlobKey(groupReceiverListFileKey));
            } catch (Exception e) {
                isError = true;
                setStatusForException(e, "An error occurred when retrieving receiver list, please try again");
            }
        }
        
        if (addressModeOn) {
            addressReceiver.add(addressReceiverListString);
            try {
                checkAddressReceiverString(addressReceiverListString);
            } catch (InvalidParametersException e) {
                isError = true;
                setStatusForException(e);
            }
        }
        
        if (!addressModeOn && !groupModeOn) {
            isError = true;
            statusToAdmin = "Error : No reciver address or file given";
            statusToUser.add(new StatusMessage("Error : No reciver address or file given", StatusMessageColor.DANGER));
        }
        
        if (isError) {
            data.emailToEdit = new AdminEmailAttributes(subject,
                                                        addressReceiver,
                                                        groupReceiver,
                                                        new Text(emailContent),
                                                        null);
            data.emailToEdit.emailId = emailId;
            return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
        }

        boolean isEmailDraft = emailId != null && !emailId.isEmpty();
        
        if (isEmailDraft) {
            updateDraftEmailToSent(emailId, subject, addressReceiver, groupReceiver, emailContent);
        } else {
            recordNewSentEmail(subject, addressReceiver, groupReceiver, emailContent);
        }
 
        if (isError) {
            data.emailToEdit = new AdminEmailAttributes(subject,
                                                        addressReceiver,
                                                        groupReceiver,
                                                        new Text(emailContent),
                                                        null);
            data.emailToEdit.emailId = emailId;
        }
        
        return createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }
    
    private void checkAddressReceiverString(String addressReceiverString) throws InvalidParametersException {
        FieldValidator validator = new FieldValidator();
       
        String[] emails = addressReceiverString.split(",");
        for (String email : emails) {
            String error = validator.getInvalidityInfoForEmail(email);
            if (error != null && !error.isEmpty()) {
                isError = true;
                statusToUser.add(new StatusMessage(error, StatusMessageColor.DANGER));
                throw new InvalidParametersException("<strong>Email Format Error</strong>");
            }
        }
       
    }
    
    private void moveJobToGroupModeTaskQueue() {
        if (!groupModeOn) {
            return;
        }
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
        paramMap.put(ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY, groupReceiverListFileKey);
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_LIST_INDEX, "0");
        paramMap.put(ParamsNames.ADMIN_GROUP_RECEIVER_EMAIL_INDEX, "0");
        paramMap.put(ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE, Const.ADMIN_EMAIL_TASK_QUEUE_GROUP_MODE);
        
        taskQueueLogic.createAndAddTask(SystemParams.ADMIN_PREPARE_EMAIL_TASK_QUEUE,
                Const.ActionURIs.ADMIN_EMAIL_PREPARE_TASK_QUEUE_WORKER, paramMap);

    }
    
    private void moveJobToAddressModeTaskQueue() {
        
        if (!addressModeOn) {
            return;
        }
        
        TaskQueuesLogic taskQueueLogic = TaskQueuesLogic.inst();
        
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put(ParamsNames.ADMIN_EMAIL_ID, emailId);
        paramMap.put(ParamsNames.ADMIN_EMAIL_TASK_QUEUE_MODE, Const.ADMIN_EMAIL_TASK_QUEUE_ADDRESS_MODE);
        paramMap.put(ParamsNames.ADMIN_EMAIL_ADDRESS_RECEIVERS, addressReceiverListString);
        
        taskQueueLogic.createAndAddTask(SystemParams.ADMIN_PREPARE_EMAIL_TASK_QUEUE,
                Const.ActionURIs.ADMIN_EMAIL_PREPARE_TASK_QUEUE_WORKER, paramMap);

    }

    private void recordNewSentEmail(String subject,
                                    List<String> addressReceiver,
                                    List<String> groupReceiver,
                                    String content) {

        AdminEmailAttributes newDraft = new AdminEmailAttributes(subject,
                                                                 addressReceiver,
                                                                 groupReceiver,
                                                                 new Text(content),
                                                                 new Date());
        try {
            Date createDate = logic.createAdminEmail(newDraft);
            emailId = logic.getAdminEmail(subject, createDate).getEmailId();
        } catch (Exception e) {
            isError = true;
            setStatusForException(e, e.getMessage());
            return;
        }
        
        moveJobToGroupModeTaskQueue();
        moveJobToAddressModeTaskQueue();
    }

    private void updateDraftEmailToSent(String emailId,
                                        String subject,
                                        List<String> addressReceiver,
                                        List<String> groupReceiver,
                                        String content) {
        
        AdminEmailAttributes fanalisedEmail = new AdminEmailAttributes(subject,
                                            addressReceiver,
                                            groupReceiver,
                                            new Text(content),
                                            new Date());
        
        try {
            logic.updateAdminEmailById(fanalisedEmail, emailId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            isError = true;
            setStatusForException(e);
            return;
        }
        moveJobToGroupModeTaskQueue();
        moveJobToAddressModeTaskQueue();
    }

}
