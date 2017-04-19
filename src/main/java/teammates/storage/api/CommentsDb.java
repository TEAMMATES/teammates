package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;

import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.attributes.CommentAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Comment;
import teammates.storage.search.CommentSearchDocument;
import teammates.storage.search.CommentSearchQuery;
import teammates.storage.search.SearchDocument;

/**
 * Handles CRUD operations for student comments.
 *
 * @see Comment
 * @see CommentAttributes
 */
public class CommentsDb extends EntitiesDb {

    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Comment: ";

    private static final Logger log = Logger.getLogger();

    public void createComments(Collection<CommentAttributes> commentsToAdd) throws InvalidParametersException {
        List<EntityAttributes> commentsToUpdate = createEntities(commentsToAdd);
        for (EntityAttributes entity : commentsToUpdate) {
            CommentAttributes comment = (CommentAttributes) entity;
            try {
                updateComment(comment);
            } catch (EntityDoesNotExistException e) {
                // This situation is not tested as replicating such a situation is
                // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }

    /**
     * Preconditions:
     * <br> * {@code entityToAdd} is not null and has valid data.
     */
    @Override
    public CommentAttributes createEntity(EntityAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Comment createdEntity = (Comment) super.createEntity(entityToAdd);
        if (createdEntity == null) {
            log.info("Trying to get non-existent Comment, possibly entity not persistent yet.");
            return null;
        }
        return new CommentAttributes(createdEntity);
    }

    /**
     * Removes search document for the given comment.
     */
    public void deleteDocument(CommentAttributes commentToDelete) {
        if (commentToDelete.getCommentId() == null) {
            CommentAttributes comment = getComment(commentToDelete);
            deleteDocument(Const.SearchIndex.COMMENT, comment.getCommentId().toString());
        } else {
            deleteDocument(Const.SearchIndex.COMMENT, commentToDelete.getCommentId().toString());
        }
    }

    /*
     * Get comment for comment's Id
     */
    public CommentAttributes getComment(Long commentId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentId);

        Comment comment = getCommentEntity(commentId);
        if (comment == null) {
            log.info("Trying to get non-existent Comment: " + commentId);
            return null;
        }
        return new CommentAttributes(comment);
    }

    /*
     * Get comment for a given comment attribute
     */
    public CommentAttributes getComment(CommentAttributes commentToGet) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentToGet);
        Comment comment = null;
        if (commentToGet.getCommentId() != null) {
            comment = getCommentEntity(commentToGet.getCommentId());
        }
        if (comment == null) {
            comment = getCommentEntity(commentToGet.courseId, commentToGet.giverEmail, commentToGet.recipientType,
                                       commentToGet.recipients, commentToGet.createdAt);
        }
        if (comment == null || JDOHelper.isDeleted(comment)) {
            log.info("Trying to get non-existent Comment: " + commentToGet);
            return null;
        }
        return new CommentAttributes(comment);
    }

    /*
     * Get comments for a giver email
     */
    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        List<Comment> comments = getCommentEntitiesForGiver(courseId, giverEmail);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();

        for (Comment comment : comments) {
            if (!JDOHelper.isDeleted(comment)) {
                commentAttributesList.add(new CommentAttributes(comment));
            }
        }
        return commentAttributesList;
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
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();

        for (Comment comment : comments) {
            if (!JDOHelper.isDeleted(comment)) {
                commentAttributesList.add(new CommentAttributes(comment));
            }
        }
        return commentAttributesList;
    }

    /*
     * Get comments with draft status
     */
    public List<CommentAttributes> getCommentDrafts(String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        List<Comment> comments = getCommentEntitiesForDraft(giverEmail);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();

        for (Comment comment : comments) {
            if (!JDOHelper.isDeleted(comment)) {
                commentAttributesList.add(new CommentAttributes(comment));
            }
        }
        return commentAttributesList;
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

        for (Comment comment : comments) {
            if (!JDOHelper.isDeleted(comment)) {
                commentAttributesList.add(new CommentAttributes(comment));
            }
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
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();

        for (Comment comment : comments) {
            if (!JDOHelper.isDeleted(comment)) {
                commentAttributesList.add(new CommentAttributes(comment));
            }
        }
        return commentAttributesList;
    }

    /*
     * Get comment for the sending state (SENT|SENDING|PENDING)
     */
    public List<CommentAttributes> getCommentsForSendingState(String courseId, CommentSendingState state) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<Comment> comments = getCommentEntitiesForSendingState(courseId, state);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();

        for (Comment comment : comments) {
            if (!JDOHelper.isDeleted(comment)) {
                commentAttributesList.add(new CommentAttributes(comment));
            }
        }
        return commentAttributesList;
    }

    /*
     * Get comments for a course
     */
    public List<CommentAttributes> getCommentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<Comment> comments = getCommentEntitiesForCourse(courseId);
        List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();

        for (Comment comment : comments) {
            if (!JDOHelper.isDeleted(comment)) {
                commentAttributesList.add(new CommentAttributes(comment));
            }
        }
        return commentAttributesList;
    }

    /*
     * Update comment from old state to new state
     */
    public void updateComments(String courseId, CommentSendingState oldState, CommentSendingState newState) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<Comment> comments = getCommentEntitiesForSendingState(courseId, oldState);

        for (Comment comment : comments) {
            comment.setSendingState(newState);
        }

        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPm().close();
    }

    /**
     * Preconditions:
     * <br> * {@code newAttributes} is not null and has valid data.
     */
    public CommentAttributes updateComment(CommentAttributes newAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);

        newAttributes.sanitizeForSaving();

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        Comment comment = (Comment) getEntity(newAttributes);

        if (comment == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }

        if (newAttributes.commentText != null) {
            comment.setCommentText(newAttributes.commentText);
        }
        if (newAttributes.showCommentTo != null) {
            comment.setShowCommentTo(newAttributes.showCommentTo);
        }
        if (newAttributes.showGiverNameTo != null) {
            comment.setShowGiverNameTo(newAttributes.showGiverNameTo);
        }
        if (newAttributes.showRecipientNameTo != null) {
            comment.setShowRecipientNameTo(newAttributes.showRecipientNameTo);
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
        comment.setSendingState(newAttributes.sendingState);
        comment.setLastEditorEmail(newAttributes.giverEmail);
        comment.setLastEditedAt(newAttributes.createdAt);

        getPm().close();

        CommentAttributes updatedComment = new CommentAttributes(comment);
        log.info(updatedComment.getBackupIdentifier());
        return updatedComment;
    }

    /*
     * Update old instructor email used in the comment with the new one
     */
    public void updateInstructorEmail(String courseId, String oldInstrEmail, String updatedInstrEmail) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, oldInstrEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updatedInstrEmail);

        updateInstructorEmailAsGiver(courseId, oldInstrEmail, updatedInstrEmail);
        updateInstructorEmailAsLastEditorForStudentComments(courseId, oldInstrEmail, updatedInstrEmail);
        // for now, instructors can only be giver
        // updateInstructorEmailAsRecipient(courseId, oldInstrEmail, updatedInstrEmail);
    }

    private void updateInstructorEmailAsGiver(String courseId, String oldInstrEmail,
                                              String updatedInstrEmail) {
        List<Comment> giverComments = this.getCommentEntitiesForGiver(courseId, oldInstrEmail);

        for (Comment giverComment : giverComments) {
            giverComment.setGiverEmail(updatedInstrEmail);
        }

        log.info(Const.SystemParams.COURSE_BACKUP_LOG_MSG + courseId);
        getPm().close();
    }

    /*
     * Updates last editor for all comments last edited by the given instructor with the instructor's new email
     */
    private void updateInstructorEmailAsLastEditorForStudentComments(String courseId, String oldInstrEmail,
                                                                     String updatedInstrEmail) {
        List<Comment> lastEditorComments = getCommentEntitiesForLastEditor(courseId, oldInstrEmail);

        for (Comment lastEditorComment : lastEditorComments) {
            lastEditorComment.setLastEditorEmail(updatedInstrEmail);
        }
        log.info("updating last editor email from: " + oldInstrEmail + " to: " + updatedInstrEmail
                 + " for student comments in the course: " + courseId);
        getPm().close();
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
        getPm().close();
    }

    /*
     * Delete comments given by certain instructor
     */
    public void deleteCommentsByInstructorEmail(String courseId, String email) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);

        getCommentsForGiverQuery(courseId, email)
            .deletePersistentAll();
    }

    /*
     * Delete comments given to certain student
     */
    public void deleteCommentsByStudentEmail(String courseId, String email) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);

        // student right now cannot be giver, so no need to&should not check for giver
        getCommentsForRecipientsQuery(courseId, CommentParticipantType.PERSON, email)
            .deletePersistentAll();
    }

    /*
     * Delete comments given to certain team
     */
    public void deleteCommentsForTeam(String courseId, String teamName) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, teamName);

        // student right now cannot be giver, so no need to&should not check for giver
        getCommentsForRecipientsQuery(courseId, CommentParticipantType.TEAM, teamName)
            .deletePersistentAll();
    }

    /*
     * Delete comments given to certain section
     */
    public void deleteCommentsForSection(String courseId, String sectionName) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sectionName);

        // student right now cannot be giver, so no need to&should not check for giver
        getCommentsForRecipientsQuery(courseId, CommentParticipantType.SECTION, sectionName)
            .deletePersistentAll();
    }

    /*
     * Delete comments in certain course
     */
    public void deleteCommentsForCourse(String courseId) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        getCommentsForCourseQuery(courseId)
            .deletePersistentAll();
    }

    /*
     * Delete comments in certain courses
     */
    public void deleteCommentsForCourses(List<String> courseIds) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        getCommentsForCoursesQuery(courseIds)
            .deletePersistentAll();
    }

    /*
     * Create or update search document for the given comment
     */
    public void putDocument(CommentAttributes comment) {
        putDocument(Const.SearchIndex.COMMENT, new CommentSearchDocument(comment));
    }

    /*
     * Batch create or update search documents for the given comments
     */
    public void putDocuments(List<CommentAttributes> comments) {
        List<SearchDocument> commentSearchDocuments = new ArrayList<SearchDocument>();
        for (CommentAttributes comment : comments) {
            commentSearchDocuments.add(new CommentSearchDocument(comment));
        }
        putDocuments(Const.SearchIndex.COMMENT, commentSearchDocuments);
    }

    /**
     * Searches for comments.
     * @return {@link CommentSearchResultBundle}
     */
    public CommentSearchResultBundle search(String queryString, List<InstructorAttributes> instructors) {
        if (queryString.trim().isEmpty()) {
            return new CommentSearchResultBundle();
        }

        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.COMMENT,
                                                          new CommentSearchQuery(instructors, queryString));

        return CommentSearchDocument.fromResults(results, instructors);
    }

    /**
     * Gets all student comments in the Datastore.
     *
     * @deprecated Not scalable. Don't use unless in admin features.
     */
    @Deprecated
    public List<CommentAttributes> getAllComments() {

        List<CommentAttributes> list = new ArrayList<CommentAttributes>();
        List<Comment> entities = getAllCommentEntities();
        for (Comment comment : entities) {
            list.add(new CommentAttributes(comment));
        }
        return list;
    }

    private List<Comment> getAllCommentEntities() {

        String query = "select from " + Comment.class.getName();

        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) getPm()
                .newQuery(query).execute();

        return getCommentsWithoutDeletedEntity(commentList);
    }

    private List<Comment> getCommentsWithoutDeletedEntity(List<Comment> commentList) {
        List<Comment> resultList = new ArrayList<Comment>();
        for (Comment c : commentList) {
            if (!JDOHelper.isDeleted(c)) {
                resultList.add(c);
            }
        }

        return resultList;
    }

    private QueryWithParams getCommentsForCourseQuery(String courseId) {
        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        return new QueryWithParams(q, new Object[] {courseId});
    }

    @SuppressWarnings("unchecked")
    private List<Comment> getCommentEntitiesForCourse(String courseId) {
        return (List<Comment>) getCommentsForCourseQuery(courseId).execute();
    }

    private QueryWithParams getCommentsForCoursesQuery(List<String> courseIds) {
        Query q = getPm().newQuery(Comment.class);
        q.setFilter(":p.contains(courseId)");
        return new QueryWithParams(q, new Object[] {courseIds});
    }

    private List<Comment> getCommentEntitiesForSendingState(String courseId, CommentSendingState sendingState) {
        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String sendingStateParam");
        q.setFilter("courseId == courseIdParam && sendingState == sendingStateParam");

        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, sendingState.toString());

        return getCommentsWithoutDeletedEntity(commentList);
    }

    private QueryWithParams getCommentsForGiverQuery(String courseId, String giverEmail) {
        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
        return new QueryWithParams(q, new Object[] {courseId, giverEmail});
    }

    @SuppressWarnings("unchecked")
    private List<Comment> getCommentEntitiesForGiver(String courseId, String giverEmail) {
        return (List<Comment>) getCommentsForGiverQuery(courseId, giverEmail).execute();
    }

    private List<Comment> getCommentEntitiesForGiverAndStatus(String courseId, String giverEmail,
                                                              CommentStatus status) {
        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam, String statusParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam && status == statusParam");

        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, giverEmail, status.toString());

        return getCommentsWithoutDeletedEntity(commentList);
    }

    private List<Comment> getCommentEntitiesForDraft(String giverEmail) {
        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String giverEmailParam, String statusParam");
        q.setFilter("giverEmail == giverEmailParam && status == statusParam");

        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(giverEmail, CommentStatus.DRAFT.toString());

        return getCommentsWithoutDeletedEntity(commentList);
    }

    /*
     * Gets a list of Comments which have a last editor associated with the given email
     */
    private List<Comment> getCommentEntitiesForLastEditor(String courseId, String lastEditorEmail) {
        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String lastEditorEmailParam");
        q.setFilter("courseId == courseIdParam && lastEditorEmail == lastEditorEmailParam");

        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, lastEditorEmail);

        return getCommentsWithoutDeletedEntity(commentList);
    }

    private QueryWithParams getCommentsForRecipientsQuery(String courseId,
            CommentParticipantType recipientType, String recipient) {
        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String recipientTypeParam, String receiverParam");
        q.setFilter("courseId == courseIdParam "
                    + "&& recipientType == recipientTypeParam "
                    + "&& recipients.contains(receiverParam)");
        return new QueryWithParams(q, new Object[] {courseId, recipientType.toString(),
                                                    SanitizationHelper.sanitizeForHtml(recipient)});
    }

    @SuppressWarnings("unchecked")
    private List<Comment> getCommentEntitiesForRecipients(String courseId,
            CommentParticipantType recipientType, String recipient) {
        return (List<Comment>) getCommentsForRecipientsQuery(courseId, recipientType, recipient).execute();
    }

    private List<Comment> getCommentEntitiesForCommentViewer(String courseId,
            CommentParticipantType commentViewerType) {
        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String commentViewerTypeParam");
        q.setFilter("courseId == courseIdParam "
                + "&& showCommentTo.contains(commentViewerTypeParam)");
        @SuppressWarnings("unchecked")
        List<Comment> commentList = (List<Comment>) q.execute(courseId, commentViewerType.toString());

        return getCommentsWithoutDeletedEntity(commentList);
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {
        CommentAttributes commentToGet = (CommentAttributes) attributes;
        if (commentToGet.getCommentId() != null) {
            return getCommentEntity(commentToGet.getCommentId());
        }

        return getCommentEntity(commentToGet.courseId, commentToGet.giverEmail, commentToGet.recipientType,
                                commentToGet.recipients, commentToGet.createdAt);
    }

    @Override
    protected QueryWithParams getEntityKeyOnlyQuery(EntityAttributes attributes) {
        Class<?> entityClass = Comment.class;
        String primaryKeyName = Comment.PRIMARY_KEY_NAME;
        CommentAttributes ca = (CommentAttributes) attributes;
        Long id = ca.getCommentId();

        Query q = getPm().newQuery(entityClass);
        Object[] params;

        if (id == null) {
            q.declareParameters("String courseIdParam, String giverEmailParam, java.util.Date createdAtParam, "
                    + "String recipientTypeParam, String receiverParam");
            q.setFilter("courseId == courseIdParam "
                        + "&& giverEmail == giverEmailParam "
                        + "&& createdAt == createdAtParam "
                        + "&& recipientType == recipientTypeParam "
                        + "&& recipients.contains(receiverParam)");
            params = new Object[]{ca.courseId, ca.giverEmail, ca.createdAt, ca.recipientType.toString(),
                    SanitizationHelper.sanitizeForHtml(ca.recipients.iterator().next())};
        } else {
            q.declareParameters("Long idParam");
            q.setFilter(primaryKeyName + " == idParam");
            params = new Object[] {id};
        }

        return new QueryWithParams(q, params, primaryKeyName);
    }

    // Gets a comment entity if the ID is known
    private Comment getCommentEntity(Long commentId) {
        Query q = getPm().newQuery(Comment.class);
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

        Query q = getPm().newQuery(Comment.class);
        q.declareParameters("String courseIdParam, String giverEmailParam, java.util.Date createdAtParam, "
                            + "String recipientTypeParam, String receiverParam");
        q.setFilter("courseId == courseIdParam "
                    + "&& giverEmail == giverEmailParam "
                    + "&& createdAt == createdAtParam "
                    + "&& recipientType == recipientTypeParam "
                    + "&& recipients.contains(receiverParam)");
        Object[] params = new Object[]{courseId, giverEmail, date, recipientType.toString(),
                SanitizationHelper.sanitizeForHtml(firstRecipient)};

        @SuppressWarnings("unchecked")
        List<Comment> results = (List<Comment>) q.executeWithArray(params);

        if (results.isEmpty()) {
            return null;
        }

        return results.get(0);
    }
}
