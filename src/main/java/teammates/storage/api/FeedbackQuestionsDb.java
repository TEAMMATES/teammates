package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackQuestion;

public class FeedbackQuestionsDb extends EntitiesDb {
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Feedback Question : ";
    private static final Logger log = Utils.getLogger();
    
    public void createFeedbackQuestions(Collection<FeedbackQuestionAttributes> questionsToAdd) throws InvalidParametersException {
        List<EntityAttributes> questionsToUpdate = createEntities(questionsToAdd);
        for (EntityAttributes entity : questionsToUpdate) {
            FeedbackQuestionAttributes question = (FeedbackQuestionAttributes) entity;
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
    public FeedbackQuestionAttributes getFeedbackQuestion (String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        FeedbackQuestion fq = getFeedbackQuestionEntity(feedbackQuestionId);
        
        if (fq == null) {
            log.info("Trying to get non-existent Question: " + feedbackQuestionId);
            return null;
        }
        
        return new FeedbackQuestionAttributes(fq);        
    }
    
    
    public FeedbackQuestionAttributes createFeedbackQuestionWithoutExistenceCheck(
            EntityAttributes entityToAdd) throws InvalidParametersException {
        Object obj = this.createEntityWithoutExistenceCheck(entityToAdd);
        
        return new FeedbackQuestionAttributes((FeedbackQuestion) obj);
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
            log.info("Trying to get non-existent Question: " +
                         questionNumber + "." + feedbackSessionName + "/" + courseId);
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
        List<FeedbackQuestionAttributes> fqList = new ArrayList<FeedbackQuestionAttributes>();

        for (FeedbackQuestion question : questions) {
            fqList.add(new FeedbackQuestionAttributes(question));
        }
        
        return fqList;
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
        List<FeedbackQuestionAttributes> fqList = new ArrayList<FeedbackQuestionAttributes>();

        for (FeedbackQuestion question : questions) {
            fqList.add(new FeedbackQuestionAttributes(question));
        }
        
        return fqList;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<FeedbackQuestion> questions = getFeedbackQuestionEntitiesForCourse(courseId);
        List<FeedbackQuestionAttributes> fqList = new ArrayList<FeedbackQuestionAttributes>();

        for (FeedbackQuestion question : questions) {
            fqList.add(new FeedbackQuestionAttributes(question));
        }
        
        return fqList;
    }
    
    /**
     * Updates the feedback question identified by `{@code newAttributes.getId()} 
     * For the remaining parameters, the existing value is preserved 
     *   if the parameter is null (due to 'keep existing' policy).<br> 
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and
     *  correspond to an existing feedback question. <br>
     */
    public void updateFeedbackQuestion(FeedbackQuestionAttributes newAttributes) throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, newAttributes);
        
        // TODO: Sanitize values and update tests accordingly
        
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        
        FeedbackQuestion fq = (FeedbackQuestion) getEntity(newAttributes);
        
        if (fq == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
        }
        
        fq.setQuestionNumber(newAttributes.questionNumber);
        fq.setQuestionText(newAttributes.questionMetaData);
        fq.setQuestionType(newAttributes.questionType);
        fq.setGiverType(newAttributes.giverType);
        fq.setRecipientType(newAttributes.recipientType);
        fq.setShowResponsesTo(newAttributes.showResponsesTo);
        fq.setShowGiverNameTo(newAttributes.showGiverNameTo);
        fq.setShowRecipientNameTo(newAttributes.showRecipientNameTo);
        fq.setNumberOfEntitiesToGiveFeedbackTo(newAttributes.numberOfEntitiesToGiveFeedbackTo);
        
        log.info(newAttributes.getBackupIdentifier());
        getPM().close();
    }
    
    public void deleteFeedbackQuestionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<String> courseIds = new ArrayList<String>();
        courseIds.add(courseId);
        deleteFeedbackQuestionsForCourses(courseIds);
    }
    
    public void deleteFeedbackQuestionsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        
        List<FeedbackQuestion> feedbackQuestionList = getFeedbackQuestionEntitiesForCourses(courseIds);
        
        getPM().deletePersistentAll(feedbackQuestionList);
        getPM().flush();
    }
    
    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForCourses(List<String> courseIds) {
        Query q = getPM().newQuery(FeedbackQuestion.class);
        q.setFilter(":p.contains(courseId)");
        
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestionList = (List<FeedbackQuestion>) q.execute(courseIds);
        
        return feedbackQuestionList;
    }
    
    // Gets a question entity if it's Key (feedbackQuestionId) is known.
    private FeedbackQuestion getFeedbackQuestionEntity(String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        Query q = getPM().newQuery(FeedbackQuestion.class);
        q.declareParameters("String feedbackQuestionIdParam");
        q.setFilter("feedbackQuestionId == feedbackQuestionIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestionList =
                (List<FeedbackQuestion>) q.execute(feedbackQuestionId);
        
        if (feedbackQuestionList.isEmpty() || JDOHelper.isDeleted(feedbackQuestionList.get(0))) {
            return null;
        }
        
        return feedbackQuestionList.get(0);
    }
    
    // Gets a feedbackQuestion based on feedbackSessionName and questionNumber.
    private FeedbackQuestion getFeedbackQuestionEntity(
            String feedbackSessionName, String courseId, int questionNumber) {
        
        Query q = getPM().newQuery(FeedbackQuestion.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, int questionNumberParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && " +
                "courseId == courseIdParam && " +
                "questionNumber == questionNumberParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestionList =
            (List<FeedbackQuestion>) q.execute(feedbackSessionName, courseId, questionNumber);
        
        if (feedbackQuestionList.isEmpty() || JDOHelper.isDeleted(feedbackQuestionList.get(0))) {
            return null;
        }
        
        return feedbackQuestionList.get(0);
    }
    
    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForSession(
            String feedbackSessionName, String courseId) {
        Query q = getPM().newQuery(FeedbackQuestion.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestionList = 
            (List<FeedbackQuestion>) q.execute(feedbackSessionName, courseId);
        
        return feedbackQuestionList;
    }
    
    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForCourse(String courseId) {
        Query q = getPM().newQuery(FeedbackQuestion.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestionList = 
            (List<FeedbackQuestion>) q.execute(courseId);
        
        return feedbackQuestionList;
    }
    
    private List<FeedbackQuestion> getFeedbackQuestionEntitiesForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        Query q = getPM().newQuery(FeedbackQuestion.class);
        q.declareParameters("String feedbackSessionNameParam, " +
                "String courseIdParam, " +
                "FeedbackParticipantType giverTypeParam");
        q.declareImports("import teammates.common.datatransfer.FeedbackParticipantType");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && " +
                "courseId == courseIdParam && " +
                "giverType == giverTypeParam ");
        
        @SuppressWarnings("unchecked")
        List<FeedbackQuestion> feedbackQuestionList = 
            (List<FeedbackQuestion>) q.execute(feedbackSessionName, courseId, giverType);
        
        return feedbackQuestionList;
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        FeedbackQuestionAttributes feedbackQuestionToGet = (FeedbackQuestionAttributes) attributes;
        
        if (feedbackQuestionToGet.getId() != null) {
            return getFeedbackQuestionEntity(feedbackQuestionToGet.getId());
        } else {
            return getFeedbackQuestionEntity(
                    feedbackQuestionToGet.feedbackSessionName,
                    feedbackQuestionToGet.courseId,
                    feedbackQuestionToGet.questionNumber);
        }
    }
}
