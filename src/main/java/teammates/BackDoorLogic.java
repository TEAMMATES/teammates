package teammates;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import teammates.api.Common;
import teammates.api.EntityAlreadyExistsException;
import teammates.api.EntityDoesNotExistException;
import teammates.api.InvalidParametersException;
import teammates.api.Logic;
import teammates.api.UnauthorizedAccessException;
import teammates.datatransfer.CoordData;
import teammates.datatransfer.CourseData;
import teammates.datatransfer.DataBundle;
import teammates.datatransfer.EvaluationData;
import teammates.datatransfer.StudentData;
import teammates.datatransfer.SubmissionData;
import teammates.manager.Courses;
import teammates.manager.Emails;
import teammates.manager.Evaluations;
import teammates.persistent.Evaluation;
import teammates.persistent.Student;

public class BackDoorLogic extends Logic{
	
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

		HashMap<String, CoordData> coords = dataBundle.coords;
		for (CoordData coord : coords.values()) {
			log.fine("API Servlet adding coord :" + coord.id);
			super.createCoord(coord.id, coord.name, coord.email);
		}

		HashMap<String, CourseData> courses = dataBundle.courses;
		for (CourseData course : courses.values()) {
			log.fine("API Servlet adding course :" + course.id);
			createCourse(course.coord, course.id, course.name);
		}

		HashMap<String, StudentData> students = dataBundle.students;
		for (StudentData student : students.values()) {
			log.fine("API Servlet adding student :" + student.email
					+ " to course " + student.course);
			createStudent(student);
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
		createSubmissions(submissionsList);
		log.fine("API Servlet added " + submissionsList.size() + " submissions");

		return Common.BACKEND_STATUS_SUCCESS;
	}
	
	public String getCoordAsJson(String coordID) {
		CoordData coordData = getCoord(coordID);
		return Common.getTeammatesGson().toJson(coordData);
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
	
	public List<MimeMessage> activateReadyEvaluations() throws EntityDoesNotExistException, MessagingException, InvalidParametersException, IOException{
		ArrayList<MimeMessage> messagesSent = new ArrayList<MimeMessage>();
		List<Evaluation> evaluations = Evaluations.inst().getReadyEvaluations(); 
		for(Evaluation e: evaluations){
			
			EvaluationData ed = new EvaluationData(e);
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
	
	
	@Override
	protected boolean isInternalCall() {
		//back door calls are considered internal calls
		return true;
	}

	public List<MimeMessage> sendRemindersForClosingEvaluations() throws MessagingException, IOException {
		ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
		
		Evaluations evaluations = Evaluations.inst();
		List<Evaluation> evaluationList = evaluations.getEvaluationsClosingWithinTimeLimit(Common.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT);

		for (Evaluation e : evaluationList) {
			List<Student> studentList = Courses.inst().getStudentList(e.getCourseID());
			List<StudentData> studentToRemindList = new ArrayList<StudentData>();

			EvaluationData ed = new EvaluationData(e);
			
			for (Student s : studentList) {
				if (!evaluations.isEvaluationSubmitted(ed, s.getEmail())) {
					studentToRemindList.add(new StudentData(s));
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
	


}
