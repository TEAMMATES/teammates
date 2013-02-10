package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Text;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Submission;
import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;

/**
 * Manager for handling basic CRUD Operations only
 * 
 */
public class SubmissionsDb {

	public static final String ERROR_CREATE_SUBMISSION_ALREADY_EXISTS = "Trying to create a Submission that exists: ";
	public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Submission: ";
	
	private static final Logger log = Common.getLogger();

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	/**
	 * Creates new Submission Entities for a particular Evaluation.
	 * 
	 * @throws EntityAlreadyExistsException 
	 * 
	 */
	public void createSubmission(SubmissionData submissionToAdd) throws EntityAlreadyExistsException {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, submissionToAdd);
		
		Assumption.assertTrue(submissionToAdd.getInvalidStateInfo(), submissionToAdd.isValid());
		
		if (getSubmissionEntity(submissionToAdd.course,
				submissionToAdd.evaluation, submissionToAdd.reviewee,
				submissionToAdd.reviewer) != null) {
			String error = ERROR_CREATE_SUBMISSION_ALREADY_EXISTS
					+ "course: " + submissionToAdd.course + ", evaluation: "
					+ submissionToAdd.evaluation + ", toStudent: "
					+ submissionToAdd.reviewee + ", fromStudent: "
					+ submissionToAdd.reviewer;
			
			log.warning(error);

			throw new EntityAlreadyExistsException(error);
		}

		Submission newSubmission = submissionToAdd.toEntity();

		try {
			getPM().makePersistent(newSubmission);
			getPM().flush();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

		// check if insert operation persisted
		int elapsedTime = 0;
		Submission submissionCheck = getSubmissionEntity(
				submissionToAdd.course, submissionToAdd.evaluation,
				submissionToAdd.reviewee, submissionToAdd.reviewer);
		while ((submissionCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			submissionCheck = getSubmissionEntity(submissionToAdd.course,
					submissionToAdd.evaluation, submissionToAdd.reviewee,
					submissionToAdd.reviewer);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createSubmission->"
					+ submissionToAdd.course + "/" + submissionToAdd.evaluation
					+ " | to: " + submissionToAdd.reviewee + " | from: "
					+ submissionToAdd.reviewer);
		}
	}
	
	/**
	 * CREATE List<Submission>
	 * 
	 * Creates a List of submissions
	 * 
	 * Use this method to persist list of submissions much faster
	 * 
	 * @param List<SubmissionData>
	 * 
	 */
	public void createListOfSubmissions(List<SubmissionData> newList) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, newList);
		
		List<Submission> newEntityList = new ArrayList<Submission>();
		
		for (SubmissionData sd : newList) {
			Assumption.assertTrue(sd.getInvalidStateInfo(), sd.isValid());
			newEntityList.add(sd.toEntity());
		}
		
		getPM().makePersistentAll(newEntityList);
		getPM().flush();
	}

	/**
	 * RETRIEVE Submission
	 * 
	 * Returns a specific SubmissionData object with the supplied params
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The parameters must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The parameters must be
	 *            valid)
	 * 
	 * @param teamName
	 *            the team name (Pre-condition: The parameters must be valid)
	 * 
	 * @param toStudent
	 *            the email of the target student (Pre-condition: The parameters
	 *            must be valid)
	 * 
	 * @param fromStudent
	 *            the email of the sending student (Pre-condition: The
	 *            parameters must be valid)
	 * 
	 * @return the submission entry of the specified fromStudent to the
	 *         specified toStudent
	 */
	public SubmissionData getSubmission(String courseId, String evaluationName,
			String toStudent, String fromStudent) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, evaluationName);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, toStudent);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, fromStudent);

		Submission s = getSubmissionEntity(courseId, evaluationName, toStudent,
				fromStudent);

		if (s == null) {
			log.warning("Trying to get non-existent Submission : " + courseId
					+ "/" + evaluationName + "| from " + fromStudent + " to "
					+ toStudent);
			return null;
		}
		return new SubmissionData(s);
	}

	/**
	 * RETRIEVE List<Submission>
	 * 
	 * Returns all submissions in a course
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @return the submissions pertaining to the specified course
	 */
	public List<SubmissionData> getSubmissionsForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();

		for (Submission s : submissionList) {
			if (!JDOHelper.isDeleted(s)) {
				submissionDataList.add(new SubmissionData(s));
			}
		}

		return submissionDataList;
	}

	/**
	 * RETRIEVE List<Submission>
	 * 
	 * Returns the Submissions from an Evaluation object.
	 * 
	 * @param courseId
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 * 
	 * @return List<SubmissionData>
	 */
	public List<SubmissionData> getSubmissionsForEvaluation(String courseId,
			String evaluationName) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, evaluationName);

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId
				+ "' && evaluationName == '" + evaluationName + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();
		List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();

		for (Submission s : submissionList) {
			if (!JDOHelper.isDeleted(s)) {
				submissionDataList.add(new SubmissionData(s));
			}
		}

		return submissionDataList;
	}

	/**
	 * RETRIEVE List<Submission>
	 * 
	 * Returns the Submissions of an Evaluation directed at a Student.
	 * 
	 * @param courseId
	 *            the course ID (Pre-condition: The parameters must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The parameters must be
	 *            valid)
	 * 
	 * @param toStudent
	 *            the email of the target student (Pre-condition: The parameters
	 *            must be valid)
	 * 
	 * @return the submissions to the target student
	 */
	public List<SubmissionData> getSubmissionsFromEvaluationToStudent(
			String courseId, String evaluationName, String revieweeEmail) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, evaluationName);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, revieweeEmail);
		
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId
				+ "' && evaluationName == '" + evaluationName
				+ "' && toStudent == '" + revieweeEmail + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();

		for (Submission s : submissionList) {
			submissionDataList.add(new SubmissionData(s));
		}
		return submissionDataList;
	}

	/**
	 * Returns the Submission of an Evaluation from a specific Student.
	 * 
	 * @param courseId
	 *            the course ID (Pre-condition: The parameters must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The parameters must be
	 *            valid)
	 * 
	 * @param reviewerEmail
	 *            the email of the sending student (Pre-condition: The
	 *            parameters must be valid)
	 * 
	 * @return the submissions of the specified student pertaining to the
	 *         specified evaluation
	 */
	public List<SubmissionData> getSubmissionsFromEvaluationFromStudent(
			String courseId, String evaluationName, String reviewerEmail) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, evaluationName);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, reviewerEmail);

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId
				+ "' && evaluationName == '" + evaluationName
				+ "' && fromStudent == '" + reviewerEmail + "'";

		log.info(query);
		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		List<SubmissionData> submissionDataList = new ArrayList<SubmissionData>();

		for (Submission s : submissionList) {
			submissionDataList.add(new SubmissionData(s));
		}
		return submissionDataList;
	}

	/**
	 * UPDATE List<Submission>
	 * 
	 * Update the email address of Submission objects from a particular course
	 * when a student changes his email
	 * 
	 * @param email
	 *            the email of the student (Pre-condition: The courseID and
	 *            email pair must be valid)
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and email pair must
	 *            be valid)
	 * 
	 * @param newEmail
	 *            the new email of the student (Pre-condition: Must not be null)
	 */
	public void editStudentEmailForSubmissionsInCourse(String courseId,
			String email, String newEmail) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, email);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, newEmail);

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		for (Submission s : submissionList) {
			// From student is changing email
			if (s.getFromStudent().equals(email)) {
				s.setFromStudent(newEmail);
			}
			// To student is changing email
			if (s.getToStudent().equals(email)) {
				s.setToStudent(newEmail);
			}
		}
		getPM().close();
	}

	/**
	 * UPDATE Submission
	 * 
	 * Edits a single Submission Entity.
	 * 
	 * @param A
	 *            SubmissionData copied from a Submission Entity, containing
	 *            modified values.
	 * 
	 */
	public void editSubmission(SubmissionData sd) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, sd);

		Submission submission = getSubmissionEntity(sd.course, sd.evaluation,
				sd.reviewee, sd.reviewer);

		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT + sd.course
					+ "/" + sd.evaluation + "| from " + sd.reviewer + " to "
					+ sd.reviewee + Common.getCurrentThreadStack(), submission);

		submission.setPoints(sd.points);
		submission.setJustification(sd.justification);
		submission.setCommentsToStudent(sd.p2pFeedback);

		// closing PM because otherwise the data is not updated during offline
		// unit testing
		getPM().close();

	}

	/**
	 * UPDATE List<Submission>
	 * 
	 * Edits a list of Submission objects.
	 * 
	 * @param submissionList
	 *            the list of submissions to be edited (Pre-condition: The
	 *            submission list must be valid)
	 * 
	 */
	public void editSubmissions(List<SubmissionData> submissionDataList) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, submissionDataList);

		for (SubmissionData sd : submissionDataList) {
			editSubmission(sd);
		}
		// closing PM because otherwise the data is not updated during offline
		// unit testing
		getPM().close();

	}

	/**
	 * DELETE List<Submission>
	 * 
	 * Deletes all submissions in a Course
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 */
	public void deleteAllSubmissionsForCourse(String courseId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		getPM().deletePersistentAll(submissionList);
		getPM().flush();

		return;
	}

	/**
	 * DELETE List<Submission>
	 * 
	 * Deletes all submissions in a Evaluation
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 */
	public void deleteAllSubmissionsForEvaluation(String courseId,
			String evaluationName) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, evaluationName);
		
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId
				+ "' && evaluationName == '" + evaluationName + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		getPM().deletePersistentAll(submissionList);
		getPM().flush();
	}

	/**
	 * DELETE List<Submission>
	 * 
	 * Deletes all submissions related to a student (to and from)
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @param studentEmail
	 *            used to identify the student pending deletion
	 * 
	 */
	public void deleteAllSubmissionsForStudent(String courseId,
			String studentEmail) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, studentEmail);
		
		String query1 = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId + "' && toStudent=='"
				+ studentEmail + "'";
		@SuppressWarnings("unchecked")
		List<Submission> submissionList1 = (List<Submission>) getPM().newQuery(
				query1).execute();
		getPM().deletePersistentAll(submissionList1);

		String query2 = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId + "' && fromStudent=='"
				+ studentEmail + "'";
		@SuppressWarnings("unchecked")
		List<Submission> submissionList2 = (List<Submission>) getPM().newQuery(
				query2).execute();
		getPM().deletePersistentAll(submissionList2);
		getPM().flush();
	}

	/**
	 * DELETE List<Submission>
	 * 
	 * Deletes all submissions in a Evaluation related to a Student
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 * @param evaluationName
	 * 
	 * @param studentEmail
	 *            Used to identify the student
	 * 
	 * @param originalTeam
	 *            Used to identify the student's team before move to delete old
	 *            submissions
	 * 
	 */
	public void deleteSubmissionsForOutgoingMember(String courseId,
			String evaluationName, String studentEmail, String originalTeam) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, evaluationName);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, studentEmail);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, originalTeam);

		// Google App Engine prevents OR filter on multiple properties
		// Only alternative -> split the query. (zzzz)
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId
				+ "' && evaluationName == '" + evaluationName
				+ "' && toStudent == '" + studentEmail + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionListTo = (List<Submission>) getPM()
				.newQuery(query).execute();

		for (Submission s : submissionListTo) {
			if (s.getTeamName().equals(originalTeam)) {
				getPM().deletePersistent(s);
			} else {
				log.severe("Unexpected submission found when deleting outgoing submissions for "
						+ s.toString());
			}
		}

		// Merging the list will probably be less efficient

		query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId
				+ "' && evaluationName == '" + evaluationName
				+ "' && fromStudent == '" + studentEmail + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionListFrom = (List<Submission>) getPM()
				.newQuery(query).execute();

		for (Submission s : submissionListFrom) {
			if (s.getTeamName().equals(originalTeam)) {
				getPM().deletePersistent(s);
			} else {
				log.severe("Unexpected submission found when deleting outgoing submissions for "
						+ s.toString());
			}
		}
		
		getPM().flush();
	}

	/**
	 * Returns the actual Submission Entity
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The parameters must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The parameters must be
	 *            valid)
	 * 
	 * @param teamName
	 *            the team name (Pre-condition: The parameters must be valid)
	 * 
	 * @param toStudent
	 *            the email of the target student (Pre-condition: The parameters
	 *            must be valid)
	 * 
	 * @param fromStudent
	 *            the email of the sending student (Pre-condition: The
	 *            parameters must be valid)
	 * 
	 * @return the submission entry of the specified fromStudent to the
	 *         specified toStudent
	 */
	private Submission getSubmissionEntity(String courseId,
			String evaluationName, String toStudent, String fromStudent) {

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId + "'"
				+ "&& evaluationName == '" + evaluationName + "'"
				+ "&& fromStudent == '" + fromStudent + "'"
				+ "&& toStudent == '" + toStudent + "'";

		@SuppressWarnings("unchecked")
		List<Submission> submissionList = (List<Submission>) getPM().newQuery(
				query).execute();

		if (submissionList.isEmpty()
				|| JDOHelper.isDeleted(submissionList.get(0))) {
			return null;
		}

		return submissionList.get(0);
	}

}
