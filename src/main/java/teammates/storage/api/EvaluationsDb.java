package teammates.storage.api;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Evaluation;

/**
 * Handles CRUD Operations for submission entities.
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
 */
public class EvaluationsDb {

	public static final String ERROR_CREATE_EVALUATION_ALREADY_EXISTS = "Trying to create an Evaluation that exists: ";
	public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Evaluation: ";
	
	private static final Logger log = Common.getLogger();

	/**
	 * Preconditions: <br>
	 * * {@code evaluationToAdd} is not null and has valid data.
	 */
	public void createEvaluation(EvaluationAttributes evaluationToAdd)
			throws EntityAlreadyExistsException, InvalidParametersException {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, evaluationToAdd);
		
		if (!evaluationToAdd.isValid()) {
			throw new InvalidParametersException(evaluationToAdd.getInvalidityInfo());
		}
		
		if (getEvaluationEntity(evaluationToAdd.courseId, evaluationToAdd.name) != null) {
			String error = ERROR_CREATE_EVALUATION_ALREADY_EXISTS
					+ evaluationToAdd.courseId + " | " + evaluationToAdd.name;
			log.warning(error);
			throw new EntityAlreadyExistsException(error);
		}

		Evaluation evaluation = evaluationToAdd.toEntity();

		getPM().makePersistent(evaluation);
		getPM().flush();

		// Wait for the operation to persist
		int elapsedTime = 0;
		Evaluation evaluationCheck = getEvaluationEntity(
				evaluationToAdd.courseId, evaluationToAdd.name);
		while ((evaluationCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			evaluationCheck = getEvaluationEntity(evaluationToAdd.courseId,
					evaluationToAdd.name);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createEvaluation->"
					+ evaluationToAdd.courseId + "/" + evaluationToAdd.name);
		}
	}
	

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public EvaluationAttributes getEvaluation(String courseId, String name) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, name);

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
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
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
				Common.ERROR_DBLEVEL_NULL_INPUT, 
				newEvaluationAttributes);
		
		if (!newEvaluationAttributes.isValid()) {
			throw new InvalidParametersException(newEvaluationAttributes.getInvalidityInfo());
		}
		
		Evaluation e = getEvaluationEntity(newEvaluationAttributes.courseId, newEvaluationAttributes.name);
		
		if (e == null) {
			throw new EntityDoesNotExistException(
					ERROR_UPDATE_NON_EXISTENT + newEvaluationAttributes.toString());
		}
		
		e.setInstructions(newEvaluationAttributes.instructions);
		e.setStart(newEvaluationAttributes.startTime);
		e.setDeadline(newEvaluationAttributes.endTime);
		e.setGracePeriod(newEvaluationAttributes.gracePeriod);
		e.setCommentsEnabled(newEvaluationAttributes.p2pEnabled);
		e.setActivated(newEvaluationAttributes.activated);
		e.setPublished(newEvaluationAttributes.published);
		e.setTimeZone(newEvaluationAttributes.timeZone);
		
		getPM().close();

	}


	/**
	 * Note: This is a non-cascade delete.<br>
	 * Fails silently if no matching objects. <br>
	 * Preconditions: <br> 
	 * * all parameters are non-null.
	 */
	public void deleteEvaluation(String courseId, String name) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, name);

		Evaluation e = getEvaluationEntity(courseId, name);
		if (e == null) {
			return;
		}

		getPM().deletePersistent(e);
		getPM().flush();

		// wait for the operation to persist.
		int elapsedTime = 0;
		Evaluation evaluationCheck = getEvaluationEntity(courseId, name);
		while ((evaluationCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			evaluationCheck = getEvaluationEntity(courseId, name);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteEvaluation->"
					+ courseId + "/" + name);
		}

	}

	/**
	 * Note: This is a non-cascade delete.<br>
	 * Fails silently if no matching objects. <br>
	 * Preconditions: <br> 
	 * * all parameters are non-null.
	 */
	public void deleteAllEvaluationsForCourse(String courseId) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);

		List<Evaluation> evaluationList = getEvaluationEntitiesForCourse(courseId);

		getPM().deletePersistentAll(evaluationList);
		getPM().flush();
	}

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
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

	private List<Evaluation> getAllEvaluationEntities() {
		
		Query q = getPM().newQuery(Evaluation.class);

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) q.execute();

		return evaluationList;
	}
	
}
