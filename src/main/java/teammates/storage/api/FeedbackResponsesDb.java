package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.storage.entity.FeedbackResponse;

/**
 * Handles CRUD operations for feedback responses.
 *
 * @see FeedbackResponse
 * @see FeedbackResponseAttributes
 */
public class FeedbackResponsesDb extends EntitiesDb<FeedbackResponse, FeedbackResponseAttributes> {

    /**
     * Gets a set of giver identifiers that has at least one response under a feedback session.
     */
    public Set<String> getGiverSetThatAnswerFeedbackSession(String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

        List<Key<FeedbackResponse>> keysOfResponses =
                load().filter("courseId =", courseId)
                        .filter("feedbackSessionName =", feedbackSessionName)
                        .keys()
                        .list();

        // the following process makes use of the key pattern of feedback response entity
        // see generateId() in FeedbackResponse.java
        Set<String> giverSet = new HashSet<>();
        for (Key<FeedbackResponse> key : keysOfResponses) {
            String[] tokens = key.getName().split("%");
            if (tokens.length >= 3) {
                giverSet.add(tokens[1]);
            }
        }

        return giverSet;
    }

    /**
     * Gets a feedback response.
     */
    public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
        Assumption.assertNotNull(feedbackResponseId);

        FeedbackResponse fr = getFeedbackResponseEntity(feedbackResponseId);

        return makeAttributesOrNull(fr);
    }

    /**
     * Gets a feedback response by unique constraint question-giver-receiver.
     */
    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String receiverEmail) {
        Assumption.assertNotNull(feedbackQuestionId);
        Assumption.assertNotNull(giverEmail);
        Assumption.assertNotNull(receiverEmail);

        FeedbackResponse fr =
                getFeedbackResponseEntity(FeedbackResponse.generateId(feedbackQuestionId, giverEmail, receiverEmail));

        return makeAttributesOrNull(fr);
    }

    /**
     * Gets all feedback responses of a question in a specific section.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection(
            String feedbackQuestionId, String section) {
        Assumption.assertNotNull(feedbackQuestionId);
        Assumption.assertNotNull(section);

        return makeAttributes(getFeedbackResponseEntitiesForQuestionInSection(feedbackQuestionId, section));
    }

    /**
     * Gets all feedback responses for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        Assumption.assertNotNull(feedbackQuestionId);

        return makeAttributes(getFeedbackResponseEntitiesForQuestion(feedbackQuestionId));
    }

    /**
     * Checks whether there are responses for a question.
     */
    public boolean areThereResponsesForQuestion(String feedbackQuestionId) {
        Assumption.assertNotNull(feedbackQuestionId);

        return !load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .limit(1)
                .list()
                .isEmpty();
    }

    /**
     * Gets all responses of a feedback session in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
            String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return makeAttributes(getFeedbackResponseEntitiesForSession(feedbackSessionName, courseId));
    }

    /**
     * Gets all responses given to/from a section in a feedback session in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionInSection(feedbackSessionName, courseId, section));
    }

    /**
     * Gets all responses given by a user for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion(
            String feedbackQuestionId, String giverEmail) {
        Assumption.assertNotNull(feedbackQuestionId);
        Assumption.assertNotNull(giverEmail);

        return makeAttributes(getFeedbackResponseEntitiesFromGiverForQuestion(feedbackQuestionId, giverEmail));
    }

    /**
     * Gets all responses received by a user for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion(
            String feedbackQuestionId, String receiver) {
        Assumption.assertNotNull(feedbackQuestionId);
        Assumption.assertNotNull(receiver);

        return makeAttributes(getFeedbackResponseEntitiesForReceiverForQuestion(feedbackQuestionId, receiver));
    }

    /**
     * Checks whether a user has responses in a session.
     */
    public boolean hasResponsesFromGiverInSession(
            String giverEmail, String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(giverEmail);
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return !load()
                .filter("giverEmail =", giverEmail)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .limit(1)
                .keys() // key query is free query
                .list()
                .isEmpty();
    }

    /**
     * Gets all responses given to a user in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse(
            String courseId, String receiver) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(receiver);

        return makeAttributes(getFeedbackResponseEntitiesForReceiverForCourse(courseId, receiver));
    }

    /**
     * Gets all responses given by a user in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse(
            String courseId, String giverEmail) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(giverEmail);

        return makeAttributes(getFeedbackResponseEntitiesFromGiverForCourse(courseId, giverEmail));
    }

    /**
     * Updates a feedback response with {@link FeedbackResponseAttributes.UpdateOptions}.
     *
     * <p>If the giver/recipient field is changed, the response is updated by recreating the response
     * as question-giver-recipient is the primary key.
     *
     * @return updated feedback response
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     * @throws EntityAlreadyExistsException if the response cannot be updated
     *         by recreation because of an existent response
     */
    public FeedbackResponseAttributes updateFeedbackResponse(FeedbackResponseAttributes.UpdateOptions updateOptions)
            throws EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        Assumption.assertNotNull(updateOptions);

        FeedbackResponse oldResponse = getFeedbackResponseEntity(updateOptions.getFeedbackResponseId());
        if (oldResponse == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT);
        }

        FeedbackResponseAttributes newAttributes = makeAttributes(oldResponse);
        newAttributes.update(updateOptions);

        newAttributes.sanitizeForSaving();
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        if (newAttributes.recipient.equals(oldResponse.getRecipientEmail())
                && newAttributes.giver.equals(oldResponse.getGiverEmail())) {

            // update only if change
            boolean hasSameAttributes =
                    this.<String>hasSameValue(oldResponse.getGiverSection(), newAttributes.getGiverSection())
                    && this.<String>hasSameValue(oldResponse.getRecipientSection(), newAttributes.getRecipientSection())
                    && this.<String>hasSameValue(
                            oldResponse.getResponseMetaData(), newAttributes.getSerializedFeedbackResponseDetail());
            if (hasSameAttributes) {
                log.info(String.format(
                        OPTIMIZED_SAVING_POLICY_APPLIED, FeedbackResponse.class.getSimpleName(), updateOptions));
                return newAttributes;
            }

            oldResponse.setGiverSection(newAttributes.giverSection);
            oldResponse.setRecipientSection(newAttributes.recipientSection);
            oldResponse.setAnswer(newAttributes.getSerializedFeedbackResponseDetail());

            saveEntity(oldResponse);

            return makeAttributes(oldResponse);
        } else {
            // need to recreate the entity
            newAttributes = FeedbackResponseAttributes
                    .builder(newAttributes.getFeedbackQuestionId(), newAttributes.getGiver(),
                             newAttributes.getRecipient())
                    .withCourseId(newAttributes.getCourseId())
                    .withFeedbackSessionName(newAttributes.getFeedbackSessionName())
                    .withResponseDetails(newAttributes.getResponseDetails())
                    .withGiverSection(newAttributes.getGiverSection())
                    .withRecipientSection(newAttributes.getRecipientSection())
                    .build();
            newAttributes = createEntity(newAttributes);
            deleteEntity(Key.create(FeedbackResponse.class, oldResponse.getId()));

            return newAttributes;
        }
    }

    /**
     * Deletes a feedback response.
     */
    public void deleteFeedbackResponse(String responseId) {
        Assumption.assertNotNull(responseId);

        deleteEntity(Key.create(FeedbackResponse.class, responseId));
    }

    /**
     * Deletes responses using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackResponses(AttributesDeletionQuery query) {
        Assumption.assertNotNull(query);

        Query<FeedbackResponse> entitiesToDelete = load().project();
        if (query.isCourseIdPresent()) {
            entitiesToDelete = entitiesToDelete.filter("courseId =", query.getCourseId());
        }
        if (query.isFeedbackSessionNamePresent()) {
            entitiesToDelete = entitiesToDelete.filter("feedbackSessionName =", query.getFeedbackSessionName());
        }
        if (query.isQuestionIdPresent()) {
            entitiesToDelete = entitiesToDelete.filter("feedbackQuestionId =", query.getQuestionId());
        }

        deleteEntity(entitiesToDelete.keys().list().toArray(new Key<?>[0]));
    }

    /**
     * Returns true if there are existing responses in any feedback session in the course.
     */
    public boolean hasFeedbackResponseEntitiesForCourse(String courseId) {
        Assumption.assertNotNull(courseId);
        return !load().filter("courseId =", courseId).limit(1).list().isEmpty();
    }

    private FeedbackResponse getFeedbackResponseEntity(String feedbackResponseId) {
        return load().id(feedbackResponseId).now();
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForQuestionInSection(
                String feedbackQuestionId, String section) {
        List<FeedbackResponse> allResponses = new ArrayList<>();

        allResponses.addAll(load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("giverSection =", section)
                .list());
        allResponses.addAll(load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("receiverSection =", section)
                .list());

        return removeDuplicates(allResponses);
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestion(String feedbackQuestionId) {
        return load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .list();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSession(String feedbackSessionName, String courseId) {
        return load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .list();
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {
        List<FeedbackResponse> allResponse = new ArrayList<>();

        allResponse.addAll(load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverSection =", section)
                .list());

        allResponse.addAll(load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("receiverSection =", section)
                .list());

        return removeDuplicates(allResponse);
    }

    private Collection<FeedbackResponse> removeDuplicates(Collection<FeedbackResponse> responses) {
        Map<String, FeedbackResponse> uniqueResponses = new HashMap<>();
        for (FeedbackResponse response : responses) {
            uniqueResponses.put(response.getId(), response);
        }
        return uniqueResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestion(
            String feedbackQuestionId, String giverEmail) {
        return load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("giverEmail =", giverEmail)
                .list();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestion(
            String feedbackQuestionId, String receiver) {
        return load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("receiver =", receiver)
                .list();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForCourse(
            String courseId, String receiver) {
        return load()
                .filter("courseId =", courseId)
                .filter("receiver =", receiver)
                .list();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForCourse(
            String courseId, String giverEmail) {
        return load()
                .filter("courseId =", courseId)
                .filter("giverEmail =", giverEmail)
                .list();
    }

    @Override
    LoadType<FeedbackResponse> load() {
        return ofy().load().type(FeedbackResponse.class);
    }

    @Override
    boolean hasExistingEntities(FeedbackResponseAttributes entityToCreate) {
        return !load()
                .filterKey(Key.create(FeedbackResponse.class,
                        FeedbackResponse.generateId(entityToCreate.getFeedbackQuestionId(),
                                entityToCreate.getGiver(), entityToCreate.getRecipient())))
                .list()
                .isEmpty();
    }

    @Override
    FeedbackResponseAttributes makeAttributes(FeedbackResponse entity) {
        Assumption.assertNotNull(entity);

        return FeedbackResponseAttributes.valueOf(entity);
    }
}
