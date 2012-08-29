package teammates.storage.api;

import java.util.ArrayList;
import java.util.Calendar;
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
	 * RETRIEVE List<Evaluation>
	 * 
	 * Returns the EvaluationData that are ready.
	 * 
	 * @param courseID
	 *            
	 * 
	 * @return List<EvaluationData> of ready evaluations
	 */
	public List<EvaluationData> getReadyEvaluations() {
		// TODO: very inefficient to go through all evaluations
		// There doesn't seem to be another alternative.
		// The readiness must be evaluated from a Calendar instance, not able to select at query time
		List<Evaluation> evaluationList = getAllEvaluations();
		List<EvaluationData> readyEvaluations = new ArrayList<EvaluationData>();

		for (Evaluation e : evaluationList) {
			if (e.isReady()) {
				readyEvaluations.add(new EvaluationData(e));
			}
		}
		return readyEvaluations;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Returns all Evaluation objects that are due in the specified number of
	 * hours.
	 * 
	 * @param hours
	 *            the number of hours in which the evaluations are due
	 * 
	 * @return the list of all existing evaluations
	 */
	public List<Evaluation> getEvaluationsClosingWithinTimeLimit(int hours) {
		String query = "select from " + Evaluation.class.getName();

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();
		Calendar now = Calendar.getInstance();
		Calendar start = Calendar.getInstance();
		Calendar deadline = Calendar.getInstance();

		long nowMillis;
		long deadlineMillis;

		long differenceBetweenDeadlineAndNow;

		List<Evaluation> dueEvaluationList = new ArrayList<Evaluation>();

		for (Evaluation e : evaluationList) {
			// Fix the time zone accordingly
			now.add(Calendar.MILLISECOND,
					(int) (60 * 60 * 1000 * e.getTimeZone()));
			start.setTime(e.getStart());
			deadline.setTime(e.getDeadline());

			nowMillis = now.getTimeInMillis();
			deadlineMillis = deadline.getTimeInMillis();

			differenceBetweenDeadlineAndNow = (deadlineMillis - nowMillis)
					/ (60 * 60 * 1000);

			// If now and start are almost similar, it means the evaluation is
			// open
			// for only 24 hours
			// hence we do not send a reminder e-mail for the evaluation
			if (now.after(start)
					&& (differenceBetweenDeadlineAndNow >= hours - 1 && differenceBetweenDeadlineAndNow < hours))

			{
				dueEvaluationList.add(e);
			}

			now.add(Calendar.MILLISECOND,
					(int) (-60 * 60 * 1000 * e.getTimeZone()));
		}

		return dueEvaluationList;
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
	public boolean editEvaluation(	String courseId, 
									String name,
									String newInstructions, 
									boolean newCommentsEnabled, 
									Date newStart,
									Date newDeadline, 
									int newGracePeriod, 
									boolean newIsActive,
									boolean newIsPublished, 
									double newTimeZone)
			throws EntityDoesNotExistException {
		
		Evaluation evaluation = getEvaluationEntity(courseId, name);
		
		if (evaluation == null)
			throw new EntityDoesNotExistException("Trying to edit non-existent evaluation:" + courseId + " " + name);

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
	 * Edits an Evaluation object with the new values and returns true if there
	 * are changes, false otherwise.
	 * 
	 * @param EvaluationData
	 * 
	 * @return <code>true</code> if there are changes, <code>false</code>
	 *         otherwise
	 *         
	 * @throws EntityDoesNotExistException
	 */
	public boolean editEvaluation(EvaluationData ed) throws EntityDoesNotExistException {
		
		try {
			return editEvaluation(	ed.course,
									ed.name,
									ed.instructions,
									ed.p2pEnabled,
									ed.startTime,
									ed.endTime,
									ed.gracePeriod,
									ed.activated,
									ed.published,
									ed.timeZone);
			
		} catch (EntityDoesNotExistException ednee) {
			throw ednee;
		}
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
	
	
	
	/**
	 * Returns all Evaluation Entities.
	 * 
	 * @return the list of all Evaluations
	 */
	private List<Evaluation> getAllEvaluations() {
		String query = "select from " + Evaluation.class.getName();

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();

		return evaluationList;
	}
	
	
}
