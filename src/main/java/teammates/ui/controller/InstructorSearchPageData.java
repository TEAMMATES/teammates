package teammates.ui.controller;

import java.util.List;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;

public class InstructorSearchPageData extends PageData {

    public CommentSearchResultBundle commentSearchResultBundle = new CommentSearchResultBundle();
    public FeedbackResponseCommentSearchResultBundle feedbackResponseCommentSearchResultBundle = new FeedbackResponseCommentSearchResultBundle();
    public String searchKey = "";
    public Set<String> instructorEmails;
    public Set<String> instructorCourseIdList;
    public int totalResultsSize;
    public boolean isSearchCommentForStudents;
    public boolean isSearchCommentForResponses;
    
    public InstructorSearchPageData(AccountAttributes account) {
        super(account);
    }
    
    public String getGiverName(FeedbackResponseAttributes response){
        FeedbackQuestionAttributes question = getFeedbackQuestion(response);
        String name = feedbackResponseCommentSearchResultBundle.responseGiverTable.get(response.getId());
        if (!isNameVisibleToInstructor(response, question.showGiverNameTo) 
                && question.giverType != FeedbackParticipantType.SELF) {
            String hash = Integer.toString(Math.abs(name.hashCode()));
            name = question.giverType.toSingularFormString();
            name = "Anonymous " + name + " " + hash;
        }
        return name;
    }
    
    public String getRecipientName(FeedbackResponseAttributes response){
        FeedbackQuestionAttributes question = getFeedbackQuestion(response);
        String name = feedbackResponseCommentSearchResultBundle.responseRecipientTable.get(response.getId());
        if (!isNameVisibleToInstructor(response, question.showRecipientNameTo) 
                && question.recipientType != FeedbackParticipantType.SELF 
                && question.recipientType != FeedbackParticipantType.NONE) {
            String hash = Integer.toString(Math.abs(name.hashCode()));
            name = question.recipientType.toSingularFormString();
            name = "Anonymous " + name + " " + hash;
        }
        return name;
    }
    
    private FeedbackQuestionAttributes getFeedbackQuestion(
            FeedbackResponseAttributes response) {
        FeedbackQuestionAttributes question = null;
        for(FeedbackQuestionAttributes qn:feedbackResponseCommentSearchResultBundle
                .questions.get(response.feedbackSessionName)){
            if(qn.getId().equals(response.feedbackQuestionId)){
                question = qn;
                break;
            }
        }
        return question;
    }
    
    private boolean isNameVisibleToInstructor(FeedbackResponseAttributes response, List<FeedbackParticipantType> showNameTo){
        //giver can always see
        if(instructorEmails.contains(response.giverEmail)){
            return true;
        }
        for(FeedbackParticipantType type:showNameTo){
            if(type == FeedbackParticipantType.INSTRUCTORS
                    && instructorCourseIdList.contains(response.courseId)){
                return true;
            } else if(type == FeedbackParticipantType.RECEIVER
                    && instructorEmails.contains(response.recipientEmail)){
                return true;
            }
        }   
        return false;
    }
}
