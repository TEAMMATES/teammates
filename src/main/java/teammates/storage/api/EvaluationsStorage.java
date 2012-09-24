package teammates.storage.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;

public class EvaluationsStorage {
	private static EvaluationsStorage instance = null;
	private static final Logger log = Common.getLogger();

	private static final AccountsDb accountsDb = new AccountsDb();
	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static final SubmissionsDb submissionsDb = new SubmissionsDb();

	public static EvaluationsStorage inst() {
		if (instance == null)
			instance = new EvaluationsStorage();
		return instance;
	}

	/**
	 * Atomically creates an Evaluation Object and a list of Submissions for the
	 * evaluation
	 * 
	 * @param e
	 *            An EvaluationData object
	 * 
	 * @author wangsha
	 * @throws EntityAlreadyExistsException
	 *             , InvalidParametersException
	 */
	public void createEvaluation(EvaluationData e)
			throws EntityAlreadyExistsException, InvalidParametersException {

		// 1st level validation - throw IPE
		if (!e.isValid()) {
			throw new InvalidParametersException(e.getInvalidStateInfo());
		}
		
		// this operation throws EntityAlreadyExistsException
		evaluationsDb.createEvaluation(e);

		// Build submission objects for each student based on their team
		// number
		List<StudentData> studentDataList = accountsDb
				.getStudentListForCourse(e.course);
		
		List<SubmissionData> listOfSubmissionsToAdd = new ArrayList<SubmissionData>();

		// This double loop creates 3 submissions for a pair of students:
		// x->x, x->y, y->x
		for (StudentData sx : studentDataList) {
			for (StudentData sy : studentDataList) {
				if (sx.team.equals(sy.team)) {
					SubmissionData submissionToAdd = new SubmissionData(
							e.course, e.name, sx.team, sx.email, sy.email);
					listOfSubmissionsToAdd.add(submissionToAdd);
				}
			}
		}

		submissionsDb.createListOfSubmissions(listOfSubmissionsToAdd);
	}

	/**
	 * Adjusts submissions for a student moving from one team to another.
	 * Deletes existing submissions for original team and creates empty
	 * submissions for the new team, in all existing submissions, including
	 * CLOSED and PUBLISHED ones.
	 */
	public void adjustSubmissionsForChangingTeam(String courseId,
			String studentEmail, String originalTeam, String newTeam) {
		List<EvaluationData> evaluationDataList = evaluationsDb
				.getEvaluationsForCourse(courseId);
		for (EvaluationData ed : evaluationDataList) {

			submissionsDb.deleteSubmissionsForOutgoingMember(courseId, ed.name,
					studentEmail, originalTeam);

			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail,
					newTeam);
		}
	}

	/**
	 * Adjusts submissions for a student adding a new student to a course.
	 * Creates empty submissions for the new team, in all existing submissions,
	 * including CLOSED and PUBLISHED ones.
	 * 
	 */
	public void adjustSubmissionsForNewStudent(String courseId,
			String studentEmail, String team) {
		List<EvaluationData> evaluationDataList = evaluationsDb
				.getEvaluationsForCourse(courseId);
		for (EvaluationData ed : evaluationDataList) {
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail,
					team);
		}
	}

	private void addSubmissionsForIncomingMember(String courseId,
			String evaluationName, String studentEmail, String newTeam) {

		List<String> students = getExistingStudentsInTeam(courseId, newTeam);

		// add self evaluation and remove self from list
		List<SubmissionData> listOfSubmissionsToAdd = new ArrayList<SubmissionData>();
		
		SubmissionData submissionToAdd = new SubmissionData(courseId,
				evaluationName, newTeam, studentEmail, studentEmail);
		listOfSubmissionsToAdd.add(submissionToAdd);
		students.remove(studentEmail);

		// add submission to/from peers
		for (String peer : students) {

			// To
			submissionToAdd = new SubmissionData(courseId, evaluationName,
					newTeam, peer, studentEmail);
			listOfSubmissionsToAdd.add(submissionToAdd);

			// From
			submissionToAdd = new SubmissionData(courseId, evaluationName,
					newTeam, studentEmail, peer);
			listOfSubmissionsToAdd.add(submissionToAdd);
		}
		
		submissionsDb.createListOfSubmissions(listOfSubmissionsToAdd);
	}

	private List<String> getExistingStudentsInTeam(String courseId, String team) {
		Set<String> students = new HashSet<String>();
		List<SubmissionData> submissionsDataList = submissionsDb
				.getSubmissionsForCourse(courseId);
		for (SubmissionData s : submissionsDataList) {
			if (s.team.equals(team)) {
				students.add(s.reviewer);
			}
		}
		return new ArrayList<String>(students);
	}

	/**
	 * Atomically deletes an Evaluation and its Submission objects.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: The courseID and evaluationName
	 *            pair must be valid)
	 * 
	 * @param name
	 *            the evaluation name (Pre-condition: The courseID and
	 *            evaluationName pair must be valid)
	 * 
	 */
	public void deleteEvaluation(String courseId, String name) {
		// Delete the Evaluation entity
		evaluationsDb.deleteEvaluation(courseId, name);

		// Delete Submission entries belonging to this Evaluation
		submissionsDb.deleteAllSubmissionsForEvaluation(courseId, name);
	}

	/**
	 * Atomically deletes all Evaluation objects and its Submission objects from
	 * a Course.
	 * 
	 * @param courseID
	 *            the course ID (Pre-condition: Must be valid)
	 * 
	 */
	public void deleteEvaluationsForCourse(String courseId) {

		evaluationsDb.deleteAllEvaluationsForCourse(courseId);
		submissionsDb.deleteAllSubmissionsForCourse(courseId);
	}

	/**
	 * Checks if a Student has done his submitted his entry for a particular
	 * Evaluation.
	 * 
	 * @param evaluation
	 *            the evaluation (Pre-condition: The evaluation and email pair
	 *            must be valid)
	 * 
	 * @param email
	 *            the email of the student (Pre-condition: The evaluation and
	 *            email pair must be valid)
	 * 
	 * @return <code>true</code> if the student has submitted the evaluation,
	 *         <code>false</code> otherwise.
	 */
	public boolean isEvaluationSubmitted(EvaluationData evaluation, String email) {
		List<SubmissionData> submissionList = submissionsDb
				.getSubmissionsFromEvaluationFromStudent(evaluation.course,
						evaluation.name, email);

		for (SubmissionData sd : submissionList) {
			// Return false if user has outstanding submissions to any of
			// his/her teammates
			if (sd.points == Common.POINTS_NOT_SUBMITTED) {
				return false;
			}
		}
		return true;
	}

	public boolean isEvaluationExists(String courseId, String evaluationName) {
		return evaluationsDb.getEvaluation(courseId, evaluationName) != null;
	}

	public EvaluationsDb getEvaluationsDb() {
		return evaluationsDb;
	}

	public SubmissionsDb getSubmissionsDb() {
		return submissionsDb;
	}

}
