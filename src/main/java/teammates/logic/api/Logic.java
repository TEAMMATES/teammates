package teammates.logic.api;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionRecipient;
import teammates.common.datatransfer.FeedbackResultFetchType;
import teammates.common.datatransfer.NotificationTargetUser;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.AccountRequestAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.DeadlineExtensionAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.NotificationAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.UsageStatisticsAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.logic.core.AccountRequestsLogic;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.DataBundleLogic;
import teammates.logic.core.DeadlineExtensionsLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.NotificationsLogic;
import teammates.logic.core.StudentsLogic;
import teammates.logic.core.UsageStatisticsLogic;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {

    private static final Logic instance = new Logic();

    final AccountsLogic accountsLogic = AccountsLogic.inst();
    final AccountRequestsLogic accountRequestsLogic = AccountRequestsLogic.inst();
    final DeadlineExtensionsLogic deadlineExtensionsLogic = DeadlineExtensionsLogic.inst();
    final NotificationsLogic notificationsLogic = NotificationsLogic.inst();
    final StudentsLogic studentsLogic = StudentsLogic.inst();
    final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    final CoursesLogic coursesLogic = CoursesLogic.inst();
    final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    final FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();
    final FeedbackResponsesLogic feedbackResponsesLogic = FeedbackResponsesLogic.inst();
    final FeedbackResponseCommentsLogic feedbackResponseCommentsLogic = FeedbackResponseCommentsLogic.inst();
    final UsageStatisticsLogic usageStatisticsLogic = UsageStatisticsLogic.inst();
    final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    Logic() {
        // prevent initialization
    }

    public static Logic inst() {
        return instance;
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public AccountAttributes getAccount(String googleId) {
        assert googleId != null;

        return accountsLogic.getAccount(googleId);
    }

    /**
     * Returns a list of accounts with email matching {@code email}.
     *
     * <br/> Preconditions: <br/>
     * * All parameters are non-null.
     */
    public List<AccountAttributes> getAccountsForEmail(String email) {
        assert email != null;

        return accountsLogic.getAccountsForEmail(email);
    }

    public List<String> getReadNotificationsId(String googleId) {
        return accountsLogic.getReadNotificationsId(googleId);
    }

    /**
     * Updates user read status for notification with ID {@code notificationId} and expiry time {@code endTime}.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null. {@code endTime} must be after current moment.
     */
    public List<String> updateReadNotifications(String googleId, String notificationId, Instant endTime)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert googleId != null;
        return accountsLogic.updateReadNotifications(googleId, notificationId, endTime);
    }

    public String getCourseInstitute(String courseId) {
        return coursesLogic.getCourseInstitute(courseId);
    }

    /**
     * Returns active notification for general users and the specified {@code targetUser}.
     */
    public List<NotificationAttributes> getActiveNotificationsByTargetUser(NotificationTargetUser targetUser) {
        return notificationsLogic.getActiveNotificationsByTargetUser(targetUser);
    }

    public List<NotificationAttributes> getAllNotifications() {
        return notificationsLogic.getAllNotifications();
    }

    /**
     * Gets a notification by ID.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public NotificationAttributes getNotification(String notificationId) {
        return notificationsLogic.getNotification(notificationId);
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
    public NotificationAttributes createNotification(NotificationAttributes notification) throws
            InvalidParametersException, EntityAlreadyExistsException {
        return notificationsLogic.createNotification(notification);
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
    public NotificationAttributes updateNotification(NotificationAttributes.UpdateOptions updateOptions) throws
            InvalidParametersException, EntityDoesNotExistException {
        return notificationsLogic.updateNotification(updateOptions);
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
    public void deleteNotification(String notificationId) {
        assert notificationId != null;

        notificationsLogic.deleteNotification(notificationId);
    }

    /**
     * Deletes both instructor and student privileges, as well as the account.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     */
    public void deleteAccountCascade(String googleId) {

        assert googleId != null;

        accountsLogic.deleteAccountCascade(googleId);
    }

    /**
     * Verifies that all the given instructors exist in the given course.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If some instructor does not exist in the course.
     */
    public void verifyAllInstructorsExistInCourse(String courseId, Collection<String> instructorEmailAddresses)
            throws EntityDoesNotExistException {
        assert courseId != null;
        assert instructorEmailAddresses != null;

        instructorsLogic.verifyAllInstructorsExistInCourse(courseId, instructorEmailAddresses);
    }

    /**
     * Creates an instructor.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database
     */
    public InstructorAttributes createInstructor(InstructorAttributes instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert instructor != null;

        return instructorsLogic.createInstructor(instructor);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     * @return Null if no match found.
     */
    public List<InstructorAttributes> searchInstructorsInWholeSystem(String queryString)
            throws SearchServiceException {
        assert queryString != null;

        return instructorsLogic.searchInstructorsInWholeSystem(queryString);
    }

    /**
     * Creates or updates search document for the given instructor.
     *
     * @see InstructorsLogic#putDocument(InstructorAttributes)
     */
    public void putInstructorDocument(InstructorAttributes instructor) throws SearchServiceException {
        instructorsLogic.putDocument(instructor);
    }

    /**
     * Update instructor being edited to ensure validity of instructors for the course.
     *
     * @see InstructorsLogic#updateToEnsureValidityOfInstructorsForTheCourse(String, InstructorAttributes)
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {

        assert courseId != null;
        assert instructorToEdit != null;

        instructorsLogic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {

        assert courseId != null;
        assert email != null;

        return instructorsLogic.getInstructorForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorById(String courseId, String email) {

        assert courseId != null;
        assert email != null;

        return instructorsLogic.getInstructorById(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {

        assert googleId != null;
        assert courseId != null;

        return instructorsLogic.getInstructorForGoogleId(courseId, googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String registrationKey) {

        assert registrationKey != null;

        return instructorsLogic.getInstructorForRegistrationKey(registrationKey);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {

        assert googleId != null;

        return instructorsLogic.getInstructorsForGoogleId(googleId);
    }

    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean omitArchived) {

        assert googleId != null;

        return instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {

        assert courseId != null;

        return instructorsLogic.getInstructorsForCourse(courseId);
    }

    public List<FeedbackSessionAttributes> getAllOngoingSessions(Instant rangeStart, Instant rangeEnd) {

        return feedbackSessionsLogic.getAllOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithGoogleId}.
     *
     * <p>Cascade update the comments, responses and deadline extensions associated with the instructor.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorCascade(InstructorAttributes.UpdateOptionsWithGoogleId updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return instructorsLogic.updateInstructorByGoogleIdCascade(updateOptions);
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithEmail}.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructor(InstructorAttributes.UpdateOptionsWithEmail updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return instructorsLogic.updateInstructorByEmail(updateOptions);
    }

    /**
     * Make the instructor join the course, i.e. associate the Google ID to the instructor.<br>
     * Creates an account for the instructor if no existing account is found.
     * Preconditions: <br>
     * * Parameters regkey and googleId are non-null.
     */
    public InstructorAttributes joinCourseForInstructor(String regkey, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert googleId != null;
        assert regkey != null;

        return accountsLogic.joinCourseForInstructor(regkey, googleId);
    }

    /**
     * Deletes an instructor cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteInstructorCascade(String courseId, String email) {

        assert courseId != null;
        assert email != null;

        instructorsLogic.deleteInstructorCascade(courseId, email);
    }

    /**
     * Creates a course and an associated instructor for the course.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null. <br/>
     * * {@code instructorGoogleId} already has an account and instructor privileges.
     */
    public void createCourseAndInstructor(String instructorGoogleId, CourseAttributes courseAttributes)
            throws EntityAlreadyExistsException, InvalidParametersException {
        assert instructorGoogleId != null;
        assert courseAttributes != null;

        coursesLogic.createCourseAndInstructor(instructorGoogleId, courseAttributes);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public CourseAttributes getCourse(String courseId) {

        assert courseId != null;

        return coursesLogic.getCourse(courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<CourseAttributes> getCoursesForStudentAccount(String googleId) {
        assert googleId != null;
        return coursesLogic.getCoursesForStudentAccount(googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Courses the given instructors is in except for courses in Recycle Bin.
     */
    public List<CourseAttributes> getCoursesForInstructor(List<InstructorAttributes> instructorList) {

        assert instructorList != null;
        return coursesLogic.getCoursesForInstructor(instructorList);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Courses in Recycle Bin that the given instructors is in.
     */
    public List<CourseAttributes> getSoftDeletedCoursesForInstructors(List<InstructorAttributes> instructorList) {

        assert instructorList != null;
        return coursesLogic.getSoftDeletedCoursesForInstructors(instructorList);
    }

    /**
     * Updates a course by {@link CourseAttributes.UpdateOptions}.
     *
     * <p>If the {@code timezone} of the course is changed, cascade the change to its corresponding feedback sessions.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated course
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the course cannot be found
     */
    public CourseAttributes updateCourseCascade(CourseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return coursesLogic.updateCourseCascade(updateOptions);
    }

    /**
     * Changes the archive status of a course for an instructor.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @param courseId The course of which the archive status is to be changed
     * @param archiveStatus The archive status to be set
     */
    public void setArchiveStatusOfInstructor(String googleId, String courseId, boolean archiveStatus)
            throws InvalidParametersException, EntityDoesNotExistException {

        assert googleId != null;
        assert courseId != null;

        instructorsLogic.setArchiveStatusOfInstructor(googleId, courseId, archiveStatus);
    }

    /**
     * Deletes a course cascade its students, instructors, sessions, responses, deadline extensions and comments.
     *
     * <p>Fails silently if no such course.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteCourseCascade(String courseId) {
        assert courseId != null;
        coursesLogic.deleteCourseCascade(courseId);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the deletion timestamp assigned to the course.
     */
    public Instant moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;
        return coursesLogic.moveCourseToRecycleBin(courseId);
    }

    /**
     * Restores a course and all data related to the course from Recycle Bin by
     * its given corresponding ID.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void restoreCourseFromRecycleBin(String courseId)
            throws EntityDoesNotExistException {
        assert courseId != null;

        coursesLogic.restoreCourseFromRecycleBin(courseId);
    }

    /**
     * Search for students. Preconditions: all parameters are non-null.
     * @param instructors   a list of InstructorAttributes associated to a googleId,
     *                      used for filtering of search result
     * @return Null if no match found
     */
    public List<StudentAttributes> searchStudents(String queryString, List<InstructorAttributes> instructors)
            throws SearchServiceException {
        assert queryString != null;
        assert instructors != null;
        return studentsLogic.searchStudents(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @return Null if no match found.
     */
    public List<StudentAttributes> searchStudentsInWholeSystem(String queryString)
            throws SearchServiceException {
        assert queryString != null;

        return studentsLogic.searchStudentsInWholeSystem(queryString);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
        assert registrationKey != null;
        return studentsLogic.getStudentForRegistrationKey(registrationKey);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForEmail(String courseId, String email) {
        assert courseId != null;
        assert email != null;

        return studentsLogic.getStudentForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
        assert courseId != null;
        assert googleId != null;

        return studentsLogic.getStudentForCourseIdAndGoogleId(courseId, googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Empty list if no match found.
     */
    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        assert googleId != null;
        return studentsLogic.getStudentsForGoogleId(googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        assert courseId != null;
        return studentsLogic.getStudentsForCourse(courseId);
    }

    /**
     * Returns a list of section names for the course with ID courseId.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see CoursesLogic#getSectionsNameForCourse(String)
     */
    public List<String> getSectionNamesForCourse(String courseId) throws EntityDoesNotExistException {
        assert courseId != null;
        return coursesLogic.getSectionsNameForCourse(courseId);
    }

    /**
     * Populates fields that need dynamic generation in a question.
     *
     * <p>Currently, only MCQ/MSQ needs to generate choices dynamically.</p>
     *
     * <br/> Preconditions: <br/>
     * * All parameters except <code>teamOfEntityDoingQuestion</code> are non-null.
     *
     * @param feedbackQuestionAttributes the question to populate
     * @param emailOfEntityDoingQuestion the email of the entity doing the question
     * @param teamOfEntityDoingQuestion the team of the entity doing the question. If the entity is an instructor,
     *                                  it can be {@code null}.
     */
    public void populateFieldsToGenerateInQuestion(FeedbackQuestionAttributes feedbackQuestionAttributes,
            String emailOfEntityDoingQuestion, String teamOfEntityDoingQuestion) {
        assert feedbackQuestionAttributes != null;
        assert emailOfEntityDoingQuestion != null;

        feedbackQuestionsLogic.populateFieldsToGenerateInQuestion(
                feedbackQuestionAttributes, emailOfEntityDoingQuestion, teamOfEntityDoingQuestion);
    }

    /**
     * Resets the googleId associated with the student.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void resetStudentGoogleId(String originalEmail, String courseId) throws EntityDoesNotExistException {
        assert originalEmail != null;
        assert courseId != null;

        studentsLogic.resetStudentGoogleId(originalEmail, courseId);
    }

    /**
     * Regenerates the registration key for the instructor with email address {@code email} in course {@code courseId}.
     *
     * @return the instructor attributes with the new registration key.
     * @throws EntityAlreadyExistsException if the newly generated instructor has the same registration key as the
     *          original one.
     * @throws EntityDoesNotExistException if the instructor does not exist.
     */
    public InstructorAttributes regenerateInstructorRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

        assert courseId != null;
        assert email != null;

        return instructorsLogic.regenerateInstructorRegistrationKey(courseId, email);
    }

    /**
     * Regenerates the registration key for the student with email address {@code email} in course {@code courseId}.
     *
     * @return the student attributes with the new registration key.
     * @throws EntityAlreadyExistsException if the newly generated course student has the same registration key as the
     *          original one.
     * @throws EntityDoesNotExistException if the student does not exist.
     */
    public StudentAttributes regenerateStudentRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, EntityAlreadyExistsException {

        assert courseId != null;
        assert email != null;

        return studentsLogic.regenerateStudentRegistrationKey(courseId, email);
    }

    /**
     * Resets the associated googleId of an instructor.
     */
    public void resetInstructorGoogleId(String originalEmail, String courseId) throws EntityDoesNotExistException {
        assert originalEmail != null;
        assert courseId != null;

        instructorsLogic.resetInstructorGoogleId(originalEmail, courseId);
    }

    /**
     * Creates a student.
     *
     * @return the created student.
     * @throws InvalidParametersException if the student is not valid.
     * @throws EntityAlreadyExistsException if the student already exists in the database.
     */
    public StudentAttributes createStudent(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert student.getCourse() != null;
        assert student.getEmail() != null;

        return studentsLogic.createStudent(student);
    }

    /**
     * Updates a student by {@link StudentAttributes.UpdateOptions}.
     *
     * <p>If email changed, update by recreating the student and cascade update all responses
     * the student gives/receives as well as any deadline extensions given to the student.
     *
     * <p>If team changed, cascade delete all responses the student gives/receives within that team.
     *
     * <p>If section changed, cascade update all responses the student gives/receives.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated student
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the student cannot be found
     * @throws EntityAlreadyExistsException if the student cannot be updated
     *         by recreation because of an existent student
     */
    public StudentAttributes updateStudentCascade(StudentAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert updateOptions != null;

        return studentsLogic.updateStudentCascade(updateOptions);
    }

    /**
     * Make the student join the course, i.e. associate the Google ID to the student.<br>
     * Create an account for the student if no existing account is found.
     * Preconditions: <br>
     * * All parameters are non-null.
     * @param key the registration key
     */
    public StudentAttributes joinCourseForStudent(String key, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        assert googleId != null;
        assert key != null;

        return accountsLogic.joinCourseForStudent(key, googleId);

    }

    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        assert courseId != null;
        return studentsLogic.getUnregisteredStudentsForCourse(courseId);
    }

    /**
     * Checks whether an instructor has attempted a feedback session.
     *
     * <p>If there is no question for instructors, the feedback session is considered as attempted.</p>
     */
    public boolean isFeedbackSessionAttemptedByInstructor(FeedbackSessionAttributes fsa, String userEmail) {
        assert fsa != null;
        assert userEmail != null;
        return feedbackSessionsLogic.isFeedbackSessionAttemptedByInstructor(fsa, userEmail);
    }

    /**
     * Checks whether a student has attempted a feedback session.
     *
     * <p>If there is no question for students, the feedback session is considered as attempted.</p>
     */
    public boolean isFeedbackSessionAttemptedByStudent(FeedbackSessionAttributes fsa, String userEmail, String userTeam) {
        assert fsa != null;
        assert userEmail != null;
        assert userTeam != null;
        return feedbackSessionsLogic.isFeedbackSessionAttemptedByStudent(fsa, userEmail, userTeam);
    }

    /**
     * Deletes a student cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteStudentCascade(String courseId, String studentEmail) {
        assert courseId != null;
        assert studentEmail != null;

        studentsLogic.deleteStudentCascade(courseId, studentEmail);
    }

    /**
     * Deletes all the students in the course cascade their associated responses, deadline extensions and comments.
     *
     * <br/>Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteStudentsInCourseCascade(String courseId, int batchSize) {
        assert courseId != null;

        studentsLogic.deleteStudentsInCourseCascade(courseId, batchSize);
    }

    /**
     * Validates sections for any limit violations and teams for any team name violations.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see StudentsLogic#validateSectionsAndTeams(List, String)
     */
    public void validateSectionsAndTeams(List<StudentAttributes> studentList, String courseId) throws EnrollException {

        assert studentList != null;
        assert courseId != null;

        studentsLogic.validateSectionsAndTeams(studentList, courseId);
    }

    /**
     * Gets all students of a team.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        assert teamName != null;
        assert courseId != null;

        return studentsLogic.getStudentsForTeam(teamName, courseId);
    }

    /**
     * Creates or updates search document for the given student.
     *
     * @see StudentsLogic#putDocument(StudentAttributes)
     */
    public void putStudentDocument(StudentAttributes student) throws SearchServiceException {
        studentsLogic.putDocument(student);
    }

    /**
     * Creates a feedback session.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return created feedback session
     * @throws InvalidParametersException if the session is not valid
     * @throws EntityAlreadyExistsException if the session already exist
     */
    public FeedbackSessionAttributes createFeedbackSession(FeedbackSessionAttributes feedbackSession)
            throws EntityAlreadyExistsException, InvalidParametersException {
        assert feedbackSession != null;

        return feedbackSessionsLogic.createFeedbackSession(feedbackSession);
    }

    /**
     * Gets a feedback session from the data storage.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if not found or in recycle bin.
     */
    public FeedbackSessionAttributes getFeedbackSession(String feedbackSessionName, String courseId) {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.getFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Gets a feedback session from the recycle bin.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if not found.
     */
    public FeedbackSessionAttributes getFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        assert courseId != null;
        return feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Gets the expected number of submissions for a feedback session.
     *
     * <br>Preconditions: <br>
     * * All parameters are non-null.
     */
    public int getExpectedTotalSubmission(FeedbackSessionAttributes fsa) {
        assert fsa != null;
        return feedbackSessionsLogic.getExpectedTotalSubmission(fsa);
    }

    /**
     * Gets the actual number of submissions for a feedback session.
     *
     * <br>Preconditions: <br>
     * * All parameters are non-null.
     */
    public int getActualTotalSubmission(FeedbackSessionAttributes fsa) {
        assert fsa != null;
        return feedbackSessionsLogic.getActualTotalSubmission(fsa);
    }

    /**
     * Gets a list of feedback sessions for instructors.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) {
        assert instructorList != null;
        return feedbackSessionsLogic.getFeedbackSessionsListForInstructor(instructorList);
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the instructors.
     * <br>
     * Omits sessions if the corresponding courses are archived or in Recycle Bin
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForInstructors(
            List<InstructorAttributes> instructorList) {
        assert instructorList != null;
        return feedbackSessionsLogic.getSoftDeletedFeedbackSessionsListForInstructors(instructorList);
    }

    /**
     * Gets the recipients of a feedback question for student.
     *
     * @see FeedbackQuestionsLogic#getRecipientsOfQuestion
     */
    public Map<String, FeedbackQuestionRecipient> getRecipientsOfQuestion(
            FeedbackQuestionAttributes question,
            @Nullable InstructorAttributes instructorGiver, @Nullable StudentAttributes studentGiver) {
        assert question != null;

        // we do not supply course roster here
        return feedbackQuestionsLogic.getRecipientsOfQuestion(question, instructorGiver, studentGiver, null);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     *
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        assert feedbackQuestionId != null;
        return feedbackQuestionsLogic.getFeedbackQuestion(feedbackQuestionId);
    }

    /**
     * Gets a list of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
    }

    /**
     * Gets a {@code List} of all questions for the given session that
     * instructor can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructors(
            String feedbackSessionName, String courseId, String instructorEmail) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForInstructors(feedbackSessionName, courseId, instructorEmail);
    }

    /**
     * Updates the details of a feedback session by {@link FeedbackSessionAttributes.UpdateOptions}.
     *
     * <p>Adjust email sending status if necessary.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback session
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     */
    public FeedbackSessionAttributes updateFeedbackSession(FeedbackSessionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return feedbackSessionsLogic.updateFeedbackSession(updateOptions);
    }

    /**
     * Publishes a feedback session.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the published feedback session
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     * @throws InvalidParametersException if session is already published
     */
    public FeedbackSessionAttributes publishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.publishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Unpublishes a feedback session.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the unpublished feedback session
     * @throws EntityDoesNotExistException if the feedback session cannot be found
     * @throws InvalidParametersException
     *             if the feedback session is not ready to be unpublished.
     */
    public FeedbackSessionAttributes unpublishFeedbackSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackSessionsLogic.unpublishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses, deadline extensions and comments.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {

        assert feedbackSessionName != null;
        assert courseId != null;

        feedbackSessionsLogic.deleteFeedbackSessionCascade(feedbackSessionName, courseId);
    }

    /**
     * Soft-deletes a specific session to Recycle Bin.
     */
    public void moveFeedbackSessionToRecycleBin(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        assert feedbackSessionName != null;
        assert courseId != null;

        feedbackSessionsLogic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
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
     * Creates a new feedback question.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the created question
     * @throws InvalidParametersException if the question is invalid
     */
    public FeedbackQuestionAttributes createFeedbackQuestion(FeedbackQuestionAttributes feedbackQuestion)
            throws InvalidParametersException {
        assert feedbackQuestion != null;

        return feedbackQuestionsLogic.createFeedbackQuestion(feedbackQuestion);
    }

    /**
     * Updates a feedback question by {@code FeedbackQuestionAttributes.UpdateOptions}.
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
    public FeedbackQuestionAttributes updateFeedbackQuestionCascade(FeedbackQuestionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return feedbackQuestionsLogic.updateFeedbackQuestionCascade(updateOptions);
    }

    /**
     * Deletes a feedback question cascade its responses and comments.
     *
     * <p>Silently fail if question does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackQuestionCascade(String questionId) {
        assert questionId != null;
        feedbackQuestionsLogic.deleteFeedbackQuestionCascade(questionId);
    }

    /**
     * Checks whether there are responses for a question.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public boolean areThereResponsesForQuestion(String feedbackQuestionId) {
        return feedbackResponsesLogic.areThereResponsesForQuestion(feedbackQuestionId);
    }

    /**
     * Gets all questions for a feedback session.<br>
     * Returns an empty list if they are no questions
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(String feedbackSessionName, String courseId) {
        assert feedbackSessionName != null;
        assert courseId != null;

        return feedbackQuestionsLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
    }

    /**
     * Gets a set of giver identifiers that has at least one response under a feedback session.
     */
    public Set<String> getGiverSetThatAnswerFeedbackSession(String courseId, String feedbackSessionName) {
        assert courseId != null;
        assert feedbackSessionName != null;

        return feedbackResponsesLogic.getGiverSetThatAnswerFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Gets the session result for a feedback session.
     *
     * @see FeedbackResponsesLogic#getSessionResultsForCourse(
     * String, String, String, String, String, FeedbackResultFetchType)
     */
    public SessionResultsBundle getSessionResultsForCourse(
            String feedbackSessionName, String courseId, String userEmail,
            @Nullable String questionId, @Nullable String section, @Nullable FeedbackResultFetchType fetchType) {
        assert feedbackSessionName != null;
        assert courseId != null;
        assert userEmail != null;

        return feedbackResponsesLogic.getSessionResultsForCourse(
                feedbackSessionName, courseId, userEmail, questionId, section, fetchType);
    }

    /**
     * Gets the session result for a feedback session for the given user.
     *
     * @see FeedbackResponsesLogic#getSessionResultsForUser(String, String, String, boolean, String, boolean)
     */
    public SessionResultsBundle getSessionResultsForUser(
            String feedbackSessionName, String courseId, String userEmail, boolean isInstructor,
            @Nullable String questionId, boolean isPreviewResults) {
        assert feedbackSessionName != null;
        assert courseId != null;
        assert userEmail != null;

        return feedbackResponsesLogic.getSessionResultsForUser(
                feedbackSessionName, courseId, userEmail, isInstructor, questionId, isPreviewResults);
    }

    /**
     * Get existing feedback responses from student or his team for the given question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromStudentOrTeamForQuestion(
            FeedbackQuestionAttributes question, StudentAttributes student) {
        assert question != null;
        assert student != null;

        return feedbackResponsesLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(question, student);
    }

    /**
     * Get existing feedback responses from instructor for the given question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromInstructorForQuestion(
            FeedbackQuestionAttributes question, InstructorAttributes instructorAttributes) {
        assert question != null;
        assert instructorAttributes != null;

        return feedbackResponsesLogic.getFeedbackResponsesFromGiverForQuestion(
                question.getFeedbackQuestionId(), instructorAttributes.getEmail());
    }

    public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
        assert feedbackResponseId != null;
        return feedbackResponsesLogic.getFeedbackResponse(feedbackResponseId);
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
    public FeedbackResponseAttributes createFeedbackResponse(FeedbackResponseAttributes feedbackResponse)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert feedbackResponse != null;

        return feedbackResponsesLogic.createFeedbackResponse(feedbackResponse);
    }

    public boolean hasResponsesForCourse(String courseId) {
        return feedbackResponsesLogic.hasResponsesForCourse(courseId);
    }

    /**
     * Updates a feedback response by {@link FeedbackResponseAttributes.UpdateOptions}.
     *
     * <p>Cascade updates its associated feedback response comment
     * (e.g. associated response ID, giverSection and recipientSection).
     *
     * <p>If the giver/recipient field is changed, the response is updated by recreating the response
     * as question-giver-recipient is the primary key.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated feedback response
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     * @throws EntityAlreadyExistsException if the response cannot be updated
     *         by recreation because of an existent response
     */
    public FeedbackResponseAttributes updateFeedbackResponseCascade(FeedbackResponseAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        assert updateOptions != null;

        return feedbackResponsesLogic.updateFeedbackResponseCascade(updateOptions);
    }

    /**
     * Deletes a feedback response cascade its associated comments.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackResponseCascade(String responseId) {
        assert responseId != null;
        feedbackResponsesLogic.deleteFeedbackResponseCascade(responseId);
    }

    /**
     * Create a feedback response comment, and return the created comment.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public FeedbackResponseCommentAttributes createFeedbackResponseComment(
            FeedbackResponseCommentAttributes feedbackResponseComment)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        assert feedbackResponseComment != null;

        return feedbackResponseCommentsLogic.createFeedbackResponseComment(feedbackResponseComment);
    }

    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        assert feedbackResponseCommentId != null;
        return feedbackResponseCommentsLogic.getFeedbackResponseComment(feedbackResponseCommentId);
    }

    /**
     * Gets comment associated with the response.
     *
     * <p>The comment is given by a feedback participant to explain the response</p>
     *
     * @param feedbackResponseId the response id
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseCommentForResponseFromParticipant(
            String feedbackResponseId) {
        assert feedbackResponseId != null;

        return feedbackResponseCommentsLogic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
    }

    /**
     * Updates a feedback response comment by {@link FeedbackResponseCommentAttributes.UpdateOptions}.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(
            FeedbackResponseCommentAttributes.UpdateOptions updateOptions)
            throws EntityDoesNotExistException, InvalidParametersException {
        assert updateOptions != null;

        return feedbackResponseCommentsLogic.updateFeedbackResponseComment(updateOptions);
    }

    /**
     * Deletes a comment.
     */
    public void deleteFeedbackResponseComment(long commentId) {
        feedbackResponseCommentsLogic.deleteFeedbackResponseComment(commentId);
    }

    /**
     * Returns returns a list of sessions that were closed within past hour.
     *
     * @see FeedbackSessionsLogic#getFeedbackSessionsClosedWithinThePastHour()
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsClosedWithinThePastHour() {
        return feedbackSessionsLogic.getFeedbackSessionsClosedWithinThePastHour();
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsClosingWithinTimeLimit() {
        return feedbackSessionsLogic.getFeedbackSessionsClosingWithinTimeLimit();
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsOpeningWithinTimeLimit() {
        return feedbackSessionsLogic.getFeedbackSessionsOpeningWithinTimeLimit();
    }

    /**
     * Returns a list of sessions that require automated emails to be sent as they are published.
     *
     * @see FeedbackSessionsLogic#getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent()
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent() {
        return feedbackSessionsLogic.getFeedbackSessionsWhichNeedAutomatedPublishedEmailsToBeSent();
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedOpenEmailsToBeSent() {
        return feedbackSessionsLogic.getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
    }

    public String getSectionForTeam(String courseId, String teamName) {
        assert courseId != null;
        assert teamName != null;
        return studentsLogic.getSectionForTeam(courseId, teamName);
    }

    /**
     * Persists the given data bundle to the database.
     *
     * @see DataBundleLogic#persistDataBundle(DataBundle)
     */
    public DataBundle persistDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        return dataBundleLogic.persistDataBundle(dataBundle);
    }

    /**
     * Removes the given data bundle from the database.
     *
     * @see DataBundleLogic#removeDataBundle(DataBundle)
     */
    public void removeDataBundle(DataBundle dataBundle) {
        dataBundleLogic.removeDataBundle(dataBundle);
    }

    /**
     * Puts searchable documents from the data bundle to the database.
     *
     * @see DataBundleLogic#putDocuments(DataBundle)
     */
    public void putDocuments(DataBundle dataBundle) throws SearchServiceException {
        dataBundleLogic.putDocuments(dataBundle);
    }

    /**
     * Verifies that all the given students exist in the given course.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @throws EntityDoesNotExistException If some student does not exist in the course.
     */
    public void verifyAllStudentsExistInCourse(String courseId, Collection<String> studentEmailAddresses)
            throws EntityDoesNotExistException {
        assert courseId != null;
        assert studentEmailAddresses != null;

        studentsLogic.verifyAllStudentsExistInCourse(courseId, studentEmailAddresses);
    }

    public boolean isStudentsInSameTeam(String courseId, String student1Email, String student2Email) {
        assert courseId != null;
        assert student1Email != null;
        assert student2Email != null;
        return studentsLogic.isStudentsInSameTeam(courseId, student1Email, student2Email);
    }

    /**
     * Creates an account request.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the created account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityAlreadyExistsException if the account request already exists
     */
    public AccountRequestAttributes createAccountRequest(AccountRequestAttributes accountRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert accountRequest != null;

        return accountRequestsLogic.createAccountRequest(accountRequest);
    }

    /**
     * Updates an account request.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the updated account request
     * @throws InvalidParametersException if the account request is not valid
     * @throws EntityDoesNotExistException if the account request to update does not exist
     */
    public AccountRequestAttributes updateAccountRequest(AccountRequestAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return accountRequestsLogic.updateAccountRequest(updateOptions);
    }

    /**
     * Deletes an account request.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     */
    public void deleteAccountRequest(String email, String institute) {
        assert email != null;

        accountRequestsLogic.deleteAccountRequest(email, institute);
    }

    /**
     * Gets an account request by unique constraint {@code registrationKey}.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the account request
     */
    public AccountRequestAttributes getAccountRequestForRegistrationKey(String registrationKey) {
        assert registrationKey != null;

        return accountRequestsLogic.getAccountRequestForRegistrationKey(registrationKey);
    }

    /**
     * Gets an account request by email address and institute.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the account request
     */
    public AccountRequestAttributes getAccountRequest(String email, String institute) {
        assert email != null;
        assert institute != null;

        return accountRequestsLogic.getAccountRequest(email, institute);
    }

    /**
     * This is used by admin to search account requests in the whole system.
     *
     * @return A list of {@link AccountRequestAttributes} or {@code null} if no match found.
     */
    public List<AccountRequestAttributes> searchAccountRequestsInWholeSystem(String queryString)
            throws SearchServiceException {
        assert queryString != null;

        return accountRequestsLogic.searchAccountRequestsInWholeSystem(queryString);
    }

    /**
     * Creates or updates search document for the given account request.
     *
     * @see AccountRequestsLogic#putDocument(AccountRequestAttributes)
     */
    public void putAccountRequestDocument(AccountRequestAttributes accountRequest) throws SearchServiceException {
        accountRequestsLogic.putDocument(accountRequest);
    }

    public List<UsageStatisticsAttributes> getUsageStatisticsForTimeRange(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;
        assert startTime.toEpochMilli() < endTime.toEpochMilli();

        return usageStatisticsLogic.getUsageStatisticsForTimeRange(startTime, endTime);
    }

    public UsageStatisticsAttributes calculateEntitiesStatisticsForTimeRange(Instant startTime, Instant endTime) {
        assert startTime != null;
        assert endTime != null;
        assert startTime.toEpochMilli() < endTime.toEpochMilli();
        return usageStatisticsLogic.calculateEntitiesStatisticsForTimeRange(startTime, endTime);
    }

    public void createUsageStatistics(UsageStatisticsAttributes attributes)
            throws EntityAlreadyExistsException, InvalidParametersException {
        usageStatisticsLogic.createUsageStatistics(attributes);
    }

    /**
     * Updates a deadline extension.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the updated deadline extension
     * @throws InvalidParametersException if the updated deadline extension is not valid
     * @throws EntityDoesNotExistException if the deadline extension to update does not exist
     */
    public DeadlineExtensionAttributes updateDeadlineExtension(DeadlineExtensionAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        return deadlineExtensionsLogic.updateDeadlineExtension(updateOptions);
    }

    /**
     * Creates a deadline extension.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the created deadline extension
     * @throws InvalidParametersException if the deadline extension is not valid
     * @throws EntityAlreadyExistsException if the deadline extension to create already exists
     */
    public DeadlineExtensionAttributes createDeadlineExtension(DeadlineExtensionAttributes deadlineExtension)
            throws InvalidParametersException, EntityAlreadyExistsException {
        assert deadlineExtension != null;

        return deadlineExtensionsLogic.createDeadlineExtension(deadlineExtension);
    }

    /**
     * Deletes a deadline extension.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * <p>Fails silently if the deadline extension doesn't exist.</p>
     */
    public void deleteDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        assert courseId != null;
        assert feedbackSessionName != null;
        assert userEmail != null;

        deadlineExtensionsLogic.deleteDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Gets a deadline extension by {@code courseId}, {@code feedbackSessionName},
     * {@code userEmail} and {@code isInstructor}.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the deadline extension if it exists, null otherwise
     */
    public DeadlineExtensionAttributes getDeadlineExtension(
            String courseId, String feedbackSessionName, String userEmail, boolean isInstructor) {
        assert courseId != null;
        assert feedbackSessionName != null;
        assert userEmail != null;

        return deadlineExtensionsLogic.getDeadlineExtension(courseId, feedbackSessionName, userEmail, isInstructor);
    }

    /**
     * Gets a list of deadline extensions with end time within the next 24 hours
     * and possibly need a closing email to be sent.
     */
    public List<DeadlineExtensionAttributes> getDeadlineExtensionsPossiblyNeedingClosingEmail() {
        return deadlineExtensionsLogic.getDeadlineExtensionsPossiblyNeedingClosingEmail();
    }

}
