package teammates.logic.core;

import static teammates.common.util.Const.ERROR_CREATE_ENTITY_ALREADY_EXISTS;

import java.util.Objects;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.Provider;
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
        Objects.requireNonNull(id);
        return accountsDb.getAccount(id);
    }

    /**
     * Gets an account by auth identity.
     */
    public Account getAccountForAuthIdentity(Provider provider, String subject, @Nullable String tenantId) {
        Objects.requireNonNull(provider);
        Objects.requireNonNull(subject);

        return accountsDb.getAccountByAuthIdentity(provider, subject, tenantId);
    }

    /**
     * Creates and returns an account for the given email if it does not exist,
     * otherwise just return the existing account.
     *
     * @param provider the provider of the account
     * @param subject the subject of the account
     * @param tenantId the tenant ID of the account
     * @param email the email of the account
     * @return the created or existing account
     */
    public Account createOrGetAccount(Provider provider, String subject, @Nullable String tenantId, String email) {
        Objects.requireNonNull(provider);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(email);

        Account account = new Account(provider, subject, tenantId, "User", email);

        try {
            validateAccount(account);
            return accountsDb.upsertAccount(account);
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
    public Account createAccount(Provider provider, String subject, @Nullable String tenantId, String email)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Objects.requireNonNull(provider);
        Objects.requireNonNull(subject);
        Objects.requireNonNull(email);
        // TODO: Account name will be removed, use a generic "User" for now.
        // googleId will be removed as well.
        Account account = new Account(provider, subject, tenantId, "User", email);
        return validateThenPersistAccount(account);
    }

    private Account validateThenPersistAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;

        validateAccount(account);

        if (getAccountForAuthIdentity(account.getProvider(), account.getSubject(), account.getTenantId()) != null) {
            throw new EntityAlreadyExistsException(String.format(ERROR_CREATE_ENTITY_ALREADY_EXISTS, account.toString()));
        }

        return accountsDb.persistAccount(account);
    }

    /**
     * Deletes account associated with the {@code accountId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccount(UUID accountId) {
        Account account = getAccount(accountId);
        if (account == null) {
            return;
        }

        accountsDb.removeAccount(account);
    }

    /**
     * Makes the user join the course, i.e. associate the account to the user.
     */
    public User joinCourse(String registrationKey, Account account)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        Objects.requireNonNull(account);
        Objects.requireNonNull(registrationKey);

        User user = validateJoinRequest(registrationKey, account.getId());
        assert user.getAccount() == null;
        user.setAccount(account);
        return user;
    }

    private User validateJoinRequest(String registrationKey, UUID accountId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        User user = usersLogic.getUserByRegistrationKey(registrationKey);

        if (user == null) {
            throw new EntityDoesNotExistException("No user with given registration key: " + registrationKey);
        }

        if (user.isRegistered()) {
            throw new EntityAlreadyExistsException(
                    "User has already joined course");
        }

        validateNonExistingLinkedUserInCourse(user, accountId);

        return user;
    }

    private void validateAccount(Account account) throws InvalidParametersException {
        if (!account.isValid()) {
            throw new InvalidParametersException(account.getInvalidityInfo());
        }
    }

    private void validateNonExistingLinkedUserInCourse(User user, UUID accountId)
            throws EntityAlreadyExistsException {
        User existingLinkedUser;
        if (user instanceof Student) {
            existingLinkedUser = usersLogic.getStudentByAccountId(accountId, user.getCourseId());
        } else if (user instanceof Instructor) {
            existingLinkedUser = usersLogic.getInstructorByAccountId(accountId, user.getCourseId());
        } else {
            throw new IllegalStateException("Unknown user type: " + user.getClass().getName());
        }

        if (existingLinkedUser != null) {
            throw new EntityAlreadyExistsException(
                    "This account is already associated with another " + user.getClass().getSimpleName().toLowerCase());
        }
    }
}
