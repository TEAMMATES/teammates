package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
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
    private FeedbackResponse getFeedbackResponseEntityWithCheck(String feedbackResponseId) {
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
    private FeedbackResponse getFeedbackResponseEntityWithCheck(
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
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionInSection(
            String feedbackQuestionId, String section, SectionDetail sectionDetail) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, sectionDetail);

        return makeAttributes(getFeedbackResponseEntitiesForQuestionInSection(feedbackQuestionId, section, sectionDetail));
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

    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInGiverAndRecipientSection(
            String feedbackSessionName, String courseId, String section) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        return makeAttributes(getFeedbackResponseEntitiesForSessionInGiverAndRecipientSection(feedbackSessionName,
                courseId, section));
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
            oldResponse.setGiverEmail(newAttributes.giver);
            oldResponse.setGiverSection(newAttributes.giverSection);
            oldResponse.setRecipientEmail(newAttributes.recipient);
            oldResponse.setRecipientSection(newAttributes.recipientSection);
            oldResponse.setAnswer(newAttributes.getSerializedFeedbackResponseDetail());

            saveEntity(oldResponse, newAttributes);

            return makeAttributes(oldResponse);
        } else {
            // need to recreate the entity
            newAttributes.setId(null);
            FeedbackResponse recreatedResponseEntity = createEntity(newAttributes);
            deleteEntityDirect(oldResponse);

            return makeAttributes(recreatedResponseEntity);
        }
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

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionInGiverAndRecipientSection(
            String feedbackSessionName, String courseId, String section) {
        return getFeedbackResponseEntitiesForSessionInGiverAndRecipientSectionWithinRange(feedbackSessionName, courseId,
                section, -1);
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

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionInGiverAndRecipientSectionWithinRange(
            String feedbackSessionName, String courseId, String section, int range) {
        List<FeedbackResponse> feedbackResponses = new ArrayList<>();

        feedbackResponses.addAll(load()
                .filter("feedbackSessionName = ", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverSection =", section)
                .filter("receiverSection =", section)
                .limit(range + 1).list());

        // also show responses in section with giver but without recipient
        feedbackResponses.addAll(load()
                .filter("feedbackSessionName = ", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverSection =", section)
                .filter("receiverSection =", "None")
                .limit(range + 1).list());

        return feedbackResponses;
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
