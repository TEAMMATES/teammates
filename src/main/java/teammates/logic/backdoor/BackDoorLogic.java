package teammates.logic.backdoor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.InstructorData;
import teammates.common.datatransfer.CourseData;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.SubmissionData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.AccountsLogic;
import teammates.logic.CoursesLogic;
import teammates.logic.Emails;
import teammates.logic.EvaluationsLogic;
import teammates.logic.api.Logic;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Evaluation;
import teammates.storage.entity.Instructor;

public class BackDoorLogic extends Logic {
	
	private static Logger log = Common.getLogger();
	
	/**
	 * Persists given data in the datastore Works ONLY if the data is correct
	 * and new (i.e. these entities do not already exist in the datastore). The
	 * behavior is undefined if incorrect or not new.
	 * 
	 * @param dataBundleJsonString
	 * @return status of the request in the form 'status meassage'+'additional
	 *         info (if any)' e.g., "[BACKEND_STATUS_SUCCESS]" e.g.,
	 *         "[BACKEND_STATUS_FAILURE]NullPointerException at ..."
	 * @throws EntityAlreadyExistsException
	 * @throws InvalidParametersException
	 * @throws Exception
	 */

	public String persistNewDataBundle(DataBundle dataBundle)
			throws InvalidParametersException, EntityAlreadyExistsException {

		if (dataBundle == null) {
			throw new InvalidParametersException(
					Common.ERRORCODE_NULL_PARAMETER, "Null data bundle");
		}
		
		HashMap<String, AccountData> accounts = dataBundle.accounts;
		for (AccountData account : accounts.values()) {
			log.fine("API Servlet adding account :" + account.googleId);
			super.createAccount(account.googleId, account.name, account.isInstructor,
									account.email, account.institute);
		}

		HashMap<String, CourseData> courses = dataBundle.courses;
		for (CourseData course : courses.values()) {
			log.fine("API Servlet adding course :" + course.id);
			this.createCourse(course.id, course.name);
		}

		HashMap<String, InstructorData> instructors = dataBundle.instructors;
		for (InstructorData instructor : instructors.values()) {
			log.fine("API Servlet adding instructor :" + instructor.googleId);
			// This method is only used in test cases, so it should be fine to hard code a value for Institute
			super.createInstructor(instructor.googleId, instructor.courseId, instructor.name, instructor.email, "National University of Singapore");
		}

		HashMap<String, StudentData> students = dataBundle.students;
		for (StudentData student : students.values()) {
			log.fine("API Servlet adding student :" + student.email
					+ " to course " + student.course);
			super.createStudent(student);
		}

		HashMap<String, EvaluationData> evaluations = dataBundle.evaluations;
		for (EvaluationData evaluation : evaluations.values()) {
			log.fine("API Servlet adding evaluation :" + evaluation.name
					+ " to course " + evaluation.course);
			createEvaluation(evaluation);
		}

		// processing is slightly different for submissions because we are
		// adding all submissions in one go
		HashMap<String, SubmissionData> submissionsMap = dataBundle.submissions;
		List<SubmissionData> submissionsList = new ArrayList<SubmissionData>();
		for (SubmissionData submission : submissionsMap.values()) {
			log.fine("API Servlet adding submission for "
					+ submission.evaluation + " from " + submission.reviewer
					+ " to " + submission.reviewee);
			submissionsList.add(submission);
		}
		EvaluationsLogic.inst().getSubmissionsDb().editSubmissions(submissionsList);
		log.fine("API Servlet added " + submissionsList.size() + " submissions");

		return Common.BACKEND_STATUS_SUCCESS;
	}
	
	public String getAccountAsJson(String googleId) {
		AccountData accountData = getAccount(googleId);
		return Common.getTeammatesGson().toJson(accountData);
	}
	
	public String getInstructorAsJson(String instructorID, String courseId) {
		InstructorData instructorData = getInstructor(instructorID, courseId);
		return Common.getTeammatesGson().toJson(instructorData);
	}

	public String getCourseAsJson(String courseId) {
		CourseData course = getCourse(courseId);
		return Common.getTeammatesGson().toJson(course);
	}

	public String getStudentAsJson(String courseId, String email) {
		StudentData student = getStudent(courseId, email);
		return Common.getTeammatesGson().toJson(student);
	}

	public String getEvaluationAsJson(String courseId, String evaluationName) {
		EvaluationData evaluation = getEvaluation(courseId, evaluationName);
		return Common.getTeammatesGson().toJson(evaluation);
	}

	public String getSubmissionAsJson(String courseId, String evaluationName,
			String reviewerEmail, String revieweeEmail) {
		SubmissionData target = getSubmission(courseId, evaluationName,
				reviewerEmail, revieweeEmail);
		return Common.getTeammatesGson().toJson(target);
	}

	public void editAccountAsJson(String newValues)
			throws InvalidParametersException, EntityDoesNotExistException {
		AccountData account = Common.getTeammatesGson().fromJson(newValues,
				AccountData.class);
		updateAccount(account);
	}
	
	public void editStudentAsJson(String originalEmail, String newValues)
			throws InvalidParametersException, EntityDoesNotExistException {
		StudentData student = Common.getTeammatesGson().fromJson(newValues,
				StudentData.class);
		editStudent(originalEmail, student);
	}

	public void editEvaluationAsJson(String evaluationJson)
			throws InvalidParametersException, EntityDoesNotExistException {
		EvaluationData evaluation = Common.getTeammatesGson().fromJson(
				evaluationJson, EvaluationData.class);
		editEvaluation(evaluation);
	}

	public void editSubmissionAsJson(String submissionJson) throws InvalidParametersException, EntityDoesNotExistException {
		SubmissionData submission = Common.getTeammatesGson().fromJson(
				submissionJson, SubmissionData.class);
		ArrayList<SubmissionData> submissionList = new ArrayList<SubmissionData>();
		submissionList.add(submission);
		editSubmissions(submissionList);
	}
	
	public ArrayList<MimeMessage> activateReadyEvaluations() throws EntityDoesNotExistException, MessagingException, InvalidParametersException, IOException{
		ArrayList<MimeMessage> messagesSent = new ArrayList<MimeMessage>();
		List<EvaluationData> evaluations = EvaluationsLogic.inst().getEvaluationsDb().getReadyEvaluations(); 
		
		for (EvaluationData ed: evaluations) {
			
			CourseData course = getCourse(ed.course);
			List<StudentData> students = getStudentListForCourse(ed.course);
			
			Emails emails = new Emails();
			List<MimeMessage> messages = emails.generateEvaluationOpeningEmails(course, ed, students);
			emails.sendEmails(messages);
			messagesSent.addAll(messages);
			
			//mark evaluation as activated
			ed.activated=true;
			editEvaluation(ed);
		}
		return messagesSent;
	}

	public ArrayList<MimeMessage> sendRemindersForClosingEvaluations() throws MessagingException, IOException {
		ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
		
		EvaluationsLogic evaluations = EvaluationsLogic.inst();
		List<EvaluationData> evaluationDataList = evaluations.getEvaluationsDb().getEvaluationsClosingWithinTimeLimit(Common.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT);

		for (EvaluationData ed : evaluationDataList) {

			List<StudentData> studentDataList = AccountsLogic.inst().getDb().getStudentListForCourse(ed.course);

			List<StudentData> studentToRemindList = new ArrayList<StudentData>();

			for (StudentData sd : studentDataList) {
				if (!evaluations.isEvaluationSubmitted(ed, sd.email)) {
					studentToRemindList.add(sd);
				}
			}
			
			CourseData c = getCourse(ed.course);
			
			Emails emailMgr = new Emails();
			List<MimeMessage> emails = emailMgr.generateEvaluationClosingEmails(c, ed, studentToRemindList);
			emailMgr.sendEmails(emails);
			emailsSent.addAll(emails);
		}
		return emailsSent;
	}
	
	public void editEvaluation(EvaluationData evaluation) throws InvalidParametersException, EntityDoesNotExistException{
		EvaluationsLogic.inst().getEvaluationsDb().editEvaluation(evaluation);
	}

	/**
	 * Creates a COURSE without an INSTRUCTOR relation
	 * Used in persisting DataBundles for Test cases
	 * 
	 * @param courseId
	 * @param courseName
	 * @throws EntityAlreadyExistsException
	 * @throws InvalidParametersException
	 */
	public void createCourse(String courseId, String courseName) 
			throws EntityAlreadyExistsException, InvalidParametersException {
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
		Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseName);

		CourseData courseToAdd = new CourseData(courseId, courseName);

		if (!courseToAdd.isValid()) {
			throw new InvalidParametersException(
					courseToAdd.getInvalidStateInfo());
		}

		CoursesLogic.inst().getDb().createCourse(courseToAdd);
	}
}
