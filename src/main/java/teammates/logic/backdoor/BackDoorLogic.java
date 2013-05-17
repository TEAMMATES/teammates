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
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.SubmissionAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
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
	 * @throws EntityDoesNotExistException 
	 * @throws Exception
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
			// This method is only used in test cases, so it should be fine to hard code a value for Institute
			super.createInstructor(instructor.googleId, instructor.courseId, instructor.name, instructor.email, "National University of Singapore");
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
					+ " to course " + evaluation.course);
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
		StudentAttributes student = getStudent(courseId, email);
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
		editEvaluation(evaluation);
	}

	public void editSubmissionAsJson(String submissionJson) throws InvalidParametersException, EntityDoesNotExistException {
		SubmissionAttributes submission = Common.getTeammatesGson().fromJson(
				submissionJson, SubmissionAttributes.class);
		ArrayList<SubmissionAttributes> submissionList = new ArrayList<SubmissionAttributes>();
		submissionList.add(submission);
		editSubmissions(submissionList);
	}
	
	public ArrayList<MimeMessage> activateReadyEvaluations() {
		ArrayList<MimeMessage> messagesSent = new ArrayList<MimeMessage>();
		List<EvaluationAttributes> evaluations = evaluationsLogic.getReadyEvaluations(); 
		
		for (EvaluationAttributes ed: evaluations) {
			try {
				CourseAttributes course = getCourse(ed.course);
				
				List<StudentAttributes> students = studentsLogic.getStudentsForCourse(ed.course);
				
				Emails emails = new Emails();
				List<MimeMessage> messages = emails.generateEvaluationOpeningEmails(course, ed, students);
				emails.sendEmails(messages);
				messagesSent.addAll(messages);
				
				//mark evaluation as activated
				ed.activated=true;
				editEvaluation(ed);
			} catch (Exception e) {
				log.severe("Unexpected error "+ Common.stackTraceToString(e));
			} 
		}
		return messagesSent;
	}

	public ArrayList<MimeMessage> sendRemindersForClosingEvaluations() throws MessagingException, IOException {
		ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
		
		List<EvaluationAttributes> evaluationDataList = evaluationsLogic.getEvaluationsClosingWithinTimeLimit(Common.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT);

		for (EvaluationAttributes ed : evaluationDataList) {
			try {

				List<StudentAttributes> studentDataList = studentsLogic.getStudentsForCourse(ed.course);
				
				List<StudentAttributes> studentToRemindList = new ArrayList<StudentAttributes>();

				for (StudentAttributes sd : studentDataList) {
					if (!evaluationsLogic.isEvaluationCompletedByStudent(ed, sd.email)) {
						studentToRemindList.add(sd);
					}
				}

				CourseAttributes c = getCourse(ed.course);

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
	
	public void editEvaluation(EvaluationAttributes evaluation) throws InvalidParametersException, EntityDoesNotExistException{
		evaluationsLogic.updateEvaluation(evaluation);
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

		coursesLogic.createCourse(courseId, courseName);
	}
}
