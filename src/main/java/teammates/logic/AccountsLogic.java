package teammates.logic;

import java.util.ArrayList;
import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.InstructorData;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.storage.api.AccountsDb;

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
	public boolean isAccountExists(String googleId) {
		return accountsDb.isAccountExists(googleId);
	}

	public boolean isInstructor(String googleId) {
		return accountsDb.isInstructor(googleId);
	}

	public boolean isInstructorOfCourse(String instructorId, String courseId) {
		return accountsDb.isInstructorOfCourse(instructorId, courseId);
	}
	 
	public boolean isStudent(String googleId) {
		return accountsDb.getStudentsWithGoogleId(googleId).size()!=0;
	}
	
	public boolean isStudentExists(String courseId, String studentEmail) {
		return accountsDb.isStudentExists(courseId, studentEmail);
	}
	
	//==========================================================================
	public void createAccount(String googleId, String name, 
			boolean isInstructor, String email, String institute) 
					throws InvalidParametersException, EntityAlreadyExistsException {
		AccountData accountToAdd = new AccountData();
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
		if (!accountsDb.isAccountExists(googleId)) {
			AccountData accountToAdd = new AccountData();
			accountToAdd.googleId = googleId;
			accountToAdd.name = name;
			accountToAdd.isInstructor = true;
			accountToAdd.email = email;
			accountToAdd.institute = institute;
			accountsDb.createAccount(accountToAdd);
		} else {
			accountsDb.makeAccountInstructor(googleId);
		}

		// Create the Instructor
		InstructorData instructorToAdd = new InstructorData(googleId, courseId, name, email);

		if (!instructorToAdd.isValid()) {
			throw new InvalidParametersException(instructorToAdd.getInvalidStateInfo());
		}

		accountsDb.createInstructor(instructorToAdd);
	}
	
	public void createStudent(StudentData studentData) throws InvalidParametersException, EntityAlreadyExistsException {
		if (!studentData.isValid()) {
			throw new InvalidParametersException(studentData.getInvalidStateInfo());
		}

		accountsDb.createStudent(studentData);
	}
	

	//==========================================================================
	public AccountData getAccount(String googleId) {
		return accountsDb.getAccount(googleId);
	}
	
	public InstructorData getInstructor(String instructorId, String courseId) {
		return accountsDb.getInstructor(instructorId, courseId);
	}
	
	public List<AccountData> getInstructorAccounts() {
		return accountsDb.getInstructorAccounts();
	}
	
	public List<InstructorData> getAllInstructors() {
		return accountsDb.getAllInstructors();
	}
	
	public List<InstructorData> getInstructorsOfCourse(String courseId) {
		return accountsDb.getInstructorsByCourseId(courseId);
	}
	
	public List<InstructorData> getInstructorRolesForAccount(String googleId) {
		return accountsDb.getInstructorsByGoogleId(googleId);
	}
	
	public List<StudentData> getStudentListForCourse(String courseId) {
		return accountsDb.getStudentListForCourse(courseId);
	}
	
	public List<StudentData> getUnregisteredStudentListForCourse(String courseId) {
		return accountsDb.getUnregisteredStudentListForCourse(courseId);
	}
	
	public StudentData getStudent(String courseId, String email) {
		return accountsDb.getStudent(courseId, email);
	}
	
	public StudentData getStudentByGoogleId(String courseId, String googleId) {
		return accountsDb.getStudentByGoogleId(courseId, googleId);
	}
	
	public ArrayList<StudentData> getStudentsWithGoogleId(String googleId) {
		List<StudentData> students = accountsDb.getStudentsWithGoogleId(googleId);
		ArrayList<StudentData> returnList = new ArrayList<StudentData>();
		for (StudentData s : students) {
			returnList.add(s);
		}
		return returnList;
	}


	//==========================================================================
	public void updateAccount(AccountData account) throws InvalidParametersException {
		if (!account.isValid()) {
			throw new InvalidParametersException(account.getInvalidStateInfo());
		}
		accountsDb.updateAccount(account);
	}
	
	public void makeAccountInstructor(String googleId) {
		accountsDb.makeAccountInstructor(googleId);
	}
	
	public void makeAccountNonInstructor(String instructorId) {
		accountsDb.makeAccountNonInstructor(instructorId);
	}
	
	public void updateInstructor(InstructorData instructor) throws InvalidParametersException {
		if (!instructor.isValid()) {
			throw new InvalidParametersException(instructor.getInvalidStateInfo());
		}
		accountsDb.updateInstructor(instructor);
	}
	
	public void updateStudent(String originalEmail, StudentData student) {
		// Edit student uses KeepOriginal policy, where unchanged fields are set
		// as null
		// Hence, we can't do isValid() here.

		// TODO: make the implementation more defensive, e.g. duplicate email
		accountsDb.updateStudent(student.course, originalEmail, student.name, student.team, student.email, student.id, student.comments);	
	}
	
	public StudentData joinCourse(String key, String googleId) throws JoinCourseException {
		return accountsDb.joinCourse(key, googleId);
	}

		
	//==========================================================================
	public void deleteAccount(String googleId) {
		accountsDb.deleteInstructorsByGoogleId(googleId);
		accountsDb.deleteStudentsByGoogleId(googleId);
		accountsDb.deleteAccount(googleId);
	}

	public void deleteInstructor(String instructorId, String courseId) {
		accountsDb.deleteInstructor(instructorId, courseId);
	}

	public void deleteInstructor(String instructorId) {
		accountsDb.deleteInstructorsByGoogleId(instructorId);
		accountsDb.makeAccountNonInstructor(instructorId);
	}

	public void deleteStudent(String courseId, String studentEmail) {
		accountsDb.deleteStudent(courseId, studentEmail);
	}

}