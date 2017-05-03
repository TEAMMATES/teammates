package teammates.storage.api;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.attributes.EntityAttributes;
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
public class FeedbackResponsesDb extends EntitiesDb {

    private static final Logger log = Logger.getLogger();

    public void createFeedbackResponses(Collection<FeedbackResponseAttributes> responsesToAdd)
            throws InvalidParametersException {
        List<EntityAttributes> responsesToUpdate = createEntities(responsesToAdd);
        for (EntityAttributes entity : responsesToUpdate) {
            FeedbackResponseAttributes response = (FeedbackResponseAttributes) entity;
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
        FeedbackResponse feedbackResponse = getFeedbackResponseEntityWithCheck(feedbackResponseId);
        if (feedbackResponse == null) {
            return null;
        }
        return new FeedbackResponseAttributes(feedbackResponse);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponseAttributes getFeedbackResponse(
            String feedbackQuestionId, String giverEmail, String receiverEmail) {
        FeedbackResponse feedbackResponse =
                getFeedbackResponseEntityWithCheck(feedbackQuestionId, giverEmail, receiverEmail);
        if (feedbackResponse == null) {
            return null;
        }
        return new FeedbackResponseAttributes(feedbackResponse);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackResponse getFeedbackResponseEntityWithCheck(String feedbackResponseId) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);

        FeedbackResponse fr =
                getFeedbackResponseEntity(feedbackResponseId);

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

        FeedbackResponse fr =
                getFeedbackResponseEntity(feedbackQuestionId, giverEmail, receiverEmail);

        if (fr == null) {
            log.warning("Trying to get non-existent response: "
                    + feedbackQuestionId + "/" + "from: "
                    + giverEmail + " to: " + receiverEmail);
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
        return (FeedbackResponse) getEntity(response);
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

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestionInSection(feedbackQuestionId, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(
            String feedbackQuestionId) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestion(feedbackQuestionId);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Finds the responses for a specified question within a given range.
     *
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestionWithinRange(
            String feedbackQuestionId, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForQuestionWithinRange(feedbackQuestionId, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForSession(feedbackSessionName, courseId);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionWithinRange(
            String feedbackSessionName, String courseId, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForSessionWithinRange(feedbackSessionName, courseId, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionInSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionFromSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionToSection(feedbackSessionName,
                                                                                      courseId, section);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionInSectionWithinRange(feedbackSessionName,
                                                                                      courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForSessionFromSectionWithinRange(feedbackSessionName, courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, section);

        Collection<FeedbackResponse> frList = getFeedbackResponseEntitiesForSessionToSectionWithinRange(feedbackSessionName,
                                                                                      courseId, section, range);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForQuestion(feedbackQuestionId, receiver);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        Collection<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForQuestionInSection(feedbackQuestionId, receiver, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForQuestion(feedbackQuestionId, giverEmail);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        Collection<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForQuestionInSection(feedbackQuestionId, giverEmail, section);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
    }

    /**
     *  Preconditions: <br>
     * * All parameters are non-null.
     *  @return An empty list if no such responses are found.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForSessionWithinRange(
            String giverEmail, String feedbackSessionName, String courseId, long range) {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        Collection<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForSessionWithinRange(giverEmail, feedbackSessionName, courseId, range);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForReceiverForCourse(courseId, receiver);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesFromGiverForCourse(courseId, giverEmail);
        List<FeedbackResponseAttributes> fraList =
                new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }

        return fraList;
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

        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT,
                newAttributes);

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        FeedbackResponse fr = (FeedbackResponse) getEntity(newAttributes);

        updateFeedbackResponseOptimized(newAttributes, fr, keepUpdateTimestamp);
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

        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT,
                newAttributes);

        //TODO: Sanitize values and update tests accordingly

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        if (fr == null || JDOHelper.isDeleted(fr)) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }

        fr.keepUpdateTimestamp = keepUpdateTimestamp;
        fr.setAnswer(newAttributes.responseMetaData);
        fr.setRecipientEmail(newAttributes.recipient);
        fr.setGiverSection(newAttributes.giverSection);
        fr.setRecipientSection(newAttributes.recipientSection);

        log.info(newAttributes.getBackupIdentifier());
        getPm().close();
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

        getFeedbackResponsesForCoursesQuery(courseIds)
            .deletePersistentAll();
    }

    private QueryWithParams getFeedbackResponsesForCoursesQuery(List<String> courseIds) {
        Query q = getPm().newQuery(FeedbackResponse.class);
        q.setFilter(":p.contains(courseId)");
        return new QueryWithParams(q, new Object[] {courseIds});
    }

    @SuppressWarnings("unchecked")
    public List<FeedbackResponse> getFeedbackResponseEntitiesForCourses(List<String> courseIds) {
        return (List<FeedbackResponse>) getFeedbackResponsesForCoursesQuery(courseIds).execute();
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForCourse(String courseId) {
        List<FeedbackResponse> frList =
                getFeedbackResponseEntitiesForCourse(courseId);
        List<FeedbackResponseAttributes> fraList = new ArrayList<FeedbackResponseAttributes>();

        for (FeedbackResponse fr : frList) {
            if (!JDOHelper.isDeleted(fr)) {
                fraList.add(new FeedbackResponseAttributes(fr));
            }
        }
        return fraList;
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackResponse> getFeedbackResponseEntitiesForCourse(String courseId) {
        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");

        return (List<FeedbackResponse>) q.execute(courseId);
    }

    /**
     * Returns true if there are existing responses in any feedback session in the course.
     */
    public boolean hasFeedbackResponseEntitiesForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        return !getFeedbackResponseEntitiesForCourseWithinRange(courseId, 1).isEmpty();
    }

    @SuppressWarnings("unchecked")
    private List<FeedbackResponse> getFeedbackResponseEntitiesForCourseWithinRange(String courseId, long range) {
        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        q.setRange(0, range);

        return (List<FeedbackResponse>) q.execute(courseId);
    }

    private FeedbackResponse getFeedbackResponseEntity(String feedbackResponseId) {
        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackResponseIdParam");
        q.setFilter("feedbackResponseId == feedbackResponseIdParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses = (List<FeedbackResponse>) q.execute(feedbackResponseId);

        if (feedbackResponses.isEmpty() || JDOHelper.isDeleted(feedbackResponses.get(0))) {
            return null;
        }

        return feedbackResponses.get(0);
    }

    private FeedbackResponse getFeedbackResponseEntity(
            String feedbackQuestionId, String giverEmail, String receiver) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, "
                            + "String giverEmailParam, String receiverParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && "
                    + "giverEmail == giverEmailParam && "
                    + "receiver == receiverParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, receiver);

        if (feedbackResponses.isEmpty() || JDOHelper.isDeleted(feedbackResponses.get(0))) {
            return null;
        }

        return feedbackResponses.get(0);
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestionInSection(
                String feedbackQuestionId, String section) {

        List<FeedbackResponse> feedbackResponses = new ArrayList<FeedbackResponse>();

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverSectionParam, String receiverSectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam "
                    + "&& giverSection == giverSectionParam "
                    + "&& receiverSection == receiverSectionParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, section, section);
        feedbackResponses.addAll(firstQueryResponses);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, section, "None");
        feedbackResponses.addAll(secondQueryResponses);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> thirdQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, "None", section);
        feedbackResponses.addAll(thirdQueryResponses);

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestion(
                String feedbackQuestionId) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam ");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses = (List<FeedbackResponse>) q.execute(feedbackQuestionId);

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestionWithinRange(
                String feedbackQuestionId, long range) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam ");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses = (List<FeedbackResponse>) q.execute(feedbackQuestionId);

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSession(
            String feedbackSessionName, String courseId) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId);

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionWithinRange(
            String feedbackSessionName, String courseId, long range) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId);

        return feedbackResponses;
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSection(
            String feedbackSessionName, String courseId, String section) {

        Map<String, FeedbackResponse> feedbackResponses = new HashMap<String, FeedbackResponse>();

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam "
                    + "&& giverSection == sectionParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for (FeedbackResponse response : firstQueryResponses) {
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }

        q.setFilter("feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam "
                    + "&& receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for (FeedbackResponse response : secondQueryResponses) {
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }

        return feedbackResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionFromSection(
            String feedbackSessionName, String courseId, String section) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam "
                    + "&& giverSection == sectionParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return queryResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionToSection(
            String feedbackSessionName, String courseId, String section) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam "
                    + "&& receiverSection == sectionParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return queryResponses;
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForSessionInSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Map<String, FeedbackResponse> feedbackResponses = new HashMap<String, FeedbackResponse>();

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam "
                    + "&& giverSection == sectionParam");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for (FeedbackResponse response : firstQueryResponses) {
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }

        q.setFilter("feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam "
                    + "&& receiverSection == sectionParam");
        q.setRange(0, range + 1);
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);
        for (FeedbackResponse response : secondQueryResponses) {
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }

        return feedbackResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionFromSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam "
                    + "&& giverSection == sectionParam");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return queryResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForSessionToSectionWithinRange(
            String feedbackSessionName, String courseId, String section, long range) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, String sectionParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam "
                    + "&& receiverSection == sectionParam");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> queryResponses =
                (List<FeedbackResponse>) q.execute(feedbackSessionName, courseId, section);

        return queryResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestion(
            String feedbackQuestionId, String receiver) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String receiverParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver);

        return feedbackResponses;
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestionInSection(
            String feedbackQuestionId, String receiver, String section) {

        Map<String, FeedbackResponse> feedbackResponses = new HashMap<String, FeedbackResponse>();

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String receiverParam, String sectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam "
                    + "&& giverSection == sectionParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver, section);
        for (FeedbackResponse response : firstQueryResponses) {
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }

        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam "
                + "&& receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver, section);
        for (FeedbackResponse response : secondQueryResponses) {
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }

        return feedbackResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestion(
            String feedbackQuestionId, String giverEmail) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail);

        return feedbackResponses;
    }

    private Collection<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForQuestionInSection(
            String feedbackQuestionId, String giverEmail, String section) {

        Map<String, FeedbackResponse> feedbackResponses = new HashMap<String, FeedbackResponse>();

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam, String sectionParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam "
                    + "&& giverSection == sectionParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> firstQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, section);
        for (FeedbackResponse response : firstQueryResponses) {
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }

        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam "
                + "&& receiverSection == sectionParam");
        @SuppressWarnings("unchecked")
        List<FeedbackResponse> secondQueryResponses =
                (List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, section);
        for (FeedbackResponse response : secondQueryResponses) {
            if (!JDOHelper.isDeleted(response)) {
                feedbackResponses.put(response.getId(), response);
            }
        }

        return feedbackResponses.values();
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForSessionWithinRange(
            String giverEmail, String feedbackSessionName, String courseId, long range) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String giverEmailParam, String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("giverEmail == giverEmailParam "
                    + "&& feedbackSessionName == feedbackSessionNameParam "
                    + "&& courseId == courseIdParam");
        q.setRange(0, range + 1);

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses =
                (List<FeedbackResponse>) q.execute(giverEmail, feedbackSessionName, courseId);

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForCourse(
            String courseId, String receiver) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam, String receiverParam");
        q.setFilter("courseId == courseIdParam && receiver == receiverParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses = (List<FeedbackResponse>) q.execute(courseId, receiver);

        return feedbackResponses;
    }

    private List<FeedbackResponse> getFeedbackResponseEntitiesFromGiverForCourse(
            String courseId, String giverEmail) {

        Query q = getPm().newQuery(FeedbackResponse.class);
        q.declareParameters("String courseIdParam, String giverEmailParam");
        q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");

        @SuppressWarnings("unchecked")
        List<FeedbackResponse> feedbackResponses = (List<FeedbackResponse>) q.execute(courseId, giverEmail);

        return feedbackResponses;
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {

        FeedbackResponseAttributes feedbackResponseToGet =
                (FeedbackResponseAttributes) attributes;

        if (feedbackResponseToGet.getId() != null) {
            return getFeedbackResponseEntity(feedbackResponseToGet.getId());
        }

        return getFeedbackResponseEntity(
            feedbackResponseToGet.feedbackQuestionId,
            feedbackResponseToGet.giver,
            feedbackResponseToGet.recipient);
    }

    @Override
    protected QueryWithParams getEntityKeyOnlyQuery(EntityAttributes attributes) {
        Class<?> entityClass = FeedbackResponse.class;
        String primaryKeyName = FeedbackResponse.PRIMARY_KEY_NAME;
        FeedbackResponseAttributes fra = (FeedbackResponseAttributes) attributes;
        String id = fra.getId();

        Query q = getPm().newQuery(entityClass);
        Object[] params;

        if (id == null) {
            q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam, String receiverParam");
            q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam && "
                        + "receiver == receiverParam");
            params = new Object[] {fra.feedbackQuestionId, fra.giver, fra.recipient};
        } else {
            q.declareParameters("String idParam");
            q.setFilter(primaryKeyName + " == idParam");
            params = new Object[] {id};
        }

        return new QueryWithParams(q, params, primaryKeyName);
    }
}
