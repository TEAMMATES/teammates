package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.appengine.api.blobstore.BlobKey;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.LoadType;
import com.googlecode.objectify.cmd.QueryKeys;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.StudentProfile;

/**
 * Handles CRUD operations for accounts.
 *
 * @see Account
 * @see AccountAttributes
 */
public class AccountsDb extends EntitiesDb<Account, AccountAttributes> {
    private ProfilesDb profilesDb = new ProfilesDb();

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
                accountToAdd.studentProfile = StudentProfileAttributes.builder(accountToAdd.googleId).build();
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
                Assumption.fail("Account found to be already existing and not existing simultaneously");
            }
        }

        try {
            profilesDb.createEntity(accountToAdd.studentProfile);
        } catch (EntityAlreadyExistsException e) {
            try {
                profilesDb.updateStudentProfile(accountToAdd.studentProfile);
            } catch (EntityDoesNotExistException edne) {
                // This situation is not tested as replicating such a situation is
                // difficult during testing
                Assumption.fail("StudentProfile found to be already existing and not existing simultaneously");
            }
        }
    }

    @Override
    public List<Account> createEntitiesDeferred(Collection<AccountAttributes> accountsToAdd)
            throws InvalidParametersException {
        List<StudentProfileAttributes> profilesToAdd = new LinkedList<>();
        for (AccountAttributes accountToAdd : accountsToAdd) {
            profilesToAdd.add(accountToAdd.studentProfile);
        }
        profilesDb.createEntitiesDeferred(profilesToAdd);
        return super.createEntitiesDeferred(accountsToAdd);
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
        return googleId.isEmpty() ? null : makeAttributesOrNull(getAccountEntity(googleId, retrieveStudentProfile));
    }

    public AccountAttributes getAccount(String googleId) {
        return getAccount(googleId, false);
    }

    /**
     * Returns {@link AccountAttributes} objects for all accounts with instructor privileges.
     *         Returns an empty list if no such accounts are found.
     */
    public List<AccountAttributes> getInstructorAccounts() {
        return makeAttributes(
                load().filter("isInstructor =", true).list());
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
            StudentProfile existingProfile = accountToUpdate.getStudentProfile();
            if (existingProfile == null) {
                existingProfile = new StudentProfile(a.studentProfile.googleId);
            }

            StudentProfileAttributes existingProfileAttributes = StudentProfileAttributes.valueOf(existingProfile);
            a.studentProfile.modifiedDate = existingProfileAttributes.modifiedDate;

            // if the student profile has changed then update the store
            // this is to maintain integrity of the modified date.
            if (!existingProfileAttributes.toString().equals(a.studentProfile.toString())) {
                StudentProfile updatedProfile = a.studentProfile.toEntity();
                accountToUpdate.setStudentProfile(updatedProfile);
                profilesDb.saveEntity(updatedProfile);
            }
        }
        saveEntity(accountToUpdate, a);
    }

    public void updateAccount(AccountAttributes a)
            throws InvalidParametersException, EntityDoesNotExistException {
        if (a != null && a.studentProfile == null) {
            a.studentProfile = StudentProfileAttributes.builder(a.googleId).build();
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

        Account accountToDelete = getAccountEntity(googleId, true);

        if (accountToDelete == null) {
            return;
        }

        StudentProfile studentProfile = accountToDelete.getStudentProfile();
        if (studentProfile != null) {
            BlobKey pictureKey = studentProfile.getPictureKey();
            if (!pictureKey.getKeyString().isEmpty()) {
                deletePicture(pictureKey);
            }
            profilesDb.deleteEntityDirect(studentProfile);
        }

        deleteEntityDirect(accountToDelete);
    }

    public void deleteAccounts(Collection<AccountAttributes> accounts) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, accounts);

        for (AccountAttributes accountToDelete : accounts) {
            deleteAccount(accountToDelete.googleId);
        }
    }

    private Account getAccountEntity(String googleId, boolean retrieveStudentProfile) {
        Account account = load().id(googleId).now();
        if (account == null) {
            return null;
        }

        account.setIsStudentProfileEnabled(retrieveStudentProfile);

        return account;
    }

    private Account getAccountEntity(String googleId) {
        return getAccountEntity(googleId, false);
    }

    @Override
    protected LoadType<Account> load() {
        return ofy().load().type(Account.class);
    }

    @Override
    protected Account getEntity(AccountAttributes entity) {
        return getAccountEntity(entity.googleId);
    }

    @Override
    protected QueryKeys<Account> getEntityQueryKeys(AccountAttributes attributes) {
        Key<Account> keyToFind = Key.create(Account.class, attributes.googleId);
        return load().filterKey(keyToFind).keys();
    }

    @Override
    protected AccountAttributes makeAttributes(Account entity) {
        Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, entity);

        return AccountAttributes.valueOf(entity);
    }
}
