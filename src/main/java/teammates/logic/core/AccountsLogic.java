package teammates.logic.core;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;
import teammates.storage.entity.Account;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Student;
import teammates.storage.entity.User;

/**
 * Handles operations related to accounts.
 *
 * @see Account
 * @see AccountsDb
 */
public final class AccountsLogic {

    private static final AccountsLogic instance = new AccountsLogic();

    private AccountsDb accountsDb;

    private UsersLogic usersLogic;

    private AccountsLogic() {
        // prevent initialization
    }

    void initLogicDependencies(AccountsDb accountsDb,
            UsersLogic usersLogic) {
        this.accountsDb = accountsDb;
        this.usersLogic = usersLogic;
    }

    public static AccountsLogic inst() {
        return instance;
    }

    /**
     * Gets an account.
     */
    public Account getAccount(UUID id) {
        assert id != null;
        return accountsDb.getAccount(id);
    }

    /**
     * Gets an account by googleId.
     */
    public Account getAccountForGoogleId(String googleId) {
        assert googleId != null;

        return accountsDb.getAccountByGoogleId(googleId);
    }

    /**
     * Gets accounts associated with email.
     */
    public List<Account> getAccountsForEmail(String email) {
        assert email != null;

        return accountsDb.getAccountsByEmail(email);
    }

    /**
     * Creates and returns an account for the given email if it does not exist,
     * otherwise just return the existing account.
     *
     * @param issuer the issuer of the account
     * @param subject the subject of the account
     * @param email the email of the account
     * @return the created or existing account
     */
    public Account createOrGetAccount(String issuer, String subject, String email) {
        assert issuer != null;
        assert subject != null;
        assert email != null;

        String googleId = email;
        // TODO: Fetch account by issuer + subject
        Account account = getAccountForGoogleId(googleId);
        if (account != null) {
            return account;
        }

        try {
            return createAccount(issuer, subject, email, googleId);
        } catch (EntityAlreadyExistsException e) {
            // This should not happen.
            throw new IllegalStateException("Failed to create existing account for email: " + email, e);
        } catch (InvalidParametersException e) {
            throw new IllegalStateException("Failed to create account with invalid parameters: " + email, e);
        }
    }

    /**
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException   if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the
     *                                      database.
     */
    public Account createAccount(String issuer, String subject, String email, String googleId)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert issuer != null;
        assert subject != null;
        assert email != null;
        assert googleId != null;
        // TODO: Account name will be removed, use a generic "User" for now.
        // googleId will be removed as well.
        Account account = new Account(googleId, issuer, subject, "User", email);
        return validateThenPersistAccount(account);
    }

    private Account validateThenPersistAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;

        validateAccount(account);

        if (getAccountForGoogleId(account.getGoogleId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, account.toString()));
        }

        return accountsDb.persistAccount(account);
    }

    /**
     * Deletes account associated with the {@code googleId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccount(String googleId) {
        Account account = getAccountForGoogleId(googleId);
        if (account == null) {
            return;
        }

        accountsDb.deleteAccount(account);
    }

    /**
     * Deletes account and all users associated with the {@code googleId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccountCascade(String googleId) {
        Account account = getAccountForGoogleId(googleId);
        if (account == null) {
            return;
        }

        List<User> usersToDelete = usersLogic.getAllUsersByGoogleId(googleId);

        for (User user : usersToDelete) {
            usersLogic.deleteUser(user);
        }

        accountsDb.deleteAccount(account);
    }

    /**
     * Makes the user join the course, i.e. associate the account to the user.
     */
    public User joinCourse(String registrationKey, Account account)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        Objects.requireNonNull(account);
        Objects.requireNonNull(registrationKey);

        User user = validateJoinRequest(registrationKey, account.getGoogleId());
        assert user.getAccount() == null;
        user.setAccount(account);
        return user;
    }

    private User validateJoinRequest(String registrationKey, String googleId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        User user = usersLogic.getUserByRegistrationKey(registrationKey);

        if (user == null) {
            throw new EntityDoesNotExistException("No user with given registration key: " + registrationKey);
        }

        if (user.isRegistered()) {
            throw new EntityAlreadyExistsException(
                    "User has already joined course");
        }

        validateNonExistingLinkedUserInCourse(user, googleId);

        return user;
    }

    private void validateAccount(Account account) throws InvalidParametersException {
        if (!account.isValid()) {
            throw new InvalidParametersException(account.getInvalidityInfo());
        }
    }

    private void validateNonExistingLinkedUserInCourse(User user, String googleId)
            throws EntityAlreadyExistsException {
        User existingLinkedUser;
        if (user instanceof Student) {
            existingLinkedUser = usersLogic.getStudentByGoogleId(user.getCourseId(), googleId);
        } else if (user instanceof Instructor) {
            existingLinkedUser = usersLogic.getInstructorByGoogleId(user.getCourseId(), googleId);
        } else {
            throw new IllegalStateException("Unknown user type: " + user.getClass().getName());
        }

        if (existingLinkedUser != null) {
            throw new EntityAlreadyExistsException(
                    "This account is already associated with another " + user.getClass().getSimpleName().toLowerCase());
        }
    }
}
