package teammates.logic;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.InstructorData;
import teammates.storage.api.AccountsDb;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Accounts handles all operations related to a Teammates account.
 * @see Instructor
 * @see Student
 * 
 */
public class AccountsLogic {

	private static UserService userService;
	private static AccountsLogic instance = null;

	private static final AccountsDb accountsDb = new AccountsDb();

	/**
	 * Constructs an Accounts object. Initialises userService for Google User
	 * Service and obtains an instance of PersistenceManager class to handle
	 * datastore transactions.
	 */
	private AccountsLogic() {
		userService = UserServiceFactory.getUserService();
	}
	

	public static AccountsLogic inst() {
		if (instance == null)
			instance = new AccountsLogic();
		return instance;
	}

	/**
	 * See if the current user is authenticated
	 * 
	 * @return
	 */
	public boolean isLoggedOn() {
		return userService.getCurrentUser() != null;
	}


	/**
	 * Returns the name of the Instructor object given his googleID.
	 * 
	 * @param googleID
	 *            the instructor's Google ID (Precondition: Must not be null)
	 * 
	 * @return the name of the instructor
	 */
	public String getInstructorName(String googleID) {
		InstructorData instructor = accountsDb.getInstructor(googleID);

		return instructor.name;
	}

	/**
	 * Returns the login page to the user, or the designated redirect page if
	 * the user is already logged in.
	 * 
	 * @param redirectPage
	 *            the page to redirect the user (Precondition: Must not be null)
	 * 
	 * @return the login page or redirect page if the user is already logged in
	 */
	public String getLoginPage(String redirectPage) {
		// Check if user is already logged in
		User user = userService.getCurrentUser();

		if (user != null) {
			// Direct user to his chosen page
			return redirectPage;
		}

		else {
			// Direct user to Google Login first before chosen page
			return userService.createLoginURL(redirectPage);
		}
	}

	/**
	 * Returns the logout page to the user.
	 * 
	 * @param redirectPage
	 *            the page to redirect the user (Precondition: Must not be null)
	 * 
	 * @return the redirect page after logout
	 */
	public String getLogoutPage(String redirectPage) {
		return userService.createLogoutURL(redirectPage);
	}

	/**
	 * Returns the Google User object.
	 * 
	 * @return the user
	 */
	public User getUser() {		
		return userService.getCurrentUser();
	}

	/**
	 * Returns the Administrator status of the user.
	 * 
	 * @return <code>true</code> if the user is an administrator,
	 *         <code>false</code> otherwise.
	 */
	public boolean isAdministrator() {
		if (userService.isUserAdmin()) {
			return true;
		}

		return false;
	}

	/**
	 * Returns the Instructor status of the user.
	 * 
	 * @return <code>true</code> if the user is an instructor,
	 *         <code>false</code> otherwise.
	 */
	public boolean isInstructor() {

		User user = userService.getCurrentUser();
		
		if (user == null)
			return false;
		
		return accountsDb.isInstructor(user.getNickname());
	}
	
	
	
	
	public boolean isStudent(String googleId) {
		return accountsDb.getStudentsWithGoogleId(googleId).size()!=0;
	}
	

	
	public boolean isStudentExists(String courseId, String studentEmail) {
		return accountsDb.isStudentExists(courseId, studentEmail);
	}
	
	
	
	public AccountsDb getDb() {
		return accountsDb;
	}
	
	
}