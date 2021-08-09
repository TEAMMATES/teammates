package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackResponseComment;

/**
 * Handles CRUD operations for feedback response comments.
 *
 * @see FeedbackResponseComment
 * @see FeedbackResponseCommentAttributes
 */
public final class FeedbackResponseCommentsDb
        extends EntitiesDb<FeedbackResponseComment, FeedbackResponseCommentAttributes> {

    private static final Logger log = Logger.getLogger();

    private static final FeedbackResponseCommentsDb instance = new FeedbackResponseCommentsDb();

    private FeedbackResponseCommentsDb() {
        // prevent initialization
    }

    public static FeedbackResponseCommentsDb inst() {
        return instance;
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
        assert feedbackResponseId != null;
        assert commentGiver != null;
        assert createdAt != null;

        return makeAttributesOrNull(getFeedbackResponseCommentEntity(feedbackResponseId, commentGiver, createdAt));
    }

    /**
     * Gets all comments given by a user in a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForGiver(String courseId, String commentGiver) {
        assert courseId != null;
        assert commentGiver != null;

        return makeAttributes(getFeedbackResponseCommentEntitiesForGiverInCourse(courseId, commentGiver));
    }

    /**
     * Gets all response comments for a response.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForResponse(String feedbackResponseId) {
        assert feedbackResponseId != null;

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
        assert feedbackResponseId != null;
        return makeAttributesOrNull(getFeedbackResponseCommentEntitiesForResponseFromParticipant(feedbackResponseId));
    }

    /**
     * Gets all comments in a feedback session of a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSession(
            String courseId, String feedbackSessionName) {
        assert courseId != null;
        assert feedbackSessionName != null;

        return makeAttributes(getFeedbackResponseCommentEntitiesForSession(courseId, feedbackSessionName));
    }

    /**
     * Gets all comments of a feedback question of a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForQuestion(String questionId) {
        assert questionId != null;

        return makeAttributes(getFeedbackResponseCommentEntitiesForQuestion(questionId));
    }

    /**
     * Gets all comments which have its corresponding response given to/from a section of a feedback session of a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSessionInSection(
            String courseId, String feedbackSessionName, String section) {
        assert courseId != null;
        assert feedbackSessionName != null;
        assert section != null;

        return makeAttributes(getFeedbackResponseCommentEntitiesForSessionInSection(courseId, feedbackSessionName, section));
    }

    /**
     * Gets all comments which have its corresponding response given to/from a section of a feedback question of a course.
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForQuestionInSection(
            String questionId, String section) {
        assert questionId != null;
        assert section != null;

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
        assert updateOptions != null;

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

        frc.setFeedbackResponseId(newAttributes.getFeedbackResponseId());
        frc.setCommentText(newAttributes.getCommentText());
        frc.setShowCommentTo(newAttributes.getShowCommentTo());
        frc.setShowGiverNameTo(newAttributes.getShowGiverNameTo());
        frc.setLastEditorEmail(newAttributes.getLastEditorEmail());
        frc.setLastEditedAt(newAttributes.getLastEditedAt());
        frc.setGiverSection(newAttributes.getGiverSection());
        frc.setReceiverSection(newAttributes.getReceiverSection());

        saveEntity(frc);

        return makeAttributes(frc);
    }

    /**
     * Updates the giver email to a new one for all comments in a course.
     */
    public void updateGiverEmailOfFeedbackResponseComments(String courseId, String oldEmail, String updatedEmail) {
        assert courseId != null;
        assert oldEmail != null;
        assert updatedEmail != null;

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
        assert courseId != null;
        assert oldEmail != null;
        assert updatedEmail != null;

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
     * Deletes a comment.
     */
    public void deleteFeedbackResponseComment(long commentId) {
        deleteEntity(Key.create(FeedbackResponseComment.class, commentId));
    }

    /**
     * Deletes comments using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackResponseComments(AttributesDeletionQuery query) {
        assert query != null;

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

        deleteEntity(entitiesToDelete.keys().list());
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
        assert entity != null;

        return FeedbackResponseCommentAttributes.valueOf(entity);
    }
}
