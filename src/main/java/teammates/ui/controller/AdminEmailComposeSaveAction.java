package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class AdminEmailComposeSaveAction extends Action {
    
    List<String> addressReceiver = new ArrayList<String>();
    List<String> groupReceiver = new ArrayList<String>();
    
    @Override
    protected ActionResult execute() {
        
        new GateKeeper().verifyAdminPrivileges(account);
        AdminEmailComposePageData data = new AdminEmailComposePageData(account);
        
        String emailContent = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_CONTENT);
        String subject = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_SUBJECT);
        String addressReceiverListString = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ADDRESS_RECEVIERS);
        
        String groupReceiverListFileKey = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_GROUP_RECEIVER_LIST_FILE_KEY);    
        

        String emailId = getRequestParamValue(Const.ParamsNames.ADMIN_EMAIL_ID);
        
        addressReceiver.add(addressReceiverListString);
        
        if(groupReceiverListFileKey != null && !groupReceiverListFileKey.isEmpty()){
            groupReceiver.add(groupReceiverListFileKey);
        }
        
        boolean isNewDraft = emailId == null;
        
        if(isNewDraft){
           
            createAndSaveNewDraft(subject, addressReceiver, groupReceiver, emailContent);
        } else {                     
            AdminEmailAttributes previousDraft = logic.getAdminEmailById(emailId);
            
            if(previousDraft == null){
                createAndSaveNewDraft(subject, addressReceiver, groupReceiver, emailContent);
            } else {
                updatePreviousEmailDraft(previousDraft.getEmailId(), subject, addressReceiver, groupReceiver, emailContent);
            }     
            
            statusToAdmin = "Email draft has been saved: <br>" +
                            "Subject: " + subject;
            statusToUser.add("Email draft has been saved");     
        }
        
        return this.createShowPageResult(Const.ViewURIs.ADMIN_EMAIL, data);
    }
    
    private void updatePreviousEmailDraft(String previousEmailId,
                                          String subject, 
                                          List<String> addressReceiver,
                                          List<String> groupReceiver,
                                          String content
                                          ){
        
        AdminEmailAttributes newDraft = new AdminEmailAttributes(subject,
                                                                 addressReceiver,
                                                                 groupReceiver,
                                                                 new Text(content),
                                                                 null);
        try {
            logic.updateAdminEmailById(newDraft, previousEmailId);
        } catch (InvalidParametersException | EntityDoesNotExistException e) {
            deleteGroupReceiverFiles(groupReceiver);
            setStatusForException(e);
        }
        
    }
    
    private void createAndSaveNewDraft(String subject, 
                                       List<String> addressReceiver,
                                       List<String> groupReceiver,
                                       String content){
        
        AdminEmailAttributes newDraft = new AdminEmailAttributes(subject,
                                                                 addressReceiver,
                                                                 groupReceiver,
                                                                 new Text(content),
                                                                 null);
        try {
            logic.createAdminEmail(newDraft);
        } catch (InvalidParametersException e) {
            deleteGroupReceiverFiles(groupReceiver);
            isError = true;
            setStatusForException(e, e.getMessage());
        }
    }
    
    private void deleteGroupReceiverFiles(List<String> groupReceiver){
        for(String key : groupReceiver){
            BlobKey blobKey = new BlobKey(key);
            logic.deleteAdminEmailUploadedFile(blobKey);
        }
    }

}
