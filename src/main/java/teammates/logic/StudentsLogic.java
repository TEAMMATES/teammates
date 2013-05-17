package teammates.logic;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.storage.api.AccountsDb;
import teammates.storage.api.CoursesDb;
import teammates.storage.api.SubmissionsDb;

/**
 * Handles  operations related to student.
 * This class does the field validation and sanitization before 
 * passing values to the Storage layer.
 */
public class StudentsLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, no need for header 
	//  comments.
	
	//TODO: add sanitization to this class.
	
	private static StudentsLogic instance = null;
	private static final AccountsDb accountsDb = new AccountsDb();
	private static final CoursesDb coursesDb = new CoursesDb();
	private static final SubmissionsDb submissionsDb = new SubmissionsDb();
	
	@SuppressWarnings("unused")
	private static Logger log = Common.getLogger();
	
	public static StudentsLogic inst() {
		if (instance == null)
			instance = new StudentsLogic();
		return instance;
	}
	
	public void createStudent(StudentAttributes studentData) 
			throws InvalidParametersException, EntityAlreadyExistsException {
		
		if (!studentData.isValid()) {
			throw new InvalidParametersException(studentData.getInvalidStateInfo());
		}
	
		accountsDb.createStudent(studentData);
	}

	public StudentAttributes getStudentForEmail(String courseId, String email) {
		return accountsDb.getStudentForEmail(courseId, email);
	}

	public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
		return accountsDb.getStudentForGoogleId(courseId, googleId);
	}

	public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
		return accountsDb.getStudentsForGoogleId(googleId);
	}

	public List<StudentAttributes> getStudentsForCourse(String courseId) 
			throws EntityDoesNotExistException {
		
		List<StudentAttributes> studentsForCourse = accountsDb.getStudentsForCourse(courseId);
		
		if ((studentsForCourse.size() == 0) && (coursesDb.getCourse(courseId) == null)) {
			throw new EntityDoesNotExistException("Course does not exist :"
					+ courseId);
		}
		
		return studentsForCourse;
	}

	public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
		
		return accountsDb.getUnregisteredStudentsForCourse(courseId);
	}
	
	public String getKeyForStudent(String courseId, String email) {
	
		StudentAttributes studentData = getStudentForEmail(courseId, email);
	
		if (studentData == null) {
			return null; //TODO: throw EntityDoesNotExistException here
		}
	
		return studentData.key;
	}

	public boolean isStudentInAnyCourse(String googleId) {
		return accountsDb.getStudentsForGoogleId(googleId).size()!=0;
	}

	public boolean isStudentInCourse(String courseId, String studentEmail) {
		return accountsDb.getStudentForEmail(courseId, studentEmail) != null;
	}

	public void confirmStudentExists(String courseId, String email) 
			throws EntityDoesNotExistException {
		if (!isStudentInCourse(courseId, email)) {
			throw new EntityDoesNotExistException(
					"Non-existent student " + courseId + "/" + email);
		}
		
	}
	
	public void updateStudent(String originalEmail, StudentAttributes student) 
			throws EntityDoesNotExistException {
		// Edit student uses KeepOriginal policy, where unchanged fields are set
		// as null. Hence, we can't do isValid() here.
	
		// TODO: make the implementation more defensive, e.g. duplicate email
		confirmStudentExists(student.course, originalEmail);
		
		accountsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.email, student.id, student.comments);	
	}

	public StudentAttributes joinCourse(String registrationKey, String googleId) 
			throws JoinCourseException {
		
		StudentAttributes student = accountsDb.getStudentForRegistrationKey(registrationKey);
		googleId = googleId.trim();
		
		if(student==null){
			throw new JoinCourseException(Common.ERRORCODE_INVALID_KEY,
					"You have entered an invalid key: " + registrationKey);
		} else if (student.isRegistered()) {
			if (student.id.equals(googleId)) {
				throw new JoinCourseException(Common.ERRORCODE_ALREADY_JOINED,
						googleId + " has already joined this course");
			} else {
				throw new JoinCourseException(
						Common.ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER,
						registrationKey + " belongs to a different user");
			}
		} 
		
		//register the student
		student.id = googleId;
		accountsDb.updateStudent(student.course, student.email,
				student.name,
				student.team, student.email, student.id, student.comments);
		
		if (accountsDb.getAccount(googleId) == null) {
			createStudentAccount(student);
		}
		
		return student;
	}

	public void deleteStudentCascade(String courseId, String studentEmail) {
		accountsDb.deleteStudent(courseId, studentEmail);
		submissionsDb.deleteAllSubmissionsForStudent(courseId, studentEmail);
	}
	
	//TODO: have a deleteStudentCascade here?

	private void createStudentAccount(StudentAttributes student) {
		AccountAttributes account = new AccountAttributes();
		account.googleId = student.id;
		account.email = student.email;
		account.name = student.name;
		account.isInstructor = false;
		account.institute = getCourseInstitute(student.course);
		accountsDb.createAccount(account);
	}

	private String getCourseInstitute(String courseId) {
		CourseAttributes cd = coursesDb.getCourse(courseId);
		List<InstructorAttributes> instructorList = accountsDb.getInstructorsForCourse(cd.id);
		
		Assumption.assertTrue("Course has no instructors: " + cd.id, !instructorList.isEmpty());
		// Retrieve institute field from the first instructor of the course
		AccountAttributes instructorAcc = accountsDb.getAccount(instructorList.get(0).googleId);
		
		Assumption.assertNotNull("Instructor has no account: " + instructorList.get(0).googleId, instructorAcc);
		return instructorAcc.institute;
	}

}