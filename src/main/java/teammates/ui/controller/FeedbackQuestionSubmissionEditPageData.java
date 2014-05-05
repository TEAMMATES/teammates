package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackQuestionBundle;

public class FeedbackQuestionSubmissionEditPageData extends PageData {
    public FeedbackQuestionBundle bundle;
    public boolean isSessionOpenForSubmission;
    
    public FeedbackQuestionSubmissionEditPageData(AccountAttributes account) {
        super(account);
    }
    
    public List<String> getRecipientOptions(String currentlySelectedOption) {
        ArrayList<String> result = new ArrayList<String>();
        
        // Add an empty option first.
        result.add("<option value=\"\" " +
                (currentlySelectedOption==null ? "selected=\"selected\">" : ">") +
                "</option>");
        
        for(Map.Entry<String, String> recipient : bundle.recipientList.entrySet()) {
            result.add("<option value=\"" + recipient.getKey() + "\"" +
                    (recipient.getKey().equals(currentlySelectedOption) 
                        ? " selected=\"selected\"" : "") +
                    ">"+sanitizeForHtml(recipient.getValue())+"</option>");            
        }

        return result;
    }
}
