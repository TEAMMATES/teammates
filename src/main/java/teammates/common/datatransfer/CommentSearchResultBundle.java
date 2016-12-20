package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import teammates.common.util.Const;
import teammates.common.util.JsonUtils;
import teammates.logic.core.CommentsLogic;

import com.google.appengine.api.search.Cursor;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

/**
 * The search result bundle for {@link CommentAttributes}.
 */
public class CommentSearchResultBundle extends SearchResultBundle {
    
    public Map<String, List<CommentAttributes>> giverCommentTable = new TreeMap<String, List<CommentAttributes>>();
    public Map<String, String> giverTable = new HashMap<String, String>();
    public Map<String, String> recipientTable = new HashMap<String, String>();
    public Cursor cursor;
    private int numberOfResults;
    private CommentsLogic commentsLogic = CommentsLogic.inst();
    
    /**
     * Produce a CommentSearchResultBundle from the Results<ScoredDocument> collection.
     * The list of InstructorAttributes is used to filter out the search result.
     */
    public CommentSearchResultBundle fromResults(Results<ScoredDocument> results,
                                                 List<InstructorAttributes> instructors) {
        if (results == null) {
            return this;
        }
        
        cursor = results.getCursor();
        List<String> giverEmailList = new ArrayList<String>();
        for (InstructorAttributes ins : instructors) {
            giverEmailList.add(ins.email);
        }
        
        List<ScoredDocument> filteredResults = filterOutCourseId(results, instructors);
        for (ScoredDocument doc : filteredResults) {
            CommentAttributes comment = JsonUtils.fromJson(
                    doc.getOnlyField(Const.SearchDocumentField.COMMENT_ATTRIBUTE).getText(),
                    CommentAttributes.class);
            if (commentsLogic.getComment(comment.getCommentId()) == null) {
                commentsLogic.deleteDocument(comment);
                continue;
            }
            comment.sendingState = CommentSendingState.SENT;
            String giverName = doc.getOnlyField(Const.SearchDocumentField.COMMENT_GIVER_NAME).getText();
            String recipientName = doc.getOnlyField(Const.SearchDocumentField.COMMENT_RECIPIENT_NAME).getText();
            
            boolean isGiver = giverEmailList.contains(comment.giverEmail);
            String giverAsKey = comment.giverEmail + comment.courseId;
            
            if (isGiver) {
                giverName = "You (" + comment.courseId + ")";
            } else if (comment.showGiverNameTo.contains(CommentParticipantType.INSTRUCTOR)) {
                giverName = extractContentFromQuotedString(giverName) + " (" + comment.courseId + ")";
            } else {
                giverAsKey = "Anonymous" + comment.courseId;
                giverName = "Anonymous" + " (" + comment.courseId + ")";
            }
            
            if (isGiver || comment.showRecipientNameTo.contains(CommentParticipantType.INSTRUCTOR)) {
                recipientName = extractContentFromQuotedString(recipientName);
            } else {
                recipientName = "Anonymous";
            }
            
            List<CommentAttributes> commentList = giverCommentTable.get(giverAsKey);
            if (commentList == null) {
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
