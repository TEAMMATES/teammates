package teammates.sqllogic.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.sqllogic.core.AccountRequestsLogic;
import teammates.sqllogic.core.AccountsLogic;
import teammates.sqllogic.core.CoursesLogic;
import teammates.sqllogic.core.DeadlineExtensionsLogic;
import teammates.sqllogic.core.FeedbackSessionsLogic;
import teammates.sqllogic.core.NotificationsLogic;
import teammates.sqllogic.core.UsageStatisticsLogic;
import teammates.sqllogic.core.UsersLogic;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.UsageStatistics;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {
    private static final Logic instance = new Logic();

    final AccountRequestsLogic accountRequestLogic = AccountRequestsLogic.inst();
    final AccountsLogic accountsLogic = AccountsLogic.inst();
    final CoursesLogic coursesLogic = CoursesLogic.inst();
    final DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
    final UsersLogic usersLogic = UsersLogic.inst();
    final NotificationsLogic notificationsLogic = NotificationsLogic.inst();

    Logic() {
        // prevent initialization
    }

    public static Logic inst() {
        return instance;
    }

    /**
     * Creates an account request.
     *
     * @return newly created account request.
     * @throws InvalidParametersException if the account request details are invalid.
     * @throws EntityAlreadyExistsException if the account request already exists.
     */
    public AccountRequest createAccountRequest(String name, String email, String institute)
            throws InvalidParametersException, EntityAlreadyExistsException {

        return accountRequestLogic.createAccountRequest(name, email, institute);
    }

    /**
     * Gets the account request with the given email and institute.
     *
     * @return account request with the given email and institute.
     */
    public AccountRequest getAccountRequest(String email, String institute) {
        return accountRequestLogic.getAccountRequest(email, institute);
    }

    /**
     * Creates/Resets the account request with the given email and institute
     * such that it is not registered.
     *
     * @return account request that is unregistered with the
     *         email and institute.
     */
    public AccountRequest resetAccountRequest(String email, String institute)
            throws EntityDoesNotExistException, InvalidParametersException {
        return accountRequestLogic.resetAccountRequest(email, institute);
    }

    /**
     * Deletes account request by email and institute.
     *
     * <ul>
     * <li>Fails silently if no such account request.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * All parameters are non-null.
     */
    public void deleteAccountRequest(String email, String institute) {
        accountRequestLogic.deleteAccountRequest(email, institute);
    }

    /**
     * Gets an account.
     */
    public Account getAccount(UUID id) {
        return accountsLogic.getAccount(id);
    }

    /**
     * Gets an account by googleId.
     */
    public Account getAccountForGoogleId(String googleId) {
        return accountsLogic.getAccountForGoogleId(googleId);
    }

    /**
     * Get a list of accounts associated with email provided.
     */
    public List<Account> getAccountsForEmail(String email) {
        return accountsLogic.getAccountsForEmail(email);
    }

    /**
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the database.
     */
    public Account createAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountsLogic.createAccount(account);
    }

    /**
     * Gets a course by course id.
     * @param courseId courseId of the course.
     * @return the specified course.
     */
    public Course getCourse(String courseId) {
        return coursesLogic.getCourse(courseId);
    }

    /**
     * Creates a course.
     * @param course the course to create.
     * @return the created course.
     * @throws InvalidParametersException if the course is not valid.
     * @throws EntityAlreadyExistsException if the course already exists.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createCourse(course);
    }

    /**
     * Get section by {@code courseId} and {@code teamName}.
     */
    public Section getSectionByCourseIdAndTeam(String courseId, String teamName) {
        return coursesLogic.getSectionByCourseIdAndTeam(courseId, teamName);
    }

    /**
     * Creates a deadline extension.
     *
     * @return created deadline extension
     * @throws InvalidParametersException if the deadline extension is not valid
     * @throws EntityAlreadyExistsException if the deadline extension already exist
     */
    public DeadlineExtension createDeadlineExtension(DeadlineExtension deadlineExtension)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
    }

    /**
     * Gets a feedback session.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSession(UUID id) {
        return feedbackSessionsLogic.getFeedbackSession(id);
    }

    /**
     * Creates a feedback session.
     *
     * @return created feedback session
     * @throws InvalidParametersException if the session is not valid
     * @throws EntityAlreadyExistsException if the session already exist
     */
    public FeedbackSession createFeedbackSession(FeedbackSession session)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return feedbackSessionsLogic.createFeedbackSession(session);
    }

    /**
     * Get usage statistics within a time range.
     */
    public List<UsageStatistics> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        return usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);
    }

    /**
     * Calculate usage statistics within a time range.
     */
    public UsageStatistics calculateEntitiesStatisticsForTimeRange(Instant startTime, Instant endTime) {
        return usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);
    }

    /**
     * Create usage statistics within a time range.
     */
    public void createUsageStatistics(UsageStatistics attributes)
            throws EntityAlreadyExistsException, InvalidParametersException {
        usageStatisticsLogic.createUsageStatistics(attributes);
    }

    /**
     * Creates a notification.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return created notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityAlreadyExistsException if the notification exists in the database
     */
    public Notification createNotification(Notification notification) throws
            InvalidParametersException, EntityAlreadyExistsException {
        return notificationsLogic.createNotification(notification);
    }

    /**
     * Gets a notification by ID.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public Notification getNotification(UUID notificationId) {
        return notificationsLogic.getNotification(notificationId);
    }

    /**
     * Updates a notification.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     * @return updated notification
     * @throws InvalidParametersException if the notification is not valid
     * @throws EntityDoesNotExistException if the notification does not exist in the database
     */
    public Notification updateNotification(UUID notificationId, Instant startTime, Instant endTime,
                                           NotificationStyle style, NotificationTargetUser targetUser, String title,
                                           String message) throws
            InvalidParametersException, EntityDoesNotExistException {
        return notificationsLogic.updateNotification(notificationId, startTime, endTime, style, targetUser, title, message);
    }

    /**
     * Deletes notification by ID.
     *
     * <ul>
     * <li>Fails silently if no such notification.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     */
    public void deleteNotification(UUID notificationId) {
        notificationsLogic.deleteNotification(notificationId);
    }

    /**
     * Get a list of IDs of the read notifications of the account.
     */
    public List<UUID> getReadNotificationsId(String id) {
        return accountsLogic.getReadNotificationsId(id);
    }

    /**
     * Updates user read status for notification with ID {@code notificationId} and expiry time {@code endTime}.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null. {@code endTime} must be after current moment.
     */
    public List<UUID> updateReadNotifications(String id, UUID notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        return accountsLogic.updateReadNotifications(id, notificationId, endTime);
    }

    /**
     * Gets instructor associated with {@code id}.
     *
     * @param id    Id of Instructor.
     * @return      Returns Instructor if found else null.
     */
    public Instructor getInstructor(UUID id) {
        return usersLogic.getInstructor(id);
    }

    /**
     * Gets instructor associated with {@code courseId} and {@code email}.
     */
    public Instructor getInstructorForEmail(String courseId, String email) {
        return usersLogic.getInstructorForEmail(courseId, email);
    }

    /**
     * Gets an instructor by associated {@code regkey}.
     */
    public Instructor getInstructorByRegistrationKey(String regKey) {
        return usersLogic.getInstructorByRegistrationKey(regKey);
    }

    /**
     * Gets an instructor by associated {@code googleId}.
     */
    public Instructor getInstructorByGoogleId(String courseId, String googleId) {
        return usersLogic.getInstructorByGoogleId(courseId, googleId);
    }

    /**
     * Gets student associated with {@code id}.
     *
     * @param id    Id of Student.
     * @return      Returns Student if found else null.
     */
    public Student getStudent(UUID id) {
        return usersLogic.getStudent(id);
    }

    /**
     * Gets student associated with {@code courseId} and {@code email}.
     */
    public Student getStudentForEmail(String courseId, String email) {
        return usersLogic.getStudentForEmail(courseId, email);
    }

    /**
     * Gets a student by associated {@code regkey}.
     */
    public Student getStudentByRegistrationKey(String regKey) {
        return usersLogic.getStudentByRegistrationKey(regKey);
    }

    /**
     * Gets a student by associated {@code googleId}.
     */
    public Student getStudentByGoogleId(String courseId, String googleId) {
        return usersLogic.getStudentByGoogleId(courseId, googleId);
    }

    public List<Notification> getAllNotifications() {
        return notificationsLogic.getAllNotifications();
    }

    /**
     * Returns active notification for general users and the specified {@code targetUser}.
     */
    public List<Notification> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        return notificationsLogic.getActiveNotificationsByTargetUser(targetUser);
    }
}
