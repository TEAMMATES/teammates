package teammates.logic.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import jakarta.annotation.Nullable;

import teammates.common.datatransfer.AccountRequestStatus;
import teammates.common.datatransfer.AuthContext;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.EnrollResults;
import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.NotificationStyle;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.Provider;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.SessionSubmissionBundle;
import teammates.common.datatransfer.SubmittedGiverSetBundle;
import teammates.common.datatransfer.UpdateExtensionsResult;
import teammates.common.datatransfer.logs.FeedbackSessionLogType;
import teammates.common.datatransfer.visibility.CommentVisibilityType;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidFeedbackSessionStateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UserUpdateException;
import teammates.common.util.Const;
import teammates.logic.core.AccountRequestsLogic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.AuthLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.DataBundleLogic;
import teammates.logic.core.DeadlineExtensionsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionLogsLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorPermissionsLogic;
import teammates.logic.core.NotificationsLogic;
import teammates.logic.core.ResponseInstructorCommentsLogic;
import teammates.logic.core.UsageStatisticsLogic;
import teammates.logic.core.UsersLogic;
import teammates.storage.entity.Account;
import teammates.storage.entity.AccountRequest;
import teammates.storage.entity.Course;
import teammates.storage.entity.DeadlineExtension;
import teammates.storage.entity.FeedbackQuestion;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession;
import teammates.storage.entity.FeedbackSessionLog;
import teammates.storage.entity.Instructor;
import teammates.storage.entity.Notification;
import teammates.storage.entity.ReadNotification;
import teammates.storage.entity.ResponseGiver;
import teammates.storage.entity.ResponseInstructorComment;
import teammates.storage.entity.ResponseRecipient;
import teammates.storage.entity.Section;
import teammates.storage.entity.Student;
import teammates.storage.entity.Team;
import teammates.storage.entity.UsageStatistics;
import teammates.storage.entity.User;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.request.CourseCreateRequest;
import teammates.ui.request.FeedbackQuestionCreateRequest;
import teammates.ui.request.FeedbackQuestionUpdateRequest;
import teammates.ui.request.FeedbackResponsesRequest;
import teammates.ui.request.FeedbackSessionCreateRequest;
import teammates.ui.request.FeedbackSessionUpdateRequest;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.InstructorUpdateRequest;
import teammates.ui.request.ResponseInstructorCommentUpdateRequest;
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

    final AuthLogic authLogic = AuthLogic.inst();
    final AccountsLogic accountsLogic = AccountsLogic.inst();
    final AccountRequestsLogic accountRequestLogic = AccountRequestsLogic.inst();
    final CoursesLogic coursesLogic = CoursesLogic.inst();
    final DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    final FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();
    final FeedbackResponsesLogic feedbackResponsesLogic = FeedbackResponsesLogic.inst();
    final ResponseInstructorCommentsLogic responseInstructorCommentsLogic = ResponseInstructorCommentsLogic.inst();
    final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    final FeedbackSessionLogsLogic feedbackSessionLogsLogic = FeedbackSessionLogsLogic.inst();
    final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
    final UsersLogic usersLogic = UsersLogic.inst();
    final NotificationsLogic notificationsLogic = NotificationsLogic.inst();
    final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();
    final InstructorPermissionsLogic instructorPermissionsLogic = InstructorPermissionsLogic.inst();

    Logic() {
        // prevent initialization
    }

    public static Logic inst() {
        return instance;
    }

    /**
     * Returns the student associated with the given authentication context and
     * course ID.
     *
     * <p>
     * If the authentication type is REG_KEY, it returns the unregistered student
     * from the authentication context.
     * Otherwise, it retrieves the student from the database linked to the account
     * and course ID.
     */
    public Student getStudentFromAuthContext(AuthContext authContext, String courseId) {
        return authLogic.getStudentFromAuthContext(authContext, courseId);
    }

    /**
     * Returns the instructor associated with the given authentication context and
     * course ID.
     *
     * <p>
     * If the authentication type is REG_KEY, it returns the unregistered instructor
     * from the authentication context. Otherwise, it retrieves the instructor from
     * the database linked to the account
     * and course ID.
     */
    public Instructor getInstructorFromAuthContext(AuthContext authContext, String courseId) {
        return authLogic.getInstructorFromAuthContext(authContext, courseId);
    }

    /**
     * Checks if the given instructor has the specified permissions.
     */
    public boolean hasInstructorPermissions(Instructor instructor, String... permissionNames) {
        return instructorPermissionsLogic.hasPermissions(instructor, permissionNames);
    }

    /**
     * Checks if the given instructor has the specified section-level permissions.
     */
    public boolean hasInstructorPermissionsForSection(Instructor instructor, UUID sectionId,
            String... permissionNames) {
        return instructorPermissionsLogic.hasPermissionsForSection(instructor, sectionId, permissionNames);
    }

    /**
     * Checks if the given instructor has the specified session-in-section-level permissions.
     */
    public boolean hasInstructorPermissionsForSessionInSection(Instructor instructor, UUID sectionId,
            UUID feedbackSessionId, String... permissionNames) {
        return instructorPermissionsLogic.hasPermissionsForSessionInSection(
                instructor, sectionId, feedbackSessionId, permissionNames);
    }

    /**
     * Checks if the given instructor has the specified session-in-section-level permissions in any section.
     */
    public boolean hasInstructorPermissionsForSectionInAnySection(Instructor instructor,
            UUID sessionId, String... permissionNames) {
        return instructorPermissionsLogic.hasPermissionsForSectionInAnySection(instructor, sessionId, permissionNames);
    }

    /**
     * Returns a map of sections with the specified permission for the given instructor.
     */
    public Map<UUID, InstructorPermissionSet> getSectionsWithInstructorPermission(
            Instructor instructor, String permissionName) {
        return instructorPermissionsLogic.getSectionsWithPermission(instructor, permissionName);
    }

    /**
     * Returns the InstructorPrivileges for the given instructor.
     *
     * <p>
     * For instructors with predefined roles, the privileges are determined by their
     * role.
     * For instructors with the custom role, the privileges are determined by their
     * stored privileges.
     */
    public InstructorPrivileges getInstructorPrivileges(Instructor instructor) {
        return instructorPermissionsLogic.getInstructorPrivileges(instructor);
    }

    /**
     * Saves the instructor privileges for the given instructor.
     */
    public void saveInstructorPrivileges(Instructor instructor, InstructorPrivileges privileges) {
        instructorPermissionsLogic.saveInstructorPrivileges(instructor, privileges);
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
     * Creates an account.
     *
     * @return the created account
     * @throws InvalidParametersException   if the account is not valid
     * @throws EntityAlreadyExistsException if the account already exists in the
     *                                      database.
     */
    public Account createAccount(Provider provider, String subject, String tenantId, String email, String googleId)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return accountsLogic.createAccount(provider, subject, tenantId, email, googleId);
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
     * Gets all students by associated {@code accountId}.
     */
    public List<Student> getStudentsByAccountId(UUID accountId) {
        return usersLogic.getStudentsByAccountId(accountId);
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
    public List<Course> getCoursesForStudentAccount(Account account) {
        return coursesLogic.getCoursesForStudentAccount(account);
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
        return coursesLogic.getSoftDeletedCoursesForInstructors(instructorsList);
    }

    /**
     * Creates a course and an associated instructor for the course.
     *
     * @param courseCreator      the account of the instructor creating the course.
     * @param courseCreateRequest the course creation details.
     * @throws InvalidParametersException   if the course is not valid.
     * @throws EntityAlreadyExistsException if the course already exists.
     */
    public Course createCourseAndInstructor(
            Account courseCreator, CourseCreateRequest courseCreateRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return coursesLogic.createCourseAndInstructor(courseCreator, courseCreateRequest);
    }

    /**
     * Deletes a course by course id.
     *
     * @param courseId of course.
     */
    public void deleteCourse(String courseId) {
        coursesLogic.deleteCourse(courseId);
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
     * Gets the sections for the given {@code courseId}.
     */
    public Set<Section> getSectionsForCourse(String courseId)
            throws EntityDoesNotExistException {
        return coursesLogic.getSectionsForCourse(courseId);
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
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the
     * instructors.
     * <br>
     * Omits sessions if the corresponding courses are in Recycle Bin
     */
    public List<FeedbackSession> getSoftDeletedFeedbackSessionsForInstructors(
            List<Instructor> instructorList) {
        return feedbackSessionsLogic.getSoftDeletedFeedbackSessionsForInstructors(instructorList);
    }

    /**
     * Gets a list of feedback sessions for instructors.
     */
    public List<FeedbackSession> getFeedbackSessionsForInstructors(
            List<Instructor> instructorList) {
        return feedbackSessionsLogic.getFeedbackSessionsForInstructors(instructorList);
    }

    /**
     * Gets all and only the feedback sessions ongoing within a range of time.
     */
    public List<FeedbackSession> getOngoingSessions(Instant rangeStart, Instant rangeEnd) {
        return feedbackSessionsLogic.getOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Gets submitted givers partitioned by giver type under a feedback session.
     *
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public SubmittedGiverSetBundle getSubmittedGiverSet(
            UUID feedbackSessionId) throws EntityDoesNotExistException {
        return feedbackSessionsLogic.getSubmittedGiverSet(feedbackSessionId);
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
     * Creates a feedback session from a create request, validating timing and copying questions if requested.
     *
     * @return returns the created feedback session.
     */
    public FeedbackSession createFeedbackSession(String courseId, Instructor instructor,
            FeedbackSessionCreateRequest createRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        return feedbackSessionsLogic.createFeedbackSession(courseId, instructor, createRequest);
    }

    /**
     * Gets all feedback sessions of a course, except those that are soft-deleted.
     */
    public List<FeedbackSession> getFeedbackSessionsForCourse(String courseId) {
        return feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Creates a feedback question from a create request, validating giver/recipient visibility and question details.
     *
     * @return the created feedback question
     */
    public FeedbackQuestion createFeedbackQuestion(UUID feedbackSessionId, FeedbackQuestionCreateRequest createRequest)
            throws InvalidParametersException, EntityDoesNotExistException {
        return feedbackQuestionsLogic.createFeedbackQuestion(feedbackSessionId, createRequest);
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
    public void createUsageStatistics(UsageStatistics attributes) {
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
     * Gets instructor associated with {@code id} in the specified course.
     */
    public Instructor getInstructorOfCourse(String courseId, UUID id) {
        return usersLogic.getInstructorOfCourse(courseId, id);
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
     * Gets a user by associated {@code regkey}.
     */
    public User getUserByRegistrationKey(String regKey) {
        return usersLogic.getUserByRegistrationKey(regKey);
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
     * Gets all instructors by associated {@code accountId}.
     */
    public List<Instructor> getInstructorsByAccountId(UUID accountId) {
        return usersLogic.getInstructorsByAccountId(accountId);
    }

    /**
     * Gets instructors by associated {@code courseId}.
     */
    public List<Instructor> getInstructorsByCourse(String courseId) {
        return usersLogic.getInstructorsForCourse(courseId);
    }

    /**
     * Creates an instructor with the given attributes.
     *
     * @param account optional account to associate with the instructor at creation time
     */
    public Instructor createInstructor(Course course, String name, String email,
            boolean isDisplayedToStudents, String displayedName, InstructorPermissionRole role,
            @Nullable Account account)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return usersLogic.createInstructor(course, name, email, isDisplayedToStudents, displayedName, role, account);
    }

    /**
     * Creates an instructor from a create request, handling sanitization and custom privileges.
     */
    public Instructor createInstructor(String courseId, InstructorCreateRequest request)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return usersLogic.createInstructor(courseId, request);
    }

    /**
     * Makes the user join the course, i.e. associate the account to the user.
     */
    public User joinCourse(String regkey, Account account)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {
        return accountsLogic.joinCourse(regkey, account);
    }

    /**
     * Searches instructors in the whole system. Used by admin only.
     *
     * @return List of found instructors in the whole system. Returns an empty list
     *         if no results are found.
     */
    public List<Instructor> searchInstructorsInWholeSystem(String queryString) {
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
    public Instructor updateInstructorCascade(InstructorUpdateRequest instructorRequest)
            throws InvalidParametersException, InstructorUpdateException, EntityDoesNotExistException {
        return usersLogic.updateInstructorCascade(instructorRequest);
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
     * Gets student associated with {@code id} in the specified course.
     */
    public Student getStudentOfCourse(String courseId, UUID id) {
        return usersLogic.getStudentOfCourse(courseId, id);
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
        return usersLogic.getSectionOrCreate(courseId, Const.NO_SPECIFIC_SECTION);
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
     * * User ID is non-null.
     */
    public void deleteStudentCascade(UUID userId) {
        usersLogic.deleteStudentCascade(userId);
    }

    /**
     * Deletes all the students in the course.
     */
    public void deleteStudentsInCourse(String courseId) {
        usersLogic.deleteStudentsInCourse(courseId);
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
     * * User ID is non-null.
     */
    public void deleteInstructorCascade(UUID userId) throws InvalidOperationException {
        usersLogic.deleteInstructorCascade(userId);
    }

    /**
     * Gets a list of notifications.
     *
     * @return a list of notifications with the specified {@code targetUsers}.
     *         If {@code isActiveOnly} is true, only active notifications are returned.
     *         Otherwise, all notifications for the specified {@code targetUsers} are returned.
     */
    public List<Notification> getNotificationsByTargetUsers(
            List<NotificationTargetUser> targetUsers, boolean isActiveOnly) {
        return notificationsLogic.getNotificationsByTargetUsers(targetUsers, isActiveOnly);
    }

    /**
     * Resets the account associated with the user.
     *
     * @return the user whose account was reset.
     * @throws EntityDoesNotExistException If user cannot be found with given id.
     */
    public User resetAccount(UUID userId) throws EntityDoesNotExistException {
        return usersLogic.resetAccount(userId);
    }

    /**
     * Regenerates the registration key for the user with {@code userId}.
     *
     * @return the user with the new registration key.
     * @throws UserUpdateException         if system was unable to generate a new
     *                                     registration key.
     * @throws EntityDoesNotExistException if the user does not exist.
     */
    public User regenerateUserRegistrationKey(UUID userId)
            throws EntityDoesNotExistException, UserUpdateException {
        return usersLogic.regenerateUserRegistrationKey(userId);
    }

    /**
     * Updates the instructor being edited to ensure validity of instructors for the
     * course.
     * * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see UsersLogic#updateToEnsureValidityOfInstructorsForTheCourse(Instructor)
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(Instructor instructorToEdit) {
        usersLogic.updateToEnsureValidityOfInstructorsForTheCourse(instructorToEdit);
    }

    /**
     * Gets all questions for a feedback session.<br>
     * Returns an empty list if they are no questions
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForSession(FeedbackSession feedbackSession) {
        return feedbackQuestionsLogic.getFeedbackQuestionsForSession(feedbackSession);
    }

    /**
     * Gets a list of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForStudents(FeedbackSession feedbackSession) {
        return feedbackQuestionsLogic.getFeedbackQuestionsForStudents(feedbackSession);
    }

    /**
     * Gets a {@code List} of all questions for the given session that
     * instructor can view/submit.
     */
    public List<FeedbackQuestion> getFeedbackQuestionsForInstructors(
            FeedbackSession feedbackSession, Instructor instructor) {
        return feedbackQuestionsLogic.getFeedbackQuestionsForInstructors(feedbackSession, instructor);
    }

    /**
     * Gets all data required for feedback session submission for a student.
     */
    public SessionSubmissionBundle getSessionSubmissionBundleForStudent(FeedbackSession feedbackSession,
            Student student, boolean isPreview, boolean isModeration) {
        return feedbackQuestionsLogic.getSessionSubmissionBundleForStudent(
                feedbackSession, student, isPreview, isModeration);
    }

    /**
     * Gets all data required for feedback session submission for an instructor.
     */
    public SessionSubmissionBundle getSessionSubmissionBundleForInstructor(FeedbackSession feedbackSession,
            Instructor instructor, boolean isPreview, boolean isModeration) {
        return feedbackQuestionsLogic.getSessionSubmissionBundleForInstructor(
                feedbackSession, instructor, isPreview, isModeration);
    }

    /**
     * Gets the session result for a feedback session.
     *
     * @param feedbackSession the feedback session
     * @param instructor the instructor requesting for the session result
     * @param questionId if not null, will only return partial bundle for the question
     * @param sectionId if not null, will only return partial bundle for the section
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResults(
            FeedbackSession feedbackSession, Instructor instructor,
            @Nullable UUID questionId, @Nullable UUID sectionId, boolean isNoSpecificSection) {
        return feedbackResponsesLogic.getSessionResults(
                feedbackSession, instructor, questionId, sectionId, isNoSpecificSection);
    }

    /**
     * Gets the session result for a feedback session for the given user.
     *
     * @param feedbackSession the feedback session
     * @param user the user viewing the feedback session
     * @param isPreviewResults true if getting session results for preview purpose
     * @return the session result bundle
     */
    public SessionResultsBundle getSessionResultsForUser(
            FeedbackSession feedbackSession, User user, boolean isPreviewResults) {
        return feedbackResponsesLogic.getSessionResultsForUser(
                feedbackSession, user, isPreviewResults);
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
     * Gets the recipients of a feedback question.
     *
     * @see FeedbackQuestionsLogic#getRecipientsOfQuestion
     */
    public Set<ResponseRecipient> getRecipientsOfQuestion(
            FeedbackQuestion question, ResponseGiver responseGiver) {
        return feedbackQuestionsLogic.getRecipientsOfQuestion(question, responseGiver);
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
     * Deletes the giver comment for a feedback response by clearing it.
     *
     * @throws EntityDoesNotExistException if the feedback response does not exist
     */
    public FeedbackResponse deleteFeedbackResponseGiverComment(UUID frId) throws EntityDoesNotExistException {
        return feedbackResponsesLogic.deleteFeedbackResponseGiverComment(frId);
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
     * Submits feedback responses from a student or the student's team for one or more feedback questions.
     */
    public List<FeedbackResponse> submitFeedbackResponsesFromStudent(
            FeedbackSession feedbackSession, Student student, FeedbackResponsesRequest submitRequest)
            throws InvalidOperationException, InvalidParametersException {
        return feedbackResponsesLogic.submitFeedbackResponsesFromStudent(feedbackSession, student, submitRequest);
    }

    /**
     * Submits feedback responses from an instructor for one or more feedback questions.
     */
    public List<FeedbackResponse> submitFeedbackResponsesFromInstructor(
            FeedbackSession feedbackSession, Instructor instructor, FeedbackResponsesRequest submitRequest)
            throws InvalidOperationException, InvalidParametersException {
        return feedbackResponsesLogic.submitFeedbackResponsesFromInstructor(feedbackSession, instructor, submitRequest);
    }

    /**
     * Gets an feedback response comment by feedback response comment id.
     *
     * @param id of feedback response comment.
     * @return the specified feedback response comment.
     */
    public ResponseInstructorComment getResponseInstructorComment(UUID id) {
        return responseInstructorCommentsLogic.getResponseInstructorComment(id);
    }

    /**
     * Updates a feedback response comment.
     *
     * @throws EntityDoesNotExistException if the comment does not exist
     */
    public ResponseInstructorComment updateResponseInstructorComment(UUID frcId,
            ResponseInstructorCommentUpdateRequest updateRequest, Instructor updater)
            throws EntityDoesNotExistException {
        return responseInstructorCommentsLogic.updateResponseInstructorComment(frcId, updateRequest, updater);
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
     * Creates a feedback response comment.
     *
     * @throws EntityDoesNotExistException if the feedback response does not exist
     * @throws InvalidParametersException   if the comment is invalid
     */
    public ResponseInstructorComment createResponseInstructorComment(UUID feedbackResponseId, Instructor giver,
            String commentText, List<CommentVisibilityType> showCommentTo, List<CommentVisibilityType> showGiverNameTo)
            throws InvalidParametersException, EntityDoesNotExistException {
        return responseInstructorCommentsLogic.createResponseInstructorComment(
                feedbackResponseId, giver, commentText, showCommentTo, showGiverNameTo);
    }

    /**
    * Deletes a responseInstructorComment.
     *
     * <p>Fails silently if the comment does not exist.</p>
     */
    public void deleteResponseInstructorComment(UUID frcId) {
        responseInstructorCommentsLogic.deleteResponseInstructorComment(frcId);
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
     * Returns a list of sessions that were closed recently.
     */
    public List<FeedbackSession> getFeedbackSessionsClosedRecently() {
        return feedbackSessionsLogic.getFeedbackSessionsClosedRecently();
    }

    /**
     * This is used by admin to search account requests in the whole system.
     *
     * @return A list of matching {@link AccountRequest}s, or an empty list if no match is found.
     */
    public List<AccountRequest> searchAccountRequestsInWholeSystem(String queryString) {
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
            FeedbackSession feedbackSession, User user,
            FeedbackSessionLogType logType, Instant timestamp) throws InvalidParametersException {
        return feedbackSessionLogsLogic.createFeedbackSessionLog(feedbackSession, user, logType, timestamp);
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
     * user's email.
     *
     * @param userId            Can be null
     * @param feedbackSessionId Can be null
     */
    public List<FeedbackSessionLog> getOrderedFeedbackSessionLogs(String courseId, UUID userId,
            UUID feedbackSessionId, Instant startTime, Instant endTime) {
        return feedbackSessionLogsLogic.getOrderedFeedbackSessionLogs(courseId, userId, feedbackSessionId, startTime,
                endTime);
    }
}
