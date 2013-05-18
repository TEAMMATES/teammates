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
		
		log.info("going to create account :\n"+accountToAdd.toString());
		
		if (!accountToAdd.isValid()) {
			throw new InvalidParametersException("Invalid parameter detected while adding account :" 
						+ accountToAdd.getInvalidStateInfo() + "\n" 
						+ "values received :\n"+ accountToAdd.toString());
		}
		accountsDb.createAccount(accountToAdd);
	}
	
	public void createInstructorAccount(String googleId, String courseId,
			String name, String email, String institute) 
					throws InvalidParametersException,	EntityAlreadyExistsException {
		
		googleId = sanitizeGoogleId(googleId);
		
		InstructorsLogic.inst().createInstructor(googleId, courseId, name, email);
		
		// Create the Account if it does not exist
		if (accountsDb.getAccount(googleId) == null) {
			createAccount(googleId, name, true, email, institute);
		} else {
			makeAccountInstructor(googleId);
		}
	}

	public AccountAttributes getAccount(String googleId) {
		return accountsDb.getAccount(googleId);
	}
	
	public boolean isAccountPresent(String googleId) {
		return accountsDb.getAccount(googleId) != null;
	}
	
	public boolean isAccountAnInstructor(String googleId) {
		AccountAttributes a = accountsDb.getAccount(googleId);
		return a == null ? false : a.isInstructor;
	}

	public List<AccountAttributes> getInstructorAccounts() {
		return accountsDb.getInstructorAccounts();
	}
	
	public String getCourseInstitute(String courseId) {
		CourseAttributes cd = new CoursesLogic().getCourse(courseId);
		List<InstructorAttributes> instructorList = InstructorsLogic.inst().getInstructorsForCourse(cd.id);
		
		Assumption.assertTrue("Course has no instructors: " + cd.id, !instructorList.isEmpty());
		// Retrieve institute field from the first instructor of the course
		AccountAttributes instructorAcc = accountsDb.getAccount(instructorList.get(0).googleId);
		
		Assumption.assertNotNull("Instructor has no account: " + instructorList.get(0).googleId, instructorAcc);
		return instructorAcc.institute;
	}

	public void updateAccount(AccountAttributes account) throws InvalidParametersException {
		if (!account.isValid()) {
			throw new InvalidParametersException(account.getInvalidStateInfo());
		}
		accountsDb.updateAccount(account);
	}
	
	public StudentAttributes joinCourse(String registrationKey, String googleId) 
			throws JoinCourseException {
		
		StudentAttributes student = StudentsLogic.inst().getStudentForRegistrationKey(registrationKey);
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
		try {
			StudentsLogic.inst().updateStudent(student.email, student);
		} catch (EntityDoesNotExistException e) {
			Assumption.fail("Student disappered while trying to register " + Common.stackTraceToString(e));
		}
		
		if (accountsDb.getAccount(googleId) == null) {
			createStudentAccount(student);
		}
		
		return student;
	}
	
	public void downgradeInstructorToStudentCascade(String googleId) {
		InstructorsLogic.inst().deleteInstructorsForGoogleId(googleId);
		makeAccountNonInstructor(googleId);
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
		InstructorsLogic.inst().deleteInstructorsForGoogleId(googleId);
		StudentsLogic.inst().deleteStudentsForGoogleId(googleId);
		accountsDb.deleteAccount(googleId); //TODO: shouldn't we use deleteAccountCascade here?
		//TODO: deal with orphan courses, submissions etc.
	}
	
	
	//TODO: move to a proper *Sanitizer class
	public static String sanitizeGoogleId(String rawGoogleId) {
		String sanitized = rawGoogleId.trim();
		// trim @gmail.com in ID field
		if (sanitized.toLowerCase().endsWith("@gmail.com")) {
			sanitized = sanitized.split("@")[0];
		}
		return sanitized;
	}
	
	private void createStudentAccount(StudentAttributes student) {
		AccountAttributes account = new AccountAttributes();
		account.googleId = student.id;
		account.email = student.email;
		account.name = student.name;
		account.isInstructor = false;
		account.institute = getCourseInstitute(student.course);
		accountsDb.createAccount(account);
	}


}