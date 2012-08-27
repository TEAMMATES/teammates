package teammates.storage.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Evaluation;
import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;


/**
 * Manager for handling basic CRUD Operations only
 *
 */
public class EvaluationsDb {
	
	private static final Logger log = Common.getLogger();
	
	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}
	

	
	
	
	/**
	 * CREATE Evaluation
	 * 
	 * Adds an evaluation to the specified course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: Must not be null)
	 * 
	 * @param instructions
	 *            the instructions for the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @param commentsEnabled
	 *            if students are allowed to make comments (Pre-condition: Must
	 *            not be null)
	 * 
	 * @param start
	 *            the start date/time of the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @param deadline
	 *            the deadline of the evaluation (Pre-condition: Must not be
	 *            null)
	 * 
	 * @param gracePeriod
	 *            the amount of time after the deadline within which submissions
	 *            will still be accepted (Pre-condition: Must not be null)
	 * 
	 * @throws EntityAlreadyExistsException, InvalidParametersException
	 */

	public void createEvaluation(EvaluationData e) throws EntityAlreadyExistsException, InvalidParametersException {
		
		if (getEvaluationEntity(e.course, e.name) != null) {
			throw new EntityAlreadyExistsException("An evaluation by the name "
					+ e.name + " already exists under course " + e.course);
		}
		
		try {
			e.validate();
		} catch (InvalidParametersException ipe) {
			throw ipe;
		}

		Evaluation evaluation = e.toEvaluation();

		getPM().makePersistent(evaluation);
		getPM().flush();

		// Check insert operation persisted
		int elapsedTime = 0;
		Evaluation evaluationCheck = getEvaluationEntity(e.course, e.name);
		while ((evaluationCheck == null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			evaluationCheck = getEvaluationEntity(e.course, e.name);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createEvaluation->"
					+ e.course + "/" + e.name);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * RETRIEVE Evaluation
	 * 
	 * Returns an EvaluationData object.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: Must not be null)
	 * 
	 * @return the EvaluationData of the specified course and name
	 */
	public EvaluationData getEvaluation(String courseId, String name) {
		
		Evaluation e = getEvaluationEntity(courseId, name);

		return e == null ? null : new EvaluationData(e);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * RETRIEVE List<Evaluation>
	 * 
	 * Returns the Evaluation objects belonging to a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must not be null)
	 * 
	 * @return the list of evaluations belonging to the specified course
	 */
	public List<EvaluationData> getEvaluationsForCourse(String courseId) {
		String query = "select from " + Evaluation.class.getName()
				+ " where courseID == '" + courseId + "'";

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();
		
		List<EvaluationData> evaluationDataList = new ArrayList<EvaluationData>();
		
		for (Evaluation e : evaluationList) {
			evaluationDataList.add(new EvaluationData(e));
		}
		
		return evaluationDataList;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * UPDATE Evaluation
	 * 
	 * Edits an Evaluation object with the new values and returns true if there
	 * are changes, false otherwise.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 * 
	 * @param newInstructions
	 *            new instructions for the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @param newCommentsEnabled
	 *            new status for comments (Pre-condition: Must not be null)
	 * 
	 * @param newStart
	 *            new start date for the evaluation (Pre-condition: Must not be
	 *            null)
	 * 
	 * @param newDeadline
	 *            new deadline for the evaluation (Pre-condition: Must not be
	 *            null)
	 * 
	 * @param newGracePeriod
	 *            new grace period for the evaluation (Pre-condition: Must not
	 *            be null)
	 * 
	 * @return <code>true</code> if there are changes, <code>false</code>
	 *         otherwise
	 */
	public boolean editEvaluation(String courseID, String name,
			String newInstructions, boolean newCommentsEnabled, Date newStart,
			Date newDeadline, int newGracePeriod, boolean newIsActive,
			boolean newIsPublished, double newTimeZone)
			throws EntityDoesNotExistException {
		Evaluation evaluation = getEvaluationEntity(courseID, name);

		Transaction tx = getPM().currentTransaction();
		try {
			tx.begin();

			evaluation.setInstructions(newInstructions);
			evaluation.setStart(newStart);
			evaluation.setDeadline(newDeadline);
			evaluation.setGracePeriod(newGracePeriod);
			evaluation.setCommentsEnabled(newCommentsEnabled);
			evaluation.setActivated(newIsActive);
			evaluation.setPublished(newIsPublished);
			evaluation.setTimeZone(newTimeZone);

			getPM().flush();

			tx.commit();
		} finally {
			if (tx.isActive()) {
				tx.rollback();
				return false;
			}
		}
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * UPDATE Evaluation
	 * 
	 * Publishes an Evaluation.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and name pair must
	 *            be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and name pair
	 *            must be valid)
	 */
	public void setEvaluationPublishedStatus(String courseId, String name, boolean status) {
		Evaluation evaluation = getEvaluationEntity(courseId, name);

		evaluation.setPublished(status);
		getPM().close();
		return;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Deletes an Evaluation
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 */
	public void deleteEvaluation(String courseId, String name) {
		
		Evaluation e = getEvaluationEntity(courseId, name);
		
		if (e == null) {
			String errorMessage = "Trying to delete non-existent evaluation : "
					+ courseId + "/" + name;
			log.warning(errorMessage);
		} else {
			getPM().deletePersistent(e);
		}

		// Check delete operation persisteed
		int elapsedTime = 0;
		Evaluation evaluationCheck = getEvaluationEntity(courseId, name);
		while ((evaluationCheck != null) && (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
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
	 * Deletes all Evaluations belonging to the specified course ID
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 */
	public void deleteAllEvaluationsForCourse(String courseId) {
		
		String query = "select from " + Evaluation.class.getName()
				+ " where courseID == '" + courseId + "'";

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();
		
		getPM().deletePersistentAll(evaluationList);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Returns the actual Evaluation Entity
	 * 
	 * @param courseID
	 *            the course ID (Precondition: Must not be null)
	 * 
	 * @param email
	 *            the email of the student (Precondition: Must not be null)
	 * 
	 * @return the student who has the specified email in the specified course
	 */
	private Evaluation getEvaluationEntity(String courseId, String evalName) {
		String query = "select from " + Evaluation.class.getName()
				+ " where name == '" + evalName + "' && courseID == '" + courseId
				+ "'";
		
		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();

		if (evaluationList.isEmpty()) {
			log.fine("Trying to get non-existent Evaluation : " + courseId
					+ "/" + evalName);
			return null;
		}

		return evaluationList.get(0);
	}
	
	
	
}
