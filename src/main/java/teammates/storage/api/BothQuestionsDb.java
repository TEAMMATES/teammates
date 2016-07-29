package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.Question;
import teammates.storage.entity.QuestionsDbPersistenceAttributes;

/**
 * Handles reading and writing for both FeedbackQuestion and Question types.
 * See https://cloud.google.com/datastore/docs/articles/balancing-strong-and-eventual-consistency-with-google-cloud-datastore/#gradual-migration-to-entity-groups
 * 
 * TODO delete this class once the old questions have been migrated to the new question type
 */
public class BothQuestionsDb extends EntitiesDb {
    
    private FeedbackQuestionsDb oldQuestionsDb = new FeedbackQuestionsDb();
    private QuestionsDb newQuestionsDb = new QuestionsDb();
    
    @Override
    public Object createEntity(EntityAttributes entityToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        FeedbackQuestionAttributes fqa =
                new FeedbackQuestionAttributes((FeedbackQuestion) oldQuestionsDb.createEntity(entityToAdd));
        return newQuestionsDb.createEntity(new QuestionsDbPersistenceAttributes(fqa));
    }
    
    public void createFeedbackQuestions(Collection<FeedbackQuestionAttributes> questionsToAdd)
            throws InvalidParametersException {
        // maybe too slow?
        oldQuestionsDb.createFeedbackQuestions(questionsToAdd);
        List<QuestionsDbPersistenceAttributes> questionsToPersist = new ArrayList<>();
        for (FeedbackQuestionAttributes question : questionsToAdd) {
            FeedbackQuestion persistedQuestion = (FeedbackQuestion) oldQuestionsDb.getEntity(question);
            
            FeedbackQuestionAttributes oldQuestionAttributes = new FeedbackQuestionAttributes(persistedQuestion);
            QuestionsDbPersistenceAttributes newQuestionAttributes =
                    new QuestionsDbPersistenceAttributes(oldQuestionAttributes);
            questionsToPersist.add(newQuestionAttributes);
        }
        newQuestionsDb.createEntities(questionsToPersist);
    }
    

    /**
     * Creates question. If the question already exist, simply writes over it instead of failing.
     * @param entityToAdd
     * @throws InvalidParametersException
     */
    public FeedbackQuestionAttributes createFeedbackQuestionWithoutIntegrityCheck(
            EntityAttributes entityToAdd) throws InvalidParametersException {
        FeedbackQuestionAttributes fqa =
                oldQuestionsDb.createFeedbackQuestionWithoutExistenceCheck(entityToAdd);
        return newQuestionsDb.createFeedbackQuestionWithoutExistenceCheck(fqa);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        FeedbackQuestionAttributes oldQuestion = oldQuestionsDb.getFeedbackQuestion(feedbackQuestionId);
        if (oldQuestion != null) {
            FeedbackQuestionAttributes newQuestion = newQuestionsDb.getFeedbackQuestion(
                    oldQuestion.feedbackSessionName, oldQuestion.courseId, oldQuestion.getId());
            if (newQuestion != null) {
                return newQuestion;
            }
        }
        return oldQuestion;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Null if not found.
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(
                String feedbackSessionName, String courseId, int questionNumber) {
        FeedbackQuestionAttributes oldQuestion =
                oldQuestionsDb.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
        if (oldQuestion != null) {
            FeedbackQuestionAttributes newQuestion = newQuestionsDb.getFeedbackQuestion(
                    oldQuestion.feedbackSessionName, oldQuestion.courseId, oldQuestion.getId());
            if (newQuestion != null) {
                return newQuestion;
            }
        }
        return oldQuestion;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(
            String feedbackSessionName, String courseId) {
        List<FeedbackQuestionAttributes> oldQuestions =
                oldQuestionsDb.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        List<FeedbackQuestionAttributes> newQuestions =
                newQuestionsDb.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
        
        return combineQuestionListWithoutDuplicates(oldQuestions, newQuestions);
    }

    private List<FeedbackQuestionAttributes> combineQuestionListWithoutDuplicates(
            List<FeedbackQuestionAttributes> oldQuestions, List<FeedbackQuestionAttributes> newQuestions) {
        Map<String, FeedbackQuestionAttributes> questionsMap = getMapOfIdToQuestion(oldQuestions);
        Map<String, FeedbackQuestionAttributes> newQuestionsMap = getMapOfIdToQuestion(newQuestions);
        
        questionsMap.putAll(newQuestionsMap);
        
        return new ArrayList<>(questionsMap.values());
    }

    private Map<String, FeedbackQuestionAttributes> getMapOfIdToQuestion(
            List<FeedbackQuestionAttributes> questions) {
        Map<String, FeedbackQuestionAttributes> map = new HashMap<>();
        for (FeedbackQuestionAttributes question : questions) {
            map.put(question.getId(), question);
        }
        return map;
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForGiverType(
            String feedbackSessionName, String courseId, FeedbackParticipantType giverType) {
        List<FeedbackQuestionAttributes> oldQuestions =
                oldQuestionsDb.getFeedbackQuestionsForGiverType(feedbackSessionName, courseId, giverType);
        List<FeedbackQuestionAttributes> newQuestions =
                newQuestionsDb.getFeedbackQuestionsForGiverType(feedbackSessionName, courseId, giverType);
        
        return combineQuestionListWithoutDuplicates(oldQuestions, newQuestions);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return An empty list if no such questions are found.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForCourse(String courseId) {
        List<FeedbackQuestionAttributes> oldQuestions =
                oldQuestionsDb.getFeedbackQuestionsForCourse(courseId);
        List<FeedbackQuestionAttributes> newQuestions =
                newQuestionsDb.getFeedbackQuestionsForCourse(courseId);
        
        return combineQuestionListWithoutDuplicates(oldQuestions, newQuestions);
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
    public void updateFeedbackQuestion(FeedbackQuestionAttributes question)
            throws InvalidParametersException, EntityDoesNotExistException {
        oldQuestionsDb.updateFeedbackQuestion(question);
        
        try {
            newQuestionsDb.updateFeedbackQuestion(question);
        } catch (EntityDoesNotExistException e) {
            // can happen on old questions where a copy of new question type does not exist
        }
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
        oldQuestionsDb.updateFeedbackQuestion(question, keepUpdateTimestamp);
        try {
            newQuestionsDb.updateFeedbackQuestion(question, keepUpdateTimestamp);
        } catch (EntityDoesNotExistException e) {
            // can happen on old questions where a copy of new question type does not exist
        }
        
    }

    @Override
    public void deleteEntity(EntityAttributes entityToDelete) {
        oldQuestionsDb.deleteEntity(entityToDelete);
        try {
            newQuestionsDb.deleteQuestion((FeedbackQuestionAttributes) entityToDelete);
        } catch (EntityDoesNotExistException expected) {
            // can happen if entityToDelete was an orphaned question (for the old question type)
            // should be safe to ignore
            log.warning("EntityDoesNotExistException for session during deletion of questions "
                        + expected);
        }
    }
    
    public void deleteFeedbackQuestionsForCourse(String courseId) {
        oldQuestionsDb.deleteFeedbackQuestionsForCourse(courseId);
        newQuestionsDb.deleteFeedbackQuestionsForCourse(courseId);
    }
    
    public void deleteFeedbackQuestionsForCourses(List<String> courseIds) {
        oldQuestionsDb.deleteFeedbackQuestionsForCourses(courseIds);
        newQuestionsDb.deleteFeedbackQuestionsForCourses(courseIds);
    }
    
    @Override
    protected Object getEntity(EntityAttributes attributes) {
        Question question = (Question) newQuestionsDb.getEntity(attributes);
        if (question != null) {
            return question;
        }
        return oldQuestionsDb.getEntity(attributes);
    }

    public void saveQuestionAndAdjustQuestionNumbers(
            FeedbackQuestionAttributes question, boolean isUpdating, int oldQuestionNumber)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        FeedbackQuestionAttributes fqaSaved =
                oldQuestionsDb.saveQuestionAndAdjustQuestionNumbers(question, isUpdating, oldQuestionNumber);
        try {
            newQuestionsDb.saveQuestionAndAdjustQuestionNumbers(fqaSaved, isUpdating, oldQuestionNumber);
        } catch (EntityDoesNotExistException e) {
            // can happen on old questions where a copy of new question type does not exist
        }
    }

    public void adjustQuestionNumbers(int oldQuestionNumber, int newQuestionNumber,
                                      List<FeedbackQuestionAttributes> questions) {
        oldQuestionsDb.adjustQuestionNumbers(oldQuestionNumber, newQuestionNumber, questions);
        newQuestionsDb.adjustQuestionNumbers(oldQuestionNumber, newQuestionNumber, questions);
    }
}
