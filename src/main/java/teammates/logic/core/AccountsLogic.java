package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.AccountsDb;

/**
 * Handles operations related to accounts.
 *
 * @see AccountAttributes
 * @see AccountsDb
 */
public final class AccountsLogic {

    private static final AccountsLogic instance = new AccountsLogic();

    private final AccountsDb accountsDb = AccountsDb.inst();

    private CoursesLogic coursesLogic;
    private InstructorsLogic instructorsLogic;
    private StudentsLogic studentsLogic;
    private NotificationsLogic notificationsLogic;

    private AccountsLogic() {
        // prevent initialization
    }

    public static AccountsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        coursesLogic = CoursesLogic.inst();
        instructorsLogic = InstructorsLogic.inst();
        studentsLogic = StudentsLogic.inst();
        notificationsLogic = NotificationsLogic.inst();
    }

    /**
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the database.
     */
    AccountAttributes createAccount(AccountAttributes accountData)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountsDb.createEntity(accountData);
    }

    /**
     * Gets an account.
     */
    public AccountAttributes getAccount(String googleId) {
        return accountsDb.getAccount(googleId);
    }

    /**
     * Gets ids of read notifications in an account.
     */
    public List<String> getReadNotificationsId(String googleId) {
        AccountAttributes a = accountsDb.getAccount(googleId);
        List<String> readNotificationIds = new ArrayList<>();
        if (a != null) {
            readNotificationIds.addAll(a.getReadNotifications().keySet());
        }
        return readNotificationIds;
    }

    /**
     * Returns a list of accounts with email matching {@code email}.
     */
    public List<AccountAttributes> getAccountsForEmail(String email) {
        return accountsDb.getAccountsForEmail(email);
    }

    /**
     * Joins the user as a student.
     */
    public StudentAttributes joinCourseForStudent(String registrationKey, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        StudentAttributes student = validateStudentJoinRequest(registrationKey, googleId);

        // Register the student
        student.setGoogleId(googleId);
        try {
            studentsLogic.updateStudentCascade(
                    StudentAttributes.updateOptionsBuilder(student.getCourse(), student.getEmail())
                            .withGoogleId(student.getGoogleId())
                            .build());
        } catch (EntityDoesNotExistException e) {
            assert false : "Student disappeared while trying to register";
        }

        if (accountsDb.getAccount(googleId) == null) {
            createStudentAccount(student);
        }

        return student;
    }

    /**
     * Joins the user as an instructor and sets the institute if it is not null.
     * If the given institute is null, the instructor is given the institute of an existing instructor of the same course.
     */
    public InstructorAttributes joinCourseForInstructor(String key, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        InstructorAttributes instructor = validateInstructorJoinRequest(key, googleId);

        // Register the instructor
        instructor.setGoogleId(googleId);
        try {
            instructorsLogic.updateInstructorByEmail(
                    InstructorAttributes.updateOptionsWithEmailBuilder(instructor.getCourseId(), instructor.getEmail())
                            .withGoogleId(instructor.getGoogleId())
                            .build());
        } catch (EntityDoesNotExistException e) {
            assert false : "Instructor disappeared while trying to register";
        } catch (InstructorUpdateException e) {
            assert false;
        }

        AccountAttributes account = accountsDb.getAccount(googleId);

        if (account == null) {
            try {
                createAccount(AccountAttributes.builder(googleId)
                        .withName(instructor.getName())
                        .withEmail(instructor.getEmail())
                        .build());
            } catch (EntityAlreadyExistsException e) {
                assert false : "Account already exists.";
            }
        }

        // Update the googleId of the student entity for the instructor which was created from sample data.
        StudentAttributes student = studentsLogic.getStudentForEmail(instructor.getCourseId(), instructor.getEmail());
        if (student != null) {
            student.setGoogleId(googleId);
            studentsLogic.updateStudentCascade(
                    StudentAttributes.updateOptionsBuilder(student.getCourse(), student.getEmail())
                            .withGoogleId(student.getGoogleId())
                            .build());
        }

        return instructor;
    }

    private InstructorAttributes validateInstructorJoinRequest(String registrationKey, String googleId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        InstructorAttributes instructorForKey = instructorsLogic.getInstructorForRegistrationKey(registrationKey);

        if (instructorForKey == null) {
            throw new EntityDoesNotExistException("No instructor with given registration key: " + registrationKey);
        }

        CourseAttributes courseAttributes = coursesLogic.getCourse(instructorForKey.getCourseId());

        if (courseAttributes == null) {
            throw new EntityDoesNotExistException("Course with id " + instructorForKey.getCourseId() + " does not exist");
        }

        if (courseAttributes.isCourseDeleted()) {
            throw new EntityDoesNotExistException("The course you are trying to join has been deleted by an instructor");
        }

        if (instructorForKey.isRegistered()) {
            if (instructorForKey.getGoogleId().equals(googleId)) {
                AccountAttributes existingAccount = accountsDb.getAccount(googleId);
                if (existingAccount != null) {
                    throw new EntityAlreadyExistsException("Instructor has already joined course");
                }
            } else {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        } else {
            // Check if this Google ID has already joined this course
            InstructorAttributes existingInstructor =
                    instructorsLogic.getInstructorForGoogleId(instructorForKey.getCourseId(), googleId);

            if (existingInstructor != null) {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        }

        return instructorForKey;
    }

    private StudentAttributes validateStudentJoinRequest(String registrationKey, String googleId)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

        StudentAttributes studentRole = studentsLogic.getStudentForRegistrationKey(registrationKey);

        if (studentRole == null) {
            throw new EntityDoesNotExistException("No student with given registration key: " + registrationKey);
        }

        CourseAttributes courseAttributes = coursesLogic.getCourse(studentRole.getCourse());

        if (courseAttributes == null) {
            throw new EntityDoesNotExistException("Course with id " + studentRole.getCourse() + " does not exist");
        }

        if (courseAttributes.isCourseDeleted()) {
            throw new EntityDoesNotExistException("The course you are trying to join has been deleted by an instructor");
        }

        if (studentRole.isRegistered()) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        // Check if this Google ID has already joined this course
        StudentAttributes existingStudent =
                studentsLogic.getStudentForCourseIdAndGoogleId(studentRole.getCourse(), googleId);

        if (existingStudent != null) {
            throw new EntityAlreadyExistsException("Student has already joined course");
        }

        return studentRole;
    }

    /**
     * Deletes both instructor and student privileges, as well as the account.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     */
    public void deleteAccountCascade(String googleId) {
        if (accountsDb.getAccount(googleId) == null) {
            return;
        }

        // to prevent orphan course
        List<InstructorAttributes> instructorsToDelete =
                instructorsLogic.getInstructorsForGoogleId(googleId, false);
        for (InstructorAttributes instructorToDelete : instructorsToDelete) {
            if (instructorsLogic.getInstructorsForCourse(instructorToDelete.getCourseId()).size() <= 1) {
                // the instructor is the last instructor in the course
                coursesLogic.deleteCourseCascade(instructorToDelete.getCourseId());
            }
        }

        instructorsLogic.deleteInstructorsForGoogleIdCascade(googleId);
        studentsLogic.deleteStudentsForGoogleIdCascade(googleId);
        accountsDb.deleteAccount(googleId);
    }

    /**
     * Creates a student account.
     */
    private void createStudentAccount(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {

        AccountAttributes account = AccountAttributes.builder(student.getGoogleId())
                .withEmail(student.getEmail())
                .withName(student.getName())
                .build();

        accountsDb.createEntity(account);
    }

    /**
     * Updates the readNotifications of an account.
     *
     * @param googleId google ID of the user who read the notification.
     * @param notificationId notification to be marked as read.
     * @param endTime the expiry time of the notification, i.e. notification will not be shown after this time.
     * @return the account attributes with updated read notifications.
     * @throws InvalidParametersException if the notification has expired.
     * @throws EntityDoesNotExistException if account or notification does not exist.
     */
    public List<String> updateReadNotifications(String googleId, String notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        AccountAttributes a = accountsDb.getAccount(googleId);

        if (a == null) {
            throw new EntityDoesNotExistException("Trying to update the read notifications of a non-existent account.");
        }
        if (!notificationsLogic.doesNotificationExists(notificationId)) {
            throw new EntityDoesNotExistException("Trying to mark as read a notification that does not exist.");
        }
        if (endTime.isBefore(Instant.now())) {
            throw new InvalidParametersException("Trying to mark an expired notification as read.");
        }

        Map<String, Instant> updatedReadNotifications = new HashMap<>();
        // only keep active notifications in readNotifications
        for (Map.Entry<String, Instant> notification : a.getReadNotifications().entrySet()) {
            if (notification.getValue().isAfter(Instant.now())) {
                updatedReadNotifications.put(notification.getKey(), notification.getValue());
            }
        }

        updatedReadNotifications.put(notificationId, endTime);

        AccountAttributes accountAttributes = accountsDb.updateAccount(
                AccountAttributes.updateOptionsBuilder(googleId)
                        .withReadNotifications(updatedReadNotifications)
                        .build());
        return new ArrayList<>(accountAttributes.getReadNotifications().keySet());
    }
}
