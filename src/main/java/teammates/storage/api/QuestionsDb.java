package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Query;
import javax.jdo.Transaction;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.storage.entity.Question;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.QuestionAttributes;

public class QuestionsDb extends EntitiesDb {
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Feedback Question : ";
    
    @Override
    public List<EntityAttributes> createEntities(Collection<? extends EntityAttributes> entitiesToAdd)
                throws InvalidParametersException {
        
        List<EntityAttributes> entitiesToUpdate = new ArrayList<>();
        
        for (EntityAttributes entity : entitiesToAdd) {
            FeedbackQuestionAttributes questionAttributes = (FeedbackQuestionAttributes) entity;
            try {
                createEntity(questionAttributes);
            } catch (EntityAlreadyExistsException e) {
                entitiesToUpdate.add(questionAttributes);
            }
        }
        return entitiesToUpdate;
    }

    
    @Override
    public Object createEntity(EntityAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        
        QuestionAttributes questionToAdd = (QuestionAttributes) entityToAdd;
        
        String courseId = questionToAdd.courseId;
        String feedbackSessionName = questionToAdd.feedbackSessionName;
        FeedbackSessionAttributes session = new FeedbackSessionsDb().getFeedbackSession(
                                                                        courseId, feedbackSessionName);
        try {
            return createFeedbackQuestion(session, questionToAdd);
        } catch (EntityDoesNotExistException e) {
            throw new InvalidParametersException(
                    "feedbackSessionName and courseId provided does not refer to an existing feedback session: "
                    + courseId + "/" + feedbackSessionName);
            
        }
    }
    
    public void createFeedbackQuestions(FeedbackSessionAttributes session,
                Collection<FeedbackQuestionAttributes> questionsToAdd)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Transaction txn = getPm().currentTransaction();
        try {
            txn.begin();
            FeedbackSession fs = new FeedbackSessionsDb().getEntity(session);
            
            if (fs == null) {
                throw new EntityDoesNotExistException(
                        ERROR_UPDATE_NON_EXISTENT + session.toString());
            }
            
            for (FeedbackQuestionAttributes questionToAdd : questionsToAdd) {
                questionToAdd.sanitizeForSaving();
                
                if (!questionToAdd.isValid()) {
                    throw new InvalidParametersException(questionToAdd.getInvalidityInfo());
                }
                
                QuestionAttributes questionAttributes = new QuestionAttributes(questionToAdd);
                fs.getFeedbackQuestions().add(questionAttributes.toEntity());
                
                log.info(questionToAdd.getBackupIdentifier());
            }
            
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
            getPm().close();
        }
    }

    public Question createFeedbackQuestion(FeedbackSessionAttributes fsa, FeedbackQuestionAttributes question)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, fsa);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, question);
        
        return addQuestionToSession(fsa, question);
    }
    
    private Question addQuestionToSession(
            FeedbackSessionAttributes existingSession, FeedbackQuestionAttributes question)
        throws EntityDoesNotExistException, EntityAlreadyExistsException, InvalidParametersException {
        
        if (!question.isValid()) {
            throw new InvalidParametersException(question.getInvalidityInfo());
        }
        
        Transaction txn = getPm().currentTransaction();
        try {
            txn.begin();
        
            Question entity = addQuestionToSessionWithoutCommitting(existingSession, question);
            
            txn.commit();
            
            return entity;
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
            getPm().close();
        }
    }
    
    private Question addQuestionToSessionWithoutCommitting(FeedbackSessionAttributes existingSession,
            FeedbackQuestionAttributes question) throws EntityDoesNotExistException,
            EntityAlreadyExistsException {
        FeedbackSession fs = (FeedbackSession) getEntity(existingSession);
        
        if (fs == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + existingSession.toString());
        }
        
        if (fs.getFeedbackQuestions().contains(question.toEntity())) {
            String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS,
                    question.getEntityTypeAsString()) + question.getIdentificationString();
            log.info(error);
            throw new EntityAlreadyExistsException(error, question);
        }
        
        QuestionAttributes questionAttributes = new QuestionAttributes(question);
        Question questionEntity = questionAttributes.toEntity();
        fs.getFeedbackQuestions().add(questionEntity);
        
        return questionEntity;
    }

    private void createFeedbackQuestionWithoutCommitting(FeedbackSessionAttributes fsa, FeedbackQuestionAttributes question)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, fsa);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, question);
        
        addQuestionToSessionWithoutCommitting(fsa, question);
    }
    
    public void saveQuestionAndAdjustQuestionNumbers(FeedbackSessionAttributes session,
                                                     FeedbackQuestionAttributes questionToAddOrUpdate,
                                                     boolean isUpdating,
                                                     int oldQuestionNumber)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Transaction txn = getPm().currentTransaction();
        
        try {
            txn.begin();
            
            FeedbackSession fs = new FeedbackSessionsDb().getEntity(session);
            
            if (fs == null) {
                throw new EntityDoesNotExistException("Session disappeared");
            }
            if (!questionToAddOrUpdate.isValid()) {
                throw new InvalidParametersException(questionToAddOrUpdate.getInvalidityInfo());
            }
            
            List<FeedbackQuestionAttributes> questions = getFeedbackQuestionsForSession(fs);
            
            int numberAdjustmentRangeStart = oldQuestionNumber <= 0 ? questions.size() + 1 : oldQuestionNumber;

            if (questionToAddOrUpdate.questionNumber <= 0) {
                questionToAddOrUpdate.questionNumber = questions.size() + 1;
            }
            adjustQuestionNumbersWithoutCommitting(numberAdjustmentRangeStart,
                                                   questionToAddOrUpdate.questionNumber, questions);
            if (isUpdating) {
                updateFeedbackQuestionWithoutFlushing(questionToAddOrUpdate);
            } else {
                questionToAddOrUpdate.setId(questionToAddOrUpdate.makeId());
                createFeedbackQuestionWithoutCommitting(session, questionToAddOrUpdate);
            }
            txn.commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
            getPm().close();
        }
    }

    private void adjustQuestionNumbersWithoutCommitting(int oldQuestionNumber, int newQuestionNumber,
                                                        List<FeedbackQuestionAttributes> questions) {
        if (oldQuestionNumber > newQuestionNumber && oldQuestionNumber >= 1) {
            for (int i = oldQuestionNumber - 1; i >= newQuestionNumber; i--) {
                FeedbackQuestionAttributes question = questions.get(i - 1);
                question.questionNumber += 1;
                try {
                    updateFeedbackQuestionWithoutFlushing(question, false);
                } catch (InvalidParametersException e) {
                    Assumption.fail("Invalid question. " + e);
                } catch (EntityDoesNotExistException e) {
                    Assumption.fail("Question disappeared." + e);
                }
            }
        } else if (oldQuestionNumber < newQuestionNumber && oldQuestionNumber < questions.size()) {
            for (int i = oldQuestionNumber + 1; i <= newQuestionNumber; i++) {
                FeedbackQuestionAttributes question = questions.get(i - 1);
                question.questionNumber -= 1;
                try {
                    updateFeedbackQuestionWithoutFlushing(question, false);
                } catch (InvalidParametersException e) {
                    Assumption.fail("Invalid question." + e);
                } catch (EntityDoesNotExistException e) {
                    Assumption.fail("Question disappeared." + e);
                }
            }
        }
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackSessionName,
                                                          String courseId,
                                                          String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        Question fq = getFeedbackQuestionEntity(feedbackSessionName, courseId, feedbackQuestionId);
        
        if (fq == null) {
            log.info("Trying to get non-existent Question: " + feedbackQuestionId);
            return null;
        }
        
        return new FeedbackQuestionAttributes(fq);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(
            String feedbackSessionName, String courseId, int questionNumber) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, questionNumber);

        Question fq = getFeedbackQuestionEntity(feedbackSessionName,
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

        List<Question> questions = getFeedbackQuestionEntitiesForSession(feedbackSessionName, courseId);
        return getFeedbackQuestionAttributesFromFeedbackQuestions(questions);
    }
    
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(FeedbackSession feedbackSession) {
        return getFeedbackQuestionAttributesFromFeedbackQuestions(feedbackSession.getFeedbackQuestions());
    }

    public static List<FeedbackQuestionAttributes> getFeedbackQuestionAttributesFromFeedbackQuestions(
                                                        Collection<Question> questions) {
        List<FeedbackQuestionAttributes> fqList = new ArrayList<FeedbackQuestionAttributes>();

        for (Question question : questions) {
            if (!JDOHelper.isDeleted(question)) {
                fqList.add(new FeedbackQuestionAttributes(question));
            }
        }
        
        Collections.sort(fqList);
        return fqList;
    }
    
    public static List<Question> getFeedbackQuestionEntitiesFromFeedbackQuestionAttributes(
            Collection<FeedbackQuestionAttributes> questions) {
        
        if (questions == null) {
            return new ArrayList<Question>();
        }
        
        List<Question> fqList = new ArrayList<Question>();
        for (FeedbackQuestionAttributes question : questions) {
            QuestionAttributes questionAttributes = new QuestionAttributes(question);
            fqList.add(questionAttributes.toEntity());
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

        List<Question> questions = getFeedbackQuestionEntitiesForGiverType(
                feedbackSessionName, courseId, giverType);
        return getFeedbackQuestionAttributesFromFeedbackQuestions(questions);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<Question> questions = getFeedbackQuestionEntitiesForCourse(courseId);
        return getFeedbackQuestionAttributesFromFeedbackQuestions(questions);
    }
    
    /**
     * Updates the feedback question identified by `{@code newAttributes.getId()}
     *   and changes the {@code updatedAt} timestamp to be the time of update.
     * For the remaining parameters, the existing value is preserved
     *   if the parameter is null (due to 'keep existing' policy).<br>
     * 
     * Preconditions: <br>
     * * {@code newAttributes.getId()} is non-null and
     *  correspond to an existing feedback question. <br>
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
        if (!newAttributes.isValid()) {
            throw new InvalidParametersException(newAttributes.getInvalidityInfo());
        }
        
        // TODO: Sanitize values and update tests accordingly
        
        updateFeedbackQuestionWithoutFlushing(newAttributes, keepUpdateTimestamp);
        
        log.info(newAttributes.getBackupIdentifier());
        getPm().close();
    }
    
    public void updateFeedbackQuestionWithoutFlushing(FeedbackQuestionAttributes newAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        updateFeedbackQuestionWithoutFlushing(newAttributes, false);
    }

    public void updateFeedbackQuestionWithoutFlushing(FeedbackQuestionAttributes newAttributes,
            boolean keepUpdateTimestamp) throws InvalidParametersException, EntityDoesNotExistException {
        Question fq = (Question) getEntity(newAttributes);
        
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
        
        //set true to prevent changes to last update timestamp
        fq.keepUpdateTimestamp = keepUpdateTimestamp;
    }
    
    public void deleteQuestion(FeedbackSessionAttributes fsa, FeedbackQuestionAttributes questionToDelete)
            throws EntityDoesNotExistException {
        Transaction txn = getPm().currentTransaction();
        try {
            txn.begin();
            FeedbackSession fs = new FeedbackSessionsDb().getEntity(fsa);
            
            if (fs == null) {
                throw new EntityDoesNotExistException(
                        ERROR_UPDATE_NON_EXISTENT + fsa.toString());
            }
            
            fs.getFeedbackQuestions().remove(questionToDelete);
            deleteEntity(questionToDelete);
            
            getPm().currentTransaction().commit();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
            getPm().close();
        }
    }
    
    public void deleteFeedbackQuestionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<String> courseIds = new ArrayList<String>();
        courseIds.add(courseId);
        deleteFeedbackQuestionsForCourses(courseIds);
    }
    
    public void deleteFeedbackQuestionsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        
        List<Question> feedbackQuestionList = getFeedbackQuestionEntitiesForCourses(courseIds);
        
        getPm().deletePersistentAll(feedbackQuestionList);
        getPm().flush();
    }
    
    private List<Question> getFeedbackQuestionEntitiesForCourses(List<String> courseIds) {
        Query q = getPm().newQuery(Question.class);
        q.setFilter(":p.contains(courseId)");
        
        @SuppressWarnings("unchecked")
        List<Question> feedbackQuestionList = (List<Question>) q.execute(courseIds);
        
        return feedbackQuestionList;
    }
    
    private Question getFeedbackQuestionEntity(String feedbackSessionName, String courseId, String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        Key k = KeyFactory.createKey(FeedbackSession.class.getSimpleName(),
                                     FeedbackSessionAttributes.makeId(feedbackSessionName, courseId))
                          .getChild(Question.class.getSimpleName(), feedbackQuestionId);
        try {
            return getPm().getObjectById(Question.class, k);
        } catch (JDOObjectNotFoundException e) {
            return null;
        }
    }
    
    // Gets a feedbackQuestion based on feedbackSessionName and questionNumber.
    private Question getFeedbackQuestionEntity(
            String feedbackSessionName, String courseId, int questionNumber) {
        
        Query q = getPm().newQuery(Question.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam, int questionNumberParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && "
                    + "courseId == courseIdParam && "
                    + "questionNumber == questionNumberParam");
        
        @SuppressWarnings("unchecked")
        List<Question> feedbackQuestionList =
                (List<Question>) q.execute(feedbackSessionName, courseId, questionNumber);
        
        if (feedbackQuestionList.size() > 1) {
            log.severe("More than one question with same question number in "
                      + courseId + "/" + feedbackSessionName + " question " + questionNumber);
        }
        
        if (feedbackQuestionList.isEmpty() || JDOHelper.isDeleted(feedbackQuestionList.get(0))) {
            return null;
        }
        
        return feedbackQuestionList.get(0);
    }
    
    private List<Question> getFeedbackQuestionEntitiesForSession(
            String feedbackSessionName, String courseId) {
        Query q = getPm().newQuery(Question.class);
        q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Question> feedbackQuestionList =
                (List<Question>) q.execute(feedbackSessionName, courseId);
        
        return feedbackQuestionList;
    }
    
    private List<Question> getFeedbackQuestionEntitiesForCourse(String courseId) {
        Query q = getPm().newQuery(Question.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Question> feedbackQuestionList = (List<Question>) q.execute(courseId);
        
        return feedbackQuestionList;
    }
    
    private List<Question> getFeedbackQuestionEntitiesForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        Query q = getPm().newQuery(Question.class);
        q.declareParameters("String feedbackSessionNameParam, "
                            + "String courseIdParam, "
                            + "FeedbackParticipantType giverTypeParam");
        q.declareImports("import teammates.common.datatransfer.FeedbackParticipantType");
        q.setFilter("feedbackSessionName == feedbackSessionNameParam && "
                    + "courseId == courseIdParam && "
                    + "giverType == giverTypeParam ");
        
        @SuppressWarnings("unchecked")
        List<Question> feedbackQuestionList =
                (List<Question>) q.execute(feedbackSessionName, courseId, giverType);
        
        return feedbackQuestionList;
    }
    
    public static List<FeedbackQuestionAttributes> getListOfQuestionAttributes(List<Question> questions) {
        List<FeedbackQuestionAttributes> questionAttributes = new ArrayList<FeedbackQuestionAttributes>();

        for (Question question : questions) {
            if (!JDOHelper.isDeleted(question)) {
                questionAttributes.add(new FeedbackQuestionAttributes(question));
            }
        }
        
        return questionAttributes;
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        FeedbackQuestionAttributes feedbackQuestionToGet = (FeedbackQuestionAttributes) attributes;
        
        if (feedbackQuestionToGet.getId() != null) {
            return getFeedbackQuestionEntity(feedbackQuestionToGet.feedbackSessionName,
                                             feedbackQuestionToGet.courseId,
                                             feedbackQuestionToGet.getId());
        }
        
        return getFeedbackQuestionEntity(
                feedbackQuestionToGet.feedbackSessionName,
                feedbackQuestionToGet.courseId,
                feedbackQuestionToGet.questionNumber);
    }

}
