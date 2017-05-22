package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.entity.FeedbackQuestion;

/**
 * Handles CRUD operations for feedback questions.
 *
 * @see FeedbackQuestion
 * @see FeedbackQuestionAttributes
 */
public class FeedbackQuestionsDb extends OfyEntitiesDb<FeedbackQuestion, FeedbackQuestionAttributes> {
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Feedback Question : ";

    private static final Logger log = Logger.getLogger();

    public void createFeedbackQuestions(Collection<FeedbackQuestionAttributes> questionsToAdd)
            throws InvalidParametersException {
        List<FeedbackQuestionAttributes> questionsToUpdate = createEntities(questionsToAdd);
        for (FeedbackQuestionAttributes question : questionsToUpdate) {
            try {
                updateFeedbackQuestion(question);
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
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        FeedbackQuestion fq = getFeedbackQuestionEntity(feedbackQuestionId);

        if (fq == null) {
            log.info("Trying to get non-existent Question: " + feedbackQuestionId);
            return null;
        }

        return new FeedbackQuestionAttributes(fq);
    }

    public FeedbackQuestionAttributes createFeedbackQuestionWithoutExistenceCheck(
            FeedbackQuestionAttributes entityToAdd) throws InvalidParametersException {
        FeedbackQuestion feedbackQuestion = createEntityWithoutExistenceCheck(entityToAdd);

        return new FeedbackQuestionAttributes(feedbackQuestion);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(
            String feedbackSessionName,
            String courseId,
            int questionNumber) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, questionNumber);

        FeedbackQuestion fq = getFeedbackQuestionEntity(feedbackSessionName,
                courseId, questionNumber);

        if (fq == null) {
            log.info("Trying to get non-existent Question: "
                     + questionNumber + "." + feedbackSessionName + "/" + courseId);
            return null;
        }

        return new FeedbackQuestionAttributes(fq);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(
            String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<FeedbackQuestion> questions = getFeedbackQuestionEntitiesForSession(
                feedbackSessionName, courseId);
        return getListOfQuestionAttributes(questions);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverType);

        List<FeedbackQuestion> questions = getFeedbackQuestionEntitiesForGiverType(
                feedbackSessionName, courseId, giverType);
        return getListOfQuestionAttributes(questions);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<FeedbackQuestion> questions = getFeedbackQuestionEntitiesForCourse(courseId);
        return getListOfQuestionAttributes(questions);
    }

    private List<FeedbackQuestionAttributes> getListOfQuestionAttributes(List<FeedbackQuestion> questions) {
        List<FeedbackQuestionAttributes> questionAttributes = new ArrayList<FeedbackQuestionAttributes>();

        for (FeedbackQuestion question : questions) {
            questionAttributes.add(new FeedbackQuestionAttributes(question));
        }

        return questionAttributes;
    }

    /**
     * Updates the feedback question identified by `{@code newAttributes.getId()}
     *   and changes the {@code updatedAt} timestamp to be the time of update.
     * For the remaining parameters, the existing value is preserved
     *   if the parameter is null (due to 'keep existing' policy).<br>
     *
     * <p>Preconditions:
     * {@code newAttributes.getId()} is non-null and correspond to an existing feedback question.
     */
    public void updateFeedbackQuestion(FeedbackQuestionAttributes newAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        updateFeedbackQuestion(newAttributes, false);
    }

    /**
     * Updates the feedback question identified by `{@code newAttributes.getId()}
     * For the remaining parameters, the existing value is preserved
     *   if the parameter is null (due to 'keep existing' policy).<br>
     * The timestamp for {@code updatedAt} is independent of the {@code newAttributes}
     *   and depends on the value of {@code keepUpdateTimestamp}
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and
     *  correspond to an existing feedback question. <br>
     */
    public void updateFeedbackQuestion(FeedbackQuestionAttributes newAttributes, boolean keepUpdateTimestamp)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);

        // TODO: Sanitize values and update tests accordingly

        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }

        FeedbackQuestion fq = getEntity(newAttributes);

        if (fq == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }

        fq.setQuestionNumber(newAttributes.questionNumber);
        fq.setQuestionText(newAttributes.questionMetaData);
        fq.setQuestionDescription(newAttributes.questionDescription);
        fq.setQuestionType(newAttributes.questionType);
        fq.setGiverType(newAttributes.giverType);
        fq.setRecipientType(newAttributes.recipientType);
        fq.setShowResponsesTo(newAttributes.showResponsesTo);
        fq.setShowGiverNameTo(newAttributes.showGiverNameTo);
        fq.setShowRecipientNameTo(newAttributes.showRecipientNameTo);
        fq.setNumberOfEntitiesToGiveFeedbackTo(newAttributes.numberOfEntitiesToGiveFeedbackTo);

        //set true to prevent changes to last update timestamp
        fq.keepUpdateTimestamp = keepUpdateTimestamp;

        log.info(newAttributes.getBackupIdentifier());
        ofy().save().entity(fq).now();
    }

    @Override
    public void deleteEntity(FeedbackQuestionAttributes entityToDelete) {
        Key<FeedbackQuestion> keyToDelete = getEntityQueryKeys(entityToDelete).first().now();
        if (keyToDelete == null) {
            return;
        }
        log.info(entityToDelete.getBackupIdentifier());
        ofy().delete().key(keyToDelete).now();
    }

    public void deleteFeedbackQuestionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        deleteFeedbackQuestionsForCourses(Arrays.asList(courseId));
    }

    public void deleteFeedbackQuestionsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);

        ofy().delete().keys(ofy().load().type(FeedbackQuestion.class).filter("courseId in", courseIds).keys()).now();
    }

    // Gets a question entity if it's Key (feedbackQuestionId) is known.
    private FeedbackQuestion getFeedbackQuestionEntity(String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        try {
            return ofy().load().type(FeedbackQuestion.class).id(Long.valueOf(feedbackQuestionId)).now();
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // Gets a feedbackQuestion based on feedbackSessionName and questionNumber.
    private FeedbackQuestion getFeedbackQuestionEntity(
            String feedbackSessionName, String courseId, int questionNumber) {
        return ofy().load().type(FeedbackQuestion.class)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("questionNumber =", questionNumber)
                .first().now();
    }

    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForSession(
            String feedbackSessionName, String courseId) {
        return ofy().load().type(FeedbackQuestion.class)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .list();
    }

    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForCourse(String courseId) {
        return ofy().load().type(FeedbackQuestion.class)
                .filter("courseId =", courseId)
                .list();
    }

    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        return ofy().load().type(FeedbackQuestion.class)
                .filter("feedbackSessionName =", feedbackSessionName)
                .filter("courseId =", courseId)
                .filter("giverType =", giverType)
                .list();
    }

    @Override
    protected FeedbackQuestion getEntity(FeedbackQuestionAttributes attributes) {
        FeedbackQuestionAttributes feedbackQuestionToGet = attributes;

        if (feedbackQuestionToGet.getId() != null) {
            return getFeedbackQuestionEntity(feedbackQuestionToGet.getId());
        }

        return getFeedbackQuestionEntity(
                feedbackQuestionToGet.feedbackSessionName,
                feedbackQuestionToGet.courseId,
                feedbackQuestionToGet.questionNumber);
    }

    @Override
    protected QueryKeys<FeedbackQuestion> getEntityQueryKeys(FeedbackQuestionAttributes attributes) {
        String id = attributes.getId();
        Query<FeedbackQuestion> query;

        if (id == null) {
            query = ofy().load().type(FeedbackQuestion.class)
                    .filter("feedbackSessionName =", attributes.feedbackSessionName)
                    .filter("courseId =", attributes.courseId)
                    .filter("questionNumber =", attributes.questionNumber);
        } else {
            query = ofy().load().type(FeedbackQuestion.class)
                    .filterKey(Key.create(FeedbackQuestion.class, Long.valueOf(id)));
        }

        return query.keys();
    }

    @Override
    public boolean hasEntity(FeedbackQuestionAttributes attributes) {
        return getEntityQueryKeys(attributes).first().now() != null;
    }
}
