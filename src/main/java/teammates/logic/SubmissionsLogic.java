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
 * Handles  operations related to submission entities.
 * This class does the field validation and sanitization before 
 * passing values to the Storage layer.
 */
public class SubmissionsLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, we have minimal
	//  header comments in this class.
	
	//TODO: add sanitization to this class.
	
	private static SubmissionsLogic instance = null;
	private static final Logger log = Common.getLogger();

	private static final SubmissionsDb submissionsDb = new SubmissionsDb();

	public static SubmissionsLogic inst() {
		if (instance == null){
			instance = new SubmissionsLogic();
		}
		return instance;
	}

	public void createSubmissions(
			List<SubmissionAttributes> listOfSubmissionsToAdd) {
		submissionsDb.createSubmissions(listOfSubmissionsToAdd);
		
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

	public List<SubmissionAttributes> getSubmissionsForEvaluationFromStudent(
			String course, String name, String email) {
		return submissionsDb.getSubmissionsForEvaluationFromStudent(course, name, email);
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

	public void updateStudentEmailForSubmissionsInCourse(String course,
			String originalEmail, String newEmail) {
		submissionsDb.updateStudentEmailForSubmissionsInCourse(course, originalEmail, newEmail);
		
	}

	/**
	 * Adjusts submissions for a student moving from one team to another.
	 * Deletes existing submissions for original team and creates empty
	 * submissions for the new team, in all existing submissions, including
	 * CLOSED and PUBLISHED ones.
	 */
	public void adjustSubmissionsForChangingTeam(String courseId,
			String studentEmail, String originalTeam, String newTeam) {
		
		List<EvaluationAttributes> evaluationDataList = 
				EvaluationsLogic.inst().getEvaluationsForCourse(courseId);
		
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
				EvaluationsLogic.inst().getEvaluationsForCourse(courseId);
		
		for (EvaluationAttributes ed : evaluationDataList) {
			addSubmissionsForIncomingMember(courseId, ed.name, studentEmail, team);
		}
	}

	public void deleteAllSubmissionsForStudent(String courseId, String studentEmail) {
		submissionsDb.deleteAllSubmissionsForStudent(courseId, studentEmail);
	}

	
	public void deleteAllSubmissionsForCourse(String courseId) {
		submissionsDb.deleteAllSubmissionsForCourse(courseId);
		
	}

	public void deleteAllSubmissionsForEvaluation(String courseId,
			String evaluationName) {
		submissionsDb.deleteAllSubmissionsForEvaluation(courseId, evaluationName);
		
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
