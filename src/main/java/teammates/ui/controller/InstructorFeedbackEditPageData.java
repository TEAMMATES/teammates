package teammates.ui.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.util.Const;

public class InstructorFeedbackEditPageData extends PageData {

    public FeedbackSessionAttributes session;
    public FeedbackQuestionAttributes newQuestion;
    public List<FeedbackQuestionAttributes> questions;
    public List<FeedbackQuestionAttributes> copiableQuestions;
    public Map<String, Boolean> questionHasResponses;
    public List<StudentAttributes> studentList;
    public List<InstructorAttributes> instructorList;
    public InstructorAttributes instructor;

    public InstructorFeedbackEditPageData(AccountAttributes account) {
        super(account);
        questionHasResponses = new HashMap<String, Boolean>();
    }
    
    public List<String> getParticipantOptions(
            FeedbackQuestionAttributes question, boolean isGiver){
        List<String> result = new ArrayList<String>();
        for(FeedbackParticipantType option : FeedbackParticipantType.values()) {
            if(isGiver && option.isValidGiver()) {
                result.add("<option value=\""+option.toString()+"\""
                        +(question!=null && question.giverType==option
                                ? " selected=\"selected\"" : "")
                        +">"+option.toDisplayGiverName()+"</option>");
            } else if(!isGiver && option.isValidRecipient()) {
                result.add("<option value=\""+option.toString()+"\""
                        +(question!=null && question.recipientType==option
                                ? " selected=\"selected\"" : "")
                        +">"+option.toDisplayRecipientName()+"</option>");
            }
        }        
        return result;
    }
    
    public ArrayList<String> getTimeZoneOptionsAsHtml(){
        return getTimeZoneOptionsAsHtml(session == null? Const.DOUBLE_UNINITIALIZED : session.timeZone);
    }
    
    
    public ArrayList<String> getGracePeriodOptionsAsHtml(){
        return getGracePeriodOptionsAsHtml(session == null ? Const.INT_UNINITIALIZED : session.gracePeriod);
    }
}