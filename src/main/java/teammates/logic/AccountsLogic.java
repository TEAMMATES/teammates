package teammates.logic;

import java.util.List;

import teammates.common.Common;
import teammates.common.datatransfer.AccountData;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.InstructorData;
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

	public AccountsDb getDb() {
		return accountsDb;
	}
	
	
	//==========================================================================
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
	public List<AccountData> getInstructorAccounts() {
		return accountsDb.getInstructorAccounts();
	}
		
}