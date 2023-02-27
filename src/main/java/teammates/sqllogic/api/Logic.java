package teammates.sqllogic.api;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidOperationException;
import teammates.common.exception.InvalidParametersException;
import teammates.sqllogic.core.AccountRequestLogic;
import teammates.sqllogic.core.CoursesLogic;
import teammates.sqllogic.core.NotificationsLogic;
import teammates.sqllogic.core.UsageStatisticsLogic;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.UsageStatistics;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {
    private static final Logic instance = new Logic();

    final AccountRequestLogic accountRequestLogic = AccountRequestLogic.inst();
    final CoursesLogic coursesLogic = CoursesLogic.inst();
    // final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
    final NotificationsLogic notificationsLogic = NotificationsLogic.inst();

    Logic() {
        // prevent initialization
    }

    public static Logic inst() {
        return instance;
    }

    // AccountRequests

    public AccountRequest createAccountRequest(String name, String email, String institute)
        throws InvalidParametersException, EntityAlreadyExistsException, InvalidOperationException {

        AccountRequest accountRequest = accountRequestLogic.createAccountRequest(name, email, institute);

        if (accountRequest.getRegisteredAt() != null) {
            throw new InvalidOperationException("Cannot create account request as instructor has already registered.");
        }

        return accountRequest;
    }

    public AccountRequest getAccountRequest(String email, String institute) {
        return accountRequestLogic.getAccountRequest(email, institute);
    }

    public AccountRequest updateAccountRequest(String email, String institute)
        throws EntityDoesNotExistException, InvalidOperationException,
            InvalidParametersException {
        return accountRequestLogic.updateAccountRequest(email, institute);
    }

    public void deleteAccountRequest(String email, String institute) throws InvalidOperationException {
        accountRequestLogic.deleteAccountRequest(email, institute);
    }

    // Courses

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
}
