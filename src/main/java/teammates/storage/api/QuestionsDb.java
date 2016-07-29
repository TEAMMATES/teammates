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
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.storage.entity.Question;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.QuestionsDbPersistenceAttributes;

public class QuestionsDb extends EntitiesDb {
    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Question : ";
    
    @Override
    public List<EntityAttributes> createEntities(Collection<? extends EntityAttributes> entitiesToAdd)
            throws InvalidParametersException {
        
        List<EntityAttributes> entitiesToUpdate = new ArrayList<>();
        
        for (EntityAttributes entity : entitiesToAdd) {
            QuestionsDbPersistenceAttributes questionAttributes = (QuestionsDbPersistenceAttributes) entity;
            try {
                createEntity(questionAttributes);
            } catch (EntityAlreadyExistsException e) {
                entitiesToUpdate.add(questionAttributes);
            }
        }
        return entitiesToUpdate;
    }
    
    @Override
    public Question createEntity(EntityAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entityToAdd);
        
        QuestionsDbPersistenceAttributes questionToAdd = (QuestionsDbPersistenceAttributes) entityToAdd;
        
        String courseId = questionToAdd.courseId;
        String feedbackSessionName = questionToAdd.feedbackSessionName;
        
        FeedbackSessionAttributes session = new FeedbackSessionAttributes();
        session.setCourseId(courseId);
        session.setFeedbackSessionName(feedbackSessionName);
        
        try {
            return createFeedbackQuestion(session, questionToAdd);
        } catch (EntityDoesNotExistException e) {
            Assumption.fail("Session disappeared");
            return null;
        }
    }
    
    /**
     * Creates question. If the question already exist, simply writes over it instead of failing.
     * @param fqa
     * @return attributes written to the database
     * @throws InvalidParametersException
     */
    public FeedbackQuestionAttributes createFeedbackQuestionWithoutExistenceCheck(FeedbackQuestionAttributes fqa)
            throws InvalidParametersException {
        try {
            QuestionsDbPersistenceAttributes questionAttributes = new QuestionsDbPersistenceAttributes(fqa);
            Question persistedQuestion = createEntity(questionAttributes);
            return new FeedbackQuestionAttributes(persistedQuestion);
        } catch (EntityAlreadyExistsException e) {
            try {
                updateFeedbackQuestion(fqa);
            } catch (EntityDoesNotExistException entityDoesNotExist) {
                Assumption.fail("Unable to find question that should already exist "
                                + TeammatesException.toStringWithStackTrace(entityDoesNotExist));
            }
            return fqa;
        }
    }
    
    public void createFeedbackQuestions(
            FeedbackSessionAttributes session, Collection<FeedbackQuestionAttributes> questionsToAdd)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Transaction txn = getPm().currentTransaction();
        try {
            txn.begin();
            FeedbackSession fs = new FeedbackSessionsDb().getEntity(session);
            
            if (fs == null) {
                throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + session.toString());
            }
            
            for (FeedbackQuestionAttributes questionToAdd : questionsToAdd) {
                questionToAdd.sanitizeForSaving();
                
                if (!questionToAdd.isValid()) {
                    throw new InvalidParametersException(questionToAdd.getInvalidityInfo());
                }
                
                QuestionsDbPersistenceAttributes questionAttributes = new QuestionsDbPersistenceAttributes(questionToAdd);
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

    public Question createFeedbackQuestion(FeedbackSessionAttributes session, FeedbackQuestionAttributes question)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, session);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, question);
        
        return addQuestionToSession(session, question);
    }
    
    /**
     * Add {@code question} to {@code existingSession}. This is done in a transaction so this
     * cannot be called in as part of a separate transaction.
     * @param existingSession
     * @param question
     * @throws EntityDoesNotExistException
     * @throws EntityAlreadyExistsException
     * @throws InvalidParametersException
     */
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
    
    /**
     * Add {@code question} to {@code existingSession}. This is not flushed or commited, therefore
     * the caller of this method must handle committing or flushing.
     * @param existingSession
     * @param question
     * @throws EntityDoesNotExistException
     * @throws EntityAlreadyExistsException
     */
    private Question addQuestionToSessionWithoutCommitting(
            FeedbackSessionAttributes existingSession, FeedbackQuestionAttributes question)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        FeedbackSession fs = new FeedbackSessionsDb().getEntity(existingSession);
        
        if (fs == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + existingSession.toString());
        }
        
        if (fs.getFeedbackQuestions().contains(question.toEntity())) {
            String error = String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS,
                    question.getEntityTypeAsString()) + question.getIdentificationString();
            log.info(error);
            throw new EntityAlreadyExistsException(error, question);
        }
        
        QuestionsDbPersistenceAttributes questionAttributes = new QuestionsDbPersistenceAttributes(question);
        Question questionEntity = questionAttributes.toEntity();
        fs.getFeedbackQuestions().add(questionEntity);
        
        return questionEntity;
    }

    /**
     * Adds {@code question} to {@code session}. This does not commit or flush so the caller of this
     * method needs to handle committing.
     * @param session
     * @param question
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     * @throws EntityAlreadyExistsException
     */
    private FeedbackQuestionAttributes createFeedbackQuestionWithoutCommitting(
            FeedbackSessionAttributes session, FeedbackQuestionAttributes question)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, session);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, question);
        
        return new FeedbackQuestionAttributes(addQuestionToSessionWithoutCommitting(session, question));
    }
    
    /**
     * Saves (either creating or updating one, determined by {@code isUpdating}) a question
     * to {@code session}.
     * @param session
     * @param questionToSave
     * @param isUpdating
     * @param oldQuestionNumber
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     * @throws EntityAlreadyExistsException
     */
    public FeedbackQuestionAttributes saveQuestionAndAdjustQuestionNumbers(
            FeedbackQuestionAttributes questionToSave, boolean isUpdating, int oldQuestionNumber)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        
        String courseId = questionToSave.courseId;
        String feedbackSessionName = questionToSave.feedbackSessionName;
       
        Transaction txn = getPm().currentTransaction();
        try {
            txn.begin();
            
            FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
            fsa.setCourseId(courseId);
            fsa.setFeedbackSessionName(feedbackSessionName);
            FeedbackSession fs = new FeedbackSessionsDb().getEntity(fsa);
            
            if (fs == null) {
                throw new EntityDoesNotExistException("Session disappeared");
            }
            if (!questionToSave.isValid()) {
                throw new InvalidParametersException(questionToSave.getInvalidityInfo());
            }
            
            adjustQuestionNumbersInSession(questionToSave, oldQuestionNumber, fs);
            
            FeedbackQuestionAttributes savedQuestion;
            if (isUpdating) {
                savedQuestion = updateFeedbackQuestionWithoutComitting(questionToSave);
            } else {
                savedQuestion = createFeedbackQuestionWithoutCommitting(fsa, questionToSave);
            }
            txn.commit();
            
            return savedQuestion;
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
            getPm().close();
        }
    }

    private void adjustQuestionNumbersInSession(
            FeedbackQuestionAttributes questionToSave, int oldQuestionNumber, FeedbackSession session) {
        List<FeedbackQuestionAttributes> questionsForAdjustingNumbers = getFeedbackQuestionsForSession(session);
        
        // remove question getting edited
        FeedbackQuestionAttributes.removeQuestionWithIdInQuestions(
                questionToSave.getId(), questionsForAdjustingNumbers);
        
        if (questionToSave.questionNumber <= 0) {
            questionToSave.questionNumber = questionsForAdjustingNumbers.size() + 1;
        }
        int numberAdjustmentRangeStart = oldQuestionNumber <= 0 ? questionsForAdjustingNumbers.size() + 1
                                                                : oldQuestionNumber;
        
        adjustQuestionNumbersWithoutCommitting(
                numberAdjustmentRangeStart, questionToSave.questionNumber, questionsForAdjustingNumbers);
    }

    
    /**
     * Adjusts {@code questions} between {@code oldQuestionNumber} and {@code newQuestionNumber}
     * @param oldQuestionNumber
     * @param newQuestionNumber
     * @param questions sorted list of question
     */
    public void adjustQuestionNumbers(
            int oldQuestionNumber, int newQuestionNumber, List<FeedbackQuestionAttributes> questions) {
        adjustQuestionNumbersWithoutCommitting(oldQuestionNumber, newQuestionNumber, questions);
        getPm().close();
    }

    /**
     * Shifts the question numbers of {@code questions} between oldQuestionNumber and newQuestionNumber,
     * if oldQuestionNumber is less than newQuestionNumber, the question numbers of the affected questions
     * are decreased by 1, otherwise they are increased by 1.
     * Does not commit or flush, the caller of this method must handle that.
     * * This should be called in an active transaction.
     * @param oldQuestionNumber
     * @param newQuestionNumber
     * @param questions
     */
    private void adjustQuestionNumbersWithoutCommitting(
            int oldQuestionNumber, int newQuestionNumber, List<FeedbackQuestionAttributes> questions) {
        if (oldQuestionNumber <= 0 || newQuestionNumber <= 0) {
            Assumption.fail("Invalid question number");
        }
        if (oldQuestionNumber > newQuestionNumber) {
            increaseQuestionNumber(newQuestionNumber, oldQuestionNumber, questions);
            
        } else if (oldQuestionNumber < newQuestionNumber) {
            decreaseQuestionNumber(oldQuestionNumber, newQuestionNumber, questions);
        }
    }
    
    private void increaseQuestionNumber(int start, int end, List<FeedbackQuestionAttributes> questions) {
        for (FeedbackQuestionAttributes question : questions) {
            if (question.questionNumber >= start && question.questionNumber <= end) {
                adjustQuestionNumberOfQuestion(question, 1);
            }
        }
    }
    
    private void decreaseQuestionNumber(int start, int end, List<FeedbackQuestionAttributes> questions) {
        for (FeedbackQuestionAttributes question : questions) {
            if (question.questionNumber >= start && question.questionNumber <= end) {
                adjustQuestionNumberOfQuestion(question, -1);
            }
        }
    }

    private void adjustQuestionNumberOfQuestion(FeedbackQuestionAttributes question, int change) {
        FeedbackQuestionAttributes updatedQuestion = question.getCopy();
        updatedQuestion.questionNumber += change;
        try {
            updateFeedbackQuestionWithoutComitting(updatedQuestion);
        } catch (InvalidParametersException e) {
            Assumption.fail("Invalid question." + e);
        } catch (EntityDoesNotExistException e) {
            // this can happen if question is an old question which did not have a Question copy of it
            // TODO Remove silencing the exception, this is not expected after the migration.
            log.warning("EntityDoesNotExistException thrown for " + e);
        }
    }
    
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(
            String feedbackSessionName, String courseId, String feedbackQuestionId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);

        Question fq = getFeedbackQuestionEntity(feedbackSessionName, courseId, feedbackQuestionId);
        
        if (fq == null || JDOHelper.isDeleted(fq)) {
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

        Question fq = getFeedbackQuestionEntity(feedbackSessionName, courseId, questionNumber);
        
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
        return getListOfQuestionAttributes(questions);
    }
    
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(FeedbackSession feedbackSession) {
        return getListOfQuestionAttributes(feedbackSession.getFeedbackQuestions());
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
        return getListOfQuestionAttributes(questions);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<Question> questions = getFeedbackQuestionEntitiesForCourse(courseId);
        return getListOfQuestionAttributes(questions);
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
    public void updateFeedbackQuestion(FeedbackQuestionAttributes question, boolean keepUpdateTimestamp)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, question);
        if (!question.isValid()) {
            throw new InvalidParametersException(question.getInvalidityInfo());
        }
        
        updateQuestionWithoutFlushing(question, keepUpdateTimestamp);
        
        log.info(question.getBackupIdentifier());
        getPm().close();
    }
    
    /**
     * Updates the given feedback question. Does not flush or commit therefore any code calling this
     * must handle flushing or committing.
     * @param question
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public FeedbackQuestionAttributes updateFeedbackQuestionWithoutComitting(
            FeedbackQuestionAttributes question)
            throws InvalidParametersException, EntityDoesNotExistException {
        return new FeedbackQuestionAttributes(
                     updateQuestionWithoutFlushing(question, false));
    }

    /**
     * Updates the given feedback question. Does not flush or commit therefore any code calling this
     * must handle flushing or committing.
     * @param question
     * @param keepUpdateTimestamp
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public Question updateQuestionWithoutFlushing(
            FeedbackQuestionAttributes question, boolean keepUpdateTimestamp)
            throws InvalidParametersException, EntityDoesNotExistException {
        Question fq = (Question) getEntity(question);
        
        if (fq == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + question.toString());
        }
        
        fq.setQuestionNumber(question.questionNumber);
        fq.setQuestionText(question.questionMetaData);
        fq.setQuestionDescription(question.getQuestionDescription());
        fq.setQuestionType(question.questionType);
        fq.setGiverType(question.giverType);
        fq.setRecipientType(question.recipientType);
        fq.setShowResponsesTo(question.showResponsesTo);
        fq.setShowGiverNameTo(question.showGiverNameTo);
        fq.setShowRecipientNameTo(question.showRecipientNameTo);
        fq.setNumberOfEntitiesToGiveFeedbackTo(question.numberOfEntitiesToGiveFeedbackTo);
        
        //set true to prevent changes to last update timestamp
        fq.keepUpdateTimestamp = keepUpdateTimestamp;
        
        return fq;
    }
    
    /**
     * Deletes a question {@code questionToDelete}.
     * This is done in a transaction, therefore any code calling this should not already be in a transaction.
     */
    public void deleteQuestion(FeedbackQuestionAttributes questionToDelete)
            throws EntityDoesNotExistException {
        FeedbackSessionAttributes session = new FeedbackSessionAttributes();
        session.setCourseId(questionToDelete.courseId);
        session.setFeedbackSessionName(questionToDelete.feedbackSessionName);
        deleteQuestion(session, questionToDelete);
    }
    
    /**
     * Deletes a question {@code questionToDelete} contained in  {@code session}
     * This is done in a transaction, therefore any code calling this should not already be in a transaction.
     */
    private void deleteQuestion(FeedbackSessionAttributes session, FeedbackQuestionAttributes questionToDelete)
            throws EntityDoesNotExistException {
        Transaction txn = getPm().currentTransaction();
        try {
            txn.begin();
            FeedbackSession fs = new FeedbackSessionsDb().getEntity(session);
            
            if (fs == null) {
                throw new EntityDoesNotExistException(
                        ERROR_UPDATE_NON_EXISTENT + session.toString());
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
    
    /**
     * Queries for all Question entities in the specified course
     * @param courseIds
     */
    public void deleteFeedbackQuestionsForCourse(String courseId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<String> courseIds = new ArrayList<String>();
        courseIds.add(courseId);
        deleteFeedbackQuestionsForCourses(courseIds);
    }
    
    /**
     * Delete all Question entities in the courses
     * @param courseIds
     */
    public void deleteFeedbackQuestionsForCourses(List<String> courseIds) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        
        List<Question> feedbackQuestionList = getFeedbackQuestionEntitiesForCourses(courseIds);
        
        getPm().deletePersistentAll(feedbackQuestionList);
        getPm().flush();
    }
    
    /**
     * Queries for all Question entities in the courses
     * @param courseIds
     */
    private List<Question> getFeedbackQuestionEntitiesForCourses(List<String> courseIds) {
        Query q = getPm().newQuery(Question.class);
        q.setFilter(":p.contains(courseId)");
        
        @SuppressWarnings("unchecked")
        List<Question> feedbackQuestionList = (List<Question>) q.execute(courseIds);
        
        return feedbackQuestionList;
    }
    
    /**
     * Retrieves a Question identified on feedbackSessionName, courseId and feedbackQuestionId.
     * 
     * @param feedbackSessionName
     * @param courseId
     * @param feedbackQuestionId
     */
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
    
    /**
     *  Gets a feedbackQuestion based on feedbackSessionName, courseId and questionNumber.
     */
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
    
    /**
     * Queries for Questions with the given feedbackSessionName and courseId
     * @param feedbackSessionName
     * @param courseId
     */
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
    
    /**
     * Queries for Questions with the given courseId
     * @param courseId
     */
    private List<Question> getFeedbackQuestionEntitiesForCourse(String courseId) {
        Query q = getPm().newQuery(Question.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseId == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Question> feedbackQuestionList = (List<Question>) q.execute(courseId);
        
        return feedbackQuestionList;
    }
    
    /**
     * 
     * Queries for Questions with the given courseId, feedbackSessionName and giver type.
     * @param feedbackSessionName
     * @param courseId
     * @param giverType
     */
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
    
    /**
     * From a list of Questions, converts them and return a list of FeedbackQuestionAttributes
     * @param questions
     */
    public static List<FeedbackQuestionAttributes> getListOfQuestionAttributes(List<Question> questions) {
        List<FeedbackQuestionAttributes> questionAttributes = new ArrayList<FeedbackQuestionAttributes>();

        for (Question question : questions) {
            questionAttributes.add(new FeedbackQuestionAttributes(question));
        }
        Collections.sort(questionAttributes);
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

    /**
     * From a list of FeedbackQuestionAttributes, converts them and return a list of Question
     * @param questions
     */
    public static List<Question> getListOfQuestionEntities(
            Collection<FeedbackQuestionAttributes> questions) {
        
        if (questions == null) {
            return new ArrayList<Question>();
        }
        
        List<Question> fqList = new ArrayList<Question>();
        for (FeedbackQuestionAttributes question : questions) {
            QuestionsDbPersistenceAttributes newQuestionAttributes =
                    new QuestionsDbPersistenceAttributes(question);
            fqList.add(newQuestionAttributes.toEntity());
        }
        return fqList;
    }

}
