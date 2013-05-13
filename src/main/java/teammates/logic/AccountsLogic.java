package teammates.logic;

import java.util.ArrayList;
import java.util.List;

import com.google.appengine.api.datastore.KeyFactory;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.storage.api.AccountsDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Student;

/**
 * Accounts handles all operations related to a Teammates account.
 * @see Instructor
 * @see Student
 * 
 */
public class AccountsLogic {
	private static AccountsLogic instance = null;
	private static final AccountsDb accountsDb = new AccountsDb();
	
	/**
	 * Retrieve singleton instance of AccountsLogic
	 * 
	 * @return AccountsLogic
	 */
	public static AccountsLogic inst() {
		if (instance == null)
			instance = new AccountsLogic();
		return instance;
	}
	
	//==========================================================================
	
	public boolean isInstructor(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
		
		AccountAttributes a = accountsDb.getAccount(googleId);
		return a == null ? false : a.isInstructor;
	}

	public boolean isInstructorOfCourse(String instructorId, String courseId) {
		return accountsDb.getInstructorForGoogleId(courseId, instructorId)!=null;
	}
	 
	public boolean isStudent(String googleId) {
		return accountsDb.getStudentsForGoogleId(googleId).size()!=0;
	}
	
	public boolean isStudentExists(String courseId, String studentEmail) {
		return accountsDb.getStudentForEmail(courseId, studentEmail) != null;
	}
	
	//==========================================================================
	public void createAccount(String googleId, String name, 
			boolean isInstructor, String email, String institute) 
					throws InvalidParametersException, EntityAlreadyExistsException {
		AccountAttributes accountToAdd = new AccountAttributes();
		accountToAdd.googleId = googleId;
		accountToAdd.name = name;
		accountToAdd.isInstructor = isInstructor;
		accountToAdd.email = email;
		accountToAdd.institute = institute;
		
		if (!accountToAdd.isValid()) {
			throw new InvalidParametersException(accountToAdd.getInvalidStateInfo());
		}
		accountsDb.createAccount(accountToAdd);
	}
	
	public void createInstructor(String googleId, String courseId, String name,
			String email, String institute) throws InvalidParametersException, EntityAlreadyExistsException {
		// trim @gmail.com in ID field
		if (googleId.contains("@gmail.com")) {
			googleId = googleId.split("@")[0];
		}

		// Create the Account if it does not exist
		if (accountsDb.getAccount(googleId)==null) {
			AccountAttributes accountToAdd = new AccountAttributes();
			accountToAdd.googleId = googleId;
			accountToAdd.name = name;
			accountToAdd.isInstructor = true;
			accountToAdd.email = email;
			accountToAdd.institute = institute;
			accountsDb.createAccount(accountToAdd);
		} else {
			makeAccountInstructor(googleId);
		}

		// Create the Instructor
		InstructorAttributes instructorToAdd = new InstructorAttributes(googleId, courseId, name, email);

		if (!instructorToAdd.isValid()) {
			throw new InvalidParametersException(instructorToAdd.getInvalidStateInfo());
		}

		accountsDb.createInstructor(instructorToAdd);
	}
	
	public void createStudent(StudentAttributes studentData) throws InvalidParametersException, EntityAlreadyExistsException {
		if (!studentData.isValid()) {
			throw new InvalidParametersException(studentData.getInvalidStateInfo());
		}

		accountsDb.createStudent(studentData);
	}
	

	//==========================================================================
	public AccountAttributes getAccount(String googleId) {
		return accountsDb.getAccount(googleId);
	}
	
	public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
		return accountsDb.getInstructorForGoogleId(courseId, googleId);
	}
	
	public InstructorAttributes getInstructorForEmail(String courseId, String email) {
		return accountsDb.getInstructorForEmail(courseId, email);
	}
	
	public List<AccountAttributes> getInstructorAccounts() {
		return accountsDb.getInstructorAccounts();
	}
	
	public List<InstructorAttributes> getAllInstructors() {
		return accountsDb.getAllInstructors();
	}
	
	public List<InstructorAttributes> getInstructorsOfCourse(String courseId) {
		return accountsDb.getInstructorsForCourse(courseId);
	}
	
	public List<InstructorAttributes> getInstructorRolesForAccount(String googleId) {
		return accountsDb.getInstructorsForGoogleId(googleId);
	}
	
	public List<StudentAttributes> getStudentListForCourse(String courseId) {
		return accountsDb.getStudentsForCourse(courseId);
	}
	
	public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
		List<StudentAttributes> allStudents = getStudentListForCourse(courseId);
		ArrayList<StudentAttributes> unregistered = new ArrayList<StudentAttributes>();
		
		for(StudentAttributes s: allStudents){
			if(s.id==null || s.id.trim().isEmpty()){
				unregistered.add(s);
			}
		}
		return unregistered;
	}
	
	public StudentAttributes getStudent(String courseId, String email) {
		return accountsDb.getStudentForEmail(courseId, email);
	}
	
	public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
		return accountsDb.getStudentForGoogleId(courseId, googleId);
	}
	
	public ArrayList<StudentAttributes> getStudentsWithGoogleId(String googleId) {
		List<StudentAttributes> students = accountsDb.getStudentsForGoogleId(googleId);
		ArrayList<StudentAttributes> returnList = new ArrayList<StudentAttributes>();
		for (StudentAttributes s : students) {
			returnList.add(s);
		}
		return returnList;
	}


	//==========================================================================
	public void updateAccount(AccountAttributes account) throws InvalidParametersException {
		if (!account.isValid()) {
			throw new InvalidParametersException(account.getInvalidStateInfo());
		}
		accountsDb.updateAccount(account);
	}
	
	private void makeAccountInstructor(String googleId) {
		AccountAttributes account = accountsDb.getAccount(googleId);
		Assumption.assertNotNull(account);
		account.isInstructor = true;
		accountsDb.updateAccount(account);
	}
	
	public void makeAccountNonInstructor(String instructorId) {
		Assumption.assertNotNull(instructorId);
		AccountAttributes account = accountsDb.getAccount(instructorId);
		if (account != null) {
			account.isInstructor = false;
			accountsDb.updateAccount(account);
		}
	}
	
	public void updateInstructor(InstructorAttributes instructor) throws InvalidParametersException {
		if (!instructor.isValid()) {
			throw new InvalidParametersException(instructor.getInvalidStateInfo());
		}
		accountsDb.updateInstructor(instructor);
	}
	
	public void updateStudent(String originalEmail, StudentAttributes student) {
		// Edit student uses KeepOriginal policy, where unchanged fields are set
		// as null
		// Hence, we can't do isValid() here.

		// TODO: make the implementation more defensive, e.g. duplicate email
		accountsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.email, student.id, student.comments);	
	}
	
	public StudentAttributes joinCourse(String registrationKey, String googleID) throws JoinCourseException {
		
		StudentAttributes student = accountsDb.getStudentForRegistrationKey(registrationKey);
		
		if(student==null){
			throw new JoinCourseException(Common.ERRORCODE_INVALID_KEY,
					"You have entered an invalid key: " + registrationKey);
		}
		
		googleID = googleID.trim();
	
		// If ID field is not empty -> check if this is user's googleId?
		if (student.id != null && !student.id.equals("")) {
	
			if (student.id.equals(googleID)) {
				// Belongs to the student and the student is already registered
				// to course
				throw new JoinCourseException(Common.ERRORCODE_ALREADY_JOINED,
						googleID + " has already joined this course");
			} else {
				// Does not belong to this student but belongs to another
				// student that is already registered
				throw new JoinCourseException(
						Common.ERRORCODE_KEY_BELONGS_TO_DIFFERENT_USER,
						registrationKey + " belongs to a different user");
			}
		}
	
		// A Student entry found with this key and ID is unregistered, register
		// him
		student.id = googleID;
	
		accountsDb.updateStudent(student.course, student.email, student.name, student.team, student.email, student.id, student.comments);
		
		return student;
	}

		
	//==========================================================================
	public void deleteAccount(String googleId) {
		accountsDb.deleteInstructorsForGoogleId(googleId);
		accountsDb.deleteStudentsForGoogleId(googleId);
		accountsDb.deleteAccount(googleId);
	}

	public void deleteInstructor(String courseId, String googleId) {
		accountsDb.deleteInstructor(courseId, googleId);
	}

	public void deleteInstructorsForGoogleId(String googleId) {
		accountsDb.deleteInstructorsForGoogleId(googleId);
		makeAccountNonInstructor(googleId);
	}

	public void deleteStudent(String courseId, String studentEmail) {
		accountsDb.deleteStudent(courseId, studentEmail);
	}

}