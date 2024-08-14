package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.List;

import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Handles CRUD operations for feedback questions.
 *
 * @see FeedbackQuestion
 * @see FeedbackQuestionAttributes
 */
public final class FeedbackQuestionsDb extends EntitiesDb<FeedbackQuestion, FeedbackQuestionAttributes> {

    private static final FeedbackQuestionsDb instance = new FeedbackQuestionsDb();

    private FeedbackQuestionsDb() {
        // prevent initialization
    }

    public static FeedbackQuestionsDb inst() {
        return instance;
    }

    /**
     * Gets a feedback question by using {@code feedbackQuestionId}.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        assert feedbackQuestionId != null;

        return makeAttributesOrNull(getFeedbackQuestionEntity(feedbackQuestionId));
    }

    /**
     * Gets a feedback question by using unique constrain: course-session-questionNumber.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(
            String feedbackSessionName, String courseId, int questionNumber) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return makeAttributesOrNull(getFeedbackQuestionEntity(feedbackSessionName, courseId, questionNumber));
    }

    /**
     * Gets all feedback questions of a session.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(
            String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return makeAttributes(getFeedbackQuestionEntitiesForSession(feedbackSessionName, courseId));
    }

    /**
     * Gets all feedback questions of a session that has certain giver type.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        assert feedbackSessionName != null;
        assert courseId != null;
        assert giverType != null;

        return makeAttributes(getFeedbackQuestionEntitiesForGiverType(feedbackSessionName, courseId, giverType));
    }

    /**
     * Checks if there is any feedback questions in a session in a course for the given giver type.
     */
    public boolean hasFeedbackQuestionsForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        assert feedbackSessionName != null;
        assert courseId != null;
        assert giverType != null;

        return hasFeedbackQuestionEntitiesForGiverType(feedbackSessionName, courseId, giverType);
    }

    /**
     * Updates a feedback question by {@code FeedbackQuestionAttributes.UpdateOptions}.
     *
     * @return updated feedback question
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback question cannot be found
     */
    public FeedbackQuestionAttributes updateFeedbackQuestion(FeedbackQuestionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        FeedbackQuestion feedbackQuestion = getFeedbackQuestionEntity(updateOptions.getFeedbackQuestionId());
        if (feedbackQuestion == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + updateOptions);
        }

        FeedbackQuestionAttributes newAttributes = makeAttributes(feedbackQuestion);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        // update only if change
        boolean hasSameAttributes =
                this.<Integer>hasSameValue(feedbackQuestion.getQuestionNumber(), newAttributes.getQuestionNumber())
                && this.<String>hasSameValue(
                        feedbackQuestion.getQuestionText(), newAttributes.getSerializedQuestionDetails())
                && this.<String>hasSameValue(
                        feedbackQuestion.getQuestionDescription(), newAttributes.getQuestionDescription())
                && this.<FeedbackParticipantType>hasSameValue(
                        feedbackQuestion.getGiverType(), newAttributes.getGiverType())
                && this.<FeedbackParticipantType>hasSameValue(
                        feedbackQuestion.getRecipientType(), newAttributes.getRecipientType())
                && this.<List<FeedbackParticipantType>>hasSameValue(
                        feedbackQuestion.getShowResponsesTo(), newAttributes.getShowResponsesTo())
                && this.<List<FeedbackParticipantType>>hasSameValue(
                        feedbackQuestion.getShowGiverNameTo(), newAttributes.getShowGiverNameTo())
                && this.<List<FeedbackParticipantType>>hasSameValue(
                        feedbackQuestion.getShowRecipientNameTo(), newAttributes.getShowRecipientNameTo())
                && this.<Integer>hasSameValue(feedbackQuestion.getNumberOfEntitiesToGiveFeedbackTo(),
                        newAttributes.getNumberOfEntitiesToGiveFeedbackTo());
        if (hasSameAttributes) {
            log.info(String.format(OPTIMIZED_SAVING_POLICY_APPLIED, FeedbackQuestion.class.getSimpleName(), updateOptions));
            return newAttributes;
        }

        feedbackQuestion.setQuestionNumber(newAttributes.getQuestionNumber());
        feedbackQuestion.setQuestionText(newAttributes.getSerializedQuestionDetails());
        feedbackQuestion.setQuestionDescription(newAttributes.getQuestionDescription());
        feedbackQuestion.setGiverType(newAttributes.getGiverType());
        feedbackQuestion.setRecipientType(newAttributes.getRecipientType());
        feedbackQuestion.setShowResponsesTo(newAttributes.getShowResponsesTo());
        feedbackQuestion.setShowGiverNameTo(newAttributes.getShowGiverNameTo());
        feedbackQuestion.setShowRecipientNameTo(newAttributes.getShowRecipientNameTo());
        feedbackQuestion.setNumberOfEntitiesToGiveFeedbackTo(newAttributes.getNumberOfEntitiesToGiveFeedbackTo());

        saveEntity(feedbackQuestion);

        return makeAttributes(feedbackQuestion);
    }

    /**
     * Deletes a feedback question.
     */
    public void deleteFeedbackQuestion(String feedbackQuestionId) {
        makeKeyFromWebSafeString(feedbackQuestionId).ifPresent(this::deleteEntity);
    }

    /**
     * Deletes questions using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackQuestions(AttributesDeletionQuery query) {
        assert query != null;

        Query<FeedbackQuestion> entitiesToDelete = load().project();
        if (query.isCourseIdPresent()) {
            entitiesToDelete = entitiesToDelete.filter("courseId =", query.getCourseId());
        }
        if (query.isFeedbackSessionNamePresent()) {
            entitiesToDelete = entitiesToDelete.filter("feedbackSessionName =", query.getFeedbackSessionName());
        }

        deleteEntity(entitiesToDelete.keys().list());
    }

    /**
     * Gets a question entity if its string key can be decoded.
     */
    private FeedbackQuestion getFeedbackQuestionEntity(String feedbackQuestionId) {
        assert feedbackQuestionId != null;

        return makeKeyFromWebSafeString(feedbackQuestionId)
                .map(key -> ofy().load().key(key).now())
                .orElse(null);
    }

    /**
     * Gets a feedback question by using unique constrain: course-session-questionNumber.
     */
    private FeedbackQuestion getFeedbackQuestionEntity(
            String feedbackSessionName, String courseId, int questionNumber) {
        return load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("questionNumber =", questionNumber)
                .first().now();
    }

    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForSession(
            String feedbackSessionName, String courseId) {
        return load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .list();
    }

    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        return load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverType =", giverType)
                .list();
    }

    private boolean hasFeedbackQuestionEntitiesForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        return !load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverType =", giverType)
                .keys()
                .list()
                .isEmpty();
    }

    @Override
    LoadType<FeedbackQuestion> load() {
        return ofy().load().type(FeedbackQuestion.class);
    }

    @Override
    boolean hasExistingEntities(FeedbackQuestionAttributes entityToCreate) {
        return !load()
                .filter("feedbackSessionName =", entityToCreate.getFeedbackSessionName())
                .filter("courseId =", entityToCreate.getCourseId())
                .filter("questionNumber =", entityToCreate.getQuestionNumber())
                .keys()
                .list()
                .isEmpty();
    }

    @Override
    FeedbackQuestionAttributes makeAttributes(FeedbackQuestion entity) {
        assert entity != null;

        return FeedbackQuestionAttributes.valueOf(entity);
    }
}
