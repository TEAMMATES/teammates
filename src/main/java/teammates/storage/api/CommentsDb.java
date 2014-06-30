package teammates.storage.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.search.Document;
import com.google.appengine.api.search.QueryOptions;
import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Sanitizer;
import teammates.common.util.Utils;
import teammates.storage.entity.Comment;

public class CommentsDb extends EntitiesDb{
    
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Comment: ";
    private static final Logger log = Utils.getLogger();
    
    @Override
    public CommentAttributes createEntity(EntityAttributes entityToAdd) 
            throws InvalidParametersException, EntityAlreadyExistsException{
        Comment createdEntity = (Comment) super.createEntity(entityToAdd);
        if(createdEntity == null){
            log.info("Trying to get non-existent Comment, possibly entity not persistent yet.");
            return null;
        } else{
            return new CommentAttributes(createdEntity);
        }
    }
    
    public void putSearchableDocument(Document doc){
        putDocument("comment", doc);
    }
    
    public void deleteSearchableDocument(String documentId){
        deleteDocument("comment", documentId);
    }
    
    public CommentAttributes getComment(Long commentId){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentId);
        
        Comment comment = getCommentEntity(commentId);
        if(comment == null){
            log.info("Trying to get non-existent Comment: " + commentId);
            return null;
        } else{
            return new CommentAttributes(comment);
        }
    }
    
    public CommentAttributes getComment(CommentAttributes commentToGet){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentToGet);
        Comment comment = null;
        if(commentToGet.getCommentId() != null){
            comment = getCommentEntity(commentToGet.getCommentId());
        }
        if(comment == null){
            comment = getCommentEntity(commentToGet.courseId, commentToGet.giverEmail, commentToGet.recipientType,
                commentToGet.recipients, commentToGet.commentText, commentToGet.createdAt);
        }
        if(comment == null){
            log.info("Trying to get non-existent Comment: " + commentToGet);
            return null;
        } else{
            return new CommentAttributes(comment);
        }
    }
    
    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        
        List<Comment> comments = getCommentEntitiesForGiver(courseId, giverEmail);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
        
        for(Comment comment: comments){
            commentAttributesList.add(new CommentAttributes(comment));
        }
        return commentAttributesList;
    }
    
    public List<CommentAttributes> getCommentsForGiverAndStatus(String courseId, String giverEmail, CommentStatus status){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, status);
        
        List<Comment> comments = getCommentEntitiesForGiverAndStatus(courseId, giverEmail, status);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
        
        for(Comment comment: comments){
            commentAttributesList.add(new CommentAttributes(comment));
        }
        return commentAttributesList;
    }
    
    public List<CommentAttributes> getCommentDrafts(String giverEmail){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        
        List<Comment> comments = getCommentEntitiesForDraft(giverEmail);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
        
        for(Comment comment: comments){
            commentAttributesList.add(new CommentAttributes(comment));
        }
        return commentAttributesList;
    }

    public List<CommentAttributes> getCommentsForReceiver(String courseId, CommentRecipientType recipientType, String receiverEmail){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, recipientType);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);
        
        List<Comment> comments = getCommentEntitiesForReceiver(courseId, recipientType, receiverEmail);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
        
        for(Comment comment: comments){
            commentAttributesList.add(new CommentAttributes(comment));
        }
        return commentAttributesList;
    }
    
    public List<CommentAttributes> getCommentsForCommentViewer(String courseId, CommentRecipientType commentViewerType){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentViewerType);
        
        List<Comment> comments = getCommentEntitiesForCommentViewer(courseId, commentViewerType);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
        
        for(Comment comment: comments){
            commentAttributesList.add(new CommentAttributes(comment));
        }
        return commentAttributesList;
    }
    
    public List<CommentAttributes> getCommentsForSendingState(String courseId, CommentSendingState state){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<Comment> comments = getCommentEntitiesForSendingState(courseId, state);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
        
        for(Comment comment: comments){
            commentAttributesList.add(new CommentAttributes(comment));
        }
        return commentAttributesList;
    }
    
    public void updateComments(String courseId, CommentSendingState oldState, CommentSendingState newState){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<Comment> comments = getCommentEntitiesForSendingState(courseId, oldState);
        
        for(Comment comment: comments){
            comment.setSendingState(newState);
        }
        
        getPM().close();
    }

    public void updateComment(CommentAttributes newAttributes) throws InvalidParametersException, EntityDoesNotExistException{
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT,  newAttributes);
        
        newAttributes.sanitizeForSaving();
        
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        Comment comment = (Comment) getEntity(newAttributes);
        
        if (comment == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }
        


        if(newAttributes.commentText != null){
            comment.setCommentText(newAttributes.commentText);
        }
        if(newAttributes.showCommentTo != null){
            comment.setShowCommentTo(newAttributes.showCommentTo);
        }
        if(newAttributes.showGiverNameTo != null){
            comment.setShowGiverNameTo(newAttributes.showGiverNameTo);
        }
        if(newAttributes.showRecipientNameTo != null){
            comment.setShowRecipientNameTo(newAttributes.showRecipientNameTo);
        }
        if(newAttributes.status != null){
            comment.setStatus(newAttributes.status);
        }
        if(newAttributes.recipientType != null){
            comment.setRecipientType(newAttributes.recipientType);
        }
        if(newAttributes.recipients != null){
            comment.setRecipients(newAttributes.recipients);
        }
        comment.setSendingState(newAttributes.sendingState);
        comment.setCreatedAt(newAttributes.createdAt);
        
        getPM().close();
    }
    
    public List<CommentAttributes> search(String queryString){
        List<CommentAttributes> comments = new ArrayList<CommentAttributes>();
        queryString = Sanitizer.sanitizeForHtml(queryString).toLowerCase().trim();
        if(queryString.isEmpty()) return comments;
        
        QueryOptions options = QueryOptions.newBuilder().setFieldsToReturn("attribute").build();
        Results<ScoredDocument> results = searchDocuments("comment", com.google.appengine.api.search.
                Query.newBuilder()
                    .setOptions(options)
                    .build("searchableText:" + queryString));
        for(ScoredDocument result : results){
            comments.add(CommentAttributes.fromDocument(result));
        }
        return comments;
    }
    
    private List<Comment> getCommentEntitiesForSendingState(String courseId, CommentSendingState sendingState){
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String sendingStateParam");
        q.setFilter("courseId == courseIdParam && sendingState == sendingStateParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, sendingState.toString());
        
        return commentList;
    }
    
    private List<Comment> getCommentEntitiesForGiver(String courseId, String giverEmail){
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, giverEmail);
        
        return commentList;
    }
    
    private List<Comment> getCommentEntitiesForGiverAndStatus(String courseId,
            String giverEmail, CommentStatus status) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam, String statusParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam && status == statusParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, giverEmail, status.toString());
        
        return commentList;
    }
    
    private List<Comment> getCommentEntitiesForDraft(String giverEmail) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String giverEmailParam, String statusParam");
        q.setFilter("giverEmail == giverEmailParam && status == statusParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(giverEmail, CommentStatus.DRAFT.toString());
        
        return commentList;
    }
    
    private List<Comment> getCommentEntitiesForReceiver(String courseId, CommentRecipientType recipientType, String recipient){
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String recipientTypeParam, String receiverParam");
        q.setFilter("courseId == courseIdParam && recipientType == recipientTypeParam && recipients.contains(receiverParam)");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, recipientType.toString(), recipient);
        
        return commentList;
    }
    
    private List<Comment> getCommentEntitiesForCommentViewer(String courseId, CommentRecipientType commentViewerType){
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String commentViewerTypeParam");
        q.setFilter("courseId == courseIdParam "
                + "&& showCommentTo.contains(commentViewerTypeParam)");
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, commentViewerType.toString());
        return commentList;
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {
        CommentAttributes commentToGet = (CommentAttributes) attributes;
        if(commentToGet.getCommentId() != null){
            return getCommentEntity(commentToGet.getCommentId());
        } else{
            commentToGet.sanitizeForSaving();
            return getCommentEntity(commentToGet.courseId, commentToGet.giverEmail, commentToGet.recipientType,
                    commentToGet.recipients, commentToGet.commentText, commentToGet.createdAt);
        }
    }
    
    // Gets a comment entity if the ID is known
    private Comment getCommentEntity(Long commentId) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("Long commentIdParam");
        q.setFilter("commentId == commentIdParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(commentId);

        if (commentList.isEmpty() || JDOHelper.isDeleted(commentList.get(0))) {
            return null;
        }
        return commentList.get(0);
    }
    
    private Comment getCommentEntity(String courseId, String giverEmail, CommentRecipientType recipientType,
            Set<String> recipients, Text commentText, Date date) {
        String firstRecipient = recipients.iterator().next();
        List<Comment> commentList = getCommentEntitiesForReceiver(courseId, recipientType, firstRecipient);
        
        if(commentList.isEmpty()){
            return null;
        }
        
        //JDO query can't seem to handle Text comparison correctly,
        //we have to compare the texts separately.
        for(Comment comment : commentList){
            if(comment.getGiverEmail().equals(giverEmail)
                    && comment.getCommentText().equals(commentText)
                    && comment.getCreatedAt().equals(date)
                    && comment.getRecipients().equals(recipients)) {
                return comment;
            }
        }
        
        return null;
    }
}
