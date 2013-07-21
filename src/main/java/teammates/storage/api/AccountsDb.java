package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.common.util.Utils;
import teammates.storage.entity.Account;

/**
 * Handles CRUD Operations for accounts.
 * The API uses data transfer classes (i.e. *Attributes) instead of persistable classes.
 * 
 */
public class AccountsDb extends EntitiesDb {

	private static final Logger log = Utils.getLogger();
	
	/**
	 * Preconditions: 
	 * <br> * {@code accountToAdd} is not null and has valid data.
	 */
	public void createAccount(AccountAttributes accountToAdd) throws InvalidParametersException {
		// TODO: use createEntity once there is a proper way to add instructor accounts.
		try {
			createEntity(accountToAdd);
		} catch (EntityAlreadyExistsException e) {
			// We update the account instead if it already exists. This is due to how
			// adding of instructor accounts work.
			updateAccount(accountToAdd);
		}
	}
	
	/**
	 * Preconditions: 
	 * <br> * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public AccountAttributes getAccount(String googleId) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
	
		Account a = getAccountEntity(googleId);
	
		if (a == null) {
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
	public void updateAccount(AccountAttributes a) throws InvalidParametersException {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, a);
		
		if (!a.isValid()) {
			throw new InvalidParametersException(a.getInvalidityInfo());
		}
		
		Account accountToUpdate = getAccountEntity(a.googleId);
		//TODO: this should be an exception instead?
		Assumption.assertNotNull(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + a.googleId
				+ ThreadHelper.getCurrentThreadStack(), accountToUpdate);
		
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
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);
	
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
				&& (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
			ThreadHelper.waitBriefly();
			accountCheck = getAccountEntity(googleId);
			elapsedTime += ThreadHelper.WAIT_DURATION;
		}
		if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
			log.severe("Operation did not persist in time: deleteAccount->"
					+ googleId);
		}
		
		//TODO: Use the delete operation in the parent class instead.
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

	@Override
	protected Object getEntity(EntityAttributes entity) {
		return getAccountEntity(((AccountAttributes)entity).googleId);
	}
	

}

