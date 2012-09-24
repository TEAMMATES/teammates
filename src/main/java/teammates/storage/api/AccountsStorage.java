package teammates.storage.api;

import teammates.common.Common;
import teammates.common.datatransfer.StudentData;
import teammates.common.datatransfer.CoordData;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * Accounts handles all operations related to a Teammates account.
 * @see Coordinator
 * @see Student
 * 
 */
public class AccountsStorage {

	private static UserService userService;
	private static AccountsStorage instance = null;

	private static final AccountsDb accountsDb = new AccountsDb();

	/**
	 * Constructs an Accounts object. Initialises userService for Google User
	 * Service and obtains an instance of PersistenceManager class to handle
	 * datastore transactions.
	 */
	private AccountsStorage() {
		userService = UserServiceFactory.getUserService();
	}
	

	public static AccountsStorage inst() {
		if (instance == null)
			instance = new AccountsStorage();
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
	 * Returns the name of the Coordinator object given his googleID.
	 * 
	 * @param googleID
	 *            the coordinator's Google ID (Precondition: Must not be null)
	 * 
	 * @return the name of the coordinator
	 */
	public String getCoordinatorName(String googleID) {
		CoordData coordinator = accountsDb.getCoord(googleID);

		return coordinator.name;
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
	 * Returns the Coordinator status of the user.
	 * 
	 * @return <code>true</code> if the user is an coordinator,
	 *         <code>false</code> otherwise.
	 */
	public boolean isCoord() {

		User user = userService.getCurrentUser();
		
		if (user == null)
			return false;
		
		return accountsDb.isCoord(user.getNickname());
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