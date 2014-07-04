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

public class CommentSearchResultBundle extends SearchResultBundle {
    
    public Map<String, List<CommentAttributes>> giverCommentTable = new HashMap<String, List<CommentAttributes>>();
    public Map<String, String> giverTable = new HashMap<String, String>();
    public Map<String, String> recipientTable = new HashMap<String, String>();
    public Cursor cursor = null;
    
    public CommentSearchResultBundle(){}
    
    public CommentSearchResultBundle fromResults(Results<ScoredDocument> results){
        if(results == null) return this;
        
        cursor = results.getCursor();
        for(ScoredDocument doc:results){
            CommentAttributes comment = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.COMMENT_ATTRIBUTE).getText(), 
                    CommentAttributes.class);
            comment.sendingState = CommentSendingState.SENT;
            List<CommentAttributes> commentList = giverCommentTable.get(comment.giverEmail+comment.courseId);
            if(commentList == null){
                commentList = new ArrayList<CommentAttributes>();
                giverCommentTable.put(comment.giverEmail+comment.courseId, commentList);
            }
            commentList.add(comment);
            String giverName = doc.getOnlyField(Const.SearchDocumentField.COMMENT_GIVER_NAME).getText();
            String recipientName = doc.getOnlyField(Const.SearchDocumentField.COMMENT_RECIPIENT_NAME).getText();
            giverTable.put(comment.giverEmail+comment.courseId, extractContentFromQuotedString(giverName) + " (" + comment.courseId + ")");
            recipientTable.put(comment.getCommentId().toString(), extractContentFromQuotedString(recipientName));
        }
        return this;
    }
}
