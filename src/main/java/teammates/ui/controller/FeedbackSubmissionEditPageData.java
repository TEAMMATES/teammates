package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;

public class FeedbackSubmissionEditPageData extends PageData {

    public FeedbackSessionQuestionsBundle bundle = null;
    public boolean isSessionOpenForSubmission;
    public boolean isPreview;
    public StudentAttributes previewStudent;
    public InstructorAttributes previewInstructor;    
    
    public FeedbackSubmissionEditPageData(AccountAttributes account, StudentAttributes student) {
        super(account, student);
        isPreview = false;
    }
    
    public List<String> getRecipientOptionsForQuestion(String feedbackQuestionId, String currentlySelectedOption) {
        ArrayList<String> result = new ArrayList<String>();        
        if(this.bundle == null) {
            return null;
        }
        
        Map<String, String> emailNamePair = this.bundle.getSortedRecipientList(feedbackQuestionId);
        
        // Add an empty option first.
        result.add("<option value=\"\" " +
                (currentlySelectedOption==null ? "selected=\"selected\">" : ">") +
                "</option>");
        
        for(Map.Entry<String, String> pair : emailNamePair.entrySet()) {
            result.add("<option value=\""+pair.getKey()+"\"" +
                    (pair.getKey().equals(currentlySelectedOption) 
                        ? " selected=\"selected\"" : "") +
                    ">"+sanitizeForHtml(pair.getValue())+"</option>");            
        }

        return result;
    }
}
