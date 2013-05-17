package teammates.logic;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.StudentsDb;

/**
 * Handles  operations related to student roles.
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
	private StudentsDb studentsDb = new StudentsDb();
	
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
	
		studentsDb.createStudent(studentData);
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
		
		List<StudentAttributes> studentsForCourse = studentsDb.getStudentsForCourse(courseId);
		
		return studentsForCourse;
	}

	public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
		
		return studentsDb.getUnregisteredStudentsForCourse(courseId);
	}
	
	public String getKeyForStudent(String courseId, String email) {
	
		StudentAttributes studentData = getStudentForEmail(courseId, email);
	
		if (studentData == null) {
			return null; //TODO: throw EntityDoesNotExistException here
		}
	
		return studentData.key;
	}

	public boolean isStudentInAnyCourse(String googleId) {
		return studentsDb.getStudentsForGoogleId(googleId).size()!=0;
	}

	public boolean isStudentInCourse(String courseId, String studentEmail) {
		return studentsDb.getStudentForEmail(courseId, studentEmail) != null;
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
		
		studentsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.email, student.id, student.comments);	
	}

	

	public void deleteStudentCascade(String courseId, String studentEmail) {
		studentsDb.deleteStudent(courseId, studentEmail);
		SubmissionsLogic.inst().deleteAllSubmissionsForStudent(courseId, studentEmail);
	}

	public void deleteStudentsForGoogleId(String googleId) {
		studentsDb.deleteStudentsForGoogleId(googleId);
	}
	
	//TODO: have a deleteStudentCascade here?

}