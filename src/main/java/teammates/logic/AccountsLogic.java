package teammates.logic;

import java.util.List;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;

/**
 * Handles  operations related to accounts.
 * This class does the field validation and sanitization before 
 * passing values to the Storage layer.
 */
public class AccountsLogic {
	//The API of this class doesn't have header comments because it sits behind
	//  the API of the logic class. Those who use this class is expected to be
	//  familiar with the its code and Logic's code. Hence, no need for header 
	//  comments.
	
	//TODO: add sanitization to this class.
	
	private static AccountsLogic instance = null;
	private static final AccountsDb accountsDb = new AccountsDb();
	
	private static Logger log = Common.getLogger();
	
	public static AccountsLogic inst() {
		if (instance == null)
			instance = new AccountsLogic();
		return instance;
	}
	
	@SuppressWarnings("unused")
	private void ____ACCOUNT_related_______________________________________() {
	}
	
	public void createAccount(String googleId, String name, 
			boolean isInstructor, String email, String institute) 
					throws InvalidParametersException, EntityAlreadyExistsException {
		
		//TODO: make this take AccountAttributes as the parameter
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

	public AccountAttributes getAccount(String googleId) {
		return accountsDb.getAccount(googleId);
	}
	
	public boolean isAccountPresent(String googleId) {
		return accountsDb.getAccount(googleId) != null;
	}

	public List<AccountAttributes> getInstructorAccounts() {
		return accountsDb.getInstructorAccounts();
	}

	public void updateAccount(AccountAttributes account) throws InvalidParametersException {
		if (!account.isValid()) {
			throw new InvalidParametersException(account.getInvalidStateInfo());
		}
		accountsDb.updateAccount(account);
	}

	public void makeAccountNonInstructor(String googleId) {
		AccountAttributes account = accountsDb.getAccount(googleId);
		if (account != null) {
			account.isInstructor = false;
			accountsDb.updateAccount(account);
		}else {
			log.warning("Accounts logic trying to modify non-existent account a non-instructor :" + googleId );
		}
	}

	public void makeAccountInstructor(String googleId) {
		AccountAttributes account = accountsDb.getAccount(googleId);
		if (account != null) {
		account.isInstructor = true;
		accountsDb.updateAccount(account);
		} else {
			log.warning("Accounts logic trying to modify non-existent account an instructor:" + googleId );
		}
	}

	public void deleteAccountCascade(String googleId) {
		accountsDb.deleteInstructorsForGoogleId(googleId);
		accountsDb.deleteStudentsForGoogleId(googleId);
		accountsDb.deleteAccount(googleId);
		//TODO: deal with orphan courses
	}

	
	
	//TODO: have a deleteStudentCascade here?

	@SuppressWarnings("unused")
	private void ____INSTRUCTOR_related____________________________________() {
	}
	
	public void createInstructor(String googleId, String courseId, String name,
			String email, String institute) throws InvalidParametersException, EntityAlreadyExistsException {
		
		googleId = sanitizeGoogleId(googleId);
		
		InstructorAttributes instructorToAdd = new InstructorAttributes(googleId, courseId, name, email);
		if (!instructorToAdd.isValid()) {
			throw new InvalidParametersException(instructorToAdd.getInvalidStateInfo());
		}
	
		// Create the Account if it does not exist
		if (accountsDb.getAccount(googleId)==null) {
			createAccount(googleId, name, true, email, institute);
		} else {
			makeAccountInstructor(googleId);
		}
	
		accountsDb.createInstructor(instructorToAdd);
	}

	public InstructorAttributes getInstructorForEmail(String courseId, String email) {
		return accountsDb.getInstructorForEmail(courseId, email);
	}

	public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
		return accountsDb.getInstructorForGoogleId(courseId, googleId);
	}

	public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
		return accountsDb.getInstructorsForCourse(courseId);
	}

	public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {
		return accountsDb.getInstructorsForGoogleId(googleId);
	}

	/**
	 * @deprecated Not scalable. Use only for admin features.
	 */
	@Deprecated 
	public List<InstructorAttributes> getAllInstructors() {
		return accountsDb.getAllInstructors();
	}

	public boolean isInstructor(String googleId) {
		AccountAttributes a = accountsDb.getAccount(googleId);
		return a == null ? false : a.isInstructor;
	}

	public boolean isInstructorOfCourse(String instructorId, String courseId) {
		return accountsDb.getInstructorForGoogleId(courseId, instructorId)!=null;
	}

	public void updateInstructor(InstructorAttributes instructor) 
			throws InvalidParametersException {
		if (!instructor.isValid()) {
			throw new InvalidParametersException(instructor.getInvalidStateInfo());
		}
		accountsDb.updateInstructor(instructor);
	}

	public void deleteInstructor(String courseId, String googleId) {
		accountsDb.deleteInstructor(courseId, googleId);
	}

	public void deleteInstructorsForGoogleId(String googleId) {
		accountsDb.deleteInstructorsForGoogleId(googleId);
		makeAccountNonInstructor(googleId);
	}

	@SuppressWarnings("unused")
	private void ____PRIVATE_methods____________________________________() {
	}

	//TODO: move to a proper *Sanitizer class
	private String sanitizeGoogleId(String rawGoogleId) {
		String sanitized = rawGoogleId.trim();
		// trim @gmail.com in ID field
		if (sanitized.toLowerCase().endsWith("@gmail.com")) {
			sanitized = sanitized.split("@")[0];
		}
		return sanitized;
	}

}