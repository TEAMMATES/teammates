package teammates.logic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.EvaluationsDb;
import teammates.storage.api.SubmissionsDb;

import com.google.appengine.api.datastore.Text;

/**
 * Handles  operations related to evaluation entities.
 * This class does the field validation and sanitization before 
 * passing values to the Storage layer.
 */
public class EvaluationsLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, we have minimal
	//  header comments in this class.
	
	//TODO: add sanitization to this class.
	
	private static EvaluationsLogic instance = null;
	private static final Logger log = Common.getLogger();

	private static final AccountsDb accountsDb = new AccountsDb();
	private static final EvaluationsDb evaluationsDb = new EvaluationsDb();
	private static final SubmissionsDb submissionsDb = new SubmissionsDb();

	public static EvaluationsLogic inst() {
		if (instance == null){
			instance = new EvaluationsLogic();
		}
		return instance;
	}

	public SubmissionAttributes getSubmission(String course, String evaluation, String reviewee, String reviewer) {
		return submissionsDb.getSubmission(course, evaluation, reviewee, reviewer);
	}

	public List<SubmissionAttributes> getSubmissionsForCourse(String courseId) {
		return submissionsDb.getSubmissionsForCourse(courseId);
	}

	public List<SubmissionAttributes> getSubmissionsForEvaluation(String courseId,String evaluationName) {
		return submissionsDb.getSubmissionsForEvaluation(courseId, evaluationName);
	}

	public List<SubmissionAttributes> getSubmissionsFromEvaluationFromStudent(String courseId, String evaluationName, String reviewerEmail) {
		return submissionsDb.getSubmissionsForEvaluationFromStudent(courseId, evaluationName, reviewerEmail);
	}

	public void updateSubmission(SubmissionAttributes submission) 
			throws InvalidParametersException, EntityDoesNotExistException {
		if (!submission.isValid()) {
			throw new InvalidParametersException(submission.getInvalidStateInfo());
		}
		submissionsDb.updateSubmission(submission);
	}

	public void updateSubmissions(List<SubmissionAttributes> submissionsDataList) 
			throws EntityDoesNotExistException {
		submissionsDb.updateSubmissions(submissionsDataList);
	}

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
		
		submissionsDb.deleteAllSubmissionsForStudent(courseId, studentEmail);
		
		for (EvaluationAttributes ed : evaluationDataList) {
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail, newTeam);
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
		
		List<EvaluationAttributes> evaluationDataList = 
				evaluationsDb.getEvaluationsForCourse(courseId);
		
		for (EvaluationAttributes ed : evaluationDataList) {
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail, team);
		}
	}

	public void deleteAllSubmissionsForStudent(String courseId, String studentEmail) {
		submissionsDb.deleteAllSubmissionsForStudent(courseId, studentEmail);
	}

	
	/**
	 * Creates an evaluation and empty submissions for it.
	 */
	public void createEvaluationCascade(EvaluationAttributes e)
			throws EntityAlreadyExistsException, InvalidParametersException {
	
		if (!e.isValid()) {
			throw new InvalidParametersException(e.getInvalidStateInfo());
		}
		
		evaluationsDb.createEvaluation(e);
	
		List<StudentAttributes> studentDataList = accountsDb.getStudentsForCourse(e.course);
		
		List<SubmissionAttributes> listOfSubmissionsToAdd = new ArrayList<SubmissionAttributes>();
	
		// This double loop creates 3 submissions for a pair of students:
		// x->x, x->y, y->x
		for (StudentAttributes sx : studentDataList) {
			for (StudentAttributes sy : studentDataList) {
				if (sx.team.equals(sy.team)) {
					SubmissionAttributes submissionToAdd = 
							new SubmissionAttributes(e.course, e.name, sx.team, sx.email, sy.email);
					submissionToAdd.p2pFeedback = new Text("");
					submissionToAdd.justification = new Text("");
					listOfSubmissionsToAdd.add(submissionToAdd);
				}
			}
		}
	
		submissionsDb.createSubmissions(listOfSubmissionsToAdd);
	}

	public EvaluationAttributes getEvaluation(String courseId, String evaluationName) {
		return evaluationsDb.getEvaluation(courseId, evaluationName);
	}

	public List<EvaluationAttributes> getEvaluationsForCourse(String courseId) {
		return evaluationsDb.getEvaluationsForCourse(courseId);
	}

	public List<EvaluationAttributes> getReadyEvaluations() {
		
		@SuppressWarnings("deprecation")
		List<EvaluationAttributes> evaluationList = evaluationsDb.getAllEvaluations();
		List<EvaluationAttributes> readyEvaluations = new ArrayList<EvaluationAttributes>();
	
		for (EvaluationAttributes e : evaluationList) {
			if (e.isReadyToActivate()) {
				readyEvaluations.add(e);
			}
		}
		return readyEvaluations;
	}

	public List<EvaluationAttributes> getEvaluationsClosingWithinTimeLimit(int hoursWithinLimit) {
		
		@SuppressWarnings("deprecation")
		List<EvaluationAttributes> evaluationList = evaluationsDb.getAllEvaluations();
		
		List<EvaluationAttributes> dueEvaluationList = new ArrayList<EvaluationAttributes>();
	
		for (EvaluationAttributes e : evaluationList) {
			
			if (e.isClosingWithinTimeLimit(hoursWithinLimit)) {
				dueEvaluationList.add(e);
			}
	
		}
	
		return dueEvaluationList;
	}

	/**
	 * @return false if the student has any incomplete submissions to any team mates.
	 */
	public boolean isEvaluationCompletedByStudent(EvaluationAttributes evaluation, String email) {
		
		List<SubmissionAttributes> submissionList = 
				submissionsDb.getSubmissionsForEvaluationFromStudent(
						evaluation.course, evaluation.name, email);

		for (SubmissionAttributes sd : submissionList) {
			if (sd.points == Common.POINTS_NOT_SUBMITTED) {
				return false;
			}
		}
		return true;
	}

	public boolean isEvaluationExists(String courseId, String evaluationName) {
		return evaluationsDb.getEvaluation(courseId, evaluationName) != null;
	}
	
	public void updateEvaluation(EvaluationAttributes evaluation) 
			throws InvalidParametersException, EntityDoesNotExistException {
		
		if (!evaluation.isValid()) {
			throw new InvalidParametersException(evaluation.getInvalidStateInfo());
		}
		
		evaluationsDb.updateEvaluation(evaluation);
	}
	
	public void updateStudentEmailForSubmissionsInCourse(String course,
			String originalEmail, String email) {
		submissionsDb.updateStudentEmailForSubmissionsInCourse(course, originalEmail, email);
	}

	public void setEvaluationPublishedStatus(String courseId, String evaluationName, boolean b) 
			throws EntityDoesNotExistException {

		EvaluationAttributes e = evaluationsDb.getEvaluation(courseId, evaluationName);

		if (e == null) {
			throw new EntityDoesNotExistException("Trying to update non-existent Evaluation: "
					+ courseId + " | " + evaluationName );
		}
		
		e.published = b;
		evaluationsDb.updateEvaluation(e);
	}	


	public void deleteEvaluationCascade(String courseId, String evaluationName) {

		evaluationsDb.deleteEvaluation(courseId, evaluationName);
		submissionsDb.deleteAllSubmissionsForEvaluation(courseId, evaluationName);
	}

	
	public void deleteEvaluationsForCourse(String courseId) {
		evaluationsDb.deleteAllEvaluationsForCourse(courseId);
		submissionsDb.deleteAllSubmissionsForCourse(courseId);
	}
	
	
	private void addSubmissionsForIncomingMember(
			String courseId, String evaluationName, String studentEmail, String newTeam) {
	
		List<String> students = getExistingStudentsInTeam(courseId, newTeam);
	
		// add self evaluation 
		List<SubmissionAttributes> listOfSubmissionsToAdd = new ArrayList<SubmissionAttributes>();
		
		SubmissionAttributes submissionToAdd = new SubmissionAttributes(courseId,
				evaluationName, newTeam, studentEmail, studentEmail);
		submissionToAdd.p2pFeedback = new Text("");
		submissionToAdd.justification = new Text("");
		listOfSubmissionsToAdd.add(submissionToAdd);
		
		//remove self from list
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
		
		submissionsDb.createSubmissions(listOfSubmissionsToAdd);
	}

	private List<String> getExistingStudentsInTeam(String courseId, String team) {
		
		Set<String> students = new HashSet<String>();
		
		List<SubmissionAttributes> submissionsDataList = 
				submissionsDb.getSubmissionsForCourse(courseId);
		
		for (SubmissionAttributes s : submissionsDataList) {
			if (s.team.equals(team)) {
				students.add(s.reviewer);
			}
		}
		
		return new ArrayList<String>(students);
	}

}
