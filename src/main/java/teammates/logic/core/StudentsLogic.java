package teammates.logic.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.EvaluationAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.common.util.StringHelper;
import teammates.common.util.Utils;
import teammates.storage.api.StudentsDb;

/**
 * Handles  operations related to student roles.
 */
public class StudentsLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, no need for header 
	//  comments.
	
	private static StudentsLogic instance = null;
	private StudentsDb studentsDb = new StudentsDb();
	
	private CoursesLogic coursesLogic = CoursesLogic.inst();
	private EvaluationsLogic evaluationsLogic = EvaluationsLogic.inst();
	private FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
	
	private static Logger log = Utils.getLogger();
	
	public static StudentsLogic inst() {
		if (instance == null)
			instance = new StudentsLogic();
		return instance;
	}
	
	public void createStudentCascade(StudentAttributes studentData) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		
		createStudentCascadeWithoutSubmissionAdjustment(studentData);
		evaluationsLogic.adjustSubmissionsForNewStudent(
				studentData.course, studentData.email, studentData.team);
	}
	
	private void createStudentCascadeWithoutSubmissionAdjustment(StudentAttributes studentData) 
			throws InvalidParametersException, EntityAlreadyExistsException {	
		studentsDb.createEntity(studentData);
	}

	public StudentAttributes getStudentForEmail(String courseId, String email) {
		return studentsDb.getStudentForEmail(courseId, email);
	}

	public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
		return studentsDb.getStudentForGoogleId(courseId, googleId);
	}

	public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
		return studentsDb.getStudentForRegistrationKey(registrationKey);
	}

	public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
		return studentsDb.getStudentsForGoogleId(googleId);
	}

	public List<StudentAttributes> getStudentsForCourse(String courseId) 
			throws EntityDoesNotExistException {
		return studentsDb.getStudentsForCourse(courseId);
	}
	
	public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
		return studentsDb.getStudentsForTeam(teamName, courseId);
	}

	public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
		
		return studentsDb.getUnregisteredStudentsForCourse(courseId);
	}
	
	public String getKeyForStudent(String courseId, String email) {
	
		StudentAttributes studentData = getStudentForEmail(courseId, email);
	
		if (studentData == null) {
			return null; //TODO: throw EntityDoesNotExistException?
		}
	
		return studentData.key;
	}
	
	public String getEncryptedKeyForStudent(String courseId, String email) {
		
		StudentAttributes studentData = getStudentForEmail(courseId, email);
		
		if (studentData == null) {
			return null; //TODO: throw EntityDoesNotExistException?
		}
	
		return StringHelper.encrypt(studentData.key);
	}

	public boolean isStudentInAnyCourse(String googleId) {
		return studentsDb.getStudentsForGoogleId(googleId).size()!=0;
	}

	public boolean isStudentInCourse(String courseId, String studentEmail) {
		return studentsDb.getStudentForEmail(courseId, studentEmail) != null;
	}
	
	public boolean isStudentInTeam(String courseId, String teamName, String studentEmail) {
		
		StudentAttributes student = getStudentForEmail(courseId, studentEmail);
		if (student == null) {
			return false;
		}
		
		List<StudentAttributes> teammates = getStudentsForTeam(teamName, courseId);		
		for(StudentAttributes teammate : teammates) {
			if (teammate.email.equals(student.email)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isStudentsInSameTeam(String courseId, String student1Email, String student2Email) {
		StudentAttributes student1 = getStudentForEmail(courseId, student1Email);
		if(student1 == null) {
			return false;
		}
		return isStudentInTeam(courseId, student1.team, student2Email);
	}
	
	public void updateStudentCascade(String originalEmail, StudentAttributes student) 
			throws InvalidParametersException, EntityDoesNotExistException {
		StudentAttributes originalStudent = getStudentForEmail(student.course, originalEmail);
		updateStudentCascadeWithoutSubmissionAdjustment(originalEmail, student);
		
		// adjust submissions if moving to a different team
		if (isTeamChanged(originalStudent.team, student.team)) {
			evaluationsLogic.adjustSubmissionsForChangingTeam(student.course, student.email, student.team);
			frLogic.updateFeedbackResponsesForChangingTeam(student.course, student.email, originalStudent.team, student.team);
		}
	}
	
	//TODO: this name is misleading. Some adjustments are done to submissions.
	private void updateStudentCascadeWithoutSubmissionAdjustment(String originalEmail, 
			StudentAttributes student) 
			throws EntityDoesNotExistException, InvalidParametersException {
		// Edit student uses KeepOriginal policy, where unchanged fields are set
		// as null. Hence, we can't do isValid() here.
	
		studentsDb.verifyStudentExists(student.course, originalEmail);
		
		StudentAttributes originalStudent = getStudentForEmail(student.course, originalEmail);
		
		//TODO: The block of code below can be extracted to a method in StudentAttributes.
		//      e.g., originalStudent.updateValues(student)
		
		// prepare new student
		if(student.email == null){
			student.email = originalStudent.email;
		}
		if(student.name == null){
			student.name = originalStudent.name;
		}
		if(student.googleId == null){
			student.googleId = originalStudent.googleId;
		}
		if(student.team == null){
			student.team = originalStudent.team;
		}
		if(student.comments == null){
			student.comments = originalStudent.comments;
		}
		
		if(!student.isValid()) {
			throw new InvalidParametersException(student.getInvalidityInfo());
		}
		
		studentsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.email, student.googleId, student.comments);	
		
		// cascade email change, if any
		if (!originalEmail.equals(student.email)) {
			evaluationsLogic.updateStudentEmailForSubmissionsInCourse(student.course, originalEmail, student.email);
			frLogic.updateFeedbackResponsesForChangingEmail(student.course, originalEmail, student.email);
		}
	}
	
	public List<StudentAttributes> enrollStudents(String enrollLines,
			String courseId)
			throws EntityDoesNotExistException, EnrollException, InvalidParametersException {

		if (!coursesLogic.isCoursePresent(courseId)) {
			throw new EntityDoesNotExistException("Course does not exist :"
					+ courseId);
		}

		Assumption.assertNotNull(StudentAttributes.ERROR_ENROLL_LINE_NULL,
				enrollLines);
		
		if (enrollLines.isEmpty()) {
			throw new EnrollException(Const.StatusMessages.ENROLL_LINE_EMPTY);
		}
		
		List<String> invalidityInfo = getInvalidityInfoInEnrollLines(enrollLines, courseId);
		if (!invalidityInfo.isEmpty()) {
			throw new EnrollException(StringHelper.toString(invalidityInfo, "<br>"));
		}

		ArrayList<StudentAttributes> returnList = new ArrayList<StudentAttributes>();
		ArrayList<StudentEnrollDetails> enrollmentList = new ArrayList<StudentEnrollDetails>();
		ArrayList<StudentAttributes> studentList = new ArrayList<StudentAttributes>();
		
		String[] linesArray = enrollLines.split(Const.EOL);
		Integer[] columnOrder = getColumnOrder(linesArray[0]);
		
		int startLine = 1;
		if (columnOrder == null) {
			startLine = 0;
		}
		for (int i = startLine; i < linesArray.length; i++) {
			String line = linesArray[i];
			
			if (StringHelper.isWhiteSpace(line)) {
				continue;
			}
			
			StudentAttributes student = new StudentAttributes(line, courseId, columnOrder);
			studentList.add(student);
		}

		// TODO: can we use a batch persist operation here?
		// enroll all students
		for (StudentAttributes student : studentList) {
			StudentEnrollDetails enrollmentDetails;
			
			enrollmentDetails = enrollStudent(student);
			student.updateStatus = enrollmentDetails.updateStatus;
			
			enrollmentList.add(enrollmentDetails);
			returnList.add(student);
		}
		
		//Adjust submissions for each evaluation within the course
		List<EvaluationAttributes> evaluations = evaluationsLogic
				.getEvaluationsForCourse(courseId);
		
		for(EvaluationAttributes eval : evaluations) {
			adjustSubmissionsForEnrollments(enrollmentList,eval);
		}
		
		//Adjust submissions for all feedback responses within the course
		List<FeedbackSessionAttributes> feedbackSessions = FeedbackSessionsLogic.inst()
				.getFeedbackSessionsForCourse(courseId);
		
		for (FeedbackSessionAttributes session : feedbackSessions) {
			List<FeedbackResponseAttributes> allResponses = frLogic
					.getFeedbackResponsesForSession(session.feedbackSessionName, session.courseId);
			
			for (FeedbackResponseAttributes response : allResponses) {
				adjustFeedbackResponseForEnrollments(enrollmentList, response);
			}
		}

		// add to return list students not included in the enroll list.
		List<StudentAttributes> studentsInCourse = getStudentsForCourse(courseId);
		for (StudentAttributes student : studentsInCourse) {
			if (!isInEnrollList(student, returnList)) {
				student.updateStatus = StudentAttributes.UpdateStatus.NOT_IN_ENROLL_LIST;
				returnList.add(student);
			}
		}

		return returnList;
	}

	public MimeMessage sendRegistrationInviteToStudent(String courseId, String studentEmail) 
			throws EntityDoesNotExistException {
		
		CourseAttributes course = coursesLogic.getCourse(courseId);
		if (course == null) {
			throw new EntityDoesNotExistException(
					"Course does not exist [" + courseId + "], trying to send invite email to student [" + studentEmail + "]");
		}
		
		StudentAttributes studentData = getStudentForEmail(courseId, studentEmail);
		if (studentData == null) {
			throw new EntityDoesNotExistException(
					"Student [" + studentEmail + "] does not exist in course [" + courseId + "]");
		}
		
		Emails emailMgr = new Emails();
		try {
			MimeMessage email = emailMgr.generateStudentCourseJoinEmail(course, studentData);
			emailMgr.sendEmail(email);
			return email;
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error while sending email", e);
		}
		
	}
	
	public List<MimeMessage> sendRegistrationInviteForCourse(String courseId) {
		List<StudentAttributes> studentDataList = getUnregisteredStudentsForCourse(courseId);
		
		ArrayList<MimeMessage> emailsSent = new ArrayList<MimeMessage>();
	
		//TODO: sending mail should be moved to somewhere else.
		for (StudentAttributes s : studentDataList) {
			try {
				MimeMessage email = sendRegistrationInviteToStudent(courseId, s.email);
				emailsSent.add(email);
			} catch (EntityDoesNotExistException e) {
				Assumption
						.fail("Unexpected EntitiyDoesNotExistException thrown when sending registration email"
								+ TeammatesException.toStringWithStackTrace(e));
			}
		}
		return emailsSent;
	}

	public void deleteStudentCascade(String courseId, String studentEmail) {
		// delete responses first as we need to know the student's team.
		frLogic.deleteFeedbackResponsesForStudent(courseId, studentEmail);
		studentsDb.deleteStudent(courseId, studentEmail);
		SubmissionsLogic.inst().deleteAllSubmissionsForStudent(courseId, studentEmail);
	}

	public void deleteStudentsForGoogleId(String googleId) {
		studentsDb.deleteStudentsForGoogleId(googleId);
	}

	public void deleteStudentsForCourse(String courseId) {
		studentsDb.deleteStudentsForCourse(courseId);
		
	}
	
	private void adjustSubmissionsForEnrollments(
			ArrayList<StudentEnrollDetails> enrollmentList,
			EvaluationAttributes eval) throws InvalidParametersException, EntityDoesNotExistException {
		
		for(StudentEnrollDetails enrollment : enrollmentList) {
			if(enrollment.updateStatus == UpdateStatus.MODIFIED &&
					isTeamChanged(enrollment.oldTeam, enrollment.newTeam)) {
				evaluationsLogic.adjustSubmissionsForChangingTeamInEvaluation(enrollment.course,
						enrollment.email, enrollment.newTeam, eval.name);
			} else if (enrollment.updateStatus == UpdateStatus.NEW) {
				evaluationsLogic.adjustSubmissionsForNewStudentInEvaluation(
						enrollment.course, enrollment.email, enrollment.newTeam, eval.name);
			}
		}
	}
	
	private void adjustFeedbackResponseForEnrollments(
			ArrayList<StudentEnrollDetails> enrollmentList,
			FeedbackResponseAttributes response) throws InvalidParametersException, EntityDoesNotExistException {
		
		for(StudentEnrollDetails enrollment : enrollmentList) {
			if(enrollment.updateStatus == UpdateStatus.MODIFIED &&
					isTeamChanged(enrollment.oldTeam, enrollment.newTeam)) {
				frLogic.updateFeedbackResponseForChangingTeam(enrollment, response);
			}
		}
	}
	
	private StudentEnrollDetails enrollStudent(StudentAttributes validStudentAttributes) {
		StudentAttributes originalStudentAttributes = getStudentForEmail(
				validStudentAttributes.course, validStudentAttributes.email);
		
		StudentEnrollDetails enrollmentDetails = new StudentEnrollDetails();
		enrollmentDetails.course = validStudentAttributes.course;
		enrollmentDetails.email = validStudentAttributes.email;
		enrollmentDetails.newTeam = validStudentAttributes.team;
		
		try {
			if (validStudentAttributes.isEnrollInfoSameAs(originalStudentAttributes)) {
				enrollmentDetails.updateStatus = UpdateStatus.UNMODIFIED;
			} else if (originalStudentAttributes != null) {
				updateStudentCascadeWithoutSubmissionAdjustment(validStudentAttributes.email, validStudentAttributes);
				enrollmentDetails.updateStatus = UpdateStatus.MODIFIED;
				
				if(!originalStudentAttributes.team.equals(validStudentAttributes.team))
					enrollmentDetails.oldTeam = originalStudentAttributes.team;
			} else {
				createStudentCascadeWithoutSubmissionAdjustment(validStudentAttributes);
				enrollmentDetails.updateStatus = UpdateStatus.NEW;
			}
		} catch (Exception e) {
			//TODO: need better error handling here. This error is not 'unexpected'. e.g., invalid student data
			/* Note: If this method is only called by the public method enrollStudents(String,String),
			* then there won't be any invalid student data, since validity check has been done in that method
			*/
			enrollmentDetails.updateStatus = UpdateStatus.ERROR;
			String errorMessage = "Exception thrown unexpectedly while enrolling student: " 
					+ validStudentAttributes.toString() + Const.EOL + TeammatesException.toStringWithStackTrace(e);
			log.severe(errorMessage);
		}
		
		return enrollmentDetails;
	}
	
	/* All empty lines or lines with only whitespaces will be skipped.
	 * The invalidity info returned are in HTML format.
	 */
	private List<String> getInvalidityInfoInEnrollLines(String lines, String courseId) {
		List<String> invalidityInfo = new ArrayList<String>();
		String[] linesArray = lines.split(Const.EOL);

		Integer[] columnOrder = getColumnOrder(linesArray[0]);
		int startLine = 1;
		if (columnOrder == null) {
			startLine = 0;
		}
		
		for (int i = startLine; i < linesArray.length; i++) {
			String line = linesArray[i];
			try {
				if (StringHelper.isWhiteSpace(line)) {
					continue;
				}
				StudentAttributes student = new StudentAttributes(line, courseId, columnOrder);
				
				if (!student.isValid()) {
					String info = StringHelper.toString(student.getInvalidityInfo(),
													"<br>" + Const.StatusMessages.ENROLL_LINES_PROBLEM_DETAIL_PREFIX + " ");
					invalidityInfo.add(String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, line, info));
				}
			} catch (EnrollException e) {
				String info = String.format(Const.StatusMessages.ENROLL_LINES_PROBLEM, line, e.getMessage());
				invalidityInfo.add(info);
			}
		}
		
		return invalidityInfo;
	}
	
	private boolean isInEnrollList(StudentAttributes student,
			ArrayList<StudentAttributes> studentInfoList) {
		for (StudentAttributes studentInfo : studentInfoList) {
			if (studentInfo.email.equalsIgnoreCase(student.email))
				return true;
		}
		return false;
	}
	
	private boolean isTeamChanged(String originalTeam, String newTeam) {
		return (newTeam != null) && (originalTeam != null)
				&& (!originalTeam.equals(newTeam));
	}

	/**
	 * Return null if the given row is not a header row according to specification.<br>
	 * The column names allowed for header row: {team, name, email, comment}<br>
	 * They are not case-sensitive and plural nouns are allowed.
	 */
	private Integer[] getColumnOrder(String row){
		//TODO: Create a StudentAttributesFactory to handle this instead
		Assumption.assertNotNull(row);
		
		String[] fields = row.replace("|", "\t").split("\t");
		if (fields.length < 3 || fields.length > 4) {
			// we do not throw exception here as it should be treated as normal row
			// and handled by enrollStudents() method instead
			return null;
		}
		
		Integer[] order = new Integer[StudentAttributes.ARG_COUNT];
		for (int i = 0; i < order.length; i++) {
			order[i] = -1;
		}
		
		for (int i = 0; i < fields.length; i++) {
			String str = fields[i].trim().toLowerCase();
			if (str.matches(FieldValidator.REGEX_COLUMN_TEAM)) {
				order[StudentAttributes.ARG_INDEX_TEAM] = i;
			} else if (str.matches(FieldValidator.REGEX_COLUMN_NAME)) {
				order[StudentAttributes.ARG_INDEX_NAME] = i;
			} else if (str.matches(FieldValidator.REGEX_COLUMN_EMAIL)) {
				order[StudentAttributes.ARG_INDEX_EMAIL] = i;
			} else if (str.matches(FieldValidator.REGEX_COLUMN_COMMENT)) {
				order[StudentAttributes.ARG_INDEX_COMMENT] = i;
			} else {
				//assume not header row if no column name is matched
				return null;
			}
		}

		return order;
	}
}