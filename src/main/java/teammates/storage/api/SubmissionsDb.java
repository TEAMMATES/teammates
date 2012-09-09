package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;

import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Submission;
import teammates.common.Common;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

/**
 * Manager for handling basic CRUD Operations only
 * 
 */
public class SubmissionsDb {

	private static final Logger log = Common.getLogger();

	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}

	/**
	 * Creates new Submission Entities for a particular Evaluation.
	 * 
	 * @param courseId
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 * 
	 * @param teamName
	 * 
	 * @param toStudent
	 * 
	 * @param fromStudent
	 * 
	 */
	public void createSubmission(SubmissionData submissionToAdd) {

		if (getSubmissionEntity(submissionToAdd.course,
				submissionToAdd.evaluation, submissionToAdd.reviewee,
				submissionToAdd.reviewer) != null) {
			log.warning("Trying to create a Submission that exists: "
					+ "course: " + submissionToAdd.course + ", evaluation: "
					+ submissionToAdd.evaluation + ", toStudent: "
					+ submissionToAdd.reviewee + ", fromStudent: "
					+ submissionToAdd.reviewer
					+ Common.printCurrentThreadStack());

			// Exception not handled yet
			// throw new EntityAlreadyExistsException("The submission for " +
			// "course: " + submissionToAdd.course +
			// ", evaluation: " + submissionToAdd.evaluation +
			// ", toStudent: " + submissionToAdd.reviewee +
			// ", fromStudent: " + submissionToAdd.reviewer);
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

		Submission s = getSubmissionEntity(courseId, evaluationName, toStudent,
				fromStudent);

		if (s == null) {
			log.warning("Trying to get non-existent Submission : " + courseId
					+ "/" + evaluationName + "| from " + fromStudent + " to "
					+ toStudent + Common.printCurrentThreadStack());
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
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param evaluationName
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 * 
	 * @return List<SubmissionData>
	 */
	public List<SubmissionData> getSubmissionsForEvaluation(String courseID,
			String evaluationName) {

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
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
	 * @param courseID
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
			String courseID, String evaluationName, String toStudent) {
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
				+ "' && evaluationName == '" + evaluationName
				+ "' && toStudent == '" + toStudent + "'";

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
	 * @param courseID
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
			String courseID, String evaluationName, String reviewerEmail) {

		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseID
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

		Submission submission = getSubmissionEntity(sd.course, sd.evaluation,
				sd.reviewee, sd.reviewer);

		if (submission == null) {
			log.severe("Trying to update non-existent Submission: " + sd.course
					+ "/" + sd.evaluation + "| from " + sd.reviewer + " to "
					+ sd.reviewee + Common.printCurrentThreadStack());
			/*
			 * Assumption.assertFail("Trying to update non-existent Submission: "
			 * + "course: " + sd.course + ", evaluation: " + sd.evaluation +
			 * ", toStudent: " + sd.reviewee + ", fromStudent: " + sd.reviewer);
			 */
		}

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
		String query = "select from " + Submission.class.getName()
				+ " where courseID == '" + courseId
				+ "' && evaluationName == '" + evaluationName + "'";

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
