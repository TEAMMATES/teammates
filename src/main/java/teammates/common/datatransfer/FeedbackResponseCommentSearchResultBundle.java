package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class FeedbackResponseCommentSearchResultBundle {
    public List<FeedbackResponseCommentAttributes> comments = new ArrayList<FeedbackResponseCommentAttributes>();
    public Map<String, FeedbackResponseAttributes> responses = new HashMap<String, FeedbackResponseAttributes>();
    public Map<String, FeedbackQuestionAttributes> questions = new HashMap<String, FeedbackQuestionAttributes>();
    public Map<String, FeedbackSessionAttributes> sessions = new HashMap<String, FeedbackSessionAttributes>();
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
            this.comments.add(comment);
            
            FeedbackResponseAttributes response = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_RESPONSE_ATTRIBUTE).getText(), 
                    FeedbackResponseAttributes.class);
            this.responses.put(response.getId(), response);
            
            FeedbackQuestionAttributes question = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_QUESTION_ATTRIBUTE).getText(), 
                    FeedbackQuestionAttributes.class);
            this.questions.put(question.getId(), question);
            
            FeedbackSessionAttributes session = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.FEEDBACK_SESSION_ATTRIBUTE).getText(), 
                    FeedbackSessionAttributes.class);
            this.sessions.put(session.getSessionName(), session);
        }
        return this;
    }
}
