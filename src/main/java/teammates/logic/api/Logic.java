package teammates.logic.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EnrollResults;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.UpdateExtensionsResult;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.StudentUpdateException;
import teammates.common.util.Const;
import teammates.logic.core.AccountRequestsLogic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.DataBundleLogic;
import teammates.logic.core.DeadlineExtensionsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionLogsLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.NotificationsLogic;
import teammates.logic.core.UsageStatisticsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackResponseComment;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.UsageStatistics;
import teammates.storage.entity.User;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.StudentEnrollRequest;
import teammates.ui.request.StudentUpdateRequest;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>
 * This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {
    private static final Logic instance = new Logic();

    final AccountsLogic accountsLogic = AccountsLogic.inst();
    final AccountRequestsLogic accountRequestLogic = AccountRequestsLogic.inst();
    final CoursesLogic coursesLogic = CoursesLogic.inst();
    final DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    final FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();
    final FeedbackResponsesLogic feedbackResponsesLogic = FeedbackResponsesLogic.inst();
    final FeedbackResponseCommentsLogic feedbackResponseCommentsLogic = FeedbackResponseCommentsLogic.inst();
    final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    final FeedbackSessionLogsLogic feedbackSessionLogsLogic = FeedbackSessionLogsLogic.inst();
    final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
    final UsersLogic usersLogic = UsersLogic.inst();
    final NotificationsLogic notificationsLogic = NotificationsLogic.inst();
    final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

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
     * @throws InvalidParametersException   if the account request details are
     *                                      invalid.
     * @throws EntityAlreadyExistsException if the account request already exists.
     */
    public AccountRequest createAccountRequest(String name, String email, String institute, AccountRequestStatus status,
            String comments) throws InvalidParametersException {

        return accountRequestLogic.createAccountRequest(name, email, institute, status, comments);
    }

    /**
     * Gets the account request with the given {@code id}.
     *
     * @return account request with the given {@code id}.
     */
    public AccountRequest getAccountRequest(UUID id) {
        return accountRequestLogic.getAccountRequest(id);
    }

    /**
     * Gets the account request with the associated {@code regkey}.
     *
     * @return account request with the associated {@code regkey}.
     */
    public AccountRequest getAccountRequestByRegistrationKey(String regkey) {
        return accountRequestLogic.getAccountRequestByRegistrationKey(regkey);
    }

    /**
     * Updates the given account request.
     *
     * @return the updated account request.
     */
    public AccountRequest updateAccountRequest(AccountRequest accountRequest)
            throws InvalidParametersException {
        return accountRequestLogic.updateAccountRequest(accountRequest);
    }

    /**
     * Creates/Resets the account request with the given id
     * such that it is not registered.
     *
     * @return account request that is unregistered with the
     *         id.
     */
    public AccountRequest resetAccountRequest(UUID id)
            throws EntityDoesNotExistException, InvalidParametersException {
        return accountRequestLogic.resetAccountRequest(id);
    }

    /**
     * Deletes account request by id.
     *
     * <ul>
     * <li>Fails silently if no such account request.</li>
     * </ul>
     *
     * <p>
     * Preconditions:
     * </p>
     * All parameters are non-null.
     */
    public void deleteAccountRequest(UUID id) {
        accountRequestLogic.deleteAccountRequest(id);
    }

    /**
     * Gets all pending account requests.
     */
    public List<AccountRequest> getPendingAccountRequests() {
        return accountRequestLogic.getPendingAccountRequests();
    }

    /**
     * Get a list of approved account requests associated with email and institute provided.
     */
    public List<AccountRequest> getApprovedAccountRequestsForEmailAndInstitute(String email, String institute) {
        return accountRequestLogic.getApprovedAccountRequestsForEmailAndInstitute(email, institute);
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
     * @throws InvalidParametersException   if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the
     *                                      database.
     */
    public Account createAccount(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountsLogic.createAccount(account);
    }

    /**
     * Deletes account and all users by googleId.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     *
     * <p>
     * Preconditions:
     * </p>
     * All parameters are non-null.
     */
    public void deleteAccountCascade(String googleId) {
        accountsLogic.deleteAccountCascade(googleId);
    }

    /**
     * Gets user associated with {@code id}.
     */
    public User getUser(UUID id) {
        return usersLogic.getUser(id);
    }

    /**
     * Gets all students associated with a googleId.
     */
    public List<Student> getStudentsByGoogleId(String googleId) {
        return usersLogic.getStudentsByGoogleId(googleId);
    }

    /**
     * Gets a course by course id.
     *
     * @param courseId courseId of the course.
     * @return the specified course.
     */
    public Course getCourse(String courseId) {
        return coursesLogic.getCourse(courseId);
    }

    /**
     * Gets a section from a course by section name.
     */
    public Section getSection(String courseId, String section) {
        return usersLogic.getSection(courseId, section);
    }

    /**
     * Gets courses associated with student.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<Course> getCoursesForStudentAccount(String googleId) {
        assert googleId != null;

        return coursesLogic.getCoursesForStudentAccount(googleId);
    }

    /**
     * Gets courses associated with instructors.
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Courses the given instructors is in except for courses in Recycle
     *         Bin.
     */
    public List<Course> getCoursesForInstructors(List<Instructor> instructorsList) {
        assert instructorsList != null;

        return coursesLogic.getCoursesForInstructors(instructorsList);
    }

    /**
     * Gets courses associated with instructors that are soft deleted.
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Courses in Recycle Bin that the given instructors is in.
     */
    public List<Course> getSoftDeletedCoursesForInstructors(List<Instructor> instructorsList) {
        assert instructorsList != null;

        return coursesLogic.getSoftDeletedCoursesForInstructors(instructorsList);
    }

    /**
     * Creates a course.
     *
     * @param course the course to create.
     * @return the created course.
     * @throws InvalidParametersException   if the course is not valid.
     * @throws EntityAlreadyExistsException if the course already exists.
     */
    public Course createCourse(Course course) throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createCourse(course);
    }

    /**
     * Creates a course and an associated instructor for the course.
     *
     * <br/>
     * Preconditions: <br/>
     * * {@code instructorGoogleId} already has an account and instructor
     * privileges.
     *
     * @param instructorGoogleId the Google ID of the instructor creating the
     *                           course.
     * @param course             the course to create.
     * @throws InvalidParametersException   if the course is not valid.
     * @throws EntityAlreadyExistsException if the course already exists.
     */
    public Course createCourseAndInstructor(String instructorGoogleId, Course course)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createCourseAndInstructor(instructorGoogleId, course);
    }

    /**
     * Deletes a course by course id.
     *
     * @param courseId of course.
     */
    public void deleteCourseCascade(String courseId) {
        coursesLogic.deleteCourseCascade(courseId);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     *
     * @return the deletion timestamp assigned to the course.
     */
    public Course moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        return coursesLogic.moveCourseToRecycleBin(courseId);
    }

    /**
     * Restores a course and all data related to the course from Recycle Bin by
     * its given corresponding ID.
     */
    public void restoreCourseFromRecycleBin(String courseId) throws EntityDoesNotExistException {
        coursesLogic.restoreCourseFromRecycleBin(courseId);
    }

    /**
     * Updates a course.
     *
     * @return updated course
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public Course updateCourse(String courseId, String name, String timezone)
            throws InvalidParametersException, EntityDoesNotExistException {
        return coursesLogic.updateCourse(courseId, name, timezone);
    }

    /**
     * Gets a list of section names for the given {@code courseId}.
     */
    public List<String> getSectionNamesForCourse(String courseId)
            throws EntityDoesNotExistException {
        return coursesLogic.getSectionNamesForCourse(courseId);
    }

    /**
     * Get section by {@code courseId} and {@code teamName}.
     */
    public Section getSectionByCourseIdAndTeam(String courseId, String teamName) {
        return coursesLogic.getSectionByCourseIdAndTeam(courseId, teamName);
    }

    /**
     * Gets the deadline extensions for a feedback session.
     */
    public Set<DeadlineExtension> getDeadlineExtensions(UUID feedbackSessionId) throws EntityDoesNotExistException {
        return deadlineExtensionsLogic.getDeadlineExtensions(feedbackSessionId);
    }

    /**
     * Gets a deadline extension by its id.
     */
    public DeadlineExtension getDeadlineExtension(UUID id) {
        return deadlineExtensionsLogic.getDeadlineExtension(id);
    }

    /**
     * Creates a deadline extension.
     *
     * @return created deadline extension
     * @throws InvalidParametersException   if the deadline extension is not valid
     * @throws EntityAlreadyExistsException if the deadline extension already exist
     */
    public DeadlineExtension createDeadlineExtension(DeadlineExtension deadlineExtension)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
    }

    /**
     * Updates the deadline extensions for a feedback session based on the provided extensions map.
     *
     * <p>The method will create new deadline extensions, update existing ones,
     * and delete any deadline extensions that are not present in the provided map.
     */
    public List<UpdateExtensionsResult> updateDeadlineExtensions(
            FeedbackSession feedbackSession, Map<UUID, Instant> extensions) throws InvalidParametersException {
        return deadlineExtensionsLogic.updateDeadlineExtensions(feedbackSession, extensions);
    }

    /**
     * Updates a deadline extension.
     *
     * @return updated deadline extension
     * @throws EntityDoesNotExistException if the deadline extension does not exist
     * @throws InvalidParametersException  if the deadline extension is not valid
     *
     */
    public DeadlineExtension updateDeadlineExtension(DeadlineExtension de)
            throws InvalidParametersException, EntityDoesNotExistException {
        return deadlineExtensionsLogic.updateDeadlineExtension(de);
    }

    /**
     * Deletes a deadline extension.
     *
     * <p>Fails silently if the deadline extension does not exist</p>
     */
    public void deleteDeadlineExtension(DeadlineExtension de) {
        deadlineExtensionsLogic.deleteDeadlineExtension(de);
    }

    /**
     * Fetch the deadline extension end time for a given user and session feedback.
     *
     * @return deadline extension instant if exists, else the default end time
     *         instant
     *         for the session feedback.
     */
    public Instant getDeadlineForUser(FeedbackSession session, User user) {
        return deadlineExtensionsLogic.getDeadlineForUser(session, user);
    }

    /**
     * Fetch the deadline extension entity for a given user and session feedback.
     *
     * @return deadline extension entity if exists, else return null.
     */
    public DeadlineExtension getDeadlineExtensionEntityForUser(FeedbackSession session, User user) {
        return deadlineExtensionsLogic.getDeadlineExtensionEntityForUser(session, user);
    }

    /**
     * Gets a list of deadline extensions with endTime coming up soon
     * and possibly need a closing soon email to be sent.
     */
    public List<DeadlineExtension> getDeadlineExtensionsPossiblyNeedingClosingSoonEmail() {
        return deadlineExtensionsLogic.getDeadlineExtensionsPossiblyNeedingClosingSoonEmail();
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
     * Gets a feedback session for {@code feedbackSessionName} and {@code courseId}.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSession(String feedbackSessionName, String courseId) {
        return feedbackSessionsLogic.getFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Gets a feedback session from the recycle bin.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if not found.
     */
    public FeedbackSession getFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the
     * instructors.
     * <br>
     * Omits sessions if the corresponding courses are in Recycle Bin
     */
    public List<FeedbackSession> getSoftDeletedFeedbackSessionsForInstructors(
            List<Instructor> instructorList) {
        assert instructorList != null;

        return feedbackSessionsLogic.getSoftDeletedFeedbackSessionsForInstructors(instructorList);
    }

    /**
     * Gets a list of feedback sessions for instructors.
     */
    public List<FeedbackSession> getFeedbackSessionsForInstructors(
            List<Instructor> instructorList) {
        assert instructorList != null;

        return feedbackSessionsLogic.getFeedbackSessionsForInstructors(instructorList);
    }

    /**
     * Gets all and only the feedback sessions ongoing within a range of time.
     */
    public List<FeedbackSession> getOngoingSessions(Instant rangeStart, Instant rangeEnd) {
        return feedbackSessionsLogic.getOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Gets a set of giver identifiers that has at least one response under a feedback session.
     *
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public Set<String> getGiverSetThatAnsweredFeedbackSession(
            UUID feedbackSessionId) throws EntityDoesNotExistException {
        return feedbackSessionsLogic.getGiverSetThatAnsweredFeedbackSession(feedbackSessionId);
    }

    /**
     * Updates a feedback session.
     *
     * @return updated feedback session
     * @throws EntityDoesNotExistException if the feedback session does not exist
     * @throws InvalidParametersException if the new fields for feedback session are invalid
     */
    public FeedbackSession updateFeedbackSession(UUID feedbackSessionId, FeedbackSessionUpdateRequest updateRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        return feedbackSessionsLogic.updateFeedbackSession(feedbackSessionId, updateRequest);
    }

    /**
     * Returns a list of sessions that require automated emails to be sent as they
     * are published.
     */
    public List<FeedbackSession> getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent() {
        return feedbackSessionsLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
    }

    /**
     * Creates a feedback session.
     *
     * @return returns the created feedback session.
     */
    public FeedbackSession createFeedbackSession(FeedbackSession feedbackSession)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert feedbackSession != null;
        assert feedbackSession.getCourse() != null && feedbackSession.getCourseId() != null;

        return feedbackSessionsLogic.createFeedbackSession(feedbackSession);
    }

    /**
     * Gets all feedback sessions of a course, except those that are soft-deleted.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourse(String courseId) {
        return feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Creates a new feedback question.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the created question
     * @throws InvalidParametersException   if the question is invalid
     * @throws EntityAlreadyExistsException if the question already exists
     */
    public FeedbackQuestion createFeedbackQuestion(FeedbackQuestion feedbackQuestion)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return feedbackQuestionsLogic.createFeedbackQuestion(feedbackQuestion);
    }

    /**
     * Publishes a feedback session.
     *
     * @return the published feedback session
     * @throws InvalidFeedbackSessionStateException if session is already published
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSession publishFeedbackSession(UUID feedbackSessionId)
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {
        return feedbackSessionsLogic.publishFeedbackSession(feedbackSessionId);
    }

    /**
     * Unpublishes a feedback session.
     *
     * @return the unpublished feedback session
     * @throws InvalidFeedbackSessionStateException if session is already unpublished
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSession unpublishFeedbackSession(UUID feedbackSessionId)
            throws EntityDoesNotExistException, InvalidFeedbackSessionStateException {
        return feedbackSessionsLogic.unpublishFeedbackSession(feedbackSessionId);
    }

    /**
     * Checks whether a student has attempted a feedback session.
     *
     * <p>
     * If there is no question for students, the feedback session is considered as
     * attempted.
     * </p>
     */
    public boolean isFeedbackSessionAttemptedByStudent(FeedbackSession session, String userEmail, String userTeam) {
        return feedbackSessionsLogic.isFeedbackSessionAttemptedByStudent(session, userEmail, userTeam);
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses,
     * deadline extensions and comments.
     *
     * <p>Fails silently if the feedback session doesn't exist.</p>
     */
    public void deleteFeedbackSessionCascade(UUID feedbackSessionId) {
        feedbackSessionsLogic.deleteFeedbackSessionCascade(feedbackSessionId);
    }

    /**
     * Soft-deletes a specific session to Recycle Bin.
     */
    public FeedbackSession moveFeedbackSessionToRecycleBin(UUID feedbackSessionId)
            throws EntityDoesNotExistException {
        return feedbackSessionsLogic.moveFeedbackSessionToRecycleBin(feedbackSessionId);
    }

    /**
     * Restores a specific session from Recycle Bin to feedback sessions table.
     */
    public FeedbackSession restoreFeedbackSessionFromRecycleBin(UUID feedbackSessionId)
            throws EntityDoesNotExistException {
        return feedbackSessionsLogic.restoreFeedbackSessionFromRecycleBin(feedbackSessionId);
    }

    /**
     * After an update to feedback session's fields, may need to adjust the email
     * status of the session.
     *
     * @param session recently updated session.
     */
    public void adjustFeedbackSessionEmailStatusAfterUpdate(FeedbackSession session) {
        assert session != null;
        feedbackSessionsLogic.adjustFeedbackSessionEmailStatusAfterUpdate(session);
    }

    /**
     * Gets the expected number of submissions for a feedback session.
     *
     * <br>
     * Preconditions: <br/>
     * * All parameters are non-null.
     */
    public int getExpectedTotalSubmission(FeedbackSession fs) {
        assert fs != null;
        return feedbackSessionsLogic.getExpectedTotalSubmission(fs);
    }

    /**
     * Gets the actual number of submissions for a feedback session.
     *
     * <br>
     * Preconditions: <br/>
     * * All parameters are non-null.
     */
    public int getActualTotalSubmission(FeedbackSession fs) {
        assert fs != null;
        return feedbackSessionsLogic.getActualTotalSubmission(fs);
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
     * <p>
     * Preconditions:
     * </p>
     * * All parameters are non-null.
     *
     * @return created notification
     * @throws InvalidParametersException   if the notification is not valid
     * @throws EntityAlreadyExistsException if the notification exists in the
     *                                      database
     */
    public Notification createNotification(Notification notification)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return notificationsLogic.createNotification(notification);
    }

    /**
     * Gets a notification by ID.
     *
     * <p>
     * Preconditions:
     * </p>
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
     * <p>
     * Preconditions:
     * </p>
     * * All parameters are non-null.
     *
     * @return updated notification
     * @throws InvalidParametersException  if the notification is not valid
     * @throws EntityDoesNotExistException if the notification does not exist in the
     *                                     database
     */
    public Notification updateNotification(UUID notificationId, Instant startTime, Instant endTime,
            NotificationStyle style, NotificationTargetUser targetUser, String title,
            String message) throws InvalidParametersException, EntityDoesNotExistException {
        return notificationsLogic.updateNotification(notificationId, startTime, endTime, style, targetUser, title,
                message);
    }

    /**
     * Deletes notification by ID.
     *
     * <p>Fails silently if the notification doesn't exist.</p>
     */
    public void deleteNotification(UUID notificationId) {
        notificationsLogic.deleteNotification(notificationId);
    }

    /**
     * Creates a read notification for the account with {@code accountId} and the notification with {@code notificationId}.
     * @throws EntityDoesNotExistException if the account or notification does not exist.
     */
    public ReadNotification createReadNotification(UUID accountId, UUID notificationId) throws EntityDoesNotExistException {
        return notificationsLogic.createReadNotification(accountId, notificationId);
    }

    /**
     * Gets a list of notifications that have been read by the account with {@code accountId}.
     */
    public List<ReadNotification> getReadNotificationsByAccountId(UUID accountId) {
        return notificationsLogic.getReadNotificationsByAccountId(accountId);
    }

    /**
     * Gets instructor associated with {@code id}.
     *
     * @param id Id of Instructor.
     * @return Returns Instructor if found else null.
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
     * Gets list of instructors by {@code googleId}.
     */
    public List<Instructor> getInstructorsForGoogleId(String googleId) {
        return usersLogic.getInstructorsForGoogleId(googleId);
    }

    /**
     * Gets a non-soft-deleted instructor with the specified email and institute.
     */
    public Instructor getInstructorForEmailAndInstitute(String email, String institute) {
        return usersLogic.getInstructorForEmailAndInstitute(email, institute);
    }

    /**
     * Gets instructors by associated {@code courseId}.
     */
    public List<Instructor> getInstructorsByCourse(String courseId) {
        return usersLogic.getInstructorsForCourse(courseId);
    }

    /**
     * Creates an instructor.
     */
    public Instructor createInstructor(Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return usersLogic.createInstructor(instructor);
    }

    /**
     * Make the instructor join the course, i.e. associate the Google ID to the
     * instructor.<br>
     * Creates an account for the instructor if no existing account is found.
     * Preconditions: <br>
     * * Parameters regkey and googleId are non-null.
     */
    public Instructor joinCourseForInstructor(String regkey, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert googleId != null;
        assert regkey != null;

        return accountsLogic.joinCourseForInstructor(regkey, googleId);
    }

    /**
     * Searches instructors in the whole system. Used by admin only.
     *
     * @return List of found instructors in the whole system. Returns an empty list
     *         if no results are found.
     */
    public List<Instructor> searchInstructorsInWholeSystem(String queryString) {
        assert queryString != null;

        return usersLogic.searchInstructorsInWholeSystem(queryString);
    }

    /**
     * Updates an instructor and cascades to responses and comments if needed.
     *
     * @return updated instructor
     * @throws InvalidParametersException  if the instructor update request is
     *                                     invalid
     * @throws InstructorUpdateException   if the update violates instructor
     *                                     validity
     * @throws EntityDoesNotExistException if the instructor does not exist in the
     *                                     database
     */
    public Instructor updateInstructorCascade(String courseId, InstructorCreateRequest instructorRequest)
            throws InvalidParametersException, InstructorUpdateException, EntityDoesNotExistException {
        return usersLogic.updateInstructorCascade(courseId, instructorRequest);
    }

    /**
     * Gets student associated with {@code id}.
     *
     * @param id Id of Student.
     * @return Returns Student if found else null.
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
     * Updates a student by student id and update request, and cascades to responses and comments if needed.
     */
    public Student updateStudent(UUID studentId, StudentUpdateRequest updateRequest)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException, EnrollException {
        return usersLogic.updateStudent(studentId, updateRequest);
    }

    /**
     * Enrolls students in a course according to the enroll requests, creating the section and team if needed.
     */
    public EnrollResults enrollStudents(Course course,
            List<StudentEnrollRequest> enrollRequests) throws EnrollException {
        return usersLogic.enrollStudents(course, enrollRequests);
    }

    /**
     * Check if the students with the provided emails exist in the course.
     */
    public boolean verifyStudentsExistInCourse(String courseId, List<String> emails) {
        return usersLogic.verifyStudentsExistInCourse(courseId, emails);
    }

    /**
     * Check if the instructors with the provided emails exist in the course.
     */
    public boolean verifyInstructorsExistInCourse(String courseId, List<String> emails) {
        return usersLogic.verifyInstructorsExistInCourse(courseId, emails);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Empty list if none found.
     */
    public List<Student> getStudentsForCourse(String courseId) {
        assert courseId != null;
        return usersLogic.getStudentsForCourse(courseId);
    }

    /**
     * Creates a student with the given parameters.
     */
    public Student createStudent(Course course, Team team, String name, String email, String comments)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return usersLogic.createStudent(course, team, name, email, comments);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Empty list if none found.
     */
    public List<Student> getUnregisteredStudentsForCourse(String courseId) {
        assert courseId != null;
        return usersLogic.getUnregisteredStudentsForCourse(courseId);
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

    /**
     * Gets students by associated {@code teamName} and {@code courseId}.
     */
    public List<Student> getStudentsByTeamName(String teamName, String courseId) {
        return usersLogic.getStudentsForTeam(teamName, courseId);
    }

    /**
     * Returns the default section.
     * If it does not exist, create and return it.
     */
    public Section getDefaultSectionOrCreate(String courseId) {
        return usersLogic.getSectionOrCreate(courseId, Const.DEFAULT_SECTION);
    }

    /**
     * Creates a section.
     */
    public Section createSection(Course course, String sectionName)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createSection(course, sectionName);
    }

    /**
     * Creates a team.
     */
    public Team createTeam(Section section, String teamName)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createTeam(section, teamName);
    }

    /**
     * Search for students. Preconditions: all parameters are non-null.
     *
     * @param instructors a list of Instructors associated to a googleId,
     *                    used for filtering of search result
     * @return an empty list if no match is found
     */
    public List<Student> searchStudents(String queryString, List<Instructor> instructors) {
        assert queryString != null;
        assert instructors != null;
        return usersLogic.searchStudents(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not
     * restrict the
     * visibility according to the logged-in user's google ID. This is used by admin
     * to
     * search students in the whole system.
     *
     * @return an empty list if no match is found.
     */
    public List<Student> searchStudentsInWholeSystem(String queryString) {
        assert queryString != null;

        return usersLogic.searchStudentsInWholeSystem(queryString);
    }

    /**
     * Deletes a student cascade its associated feedback responses, deadline
     * extensions and comments.
     *
     * <p>
     * Fails silently if the student does not exist.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteStudentCascade(String courseId, String studentEmail) {
        assert courseId != null;
        assert studentEmail != null;

        usersLogic.deleteStudentCascade(courseId, studentEmail);
    }

    /**
     * Deletes all the students in the course cascade their associated responses,
     * deadline extensions and comments.
     *
     * <br/>
     * Preconditions: <br/>
     * Parameter is non-null.
     */
    public void deleteStudentsInCourseCascade(String courseId) {
        assert courseId != null;

        usersLogic.deleteStudentsInCourseCascade(courseId);
    }

    /**
     * Make the student join the course, i.e. associate the Google ID to the
     * student.<br>
     * Create an account for the student if no existing account is found.
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @param key the registration key
     */
    public Student joinCourseForStudent(String key, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert googleId != null;
        assert key != null;

        return accountsLogic.joinCourseForStudent(key, googleId);

    }

    /**
     * Deletes an instructor and cascades deletion to
     * associated feedback responses, deadline extensions and comments.
     *
     * <p>
     * Fails silently if the instructor does not exist.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteInstructorCascade(String courseId, String email) {
        assert courseId != null;
        assert email != null;

        usersLogic.deleteInstructorCascade(courseId, email);
    }

    public List<Notification> getAllNotifications() {
        return notificationsLogic.getAllNotifications();
    }

    /**
     * Resets the googleId associated with the instructor.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If instructor cannot be found with given
     *                                     email and courseId.
     */
    public void resetInstructorGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        usersLogic.resetInstructorGoogleId(email, courseId, googleId);
    }

    /**
     * Resets the googleId associated with the student.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If student cannot be found with given
     *                                     email and courseId.
     */
    public void resetStudentGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        usersLogic.resetStudentGoogleId(email, courseId, googleId);
    }

    /**
     * Regenerates the registration key for the instructor with email address
     * {@code email} in course {@code courseId}.
     *
     * @return the instructor with the new registration key.
     * @throws InstructorUpdateException   if system was unable to generate a new
     *                                     registration key.
     * @throws EntityDoesNotExistException if the instructor does not exist.
     */
    public Instructor regenerateInstructorRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, InstructorUpdateException {

        assert courseId != null;
        assert email != null;

        return usersLogic.regenerateInstructorRegistrationKey(courseId, email);
    }

    /**
     * Regenerates the registration key for the student with email address
     * {@code email} in course {@code courseId}.
     *
     * @return the student with the new registration key.
     * @throws StudentUpdateException      if system was unable to generate a new
     *                                     registration key.
     * @throws EntityDoesNotExistException if the student does not exist.
     */
    public Student regenerateStudentRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, StudentUpdateException {

        assert courseId != null;
        assert email != null;

        return usersLogic.regenerateStudentRegistrationKey(courseId, email);
    }

    /**
     * Updates the instructor being edited to ensure validity of instructors for the
     * course.
     * * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see UsersLogic#updateToEnsureValidityOfInstructorsForTheCourse(String,
     *      Instructor)
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, Instructor instructorToEdit) {
        assert courseId != null;
        assert instructorToEdit != null;

        usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);
    }

    /**
     * Returns active notification for general users and the specified
     * {@code targetUser}.
     */
    public List<Notification> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        return notificationsLogic.getActiveNotificationsByTargetUser(targetUser);
    }

    /**
     * Returns active unread notifications for the specified {@code targetUsers} and {@code accountId}.
     */
    public List<Notification> getUnreadActiveNotificationsByTargetUser(
            List<NotificationTargetUser> targetUsers, UUID accountId, Instant now) {
        return notificationsLogic.getUnreadActiveNotificationsByTargetUser(targetUsers, accountId, now);
    }

    /**
     * Gets all questions for a feedback session.<br>
     * Returns an empty list if they are no questions
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForSession(FeedbackSession feedbackSession) {
        assert feedbackSession != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForSession(feedbackSession);
    }

    /**
     * Gets a list of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForStudents(FeedbackSession feedbackSession) {
        assert feedbackSession != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForStudents(feedbackSession);
    }

    /**
     * Gets a {@code List} of all questions for the given session that
     * instructor can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForInstructors(
            FeedbackSession feedbackSession, String instructorEmail) {
        assert feedbackSession != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForInstructors(feedbackSession, instructorEmail);
    }

    /**
     * Gets the session result for a feedback session.
     *
     * @param feedbackSession the feedback session
     * @param instructorEmail the email of the instructor requesting for the session result
     * @param questionId if not null, will only return partial bundle for the question
     * @param sectionName if not null, will only return partial bundle for the section
     * @param fetchType if not null, will fetch responses by giver, receiver sections, or both
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResults(
            FeedbackSession feedbackSession, String instructorEmail,
            @Nullable UUID questionId, @Nullable String sectionName, @Nullable FeedbackResultFetchType fetchType) {
        return feedbackResponsesLogic.getSessionResults(
                feedbackSession, instructorEmail, questionId, sectionName, fetchType);
    }

    /**
     * Gets the session result for a feedback session for the given user.
     *
     * @see FeedbackResponsesLogic#getSessionResultsForUser(FeedbackSession, String,
     *      String, boolean, String)
     */
    public SessionResultsBundle getSessionResultsForUser(
            FeedbackSession feedbackSession, String userEmail, boolean isInstructor,
            @Nullable UUID questionId, boolean isPreviewResults) {
        return feedbackResponsesLogic.getSessionResultsForUser(
                feedbackSession, userEmail, isInstructor, questionId, isPreviewResults);
    }

    /**
     * Persists the given data bundle to the database.
     */
    public DataBundle persistDataBundle(DataBundle dataBundle)
            throws InvalidParametersException {
        return dataBundleLogic.persistDataBundle(dataBundle);
    }

    /**
     * Removes the given data bundle from the database.
     */
    public void removeDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        dataBundleLogic.removeDataBundle(dataBundle);
    }

    /**
     * Gets the dynamically generated options for a question if applicable.
     *
     * <p>
     * This only applies to MCQ and MSQ questions with "generate options for" field.
     *
     * @param feedbackQuestion the question to get the dynamically generated options for
     * @param student the student who is doing the question, or null if the entity doing the question is an instructor
     *
     * @return an Optional containing a list of dynamically generated options, or an empty Optional if not applicable
     */
    public Optional<List<String>> getDynamicallyGeneratedOptions(FeedbackQuestion feedbackQuestion, Student student) {
        return feedbackQuestionsLogic.getDynamicallyGeneratedOptions(feedbackQuestion, student);
    }

    /**
     * Gets a feedback question.
     *
     * @return null if not found.
     */
    public FeedbackQuestion getFeedbackQuestion(UUID id) {
        return feedbackQuestionsLogic.getFeedbackQuestion(id);
    }

    /**
     * Deletes a feedback question cascade its responses and comments.
     *
     * <p>
     * Silently fail if question does not exist.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackQuestionCascade(UUID questionId) {
        feedbackQuestionsLogic.deleteFeedbackQuestionCascade(questionId);
    }

    /**
     * Gets the recipients of a feedback question for student.
     *
     * @see FeedbackQuestionsLogic#getRecipientsOfQuestion
     */
    public Map<String, FeedbackQuestionRecipient> getRecipientsOfQuestion(
            FeedbackQuestion question,
            @Nullable Instructor instructorGiver, @Nullable Student studentGiver) {
        assert question != null;

        return feedbackQuestionsLogic.getRecipientsOfQuestion(question, instructorGiver, studentGiver, null);
    }

    /**
     * Gets a list of students with the specified email.
     */
    public List<Student> getAllStudentsForEmail(String email) {
        return usersLogic.getAllStudentsForEmail(email);
    }

    /**
     * Gets a feedbackResponse or null if it does not exist.
     */
    public FeedbackResponse getFeedbackResponse(UUID frId) {
        return feedbackResponsesLogic.getFeedbackResponse(frId);
    }

    /**
     * Creates a feedback response.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return created feedback response
     * @throws InvalidParametersException   if the response is not valid
     * @throws EntityAlreadyExistsException if the response already exist
     */
    public FeedbackResponse createFeedbackResponse(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert feedbackResponse != null;
        return feedbackResponsesLogic.createFeedbackResponse(feedbackResponse);
    }

    /**
     * Deletes a feedback response and its associated feedback response comments.
     *
     * <p>Fails silently if the feedback response doesn't exist.</p>
     */
    public void deleteFeedbackResponsesAndCommentsCascade(FeedbackResponse feedbackResponse) {
        feedbackResponsesLogic.deleteFeedbackResponsesAndCommentsCascade(feedbackResponse);
    }

    /**
     * Get existing feedback responses from instructor for the given question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromInstructorForQuestion(
            FeedbackQuestion question, Instructor instructor) {
        return feedbackResponsesLogic.getFeedbackResponsesFromInstructorForQuestion(
                question, instructor);
    }

    /**
     * Get existing feedback responses from student or his team for the given
     * question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromStudentOrTeamForQuestion(
            FeedbackQuestion question, Student student) {
        return feedbackResponsesLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(
                question, student);
    }

    /**
     * Gets an feedback response comment by feedback response comment id.
     *
     * @param id of feedback response comment.
     * @return the specified feedback response comment.
     */
    public FeedbackResponseComment getFeedbackResponseComment(UUID id) {
        return feedbackResponseCommentsLogic.getFeedbackResponseComment(id);
    }

    /**
     * Updates a feedback response comment.
     *
     * @throws EntityDoesNotExistException if the comment does not exist
     */
    public FeedbackResponseComment updateFeedbackResponseComment(UUID frcId,
            FeedbackResponseCommentUpdateRequest updateRequest, String updaterEmail)
            throws EntityDoesNotExistException {
        return feedbackResponseCommentsLogic.updateFeedbackResponseComment(frcId, updateRequest, updaterEmail);
    }

    /**
     * Updates a feedback response and comments by {@link FeedbackResponse}.
     *
     * <p>
     * Cascade updates its associated feedback response comment
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback response
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponse updateFeedbackResponseCascade(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert feedbackResponse != null;

        return feedbackResponsesLogic.updateFeedbackResponse(feedbackResponse);
    }

    /**
     * Checks whether there are responses for a question.
     */
    public boolean areThereResponsesForQuestion(UUID questionId) {
        return feedbackResponsesLogic.areThereResponsesForQuestion(questionId);
    }

    /**
     * Checks whether there are responses for a course.
     */
    public boolean hasResponsesForCourse(String courseId) {
        return feedbackResponsesLogic.hasResponsesForCourse(courseId);
    }

    /**
     * Gets the comment associated with the response.
     */
    public FeedbackResponseComment getFeedbackResponseCommentForResponseFromParticipant(
            UUID feedbackResponseId) {
        return feedbackResponseCommentsLogic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
    }

    /**
     * Creates a feedback response comment.
     *
     * @throws EntityAlreadyExistsException if the comment alreadty exists
     * @throws InvalidParametersException   if the comment is invalid
     */
    public FeedbackResponseComment createFeedbackResponseComment(FeedbackResponseComment frc)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return feedbackResponseCommentsLogic.createFeedbackResponseComment(frc);
    }

    /**
     * Deletes a feedbackResponseComment.
     *
     * <p>Fails silently if the comment does not exist.</p>
     */
    public void deleteFeedbackResponseComment(UUID frcId) {
        feedbackResponseCommentsLogic.deleteFeedbackResponseComment(frcId);
    }

    /**
     * Gets all feedback responses from a giver for a question.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverForCourse(String courseId, String giverEmail) {
        return feedbackResponsesLogic.getFeedbackResponsesFromGiverForCourse(courseId, giverEmail);
    }

    /**
     * Gets all feedback responses for a recipient for a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesForRecipientForCourse(String courseId, String recipientEmail) {
        return feedbackResponsesLogic.getFeedbackResponsesForRecipientForCourse(courseId, recipientEmail);
    }

    /**
     * Updates a feedback question by {@code FeedbackQuestionUpdateRequest}.
     *
     * <p>
     * Cascade adjust the question number of questions in the same session.
     *
     * <p>
     * Cascade adjust the existing response of the question.
     *
     * <br/>
     * Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback question
     * @throws InvalidParametersException  if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback question cannot be found
     */
    public FeedbackQuestion updateFeedbackQuestionCascade(UUID questionId, FeedbackQuestionUpdateRequest updateRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        return feedbackQuestionsLogic.updateFeedbackQuestionCascade(questionId, updateRequest);
    }

    /**
     * Returns a list of feedback sessions that need an "Open" email to be sent.
     */
    public List<FeedbackSession> getFeedbackSessionsWhichNeedOpenedEmailsToBeSent() {
        return feedbackSessionsLogic.getFeedbackSessionsWhichNeedOpenedEmailsToBeSent();
    }

    /**
     * Returns a list of sessions that were closed within past hour.
     */
    public List<FeedbackSession> getFeedbackSessionsClosedWithinThePastHour() {
        return feedbackSessionsLogic.getFeedbackSessionsClosedWithinThePastHour();
    }

    /**
     * This is used by admin to search account requests in the whole system.
     *
     * @return A list of matching {@link AccountRequest}s, or an empty list if no match is found.
     */
    public List<AccountRequest> searchAccountRequestsInWholeSystem(String queryString) {
        assert queryString != null;

        return accountRequestLogic.searchAccountRequestsInWholeSystem(queryString);
    }

    /**
     * Returns a list of sessions that are going to close soon.
     */
    public List<FeedbackSession> getFeedbackSessionsClosingWithinTimeLimit() {
        return feedbackSessionsLogic.getFeedbackSessionsClosingWithinTimeLimit();
    }

    /**
     * Returns a list of sessions that are going to open soon.
     */
    public List<FeedbackSession> getFeedbackSessionsOpeningWithinTimeLimit() {
        return feedbackSessionsLogic.getFeedbackSessionsOpeningWithinTimeLimit();
    }

    /**
     * Creates feedback session log.
     */
    public FeedbackSessionLog createFeedbackSessionLog(
            FeedbackSession feedbackSession, Student student,
            FeedbackSessionLogType logType, Instant timestamp) throws InvalidParametersException {
        return feedbackSessionLogsLogic.createFeedbackSessionLog(feedbackSession, student, logType, timestamp);
    }

    /**
     * Deletes feedback session logs older than the given cutoff time.
     */
    public int deleteFeedbackSessionLogsOlderThan(Instant cutoffTime) {
        return feedbackSessionLogsLogic.deleteFeedbackSessionLogsOlderThan(cutoffTime);
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters ordered by
     * ascending timestamp. Logs with the same timestamp will be ordered by the
     * student's email.
     *
     * @param studentId         Can be null
     * @param feedbackSessionId Can be null
     */
    public List<FeedbackSessionLog> getOrderedFeedbackSessionLogs(String courseId, UUID studentId,
            UUID feedbackSessionId, Instant startTime, Instant endTime) {
        return feedbackSessionLogsLogic.getOrderedFeedbackSessionLogs(courseId, studentId, feedbackSessionId, startTime,
                endTime);
    }
}
