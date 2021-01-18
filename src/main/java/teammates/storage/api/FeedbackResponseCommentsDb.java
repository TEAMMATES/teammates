package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.search.FeedbackResponseCommentSearchDocument;
import teammates.storage.search.FeedbackResponseCommentSearchQuery;
import teammates.storage.search.SearchDocument;

/**
 * Handles CRUD operations for feedback response comments.
 *
 * @see FeedbackResponseComment
 * @see FeedbackResponseCommentAttributes
 */
public class FeedbackResponseCommentsDb extends EntitiesDb<FeedbackResponseComment, FeedbackResponseCommentAttributes> {

    private static final Logger log = Logger.getLogger();

    /**
     * Creates a feedback response comment.
     *
     * @return the created comment
     * @throws InvalidParametersException if the comment is not valid
     * @throws EntityAlreadyExistsException if the comment already exists in the Datastore
     */
    @Override
    public FeedbackResponseCommentAttributes createEntity(FeedbackResponseCommentAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackResponseCommentAttributes createdComment = super.createEntity(entityToAdd);
        putDocument(createdComment);

        return createdComment;
    }

    /**
     * Removes search document for the comment with given id.
     *
     * <p>See {@link FeedbackResponseCommentSearchDocument#toDocument()} for more details.</p>
     *
     * @param commentId ID of comment
     */
    public void deleteDocumentByCommentId(long commentId) {
        deleteDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, String.valueOf(commentId));
    }

    /**
     * Gets a feedback response comment.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(long feedbackResponseCommentId) {
        return makeAttributesOrNull(getFeedbackResponseCommentEntity(feedbackResponseCommentId));
    }

    /**
     * Gets a feedback response comment by "fake" unique constraint response-giver-createdAt.
     *
     * <p>The method is only used in testing</p>
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String feedbackResponseId, String commentGiver, Instant createdAt) {
        Assumption.assertNotNull(feedbackResponseId);
        Assumption.assertNotNull(commentGiver);
        Assumption.assertNotNull(createdAt);

        return makeAttributesOrNull(getFeedbackResponseCommentEntity(feedbackResponseId, commentGiver, createdAt));
    }

    /**
     * Gets a feedback response comment by "fake" unique constraint course-createdAt-giver.
     *
     * <p>The method is only used in testing</p>
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String courseId, Instant createdAt, String commentGiver) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(commentGiver);
        Assumption.assertNotNull(createdAt);

        return makeAttributesOrNull(getFeedbackResponseCommentEntity(courseId, createdAt, commentGiver));
    }

    /**
     * Gets all comments given by a user in a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForGiver(String courseId, String commentGiver) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(commentGiver);

        return makeAttributes(getFeedbackResponseCommentEntitiesForGiverInCourse(courseId, commentGiver));
    }

    /**
     * Gets all response comments for a response.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponse(String feedbackResponseId) {
        Assumption.assertNotNull(feedbackResponseId);

        return makeAttributes(getFeedbackResponseCommentEntitiesForResponse(feedbackResponseId));
    }

    /**
     * Gets comment associated with the response.
     *
     * <p>The comment is given by a feedback participant to explain the response</p>
     *
     * @param feedbackResponseId the response id
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseCommentForResponseFromParticipant(
            String feedbackResponseId) {
        Assumption.assertNotNull(feedbackResponseId);
        return makeAttributesOrNull(getFeedbackResponseCommentEntitiesForResponseFromParticipant(feedbackResponseId));
    }

    /**
     * Gets all comments in a feedback session of a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSession(
            String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

        return makeAttributes(getFeedbackResponseCommentEntitiesForSession(courseId, feedbackSessionName));
    }

    /**
     * Gets all comments of a feedback question of a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForQuestion(String questionId) {
        Assumption.assertNotNull(questionId);

        return makeAttributes(getFeedbackResponseCommentEntitiesForQuestion(questionId));
    }

    /**
     * Gets all comments which have its corresponding response given to/from a section of a feedback session of a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSessionInSection(
            String courseId, String feedbackSessionName, String section) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(section);

        return makeAttributes(getFeedbackResponseCommentEntitiesForSessionInSection(courseId, feedbackSessionName, section));
    }

    /**
     * Gets all comments which have its corresponding response given to/from a section of a feedback question of a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForQuestionInSection(
            String questionId, String section) {
        Assumption.assertNotNull(questionId);
        Assumption.assertNotNull(section);

        return makeAttributes(getFeedbackResponseCommentEntitiesForQuestionInSection(questionId, section));
    }

    /**
     * Updates a feedback response comment by {@link FeedbackResponseCommentAttributes.UpdateOptions}.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(
            FeedbackResponseCommentAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(updateOptions);

        FeedbackResponseComment frc = getFeedbackResponseCommentEntity(updateOptions.getFeedbackResponseCommentId());
        if (frc == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        FeedbackResponseCommentAttributes newAttributes = makeAttributes(frc);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<String>hasSameValue(frc.getFeedbackResponseId(), newAttributes.getFeedbackResponseId())
                && this.<String>hasSameValue(frc.getCommentText(), newAttributes.getCommentText())
                && this.<List<FeedbackParticipantType>>hasSameValue(frc.getShowCommentTo(), newAttributes.getShowCommentTo())
                && this.<List<FeedbackParticipantType>>hasSameValue(
                        frc.getShowGiverNameTo(), newAttributes.getShowGiverNameTo())
                && this.<String>hasSameValue(frc.getLastEditorEmail(), newAttributes.getLastEditorEmail())
                && this.<Instant>hasSameValue(frc.getLastEditedAt(), newAttributes.getLastEditedAt())
                && this.<String>hasSameValue(frc.getGiverSection(), newAttributes.getGiverSection())
                && this.<String>hasSameValue(frc.getReceiverSection(), newAttributes.getReceiverSection());
        if (hasSameAttributes) {
            log.info(String.format(
                    OPTIMIZED_SAVING_POLICY_APPLIED, FeedbackResponseComment.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        frc.setFeedbackResponseId(newAttributes.feedbackResponseId);
        frc.setCommentText(newAttributes.commentText);
        frc.setShowCommentTo(newAttributes.showCommentTo);
        frc.setShowGiverNameTo(newAttributes.showGiverNameTo);
        frc.setLastEditorEmail(newAttributes.lastEditorEmail);
        frc.setLastEditedAt(newAttributes.lastEditedAt);
        frc.setGiverSection(newAttributes.giverSection);
        frc.setReceiverSection(newAttributes.receiverSection);

        saveEntity(frc);

        newAttributes = makeAttributes(frc);
        putDocument(newAttributes);

        return newAttributes;
    }

    /**
     * Updates the giver email to a new one for all comments in a course.
     */
    public void updateGiverEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(oldEmail);
        Assumption.assertNotNull(updatedEmail);

        if (oldEmail.equals(updatedEmail)) {
            return;
        }

        List<FeedbackResponseComment> responseComments =
                getFeedbackResponseCommentEntitiesForGiverInCourse(courseId, oldEmail);

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setGiverEmail(updatedEmail);
        }

        saveEntities(responseComments);
    }

    /**
     * Updates the last editor to a new one for all comments in a course.
     */
    public void updateLastEditorEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(oldEmail);
        Assumption.assertNotNull(updatedEmail);

        if (oldEmail.equals(updatedEmail)) {
            return;
        }

        List<FeedbackResponseComment> responseComments =
                getFeedbackResponseCommentEntitiesForLastEditorInCourse(courseId, oldEmail);

        for (FeedbackResponseComment responseComment : responseComments) {
            responseComment.setLastEditorEmail(updatedEmail);
        }

        saveEntities(responseComments);
        log.info("updating last editor email from: " + oldEmail + " to: " + updatedEmail
                 + " for feedback response comments in the course: " + courseId);
    }

    /**
     * Creates or updates search document for the given comment.
     */
    public void putDocument(FeedbackResponseCommentAttributes comment) {
        putDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, new FeedbackResponseCommentSearchDocument(comment));
    }

    /**
     * Batch creates or updates search documents for the given comments.
     */
    public void putDocuments(List<FeedbackResponseCommentAttributes> comments) {
        List<SearchDocument> frcSearchDocuments = new ArrayList<>();
        for (FeedbackResponseCommentAttributes comment : comments) {
            frcSearchDocuments.add(new FeedbackResponseCommentSearchDocument(comment));
        }
        putDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT, frcSearchDocuments.toArray(new SearchDocument[0]));
    }

    /**
     * Searches for comments, using a list of instructors as a constraint.
     */
    public FeedbackResponseCommentSearchResultBundle search(String queryString, List<InstructorAttributes> instructors) {
        if (queryString.trim().isEmpty()) {
            return new FeedbackResponseCommentSearchResultBundle();
        }

        Results<ScoredDocument> results = searchDocuments(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT,
                new FeedbackResponseCommentSearchQuery(instructors, queryString));

        return FeedbackResponseCommentSearchDocument.fromResults(results, instructors);
    }

    /**
     * Deletes a comment.
     */
    public void deleteFeedbackResponseComment(long commentId) {
        deleteEntity(Key.create(FeedbackResponseComment.class, commentId));
        deleteDocumentByCommentId(commentId);
    }

    /**
     * Deletes comments using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackResponseComments(AttributesDeletionQuery query) {
        Assumption.assertNotNull(query);

        Query<FeedbackResponseComment> entitiesToDelete = load().project();
        if (query.isCourseIdPresent()) {
            entitiesToDelete = entitiesToDelete.filter("courseId =", query.getCourseId());
        }
        if (query.isFeedbackSessionNamePresent()) {
            entitiesToDelete = entitiesToDelete.filter("feedbackSessionName =", query.getFeedbackSessionName());
        }
        if (query.isQuestionIdPresent()) {
            entitiesToDelete = entitiesToDelete.filter("feedbackQuestionId =", query.getQuestionId());
        }
        if (query.isResponseIdPresent()) {
            entitiesToDelete = entitiesToDelete.filter("feedbackResponseId =", query.getResponseId());
        }

        List<Key<FeedbackResponseComment>> keysToDelete = entitiesToDelete.keys().list();

        deleteDocument(Const.SearchIndex.FEEDBACK_RESPONSE_COMMENT,
                keysToDelete.stream().map(key -> String.valueOf(key.getId())).toArray(String[]::new));
        deleteEntity(keysToDelete.toArray(new Key<?>[0]));
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntity(String courseId, Instant createdAt, String giverEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("createdAt =", createdAt)
                .filter("giverEmail =", giverEmail)
                .first().now();
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntity(long feedbackResponseCommentId) {
        return load().id(feedbackResponseCommentId).now();
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntity(
            String feedbackResponseId, String giverEmail, Instant createdAt) {
        return load()
                .filter("feedbackResponseId =", feedbackResponseId)
                .filter("giverEmail =", giverEmail)
                .filter("createdAt =", createdAt)
                .first().now();
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForGiverInCourse(
            String courseId, String giverEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("giverEmail =", giverEmail)
                .list();
    }

    /**
     * Gets a list of comments which have a last editor set to the given email.
     */
    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForLastEditorInCourse(
            String courseId, String lastEditorEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("lastEditorEmail =", lastEditorEmail)
                .list();
    }

    private Query<FeedbackResponseComment> getFeedbackResponseCommentsForResponseQuery(String feedbackResponseId) {
        return load().filter("feedbackResponseId =", feedbackResponseId);
    }

    private FeedbackResponseComment getFeedbackResponseCommentEntitiesForResponseFromParticipant(
            String feedbackResponseId) {
        return load()
                .filter("feedbackResponseId =", feedbackResponseId)
                .filter("isCommentFromFeedbackParticipant =", true)
                .first()
                .now();
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForResponse(String feedbackResponseId) {
        return getFeedbackResponseCommentsForResponseQuery(feedbackResponseId).list();
    }

    private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSession(
            String courseId, String feedbackSessionName) {
        return load()
                .filter("courseId =", courseId)
                .filter("feedbackSessionName =", feedbackSessionName)
                .list();
    }

    private Collection<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForQuestion(String questionId) {
        return load()
                .filter("feedbackQuestionId =", questionId)
                .list();
    }

    private Collection<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForQuestionInSection(
            String questionId, String section) {
        // creating map to remove duplicates
        Map<Long, FeedbackResponseComment> comments = new HashMap<>();

        List<FeedbackResponseComment> responseCommentsFromSection = load()
                .filter("feedbackQuestionId =", questionId)
                .filter("giverSection =", section)
                .list();

        for (FeedbackResponseComment comment : responseCommentsFromSection) {
            comments.put(comment.getFeedbackResponseCommentId(), comment);
        }

        List<FeedbackResponseComment> responseCommentsToSection = load()
                .filter("feedbackQuestionId =", questionId)
                .filter("receiverSection =", section)
                .list();

        for (FeedbackResponseComment comment : responseCommentsToSection) {
            comments.put(comment.getFeedbackResponseCommentId(), comment);
        }

        return comments.values();
    }

    private Collection<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSessionInSection(
            String courseId, String feedbackSessionName, String section) {
        Map<Long, FeedbackResponseComment> comments = new HashMap<>();

        List<FeedbackResponseComment> firstQueryResponseComments = load()
                .filter("courseId =", courseId)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("giverSection =", section)
                .list();

        for (FeedbackResponseComment comment : firstQueryResponseComments) {
            comments.put(comment.getFeedbackResponseCommentId(), comment);
        }

        List<FeedbackResponseComment> secondQueryResponseComments = load()
                .filter("courseId =", courseId)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("receiverSection =", section)
                .list();

        for (FeedbackResponseComment comment : secondQueryResponseComments) {
            comments.put(comment.getFeedbackResponseCommentId(), comment);
        }

        return comments.values();
    }

    @Override
    LoadType<FeedbackResponseComment> load() {
        return ofy().load().type(FeedbackResponseComment.class);
    }

    @Override
    boolean hasExistingEntities(FeedbackResponseCommentAttributes entityToCreate) {
        // comment does not have unique constraint
        return false;
    }

    @Override
    FeedbackResponseCommentAttributes makeAttributes(FeedbackResponseComment entity) {
        Assumption.assertNotNull(entity);

        return FeedbackResponseCommentAttributes.valueOf(entity);
    }
}
