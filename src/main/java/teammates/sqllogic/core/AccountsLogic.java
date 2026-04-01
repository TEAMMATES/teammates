package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.FieldValidator;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountIdentity;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.ReadNotification;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.User;

/**
 * Handles operations related to accounts.
 *
 * @see Account
 * @see AccountsDb
 */
public final class AccountsLogic {

    private static final AccountsLogic instance = new AccountsLogic();

    private AccountsDb accountsDb;

    private NotificationsLogic notificationsLogic;

    private UsersLogic usersLogic;

    private CoursesLogic coursesLogic;

    private AccountsLogic() {
        // prevent initialization
    }

    void initLogicDependencies(AccountsDb accountsDb, NotificationsLogic notificationsLogic,
            UsersLogic usersLogic, CoursesLogic coursesLogic) {
        this.accountsDb = accountsDb;
        this.notificationsLogic = notificationsLogic;
        this.usersLogic = usersLogic;
        this.coursesLogic = coursesLogic;
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
     * Gets an account by internal account id (UUID string).
     */
    public Account getAccountById(String accountId) {
        assert accountId != null;
        return getAccount(UUID.fromString(accountId));
    }

    /**
     * Resolves or creates an account from a verified OIDC login (issuer + subject).
     */
    public Account resolveOrCreateAccountFromOidc(String issuer, String subject, String email, String name)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert issuer != null;
        assert subject != null;

        AccountIdentity existing = accountsDb.getAccountIdentityByIssuerAndSubject(issuer, subject);
        if (existing != null) {
            return existing.getAccount();
        }

        assert email != null : "OIDC email claim must be present to create an account";
        String safeName = name != null ? name : "";
        Account account = new Account(safeName, email);
        AccountIdentity identity = new AccountIdentity(issuer, subject, email);
        account.addIdentity(identity);
        return accountsDb.createAccount(account);
    }

    /**
     * Links an additional OIDC identity to an existing account.
     */
    public AccountIdentity linkAccountIdentity(Account account, String issuer, String subject, String loginIdentifier)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;
        if (accountsDb.getAccountIdentityByIssuerAndSubject(issuer, subject) != null) {
            throw new EntityAlreadyExistsException("Identity already linked to an account.");
        }
        AccountIdentity identity = new AccountIdentity(issuer, subject, loginIdentifier);
        account.addIdentity(identity);
        return accountsDb.createAccountIdentity(identity);
    }

    /**
     * Returns the login identifier for the first identity linked to the given account,
     * or an empty string if none exists.
     */
    public String getLoginIdentifierForAccount(String accountId) {
        AccountIdentity identity = accountsDb.getFirstAccountIdentityByAccountId(UUID.fromString(accountId));
        return identity != null ? identity.getLoginIdentifier() : "";
    }

    /**
     * Gets accounts associated with email.
     */
    public List<Account> getAccountsForEmail(String email) {
        assert email != null;

        return accountsDb.getAccountsByEmail(email);
    }

    /**
     * Creates an account (typically with {@link Account#addIdentity} already populated).
     *
     * @return the created account
     * @throws InvalidParametersException   if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the
     *                                      database.
     */
    public Account createAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;
        return accountsDb.createAccount(account);
    }

    /**
     * Deletes account by internal id.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccount(UUID accountId) {
        assert accountId != null;

        Account account = getAccount(accountId);
        if (account != null) {
            accountsDb.deleteAccount(account);
        }
    }

    /**
     * Deletes account and all users associated with the account id.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccountCascade(String accountId) {
        assert accountId != null;

        Account acc = getAccountById(accountId);
        if (acc == null) {
            return;
        }
        String canonicalId = acc.getId().toString();
        List<User> usersToDelete = usersLogic.getAllUsersByAccountId(canonicalId);

        for (User user : usersToDelete) {
            usersLogic.deleteUser(user);
        }

        deleteAccount(acc.getId());
    }

    /**
     * Updates the readNotifications of an account.
     *
     * @param accountId      internal account UUID string
     * @param notificationId ID of notification to be marked as read.
     * @param endTime        the expiry time of the notification, i.e. notification
     *                       will not be shown after this time.
     * @return the account with updated read notifications.
     * @throws InvalidParametersException  if the notification has expired.
     * @throws EntityDoesNotExistException if account or notification does not
     *                                     exist.
     */
    public List<UUID> updateReadNotifications(String accountId, UUID notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        Account account = accountsDb.getAccount(UUID.fromString(accountId));
        if (account == null) {
            throw new EntityDoesNotExistException("Trying to update the read notifications of a non-existent account.");
        }

        Notification notification = notificationsLogic.getNotification(notificationId);
        if (notification == null) {
            throw new EntityDoesNotExistException("Trying to mark as read a notification that does not exist.");
        }
        if (endTime.isBefore(Instant.now())) {
            throw new InvalidParametersException("Trying to mark an expired notification as read.");
        }

        ReadNotification readNotification = new ReadNotification(account, notification);
        account.addReadNotification(readNotification);

        return account.getReadNotifications().stream()
                .map(n -> n.getNotification().getId())
                .collect(Collectors.toList());
    }

    /**
     * Gets ids of read notifications in an account.
     */
    public List<UUID> getReadNotificationsId(String accountId) {
        return accountsDb.getAccount(UUID.fromString(accountId)).getReadNotifications().stream()
                .map(n -> n.getNotification().getId())
                .collect(Collectors.toList());
    }

    /**
     * Joins the user as a student.
     */
    public Student joinCourseForStudent(String registrationKey, String accountId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Student student = validateStudentJoinRequest(registrationKey, accountId);

        Account account = getAccountById(accountId);
        if (account == null) {
            throw new EntityDoesNotExistException("No account found for the current user.");
        }

        if (student.getAccount() == null) {
            student.setAccount(account);
        }

        return student;
    }

    /**
     * Joins the user as an instructor.
     */
    public Instructor joinCourseForInstructor(String key, String accountId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Instructor instructor = validateInstructorJoinRequest(key, accountId);

        Account account = getAccountById(accountId);
        if (account == null) {
            throw new EntityDoesNotExistException("No account found for the current user.");
        }

        instructor.setAccount(account);

        // Update the account of the student entity for the instructor which was created from sample data.
        Student student = usersLogic.getStudentForEmail(instructor.getCourseId(), instructor.getEmail());
        if (student != null) {
            student.setAccount(account);
            usersLogic.updateStudentCascade(student);
        }

        return instructor;
    }

    private Instructor validateInstructorJoinRequest(String registrationKey, String accountId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Instructor instructorForKey = usersLogic.getInstructorByRegistrationKey(registrationKey);

        if (instructorForKey == null) {
            throw new EntityDoesNotExistException("No instructor with given registration key: " + registrationKey);
        }

        Course course = coursesLogic.getCourse(instructorForKey.getCourseId());

        if (course == null) {
            throw new EntityDoesNotExistException("Course with id " + instructorForKey.getCourseId() + " does not exist");
        }

        if (course.isCourseDeleted()) {
            throw new EntityDoesNotExistException("The course you are trying to join has been deleted by an instructor");
        }

        validateAccountIdFormat(accountId);

        if (instructorForKey.isRegistered()) {
            if (accountId.equals(instructorForKey.getAccountId())) {
                Account existingAccount = getAccountById(accountId);
                if (existingAccount != null) {
                    throw new EntityAlreadyExistsException("Instructor has already joined course");
                }
            } else {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        } else {
            Instructor existingInstructor =
                    usersLogic.getInstructorByAccountId(instructorForKey.getCourseId(), accountId);

            if (existingInstructor != null) {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        }

        return instructorForKey;
    }

    private Student validateStudentJoinRequest(String registrationKey, String accountId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Student studentRole = usersLogic.getStudentByRegistrationKey(registrationKey);

        if (studentRole == null) {
            throw new EntityDoesNotExistException("No student with given registration key: " + registrationKey);
        }

        Course course = coursesLogic.getCourse(studentRole.getCourseId());

        if (course == null) {
            throw new EntityDoesNotExistException("Course with id " + studentRole.getCourseId() + " does not exist");
        }

        if (course.isCourseDeleted()) {
            throw new EntityDoesNotExistException("The course you are trying to join has been deleted by an instructor");
        }

        validateAccountIdFormat(accountId);

        if (studentRole.isRegistered()) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        Student existingStudent =
                usersLogic.getStudentByAccountId(studentRole.getCourseId(), accountId);

        if (existingStudent != null) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        return studentRole;
    }

    private void validateAccountIdFormat(String accountId) throws InvalidParametersException {
        if (accountId == null) {
            throw new InvalidParametersException("Account ID " + FieldValidator.REASON_INCORRECT_FORMAT);
        }
        try {
            UUID.fromString(accountId);
        } catch (IllegalArgumentException e) {
            InvalidParametersException ex =
                    new InvalidParametersException("Account ID " + FieldValidator.REASON_INCORRECT_FORMAT);
            ex.initCause(e);
            throw ex;
        }
    }
}
