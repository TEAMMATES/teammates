package teammates.sqllogic.core;

import java.util.List;
import java.util.UUID;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.HibernateUtil;
import teammates.storage.sqlapi.AccountsDb;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
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

    private UsersLogic usersLogic;

    private CoursesLogic coursesLogic;

    private AccountsLogic() {
        // prevent initialization
    }

    void initLogicDependencies(AccountsDb accountsDb,
            UsersLogic usersLogic, CoursesLogic coursesLogic) {
        this.accountsDb = accountsDb;
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
     * Gets accounts associated with email.
     */
    public List<Account> getAccountsForEmail(String email) {
        assert email != null;

        return accountsDb.getAccountsByEmail(email);
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
     * Deletes account associated with the {@code accountId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccount(UUID accountId) {
        assert accountId != null;

        Account account = HibernateUtil.getReference(Account.class, accountId);
        accountsDb.deleteAccount(account);
    }

    /**
     * Deletes account and all users associated with the {@code accountId}.
     *
     * <p>Fails silently if the account doesn't exist.</p>
     */
    public void deleteAccountCascade(UUID accountId) {
        assert accountId != null;

        List<User> usersToDelete = usersLogic.getAllUsersByAccountId(accountId);

        for (User user : usersToDelete) {
            usersLogic.deleteUser(user);
        }

        deleteAccount(accountId);
    }

    /**
     * Joins the user as a student.
     */
    public Student joinCourseForStudent(String registrationKey, UUID accountId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        Student student = validateStudentJoinRequest(registrationKey, accountId);
        Account account = accountsDb.getAccount(accountId);
        if (student.getAccount() == null) {
            student.setAccount(account);
        }

        return student;
    }

    /**
     * Joins the user as an instructor.
     */
    public Instructor joinCourseForInstructor(String key, UUID accountId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        Instructor instructor = validateInstructorJoinRequest(key, accountId);
        Account account = accountsDb.getAccount(accountId);
        instructor.setAccount(account);

        // TODO: verify what is this
        // Update the accountId of the student entity for the instructor which was created from sample data.
        Student student = usersLogic.getStudentForEmail(instructor.getCourseId(), instructor.getEmail());
        if (student != null) {
            student.setAccount(account);
            usersLogic.updateStudentCascade(student);
        }

        return instructor;
    }

    private Instructor validateInstructorJoinRequest(String registrationKey, UUID accountId)
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
            if (instructorForKey.getAccountId().equals(accountId)) {
                Account existingAccount = accountsDb.getAccount(accountId);
                if (existingAccount != null) {
                    throw new EntityAlreadyExistsException("Instructor has already joined course");
                }
            } else {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        } else {
            // Check if this account ID has already joined this course
            Instructor existingInstructor =
                    usersLogic.getInstructorByAccountId(instructorForKey.getCourseId(), accountId);

            if (existingInstructor != null) {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        }

        return instructorForKey;
    }

    private Student validateStudentJoinRequest(String registrationKey, UUID accountId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        Account account = accountsDb.getAccount(accountId);
        if (account == null) {
            throw new EntityDoesNotExistException("No student with given account ID: " + accountId);
        }

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

        // Check if this account ID has already joined this course
        Student existingStudent =
                usersLogic.getStudentByAccountId(studentRole.getCourseId(), accountId);

        if (existingStudent != null) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        return studentRole;
    }
}
