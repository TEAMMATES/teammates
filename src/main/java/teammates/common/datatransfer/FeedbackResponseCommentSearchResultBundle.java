package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.util.Const;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class FeedbackResponseCommentSearchResultBundle extends SearchResultBundle {
    public int numberOfCommentFound = 0;
    public Map<String, List<FeedbackResponseCommentAttributes>> comments = new HashMap<String, List<FeedbackResponseCommentAttributes>>();
    public Map<String, List<FeedbackResponseAttributes>> responses = new HashMap<String, List<FeedbackResponseAttributes>>();
    public Map<String, List<FeedbackQuestionAttributes>> questions = new HashMap<String, List<FeedbackQuestionAttributes>>();
    public Map<String, FeedbackSessionAttributes> sessions = new HashMap<String, FeedbackSessionAttributes>();
    public Map<String, String> commentGiverTable = new HashMap<String, String>();
    public Map<String, String> responseGiverEmailTable = new HashMap<String, String>();
    public Map<String, String> responseGiverTable = new HashMap<String, String>();
    public Map<String, String> responseRecipientTable = new HashMap<String, String>();
    public Set<String> isAdded = new HashSet<String>();
    public Cursor cursor = null;
    
    public FeedbackResponseCommentSearchResultBundle(){}

    public FeedbackResponseCommentSearchResultBundle fromResults(Results<ScoredDocument> results){
        if(results == null) return this;
        
        cursor = results.getCursor();
        for(ScoredDocument doc:results){
            FeedbackResponseCommentAttributes comment = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_ATTRIBUTE).getText(), 
                    FeedbackResponseCommentAttributes.class);
            comment.sendingState = CommentSendingState.SENT;
            List<FeedbackResponseCommentAttributes> commentList = comments.get(comment.feedbackResponseId);
            if(commentList == null){
                commentList = new ArrayList<FeedbackResponseCommentAttributes>();
                comments.put(comment.feedbackResponseId, commentList);
            }
            commentList.add(comment);
            numberOfCommentFound++;
            
            FeedbackResponseAttributes response = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_ATTRIBUTE).getText(), 
                    FeedbackResponseAttributes.class);
            List<FeedbackResponseAttributes> responseList = responses.get(response.feedbackQuestionId);
            if(responseList == null){
                responseList = new ArrayList<FeedbackResponseAttributes>();
                responses.put(response.feedbackQuestionId, responseList);
            }
            if(!isAdded.contains(response.getId())){
                isAdded.add(response.getId());
                responseList.add(response);
            }
            
            FeedbackQuestionAttributes question = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_QUESTION_ATTRIBUTE).getText(), 
                    FeedbackQuestionAttributes.class);
            List<FeedbackQuestionAttributes> questionList = questions.get(question.feedbackSessionName);
            if(questionList == null){
                questionList = new ArrayList<FeedbackQuestionAttributes>();
                questions.put(question.feedbackSessionName, questionList);
            }
            if(!isAdded.contains(question.getId())){
                isAdded.add(question.getId());
                questionList.add(question);
            }
            
            FeedbackSessionAttributes session = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_SESSION_ATTRIBUTE).getText(), 
                    FeedbackSessionAttributes.class);
            if(!isAdded.contains(session.feedbackSessionName)){
                isAdded.add(session.feedbackSessionName);
                this.sessions.put(session.getSessionName(), session);
            }
            
            responseGiverEmailTable.put(response.getId(), response.giverEmail);
            
            String responseGiverName = doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_GIVER_NAME).getText();
            responseGiverTable.put(response.getId(), extractContentFromQuotedString(responseGiverName));
            
            String responseRecipientName = doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_RECEIVER_NAME).getText();
            responseRecipientTable.put(response.getId(), extractContentFromQuotedString(responseRecipientName));
            
            String commentGiverName = doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_COMMENT_GIVER_NAME).getText();
            commentGiverTable.put(comment.getId().toString(), extractContentFromQuotedString(commentGiverName));
        }
        return this;
    }
}
