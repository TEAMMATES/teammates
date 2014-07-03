package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

public class CommentSearchResultBundle {
    
    public List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
    public Cursor cursor = null;
    
    public CommentSearchResultBundle(){}
    
    public CommentSearchResultBundle fromResults(Results<ScoredDocument> results){
        cursor = results.getCursor();
        for(ScoredDocument doc:results){
            CommentAttributes comment = new Gson().fromJson(doc.getOnlyField("attribute").getText(), CommentAttributes.class);
            comment.sendingState = CommentSendingState.SENT;
            this.comments.add(comment);
        }
        return this;
    }
}
