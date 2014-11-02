package teammates.storage.api;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.Evaluation;

/**
 * Handles CRUD Operations for submission entities.
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
 */
public class EvaluationsDb extends EntitiesDb {

    public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Evaluation: ";
    
    private static final Logger log = Utils.getLogger();

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Null if not found.
     */
    public EvaluationAttributes getEvaluation(String courseId, String name) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, name);

        Evaluation e = getEvaluationEntity(courseId, name);

        if (e == null) {
            return null;
        } else {
            return new EvaluationAttributes(e);
        }
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. 
     * @return Empty list if no matching objects found.
     */
    public List<EvaluationAttributes> getEvaluationsForCourse(String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        
        List<Evaluation> evaluationList = getEvaluationEntitiesForCourse(courseId);

        return EvaluationAttributes.toAttributes(evaluationList);
    }


    /**
     * @return empty list if none found.
     * @deprecated Not scalable. 
     */
    @Deprecated
    public List<EvaluationAttributes> getAllEvaluations() {
        List<Evaluation> allEvaluations = getAllEvaluationEntities();
        return EvaluationAttributes.toAttributes(allEvaluations);
    }


    /**
     * Course ID, evaluation name will not be changed. <br>
     * Does not follow the 'Keep existing' policy. <br>
     * Preconditions: <br> 
     * * The given list is not null and contains valid {@link SubmissionAttributes} objects. <br>
     */
    public void updateEvaluation(EvaluationAttributes newEvaluationAttributes) 
            throws EntityDoesNotExistException, InvalidParametersException {
        
        Assumption.assertNotNull(
                Const.StatusCodes.DBLEVEL_NULL_INPUT, 
                newEvaluationAttributes);
        
        newEvaluationAttributes.sanitizeForSaving();
        
        if (!newEvaluationAttributes.isValid()) {
            throw new InvalidParametersException(newEvaluationAttributes.getInvalidityInfo());
        }
        
        Evaluation e = getEvaluationEntity(newEvaluationAttributes.courseId, newEvaluationAttributes.name);
        
        if (e == null) {
            throw new EntityDoesNotExistException(
                    ERROR_UPDATE_NON_EXISTENT + newEvaluationAttributes.toString());
        }
        
        e.setLongInstructions(newEvaluationAttributes.instructions);
        e.setStart(newEvaluationAttributes.startTime);
        e.setDeadline(newEvaluationAttributes.endTime);
        e.setGracePeriod(newEvaluationAttributes.gracePeriod);
        e.setCommentsEnabled(newEvaluationAttributes.p2pEnabled);
        e.setActivated(newEvaluationAttributes.activated);
        e.setPublished(newEvaluationAttributes.published);
        e.setTimeZone(newEvaluationAttributes.timeZone);
        
        log.info(newEvaluationAttributes.getBackupIdentifier());
        getPM().close();

    }


    /**
     * Note: This is a non-cascade delete.<br>
     * Fails silently if no matching objects. <br>
     * Preconditions: <br> 
     * * all parameters are non-null.
     */
    public void deleteEvaluation(String courseId, String name) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, name);

        Evaluation e = getEvaluationEntity(courseId, name);
        if (e == null) {
            return;
        }

        getPM().deletePersistent(e);
        getPM().flush();

        // wait for the operation to persist.
        if(Config.PERSISTENCE_CHECK_DURATION > 0){
            int elapsedTime = 0;
            Evaluation evaluationCheck = getEvaluationEntity(courseId, name);
            while ((evaluationCheck != null)
                    && (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
                ThreadHelper.waitBriefly();
                evaluationCheck = getEvaluationEntity(courseId, name);
                elapsedTime += ThreadHelper.WAIT_DURATION;
            }
            if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
                log.severe("Operation did not persist in time: deleteEvaluation->"
                        + courseId + "/" + name);
            }
        }
        
        //TODO: use the method in the parent class instead.

    }

    /**
     * Note: This is a non-cascade delete.<br>
     * Fails silently if no matching objects. <br>
     * Preconditions: <br> 
     * * all parameters are non-null.
     */
    public void deleteAllEvaluationsForCourse(String courseId) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);

        List<Evaluation> evaluationList = getEvaluationEntitiesForCourse(courseId);

        getPM().deletePersistentAll(evaluationList);
        getPM().flush();
    }
    
    public void deleteEvaluationsForCourses(List<String> courseIds) {
        
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseIds);
        
        List<Evaluation> evaluationList = getEvaluationEntitiesForCourses(courseIds);
        
        getPM().deletePersistentAll(evaluationList);
        getPM().flush();
    }

    private Evaluation getEvaluationEntity(String courseId, String evaluationName) {
        
        Query q = getPM().newQuery(Evaluation.class);
        q.declareParameters("String courseIdParam, String EvaluationNameParam");
        q.setFilter("name == EvaluationNameParam && courseID == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Evaluation> evaluationList = (List<Evaluation>) q.execute(courseId, evaluationName);

        if (evaluationList.isEmpty()
                || JDOHelper.isDeleted(evaluationList.get(0))) {
            return null;
        }

        return evaluationList.get(0);
    }

    private List<Evaluation> getEvaluationEntitiesForCourse(String courseId) {
        Query q = getPM().newQuery(Evaluation.class);
        q.declareParameters("String courseIdParam");
        q.setFilter("courseID == courseIdParam");
        
        @SuppressWarnings("unchecked")
        List<Evaluation> evaluationList = (List<Evaluation>) q.execute(courseId);
        return evaluationList;
    }
    
    private List<Evaluation> getEvaluationEntitiesForCourses(List<String> courseIds) {
        Query q = getPM().newQuery(Evaluation.class);
        q.setFilter(":p.contains(courseID)");
        
        @SuppressWarnings("unchecked")
        List<Evaluation> evaluationList = (List<Evaluation>) q.execute(courseIds);
        return evaluationList;
    }

    private List<Evaluation> getAllEvaluationEntities() {
        
        Query q = getPM().newQuery(Evaluation.class);

        @SuppressWarnings("unchecked")
        List<Evaluation> evaluationList = (List<Evaluation>) q.execute();

        return evaluationList;
    }

    @Override
    protected Object getEntity(EntityAttributes attributes) {
        EvaluationAttributes evaluationToGet = (EvaluationAttributes) attributes;
        return getEvaluationEntity(evaluationToGet.courseId, evaluationToGet.name);
    }
    
}
