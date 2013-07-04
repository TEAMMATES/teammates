package teammates.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.mail.internet.MimeMessage;

import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentAttributes.UpdateStatus;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.StringHelper;
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
	
	private static Logger log = Config.getLogger();
	
	public static StudentsLogic inst() {
		if (instance == null)
			instance = new StudentsLogic();
		return instance;
	}
	
	public void createStudentCascade(StudentAttributes studentData) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		
		studentsDb.createEntity(studentData);
		evaluationsLogic.adjustSubmissionsForNewStudent(
				studentData.course, studentData.email, studentData.team);
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

	public boolean isStudentInAnyCourse(String googleId) {
		return studentsDb.getStudentsForGoogleId(googleId).size()!=0;
	}

	public boolean isStudentInCourse(String courseId, String studentEmail) {
		return studentsDb.getStudentForEmail(courseId, studentEmail) != null;
	}
	
	public boolean isStudentInTeam(String courseId, String teamName, String studentEmail) {
		StudentAttributes student = getStudentForEmail(courseId, studentEmail);
		List<StudentAttributes> teammates = getStudentsForTeam(teamName, courseId);	
		return teammates.contains(student) ? true : false;
	}
	
	public void updateStudentCascade(String originalEmail, StudentAttributes student) 
			throws EntityDoesNotExistException, InvalidParametersException {
		// Edit student uses KeepOriginal policy, where unchanged fields are set
		// as null. Hence, we can't do isValid() here.
	
		studentsDb.verifyStudentExists(student.course, originalEmail);
		
		StudentAttributes originalStudent = getStudentForEmail(student.course, originalEmail);
		
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

		// adjust submissions if moving to a different team
		if (isTeamChanged(originalStudent.team, student.team)) {
			evaluationsLogic.adjustSubmissionsForChangingTeam(student.course, student.email, originalStudent.team, student.team);
			frLogic.updateFeedbackResponsesForChangingTeam(student.course, student.email, originalStudent.team, student.team);
		}
	}
	
	//TODO: add better error reporting and validity checks
	public List<StudentAttributes> enrollStudents(String enrollLines,
			String courseId)
			throws EntityDoesNotExistException, EnrollException {

		if (!coursesLogic.isCoursePresent(courseId)) {
			throw new EntityDoesNotExistException("Course does not exist :"
					+ courseId);
		}

		Assumption.assertNotNull(StudentAttributes.ERROR_ENROLL_LINE_NULL,
				enrollLines);

		ArrayList<StudentAttributes> returnList = new ArrayList<StudentAttributes>();
		String[] linesArray = enrollLines.split(Const.EOL);
		ArrayList<StudentAttributes> studentList = new ArrayList<StudentAttributes>();

		// check if all non-empty lines are formatted correctly
		for (int i = 0; i < linesArray.length; i++) {
			String line = linesArray[i];
			try {
				if (StringHelper.isWhiteSpace(line))
					continue;
				studentList.add(new StudentAttributes(line, courseId));
			} catch (EnrollException e) {
				throw new EnrollException(e.errorCode, "Problem in line : "
						+ line + Const.EOL + e.getMessage());
			}
		}

		// TODO: can we use a batch persist operation here?
		// enroll all students
		for (StudentAttributes student : studentList) {
			StudentAttributes studentInfo;
			studentInfo = enrollStudent(student);
			returnList.add(studentInfo);
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
				MimeMessage email = sendRegistrationInviteToStudent(courseId,
						s.email);
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
		studentsDb.deleteStudent(courseId, studentEmail);
		SubmissionsLogic.inst().deleteAllSubmissionsForStudent(courseId, studentEmail);
		frLogic.deleteFeedbackResponsesForStudent(courseId, studentEmail);
	}

	public void deleteStudentsForGoogleId(String googleId) {
		studentsDb.deleteStudentsForGoogleId(googleId);
	}

	public void deleteStudentsForCourse(String courseId) {
		studentsDb.deleteStudentsForCourse(courseId);
		
	}
	
	
	private StudentAttributes enrollStudent(StudentAttributes validStudentAttributes) {
		StudentAttributes.UpdateStatus updateStatus = UpdateStatus.UNMODIFIED;
		try {
			if (isSameAsExistingStudent(validStudentAttributes)) {
				updateStatus = UpdateStatus.UNMODIFIED;
			} else if (isModificationToExistingStudent(validStudentAttributes)) {
				updateStudentCascade(validStudentAttributes.email, validStudentAttributes);
				updateStatus = UpdateStatus.MODIFIED;
			} else {
				createStudentCascade(validStudentAttributes);
				updateStatus = UpdateStatus.NEW;
			}
		} catch (Exception e) {
			//TODO: need better error handling here. This error is not 'unexpected'. e.g., invalid student data
			updateStatus = UpdateStatus.ERROR;
			String errorMessage = "Exception thrown unexpectedly while enrolling student: " 
					+ validStudentAttributes.toString() + Const.EOL + TeammatesException.toStringWithStackTrace(e);
			log.severe(errorMessage);
		}
		validStudentAttributes.updateStatus = updateStatus;
		return validStudentAttributes;
	}
	
	private boolean isInEnrollList(StudentAttributes student,
			ArrayList<StudentAttributes> studentInfoList) {
		for (StudentAttributes studentInfo : studentInfoList) {
			if (studentInfo.email.equalsIgnoreCase(student.email))
				return true;
		}
		return false;
	}

	private boolean isSameAsExistingStudent(StudentAttributes student) {
		StudentAttributes existingStudent = 
				getStudentForEmail(student.course, student.email);
		if (existingStudent == null)
			return false;
		return student.isEnrollInfoSameAs(existingStudent);
	}

	private boolean isModificationToExistingStudent(StudentAttributes student) {
		return isStudentInCourse(student.course, student.email);
	}
	
	private boolean isTeamChanged(String originalTeam, String newTeam) {
		return (newTeam != null) && (originalTeam != null)
				&& (!originalTeam.equals(newTeam));
	}
}