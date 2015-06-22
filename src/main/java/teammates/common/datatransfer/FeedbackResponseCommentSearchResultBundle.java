package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.util.Const;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

/**
 * The search result bundle for {@link FeedbackResponseCommentAttributes}. 
 */
public class FeedbackResponseCommentSearchResultBundle extends SearchResultBundle {
    private int numberOfCommentFound = 0;
    public Map<String, List<FeedbackResponseCommentAttributes>> comments = new HashMap<String, List<FeedbackResponseCommentAttributes>>();
    public Map<String, List<FeedbackResponseAttributes>> responses = new HashMap<String, List<FeedbackResponseAttributes>>();
    public Map<String, List<FeedbackQuestionAttributes>> questions = new HashMap<String, List<FeedbackQuestionAttributes>>();
    public Map<String, FeedbackSessionAttributes> sessions = new HashMap<String, FeedbackSessionAttributes>();
    public Map<String, String> commentGiverTable = new HashMap<String, String>();
    public Map<String, String> responseGiverTable = new HashMap<String, String>();
    public Map<String, String> responseRecipientTable = new HashMap<String, String>();
    public Set<String> instructorEmails = new HashSet<String>();
    
    public Cursor cursor = null;
    
    private Set<String> isAdded = new HashSet<String>();
    
    private Set<String> instructorCourseIdList = new HashSet<String>();
    
    private FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    
    public FeedbackResponseCommentSearchResultBundle(){}
    
    public Map<String, List<FeedbackQuestionAttributes>> getQuestions() {
        return questions;
    }

    /**
     * Produce a FeedbackResponseCommentSearchResultBundle from the Results<ScoredDocument> collection
     */
    public FeedbackResponseCommentSearchResultBundle fromResults(Results<ScoredDocument> results, String googleId){
        if(results == null) return this;
        
        //get instructor's information
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        instructorEmails = new HashSet<String>();
        instructorCourseIdList = new HashSet<String>();
        for(InstructorAttributes ins:instructorRoles){
            instructorEmails.add(ins.email);
            instructorCourseIdList.add(ins.courseId);
        }
        
        cursor = results.getCursor();
        List<ScoredDocument> filteredResults = filterOutCourseId(results, googleId);
        for(ScoredDocument doc:filteredResults){
            //get FeedbackResponseComment from results
            FeedbackResponseCommentAttributes comment = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_ATTRIBUTE).getText(), 
                    FeedbackResponseCommentAttributes.class);
            if(frcLogic.getFeedbackResponseComment(comment.getId()) == null){
                frcLogic.deleteDocument(comment);
                continue;
            }
            comment.sendingState = CommentSendingState.SENT;
            List<FeedbackResponseCommentAttributes> commentList = comments.get(comment.feedbackResponseId);
            if(commentList == null){
                commentList = new ArrayList<FeedbackResponseCommentAttributes>();
                comments.put(comment.feedbackResponseId, commentList);
            }
            commentList.add(comment);
            
            //get related response from results
            FeedbackResponseAttributes response = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_ATTRIBUTE).getText(), 
                    FeedbackResponseAttributes.class);
            if(frLogic.getFeedbackResponse(response.getId()) == null){
                frcLogic.deleteDocument(comment);
                continue;
            }
            List<FeedbackResponseAttributes> responseList = responses.get(response.feedbackQuestionId);
            if(responseList == null){
                responseList = new ArrayList<FeedbackResponseAttributes>();
                responses.put(response.feedbackQuestionId, responseList);
            }
            if(!isAdded.contains(response.getId())){
                isAdded.add(response.getId());
                responseList.add(response);
            }
            
            //get related question from results
            FeedbackQuestionAttributes question = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_QUESTION_ATTRIBUTE).getText(), 
                    FeedbackQuestionAttributes.class);
            if(fqLogic.getFeedbackQuestion(question.getId()) == null){
                frcLogic.deleteDocument(comment);
                continue;
            }
            List<FeedbackQuestionAttributes> questionList = questions.get(question.feedbackSessionName);
            if(questionList == null){
                questionList = new ArrayList<FeedbackQuestionAttributes>();
                questions.put(question.feedbackSessionName, questionList);
            }
            if(!isAdded.contains(question.getId())){
                isAdded.add(question.getId());
                questionList.add(question);
            }
            
            //get related session from results
            FeedbackSessionAttributes session = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_SESSION_ATTRIBUTE).getText(), 
                    FeedbackSessionAttributes.class);
            if(fsLogic.getFeedbackSession(session.getSessionName(), session.courseId) == null){
                frcLogic.deleteDocument(comment);
                continue;
            }
            if(!isAdded.contains(session.feedbackSessionName)){
                isAdded.add(session.feedbackSessionName);
                sessions.put(session.getSessionName(), session);
            }
            
            //get giver and recipient names
            String responseGiverName = extractContentFromQuotedString(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_GIVER_NAME).getText());
            responseGiverTable.put(response.getId(), getFilteredGiverName(response, responseGiverName));
            
            String responseRecipientName = extractContentFromQuotedString(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_RECEIVER_NAME).getText());
            responseRecipientTable.put(response.getId(), getFilteredRecipientName(response, responseRecipientName));
            
            String commentGiverName = extractContentFromQuotedString(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_GIVER_NAME).getText());
            commentGiverTable.put(comment.getId().toString(), getFilteredCommentGiverName(response, comment, commentGiverName));
            numberOfCommentFound++;
        }
        
        for (List<FeedbackQuestionAttributes> questions : this.questions.values()) {
            Collections.sort(questions);
        }
        
        for (List<FeedbackResponseAttributes> responses : this.responses.values()) {
            FeedbackResponseAttributes.sortFeedbackResponses(responses);
        }
        
        for (List<FeedbackResponseCommentAttributes> responseComments : this.comments.values()) {
            FeedbackResponseCommentAttributes.sortFeedbackResponseCommentsByCreationTime(responseComments);
        }
        
        return this;
    }
    
    private String getFilteredCommentGiverName(FeedbackResponseAttributes response, FeedbackResponseCommentAttributes comment, String name){
        if (!isCommentGiverNameVisibleToInstructor(response, comment)) {
            name = "Anonymous";
        }
        return name;
    }
    
    private String getFilteredGiverName(FeedbackResponseAttributes response, String name){
        FeedbackQuestionAttributes question = getFeedbackQuestion(response);
        if (!isNameVisibleToInstructor(response, question.showGiverNameTo) 
                && question.giverType != FeedbackParticipantType.SELF) {
            String hash = Integer.toString(Math.abs(name.hashCode()));
            name = question.giverType.toSingularFormString();
            name = "Anonymous " + name + " " + hash;
        }
        return name;
    }
    
    private String getFilteredRecipientName(FeedbackResponseAttributes response, String name){
        FeedbackQuestionAttributes question = getFeedbackQuestion(response);
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
        for(FeedbackQuestionAttributes qn:questions.get(response.feedbackSessionName)){
            if(qn.getId().equals(response.feedbackQuestionId)){
                question = qn;
                break;
            }
        }
        return question;
    }
    
    private boolean isCommentGiverNameVisibleToInstructor(FeedbackResponseAttributes response, 
            FeedbackResponseCommentAttributes comment){
        List<FeedbackParticipantType> showNameTo = comment.showGiverNameTo;
        //in the old ver, name is always visible
        if(comment.isVisibilityFollowingFeedbackQuestion){
            return true;
        }
        
        //comment giver can always see
        if(instructorEmails.contains(comment.giverEmail)){
            return true;
        }
        for(FeedbackParticipantType type:showNameTo){
            if(type == FeedbackParticipantType.GIVER
                    && instructorEmails.contains(response.giverEmail)){
                return true;
            } else if(type == FeedbackParticipantType.INSTRUCTORS
                    && instructorCourseIdList.contains(response.courseId)){
                return true;
            } else if(type == FeedbackParticipantType.RECEIVER
                    && instructorEmails.contains(response.recipientEmail)){
                return true;
            }
        }   
        return false;
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

    @Override
    public int getResultSize() {
        return numberOfCommentFound;
    }
}
