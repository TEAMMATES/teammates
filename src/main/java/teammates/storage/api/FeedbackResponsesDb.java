package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackResponse;

/**
 * Handles CRUD operations for feedback responses.
 *
 * @see FeedbackResponse
 * @see FeedbackResponseAttributes
 */
public class FeedbackResponsesDb extends EntitiesDb<FeedbackResponse, FeedbackResponseAttributes> {

    private static final Logger log = Logger.getLogger();

    public void createFeedbackResponses(Collection<FeedbackResponseAttributes> responsesToAdd)
            throws InvalidParametersException {
        List<FeedbackResponseAttributes> responsesToUpdate = createEntities(responsesToAdd);
        for (FeedbackResponseAttributes response : responsesToUpdate) {
            try {
                updateFeedbackResponse(response);
            } catch (EntityDoesNotExistException e) {
                // This situation is not tested as replicating such a situation is
                // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
        return makeAttributesOrNull(getFeedbackResponseEntityWithCheck(feedbackResponseId));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String receiverEmail) {
        return makeAttributesOrNull(getFeedbackResponseEntityWithCheck(feedbackQuestionId, giverEmail, receiverEmail));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponse getFeedbackResponseEntityWithCheck(String feedbackResponseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);

        FeedbackResponse fr = getFeedbackResponseEntity(feedbackResponseId);
        if (fr == null) {
            log.info("Trying to get non-existent response: " + feedbackResponseId + ".");
            return null;
        }
        return fr;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponse getFeedbackResponseEntityWithCheck(
            String feedbackQuestionId, String giverEmail, String receiverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);

        FeedbackResponse fr = getFeedbackResponseEntity(feedbackQuestionId, giverEmail, receiverEmail);
        if (fr == null) {
            log.warning("Trying to get non-existent response: " + feedbackQuestionId + "/" + "from: " + giverEmail
                    + " to: " + receiverEmail);
            return null;
        }
        return fr;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponse getFeedbackResponseEntityOptimized(FeedbackResponseAttributes response) {
        return getEntity(response);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection(
            String feedbackQuestionId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForQuestionInSection(feedbackQuestionId, section));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(
            String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        return makeAttributes(getFeedbackResponseEntitiesForQuestion(feedbackQuestionId));
    }

    /**
     * Finds the responses for a specified question within a given range.
     *
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionWithinRange(
            String feedbackQuestionId, int range) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        return makeAttributes(getFeedbackResponseEntitiesForQuestionWithinRange(feedbackQuestionId, range));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
            String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getFeedbackResponseEntitiesForSession(feedbackSessionName, courseId));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionWithinRange(
            String feedbackSessionName, String courseId, int range) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        return makeAttributes(getFeedbackResponseEntitiesForSessionWithinRange(feedbackSessionName, courseId, range));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionInSection(feedbackSessionName, courseId, section));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionFromSection(feedbackSessionName, courseId, section));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionToSection(feedbackSessionName, courseId, section));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
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
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
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
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
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
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion(
            String feedbackQuestionId, String receiver) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

        return makeAttributes(getFeedbackResponseEntitiesForReceiverForQuestion(feedbackQuestionId, receiver));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
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
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion(
            String feedbackQuestionId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        return makeAttributes(getFeedbackResponseEntitiesFromGiverForQuestion(feedbackQuestionId, giverEmail));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
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
     *  Preconditions: <br>
     * * All parameters are non-null.
     *  @return An empty list if no such responses are found.
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
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse(
            String courseId, String receiver) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

        return makeAttributes(getFeedbackResponseEntitiesForReceiverForCourse(courseId, receiver));
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse(
            String courseId, String giverEmail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

        return makeAttributes(getFeedbackResponseEntitiesFromGiverForCourse(courseId, giverEmail));
    }

    /**
     * Updates the feedback response identified by {@code newAttributes.getId()} and
     *   changes the {@code updatedAt} timestamp to be the time of update.
     * For the remaining parameters, the existing value is preserved
     *   if the parameter is null (due to 'keep existing' policy).<br>
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and correspond to an existing feedback response.
     */
    public void updateFeedbackResponse(FeedbackResponseAttributes newAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        updateFeedbackResponse(newAttributes, false);
    }

    /**
     * Updates the feedback response identified by {@code newAttributes.getId()}
     * For the remaining parameters, the existing value is preserved
     *   if the parameter is null (due to 'keep existing' policy).<br>
     * The timestamp for {@code updatedAt} is independent of the {@code newAttributes}
     *   and depends on the value of {@code keepUpdateTimestamp}
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and correspond to an existing feedback response.
     */
    public void updateFeedbackResponse(FeedbackResponseAttributes newAttributes, boolean keepUpdateTimestamp)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        updateFeedbackResponseOptimized(newAttributes, getEntity(newAttributes), keepUpdateTimestamp);
    }

    /**
     * Optimized to take in FeedbackResponse entity if available, to prevent reading the entity again.
     * Updates the feedback response identified by {@code newAttributes.getId()}
     * For the remaining parameters, the existing value is preserved
     *   if the parameter is null (due to 'keep existing' policy).<br>
     * The timestamp for {@code updatedAt} is independent of the {@code newAttributes}
     *   and depends on the value of {@code keepUpdateTimestamp}
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and correspond to an existing feedback response.
     */
    private void updateFeedbackResponseOptimized(FeedbackResponseAttributes newAttributes, FeedbackResponse fr,
            boolean keepUpdateTimestamp) throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);

        //TODO: Sanitize values and update tests accordingly

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        if (fr == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }

        fr.keepUpdateTimestamp = keepUpdateTimestamp;
        fr.setAnswer(newAttributes.responseMetaData);
        fr.setRecipientEmail(newAttributes.recipient);
        fr.setGiverSection(newAttributes.giverSection);
        fr.setRecipientSection(newAttributes.recipientSection);

        saveEntity(fr, newAttributes);
    }

    public void updateFeedbackResponseOptimized(FeedbackResponseAttributes newAttributes, FeedbackResponse fr)
            throws InvalidParametersException, EntityDoesNotExistException {
        updateFeedbackResponseOptimized(newAttributes, fr, false);
    }

    public void deleteFeedbackResponsesForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        deleteFeedbackResponsesForCourses(Arrays.asList(courseId));
    }

    public void deleteFeedbackResponsesForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        ofy().delete().keys(getFeedbackResponsesForCoursesQuery(courseIds).keys()).now();
    }

    private Query<FeedbackResponse> getFeedbackResponsesForCoursesQuery(List<String> courseIds) {
        return load().filter("courseId in", courseIds);
    }

    /**
     * Returns true if there are existing responses in any feedback session in the course.
     */
    public boolean hasFeedbackResponseEntitiesForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        return !getFeedbackResponseEntitiesForCourseWithinRange(courseId, 1).isEmpty();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForCourseWithinRange(String courseId, int range) {
        return load().filter("courseId =", courseId).limit(range).list();
    }

    private FeedbackResponse getFeedbackResponseEntity(String feedbackResponseId) {
        return load().id(feedbackResponseId).now();
    }

    private FeedbackResponse getFeedbackResponseEntity(
            String feedbackQuestionId, String giverEmail, String receiver) {
        return load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("giverEmail =", giverEmail)
                .filter("receiver =", receiver)
                .first().now();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestionInSection(
                String feedbackQuestionId, String section) {
        List<FeedbackResponse> feedbackResponses = new ArrayList<>();

        feedbackResponses.addAll(load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .filter("giverSection =", section)
                .list());

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

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestion(String feedbackQuestionId) {
        return getFeedbackResponseEntitiesForQuestionWithinRange(feedbackQuestionId, -1);
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestionWithinRange(String feedbackQuestionId, int range) {
        return load()
                .filter("feedbackQuestionId =", feedbackQuestionId)
                .limit(range + 1).list();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSession(
            String feedbackSessionName, String courseId) {
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
    protected FeedbackResponse getEntity(FeedbackResponseAttributes attributes) {
        if (attributes.getId() != null) {
            return getFeedbackResponseEntity(attributes.getId());
        }

        return getFeedbackResponseEntity(attributes.feedbackQuestionId, attributes.giver, attributes.recipient);
    }

    @Override
    protected QueryKeys<FeedbackResponse> getEntityQueryKeys(FeedbackResponseAttributes attributes) {
        String id = attributes.getId();

        Query<FeedbackResponse> query;
        if (id == null) {
            query = load()
                    .filter("feedbackQuestionId =", attributes.feedbackQuestionId)
                    .filter("giverEmail =", attributes.giver)
                    .filter("receiver =", attributes.recipient);
        } else {
            query = load().filterKey(Key.create(FeedbackResponse.class, id));
        }

        return query.keys();
    }

    @Override
    protected FeedbackResponseAttributes makeAttributes(FeedbackResponse entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return new FeedbackResponseAttributes(entity);
    }
}
