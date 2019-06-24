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
import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);

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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);

        FeedbackResponse fr = getFeedbackResponseEntity(feedbackResponseId);

        return makeAttributesOrNull(fr);
    }

    /**
     * Gets a feedback response by unique constraint question-giver-receiver.
     */
    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String receiverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);

        FeedbackResponse fr =
                getFeedbackResponseEntity(FeedbackResponse.generateId(feedbackQuestionId, giverEmail, receiverEmail));

        return makeAttributesOrNull(fr);
    }

    /**
     * Gets all feedback responses of a question in a specific section.
     *
     * <p>{@code sectionDetail} specifies the criteria of classifying a response in a section</p>
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection(
            String feedbackQuestionId, String section, SectionDetail sectionDetail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sectionDetail);

        return makeAttributes(getFeedbackResponseEntitiesForQuestionInSection(feedbackQuestionId, section, sectionDetail));
    }

    /**
     * Gets all feedback responses for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        return makeAttributes(getFeedbackResponseEntitiesForQuestion(feedbackQuestionId));
    }

    /**
     * Checks whether there are responses for a question.
     */
    public boolean areThereResponsesForQuestion(String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getFeedbackResponseEntitiesForSession(feedbackSessionName, courseId));
    }

    /**
     * Gets all responses of a feedback session in a course with limit.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionWithinRange(
            String feedbackSessionName, String courseId, int range) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getFeedbackResponseEntitiesForSessionWithinRange(feedbackSessionName, courseId, range));
    }

    /**
     * Gets all responses given to/from a section in a feedback session in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionInSection(feedbackSessionName, courseId, section));
    }

    /**
     * Gets all responses given to&from a section in a feedback session in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInGiverAndRecipientSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionInGiverAndRecipientSection(feedbackSessionName,
                courseId, section));
    }

    /**
     * Gets all responses given from a section in a feedback session in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionFromSection(feedbackSessionName, courseId, section));
    }

    /**
     * Gets all responses given to a section in a feedback session in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionToSection(feedbackSessionName, courseId, section));
    }

    /**
     * Gets all responses given to/from a section in a feedback session in a course with limit.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(
                getFeedbackResponseEntitiesForSessionInSectionWithinRange(
                        feedbackSessionName, courseId, section, range));
    }

    /**
     * Gets all responses given from a section in a feedback session in a course with limit.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(
                getFeedbackResponseEntitiesForSessionFromSectionWithinRange(
                        feedbackSessionName, courseId, section, range));
    }

    /**
     * Gets all responses given to a section in a feedback session in a course with limit.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(
                getFeedbackResponseEntitiesForSessionToSectionWithinRange(
                        feedbackSessionName, courseId, section, range));
    }

    /**
     * Gets all responses given to a user for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion(
            String feedbackQuestionId, String receiver) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

        return makeAttributes(getFeedbackResponseEntitiesForReceiverForQuestion(feedbackQuestionId, receiver));
    }

    /**
     * Gets all responses given to a user and given by a section for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestionInSection(
            String feedbackQuestionId, String receiver, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(
                getFeedbackResponseEntitiesForReceiverForQuestionInSection(feedbackQuestionId, receiver, section));
    }

    /**
     * Gets all responses given by a user for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion(
            String feedbackQuestionId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        return makeAttributes(getFeedbackResponseEntitiesFromGiverForQuestion(feedbackQuestionId, giverEmail));
    }

    /**
     * Gets all responses given by a user and given to a section for a question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestionInSection(
            String feedbackQuestionId, String giverEmail, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(
                getFeedbackResponseEntitiesFromGiverForQuestionInSection(feedbackQuestionId, giverEmail, section));
    }

    /**
     * Gets all responses given by a user in a feedback session of a course with limit.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForSessionWithinRange(
            String giverEmail, String feedbackSessionName, String courseId, int range) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getFeedbackResponseEntitiesFromGiverForSessionWithinRange(
                giverEmail, feedbackSessionName, courseId, range));
    }

    /**
     * Gets all responses given to a user in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse(
            String courseId, String receiver) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

        return makeAttributes(getFeedbackResponseEntitiesForReceiverForCourse(courseId, receiver));
    }

    /**
     * Gets all responses given by a user in a course.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse(
            String courseId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, updateOptions);

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
                    .builder(newAttributes.getFeedbackQuestionId(), newAttributes.getGiver(), newAttributes.getRecipient())
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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, responseId);

        deleteEntity(Key.create(FeedbackResponse.class, responseId));
    }

    /**
     * Deletes responses using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackResponses(AttributesDeletionQuery query) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, query);

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
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        return !load().filter("courseId =", courseId).limit(1).list().isEmpty();
    }

    private FeedbackResponse getFeedbackResponseEntity(String feedbackResponseId) {
        return load().id(feedbackResponseId).now();
    }

    private Set<FeedbackResponse> getFeedbackResponseEntitiesForQuestionInSection(
                String feedbackQuestionId, String section, SectionDetail sectionDetail) {
        Set<FeedbackResponse> feedbackResponses = new HashSet<>();
        if (sectionDetail == SectionDetail.BOTH) {
            // responses in section with giver or recipient as None are added to respective section selected
            feedbackResponses.addAll(load()
                    .filter("feedbackQuestionId =", feedbackQuestionId)
                    .filter("giverSection =", section)
                    .filter("receiverSection =", "None")
                    .list());

            feedbackResponses.addAll(load()
                    .filter("feedbackQuestionId =", feedbackQuestionId)
                    .filter("giverSection =", "None")
                    .filter("receiverSection =", section)
                    .list());

            feedbackResponses.addAll(load()
                    .filter("feedbackQuestionId =", feedbackQuestionId)
                    .filter("giverSection =", section)
                    .filter("receiverSection =", section)
                    .list());

        }
        if (sectionDetail == SectionDetail.GIVER || sectionDetail == SectionDetail.EITHER) {
            feedbackResponses.addAll(load()
                    .filter("feedbackQuestionId =", feedbackQuestionId)
                    .filter("giverSection =", section)
                    .list());

        }
        if (sectionDetail == SectionDetail.EVALUEE || sectionDetail == SectionDetail.EITHER) {
            feedbackResponses.addAll(load()
                    .filter("feedbackQuestionId =", feedbackQuestionId)
                    .filter("receiverSection =", section)
                    .list());

        }

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestion(String feedbackQuestionId) {
        return load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .list();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSession(String feedbackSessionName, String courseId) {
        return getFeedbackResponseEntitiesForSessionWithinRange(feedbackSessionName, courseId, -1);
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionWithinRange(
            String feedbackSessionName, String courseId, int range) {
        return load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .limit(range + 1).list();
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {
        Map<String, FeedbackResponse> feedbackResponses = new HashMap<>();

        for (FeedbackResponse result : getFeedbackResponseEntitiesForSessionFromSection(
                feedbackSessionName, courseId, section)) {
            feedbackResponses.put(result.getId(), result);
        }

        for (FeedbackResponse result : getFeedbackResponseEntitiesForSessionToSection(
                feedbackSessionName, courseId, section)) {
            feedbackResponses.put(result.getId(), result);
        }

        return feedbackResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionInGiverAndRecipientSection(
            String feedbackSessionName, String courseId, String section) {
        List<FeedbackResponse> feedbackResponses = new ArrayList<>();

        feedbackResponses.addAll(load()
                .filter("feedbackSessionName = ", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverSection =", section)
                .filter("receiverSection =", section)
                .list());

        // also show responses in section with giver but without recipient
        feedbackResponses.addAll(load()
                .filter("feedbackSessionName = ", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverSection =", section)
                .filter("receiverSection =", "None")
                .list());

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionFromSection(
            String feedbackSessionName, String courseId, String section) {
        return getFeedbackResponseEntitiesForSessionFromSectionWithinRange(feedbackSessionName, courseId, section, -1);
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionToSection(
            String feedbackSessionName, String courseId, String section) {
        return getFeedbackResponseEntitiesForSessionToSectionWithinRange(feedbackSessionName, courseId, section, -1);
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        Map<String, FeedbackResponse> feedbackResponses = new HashMap<>();

        for (FeedbackResponse result : getFeedbackResponseEntitiesForSessionFromSectionWithinRange(
                feedbackSessionName, courseId, section, range)) {
            feedbackResponses.put(result.getId(), result);
        }

        for (FeedbackResponse result : getFeedbackResponseEntitiesForSessionToSectionWithinRange(
                feedbackSessionName, courseId, section, range)) {
            feedbackResponses.put(result.getId(), result);
        }

        return feedbackResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        return load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverSection =", section)
                .limit(range + 1).list();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        return load()
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("receiverSection =", section)
                .limit(range + 1).list();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestion(
            String feedbackQuestionId, String receiver) {
        return load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("receiver =", receiver)
                .list();
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestionInSection(
            String feedbackQuestionId, String receiver, String section) {
        Map<String, FeedbackResponse> feedbackResponses = new HashMap<>();

        List<FeedbackResponse> firstQueryResponses = load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("receiver =", receiver)
                .filter("giverSection =", section)
                .list();

        for (FeedbackResponse response : firstQueryResponses) {
            feedbackResponses.put(response.getId(), response);
        }

        List<FeedbackResponse> secondQueryResponses = load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("receiver =", receiver)
                .filter("receiverSection =", section)
                .list();

        for (FeedbackResponse response : secondQueryResponses) {
            feedbackResponses.put(response.getId(), response);
        }

        return feedbackResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestion(
            String feedbackQuestionId, String giverEmail) {
        return load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("giverEmail =", giverEmail)
                .list();
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestionInSection(
            String feedbackQuestionId, String giverEmail, String section) {
        Map<String, FeedbackResponse> feedbackResponses = new HashMap<>();

        List<FeedbackResponse> firstQueryResponses = load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("giverEmail =", giverEmail)
                .filter("giverSection =", section)
                .list();

        for (FeedbackResponse response : firstQueryResponses) {
            feedbackResponses.put(response.getId(), response);
        }

        List<FeedbackResponse> secondQueryResponses = load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("giverEmail =", giverEmail)
                .filter("receiverSection =", section)
                .list();

        for (FeedbackResponse response : secondQueryResponses) {
            feedbackResponses.put(response.getId(), response);
        }

        return feedbackResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForSessionWithinRange(
            String giverEmail, String feedbackSessionName, String courseId, int range) {
        return load()
                .filter("giverEmail =", giverEmail)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .limit(range + 1).list();
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
    protected LoadType<FeedbackResponse> load() {
        return ofy().load().type(FeedbackResponse.class);
    }

    @Override
    protected boolean hasExistingEntities(FeedbackResponseAttributes entityToCreate) {
        return !load()
                .filterKey(Key.create(FeedbackResponse.class,
                        FeedbackResponse.generateId(entityToCreate.getFeedbackQuestionId(),
                                entityToCreate.getGiver(), entityToCreate.getRecipient())))
                .list()
                .isEmpty();
    }

    @Override
    protected FeedbackResponseAttributes makeAttributes(FeedbackResponse entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return FeedbackResponseAttributes.valueOf(entity);
    }
}
