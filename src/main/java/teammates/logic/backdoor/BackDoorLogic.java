package teammates.logic.backdoor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.Emails;
import teammates.logic.api.Logic;

public class BackDoorLogic extends Logic {
	
	private static Logger log = Common.getLogger();
	
	@SuppressWarnings("unused")
	private void ____methods_used_in_PRODUCTION____________________________() {
	}
	
	//TODO: consider moving methods in this section to a new class so that this
	//  class can be 'only for testing'
	public ArrayList<MimeMessage> activateReadyEvaluations() {
		ArrayList<MimeMessage> messagesSent = new ArrayList<MimeMessage>();
		List<EvaluationAttributes> evaluations = evaluationsLogic.getReadyEvaluations(); 
		
		for (EvaluationAttributes ed: evaluations) {
			try {
				CourseAttributes course = getCourse(ed.courseId);
				
				List<StudentAttributes> students = studentsLogic.getStudentsForCourse(ed.courseId);
				
				Emails emails = new Emails();
				List<MimeMessage> messages = emails.generateEvaluationOpeningEmails(course, ed, students);
				emails.sendEmails(messages);
				messagesSent.addAll(messages);
				
				//mark evaluation as activated
				ed.activated=true;
				updateEvaluation(ed);
			} catch (Exception e) {
				log.severe("Unexpected error "+ Common.stackTraceToString(e));
			} 
		}
		return messagesSent;
	}

	public ArrayList<MimeMessage> sendRemindersForClosingEvaluations() 
			throws MessagingException, IOException {
		ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
		
		List<EvaluationAttributes> evaluationDataList = 
				evaluationsLogic.getEvaluationsClosingWithinTimeLimit(Common.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT);
	
		for (EvaluationAttributes ed : evaluationDataList) {
			try {
	
				List<StudentAttributes> studentDataList = studentsLogic.getStudentsForCourse(ed.courseId);
				
				List<StudentAttributes> studentToRemindList = new ArrayList<StudentAttributes>();
	
				for (StudentAttributes sd : studentDataList) {
					if (!evaluationsLogic.isEvaluationCompletedByStudent(ed, sd.email)) {
						studentToRemindList.add(sd);
					}
				}
	
				CourseAttributes c = getCourse(ed.courseId);
	
				Emails emailMgr = new Emails();
				List<MimeMessage> emails = emailMgr.generateEvaluationClosingEmails(c, ed, studentToRemindList);
				emailMgr.sendEmails(emails);
				emailsSent.addAll(emails);
				
			} catch (Exception e) {
				log.severe("Unexpected error " + Common.stackTraceToString(e));
			}
		}
		return emailsSent;
	}

	@SuppressWarnings("unused")
	private void ____methods_used_for_TESTING______________________________() {
	}
	
	/**
	 * Persists given data in the datastore Works ONLY if the data is correct
	 * and new (i.e. these entities do not already exist in the datastore). The
	 * behavior is undefined if incorrect or not new.
	 * 
	 * @return status of the request in the form 'status meassage'+'additional
	 *         info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
	 *         "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
	 */

	public String persistNewDataBundle(DataBundle dataBundle)
			throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {

		if (dataBundle == null) {
			throw new InvalidParametersException(
					Common.ERRORCODE_NULL_PARAMETER, "Null data bundle");
		}
		
		HashMap<String, AccountAttributes> accounts = dataBundle.accounts;
		for (AccountAttributes account : accounts.values()) {
			log.fine("API Servlet adding account :" + account.googleId);
			super.createAccount(account.googleId, account.name, account.isInstructor,
									account.email, account.institute);
		}

		HashMap<String, CourseAttributes> courses = dataBundle.courses;
		for (CourseAttributes course : courses.values()) {
			log.fine("API Servlet adding course :" + course.id);
			this.createCourse(course.id, course.name);
		}

		HashMap<String, InstructorAttributes> instructors = dataBundle.instructors;
		for (InstructorAttributes instructor : instructors.values()) {
			log.fine("API Servlet adding instructor :" + instructor.googleId);
			AccountAttributes existingAccount = getAccount(instructor.googleId);
			//Hardcoding institute value because this is used for testing only
			super.createInstructorAccount(instructor.googleId, instructor.courseId, 
					instructor.name, instructor.email, existingAccount==null? "National University of Singapore" : existingAccount.institute);
		}

		HashMap<String, StudentAttributes> students = dataBundle.students;
		for (StudentAttributes student : students.values()) {
			log.fine("API Servlet adding student :" + student.email
					+ " to course " + student.course);
			super.createStudent(student);
		}

		HashMap<String, EvaluationAttributes> evaluations = dataBundle.evaluations;
		for (EvaluationAttributes evaluation : evaluations.values()) {
			log.fine("API Servlet adding evaluation :" + evaluation.name
					+ " to course " + evaluation.courseId);
			createEvaluation(evaluation);
		}

		// processing is slightly different for submissions because we are
		// adding all submissions in one go
		HashMap<String, SubmissionAttributes> submissionsMap = dataBundle.submissions;
		List<SubmissionAttributes> submissionsList = new ArrayList<SubmissionAttributes>();
		for (SubmissionAttributes submission : submissionsMap.values()) {
			log.fine("API Servlet adding submission for "
					+ submission.evaluation + " from " + submission.reviewer
					+ " to " + submission.reviewee);
			submissionsList.add(submission);
		}
		submissionsLogic.updateSubmissions(submissionsList);
		log.fine("API Servlet added " + submissionsList.size() + " submissions");

		HashMap<String, FeedbackSessionAttributes> sessions = dataBundle.feedbackSessions;
		for (FeedbackSessionAttributes session : sessions.values()) {
			log.fine("API Servlet adding feedback session :" + session.feedbackSessionName
					+ " to course " + session.courseId);
			super.createFeedbackSession(session);
		}
		
		HashMap<String, FeedbackQuestionAttributes> questions = dataBundle.feedbackQuestions;
		for (FeedbackQuestionAttributes question : questions.values()) {
			log.fine("API Servlet adding feedback question :" + question.getId()
					+ " to session " + question.feedbackSessionName);
			super.createFeedbackQuestion(question);
		}
		
		return Common.BACKEND_STATUS_SUCCESS;
	}
	
	public String getAccountAsJson(String googleId) {
		AccountAttributes accountData = getAccount(googleId);
		return Common.getTeammatesGson().toJson(accountData);
	}
	
	public String getInstructorAsJson(String instructorID, String courseId) {
		InstructorAttributes instructorData = getInstructorForGoogleId(courseId, instructorID);
		return Common.getTeammatesGson().toJson(instructorData);
	}

	public String getCourseAsJson(String courseId) {
		CourseAttributes course = getCourse(courseId);
		return Common.getTeammatesGson().toJson(course);
	}

	public String getStudentAsJson(String courseId, String email) {
		StudentAttributes student = getStudentForEmail(courseId, email);
		return Common.getTeammatesGson().toJson(student);
	}

	public String getEvaluationAsJson(String courseId, String evaluationName) {
		EvaluationAttributes evaluation = getEvaluation(courseId, evaluationName);
		return Common.getTeammatesGson().toJson(evaluation);
	}

	public String getSubmissionAsJson(String courseId, String evaluationName,
			String reviewerEmail, String revieweeEmail) {
		SubmissionAttributes target = getSubmission(courseId, evaluationName,
				reviewerEmail, revieweeEmail);
		return Common.getTeammatesGson().toJson(target);
	}
	
	public String getFeedbackSessionAsJson(String feedbackSessionName, String courseId) {
		FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
		return Common.getTeammatesGson().toJson(fs);
	}
	
	public void editAccountAsJson(String newValues)
			throws InvalidParametersException, EntityDoesNotExistException {
		AccountAttributes account = Common.getTeammatesGson().fromJson(newValues,
				AccountAttributes.class);
		updateAccount(account);
	}
	
	public void editStudentAsJson(String originalEmail, String newValues)
			throws InvalidParametersException, EntityDoesNotExistException {
		StudentAttributes student = Common.getTeammatesGson().fromJson(newValues,
				StudentAttributes.class);
		updateStudent(originalEmail, student);
	}

	public void editEvaluationAsJson(String evaluationJson)
			throws InvalidParametersException, EntityDoesNotExistException {
		EvaluationAttributes evaluation = Common.getTeammatesGson().fromJson(
				evaluationJson, EvaluationAttributes.class);
		updateEvaluation(evaluation);
	}

	public void editSubmissionAsJson(String submissionJson) throws InvalidParametersException, EntityDoesNotExistException {
		SubmissionAttributes submission = Common.getTeammatesGson().fromJson(
				submissionJson, SubmissionAttributes.class);
		ArrayList<SubmissionAttributes> submissionList = new ArrayList<SubmissionAttributes>();
		submissionList.add(submission);
		updateSubmissions(submissionList);
	}
	
	public void updateEvaluation(EvaluationAttributes evaluation) 
			throws InvalidParametersException, EntityDoesNotExistException{
		
		evaluationsLogic.updateEvaluation(evaluation);
	}
	
	// This cascades deleting feedbackQuestion and feedbackResponses for testing purposes.
	// We do not do it in production to preserve question/responses for future repo.
	@Override
	public void deleteFeedbackSession(String feedbackSessionName, String courseId) {
		List<FeedbackQuestionAttributes> questionsToCascadeDelete;
		try {
			questionsToCascadeDelete = feedbackQuestionsLogic
					.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		} catch (EntityDoesNotExistException e) {
			// Silently fail
			return;
		}
		for (FeedbackQuestionAttributes question : questionsToCascadeDelete) {
			feedbackQuestionsLogic.deleteFeedbackQuestion(feedbackSessionName, courseId, question.questionNumber);
		}
		super.deleteFeedbackSession(feedbackSessionName, courseId);
	}

	/**
	 * Creates a COURSE without an INSTRUCTOR relation
	 * Used in persisting DataBundles for Test cases
	 */
	public void createCourse(String courseId, String courseName) 
			throws EntityAlreadyExistsException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseName);

		coursesLogic.createCourse(courseId, courseName);
	}

	private SubmissionAttributes getSubmission(
			String courseId, String evaluationName, String reviewerEmail, String revieweeEmail) {
				
		return submissionsLogic.getSubmission(
				courseId, evaluationName, revieweeEmail, reviewerEmail);
	}
}
