package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.jdo.JDOHelper;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.Query;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.EntityAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.ThreadHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * Handles CRUD operations for accounts.
 *
 * @see Account
 * @see AccountAttributes
 */
public class AccountsDb extends EntitiesDb {

    private static final Logger log = Logger.getLogger();

    /**
     * Preconditions:
     * <br> * {@code accountToAdd} is not null and has valid data.
     */
    public void createAccount(AccountAttributes accountToAdd)
            throws InvalidParametersException {
        // TODO: use createEntity once there is a proper way to add instructor accounts.
        try {
            // this is for legacy code to be handled
            if (accountToAdd != null && accountToAdd.studentProfile == null) {
                accountToAdd.studentProfile = new StudentProfileAttributes();
                accountToAdd.studentProfile.googleId = accountToAdd.googleId;
            }
            createEntity(accountToAdd);
        } catch (EntityAlreadyExistsException e) {
            // We update the account instead if it already exists. This is due to how
            // adding of instructor accounts work.
            try {
                updateAccount(accountToAdd, true);
            } catch (EntityDoesNotExistException edne) {
                // This situation is not tested as replicating such a situation is
                // difficult during testing
                Assumption.fail("Entity found be already existing and not existing simultaneously");
            }
        }
    }

    /* This function is used for persisting data bundle in testing process */
    public void createAccounts(Collection<AccountAttributes> accountsToAdd, boolean updateAccount)
            throws InvalidParametersException {

        List<EntityAttributes> accountsToUpdate = createEntities(accountsToAdd);
        if (updateAccount) {
            for (EntityAttributes entity : accountsToUpdate) {
                AccountAttributes account = (AccountAttributes) entity;
                try {
                    updateAccount(account, true);
                } catch (EntityDoesNotExistException e) {
                    // This situation is not tested as replicating such a situation is
                    // difficult during testing
                    Assumption.fail("Entity found be already existing and not existing simultaneously");
                }
            }
        }
    }

    /**
     * Gets the data transfer version of the account. Does not retrieve the profile
     * if the given parameter is false<br>
     * Preconditions:
     * <br> * All parameters are non-null.
     * @return Null if not found.
     */
    public AccountAttributes getAccount(String googleId, boolean retrieveStudentProfile) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        Account a = getAccountEntity(googleId, retrieveStudentProfile);

        if (a == null) {
            return null;
        }
        closePm();

        return new AccountAttributes(a);
    }

    public AccountAttributes getAccount(String googleId) {
        return getAccount(googleId, false);
    }

    /**
     * Returns {@link AccountAttributes} objects for all accounts with instructor privileges.
     *         Returns an empty list if no such accounts are found.
     */
    public List<AccountAttributes> getInstructorAccounts() {
        Query q = getPm().newQuery(Account.class);
        q.setFilter("isInstructor == true");

        @SuppressWarnings("unchecked")
        List<Account> accountsList = (List<Account>) q.execute();

        List<AccountAttributes> instructorsAccountData = new ArrayList<AccountAttributes>();

        for (Account a : accountsList) {
            if (!JDOHelper.isDeleted(a)) {
                instructorsAccountData.add(new AccountAttributes(a));
            }
        }

        return instructorsAccountData;
    }

    /**
     * Preconditions:
     * <br> * {@code accountToAdd} is not null and has valid data.
     */
    public void updateAccount(AccountAttributes a, boolean updateStudentProfile)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, a);

        if (!a.isValid()) {
            throw new InvalidParametersException(a.getInvalidityInfo());
        }

        Account accountToUpdate = getAccountEntity(a.googleId, updateStudentProfile);

        if (accountToUpdate == null) {
            throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT_ACCOUNT + a.googleId
                + ThreadHelper.getCurrentThreadStack());
        }

        a.sanitizeForSaving();
        accountToUpdate.setName(a.name);
        accountToUpdate.setEmail(a.email);
        accountToUpdate.setIsInstructor(a.isInstructor);
        accountToUpdate.setInstitute(a.institute);

        if (updateStudentProfile) {
            StudentProfileAttributes existingProfile = new StudentProfileAttributes(accountToUpdate.getStudentProfile());
            a.studentProfile.modifiedDate = existingProfile.modifiedDate;

            // if the student profile has changed then update the store
            // this is to maintain integrity of the modified date.
            if (!existingProfile.toString().equals(a.studentProfile.toString())) {
                accountToUpdate.setStudentProfile((StudentProfile) a.studentProfile.toEntity());
            }
        }
        log.info(a.getBackupIdentifier());
        closePm();
    }

    public void updateAccount(AccountAttributes a)
            throws InvalidParametersException, EntityDoesNotExistException {
        if (a != null && a.studentProfile == null) {
            a.studentProfile = new StudentProfileAttributes();
            a.studentProfile.googleId = a.googleId;
        }
        updateAccount(a, false);
    }

    /**
     * Note: This is a non-cascade delete. <br>
     *   <br> Fails silently if there is no such account.
     * <br> Preconditions:
     * <br> * {@code googleId} is not null.
     */
    public void deleteAccount(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        AccountAttributes accountToDelete = getAccount(googleId, true);

        if (accountToDelete == null) {
            return;
        }

        if (!accountToDelete.studentProfile.pictureKey.isEmpty()) {
            deletePicture(new BlobKey(accountToDelete.studentProfile.pictureKey));
        }
        deleteEntity(accountToDelete);
        closePm();
    }

    public void deleteAccounts(Collection<AccountAttributes> accounts) {

        for (AccountAttributes accountToDelete : accounts) {
            if (!accountToDelete.studentProfile.pictureKey.isEmpty()) {
                deletePicture(new BlobKey(accountToDelete.studentProfile.pictureKey));
            }
        }
        deleteEntities(accounts);
        closePm();
    }

    private Account getAccountEntity(String googleId, boolean retrieveStudentProfile) {

        try {
            Key key = KeyFactory.createKey(Account.class.getSimpleName(), googleId);
            Account account = getPm().getObjectById(Account.class, key);

            if (JDOHelper.isDeleted(account)) {
                return null;
            } else if (retrieveStudentProfile && account.getStudentProfile() == null) {
                // This situation cannot be reproduced and hence not tested
                // This only happens when existing data in the store do not have a profile
                account.setStudentProfile(new StudentProfile(account.getGoogleId()));
            }

            return account;
        } catch (IllegalArgumentException iae) {
            return null;
        } catch (JDOObjectNotFoundException je) {
            return null;
        }
    }

    private Account getAccountEntity(String googleId) {
        return getAccountEntity(googleId, false);
    }

    @Override
    protected Object getEntity(EntityAttributes entity) {
        return getAccountEntity(((AccountAttributes) entity).googleId);
    }

    @Override
    protected QueryWithParams getEntityKeyOnlyQuery(EntityAttributes attributes) {
        Class<?> entityClass = Account.class;
        String primaryKeyName = Account.PRIMARY_KEY_NAME;
        AccountAttributes aa = (AccountAttributes) attributes;
        String id = aa.googleId;

        Query q = getPm().newQuery(entityClass);
        q.declareParameters("String idParam");
        q.setFilter(primaryKeyName + " == idParam");

        return new QueryWithParams(q, new Object[] {id}, primaryKeyName);
    }
}
