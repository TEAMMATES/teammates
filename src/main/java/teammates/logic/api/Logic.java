package teammates.logic.api;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nullable;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.SessionResultsBundle;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.UserRole;
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
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.RegenerateStudentException;
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

    protected static final AccountsLogic accountsLogic = AccountsLogic.inst();
    protected static final StudentsLogic studentsLogic = StudentsLogic.inst();
    protected static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    protected static final CoursesLogic coursesLogic = CoursesLogic.inst();
    protected static final FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    protected static final FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();
    protected static final FeedbackResponsesLogic feedbackResponsesLogic = FeedbackResponsesLogic.inst();
    protected static final FeedbackResponseCommentsLogic feedbackResponseCommentsLogic =
            FeedbackResponseCommentsLogic.inst();
    protected static final ProfilesLogic profilesLogic = ProfilesLogic.inst();
    protected static final DataBundleLogic dataBundleLogic = DataBundleLogic.inst();

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public AccountAttributes getAccount(String googleId) {
        Assumption.assertNotNull(googleId);

        return accountsLogic.getAccount(googleId);
    }

    public String getCourseInstitute(String courseId) {
        return accountsLogic.getCourseInstitute(courseId);
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

    public List<FeedbackSessionAttributes> getAllOngoingSessions(Instant rangeStart, Instant rangeEnd) {

        return feedbackSessionsLogic.getAllOngoingSessions(rangeStart, rangeEnd);
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
     * Creates an account for the instructor if there is no existing account for him.
     * Preconditions: <br>
     * * Parameters encryptedKey and googleId are non-null.
     */
    public InstructorAttributes joinCourseForInstructor(String encryptedKey, String googleId, String institute, String mac)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        Assumption.assertNotNull(googleId);
        Assumption.assertNotNull(encryptedKey);

        return accountsLogic.joinCourseForInstructor(encryptedKey, googleId, institute, mac);
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
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<CourseAttributes> getCoursesForStudentAccount(String googleId) {
        Assumption.assertNotNull(googleId);
        return coursesLogic.getCoursesForStudentAccount(googleId);
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
     * Moves a course to Recycle Bin by its given corresponding ID.
     *
     * <br/>Preconditions: <br/>
     * * All parameters are non-null.
     *
     * @return the deletion timestamp assigned to the course.
     */
    public Instant moveCourseToRecycleBin(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(courseId);
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
        Assumption.assertNotNull(courseId);

        coursesLogic.restoreCourseFromRecycleBin(courseId);
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
        Assumption.assertNotNull(feedbackQuestionAttributes);
        Assumption.assertNotNull(emailOfEntityDoingQuestion);

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
        Assumption.assertNotNull(originalEmail);
        Assumption.assertNotNull(courseId);

        studentsLogic.resetStudentGoogleId(originalEmail, courseId);
    }

    /**
     * Regenerates the registration key for the student with email address {@code email} in course {@code courseId}.
     *
     * @return the student attributes with the new registration key.
     * @throws RegenerateStudentException if the newly generated course student has the same registration key as the
     *          original one.
     * @throws EntityDoesNotExistException if the student does not exist.
     */
    public StudentAttributes regenerateStudentRegistrationKey(String courseId, String email)
            throws EntityDoesNotExistException, RegenerateStudentException {

        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(email);

        return studentsLogic.regenerateStudentRegistrationKey(courseId, email);
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

    /**
     * Checks whether an instructor has completed a feedback session.
     *
     * <p> If there is no question for instructors, the feedback session is completed</p>
     */
    public boolean isFeedbackSessionCompletedByInstructor(FeedbackSessionAttributes fsa, String userEmail)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(fsa);
        Assumption.assertNotNull(userEmail);
        return feedbackSessionsLogic.isFeedbackSessionCompletedByInstructor(fsa, userEmail);
    }

    /**
     * Checks whether a student has completed a feedback session.
     *
     * <p> If there is no question for students, the feedback session is completed</p>
     */
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
     * Gets the expected number of submissions for a feedback session.
     *
     * <br>Preconditions: <br>
     * * All parameters are non-null.
     */
    public int getExpectedTotalSubmission(FeedbackSessionAttributes fsa) {
        Assumption.assertNotNull(fsa);
        return feedbackSessionsLogic.getExpectedTotalSubmission(fsa);
    }

    /**
     * Gets the actual number of submissions for a feedback session.
     *
     * <br>Preconditions: <br>
     * * All parameters are non-null.
     */
    public int getActualTotalSubmission(FeedbackSessionAttributes fsa) {
        Assumption.assertNotNull(fsa);
        return feedbackSessionsLogic.getActualTotalSubmission(fsa);
    }

    /**
     * Gets a list of feedback sessions for instructors.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) {
        Assumption.assertNotNull(instructorList);
        return feedbackSessionsLogic.getFeedbackSessionsListForInstructor(instructorList);
    }

    /**
     * Returns a {@code List} of feedback sessions in the Recycle Bin for the instructors.
     * <br>
     * Omits sessions if the corresponding courses are archived or in Recycle Bin
     */
    public List<FeedbackSessionAttributes> getSoftDeletedFeedbackSessionsListForInstructors(
            List<InstructorAttributes> instructorList) {
        Assumption.assertNotNull(instructorList);
        return feedbackSessionsLogic.getSoftDeletedFeedbackSessionsListForInstructors(instructorList);
    }

    /**
     * Gets the recipients of a feedback question for student.
     *
     * @see FeedbackQuestionsLogic#getRecipientsOfQuestion
     */
    public Map<String, String> getRecipientsOfQuestion(
            FeedbackQuestionAttributes question,
            @Nullable InstructorAttributes instructorGiver, @Nullable StudentAttributes studentGiver) {
        Assumption.assertNotNull(question);

        // we do not supply course roster here
        return feedbackQuestionsLogic.getRecipientsOfQuestion(question, instructorGiver, studentGiver, null);
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
     * Gets a set of giver identifiers that has at least one response under a feedback session.
     */
    public Set<String> getGiverSetThatAnswerFeedbackSession(String courseId, String feedbackSessionName) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(feedbackSessionName);

        return feedbackResponsesLogic.getGiverSetThatAnswerFeedbackSession(courseId, feedbackSessionName);
    }

    /**
     * Gets the session result for a feedback session.
     *
     * @see FeedbackSessionsLogic#getSessionResultsForUser(String, String, String, UserRole, String, String)
     */
    public SessionResultsBundle getSessionResultsForUser(
            String feedbackSessionName, String courseId, String userEmail, UserRole role,
            @Nullable String questionId, @Nullable String section) {
        Assumption.assertNotNull(feedbackSessionName);
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(userEmail);
        Assumption.assertNotNull(role);

        return feedbackSessionsLogic.getSessionResultsForUser(
                feedbackSessionName, courseId, userEmail, role, questionId, section);
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
        Assumption.assertNotNull(updateOptions);

        return feedbackResponsesLogic.updateFeedbackResponseCascade(updateOptions);
    }

    /**
     * Deletes a feedback response cascade its associated comments.
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

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForGiver(String courseId,
                                                                                      String giverEmail) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(giverEmail);

        return feedbackResponseCommentsLogic.getFeedbackResponseCommentsForGiver(courseId, giverEmail);
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
        Assumption.assertNotNull(feedbackResponseId);

        return feedbackResponseCommentsLogic.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
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
    public DataBundle persistDataBundle(DataBundle dataBundle) throws InvalidParametersException {
        return dataBundleLogic.persistDataBundle(dataBundle);
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

    public int getNumOfGeneratedChoicesForParticipantType(String courseId, FeedbackParticipantType generateOptionsFor) {
        Assumption.assertNotNull(courseId);
        Assumption.assertNotNull(generateOptionsFor);
        return feedbackQuestionsLogic.getNumOfGeneratedChoicesForParticipantType(courseId, generateOptionsFor);
    }

}
