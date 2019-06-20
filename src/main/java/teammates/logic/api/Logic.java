package teammates.logic.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.SectionDetail;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.datatransfer.attributes.StudentProfileAttributes;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.DataBundleLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.ProfilesLogic;
import teammates.logic.core.StudentsLogic;

/**
 * Provides the business logic for production usage of the system.
 *
 * <p>This is a Facade class which simply forwards the method to internal classes.
 */
public class Logic {

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    private static final FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic feedbackResponsesLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackResponseCommentsLogic feedbackResponseCommentsLogic =
            FeedbackResponseCommentsLogic.inst();
    private static final ProfilesLogic profilesLogic = ProfilesLogic.inst();
    private static final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public AccountAttributes getAccount(String googleId) {
        Assumption.assertNotNull(googleId);

        return accountsLogic.getAccount(googleId);
    }

    /**
     * Updates/Creates the profile using {@link StudentProfileAttributes.UpdateOptions}.
     *
     * <br/> Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated student profile
     * @throws InvalidParametersException if attributes to update are not valid
     */
    public StudentProfileAttributes updateOrCreateStudentProfile(StudentProfileAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException {

        Assumption.assertNotNull(updateOptions);

        return profilesLogic.updateOrCreateStudentProfile(updateOptions);
    }

    /**
     * Deletes both instructor and student privileges, as long as the account and associated student profile.
     *
     * <ul>
     * <li>Fails silently if no such account.</li>
     * </ul>
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     */
    public void deleteAccountCascade(String googleId) {

        Assumption.assertNotNull(googleId);

        accountsLogic.deleteAccountCascade(googleId);
    }

    /**
     * Delete the picture associated with the {@code key} in Cloud Storage.
     *
     * <br/> Preconditions: <br/>
     * All parameters are non-null.
     *
     * <p>Fails silently if the {@code key} doesn't exist.</p>
     */
    public void deletePicture(String key) {
        Assumption.assertNotNull(key);

        profilesLogic.deletePicture(key);
    }

    /**
     * Deletes {@code pictureKey} for the student profile associated with {@code googleId}.
     *
     * <p>If the associated profile doesn't exist, create a new one.</p>
     */
    public void deletePictureKey(String googleId) {
        Assumption.assertNotNull(googleId);

        profilesLogic.deletePictureKey(googleId);
    }

    /**
     * Add an instructor for a course. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    @Deprecated
    public void addInstructor(String courseId, String name, String email, String role)
            throws InvalidParametersException, EntityAlreadyExistsException {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(name);
        Assumption.assertNotNull(email);

        InstructorAttributes instructor = InstructorAttributes.builder(courseId, email)
                .withName(name)
                .withRole(role)
                .withPrivileges(new InstructorPrivileges(role))
                .build();
        instructorsLogic.createInstructor(instructor);
    }

    /**
     * Creates an instructor.
     *
     * <p>Preconditions:</p>
     * * All parameters are non-null.
     *
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the Datastore
     */
    public InstructorAttributes createInstructor(InstructorAttributes instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Assumption.assertNotNull(instructor);

        return instructorsLogic.createInstructor(instructor);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     * @return Null if no match found.
     */
    public InstructorSearchResultBundle searchInstructorsInWholeSystem(String queryString) {
        Assumption.assertNotNull(queryString);

        return instructorsLogic.searchInstructorsInWholeSystem(queryString);
    }

    /**
     * Batch creates or updates documents for the given Instructors.
     *
     * @see InstructorsLogic#putDocuments(List)
     */
    public void putInstructorDocuments(List<InstructorAttributes> instructors) {
        instructorsLogic.putDocuments(instructors);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

        return instructorsLogic.getInstructorForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorById(String courseId, String email) {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

        return instructorsLogic.getInstructorById(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {

        Assumption.assertNotNull(googleId);
        Assumption.assertNotNull(courseId);

        return instructorsLogic.getInstructorForGoogleId(courseId, googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String encryptedKey) {

        Assumption.assertNotNull(encryptedKey);

        return instructorsLogic.getInstructorForRegistrationKey(encryptedKey);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {

        Assumption.assertNotNull(googleId);

        return instructorsLogic.getInstructorsForGoogleId(googleId);
    }

    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean omitArchived) {

        Assumption.assertNotNull(googleId);

        return instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {

        Assumption.assertNotNull(courseId);

        return instructorsLogic.getInstructorsForCourse(courseId);
    }

    /**
     * Get the encrypted registration key for the instructor.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public String getEncryptedKeyForInstructor(String courseId, String email)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

        return instructorsLogic.getEncryptedKeyForInstructor(courseId, email);
    }

    public List<FeedbackSessionAttributes> getAllOngoingSessions(Instant rangeStart, Instant rangeEnd) {

        return feedbackSessionsLogic.getAllOngoingSessions(rangeStart, rangeEnd);
    }

    /**
     * Returns true if this user has instructor privileges.
     */
    public boolean isInstructor(String googleId) {

        return accountsLogic.isAccountAnInstructor(googleId);
    }

    /**
     * Returns true if this user is an instructor of the course.
     */
    public boolean isInstructorOfCourse(String googleId, String courseId) {

        return instructorsLogic.isGoogleIdOfInstructorOfCourse(googleId, courseId);
    }

    /**
     * Returns true if the instructor is a new user.
     *
     * @see InstructorsLogic#isNewInstructor(String)
     */
    public boolean isNewInstructor(String googleId) {

        return instructorsLogic.isNewInstructor(googleId);
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithGoogleId}.
     *
     * <p>Cascade update the comments and responses given by the instructor.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorCascade(InstructorAttributes.UpdateOptionsWithGoogleId updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(updateOptions);

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
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(updateOptions);

        return instructorsLogic.updateInstructorByEmail(updateOptions);
    }

    /**
     * Make the instructor join the course, i.e. associate the Google ID to the instructor.<br>
     * Create an account for the instructor if there is no account exist for him.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public InstructorAttributes joinCourseForInstructor(String encryptedKey, String googleId, String institute)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        Assumption.assertNotNull(googleId);
        Assumption.assertNotNull(encryptedKey);

        return accountsLogic.joinCourseForInstructor(encryptedKey, googleId, institute);
    }

    /**
     * Downgrades an instructor account to student account.
     *
     * <p>Cascade deletes all instructors associated with the account.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void downgradeInstructorToStudentCascade(String googleId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(googleId);

        accountsLogic.downgradeInstructorToStudentCascade(googleId);
    }

    /**
     * Deletes an instructor cascade its associated feedback responses and comments.
     *
     * <p>Fails silently if the student does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteInstructorCascade(String courseId, String email) {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

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
        Assumption.assertNotNull(instructorGoogleId);
        Assumption.assertNotNull(courseAttributes);

        coursesLogic.createCourseAndInstructor(instructorGoogleId, courseAttributes);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public CourseAttributes getCourse(String courseId) {

        Assumption.assertNotNull(courseId);

        return coursesLogic.getCourse(courseId);
    }

    /**
     * Returns a detailed version of course data. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public CourseDetailsBundle getCourseDetails(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(courseId);
        return coursesLogic.getCourseSummary(courseId);
    }

    /**
     * Returns a course data, including its feedback sessions, according to the instructor passed in.<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public CourseSummaryBundle getCourseSummaryWithFeedbackSessions(InstructorAttributes instructor)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(instructor);
        return coursesLogic.getCourseSummaryWithFeedbackSessionsForInstructor(instructor);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<CourseAttributes> getCoursesForStudentAccount(String googleId) {
        Assumption.assertNotNull(googleId);
        return coursesLogic.getCoursesForStudentAccount(googleId);
    }

    /**
     * Omits archived courses if omitArchived == true<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return A less detailed version of courses for this instructor without stats.
     *         Returns an empty list if none found.
     */
    public Map<String, CourseSummaryBundle> getCourseSummariesWithoutStatsForInstructor(String googleId,
                                                                                            boolean omitArchived) {

        Assumption.assertNotNull(googleId);
        return coursesLogic.getCoursesSummaryWithoutStatsForInstructor(googleId, omitArchived);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return A less detailed version of courses for this instructor.
     *         Returns an empty list if none found.
     */
    public Map<String, CourseDetailsBundle> getCourseSummariesForInstructor(String googleId)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(googleId);
        return coursesLogic.getCourseSummariesForInstructor(googleId, false);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Details of courses the student is in. CourseData objects
     *         returned contain details of feedback sessions too (except the ones
     *         still AWAITING).
     */
    public List<CourseDetailsBundle> getCourseDetailsListForStudent(String googleId)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(googleId);
        return coursesLogic.getCourseDetailsListForStudent(googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Courses the instructor is in.
     */
    public List<CourseAttributes> getCoursesForInstructor(String googleId) {

        return getCoursesForInstructor(googleId, false);
    }

    /**
     * Omits archived courses if omitArchived == true<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Courses the instructor is in.
     */
    public List<CourseAttributes> getCoursesForInstructor(String googleId, boolean omitArchived) {

        Assumption.assertNotNull(googleId);
        return coursesLogic.getCoursesForInstructor(googleId, omitArchived);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Courses the given instructors is in except for courses in Recycle Bin.
     */
    public List<CourseAttributes> getCoursesForInstructor(List<InstructorAttributes> instructorList) {

        Assumption.assertNotNull(instructorList);
        return coursesLogic.getCoursesForInstructor(instructorList);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Courses in Recycle Bin that the given instructors is in.
     */
    public List<CourseAttributes> getSoftDeletedCoursesForInstructors(List<InstructorAttributes> instructorList) {

        Assumption.assertNotNull(instructorList);
        return coursesLogic.getSoftDeletedCoursesForInstructors(instructorList);
    }

    public CourseAttributes getSoftDeletedCourseForInstructor(InstructorAttributes instructor) {

        Assumption.assertNotNull(instructor);
        return coursesLogic.getSoftDeletedCourseForInstructor(instructor);
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
        Assumption.assertNotNull(updateOptions);

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

        Assumption.assertNotNull(googleId);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(archiveStatus);

        instructorsLogic.setArchiveStatusOfInstructor(googleId, courseId, archiveStatus);
    }

    /**
     * Deletes a course cascade its students, instructors, sessions, responses and comments.
     *
     * <p>Fails silently if no such course.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteCourseCascade(String courseId) {
        Assumption.assertNotNull(courseId);
        coursesLogic.deleteCourseCascade(courseId);
    }

    /**
     * Checks if the course is present in the system based on its courseid.
     */
    public boolean isCoursePresent(String courseid) {
        return coursesLogic.isCoursePresent(courseid);
    }

    /**
     * Moves a course to Recycle Bin by its given corresponding ID.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(courseId);
        coursesLogic.moveCourseToRecycleBin(courseId);
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
        Assumption.assertNotNull(courseId);

        coursesLogic.restoreCourseFromRecycleBin(courseId);
    }

    /**
     * Restores all courses and all data related to these courses from Recycle Bin.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void restoreAllCoursesFromRecycleBin(List<InstructorAttributes> instructorList)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(instructorList);

        coursesLogic.restoreAllCoursesFromRecycleBin(instructorList);
    }

    /**
     * Search for students. Preconditions: all parameters are non-null.
     * @param instructors   a list of InstructorAttributes associated to a googleId,
     *                      used for filtering of search result
     * @return Null if no match found
     */
    public StudentSearchResultBundle searchStudents(String queryString, List<InstructorAttributes> instructors) {
        Assumption.assertNotNull(queryString);
        Assumption.assertNotNull(instructors);
        return studentsLogic.searchStudents(queryString, instructors);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @return Null if no match found.
     */
    public StudentSearchResultBundle searchStudentsInWholeSystem(String queryString) {
        Assumption.assertNotNull(queryString);

        return studentsLogic.searchStudentsInWholeSystem(queryString);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
        Assumption.assertNotNull(registrationKey);
        return studentsLogic.getStudentForRegistrationKey(registrationKey);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForEmail(String courseId, String email) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

        return studentsLogic.getStudentForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return an empty list if no match found.
     */
    public List<StudentAttributes> getAllStudentForEmail(String email) {
        Assumption.assertNotNull(email);

        return studentsLogic.getAllStudentsForEmail(email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(googleId);

        return studentsLogic.getStudentForCourseIdAndGoogleId(courseId, googleId);
    }

    /**
     * Gets student profile associated with the {@code googleId}.
     *
     * <br/> Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return null if no match found.
     */
    public StudentProfileAttributes getStudentProfile(String googleId) {
        Assumption.assertNotNull(googleId);
        return profilesLogic.getStudentProfile(googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     *
     * @return Empty list if no match found.
     */
    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        Assumption.assertNotNull(googleId);
        return studentsLogic.getStudentsForGoogleId(googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        Assumption.assertNotNull(courseId);
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
        Assumption.assertNotNull(courseId);
        return coursesLogic.getSectionsNameForCourse(courseId);
    }

    /**
     * Returns a list of {@link SectionDetailsBundle} for a given course using courseId.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see CoursesLogic#getSectionsForCourseWithoutStats(String)
     */
    public List<SectionDetailsBundle> getSectionsForCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(courseId);
        return coursesLogic.getSectionsForCourseWithoutStats(courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<TeamDetailsBundle> getTeamsForCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(courseId);
        return coursesLogic.getTeamsForCourse(courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public TeamDetailsBundle getTeamDetailsForStudent(StudentAttributes student) {
        Assumption.assertNotNull(student);
        return studentsLogic.getTeamDetailsForStudent(student);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public String getEncryptedKeyForStudent(String courseId, String email) throws EntityDoesNotExistException {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

        return studentsLogic.getEncryptedKeyForStudent(courseId, email);
    }

    /**
     * Resets the googleId associated with the student.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void resetStudentGoogleId(String originalEmail, String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(originalEmail);
        Assumption.assertNotNull(courseId);

        studentsLogic.resetStudentGoogleId(originalEmail, courseId);
    }

    /**
     * Resets the associated googleId of an instructor.
     */
    public void resetInstructorGoogleId(String originalEmail, String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(originalEmail);
        Assumption.assertNotNull(courseId);

        instructorsLogic.resetInstructorGoogleId(originalEmail, courseId);
    }

    /**
     * Creates a student.
     *
     * @return the created student.
     * @throws InvalidParametersException if the student is not valid.
     * @throws EntityAlreadyExistsException if the student already exists in the Datastore.
     */
    public StudentAttributes createStudent(StudentAttributes student)
            throws InvalidParametersException, EntityAlreadyExistsException {
        Assumption.assertNotNull(student.getCourse());
        Assumption.assertNotNull(student.getEmail());

        return studentsLogic.createStudent(student);
    }

    /**
     * Updates a student by {@link StudentAttributes.UpdateOptions}.
     *
     * <p>If email changed, update by recreating the student and cascade update all responses the student gives/receives.
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

        Assumption.assertNotNull(updateOptions);

        return studentsLogic.updateStudentCascade(updateOptions);
    }

    /**
     * Make the student join the course, i.e. associate the Google ID to the student.<br>
     * Create an account for the student if there is no account exist for him.
     * Preconditions: <br>
     * * All parameters are non-null.
     * @param key the encrypted registration key
     */
    public StudentAttributes joinCourseForStudent(String key, String googleId)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        Assumption.assertNotNull(googleId);
        Assumption.assertNotNull(key);

        return accountsLogic.joinCourseForStudent(key, googleId);

    }

    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        Assumption.assertNotNull(courseId);
        return studentsLogic.getUnregisteredStudentsForCourse(courseId);
    }

    public boolean isFeedbackSessionCompletedByInstructor(FeedbackSessionAttributes fsa, String userEmail)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(fsa);
        Assumption.assertNotNull(userEmail);
        return feedbackSessionsLogic.isFeedbackSessionCompletedByInstructor(fsa, userEmail);
    }

    public boolean isFeedbackSessionCompletedByStudent(FeedbackSessionAttributes fsa, String userEmail) {
        Assumption.assertNotNull(fsa);
        Assumption.assertNotNull(userEmail);
        return feedbackSessionsLogic.isFeedbackSessionCompletedByStudent(fsa, userEmail);
    }

    /**
     * Deletes a student cascade its associated feedback responses and comments.
     *
     * <p>Fails silently if the student does not exist.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteStudentCascade(String courseId, String studentEmail) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(studentEmail);

        studentsLogic.deleteStudentCascade(courseId, studentEmail);
    }

    /**
     * Deletes all the students in the course cascade their associated responses and comments.
     *
     * <br/>Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteStudentsInCourseCascade(String courseId) {
        Assumption.assertNotNull(courseId);

        studentsLogic.deleteStudentsInCourseCascade(courseId);
    }

    /**
     *  Checks if a course has sections for each team
     *  Preconditions: <br>
     *  * All parameters are non-null.
     */
    public boolean hasIndicatedSections(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(courseId);
        return coursesLogic.hasIndicatedSections(courseId);
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

        Assumption.assertNotNull(studentList);
        Assumption.assertNotNull(courseId);

        studentsLogic.validateSectionsAndTeams(studentList, courseId);
    }

    /**
     * Validates teams for any team name violations.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see StudentsLogic#validateTeams(List, String)
     */
    public void validateTeams(List<StudentAttributes> studentList, String courseId) throws EnrollException {

        Assumption.assertNotNull(studentList);
        Assumption.assertNotNull(courseId);

        studentsLogic.validateTeams(studentList, courseId);
    }

    /**
     * Gets all students of a team.
     */
    public List<StudentAttributes> getStudentsForTeam(String teamName, String courseId) {
        Assumption.assertNotNull(teamName);
        Assumption.assertNotNull(courseId);

        return studentsLogic.getStudentsForTeam(teamName, courseId);
    }

    /**
     * Batch creates or updates search documents for the given students.
     */
    public void putStudentDocuments(List<StudentAttributes> students) {
        studentsLogic.putDocuments(students);
    }

    /**
     * Generates students list of a course in CSV format. <br>
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     */
    public String getCourseStudentListAsCsv(String courseId, String googleId) throws EntityDoesNotExistException {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(googleId);

        return coursesLogic.getCourseStudentListAsCsv(courseId, googleId);
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
        Assumption.assertNotNull(feedbackSession);

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

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

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
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackSessionsLogic.getFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(courseId);
        return feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionDetailsBundle getFeedbackSessionDetails(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackSessionsLogic.getFeedbackSessionDetails(feedbackSessionName, courseId);
    }

    /**
     * Returns a {@code List} of all feedback sessions WITHOUT their response
     * statistics for a instructor given by his googleId.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see FeedbackSessionsLogic#getFeedbackSessionsListForInstructor(String, boolean)
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(String googleId, boolean omitArchived) {
        Assumption.assertNotNull(googleId);
        return feedbackSessionsLogic.getFeedbackSessionsListForInstructor(googleId, omitArchived);
    }

    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) {
        Assumption.assertNotNull(instructorList);
        return feedbackSessionsLogic.getFeedbackSessionsListForInstructor(instructorList);
    }

    /**
     * Returns a {@code List} of all feedback sessions in Recycle Bin WITHOUT their response
     * statistics for a instructor.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForInstructor(InstructorAttributes instructor) {
        Assumption.assertNotNull(instructor);
        return feedbackSessionsLogic.getSoftDeletedFeedbackSessionsListForInstructor(instructor);
    }

    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForInstructors(
            List<InstructorAttributes> instructorList) {
        Assumption.assertNotNull(instructorList);
        return feedbackSessionsLogic.getSoftDeletedFeedbackSessionsListForInstructors(instructorList);
    }

    /**
     * Gets {@code FeedbackQuestions} and previously filled
     * {@code FeedbackResponses} that an instructor can view/submit as a
     * {@link FeedbackSessionQuestionsBundle}.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see FeedbackSessionsLogic#getFeedbackSessionQuestionsForInstructor(String, String, String)
     */
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsBundleForInstructor(String feedbackSessionName,
                                                                                         String courseId,
                                                                                         String userEmail)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);

        return feedbackSessionsLogic.getFeedbackSessionQuestionsForInstructor(feedbackSessionName, courseId, userEmail);
    }

    /**
     * Gets {@code FeedbackQuestions} and previously filled
     * {@code FeedbackResponses} that a student can view/submit as a
     * {@link FeedbackSessionQuestionsBundle}.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see FeedbackSessionsLogic#getFeedbackSessionQuestionsForStudent(String, String, String)
     */
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsBundleForStudent(String feedbackSessionName,
                                                                                      String courseId,
                                                                                      String userEmail)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);

        return feedbackSessionsLogic.getFeedbackSessionQuestionsForStudent(feedbackSessionName, courseId, userEmail);
    }

    /**
     * Gets the recipients of a feedback question for student.
     *
     * @see FeedbackQuestionsLogic#getRecipientsOfQuestionForStudent(FeedbackQuestionAttributes, String, String)
     */
    public Map<String, String> getRecipientsOfQuestionForStudent(
            FeedbackQuestionAttributes question, String giverEmail, String giverTeam) {
        Assumption.assertNotNull(question);
        Assumption.assertNotNull(giverEmail);
        Assumption.assertNotNull(giverTeam);

        return feedbackQuestionsLogic.getRecipientsOfQuestionForStudent(question, giverEmail, giverTeam);
    }

    /**
     * Gets the recipients of a feedback question for instructor.
     *
     * @see FeedbackQuestionsLogic#getRecipientsOfQuestionForInstructor(FeedbackQuestionAttributes, String)
     */
    public Map<String, String> getRecipientsOfQuestionForInstructor(FeedbackQuestionAttributes question, String giverEmail) {
        Assumption.assertNotNull(question);
        Assumption.assertNotNull(giverEmail);

        return feedbackQuestionsLogic.getRecipientsOfQuestionForInstructor(question, giverEmail);
    }

    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackSessionName,
                                                          String courseId,
                                                          int questionNumber) {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackQuestionsLogic.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     *
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        Assumption.assertNotNull(feedbackQuestionId);
        return feedbackQuestionsLogic.getFeedbackQuestion(feedbackQuestionId);
    }

    /**
     * Gets a list of all questions for the given session that
     * students can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForStudents(
            String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackQuestionsLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
    }

    /**
     * Gets a {@code List} of all questions for the given session that
     * instructor can view/submit.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForInstructors(
            String feedbackSessionName, String courseId, String instructorEmail) throws EntityDoesNotExistException {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackQuestionsLogic.getFeedbackQuestionsForInstructor(feedbackSessionName, courseId, instructorEmail);
    }

    /**
     * Generates summary results (without comments) in CSV format. <br>
     * Preconditions: <br>
     * * All parameters(except questionId) are non-null. <br>
     * @see FeedbackSessionsLogic#getFeedbackSessionResultsSummaryAsCsv(String, String,
     *      String, String, boolean, boolean)
     */
    public String getFeedbackSessionResultSummaryAsCsv(
            String courseId, String feedbackSessionName, String instructorEmail,
            boolean isMissingResponsesShown, boolean isStatsShown, String questionId)
            throws EntityDoesNotExistException, ExceedingRangeException {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

        return feedbackSessionsLogic.getFeedbackSessionResultsSummaryAsCsv(
                feedbackSessionName, courseId, instructorEmail, questionId,
                isMissingResponsesShown, isStatsShown);
    }

    /**
     * Generates summary results (without comments) within a section in CSV format. <br>
     * Preconditions: <br>
     * * All parameters(except questionId) are non-null. <br>
     * @see FeedbackSessionsLogic#getFeedbackSessionResultsSummaryInSectionAsCsv(String, String, String,
     *      String, SectionDetail, String, boolean, boolean)
     */
    public String getFeedbackSessionResultSummaryInSectionAsCsv(
            String courseId, String feedbackSessionName, String instructorEmail, String section,
            SectionDetail sectionDetail, String questionId, boolean isMissingResponsesShown, boolean isStatsShown)
            throws EntityDoesNotExistException, ExceedingRangeException {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(section);
        Assumption.assertNotNull(sectionDetail);

        return feedbackSessionsLogic.getFeedbackSessionResultsSummaryInSectionAsCsv(
                feedbackSessionName, courseId, instructorEmail, section, sectionDetail,
                questionId, isMissingResponsesShown, isStatsShown);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of viewable feedback sessions for any user in the course.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForUserInCourse(String courseId, String userEmail)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(userEmail);
        Assumption.assertNotNull(courseId);

        return feedbackSessionsLogic.getFeedbackSessionsForUserInCourse(courseId, userEmail);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public boolean hasStudentSubmittedFeedback(FeedbackSessionAttributes fsa, String studentEmail) {

        Assumption.assertNotNull(fsa);
        Assumption.assertNotNull(studentEmail);

        return feedbackSessionsLogic.isFeedbackSessionCompletedByStudent(fsa, studentEmail);
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
        Assumption.assertNotNull(updateOptions);

        return feedbackSessionsLogic.updateFeedbackSession(updateOptions);
    }

    /**
     * Adds an instructor with {@code email} in the instructor respondent set
     * in feedback session {@code feedbackSessionName} in {@code courseId}.
     */
    public void addInstructorRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(email);
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        feedbackSessionsLogic.addInstructorRespondent(email, feedbackSessionName, courseId);
    }

    /**
     * Adds a student with {@code email} in the student respondent set
     * in feedback session {@code feedbackSessionName} in {@code courseId}.
     */
    public void addStudentRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(email);
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        feedbackSessionsLogic.addStudentRespondent(email, feedbackSessionName, courseId);
    }

    /**
     * Deletes an instructor with {@code email} in the instructor respondent set
     * in session {@code feedbackSessionName} of course {@code courseId}.
     */
    public void deleteInstructorRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(email);
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        feedbackSessionsLogic.deleteInstructorRespondent(email, feedbackSessionName, courseId);
    }

    /**
     * Deletes a student with {@code email} in the student respondent set
     * in session {@code feedbackSessionName} of course {@code courseId}.
     */
    public void deleteStudentRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(email);
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        feedbackSessionsLogic.deleteStudentFromRespondentList(email, feedbackSessionName, courseId);
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

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

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

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackSessionsLogic.unpublishFeedbackSession(feedbackSessionName, courseId);
    }

    /**
     * Deletes a feedback session cascade to its associated questions, responses and comments.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackSessionCascade(String feedbackSessionName, String courseId) {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        feedbackSessionsLogic.deleteFeedbackSessionCascade(feedbackSessionName, courseId);
    }

    /**
     * Soft-deletes a specific session to Recycle Bin.
     */
    public void moveFeedbackSessionToRecycleBin(String feedbackSessionName, String courseId)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        feedbackSessionsLogic.moveFeedbackSessionToRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Restores a specific session from Recycle Bin to feedback sessions table.
     */
    public void restoreFeedbackSessionFromRecycleBin(String feedbackSessionName, String courseId)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        feedbackSessionsLogic.restoreFeedbackSessionFromRecycleBin(feedbackSessionName, courseId);
    }

    /**
     * Restores all sessions from Recycle Bin to feedback sessions table.
     */
    public void restoreAllFeedbackSessionsFromRecycleBin(List<InstructorAttributes> instructorList)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(instructorList);

        feedbackSessionsLogic.restoreAllFeedbackSessionsFromRecycleBin(instructorList);
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
        Assumption.assertNotNull(feedbackQuestion);

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
        Assumption.assertNotNull(updateOptions);

        return feedbackQuestionsLogic.updateFeedbackQuestionCascade(updateOptions);
    }

    /**
     * Deletes a feedback question cascade its responses and comments.
     *
     * <p>Silently fail if question does not exist.
     *
     * <p>The respondent lists will also be updated due the deletion of question.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackQuestionCascade(String questionId) {
        Assumption.assertNotNull(questionId);
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
     * Gets all copiable questions for an instructor<br>
     * Returns an empty list if they are no questions
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackQuestionAttributes> getCopiableFeedbackQuestionsForInstructor(String googleId)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(googleId);
        return feedbackQuestionsLogic.getCopiableFeedbackQuestionsForInstructor(googleId);
    }

    /**
     * Gets all questions for a feedback session.<br>
     * Returns an empty list if they are no questions
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackQuestionsLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
    }

    /**
     * Gets the response rate status for a session.
     *
     * <p>Preconditions: <br>
     * * All parameters are non-null.
     *
     * @see FeedbackSessionsLogic#getFeedbackSessionResponseStatus(String, String)
     */
    public FeedbackSessionResponseStatus getFeedbackSessionResponseStatus(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackSessionsLogic.getFeedbackSessionResponseStatus(feedbackSessionName, courseId);
    }

    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the student for a feedback session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForStudent(String feedbackSessionName,
                                                                            String courseId, String userEmail)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForStudent(feedbackSessionName, courseId, userEmail);
    }

    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session within the given range
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, int range, String viewType)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);
        Assumption.assertNotNull(viewType);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorWithinRangeFromView(feedbackSessionName,
                                                                                               courseId, userEmail,
                                                                                               range, viewType);
    }

    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session from a section within the given range
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, int range)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorFromSectionWithinRange(
                                        feedbackSessionName, courseId, userEmail, section, range);
    }

    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session to a section within the given range
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorToSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, int range)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorToSectionWithinRange(
                                        feedbackSessionName, courseId, userEmail, section, range);
    }

    /**
     * Gets a set of giver identifiers that has at least one response under a feedback session.
     */
    public Set<String> getGiverSetThatAnswerFeedbackSession(String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

        return feedbackResponsesLogic.getGiverSetThatAnswerFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session from a given question number
     * This will not retrieve the list of comments for this question
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromQuestion(
            String feedbackSessionName, String courseId, String userEmail, String questionId)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorFromQuestion(feedbackSessionName, courseId,
                                                                                        userEmail, questionId);
    }

    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session from a given question number
     * in a given section.
     * This will not retrieve the list of comments for this question
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromQuestionInSection(
                                    String feedbackSessionName, String courseId, String userEmail,
                                    String questionId, String selectedSection, SectionDetail selectedSectionDetail)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);
        Assumption.assertNotNull(selectedSection);
        Assumption.assertNotNull(selectedSectionDetail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorFromQuestionInSection(
                                            feedbackSessionName, courseId, userEmail,
                                            questionId, selectedSection, selectedSectionDetail);
    }

    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructor(
            String feedbackSessionName, String courseId, String userEmail)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructor(feedbackSessionName, courseId, userEmail);
    }

    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session in a specific section.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorInSection(String feedbackSessionName,
            String courseId, String userEmail, String section, SectionDetail sectionDetail)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);
        Assumption.assertNotNull(section);
        Assumption.assertNotNull(sectionDetail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorInSection(feedbackSessionName, courseId,
                                                                                     userEmail, section, sectionDetail);
    }

    /**
     * Get existing feedback responses from student or his team for the given question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromStudentOrTeamForQuestion(
            FeedbackQuestionAttributes question, StudentAttributes student) {
        Assumption.assertNotNull(question);
        Assumption.assertNotNull(student);

        return feedbackResponsesLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(question, student);
    }

    /**
     * Get existing feedback responses from instructor for the given question.
     */
    public List<FeedbackResponseAttributes> getFeedbackResponsesFromInstructorForQuestion(
            FeedbackQuestionAttributes question, InstructorAttributes instructorAttributes) {
        Assumption.assertNotNull(question);
        Assumption.assertNotNull(instructorAttributes);

        return feedbackResponsesLogic.getFeedbackResponsesFromGiverForQuestion(
                question.getFeedbackQuestionId(), instructorAttributes.getEmail());
    }

    public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
        Assumption.assertNotNull(feedbackResponseId);
        return feedbackResponsesLogic.getFeedbackResponse(feedbackResponseId);
    }

    public FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId,
                                                          String giverEmail,
                                                          String recipient) {

        Assumption.assertNotNull(feedbackQuestionId);
        Assumption.assertNotNull(giverEmail);
        Assumption.assertNotNull(recipient);

        return feedbackResponsesLogic.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
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
        Assumption.assertNotNull(feedbackResponse);

        return feedbackResponsesLogic.createFeedbackResponse(feedbackResponse);
    }

    public boolean hasGiverRespondedForSession(String userEmail, String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(userEmail);
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);

        return feedbackResponsesLogic.hasGiverRespondedForSession(userEmail, feedbackSessionName, courseId);
    }

    public boolean hasResponsesForCourse(String courseId) {
        return feedbackResponsesLogic.hasResponsesForCourse(courseId);
    }

    public boolean isOpenOrPublishedEmailSentForTheCourse(String courseId) {
        Assumption.assertNotNull(courseId);
        return feedbackSessionsLogic.isOpenOrPublishedEmailSentForTheCourse(courseId);
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
        Assumption.assertNotNull(updateOptions);

        return feedbackResponsesLogic.updateFeedbackResponseCascade(updateOptions);
    }

    /**
     * Deletes a feedback response cascade its associated comments.
     *
     * <p>The respondent lists will NOT be updated.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     */
    public void deleteFeedbackResponseCascade(String responseId) {
        Assumption.assertNotNull(responseId);
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
        Assumption.assertNotNull(feedbackResponseComment);

        return feedbackResponseCommentsLogic.createFeedbackResponseComment(feedbackResponseComment);
    }

    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        Assumption.assertNotNull(feedbackResponseCommentId);
        return feedbackResponseCommentsLogic.getFeedbackResponseComment(feedbackResponseCommentId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String responseId, String giverEmail, Instant creationDate) {
        Assumption.assertNotNull(responseId);
        Assumption.assertNotNull(giverEmail);
        Assumption.assertNotNull(creationDate);

        return feedbackResponseCommentsLogic.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForGiver(String courseId,
                                                                                      String giverEmail) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(giverEmail);

        return feedbackResponseCommentsLogic.getFeedbackResponseCommentsForGiver(courseId, giverEmail);
    }

    /**
     * Creates or updates document for the given comment.
     *
     * @see FeedbackResponseCommentsLogic#putDocument(FeedbackResponseCommentAttributes)
     */
    public void putDocument(FeedbackResponseCommentAttributes comment) {
        feedbackResponseCommentsLogic.putDocument(comment);
    }

    /**
     * Batch creates or updates documents for the given comments.
     *
     * @see FeedbackResponseCommentsLogic#putDocuments(List)
     */
    public void putFeedbackResponseCommentDocuments(List<FeedbackResponseCommentAttributes> comments) {
        feedbackResponseCommentsLogic.putDocuments(comments);
    }

    /**
     * Search for FeedbackResponseComment. Preconditions: all parameters are non-null.
     * @param instructors   a list of InstructorAttributes associated to a googleId,
     *                      used for filtering of search result
     * @return Null if no match found
     */
    public FeedbackResponseCommentSearchResultBundle searchFeedbackResponseComments(String queryString,
                                                                         List<InstructorAttributes> instructors) {
        Assumption.assertNotNull(queryString);
        Assumption.assertNotNull(instructors);
        return feedbackResponseCommentsLogic.searchFeedbackResponseComments(queryString, instructors);
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
        Assumption.assertNotNull(updateOptions);

        return feedbackResponseCommentsLogic.updateFeedbackResponseComment(updateOptions);
    }

    /**
     * Deletes a comment.
     */
    public void deleteFeedbackResponseComment(long commentId) {
        feedbackResponseCommentsLogic.deleteFeedbackResponseComment(commentId);
    }

    public List<String> getArchivedCourseIds(List<CourseAttributes> allCourses,
                                             Map<String, InstructorAttributes> instructorsForCourses) {
        Assumption.assertNotNull(allCourses);
        Assumption.assertNotNull(instructorsForCourses);
        return coursesLogic.getArchivedCourseIds(allCourses, instructorsForCourses);
    }

    public List<FeedbackResponseAttributes>
            getFeedbackResponsesForSession(String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        return feedbackResponsesLogic.getFeedbackResponsesForSession(feedbackSessionName, courseId);
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
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(teamName);
        return studentsLogic.getSectionForTeam(courseId, teamName);
    }

    /**
     * Persists the given data bundle to the datastore.
     *
     * @see DataBundleLogic#persistDataBundle(DataBundle)
     */
    public void persistDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        dataBundleLogic.persistDataBundle(dataBundle);
    }

    /**
     * Removes the given data bundle from the datastore.
     *
     * @see DataBundleLogic#removeDataBundle(DataBundle)
     */
    public void removeDataBundle(DataBundle dataBundle) {
        dataBundleLogic.removeDataBundle(dataBundle);
    }

    /**
     * Puts searchable documents from the data bundle to the datastore.
     *
     * @see DataBundleLogic#putDocuments(DataBundle)
     */
    public void putDocuments(DataBundle dataBundle) {
        dataBundleLogic.putDocuments(dataBundle);
    }

}
