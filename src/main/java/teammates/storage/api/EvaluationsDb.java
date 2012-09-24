package teammates.storage.api;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Evaluation;
import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.exception.EntityAlreadyExistsException;

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
	 * @throws EntityAlreadyExistsException
	 * 
	 */

	public void createEvaluation(EvaluationData evaluationToAdd)
			throws EntityAlreadyExistsException {

		Assumption.assertTrue(evaluationToAdd.getInvalidStateInfo(),
				evaluationToAdd.isValid());
		
		if (getEvaluationEntity(evaluationToAdd.course, evaluationToAdd.name) != null) {
			String error = "Trying to create an Evaluation that exists: "
					+ evaluationToAdd.course + " | " + evaluationToAdd.name;

			log.warning(error + "\n" + Common.getCurrentThreadStack());

			throw new EntityAlreadyExistsException(error);
		}

		Evaluation evaluation = evaluationToAdd.toEntity();

		getPM().makePersistent(evaluation);
		getPM().flush();

		// Check insert operation persisted
		int elapsedTime = 0;
		Evaluation evaluationCheck = getEvaluationEntity(
				evaluationToAdd.course, evaluationToAdd.name);
		while ((evaluationCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			evaluationCheck = getEvaluationEntity(evaluationToAdd.course,
					evaluationToAdd.name);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createEvaluation->"
					+ evaluationToAdd.course + "/" + evaluationToAdd.name);
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

		if (e == null) {
			log.warning("Trying to get non-existent Evaluation : " + courseId
					+ "/" + name + Common.getCurrentThreadStack());
			return null;
		}

		return new EvaluationData(e);
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
			if (!JDOHelper.isDeleted(e)) {
				evaluationDataList.add(new EvaluationData(e));
			}
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
		// The readiness must be evaluated from a Calendar instance, not able to
		// select at query time
		List<Evaluation> evaluationList = getAllEvaluations();
		List<EvaluationData> readyEvaluations = new ArrayList<EvaluationData>();

		for (Evaluation e : evaluationList) {
			if (!JDOHelper.isDeleted(e) && e.isReady()) {
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
	public List<EvaluationData> getEvaluationsClosingWithinTimeLimit(int hours) {
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
					&& (differenceBetweenDeadlineAndNow >= hours - 1 && differenceBetweenDeadlineAndNow < hours)) {
				dueEvaluationList.add(e);
			}

			now.add(Calendar.MILLISECOND,
					(int) (-60 * 60 * 1000 * e.getTimeZone()));
		}

		List<EvaluationData> evalDataList = new ArrayList<EvaluationData>();

		for (Evaluation e : dueEvaluationList) {
			if (!JDOHelper.isDeleted(e)) {
				evalDataList.add(new EvaluationData(e));
			}
		}

		return evalDataList;
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
	public boolean editEvaluation(String courseId, String name,
			String newInstructions, boolean newCommentsEnabled, Date newStart,
			Date newDeadline, int newGracePeriod, boolean newIsActive,
			boolean newIsPublished, double newTimeZone) {

		Evaluation evaluation = getEvaluationEntity(courseId, name);

		Assumption.assertNotNull("Trying to update non-existent Evaluation: "
				+ courseId + " | " + name + Common.getCurrentThreadStack(),
				evaluation);

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
	 */
	public boolean editEvaluation(EvaluationData ed) {

		return editEvaluation(ed.course, ed.name, ed.instructions,
				ed.p2pEnabled, ed.startTime, ed.endTime, ed.gracePeriod,
				ed.activated, ed.published, ed.timeZone);

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
	public void setEvaluationPublishedStatus(String courseId, String name,
			boolean status) {

		Evaluation evaluation = getEvaluationEntity(courseId, name);

		Assumption.assertNotNull("Trying to update non-existent Evaluation: "
				+ courseId + " | " + name + Common.getCurrentThreadStack(),
				evaluation);

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
			return;
		}

		getPM().deletePersistent(e);

		// Check delete operation persisteed
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
				+ " where name == '" + evalName + "' && courseID == '"
				+ courseId + "'";

		@SuppressWarnings("unchecked")
		List<Evaluation> evaluationList = (List<Evaluation>) getPM().newQuery(
				query).execute();

		if (evaluationList.isEmpty()
				|| JDOHelper.isDeleted(evaluationList.get(0))) {
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

		List<Evaluation> cleanList = new ArrayList<Evaluation>();

		for (Evaluation e : evaluationList) {
			if (!JDOHelper.isDeleted(e)) {
				cleanList.add(e);
			}
		}

		return cleanList;
	}

}
