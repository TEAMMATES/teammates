package teammates.sqllogic.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.SqlDataBundle;
import teammates.common.datatransfer.SqlSessionResultsBundle;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.exception.StudentUpdateException;
import teammates.common.util.Const;
import teammates.sqllogic.core.AccountRequestsLogic;
import teammates.sqllogic.core.AccountsLogic;
import teammates.sqllogic.core.CoursesLogic;
import teammates.sqllogic.core.DataBundleLogic;
import teammates.sqllogic.core.DeadlineExtensionsLogic;
import teammates.sqllogic.core.FeedbackQuestionsLogic;
import teammates.sqllogic.core.FeedbackResponseCommentsLogic;
import teammates.sqllogic.core.FeedbackResponsesLogic;
import teammates.sqllogic.core.FeedbackSessionLogsLogic;
import teammates.sqllogic.core.FeedbackSessionsLogic;
import teammates.sqllogic.core.NotificationsLogic;
import teammates.sqllogic.core.UsageStatisticsLogic;
import teammates.sqllogic.core.UsersLogic;
import teammates.storage.sqlentity.Account;
import teammates.storage.sqlentity.AccountRequest;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.DeadlineExtension;
import teammates.storage.sqlentity.FeedbackQuestion;
import teammates.storage.sqlentity.FeedbackResponse;
import teammates.storage.sqlentity.FeedbackResponseComment;
import teammates.storage.sqlentity.FeedbackSession;
import teammates.storage.sqlentity.FeedbackSessionLog;
import teammates.storage.sqlentity.Instructor;
import teammates.storage.sqlentity.Notification;
import teammates.storage.sqlentity.Section;
import teammates.storage.sqlentity.Student;
import teammates.storage.sqlentity.Team;
import teammates.storage.sqlentity.UsageStatistics;
import teammates.storage.sqlentity.User;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.request.FeedbackResponseCommentUpdateRequest;
import teammates.ui.request.InstructorCreateRequest;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
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
     * @throws InvalidParametersException if the account request details are invalid.
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
     * Gets the account request with the given {@code id}.
     *
     * @return account request with the given {@code id}.
     */
    public AccountRequest getAccountRequestWithTransaction(UUID id) {
        return accountRequestLogic.getAccountRequestWithTransaction(id);
    }

    /**
     * Creates a or gets an account request.
     *
     * @return newly created account request.
     * @throws InvalidParametersException if the account request details are invalid.
     * @throws EntityAlreadyExistsException if the account request already exists.
     */
    public AccountRequest createAccountRequestWithTransaction(String name, String email, String institute,
            AccountRequestStatus status, String comments) throws InvalidParametersException {

        return accountRequestLogic.createOrGetAccountRequestWithTransaction(name, email, institute, status, comments);
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
            throws InvalidParametersException, EntityDoesNotExistException {
        return accountRequestLogic.updateAccountRequest(accountRequest);
    }

    /**
     * Updates the given account request.
     *
     * @return the updated account request.
     */
    public AccountRequest updateAccountRequestWithTransaction(AccountRequest accountRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        return accountRequestLogic.updateAccountRequestWithTransaction(accountRequest);
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
     * <p>Preconditions:</p>
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
     * Gets all pending account requests.
     */
    public List<AccountRequest> getAllAccountRequests() {
        return accountRequestLogic.getAllAccountRequests();
    }

    /**
     * Get a list of account requests associated with email provided.
     */
    public List<AccountRequest> getApprovedAccountRequestsForEmailWithTransaction(String email) {
        return accountRequestLogic.getApprovedAccountRequestsForEmailWithTransaction(email);
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
     * Get a list of accounts associated with email provided.
     */
    public List<Account> getAccountsForEmailWithTransaction(String email) {
        return accountsLogic.getAccountsForEmailWithTransaction(email);
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
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the database.
     */
    public Account createAccountWithTransaction(Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountsLogic.createAccountWithTransaction(account);
    }

    /**
     * Deletes account by googleId.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * All parameters are non-null.
     */
    public void deleteAccount(String googleId) {
        accountsLogic.deleteAccount(googleId);
    }

    /**
     * Deletes account and all users by googleId.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * All parameters are non-null.
     */
    public void deleteAccountCascade(String googleId) {
        accountsLogic.deleteAccountCascade(googleId);
    }

    /**
     * Gets all students associated with a googleId.
     */
    public List<Student> getStudentsByGoogleId(String googleId) {
        return usersLogic.getStudentsByGoogleId(googleId);
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
     * @return Courses the given instructors is in except for courses in Recycle Bin.
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
     * Gets the institute of the course.
     */
    public String getCourseInstitute(String courseId) {
        return coursesLogic.getCourseInstitute(courseId);
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
     * Deletes a course by course id.
     * @param courseId of course.
     */
    public void deleteCourseCascade(String courseId) {
        coursesLogic.deleteCourseCascade(courseId);
    }

    /**
     * Updates a student by {@link Student}.
     *
     * <p>If email changed, update by recreating the student and cascade update all responses
     * and comments the student gives/receives.
     *
     * <p>If team changed, cascade delete all responses the student gives/receives within that team.
     *
     * <p>If section changed, cascade update all responses the student gives/receives.
     *
     * <br/>Preconditions: <br/>
     * * Student parameter is non-null.
     *
     * @return updated student
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the student cannot be found
     * @throws EntityAlreadyExistsException if the student cannot be updated
     *         by recreation because of an existent student
     */
    public Student updateStudentCascade(Student student)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert student != null;

        return usersLogic.updateStudentCascade(student);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
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
     * @throws InvalidParametersException if attributes to update are not valid
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
     * Updates a deadline extension.
     *
     * @return updated deadline extension
     * @throws EntityDoesNotExistException if the deadline extension does not exist
     * @throws InvalidParametersException if the deadline extension is not valid
     *
     */
    public DeadlineExtension updateDeadlineExtension(DeadlineExtension de)
            throws InvalidParametersException, EntityDoesNotExistException {
        return deadlineExtensionsLogic.updateDeadlineExtension(de);
    }

    /**
     * Deletes a deadline extension.
     */
    public void deleteDeadlineExtension(DeadlineExtension de) {
        deadlineExtensionsLogic.deleteDeadlineExtension(de);
    }

    /**
     * Fetch the deadline extension end time for a given user and session feedback.
     *
     * @return deadline extension instant if exists, else the default end time instant
     *         for the session feedback.
     */
    public Instant getDeadlineForUser(FeedbackSession session, User user) {
        return deadlineExtensionsLogic.getDeadlineForUser(session, user);
    }

    /**
     * Fetch the deadline extension end time for a given user and session feedback.
     *
     * @return deadline extension instant if exists, else return null since no deadline extensions.
     */
    public Instant getExtendedDeadlineForUser(FeedbackSession session, User user) {
        return deadlineExtensionsLogic.getExtendedDeadlineForUser(session, user);
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
     * Gets a feedback session reference.
     *
     * @return Returns a proxy for the feedback session.
     */
    public FeedbackSession getFeedbackSessionReference(UUID id) {
        return feedbackSessionsLogic.getFeedbackSessionReference(id);
    }

    /**
     * Gets a feedback session from the recycle bin.
     *
     * <br/>Preconditions: <br/>
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
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the instructors.
     * <br>
     * Omits sessions if the corresponding courses are archived or in Recycle Bin
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
     */
    public Set<String> getGiverSetThatAnsweredFeedbackSession(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.getGiverSetThatAnsweredFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Updates a feedback session.
     *
     * @return returns the updated feedback session.
     */
    public FeedbackSession updateFeedbackSession(FeedbackSession feedbackSession)
            throws InvalidParametersException, EntityDoesNotExistException {
        return feedbackSessionsLogic.updateFeedbackSession(feedbackSession);
    }

    /**
     * Returns a list of sessions that require automated emails to be sent as they are published.
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
        assert feedbackSession.getCourse() != null && feedbackSession.getCourse().getId() != null;

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
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the created question
     * @throws InvalidParametersException if the question is invalid
     * @throws EntityAlreadyExistsException if the question already exists
     */
    public FeedbackQuestion createFeedbackQuestion(FeedbackQuestion feedbackQuestion)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return feedbackQuestionsLogic.createFeedbackQuestion(feedbackQuestion);
    }

    /**
     * Publishes a feedback session.
     * @return the published feedback session
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     * @throws InvalidParametersException if session is already published
     */
    public FeedbackSession publishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.publishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Checks whether a student has attempted a feedback session.
     *
     * <p>If there is no question for students, the feedback session is considered as attempted.</p>
     */
    public boolean isFeedbackSessionAttemptedByStudent(FeedbackSession session, String userEmail, String userTeam) {
        return feedbackSessionsLogic.isFeedbackSessionAttemptedByStudent(session, userEmail, userTeam);
    }

    /**
     * Checks whether an instructor has attempted a feedback session.
     *
     * <p>If there is no question for instructors, the feedback session is considered as attempted.</p>
     */
    public boolean isFeedbackSessionAttemptedByInstructor(FeedbackSession session, String userEmail) {
        return feedbackSessionsLogic.isFeedbackSessionAttemptedByInstructor(session, userEmail);
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses, deadline extensions and comments.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {
        feedbackSessionsLogic.deleteFeedbackSessionCascade(feedbackSessionName, courseId);
    }

    /**
     * Soft-deletes a specific session to Recycle Bin.
     */
    public FeedbackSession moveFeedbackSessionToRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Restores a specific session from Recycle Bin to feedback sessions table.
     */
    public void restoreFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        assert feedbackSessionName != null;
        assert courseId != null;

        feedbackSessionsLogic.restoreFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Unpublishes a feedback session.
     * @return the unpublished feedback session
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     * @throws InvalidParametersException
     *             if the feedback session is not ready to be unpublished.
     */
    public FeedbackSession unpublishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.unpublishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * After an update to feedback session's fields, may need to adjust the email status of the session.
     * @param session recently updated session.
     */
    public void adjustFeedbackSessionEmailStatusAfterUpdate(FeedbackSession session) {
        assert session != null;
        feedbackSessionsLogic.adjustFeedbackSessionEmailStatusAfterUpdate(session);
    }

    /**
     * Gets the expected number of submissions for a feedback session.
     *
     * <br>Preconditions: <br>
     * * All parameters are non-null.
     */
    public int getExpectedTotalSubmission(FeedbackSession fs) {
        assert fs != null;
        return feedbackSessionsLogic.getExpectedTotalSubmission(fs);
    }

    /**
     * Gets the actual number of submissions for a feedback session.
     *
     * <br>Preconditions: <br>
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
     * Gets list of instructors by {@code googleId}.
     */
    public List<Instructor> getInstructorsForGoogleId(String googleId) {
        return usersLogic.getInstructorsForGoogleId(googleId);
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
     * Make the instructor join the course, i.e. associate the Google ID to the instructor.<br>
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
     * Validates that the join course request is valid, then
     * makes the instructor join the course, i.e. associate an account to the instructor with the given googleId.
     * Creates an account for the instructor if no existing account is found.
     * Preconditions:
     * Parameters regkey and googleId are non-null.
     */
    public Instructor joinCourseForInstructor(String googleId, Instructor instructor)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        if (googleId == null) {
            throw new InvalidParametersException("Instructor's googleId cannot be null");
        }
        if (instructor == null) {
            throw new InvalidParametersException("Instructor cannot be null");
        }

        validateJoinCourseRequest(googleId, instructor);
        return usersLogic.joinCourseForInstructor(googleId, instructor);
    }

    /**
     * Validates that the instructor can join the course it has as courseId field.
     *
     * @return true if the instructor can join the course.
     * @throws Exception if the instructor cannot join the course.
     */
    private boolean validateJoinCourseRequest(String googleId, Instructor instructor)
            throws EntityAlreadyExistsException, EntityDoesNotExistException {
        if (instructor == null) {
            throw new EntityDoesNotExistException("Instructor not found");
        }

        // check course exists and has not been deleted
        Course course = getCourse(instructor.getCourseId());

        if (course == null) {
            throw new EntityDoesNotExistException("Course with id " + instructor.getCourseId() + " does not exist");
        }
        if (course.isCourseDeleted()) {
            throw new EntityDoesNotExistException("The course you are trying to join has been deleted by an instructor");
        }

        if (instructor.isRegistered()) {
            throw new EntityAlreadyExistsException("Instructor has already joined course");
        } else {
            // Check if this Google ID has already joined this course with courseId
            Instructor existingInstructor =
                    usersLogic.getInstructorByGoogleId(instructor.getCourseId(), googleId);
            if (existingInstructor != null) {
                throw new EntityAlreadyExistsException("Instructor has already joined course");
            }
        }
        return true;
    }

    /**
     * Searches instructors in the whole system. Used by admin only.
     *
     * @return List of found instructors in the whole system. Null if no result found.
     */
    public List<Instructor> searchInstructorsInWholeSystem(String queryString)
            throws SearchServiceException {
        assert queryString != null;

        return usersLogic.searchInstructorsInWholeSystem(queryString);
    }

    /**
     * Updates an instructor and cascades to responses and comments if needed.
     *
     * @return updated instructor
     * @throws InvalidParametersException if the instructor update request is invalid
     * @throws InstructorUpdateException if the update violates instructor validity
     * @throws EntityDoesNotExistException if the instructor does not exist in the database
     */
    public Instructor updateInstructorCascade(String courseId, InstructorCreateRequest instructorRequest) throws
            InvalidParametersException, InstructorUpdateException, EntityDoesNotExistException {
        return usersLogic.updateInstructorCascade(courseId, instructorRequest);
    }

    /**
     * Checks if an instructor with {@code googleId} can create a course with {@code institute}.
     */
    public boolean canInstructorCreateCourse(String googleId, String institute) {
        return usersLogic.canInstructorCreateCourse(googleId, institute);
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
     * Gets student reference associated with {@code id}.
     *
     * @param id    Id of Student.
     * @return      Returns a proxy for the Student.
     */
    public Student getStudentReference(UUID id) {
        return usersLogic.getStudentReference(id);
    }

    /**
     * Gets student associated with {@code courseId} and {@code email}.
     */
    public Student getStudentForEmail(String courseId, String email) {
        return usersLogic.getStudentForEmail(courseId, email);
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
     * @return Empty list if none found.
     */
    public List<Student> getStudentsForCourse(String courseId) {
        assert courseId != null;
        return usersLogic.getStudentsForCourse(courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
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
     * Returns the default SQL section.
     * If it does not exist, create and return it.
     */
    public Section getDefaultSectionOrCreate(String courseId) {
        return getSectionOrCreate(courseId, Const.DEFAULT_SECTION);
    }

    /**
     * Gets a team by associated {@code courseId} and {@code sectionName}.
     */
    public Section getSectionOrCreate(String courseId, String sectionName) {
        return usersLogic.getSectionOrCreate(courseId, sectionName);
    }

    /**
     * Gets a team by associated {@code section} and {@code teamName}.
     */
    public Team getTeamOrCreate(Section section, String teamName) {
        return usersLogic.getTeamOrCreate(section, teamName);
    }

    /**
     * Creates a student.
     *
     * @return the created student
     * @throws InvalidParametersException if the student is not valid
     * @throws EntityAlreadyExistsException if the student already exists in the database.
     */
    public Student createStudent(Student student) throws InvalidParametersException, EntityAlreadyExistsException {
        return usersLogic.createStudent(student);
    }

    /**
     * Search for students. Preconditions: all parameters are non-null.
     * @param instructors   a list of Instructors associated to a googleId,
     *                      used for filtering of search result
     * @return Null if no match found
     */
    public List<Student> searchStudents(String queryString, List<Instructor> instructors)
            throws SearchServiceException {
        assert queryString != null;
        assert instructors != null;
        return usersLogic.searchStudents(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @return Null if no match found.
     */
    public List<Student> searchStudentsInWholeSystem(String queryString)
            throws SearchServiceException {
        assert queryString != null;

        return usersLogic.searchStudentsInWholeSystem(queryString);
    }

    /**
     * Deletes a student cascade its associated feedback responses, deadline
     * extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
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
     * Deletes all the students in the course cascade their associated responses, deadline extensions and comments.
     *
     * <br/>Preconditions: <br>
     * Parameter is non-null.
     */
    public void deleteStudentsInCourseCascade(String courseId) {
        assert courseId != null;

        usersLogic.deleteStudentsInCourseCascade(courseId);
    }

    /**
     * Make the student join the course, i.e. associate the Google ID to the student.<br>
     * Create an account for the student if no existing account is found.
     * Preconditions: <br>
     * * All parameters are non-null.
     * @param key the registration key
     */
    public Student joinCourseForStudent(String key, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert googleId != null;
        assert key != null;

        return accountsLogic.joinCourseForStudent(key, googleId);

    }

    /**
     * Gets all instructors and students by associated {@code googleId}.
     */
    public List<User> getAllUsersByGoogleId(String googleId) {
        return usersLogic.getAllUsersByGoogleId(googleId);
    }

    /**
     * Deletes a user.
     *
     * <p>Fails silently if the user does not exist.</p>
     */
    public <T extends User> void deleteUser(T user) {
        usersLogic.deleteUser(user);
    }

    /**
     * Deletes an instructor and cascades deletion to
     * associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the instructor does not exist.
     *
     * <br/>Preconditions: <br/>
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
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If instructor cannot be found with given email and courseId.
     */
    public void resetInstructorGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        usersLogic.resetInstructorGoogleId(email, courseId, googleId);
    }

    /**
     * Resets the googleId associated with the student.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If student cannot be found with given email and courseId.
     */
    public void resetStudentGoogleId(String email, String courseId, String googleId)
            throws EntityDoesNotExistException {
        usersLogic.resetStudentGoogleId(email, courseId, googleId);
    }

    /**
     * Regenerates the registration key for the instructor with email address {@code email} in course {@code courseId}.
     *
     * @return the instructor with the new registration key.
     * @throws InstructorUpdateException if system was unable to generate a new registration key.
     * @throws EntityDoesNotExistException if the instructor does not exist.
     */
    public Instructor regenerateInstructorRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, InstructorUpdateException {

        assert courseId != null;
        assert email != null;

        return usersLogic.regenerateInstructorRegistrationKey(courseId, email);
    }

    /**
     * Regenerates the registration key for the student with email address {@code email} in course {@code courseId}.
     *
     * @return the student with the new registration key.
     * @throws StudentUpdateException if system was unable to generate a new registration key.
     * @throws EntityDoesNotExistException if the student does not exist.
     */
    public Student regenerateStudentRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, StudentUpdateException {

        assert courseId != null;
        assert email != null;

        return usersLogic.regenerateStudentRegistrationKey(courseId, email);
    }

    /**
     * Updates the instructor being edited to ensure validity of instructors for the course.
     * * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see UsersLogic#updateToEnsureValidityOfInstructorsForTheCourse(String, Instructor)
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, Instructor instructorToEdit) {
        assert courseId != null;
        assert instructorToEdit != null;

        usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);
    }

    /**
     * Returns active notification for general users and the specified {@code targetUser}.
     */
    public List<Notification> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        return notificationsLogic.getActiveNotificationsByTargetUser(targetUser);
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
     * Gets the unique feedback question based on sessionId and questionNumber.
     */
    public FeedbackQuestion getFeedbackQuestionForSessionQuestionNumber(UUID sessionId, int questionNumber) {
        return feedbackQuestionsLogic.getFeedbackQuestionForSessionQuestionNumber(sessionId, questionNumber);
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
     * @see FeedbackResponsesLogic#getSessionResultsForCourse(
     * FeedbackSession, String, String, String, Section, FeedbackResultFetchType)
     */
    public SqlSessionResultsBundle getSessionResultsForCourse(
            FeedbackSession feedbackSession, String courseId, String userEmail,
            @Nullable UUID questionId, @Nullable String sectionName, @Nullable FeedbackResultFetchType fetchType) {
        assert feedbackSession != null;
        assert courseId != null;
        assert userEmail != null;

        return feedbackResponsesLogic.getSessionResultsForCourse(
                feedbackSession, courseId, userEmail, questionId, sectionName, fetchType);
    }

    /**
     * Gets the session result for a feedback session for the given user.
     *
     * @see FeedbackResponsesLogic#getSessionResultsForUser(FeedbackSession, String, String, boolean, String)
     */
    public SqlSessionResultsBundle getSessionResultsForUser(
            FeedbackSession feedbackSession, String courseId, String userEmail, boolean isInstructor,
            @Nullable UUID questionId, boolean isPreviewResults) {
        assert feedbackSession != null;
        assert courseId != null;
        assert userEmail != null;

        return feedbackResponsesLogic.getSessionResultsForUser(
                feedbackSession, courseId, userEmail, isInstructor, questionId, isPreviewResults);
    }

    /**
     * Persists the given data bundle to the database.
     */
    public SqlDataBundle persistDataBundle(SqlDataBundle dataBundle)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        return dataBundleLogic.persistDataBundle(dataBundle);
    }

    /**
     * Puts searchable documents from the data bundle to the database.
     *
     * @see DataBundleLogic#putDocuments(DataBundle)
     */
    public void putDocuments(SqlDataBundle dataBundle) throws SearchServiceException {
        dataBundleLogic.putDocuments(dataBundle);
    }

    /**
     * Puts searchable instructor to the database.
     */
    public void putInstructorDocument(Instructor instructor) throws SearchServiceException {
        usersLogic.putInstructorDocument(instructor);
    }

    /**
     * Creates or updates search document for the given account request.
     *
     * @see AccountRequestsLogic#putDocument(AccountRequest)
     */
    public void putAccountRequestDocument(AccountRequest accountRequest) throws SearchServiceException {
        accountRequestLogic.putDocument(accountRequest);
    }

    /**
     * Removes the given data bundle from the database.
     */
    public void removeDataBundle(SqlDataBundle dataBundle) throws InvalidParametersException {
        dataBundleLogic.removeDataBundle(dataBundle);
    }

    /**
     * Populates fields that need dynamic generation in a question.
     *
     * <p>Currently, only MCQ/MSQ needs to generate choices dynamically.</p>
     *
     * @param feedbackQuestion the question to populate
     * @param courseId the ID of the course
     * @param emailOfEntityDoingQuestion the email of the entity doing the question
     * @param teamOfEntityDoingQuestion the team of the entity doing the question. If the entity is an instructor,
     *                                  it can be {@code null}.
     */
    public void populateFieldsToGenerateInQuestion(FeedbackQuestion feedbackQuestion,
            String courseId, String emailOfEntityDoingQuestion,
            String teamOfEntityDoingQuestion) {
        assert feedbackQuestion != null;
        assert courseId != null;
        assert emailOfEntityDoingQuestion != null;

        feedbackQuestionsLogic.populateFieldsToGenerateInQuestion(
                feedbackQuestion, courseId, emailOfEntityDoingQuestion, teamOfEntityDoingQuestion);
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
     * <p>Silently fail if question does not exist.
     *
     * <br/>Preconditions: <br/>
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
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return created feedback response
     * @throws InvalidParametersException if the response is not valid
     * @throws EntityAlreadyExistsException if the response already exist
     */
    public FeedbackResponse createFeedbackResponse(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert feedbackResponse != null;
        return feedbackResponsesLogic.createFeedbackResponse(feedbackResponse);
    }

    /**
     * Deletes a feedback response and cascades its associated comments.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackResponsesAndCommentsCascade(FeedbackResponse feedbackResponse) {
        assert feedbackResponse != null;
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
     * @param id of feedback response comment.
     * @return the specified feedback response comment.
     */
    public FeedbackResponseComment getFeedbackResponseComment(Long id) {
        return feedbackResponseCommentsLogic.getFeedbackResponseComment(id);
    }

    /**
     * Updates a feedback response comment.
     * @throws EntityDoesNotExistException if the comment does not exist
     */
    public FeedbackResponseComment updateFeedbackResponseComment(Long frcId,
            FeedbackResponseCommentUpdateRequest updateRequest, String updaterEmail)
            throws EntityDoesNotExistException {
        return feedbackResponseCommentsLogic.updateFeedbackResponseComment(frcId, updateRequest, updaterEmail);
    }

    /**
     * Updates a feedback response and comments by {@link FeedbackResponse}.
     *
     * <p>Cascade updates its associated feedback response comment
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback response
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponse updateFeedbackResponseCascade(FeedbackResponse feedbackResponse)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert feedbackResponse != null;

        return feedbackResponsesLogic.updateFeedbackResponseCascade(feedbackResponse);
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
     * @throws EntityAlreadyExistsException if the comment alreadty exists
     * @throws InvalidParametersException if the comment is invalid
     */
    public FeedbackResponseComment createFeedbackResponseComment(FeedbackResponseComment frc)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return feedbackResponseCommentsLogic.createFeedbackResponseComment(frc);
    }

    /**
     * Deletes a feedbackResponseComment.
     */
    public void deleteFeedbackResponseComment(Long frcId) {
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
     * Gets all feedback responses from a specific giver and recipient for a course.
     */
    public List<FeedbackResponse> getFeedbackResponsesFromGiverAndRecipientForCourse(String courseId, String giverEmail,
            String recipientEmail) {

        return feedbackResponsesLogic.getFeedbackResponsesFromGiverAndRecipientForCourse(courseId, giverEmail,
            recipientEmail);
    }

    /**
     * Gets all feedback response comments for a feedback response.
     */
    public List<FeedbackResponseComment> getFeedbackResponseCommentsForResponse(UUID feedbackResponse) {
        return feedbackResponseCommentsLogic.getFeedbackResponseCommentsForResponse(feedbackResponse);
    }

    /**
     * Validates sections for any limit violations and teams for any team name violations.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see StudentsLogic#validateSectionsAndTeams(List, String)
     */
    public void validateSectionsAndTeams(
            List<Student> studentList, String courseId) throws EnrollException {

        assert studentList != null;
        assert courseId != null;

        usersLogic.validateSectionsAndTeams(studentList, courseId);
    }

    /**
     * Updates a feedback question by {@code FeedbackQuestionUpdateRequest}.
     *
     * <p>Cascade adjust the question number of questions in the same session.
     *
     * <p>Cascade adjust the existing response of the question.
     *
     * <br/> Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback question
     * @throws InvalidParametersException if attributes to update are not valid
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
     * Creates or updates search document for the given student.
     *
     * @see UsersLogic#putStudentDocument(Student)
     */
    public void putStudentDocument(Student student) throws SearchServiceException {
        usersLogic.putStudentDocument(student);
    }

    /**
     * This is used by admin to search account requests in the whole system.
     *
     * @return A list of {@link AccountRequest} or {@code null} if no match found.
     */
    public List<AccountRequest> searchAccountRequestsInWholeSystem(String queryString)
            throws SearchServiceException {
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
     * Create feedback session logs.
     */
    public void createFeedbackSessionLogs(List<FeedbackSessionLog> feedbackSessionLogs) {
        feedbackSessionLogsLogic.createFeedbackSessionLogs(feedbackSessionLogs);
    }

    /**
     * Gets the feedback session logs as filtered by the given parameters ordered by
     * ascending timestamp. Logs with the same timestamp will be ordered by the
     * student's email.
     *
     * @param studentId        Can be null
     * @param feedbackSessionId Can be null
     */
    public List<FeedbackSessionLog> getOrderedFeedbackSessionLogs(String courseId, UUID studentId,
            UUID feedbackSessionId, Instant startTime, Instant endTime) {
        return feedbackSessionLogsLogic.getOrderedFeedbackSessionLogs(courseId, studentId, feedbackSessionId, startTime,
                endTime);
    }
}
