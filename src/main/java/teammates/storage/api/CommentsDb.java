package teammates.storage.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.BaseCommentAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.BaseComment;
import teammates.storage.entity.Comment;
import teammates.storage.search.CommentSearchDocument;
import teammates.storage.search.CommentSearchQuery;

/**
 * Handles CRUD Operations for {@link Comment}.
 * The API uses data transfer classes (i.e. *Attributes) instead of persistable classes.
 */
public class CommentsDb extends BaseCommentsDb {
    
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Comment: ";
    public static final String ERROR_GET_NON_EXISTENT = "Trying to get non-existent Comment: ";
    public String getUpdateError() {
        return ERROR_UPDATE_NON_EXISTENT;
    }
    public String getReadError() {
        return ERROR_GET_NON_EXISTENT;
    }
    private static final Logger log = Utils.getLogger();

    public CommentAttributes createEntity(EntityAttributes entityToAdd) throws InvalidParametersException, EntityAlreadyExistsException {
        return (CommentAttributes) super.createEntity(entityToAdd);
    }
    
    protected CommentAttributes getAttributeFromEntity(BaseComment createdEntity) {
        return new CommentAttributes((Comment) createdEntity);
    }
    
    @SuppressWarnings("unchecked")
    protected List<CommentAttributes> getAttributesListFromEntitiesList(List<? extends BaseComment> bcList) {
        return (List<CommentAttributes>) super.getAttributesListFromEntitiesList(bcList);
    }
    
    protected void deleteDocument(String commentId) {
        deleteDocument(Const.SearchIndex.COMMENT, commentId);        
    }
    
    public CommentAttributes getComment(BaseCommentAttributes bca) {
        return (CommentAttributes) super.getCommentAttributes(bca);
    }
    
    public CommentAttributes getComment(Long commentId) {
        return (CommentAttributes) super.getComment(commentId);
    }

    @SuppressWarnings("unchecked")
    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail) {
        return (List<CommentAttributes>) super.getCommentsForGiver(courseId, giverEmail);
    }

    /*
     * Get comments for a giver email and the comment status
     */
    public List<CommentAttributes> getCommentsForGiverAndStatus(String courseId,
            String giverEmail, CommentStatus status) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, status);
        
        List<Comment> comments = getCommentEntitiesForGiverAndStatus(courseId, giverEmail, status);
        return getAttributesListFromEntitiesList(comments);
    }
    
    /*
     * Get comments with draft status
     */
    public List<CommentAttributes> getCommentDrafts(String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        
        List<Comment> comments = getCommentEntitiesForDraft(giverEmail);
        return getAttributesListFromEntitiesList(comments);
    }

    /*
     * Get comment for the receiver email
     */
    public List<CommentAttributes> getCommentsForReceiver(String courseId,
            CommentParticipantType recipientType, String receiverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, recipientType);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);
        
        List<Comment> comments = getCommentEntitiesForRecipients(courseId, recipientType, receiverEmail);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
        
        for(Comment comment : comments) {
            commentAttributesList.add(new CommentAttributes(comment));
        }
        return commentAttributesList;
    }
    
    /*
     * Get comment for the viewer (who can see the comment) type
     */
    public List<CommentAttributes> getCommentsForCommentViewer(String courseId,
            CommentParticipantType commentViewerType) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentViewerType);
        
        List<Comment> comments = getCommentEntitiesForCommentViewer(courseId, commentViewerType);
        return getAttributesListFromEntitiesList(comments);
    }
    
    @SuppressWarnings("unchecked")
    public List<CommentAttributes> getCommentsForSendingState(String courseId, CommentSendingState state) {
        return (List<CommentAttributes>) super.getCommentsForSendingState(courseId, "PLACEHOLDER", state);
    }
    
    /*
     * Get comments for a course
     */
    @SuppressWarnings("unchecked")
    public List<CommentAttributes> getCommentsForCourse(String courseId) {
        return (List<CommentAttributes>) super.getCommentsForCourse(courseId);
    }
    
    public void updateComments(String courseId, CommentSendingState oldState, CommentSendingState newState) {
        super.updateComments(courseId, "PLACEHOLDER", oldState, newState);
    }

    public CommentAttributes updateComment(BaseCommentAttributes newBaseAttributes) 
                             throws InvalidParametersException, EntityDoesNotExistException {
        return (CommentAttributes) super.updateComment(newBaseAttributes);
    }
    
    protected Comment updateSpecificFields(BaseComment bc, BaseCommentAttributes bca) {
        CommentAttributes newAttributes = (CommentAttributes) bca;
        Comment comment = (Comment) bc;
        if (newAttributes.showCommentTo != null) {
            comment.setShowCommentTo(newAttributes.showCommentTo);
        }
        if (newAttributes.showGiverNameTo != null) {
            comment.setShowGiverNameTo(newAttributes.showGiverNameTo);
        }
        if (newAttributes.status != null) {
            comment.setStatus(newAttributes.status);
        }
        if (newAttributes.recipientType != null) {
            comment.setRecipientType(newAttributes.recipientType);
        }
        if (newAttributes.recipients != null) {
            comment.setRecipients(newAttributes.recipients);
        }
        if (newAttributes.showRecipientNameTo != null) {
            comment.setShowRecipientNameTo(newAttributes.showRecipientNameTo);
        }
        return comment;
    }
    
    /*
     * Update old instructor email used in the comment with the new one
     */
    public void updateInstructorEmail(String courseId, String oldInstrEmail, String updatedInstrEmail) {
        super.updateGiverEmailOfComment(courseId, oldInstrEmail, updatedInstrEmail);
        // for now, instructors can only be giver
        // updateInstructorEmailAsRecipient(courseId, oldInstrEmail, updatedInstrEmail);
    }
    
    // for now, this method is not being used as instructor cannot be receiver
    @SuppressWarnings("unused")
    private void updateInstructorEmailAsRecipient(String courseId, String oldInstrEmail, String updatedInstrEmail) {
        List<Comment> recipientComments = this.getCommentEntitiesForRecipients(courseId, 
                                                       CommentParticipantType.INSTRUCTOR, oldInstrEmail);
        
        for (Comment recipientComment : recipientComments) {
            recipientComment.setGiverEmail(updatedInstrEmail);
        }
        
        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPM().close();
    }
    
    /*
     * Update student email used in the comment with the new one
     */
    public void updateStudentEmail(String courseId, String oldStudentEmail, String updatedStudentEmail) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldStudentEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updatedStudentEmail);
        
        updateStudentEmailAsRecipient(courseId, oldStudentEmail, updatedStudentEmail);
    }

    private void updateStudentEmailAsRecipient(String courseId,
                                               String oldStudentEmail,
                                               String updatedStudentEmail) {
        List<Comment> recipientComments = this.getCommentEntitiesForRecipients(courseId, 
                                                       CommentParticipantType.PERSON, oldStudentEmail);
        
        for (Comment recipientComment : recipientComments) {
            recipientComment.getRecipients().remove(oldStudentEmail);
            recipientComment.getRecipients().add(updatedStudentEmail);
        }
        
        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPM().close();
    }
    
    /*
     * Delete comments given by certain instructor
     */
    public void deleteCommentsByInstructorEmail(String courseId, String email) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        
        List<Comment> giverComments = this.getCommentEntitiesForGiver(courseId, email);
        // for now, this list is empty
//        List<Comment> recipientComments = this.getCommentEntitiesForRecipients(courseId, 
//                CommentRecipientType.INSTRUCTOR, email);
//        getPM().deletePersistentAll(recipientComments);
        
        getPM().deletePersistentAll(giverComments);
        
        getPM().flush();
    }
    
    /*
     * Delete comments given to certain student
     */
    public void deleteCommentsByStudentEmail(String courseId, String email) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);
        
        // student right now cannot be giver, so no need to&should not check for giver
        List<Comment> recipientComments = this.getCommentEntitiesForRecipients(courseId, 
                                                       CommentParticipantType.PERSON, email);
        
        getPM().deletePersistentAll(recipientComments);
        
        getPM().flush();
    }
    
    /*
     * Delete comments given to certain team
     */
    public void deleteCommentsForTeam(String courseId, String teamName) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, teamName);
        
        // student right now cannot be giver, so no need to&should not check for giver
        List<Comment> recipientComments = this.getCommentEntitiesForRecipients(courseId, 
                                                       CommentParticipantType.TEAM, teamName);
        
        getPM().deletePersistentAll(recipientComments);
        getPM().flush();
    }
    
    /*
     * Delete comments given to certain section
     */
    public void deleteCommentsForSection(String courseId, String sectionName) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sectionName);
        
        // student right now cannot be giver, so no need to&should not check for giver
        List<Comment> recipientComments = this.getCommentEntitiesForRecipients(courseId, 
                                                       CommentParticipantType.SECTION, sectionName);
        
        getPM().deletePersistentAll(recipientComments);
        getPM().flush();
    }
    
    /*
     * Create or update search document for the given comment
     */
    public void putDocument(CommentAttributes comment) {
        putDocument(Const.SearchIndex.COMMENT, new CommentSearchDocument(comment));
    }
    
    protected Results<ScoredDocument> getSearchResult(String googleId, String queryString, String cursorString) {
        return searchDocuments(Const.SearchIndex.COMMENT,
                               new CommentSearchQuery(googleId, queryString, cursorString));
    }

    protected CommentSearchResultBundle getNewSearchResultBundle() {
        return new CommentSearchResultBundle();
    }
    
    @Deprecated
    @SuppressWarnings("unchecked")
    public List<CommentAttributes> getAllComments() {
        return (List<CommentAttributes>) super.getAllComments(Comment.class.getName());
    }
    
    @SuppressWarnings("unchecked")
    protected List<Comment> getCommentsWithoutDeletedEntity(List<? extends BaseComment> bcList) {
        return (List<Comment>) super.getCommentsWithoutDeletedEntity(bcList);
    }
    
    protected List<Comment> getCommentEntitiesForCourse(String courseId) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentsForCourse = (List<Comment>) q.execute(courseId);
        
        return commentsForCourse;
    }
    
    protected List<Comment> getCommentEntitiesForCourses(List<String> courseIds) {
        Query q = getPM().newQuery(Comment.class);
        q.setFilter(":p.contains(courseId)");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentsForCourse = (List<Comment>) q.execute(courseIds);
        
        return commentsForCourse;
    }
    
    protected List<Comment> getCommentEntitiesForSendingState(String courseId, String fsName, CommentSendingState sendingState) {
        return getCommentEntitiesForSendingState(courseId, sendingState);
    }
    
    private List<Comment> getCommentEntitiesForSendingState(String courseId, CommentSendingState sendingState) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String sendingStateParam");
        q.setFilter("courseId == courseIdParam && sendingState == sendingStateParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, sendingState.toString());
        
        return getCommentsWithoutDeletedEntity(commentList);
    }
    
    protected List<Comment> getCommentEntitiesForGiver(String courseId, String giverEmail) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, giverEmail);
        
        return getCommentsWithoutDeletedEntity(commentList);
    }
    
    private List<Comment> getCommentEntitiesForGiverAndStatus(String courseId, String giverEmail,
                                                              CommentStatus status) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam, String statusParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam && status == statusParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, giverEmail, status.toString());
        
        return getCommentsWithoutDeletedEntity(commentList);
    }
    
    private List<Comment> getCommentEntitiesForDraft(String giverEmail) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String giverEmailParam, String statusParam");
        q.setFilter("giverEmail == giverEmailParam && status == statusParam");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(giverEmail, CommentStatus.DRAFT.toString());
        
        return getCommentsWithoutDeletedEntity(commentList);
    }
    
    private List<Comment> getCommentEntitiesForRecipients(String courseId,
            CommentParticipantType recipientType, String recipient) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String recipientTypeParam, String receiverParam");
        q.setFilter("courseId == courseIdParam && recipientType == recipientTypeParam && recipients.contains(receiverParam)");
        
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, recipientType.toString(), recipient);
        
        return getCommentsWithoutDeletedEntity(commentList);
    }
    
    private List<Comment> getCommentEntitiesForCommentViewer(String courseId,
            CommentParticipantType commentViewerType) {
        Query q = getPM().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String commentViewerTypeParam");
        q.setFilter("courseId == courseIdParam "
                + "&& showCommentTo.contains(commentViewerTypeParam)");
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, commentViewerType.toString());
        
        return getCommentsWithoutDeletedEntity(commentList);
    }

    protected Comment getEntityFromAttributes(BaseCommentAttributes bca) {
        CommentAttributes commentToGet = (CommentAttributes) bca;
        return getCommentEntity(commentToGet.courseId, commentToGet.giverEmail, commentToGet.recipientType,
                                commentToGet.recipients, commentToGet.createdAt);
    }
    
    // Gets a comment entity if the ID is known
    protected Comment getCommentEntity(Long commentId) {
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
    
    private Comment getCommentEntity(String courseId, String giverEmail, CommentParticipantType recipientType, 
                                     Set<String> recipients, Date date) {
        String firstRecipient = recipients.iterator().next();
        List<Comment> commentList = getCommentEntitiesForRecipients(courseId, recipientType, firstRecipient);
        
        if (commentList.isEmpty()) {
            return null;
        }
        
        //JDO query can't seem to handle Text comparison correctly,
        //we have to compare the texts separately.
        for (Comment comment : commentList) {
            if (!JDOHelper.isDeleted(comment) 
                    && comment.getGiverEmail().equals(giverEmail)
                    && comment.getCreatedAt().equals(date)
                    && comment.getRecipients().equals(recipients)) {
                return comment;
            }
        }
        
        return null;
    }
}
