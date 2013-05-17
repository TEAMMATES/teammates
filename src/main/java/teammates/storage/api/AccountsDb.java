package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.AccountAttributes;
import teammates.storage.datastore.Datastore;
import teammates.storage.entity.Account;

/**
 * Handles CRUD Operations for accounts.
 * The API uses data transfer classes (i.e. *Attributes) instead of presistable classes.
 * 
 */
public class AccountsDb {
	public static final String ERROR_UPDATE_NON_EXISTENT_ACCOUNT = "Trying to update non-existent Account: ";
	public static final String ERROR_UPDATE_NON_EXISTENT_STUDENT = "Trying to update non-existent Student: ";
	public static final String ERROR_CREATE_ACCOUNT_ALREADY_EXISTS = "Trying to create an Account that exists: ";
	public static final String ERROR_CREATE_INSTRUCTOR_ALREADY_EXISTS = "Trying to create a Instructor that exists: ";
	public static final String ERROR_CREATE_STUDENT_ALREADY_EXISTS = "Trying to create a Student that exists: ";
	public static final String ERROR_TRYING_TO_MAKE_NON_EXISTENT_ACCOUNT_AN_INSTRUCTOR = "Trying to make an non-existent account an Instructor :";
	
	private static final Logger log = Common.getLogger();

	/**
	 * Preconditions: 
	 * <br> * {@code accountToAdd} is not null and has valid data.
	 */
	public void createAccount(AccountAttributes accountToAdd) {
		
		//TODO: why doesn't this throw EntityAlreadyExistsException?
		
		Assumption.assertNotNull(
				Common.ERROR_DBLEVEL_NULL_INPUT, accountToAdd);
		Assumption.assertTrue(
				"Invalid object received as a parameter :" + Common.toString(accountToAdd.getInvalidStateInfo()) + accountToAdd.toString(),
				accountToAdd.isValid());
		
		Account newAccount = accountToAdd.toEntity();
		getPM().makePersistent(newAccount);
		getPM().flush();

		// Wait for the operation to persist
		int elapsedTime = 0;
		Account accountCheck = getAccountEntity(accountToAdd.googleId);
		while ((accountCheck == null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			accountCheck = getAccountEntity(accountToAdd.googleId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: createAccount->"
					+ accountToAdd.googleId);
		}
	}
	
	/**
	 * Preconditions: 
	 * <br> * {@code accountsToAdd} is not null and 
	 * contains {@link AccountAttributes} objects with valid data.
	 */
	public void createAccounts(List<AccountAttributes> accountsToAdd) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, accountsToAdd);
		
		List<Account> accounts = new ArrayList<Account>();
		for (AccountAttributes ad : accountsToAdd) {
			Assumption.assertTrue(
					"Invalid object received as a parameter" + ad.getInvalidStateInfo().toString(),
					ad.isValid());
			accounts.add(ad.toEntity());
		}
		
		getPM().makePersistentAll(accounts);
		getPM().flush();
	}
	
	/**
	 * Preconditions: 
	 * <br> * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public AccountAttributes getAccount(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
	
		Account a = getAccountEntity(googleId);
	
		if (a == null) {
			log.info("Trying to get non-existent Account: " + googleId);
			return null;
		}
	
		return new AccountAttributes(a);
	}


	/**
	 * @return {@link AccountAttribute} objects for all accounts with instructor privileges.
	 *   Returns an empty list if no such accounts are found.
	 */
	public List<AccountAttributes> getInstructorAccounts() {
		Query q = getPM().newQuery(Account.class);
		q.setFilter("isInstructor == true");
		
		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) q.execute();
		
		List<AccountAttributes> instructorsAccountData = new ArrayList<AccountAttributes>();
		
		for (Account a : accountsList) {
			instructorsAccountData.add(new AccountAttributes(a));
		}
		
		return instructorsAccountData;
	}

	/**
	 * Preconditions: 
	 * <br> * {@code accountToAdd} is not null and has valid data.
	 */
	public void updateAccount(AccountAttributes a) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, a);
		Assumption.assertTrue(
				"Invalid object received as a parameter" + a.getInvalidStateInfo().toString(),
				a.isValid());
		
		Account accountToUpdate = getAccountEntity(a.googleId);
		//TODO: this should be an exception instead?
		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + a.googleId
				+ Common.getCurrentThreadStack(), accountToUpdate);
		
		accountToUpdate.setName(a.name);
		accountToUpdate.setEmail(a.email);
		accountToUpdate.setIsInstructor(a.isInstructor);
		accountToUpdate.setInstitute(a.institute);
		
		getPM().close();
	}

	/**
	 * Note: This is a non-cascade delete. <br>
	 *   <br> Fails silently if there is no such account.
	 * <br> Preconditions: 
	 * <br> * {@code googleId} is not null.
	 */
	public void deleteAccount(String googleId) {
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, googleId);
	
		Account accountToDelete = getAccountEntity(googleId);
	
		if (accountToDelete == null) {
			return;
		}
	
		getPM().deletePersistent(accountToDelete);
		getPM().flush();
	
		// Wait for the operation to persist
		int elapsedTime = 0;
		Account accountCheck = getAccountEntity(googleId);
		while ((accountCheck != null)
				&& (elapsedTime < Common.PERSISTENCE_CHECK_DURATION)) {
			Common.waitBriefly();
			accountCheck = getAccountEntity(googleId);
			elapsedTime += Common.WAIT_DURATION;
		}
		if (elapsedTime == Common.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteAccount->"
					+ googleId);
		}
		
		//TODO: the above piece of code is duplicated in many places. 
		//  Eliminate using anonymous classes? e.g., similar to the way sorting works
	}

	//TODO: add an updateStudent(StudentAttributes) version and make the above private
	
	private PersistenceManager getPM() {
		return Datastore.getPersistenceManager();
	}


	private Account getAccountEntity(String googleId) {
		
		Query q = getPM().newQuery(Account.class);
		q.declareParameters("String googleIdParam");
		q.setFilter("googleId == googleIdParam");
		
		@SuppressWarnings("unchecked")
		List<Account> accountsList = (List<Account>) q.execute(googleId);
		
		if (accountsList.isEmpty() || JDOHelper.isDeleted(accountsList.get(0))) {
			return null;
		}
	
		return accountsList.get(0);
	}
	

}

