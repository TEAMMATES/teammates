package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.attributes.CommentAttributes;
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
public class CommentsDb extends EntitiesDb<Comment, CommentAttributes> {

    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Comment: ";

    private static final Logger log = Logger.getLogger();

    public void createComments(Collection<CommentAttributes> commentsToAdd) throws InvalidParametersException {
        List<CommentAttributes> commentsToUpdate = createEntities(commentsToAdd);
        for (CommentAttributes comment : commentsToUpdate) {
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
    public CommentAttributes createComment(CommentAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return makeAttributesOrNull(createEntity(entityToAdd),
                "Trying to get non-existent Comment, possibly entity not persistent yet.");
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

        return makeAttributesOrNull(getCommentEntity(commentId), "Trying to get non-existent Comment: " + commentId);
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

        return makeAttributesOrNull(comment, "Trying to get non-existent Comment: " + commentToGet);
    }

    /*
     * Get comments for a giver email
     */
    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        return makeAttributes(getCommentEntitiesForGiver(courseId, giverEmail));
    }

    /*
     * Get comments for a giver email and the comment status
     */
    public List<CommentAttributes> getCommentsForGiverAndStatus(String courseId,
            String giverEmail, CommentStatus status) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, status);

        return makeAttributes(getCommentEntitiesForGiverAndStatus(courseId, giverEmail, status));
    }

    /*
     * Get comments with draft status
     */
    public List<CommentAttributes> getCommentDrafts(String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        return makeAttributes(getCommentEntitiesForDraft(giverEmail));
    }

    /*
     * Get comment for the receiver email
     */
    public List<CommentAttributes> getCommentsForReceiver(String courseId,
            CommentParticipantType recipientType, String receiverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, recipientType);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);

        return makeAttributes(getCommentEntitiesForRecipients(courseId, recipientType, receiverEmail));
    }

    /*
     * Get comment for the viewer (who can see the comment) type
     */
    public List<CommentAttributes> getCommentsForCommentViewer(String courseId,
            CommentParticipantType commentViewerType) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentViewerType);

        return makeAttributes(getCommentEntitiesForCommentViewer(courseId, commentViewerType));
    }

    /*
     * Get comment for the sending state (SENT|SENDING|PENDING)
     */
    public List<CommentAttributes> getCommentsForSendingState(String courseId, CommentSendingState state) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getCommentEntitiesForSendingState(courseId, state));
    }

    /*
     * Get comments for a course
     */
    public List<CommentAttributes> getCommentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getCommentEntitiesForCourse(courseId));
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

        saveEntities(comments);
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
        Comment comment = getEntity(newAttributes);

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

        saveEntity(comment);
        return makeAttributes(comment);
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

        saveEntities(giverComments);
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

        saveEntities(lastEditorComments);
        log.info("updating last editor email from: " + oldInstrEmail + " to: " + updatedInstrEmail
                 + " for student comments in the course: " + courseId);
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

        saveEntities(recipientComments);
    }

    /*
     * Delete comments given by certain instructor
     */
    public void deleteCommentsByInstructorEmail(String courseId, String email) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);

        ofy().delete().keys(getCommentsForGiverQuery(courseId, email).keys()).now();
    }

    /*
     * Delete comments given to certain student
     */
    public void deleteCommentsByStudentEmail(String courseId, String email) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, email);

        // student right now cannot be giver, so no need to&should not check for giver
        ofy().delete().keys(getCommentsForRecipientsQuery(courseId, CommentParticipantType.PERSON, email).keys()).now();
    }

    /*
     * Delete comments given to certain team
     */
    public void deleteCommentsForTeam(String courseId, String teamName) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, teamName);

        // student right now cannot be giver, so no need to&should not check for giver
        ofy().delete().keys(getCommentsForRecipientsQuery(courseId, CommentParticipantType.TEAM, teamName).keys()).now();
    }

    /*
     * Delete comments given to certain section
     */
    public void deleteCommentsForSection(String courseId, String sectionName) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sectionName);

        // student right now cannot be giver, so no need to&should not check for giver
        ofy().delete().keys(getCommentsForRecipientsQuery(courseId, CommentParticipantType.SECTION, sectionName).keys())
        .now();
    }

    /*
     * Delete comments in certain course
     */
    public void deleteCommentsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        ofy().delete().keys(getCommentsForCourseQuery(courseId).keys()).now();
    }

    /*
     * Delete comments in certain courses
     */
    public void deleteCommentsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        ofy().delete().keys(getCommentsForCoursesQuery(courseIds).keys()).now();
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
        return makeAttributes(getAllCommentEntities());
    }

    private List<Comment> getAllCommentEntities() {
        return load().list();
    }

    private Query<Comment> getCommentsForCourseQuery(String courseId) {
        return load().filter("courseId =", courseId);
    }

    private List<Comment> getCommentEntitiesForCourse(String courseId) {
        return getCommentsForCourseQuery(courseId).list();
    }

    private Query<Comment> getCommentsForCoursesQuery(List<String> courseIds) {
        return load().filter("courseId in", courseIds);
    }

    private List<Comment> getCommentEntitiesForSendingState(String courseId, CommentSendingState sendingState) {
        return load()
                .filter("courseId =", courseId)
                .filter("sendingState =", sendingState.toString())
                .list();
    }

    private Query<Comment> getCommentsForGiverQuery(String courseId, String giverEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("giverEmail =", giverEmail);
    }

    private List<Comment> getCommentEntitiesForGiver(String courseId, String giverEmail) {
        return getCommentsForGiverQuery(courseId, giverEmail).list();
    }

    private List<Comment> getCommentEntitiesForGiverAndStatus(String courseId, String giverEmail,
                                                              CommentStatus status) {
        return load()
                .filter("courseId =", courseId)
                .filter("giverEmail =", giverEmail)
                .filter("status =", status.toString())
                .list();
    }

    private List<Comment> getCommentEntitiesForDraft(String giverEmail) {
        return load()
                .filter("giverEmail =", giverEmail)
                .filter("status =", CommentStatus.DRAFT.toString())
                .list();
    }

    /*
     * Gets a list of Comments which have a last editor associated with the given email
     */
    private List<Comment> getCommentEntitiesForLastEditor(String courseId, String lastEditorEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("lastEditorEmail =", lastEditorEmail)
                .list();
    }

    private Query<Comment> getCommentsForRecipientsQuery(String courseId,
            CommentParticipantType recipientType, String recipient) {
        return load()
                .filter("courseId =", courseId)
                .filter("recipientType =", recipientType.toString())
                .filter("recipients", SanitizationHelper.sanitizeForHtml(recipient));
    }

    private List<Comment> getCommentEntitiesForRecipients(String courseId,
            CommentParticipantType recipientType, String recipient) {
        return getCommentsForRecipientsQuery(courseId, recipientType, recipient).list();
    }

    private List<Comment> getCommentEntitiesForCommentViewer(String courseId,
            CommentParticipantType commentViewerType) {
        return load()
                .filter("courseId =", courseId)
                .filter("showCommentTo", commentViewerType.toString())
                .list();
    }

    @Override
    protected LoadType<Comment> load() {
        return ofy().load().type(Comment.class);
    }

    @Override
    protected Comment getEntity(CommentAttributes commentToGet) {
        if (commentToGet.getCommentId() != null) {
            return getCommentEntity(commentToGet.getCommentId());
        }

        return getCommentEntity(commentToGet.courseId, commentToGet.giverEmail, commentToGet.recipientType,
                                commentToGet.recipients, commentToGet.createdAt);
    }

    @Override
    protected QueryKeys<Comment> getEntityQueryKeys(CommentAttributes attributes) {
        Long id = attributes.getCommentId();

        Query<Comment> query;

        if (id == null) {
            query = load()
                    .filter("courseId =", attributes.courseId)
                    .filter("giverEmail =", attributes.giverEmail)
                    .filter("createdAt =", attributes.createdAt)
                    .filter("recipientType =", attributes.recipientType.toString())
                    .filter("recipients", SanitizationHelper.sanitizeForHtml(attributes.recipients.iterator().next()));
        } else {
            query = load().filterKey(Key.create(Comment.class, id));
        }

        return query.keys();
    }

    // Gets a comment entity if the ID is known
    private Comment getCommentEntity(Long commentId) {
        return load().id(commentId).now();
    }

    private Comment getCommentEntity(String courseId, String giverEmail, CommentParticipantType recipientType,
                                     Set<String> recipients, Date date) {
        return load()
                .filter("courseId =", courseId)
                .filter("giverEmail =", giverEmail)
                .filter("createdAt =", date)
                .filter("recipientType =", recipientType.toString())
                .filter("recipients", SanitizationHelper.sanitizeForHtml(recipients.iterator().next()))
                .first().now();
    }

    @Override
    protected CommentAttributes makeAttributes(Comment entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return new CommentAttributes(entity);
    }
}
