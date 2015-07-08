package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.InstructorsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.google.gson.Gson;

/**
 * The search result bundle for {@link CommentAttributes}. 
 */
public class CommentSearchResultBundle extends SearchResultBundle {
    
    public Map<String, List<CommentAttributes>> giverCommentTable = new TreeMap<String, List<CommentAttributes>>();
    public Map<String, String> giverTable = new HashMap<String, String>();
    public Map<String, String> recipientTable = new HashMap<String, String>();
    public Cursor cursor = null;
    private int numberOfResults = 0;
    private CommentsLogic commentsLogic = CommentsLogic.inst();
    
    public CommentSearchResultBundle(){}
    
    /**
     * Produce a CommentSearchResultBundle from the Results<ScoredDocument> collection
     */
    public CommentSearchResultBundle fromResults(Results<ScoredDocument> results, String googleId){
        if(results == null) return this;
        
        cursor = results.getCursor();
        List<InstructorAttributes> instructorRoles = InstructorsLogic.inst().getInstructorsForGoogleId(googleId);
        List<String> giverEmailList = new ArrayList<String>();
        for(InstructorAttributes ins:instructorRoles){
            giverEmailList.add(ins.email);
        }
        
        List<ScoredDocument> filteredResults = filterOutCourseId(results, googleId);
        for(ScoredDocument doc:filteredResults){
            CommentAttributes comment = new Gson().fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.COMMENT_ATTRIBUTE).getText(), 
                    CommentAttributes.class);
            if(commentsLogic.getComment(comment.getCommentId()) == null){
                commentsLogic.deleteDocument(comment);
                continue;
            }
            comment.sendingState = CommentSendingState.SENT;
            String giverName = doc.getOnlyField(Const.SearchDocumentField.COMMENT_GIVER_NAME).getText();
            String recipientName = doc.getOnlyField(Const.SearchDocumentField.COMMENT_RECIPIENT_NAME).getText();
            
            boolean isGiver = giverEmailList.contains(comment.giverEmail);
            String giverAsKey = comment.giverEmail + comment.courseId;
            
            if(!isGiver && !comment.showGiverNameTo.contains(CommentParticipantType.INSTRUCTOR)){
                giverAsKey = "Anonymous" + comment.courseId;
                giverName = "Anonymous" + " (" + comment.courseId + ")";
            } else if (isGiver) {
                giverName = "You (" + comment.courseId + ")";
            } else {
                giverName = extractContentFromQuotedString(giverName) + " (" + comment.courseId + ")";
            }
            
            if(!isGiver && !comment.showRecipientNameTo.contains(CommentParticipantType.INSTRUCTOR)){
                recipientName = "Anonymous";
            } else {
                recipientName = extractContentFromQuotedString(recipientName);
            }
            
            List<CommentAttributes> commentList = giverCommentTable.get(giverAsKey);
            if(commentList == null){
                commentList = new ArrayList<CommentAttributes>();
                giverCommentTable.put(giverAsKey, commentList);
            }
            commentList.add(comment);
            giverTable.put(giverAsKey, giverName);
            recipientTable.put(comment.getCommentId().toString(), recipientName);
            numberOfResults++;
        }
        
        for (List<CommentAttributes> comments : this.giverCommentTable.values()) {
            CommentAttributes.sortCommentsByCreationTime(comments);
        }
        
        return this;
    }

    @Override
    public int getResultSize() {
        return numberOfResults;
    }
}
