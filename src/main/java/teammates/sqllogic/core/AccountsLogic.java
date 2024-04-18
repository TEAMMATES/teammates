package teammates.sqllogic.core;

import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
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
     * Gets accounts associated with email.
     */
    public List<Account> getAccountsForEmailWithTransaction(String email) {
        assert email != null;

        HibernateUtil.beginTransaction();
        List<Account> accounts = accountsDb.getAccountsByEmail(email);
        HibernateUtil.commitTransaction();

        return accounts;
    }

    /**
     * Creates an account.
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
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException   if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the
     *                                      database.
     */
    public Account createAccountWithTransaction(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert account != null;

        HibernateUtil.beginTransaction();
        Account createdAccount = accountsDb.createAccount(account);
        HibernateUtil.commitTransaction();

        return createdAccount;
    }

    /**
     * Deletes account associated with the {@code googleId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccount(String googleId) {
        assert googleId != null;

        Account account = getAccountForGoogleId(googleId);
        accountsDb.deleteAccount(account);
    }

    /**
     * Deletes account and all users associated with the {@code googleId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccountCascade(String googleId) {
        assert googleId != null;

        List<User> usersToDelete = usersLogic.getAllUsersByGoogleId(googleId);

        for (User user : usersToDelete) {
            usersLogic.deleteUser(user);
        }

        deleteAccount(googleId);
    }

    /**
     * Updates the readNotifications of an account.
     *
     * @param googleId       google ID of the user who read the notification.
     * @param notificationId ID of notification to be marked as read.
     * @param endTime        the expiry time of the notification, i.e. notification
     *                       will not be shown after this time.
     * @return the account with updated read notifications.
     * @throws InvalidParametersException  if the notification has expired.
     * @throws EntityDoesNotExistException if account or notification does not
     *                                     exist.
     */
    public List<UUID> updateReadNotifications(String googleId, UUID notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        Account account = accountsDb.getAccountByGoogleId(googleId);
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
    public List<UUID> getReadNotificationsId(String googleId) {
        return accountsDb.getAccountByGoogleId(googleId).getReadNotifications().stream()
                .map(n -> n.getNotification().getId())
                .collect(Collectors.toList());
    }

    /**
     * Joins the user as a student.
     */
    public Student joinCourseForStudent(String registrationKey, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Student student = validateStudentJoinRequest(registrationKey, googleId);

        Account account = accountsDb.getAccountByGoogleId(googleId);
        // Create an account if it doesn't exist
        if (account == null) {
            account = new Account(googleId, student.getName(), student.getEmail());
            createAccount(account);
        }

        if (student.getAccount() == null) {
            student.setAccount(account);
        }

        return student;
    }

    /**
     * Joins the user as an instructor.
     */
    public Instructor joinCourseForInstructor(String key, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Instructor instructor = validateInstructorJoinRequest(key, googleId);

        Account account = accountsDb.getAccountByGoogleId(googleId);
        if (account == null) {
            try {
                account = new Account(googleId, instructor.getName(), instructor.getEmail());
                createAccount(account);
            } catch (EntityAlreadyExistsException e) {
                assert false : "Account already exists.";
            }
        }

        instructor.setAccount(account);

        // Update the googleId of the student entity for the instructor which was created from sample data.
        Student student = usersLogic.getStudentForEmail(instructor.getCourseId(), instructor.getEmail());
        if (student != null) {
            student.setAccount(account);
            usersLogic.updateStudentCascade(student);
        }

        return instructor;
    }

    private Instructor validateInstructorJoinRequest(String registrationKey, String googleId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
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

        if (instructorForKey.isRegistered()) {
            if (instructorForKey.getGoogleId().equals(googleId)) {
                Account existingAccount = accountsDb.getAccountByGoogleId(googleId);
                if (existingAccount != null) {
                    throw new EntityAlreadyExistsException("Instructor has already joined course");
                }
            } else {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        } else {
            // Check if this Google ID has already joined this course
            Instructor existingInstructor =
                    usersLogic.getInstructorByGoogleId(instructorForKey.getCourseId(), googleId);

            if (existingInstructor != null) {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        }

        return instructorForKey;
    }

    private Student validateStudentJoinRequest(String registrationKey, String googleId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

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

        if (studentRole.isRegistered()) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        // Check if this Google ID has already joined this course
        Student existingStudent =
                usersLogic.getStudentByGoogleId(studentRole.getCourseId(), googleId);

        if (existingStudent != null) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        return studentRole;
    }
}
