package teammates.storage.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.Comment;

public class CommentsDb extends EntitiesDb{
    
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Comment: ";
    private static final Logger log = Utils.getLogger();
    
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
    
    public List<CommentAttributes> getCommentsForStudent(String courseId, StudentAttributes student){
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, student);
        
        List<Comment> comments = getCommentEntitiesForStudent(courseId, student);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
        
        for(Comment comment: comments){
            commentAttributesList.add(new CommentAttributes(comment));
        }
        return commentAttributesList;
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
        
        comment.setCommentText(newAttributes.commentText);
        
        getPM().close();
    }
    
    private List<Comment> getCommentEntitiesForGiver(String courseId, String giverEmail){
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, giverEmail);
        
        return commentList;
    }
    
    private List<Comment> getCommentEntitiesForReceiver(String courseId, CommentRecipientType recipientType, String recipient){
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String recipientTypeParam, String receiverParam");
        q.setFilter("courseId == courseIdParam && recipientType == recipientTypeParam && recipients.contains(receiverParam)");
        
        String recipientTypeString = recipientType.toString();
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, recipientTypeString, recipient);
        
        return commentList;
    }
    
    private List<Comment> getCommentEntitiesForStudent(String courseId, StudentAttributes student) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String studentEmailParam, String teamParam");
        q.setFilter("courseId == courseIdParam "
                + "&& (recipients.contains(studentEmailParam) || recipients.contains(teamParam) || recipients.contains(courseIdParam))");
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, student.email, student.team);
        
        //filter based on recipientType
        Iterator<Comment> iter = commentList.iterator();
        while (iter.hasNext()) {
            Comment c = iter.next();
            CommentRecipientType recipientType = c.getRecipientType();
            Set<String> recipients = c.getRecipients(); 
            switch(recipientType){
            case PERSON:
                if(!recipients.contains(student.email)){
                    iter.remove();
                }
                break;
            case TEAM:
                if(!recipients.contains(student.team)){
                    iter.remove();
                }
                break;
            case SECTION:
                //TODO: impl this
                break;
            case COURSE:
                if(!recipients.contains(courseId)){
                    iter.remove();
                }
                break;
            default:
                break;
            }
        }
        
        return commentList;
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {
        CommentAttributes commentToGet = (CommentAttributes) attributes;
        if(commentToGet.getCommentId() != null){
            return getCommentEntity(commentToGet.getCommentId());
        } else{
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
