package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class CommentSearchResultBundle {
    
    public List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
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
            this.comments.add(comment);
        }
        return this;
    }
}
