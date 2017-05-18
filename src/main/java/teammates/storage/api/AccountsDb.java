package teammates.storage.api;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.QueryKeys;

import static com.googlecode.objectify.ObjectifyService.ofy;

import teammates.common.datatransfer.attributes.AccountAttributes;
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
public class AccountsDb extends OfyEntitiesDb<Account, AccountAttributes> {

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

        List<AccountAttributes> accountsToUpdate = createEntities(accountsToAdd);
        if (updateAccount) {
            for (AccountAttributes entity : accountsToUpdate) {
                AccountAttributes account = entity;
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
     * Gets the data transfer version of the account. With the new Objectify API, the StudentProfile
     * is retrieved when it is first accessed via its getter. Hence, the parameter retrieveStudentProfile
     * is now obsolete, and not used (but maintained here for backward compatibility).
     * Preconditions:
     * <br> * All parameters are non-null.
     * @return Null if not found.
     */
    @Deprecated
    public AccountAttributes getAccount(String googleId, boolean retrieveStudentProfile) {
        return getAccount(googleId);
    }

    public AccountAttributes getAccount(String googleId) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, googleId);

        Account a = getAccountEntity(googleId);

        if (a == null) {
            return null;
        }

        return new AccountAttributes(a);
    }

    /**
     * Returns {@link AccountAttributes} objects for all accounts with instructor privileges.
     *         Returns an empty list if no such accounts are found.
     */
    public List<AccountAttributes> getInstructorAccounts() {
        List<Account> accountsList = ofy().load().type(Account.class).filter("isInstructor =", true).list();

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
    public void updateAccount(AccountAttributes a, boolean updateStudentProfile)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, a);

        if (!a.isValid()) {
            throw new InvalidParametersException(a.getInvalidityInfo());
        }

        Account accountToUpdate = getAccountEntity(a.googleId);

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
            StudentProfile existingStudentProfile = accountToUpdate.getStudentProfile();
            if (existingStudentProfile == null) {
                existingStudentProfile = new StudentProfile(a.studentProfile.googleId);
            }

            StudentProfileAttributes existingProfile = new StudentProfileAttributes(existingStudentProfile);
            a.studentProfile.modifiedDate = existingProfile.modifiedDate;

            // if the student profile has changed then update the store
            // this is to maintain integrity of the modified date.
            if (!existingProfile.toString().equals(a.studentProfile.toString())) {
                StudentProfile updatedStudentProfile = a.studentProfile.toEntity();
                accountToUpdate.setStudentProfile(updatedStudentProfile);
                ofy().save().entity(updatedStudentProfile).now();
            }
        }
        log.info(a.getBackupIdentifier());
        ofy().save().entity(accountToUpdate).now();
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
    }

    public void deleteAccounts(Collection<AccountAttributes> accounts) {

        for (AccountAttributes accountToDelete : accounts) {
            if (!accountToDelete.studentProfile.pictureKey.isEmpty()) {
                deletePicture(new BlobKey(accountToDelete.studentProfile.pictureKey));
            }
        }
        deleteEntities(accounts);
    }

    private Account getAccountEntity(String googleId) {
        Account account = ofy().load().type(Account.class).id(googleId).now();
        if (account == null) {
            return null;
        }

        return account;
    }

    @Override
    protected Account getEntity(AccountAttributes entity) {
        return getAccountEntity(entity.googleId);
    }

    @Override
    public boolean hasEntity(AccountAttributes attributes) {
        Key<Account> keyToFind = Key.create(Account.class, attributes.googleId);
        QueryKeys<Account> keysOnlyQuery = ofy().load().type(Account.class).filterKey(keyToFind).keys();
        return keysOnlyQuery.first().now() != null;
    }
}
