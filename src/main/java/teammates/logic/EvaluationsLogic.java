package teammates.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.Text;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.SubmissionsDb;

public class EvaluationsLogic {
	private static EvaluationsLogic instance = null;
	private static final Logger log = Common.getLogger();

	private static final AccountsDb accountsDb = new AccountsDb();
	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static final SubmissionsDb submissionsDb = new SubmissionsDb();

	public static EvaluationsLogic inst() {
		if (instance == null)
			instance = new EvaluationsLogic();
		return instance;
	}

	//==========================================================================
	/**
	 * Adjusts submissions for a student moving from one team to another.
	 * Deletes existing submissions for original team and creates empty
	 * submissions for the new team, in all existing submissions, including
	 * CLOSED and PUBLISHED ones.
	 */
	public void adjustSubmissionsForChangingTeam(String courseId,
			String studentEmail, String originalTeam, String newTeam) {
		List<EvaluationAttributes> evaluationDataList = evaluationsDb
				.getEvaluationsForCourse(courseId);
		for (EvaluationAttributes ed : evaluationDataList) {

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
		List<EvaluationAttributes> evaluationDataList = evaluationsDb
				.getEvaluationsForCourse(courseId);
		for (EvaluationAttributes ed : evaluationDataList) {
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail,
					team);
		}
	}

	private void addSubmissionsForIncomingMember(String courseId,
			String evaluationName, String studentEmail, String newTeam) {

		List<String> students = getExistingStudentsInTeam(courseId, newTeam);

		// add self evaluation and remove self from list
		List<SubmissionAttributes> listOfSubmissionsToAdd = new ArrayList<SubmissionAttributes>();
		
		SubmissionAttributes submissionToAdd = new SubmissionAttributes(courseId,
				evaluationName, newTeam, studentEmail, studentEmail);
		submissionToAdd.p2pFeedback = new Text("");
		submissionToAdd.justification = new Text("");
		listOfSubmissionsToAdd.add(submissionToAdd);
		students.remove(studentEmail);

		// add submission to/from peers
		for (String peer : students) {

			// To
			submissionToAdd = new SubmissionAttributes(courseId, evaluationName,
					newTeam, peer, studentEmail);
			submissionToAdd.p2pFeedback = new Text("");
			submissionToAdd.justification = new Text("");
			listOfSubmissionsToAdd.add(submissionToAdd);

			// From
			submissionToAdd = new SubmissionAttributes(courseId, evaluationName,
					newTeam, studentEmail, peer);
			submissionToAdd.p2pFeedback = new Text("");
			submissionToAdd.justification = new Text("");
			listOfSubmissionsToAdd.add(submissionToAdd);
		}
		
		submissionsDb.createListOfSubmissions(listOfSubmissionsToAdd);
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
	public boolean isEvaluationSubmitted(EvaluationAttributes evaluation, String email) {
		List<SubmissionAttributes> submissionList = submissionsDb
				.getSubmissionsFromEvaluationFromStudent(evaluation.course,
						evaluation.name, email);

		for (SubmissionAttributes sd : submissionList) {
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
	
	//==========================================================================
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
	public void createEvaluation(EvaluationAttributes e)
			throws EntityAlreadyExistsException, InvalidParametersException {

		// 1st level validation - throw IPE
		if (!e.isValid()) {
			throw new InvalidParametersException(e.getInvalidStateInfo());
		}
		
		// this operation throws EntityAlreadyExistsException
		evaluationsDb.createEvaluation(e);

		// Build submission objects for each student based on their team
		// number
		List<StudentAttributes> studentDataList = accountsDb.getStudentsForCourse(e.course);
		
		List<SubmissionAttributes> listOfSubmissionsToAdd = new ArrayList<SubmissionAttributes>();

		// This double loop creates 3 submissions for a pair of students:
		// x->x, x->y, y->x
		for (StudentAttributes sx : studentDataList) {
			for (StudentAttributes sy : studentDataList) {
				if (sx.team.equals(sy.team)) {
					SubmissionAttributes submissionToAdd = new SubmissionAttributes(
							e.course, e.name, sx.team, sx.email, sy.email);
					submissionToAdd.p2pFeedback = new Text("");
					submissionToAdd.justification = new Text("");
					listOfSubmissionsToAdd.add(submissionToAdd);
				}
			}
		}

		submissionsDb.createListOfSubmissions(listOfSubmissionsToAdd);
	}
	
	//==========================================================================
	public EvaluationAttributes getEvaluation(String courseId, String evaluationName) {
		return evaluationsDb.getEvaluation(courseId, evaluationName);
	}
	
	public List<EvaluationAttributes> getEvaluationsForCourse(String courseId) {
		return evaluationsDb.getEvaluationsForCourse(courseId);
	}
	
	// Used in BackdoorLogic
	public List<EvaluationAttributes> getReadyEvaluations() {
		return evaluationsDb.getReadyEvaluations();
	}

	// Used in BackdoorLogic
	public List<EvaluationAttributes> getEvaluationsClosingWithinTimeLimit(int hoursWithinLimit) {
		return evaluationsDb.getEvaluationsClosingWithinTimeLimit(hoursWithinLimit);
	}

	public SubmissionAttributes getSubmission(String course, String evaluation, String reviewee, String reviewer) {
		return submissionsDb.getSubmission(course, evaluation, reviewee, reviewer);
	}
	
	public List<SubmissionAttributes> getSubmissionsFromEvaluationFromStudent(String courseId, String evaluationName, String reviewerEmail) {
		return submissionsDb.getSubmissionsFromEvaluationFromStudent(courseId, evaluationName, reviewerEmail);
	}
	
	public List<SubmissionAttributes> getSubmissionsForCourse(String courseId) {
		return submissionsDb.getSubmissionsForCourse(courseId);
	}

	public List<SubmissionAttributes> getSubmissionsForEvaluation(String courseId,String evaluationName) {
		return submissionsDb.getSubmissionsForEvaluation(courseId, evaluationName);
	}

	//==========================================================================
	public void updateEvaluation(EvaluationAttributes evaluation) throws InvalidParametersException {
		if (!evaluation.isValid()) {
			throw new InvalidParametersException(evaluation.getInvalidStateInfo());
		}
		evaluationsDb.editEvaluation(evaluation);
	}
	
	public void setEvaluationPublishedStatus(String courseId, String evaluationName, boolean b) {
		evaluationsDb.setEvaluationPublishedStatus(courseId, evaluationName, b);
	}	

	public void updateStudentEmailForSubmissionsInCourse(String course,
			String originalEmail, String email) {
		submissionsDb.editStudentEmailForSubmissionsInCourse(course, originalEmail, email);
	}
	
	public void updateSubmission(SubmissionAttributes submission) throws InvalidParametersException {
		if (!submission.isValid()) {
			throw new InvalidParametersException(submission.getInvalidStateInfo());
		}
		submissionsDb.updateSubmission(submission);
	}
	
	public void updateSubmissions(List<SubmissionAttributes> submissionsDataList) {
		submissionsDb.updateSubmissions(submissionsDataList);
	}
	
	//==========================================================================
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
	
	public void deleteSubmissionsForOutgoingMember(String courseId, String name, String email, String team) {
		submissionsDb.deleteSubmissionsForOutgoingMember(courseId, name, email, team);
	}
	
	public void deleteAllSubmissionsForStudent(String courseId, String studentEmail) {
		submissionsDb.deleteAllSubmissionsForStudent(courseId, studentEmail);
	}
	
	//==========================================================================
	private List<String> getExistingStudentsInTeam(String courseId, String team) {
		Set<String> students = new HashSet<String>();
		List<SubmissionAttributes> submissionsDataList = submissionsDb
				.getSubmissionsForCourse(courseId);
		for (SubmissionAttributes s : submissionsDataList) {
			if (s.team.equals(team)) {
				students.add(s.reviewer);
			}
		}
		return new ArrayList<String>(students);
	}

}
