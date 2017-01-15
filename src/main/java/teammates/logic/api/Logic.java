package teammates.logic.api;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.AdminEmailAttributes;
import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSearchResultBundle;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.CourseDetailsBundle;
import teammates.common.datatransfer.CourseEnrollmentResult;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.CourseSummaryBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.SectionDetailsBundle;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentProfileAttributes;
import teammates.common.datatransfer.StudentSearchResultBundle;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EnrollException;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.ExceedingRangeException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.JoinCourseException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.GoogleCloudStorageHelper;
import teammates.logic.core.AccountsLogic;
import teammates.logic.core.AdminEmailsLogic;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.CoursesLogic;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.logic.core.FeedbackSessionsLogic;
import teammates.logic.core.InstructorsLogic;
import teammates.logic.core.ProfilesLogic;
import teammates.logic.core.StudentsLogic;

import com.google.appengine.api.blobstore.BlobKey;

/**
 * This class represents the API to the business logic of the system. Please
 * refer to DevMan for general policies followed by Logic. As those policies
 * cover most of the behavior of the API, we use very short comments to describe
 * operations here.
 * Logic class is a Facade class. It simply forwards the method to internal classes.
 */
public class Logic {
    
    //TODO: sanitizes values received from outside.

    //TODO: remove this constant
    public static final String ERROR_NULL_PARAMETER = "The supplied parameter was null\n";
    
    protected static GateKeeper gateKeeper = GateKeeper.inst();
    protected static AccountsLogic accountsLogic = AccountsLogic.inst();
    protected static StudentsLogic studentsLogic = StudentsLogic.inst();
    protected static InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    protected static CoursesLogic coursesLogic = CoursesLogic.inst();
    protected static CommentsLogic commentsLogic = CommentsLogic.inst();
    protected static FeedbackSessionsLogic feedbackSessionsLogic = FeedbackSessionsLogic.inst();
    protected static FeedbackQuestionsLogic feedbackQuestionsLogic = FeedbackQuestionsLogic.inst();
    protected static FeedbackResponsesLogic feedbackResponsesLogic = FeedbackResponsesLogic.inst();
    protected static FeedbackResponseCommentsLogic feedbackResponseCommentsLogic = FeedbackResponseCommentsLogic.inst();
    protected static AdminEmailsLogic adminEmailsLogic = AdminEmailsLogic.inst();
    protected static ProfilesLogic profilesLogic = ProfilesLogic.inst();

    /**
     * Produces the URL the user should use to login to the system
     * 
     * @param redirectUrl
     *            This is the URL the user will be directed to after login.
     */
    public static String getLoginUrl(String redirectUrl) {
        return gateKeeper.getLoginUrl(redirectUrl);
    }

    /**
     * Produces the URL used to logout the user
     * 
     * @param redirectUrl
     *            This is the URL the user will be directed to after logout.
     */
    public static String getLogoutUrl(String redirectUrl) {
        return gateKeeper.getLogoutUrl(redirectUrl);
    }

    /**
     * Verifies if the user is logged into his/her Google account
     */
    public static boolean isUserLoggedIn() {
        return gateKeeper.isUserLoggedOn();
    }

    /**
     * @return Returns null if the user is not logged in.
     */
    public UserType getCurrentUser() {
        return gateKeeper.getCurrentUser();
    }


    /**
     * Creates a new Account based on given values. If a profile is not given,
     * a default empty profile is created for the user<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     */
    public void createAccount(String googleId, String name, boolean isInstructor, String email, String institute,
                              StudentProfileAttributes studentProfileParam) throws InvalidParametersException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, name);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, isInstructor);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, institute);
        
        StudentProfileAttributes studentProfile = studentProfileParam;
        if (studentProfile == null) {
            studentProfile = new StudentProfileAttributes();
            studentProfile.googleId = googleId;
        }
        AccountAttributes accountToAdd = new AccountAttributes(googleId, name, isInstructor, email, institute,
                                                               studentProfile);
        
        accountsLogic.createAccount(accountToAdd);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * This is just for legacy code that creates an Account without the profile parameter
     */
    public void createAccount(String googleId, String name, boolean isInstructor, String email, String institute)
            throws InvalidParametersException {

        createAccount(googleId, name, isInstructor, email, institute, null);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public AccountAttributes getAccount(String googleId) {
        return accountsLogic.getAccount(googleId, false);
    }
    
    public AccountAttributes getAccount(String googleId, boolean retrieveStudentProfile) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        
        return accountsLogic.getAccount(googleId, retrieveStudentProfile);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @return Details of accounts with instruction privileges. Returns empty
     *         list if no such accounts are found.
     */
    @Deprecated //Not scalable.
    public List<AccountAttributes> getInstructorAccounts() {
        
        return accountsLogic.getInstructorAccounts();
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.<br>
     * * {@code newAccountAttributes} represents an existing account.
     */
    public void updateAccount(AccountAttributes newAccountAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, newAccountAttributes);
        
        accountsLogic.updateAccount(newAccountAttributes);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.<br>
     * * {@code newAccountAttributes} represents an existing account.
     */
    public void updateStudentProfile(StudentProfileAttributes newStudentProfileAttributes)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, newStudentProfileAttributes);
        
        profilesLogic.updateStudentProfile(newStudentProfileAttributes);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.<br>
     * * {@code newAccountAttributes} represents an existing account.
     */
    public void updateStudentProfilePicture(String googleId, String newPictureKey)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, newPictureKey);
        
        profilesLogic.updateStudentProfilePicture(googleId, newPictureKey);
    }
    
    /**
     * Deletes both instructor and student privileges.
     * Does not delete courses. Can result in orphan courses
     * (to be rectified in future).
     * Fails silently if no such account. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteAccount(String googleId) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        
        accountsLogic.deleteAccountCascade(googleId);
    }
    
    public void deleteStudentProfilePicture(String googleId) throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        
        profilesLogic.deleteStudentProfilePicture(googleId);
    }
    
    public void deletePicture(BlobKey key) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, key);
        
        profilesLogic.deletePicture(key);
    }
    
    /**
     * Creates an instructor and an new account if the instructor doesn't not have account yet.<br>
     * Used as a shorthand when the account entity is not important and is
     * only needed for completeness<br>
     * <b>Note: Now used for the purpose of testing only.</b><br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    @Deprecated
    public void createInstructorAccount(String googleId, String courseId, String name, String email,
                                        Boolean isArchived, String roleParam, boolean isDisplayedToStudents,
                                        String displayedNameParam, String privileges, String institute)
            throws EntityAlreadyExistsException, InvalidParametersException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, name);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, institute);
        
        if (accountsLogic.getAccount(googleId) == null) {
            AccountAttributes account = new AccountAttributes(googleId, name, true, email, institute);
            accountsLogic.createAccount(account);
        }
        
        String role = roleParam == null ? Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_COOWNER : roleParam;
        String displayedName = displayedNameParam == null ? InstructorAttributes.DEFAULT_DISPLAY_NAME : displayedNameParam;
        InstructorAttributes instructor = null;
        
        if (privileges == null) {
            instructor = new InstructorAttributes(googleId, courseId, name, email, role, isDisplayedToStudents,
                                                  displayedName, new InstructorPrivileges(role));
        } else {
            instructor = new InstructorAttributes(googleId, courseId, name, email, role, displayedName, privileges);
        }
        
        instructor.isArchived = isArchived;
        instructor.isDisplayedToStudents = isDisplayedToStudents;
        instructorsLogic.createInstructor(instructor);
    }
    
    /**
     * Add an instructor for a course. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    @Deprecated
    public void addInstructor(String courseId, String name, String email, String role)
            throws InvalidParametersException, EntityAlreadyExistsException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, name);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        
        InstructorAttributes instructor =
                new InstructorAttributes(null, courseId, name, email, role, role, new InstructorPrivileges(role));

        instructorsLogic.createInstructor(instructor);
    }
    
    public InstructorAttributes createInstructor(InstructorAttributes instructor)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return instructorsLogic.createInstructor(instructor);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     * @param queryString
     * @param cursorString
     * @return Null if no match found.
     */
    public InstructorSearchResultBundle searchInstructorsInWholeSystem(String queryString, String cursorString) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, queryString);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, cursorString);
        
        return instructorsLogic.searchInstructorsInWholeSystem(queryString, cursorString);
    }
    
    /**
     * Create or update document for the given Instructor
     * @param Instructor to be put into documents
     */
    public void putDocument(InstructorAttributes instructor) {
        instructorsLogic.putDocument(instructor);
    }
    
    /**
     * Remove document for the given Instructor
     * @param comment to be removed from documents
     */
    public void deleteDocument(InstructorAttributes instructor) {
        instructorsLogic.deleteDocument(instructor);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        
        return instructorsLogic.getInstructorForEmail(courseId, email);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        return instructorsLogic.getInstructorForGoogleId(courseId, googleId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String encryptedKey) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, encryptedKey);

        return instructorsLogic.getInstructorForRegistrationKey(encryptedKey);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        
        return instructorsLogic.getInstructorsForGoogleId(googleId);
    }
    
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean omitArchived) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        
        return instructorsLogic.getInstructorsForGoogleId(googleId, omitArchived);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForEmail(String email) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        
        return instructorsLogic.getInstructorsForEmail(email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
        return instructorsLogic.getInstructorsForCourse(courseId);
    }
    
    /**
     * Get the encrypted registration key for the instructor.
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws EntityDoesNotExistException
     */
    public String getEncryptedKeyForInstructor(String courseId, String email)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
    
        return instructorsLogic.getEncryptedKeyForInstructor(courseId, email);
    }

    /**
     * @deprecated Not scalable. Don't use unless in admin features.
     */
    @Deprecated
    public List<InstructorAttributes> getAllInstructors() {
        
        return instructorsLogic.getAllInstructors();
    }

    public List<FeedbackSessionAttributes> getAllOpenFeedbackSessions(Date start, Date end, double zone) {
        
        return feedbackSessionsLogic.getAllOpenFeedbackSessions(start, end, zone);
    }

    /**
     * @return true if this user has instructor privileges.
     */
    public boolean isInstructor(String googleId) {
        
        return accountsLogic.isAccountAnInstructor(googleId);
    }

    /**
     * @return true if this user is an instructor of the course
     */
    public boolean isInstructorOfCourse(String googleId, String courseId) {
        
        return instructorsLogic.isGoogleIdOfInstructorOfCourse(googleId, courseId);
    }
    
    /**
     * @return true if this email belongs to an instructor of the course
     */
    public boolean isInstructorEmailOfCourse(String email, String courseId) {
        
        return instructorsLogic.isEmailOfInstructorOfCourse(email, courseId);
    }
    
    /**
     * Returns whether the instructor is a new user, according to one of the following criterias:<br>
     * <ul>
     *     <li>
     *         There is only a sample course (created by system) for the instructor.</li>
     *  </li>
     *  <li>
     *      There is no any course for the instructor.
     *     </li>
     * </ul>
     * 
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @return true if the instructor is a new user
     */
    public boolean isNewInstructor(String googleId) {
        
        return instructorsLogic.isNewInstructor(googleId);
    }

    /**
     * Update the name and email address of an instructor with the specific Google ID.
     * Preconditions: <br>
     * * All parameters are non-null.
     * @param googleId
     * @param instr InstructorAttributes object containing the details to be updated
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public void updateInstructorByGoogleId(String googleId, InstructorAttributes instr)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instr);
            
        instructorsLogic.updateInstructorByGoogleId(googleId, instr);
    }
    
    public void updateInstructorByEmail(String email, InstructorAttributes instr)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instr);
        
        instructorsLogic.updateInstructorByEmail(email, instr);
    }
    
    /**
     * Make the instructor join the course, i.e. associate the Google ID to the instructor.<br>
     * Create an account for the instructor if there is no account exist for him.
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws InvalidParametersException
     */
    public void joinCourseForInstructor(String encryptedKey, String googleId, String institute)
            throws JoinCourseException, InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, encryptedKey);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, institute);
        
        accountsLogic.joinCourseForInstructor(encryptedKey, googleId, institute);
    }
    
    public void joinCourseForInstructor(String encryptedKey, String googleId)
            throws JoinCourseException, InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, encryptedKey);
        accountsLogic.joinCourseForInstructor(encryptedKey, googleId);
    }

    public void verifyInputForAdminHomePage(String shortName, String name, String institute, String email)
            throws InvalidParametersException {
         
        List<String> invalidityInfo = instructorsLogic.getInvalidityInfoForNewInstructorData(shortName, name,
                                                                                              institute, email);
         
        if (!invalidityInfo.isEmpty()) {
            throw new InvalidParametersException(invalidityInfo);
        }
    }

    /**
     * Removes instructor access but does not delete the account.
     * The account will continue to have student access. <br>
     * Fails silently if no match found.<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void downgradeInstructorToStudentCascade(String googleId) {
    
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        
        accountsLogic.downgradeInstructorToStudentCascade(googleId);
    }

    /**
     * Fails silently if no match found.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteInstructor(String courseId, String email) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);

        instructorsLogic.deleteInstructorCascade(courseId, email);
    }

    /**
     * Creates a course and an instructor for it. <br>
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * * {@code instructorGoogleId} already has instructor privileges.
     */
    public void createCourseAndInstructor(String instructorGoogleId, String courseId, String courseName,
                                          String courseTimeZone)
            throws EntityAlreadyExistsException, InvalidParametersException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorGoogleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseTimeZone);

        coursesLogic.createCourseAndInstructor(instructorGoogleId, courseId, courseName, courseTimeZone);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return null if not found.
     */
    public CourseAttributes getCourse(String courseId) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
        return coursesLogic.getCourse(courseId);
    }

    /**
     * Returns a detailed version of course data. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public CourseDetailsBundle getCourseDetails(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return coursesLogic.getCourseDetails(courseId);
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
    public List<CourseAttributes> getCoursesForStudentAccount(String googleId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return coursesLogic.getCoursesForStudentAccount(googleId);
    }

    /**
     * Omits archived courses if omitArchived == true<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return A less detailed version of courses for this instructor without stats
     *   Returns an empty list if none found.
     */
    public HashMap<String, CourseSummaryBundle> getCourseSummariesWithoutStatsForInstructor(String googleId,
                                                                                            boolean omitArchived) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return coursesLogic.getCoursesSummaryWithoutStatsForInstructor(googleId, omitArchived);
    }

    /**
     * Omits archived courses if omitArchived == true<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return A more detailed version of courses for this instructor.
     *   Returns an empty list if none found.\
     */
    public HashMap<String, CourseDetailsBundle> getCourseDetailsListForInstructor(
            String instructorId, boolean omitArchived) throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorId);
        return coursesLogic.getCoursesDetailsListForInstructor(instructorId, omitArchived);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return A less detailed version of courses for this instructor.
     *   Returns an empty list if none found.
     */
    public HashMap<String, CourseDetailsBundle> getCourseSummariesForInstructor(String googleId)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return coursesLogic.getCourseSummariesForInstructor(googleId, false);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return A less detailed version of courses for the specified instructor attributes.
     *   Returns an empty list if none found.
     */
    public HashMap<String, CourseDetailsBundle> getCourseSummariesForInstructors(List<InstructorAttributes> instructorList) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorList);
        return coursesLogic.getCourseSummariesForInstructor(instructorList);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return All archived courses for this instructor.
     */
    public List<CourseAttributes> getArchivedCoursesForInstructor(String googleId) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return coursesLogic.getArchivedCoursesForInstructor(googleId);
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
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
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
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return coursesLogic.getCoursesForInstructor(googleId, omitArchived);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @return Courses the given instructors is in.
     */
    public List<CourseAttributes> getCoursesForInstructor(List<InstructorAttributes> instructorList) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorList);
        return coursesLogic.getCoursesForInstructor(instructorList);
    }
    
    /**
     * Updates the details of a course
     */
    public void updateCourse(CourseAttributes course) throws InvalidParametersException,
                                                             EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, course);
        coursesLogic.updateCourse(course);
    }

    /**
     * Change the archive status of a course for a instructor.<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     * @param courseId The course of which the archive status is to be changed
     * @param archiveStatus The archive status to be set
     */
    
    public void setArchiveStatusOfInstructor(String googleId, String courseId, boolean archiveStatus)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, archiveStatus);
        
        instructorsLogic.setArchiveStatusOfInstructor(googleId, courseId, archiveStatus);
    }
    
    /**
     * Deletes the course and all data related to the course
     * (instructors, students, feedback sessions).
     * Fails silently if no such account. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteCourse(String courseId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        coursesLogic.deleteCourseCascade(courseId);
    }


    /**
     * Creates a student. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void createStudent(StudentAttributes student)
            throws EntityAlreadyExistsException, InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);
        studentsLogic.createStudentCascade(student);
    }

    public void createStudentWithoutDocument(StudentAttributes student) throws EntityAlreadyExistsException,
                                                                               InvalidParametersException,
                                                                               EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);
        studentsLogic.createStudentCascadeWithoutDocument(student);
    }
    
    /**
     * Search for students. Preconditions: all parameters are non-null.
     * @param queryString
     * @param instructors   a list of InstructorAttributes associated to a googleId,
     *                      used for filtering of search result
     * @param cursorString  used to support the pagination
     * @return Null if no match found
     */
    public StudentSearchResultBundle searchStudents(String queryString, List<InstructorAttributes> instructors,
                                                    String cursorString) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, queryString);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructors);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, cursorString);
        return studentsLogic.searchStudents(queryString, instructors, cursorString);
    }
    
    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search students in the whole system.
     * @param queryString
     * @param cursorString
     * @return Null if no match found.
     */
    public StudentSearchResultBundle searchStudentsInWholeSystem(String queryString, String cursorString) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, queryString);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, cursorString);
        
        return studentsLogic.searchStudentsInWholeSystem(queryString, cursorString);
    }
    
    /**
     * Get all students in the database
     * @return empty list if there is no students
     */
    public List<StudentAttributes> getAllStudents() {
        return studentsLogic.getAllStudents();
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForRegistrationKey(String registrationKey) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, registrationKey);
        return studentsLogic.getStudentForRegistrationKey(registrationKey);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForEmail(String courseId, String email) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);

        return studentsLogic.getStudentForEmail(courseId, email);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @return Null if no match found.
     */
    public StudentAttributes getStudentForGoogleId(String courseId, String googleId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
    
        return studentsLogic.getStudentForCourseIdAndGoogleId(courseId, googleId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @return Null if no match found.
     */
    public StudentProfileAttributes getStudentProfile(String googleId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return profilesLogic.getStudentProfile(googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @return Empty list if no match found.
     */
    public List<StudentAttributes> getStudentsForGoogleId(String googleId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return studentsLogic.getStudentsForGoogleId(googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<StudentAttributes> getStudentsForCourse(String courseId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return studentsLogic.getStudentsForCourse(courseId);
    }

     /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<StudentAttributes> getStudentsForSection(String section, String courseId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, section);
        return studentsLogic.getStudentsForSection(section, courseId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return Empty list if none found.
     */
    public List<StudentAttributes> getStudentsForTeam(String team, String courseId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, team);
        return studentsLogic.getStudentsForTeam(team, courseId);
    }

    /** 
     * Preconditions: <br>
     * * All parameters are non-null
     */
    public List<String> getSectionNamesForCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return coursesLogic.getSectionsNameForCourse(courseId);
    }

    /** 
     * Preconditions: <br>
     * * All parameters are non-null
     */
    public List<String> getSectionNamesForCourse(CourseAttributes course) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, course);
        return coursesLogic.getSectionsNameForCourse(course);
    }
    
    /** 
     * Preconditions: <br>
     * * All parameters are non-null
     * @throws EntityDoesNotExistException
     */
    public Map<String, List<String>> getCourseIdToSectionNamesMap(List<CourseAttributes> courses)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courses);
        return coursesLogic.getCourseIdToSectionNamesMap(courses);
    }

    /** 
     * Preconditions: <br>
     * * All parameters are non-null
     */
    public SectionDetailsBundle getSectionForCourse(String section, String courseId)
            throws EntityDoesNotExistException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, section);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
        return coursesLogic.getSectionForCourse(section, courseId);
    }
    
    /** 
     * Preconditions: <br>
     * * All parameters are non-null
     */
    public List<SectionDetailsBundle> getSectionsForCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return coursesLogic.getSectionsForCourseWithoutStats(courseId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<TeamDetailsBundle> getTeamsForCourse(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return coursesLogic.getTeamsForCourse(courseId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public TeamDetailsBundle getTeamDetailsForStudent(StudentAttributes student) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);
        return studentsLogic.getTeamDetailsForStudent(student);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * 
     * @throws EntityDoesNotExistException
     */
    public String getEncryptedKeyForStudent(String courseId, String email) throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
    
        return studentsLogic.getEncryptedKeyForStudent(courseId, email);
    }

    public void resetStudentGoogleId(String originalEmail, String courseId) throws InvalidParametersException,
                                                                                   EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, originalEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        studentsLogic.resetStudentGoogleId(originalEmail, courseId, true);
    }

    /**
     * All attributes except courseId be changed. Trying to change courseId will
     * be treated as trying to edit a student in a different course.<br>
     * Changing team name will not delete existing submissions under that team <br>
     * Cascade logic: Email changed-> changes email in all existing submissions <br>
     * Team changed-> creates new submissions for the new team, deletes
     * submissions for previous team structure. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void updateStudent(String originalEmail, StudentAttributes student)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, originalEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);

        studentsLogic.updateStudentCascade(originalEmail, student);
    }

    public void updateStudentWithoutDocument(String originalEmail, StudentAttributes student)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, originalEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);

        studentsLogic.updateStudentCascadeWithoutDocument(originalEmail, student);
    }

    /**
     * Make the student join the course, i.e. associate the Google ID to the student.<br>
     * Create an account for the student if there is no account exist for him.
     * Preconditions: <br>
     * * All parameters are non-null.
     * @param googleId
     * @param key the encrypted registration key
     */
    public void joinCourseForStudent(String key, String googleId) throws JoinCourseException, InvalidParametersException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, key);
    
        accountsLogic.joinCourseForStudent(key, googleId);
    
    }

    /**
     * Enrolls new students in the course or modifies existing students. But it
     * will not delete any students. It will not edit email address either. If
     * an existing student was enrolled with a different email address, that
     * student will be treated as a new student.<br>
     * If there is an error in the enrollLines, there will be no changes to the
     * datastore <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return StudentData objects in the return value contains the status of
     *         enrollment. It also includes data for other students in the
     *         course that were not touched by the operation.
     * @throws EntityAlreadyExistsException
     */
    public CourseEnrollmentResult enrollStudents(String enrollLines, String courseId)
            throws EnrollException, EntityDoesNotExistException, InvalidParametersException, EntityAlreadyExistsException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, enrollLines);
    
        return studentsLogic.enrollStudents(enrollLines.trim(), courseId);
    
    }

    /**
     * @see {@link StudentsLogic#getUnregisteredStudentsForCourse(String)}
     */
    public List<StudentAttributes> getUnregisteredStudentsForCourse(String courseId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return studentsLogic.getUnregisteredStudentsForCourse(courseId);
    }

    /**
     * @see {@link FeedbackSessionsLogic#isFeedbackSessionCompletedByInstructor(FeedbackSessionAttributes, String)}
     */
    public boolean isFeedbackSessionCompletedByInstructor(FeedbackSessionAttributes fsa, String userEmail)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, fsa);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        return feedbackSessionsLogic.isFeedbackSessionCompletedByInstructor(fsa, userEmail);
    }

    /**
     * @see {@link FeedbackSessionsLogic#isFeedbackSessionCompletedByStudent(FeedbackSessionAttributes, String)}
     */
    public boolean isFeedbackSessionCompletedByStudent(FeedbackSessionAttributes fsa, String userEmail) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, fsa);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        return feedbackSessionsLogic.isFeedbackSessionCompletedByStudent(fsa, userEmail);
    }

    /**
     * Deletes the student from the course including any submissions to/from
     * for this student in this course.
     * Fails silently if no match found. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteStudent(String courseId, String studentEmail) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

        studentsLogic.deleteStudentCascade(courseId, studentEmail);
    }

    public void deleteStudentWithoutDocument(String courseId, String studentEmail) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);

        studentsLogic.deleteStudentCascadeWithoutDocument(courseId, studentEmail);
    }

    /**
     *  Checks if a course has sections for each team
     *  Preconditions: <br>
     *  * All parameters are non-null.
     */
    public boolean hasIndicatedSections(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return coursesLogic.hasIndicatedSections(courseId);
    }

    /**
     * Validates if the list of modified students will not create conflicts for both the sections and teams
     * in a course
     * Preconditions: <br>
     * * All parameters are non-null
     */
    public void validateSectionsAndTeams(List<StudentAttributes> studentList, String courseId) throws EnrollException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentList);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        studentsLogic.validateSectionsAndTeams(studentList, courseId);
    }
    
    /**
     * Validates if the list of modified students will not create conflicts for the teams
     * in a course
     * Preconditions: <br>
     * * All parameters are non-null
     */
    public void validateTeams(List<StudentAttributes> studentList, String courseId) throws EnrollException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentList);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        studentsLogic.validateTeams(studentList, courseId);
    }
    
    public void putDocument(StudentAttributes student) {
        studentsLogic.putDocument(student);
    }
    
    /**
     * Generates students list of a course in CSV format. <br>
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     */
    public String getCourseStudentListAsCsv(String courseId, String googleId) throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        
        return coursesLogic.getCourseStudentListAsCsv(courseId, googleId);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void createFeedbackSession(FeedbackSessionAttributes feedbackSession)
            throws EntityAlreadyExistsException, InvalidParametersException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSession);
        feedbackSessionsLogic.createFeedbackSession(feedbackSession);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionAttributes copyFeedbackSession(String copiedFeedbackSessionName,
                                                         String copiedCourseId,
                                                         String feedbackSessionName,
                                                         String courseId,
                                                         String instructorEmail) throws EntityAlreadyExistsException,
                                                                                        InvalidParametersException,
                                                                                        EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, copiedFeedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, copiedCourseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorEmail);

        return feedbackSessionsLogic.copyFeedbackSession(copiedFeedbackSessionName,
                copiedCourseId, feedbackSessionName, courseId, instructorEmail);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionAttributes getFeedbackSession(String feedbackSessionName, String courseId) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
        return feedbackSessionsLogic.getFeedbackSession(feedbackSessionName, courseId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return feedbackSessionsLogic.getFeedbackSessionsForCourse(courseId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws EntityDoesNotExistException
     */
    public FeedbackSessionDetailsBundle getFeedbackSessionDetails(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
        FeedbackSessionAttributes fsa = feedbackSessionsLogic.getFeedbackSession(feedbackSessionName, courseId);
        
        return feedbackSessionsLogic.getFeedbackSessionDetails(fsa);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * 
     * @return Details of Instructor's feedback sessions. <br>
     * Returns an empty list if none found.
     */
    public List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForInstructor(
            String googleId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return feedbackSessionsLogic.getFeedbackSessionDetailsForInstructor(googleId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * 
     * @return List(without details) of Instructor's feedback sessions. <br>
     * Returns an empty list if none found.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(String googleId) {
        return getFeedbackSessionsListForInstructor(googleId, false);
    }
    
    /**
     * Omits feedback sessions from archived courses if omitArchived == true<br>
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * 
     * @return List(without details) of Instructor's feedback sessions. <br>
     * Returns an empty list if none found.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(String googleId, boolean omitArchived) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return feedbackSessionsLogic.getFeedbackSessionsListForInstructor(googleId, omitArchived);
    }
    
    public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
            List<InstructorAttributes> instructorList) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorList);
        return feedbackSessionsLogic.getFeedbackSessionsListForInstructor(instructorList);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * 
     * @return Feedback session information, question + responses bundle for user <br>
     * Returns an empty list if none found.
     */
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsBundleForInstructor(String feedbackSessionName,
                                                                                         String courseId,
                                                                                         String userEmail)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        
        return feedbackSessionsLogic.getFeedbackSessionQuestionsForInstructor(feedbackSessionName, courseId, userEmail);
    }
    
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsBundleForInstructor(
            String feedbackSessionName, String courseId, String questionId, String userEmail)
                throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        
        return feedbackSessionsLogic
                   .getFeedbackSessionQuestionsForInstructor(feedbackSessionName, courseId, questionId, userEmail);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * 
     * @return Feedback session information, question + responses bundle for user <br>
     * Returns an empty list if none found.
     */
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsBundleForStudent(String feedbackSessionName,
                                                                                      String courseId,
                                                                                      String userEmail)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        
        return feedbackSessionsLogic.getFeedbackSessionQuestionsForStudent(feedbackSessionName, courseId, userEmail);
    }
    
    public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsBundleForStudent(
            String feedbackSessionName, String courseId, String questionId, String userEmail)
                throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        
        return feedbackSessionsLogic
                   .getFeedbackSessionQuestionsForStudent(feedbackSessionName, courseId, questionId, userEmail);
    }

    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackSessionName,
                                                          String courseId,
                                                          int questionNumber) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        return feedbackQuestionsLogic.getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * 
     */
    public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackQuestionId);
        return feedbackQuestionsLogic.getFeedbackQuestion(feedbackQuestionId);
    }

    /**
     * Generates summary results (without comments) in CSV format. <br>
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     */
    public String getFeedbackSessionResultSummaryAsCsv(String courseId,
                                                       String feedbackSessionName,
                                                       String instructorEmail,
                                                       String filterText,
                                                       boolean isMissingResponsesShown,
                                                       boolean isStatsShown)
            throws EntityDoesNotExistException, ExceedingRangeException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        
        return feedbackSessionsLogic.getFeedbackSessionResultsSummaryAsCsv(
                feedbackSessionName, courseId, instructorEmail, filterText, isMissingResponsesShown, isStatsShown);
    }

    /**
     * Generates summary results (without comments) within a section in CSV format. <br>
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     */
    public String getFeedbackSessionResultSummaryInSectionAsCsv(
            String courseId, String feedbackSessionName, String instructorEmail,
            String section, String filterText, boolean isMissingResponsesShown, boolean isStatsShown)
            throws EntityDoesNotExistException, ExceedingRangeException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, section);

        return feedbackSessionsLogic.getFeedbackSessionResultsSummaryInSectionAsCsv(
                feedbackSessionName, courseId, instructorEmail, section,
                filterText, isMissingResponsesShown, isStatsShown);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of viewable feedback sessions for any user in the course.
     */
    public List<FeedbackSessionAttributes> getFeedbackSessionsForUserInCourse(String courseId, String userEmail)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
        return feedbackSessionsLogic.getFeedbackSessionsForUserInCourse(courseId, userEmail);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public boolean hasStudentSubmittedFeedback(FeedbackSessionAttributes fsa, String studentEmail) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, fsa);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, studentEmail);
    
        return feedbackSessionsLogic.isFeedbackSessionCompletedByStudent(fsa, studentEmail);
    }
    
    /**
     * Updates the details of a feedback session <br>
     * Does not affect the questions and responses associated with it.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void updateFeedbackSession(FeedbackSessionAttributes updatedSession) throws InvalidParametersException,
                                                                                       EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, updatedSession);
        feedbackSessionsLogic.updateFeedbackSession(updatedSession);
    }
    
    public void updateRespondents(String feedbackSessionName, String courseId) throws InvalidParametersException,
                                                                                      EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
        feedbackSessionsLogic.updateRespondentsForSession(feedbackSessionName, courseId);
    }

    public void addInstructorRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        feedbackSessionsLogic.addInstructorRespondent(email, feedbackSessionName, courseId);
    }

    public void addStudentRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        feedbackSessionsLogic.addStudentRespondent(email, feedbackSessionName, courseId);
    }

    public void deleteInstructorRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        feedbackSessionsLogic.deleteInstructorRespondent(email, feedbackSessionName, courseId);
    }

    public void deleteStudentRespondent(String email, String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException, InvalidParametersException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, email);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        feedbackSessionsLogic.deleteStudentFromRespondentList(email, feedbackSessionName, courseId);
    }
    
    /**
     * Publishes the feedback session and send email alerts to students.
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * @throws InvalidParametersException
     *             if the feedback session is not ready to be published.
     * @throws EntityDoesNotExistException
     */
    public void publishFeedbackSession(FeedbackSessionAttributes session)
            throws EntityDoesNotExistException, InvalidParametersException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, session);
    
        feedbackSessionsLogic.publishFeedbackSession(session);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null. <br>
     * @throws InvalidParametersException
     *             if the feedback session is not ready to be unpublished.
     */
    public void unpublishFeedbackSession(FeedbackSessionAttributes session)
            throws EntityDoesNotExistException, InvalidParametersException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, session);
    
        feedbackSessionsLogic.unpublishFeedbackSession(session);
    }

    /**
     * Deletes the feedback session but not the questions and
     * responses associated to it.
     * Fails silently if no such feedback session. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteFeedbackSession(String feedbackSessionName, String courseId) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        feedbackSessionsLogic.deleteFeedbackSessionCascade(feedbackSessionName, courseId);
    }
    
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void createFeedbackQuestion(FeedbackQuestionAttributes feedbackQuestion) throws InvalidParametersException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackQuestion);
        feedbackQuestionsLogic.createFeedbackQuestion(feedbackQuestion);
    }
    
    /**
     * Used for creating initial questions for template sessions only.
     * Does not check if feedback session exists.
     * Does not check if question number supplied is valid(does not check for clashes, or make adjustments)
     * Preconditions: <br>
     * * All parameters are non-null.
     * * questionNumber is > 0
     */
    public FeedbackQuestionAttributes createFeedbackQuestionForTemplate(
            FeedbackQuestionAttributes feedbackQuestion, int questionNumber)
            throws InvalidParametersException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackQuestion);
        Assumption.assertTrue(questionNumber > 0);
        return feedbackQuestionsLogic.createFeedbackQuestionNoIntegrityCheck(feedbackQuestion, questionNumber);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackQuestionAttributes copyFeedbackQuestion(String oldCourseId, String oldFeedbackSessionName,
                                                           String feedbackQuestionId, String feedbackSessionName,
                                                           String courseId, String instructorEmail)
            throws InvalidParametersException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, oldFeedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, oldCourseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackQuestionId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorEmail);

        return feedbackQuestionsLogic.copyFeedbackQuestion(oldCourseId, oldFeedbackSessionName, feedbackQuestionId,
                                                           feedbackSessionName, courseId, instructorEmail);
    }
    
    /**
     * Updates the question number of a Feedback Question.<br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void updateFeedbackQuestionNumber(FeedbackQuestionAttributes updatedQuestion)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull(ERROR_NULL_PARAMETER, updatedQuestion);
        feedbackQuestionsLogic.updateFeedbackQuestionNumber(updatedQuestion);
    }
    
    /**
     * Updates the details of a Feedback Question.<br>
     * The FeedbackQuestionAttributes should have the updated attributes
     * together with the original ID of the question. Preserves null
     * attributes.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void updateFeedbackQuestion(FeedbackQuestionAttributes updatedQuestion)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, updatedQuestion);
        feedbackQuestionsLogic.updateFeedbackQuestion(updatedQuestion);
    }

    /**
     * Deletes the feedback question and the responses associated to it. Fails
     * silently if there is no such feedback question. <br>
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteFeedbackQuestion(String questionId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, questionId);
        feedbackQuestionsLogic.deleteFeedbackQuestionCascade(questionId);
    }

    /**
     * Returns true if there is at least one response for the given feedback question,
     * false if not.
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    
    public boolean isQuestionHasResponses(String feedbackQuestionId) {
        return feedbackQuestionsLogic.isQuestionHasResponses(feedbackQuestionId);
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
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, googleId);
        return feedbackQuestionsLogic.getCopiableFeedbackQuestionsForInstructor(googleId);
    }
    
    /**
     * Gets all questions for a feedback session.<br>
     * Returns an empty list if they are no questions
     * for the session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
        return feedbackQuestionsLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
    }

    /**
     * Gets the response rate status for a session
     * Preconditions: <br>
     * * All parameters are non-null
     */
    public FeedbackSessionResponseStatus getFeedbackSessionResponseStatus(String feedbackSessionName, String courseId)
            throws EntityDoesNotExistException {
    
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        
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
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        
        return feedbackSessionsLogic.getFeedbackSessionResultsForStudent(feedbackSessionName, courseId, userEmail);
    }
    
    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the student for a feedback session.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForStudent(String feedbackSessionName,
                                                                            String courseId, String userEmail,
                                                                            CourseRoster roster)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, roster);
        
        return feedbackSessionsLogic.getFeedbackSessionResultsForStudent(feedbackSessionName, courseId,
                                                                         userEmail, roster);
    }
    
    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session within the given range
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, long range, String viewType)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, viewType);
       
        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorWithinRangeFromView(feedbackSessionName,
                                                                                               courseId, userEmail,
                                                                                               range, viewType);
    }
    
    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session in a section within the given range
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
            String feedbackSessionName, String courseId, String userEmail, String section, long range, String viewType)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, viewType);
        
        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorInSectionWithinRangeFromView(
                                        feedbackSessionName, courseId, userEmail, section, range, viewType);
    }
    
    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session from a section within the given range
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromSectionWithinRange(
            String feedbackSessionName, String courseId, String userEmail, String section, long range)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);

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
            String feedbackSessionName, String courseId, String userEmail, String section, long range)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorToSectionWithinRange(
                                        feedbackSessionName, courseId, userEmail, section, range);
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
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
       
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
                                    String questionId, String selectedSection)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
       
        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorFromQuestionInSection(
                                            feedbackSessionName, courseId, userEmail,
                                            questionId, selectedSection);
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
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        
        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructor(feedbackSessionName, courseId, userEmail);
    }
    
    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session of a roster.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructor(String feedbackSessionName,
                                                                               String courseId, String userEmail,
                                                                               CourseRoster roster,
                                                                               Boolean isIncludeResponseStatus)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructor(feedbackSessionName, courseId, userEmail,
                                                                            roster, isIncludeResponseStatus);
    }
    
    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session in a specific section.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorInSection(String feedbackSessionName,
                                                                                        String courseId,
                                                                                        String userEmail,
                                                                                        String section)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, section);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorInSection(feedbackSessionName, courseId,
                                                                                     userEmail, section);
    }
    
    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session in a specific section.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorFromSection(String feedbackSessionName,
                                                                                          String courseId,
                                                                                          String userEmail,
                                                                                          String section)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, section);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorFromSection(feedbackSessionName, courseId,
                                                                                       userEmail, section);
    }
    
    /**
     * Gets a question+response bundle for questions with responses that
     * is visible to the instructor for a feedback session in a specific section.
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructorToSection(String feedbackSessionName,
                                                                                        String courseId,
                                                                                        String userEmail,
                                                                                        String section)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, section);

        return feedbackSessionsLogic.getFeedbackSessionResultsForInstructorToSection(feedbackSessionName, courseId,
                                                                                     userEmail, section);
    }
    
    public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackResponseId);
        return feedbackResponsesLogic.getFeedbackResponse(feedbackResponseId);
    }
    
    public FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId,
                                                          String giverEmail,
                                                          String recipient) {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackQuestionId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, giverEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, recipient);
     
        return feedbackResponsesLogic.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws EntityDoesNotExistException
     */
    public void createFeedbackResponse(FeedbackResponseAttributes feedbackResponse)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackResponse);
        feedbackResponsesLogic.createFeedbackResponse(feedbackResponse);
    }

    public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(String questionId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, questionId);
        return feedbackResponsesLogic.getFeedbackResponsesForQuestion(questionId);
    }

    public boolean hasGiverRespondedForSession(String userEmail, String feedbackSessionName, String courseId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, userEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackSessionName);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);

        return feedbackResponsesLogic.hasGiverRespondedForSession(userEmail, feedbackSessionName, courseId);
    }
    
    public boolean isCourseHasResponses(String courseId) {
        return feedbackResponsesLogic.isCourseHasResponses(courseId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void updateFeedbackResponse(FeedbackResponseAttributes feedbackResponse)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackResponse);
        feedbackResponsesLogic.updateFeedbackResponse(feedbackResponse);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteFeedbackResponse(FeedbackResponseAttributes feedbackResponse) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackResponse);
        feedbackResponsesLogic.deleteFeedbackResponseAndCascade(feedbackResponse);
    }
    
    
    /**
     * Create a feedback response comment, and return the created comment
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws EntityDoesNotExistException
     */
    public FeedbackResponseCommentAttributes createFeedbackResponseComment(
            FeedbackResponseCommentAttributes feedbackResponseComment)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackResponseComment);
        return feedbackResponseCommentsLogic.createFeedbackResponseComment(feedbackResponseComment);
    }
    
    /**
     * @deprecated Not scalable. Don't use unless in admin features.
     */
    @Deprecated
    public List<FeedbackResponseCommentAttributes> getAllFeedbackResponseComments() {
        return feedbackResponseCommentsLogic.getAllFeedbackResponseComments();
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
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(String responseId,
                                                                        String giverEmail,
                                                                        Date creationDate) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, responseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, giverEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, creationDate);

        return feedbackResponseCommentsLogic.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws EntityDoesNotExistException when the course with given courseId doesn't exist
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSendingState(
            String courseId, CommentSendingState state)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return feedbackResponseCommentsLogic.getFeedbackResponseCommentsForSendingState(courseId, state);
    }
    
    /**
     * Create or update document for the given FeedbackResponseComment
     * @param comment to be put into documents
     */
    public void putDocument(FeedbackResponseCommentAttributes comment) {
        feedbackResponseCommentsLogic.putDocument(comment);
    }
    
    /**
     * Remove document for the given FeedbackResponseComment
     * @param comment to be removed from documents
     */
    public void deleteDocument(FeedbackResponseCommentAttributes comment) {
        feedbackResponseCommentsLogic.deleteDocument(comment);
    }
    
    /**
     * Search for FeedbackResponseComment. Preconditions: all parameters are non-null.
     * @param queryString
     * @param instructors   a list of InstructorAttributes associated to a googleId,
     *                      used for filtering of search result
     * @param cursorString  used to support the pagination
     * @return Null if no match found
     */
    public FeedbackResponseCommentSearchResultBundle searchFeedbackResponseComments(String queryString,
                                                                         List<InstructorAttributes> instructors,
                                                                         String cursorString) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, queryString);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructors);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, cursorString);
        return feedbackResponseCommentsLogic.searchFeedbackResponseComments(queryString, instructors, cursorString);
    }
    
    /**
     * Update a feedback response comment and return the updated feedback response comment
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(
            FeedbackResponseCommentAttributes feedbackResponseComment)
            throws EntityDoesNotExistException, InvalidParametersException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackResponseComment);
        return feedbackResponseCommentsLogic.updateFeedbackResponseComment(feedbackResponseComment);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void updateFeedbackResponseCommentsSendingState(String courseId,
                                                           CommentSendingState oldState,
                                                           CommentSendingState newState)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, oldState);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, newState);

        feedbackResponseCommentsLogic.updateFeedbackResponseCommentsSendingState(courseId, oldState, newState);
    }

    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteFeedbackResponseComment(FeedbackResponseCommentAttributes feedbackResponseComment) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, feedbackResponseComment);
        feedbackResponseCommentsLogic.deleteFeedbackResponseComment(feedbackResponseComment);
    }
    
    /**
     * Create a comment, and return the created comment
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws EntityDoesNotExistException
     */
    public CommentAttributes createComment(CommentAttributes comment)
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
    
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, comment);
        return commentsLogic.createComment(comment);
    }
    
    /**
     * @deprecated Not scalable. Don't use unless in admin features.
     */
    @Deprecated
    public List<CommentAttributes> getAllComments() {
        return commentsLogic.getAllComments();
    }
    
    public CommentAttributes getComment(Long commentId) {
        Assumption.assertNotNull(commentId);
        return commentsLogic.getComment(commentId);
    }
    
    /**
     * @see {@link CommentsLogic#getComment(CommentAttributes)}.
     */
    public CommentAttributes getComment(CommentAttributes comment) {
        Assumption.assertNotNull(comment);
        return commentsLogic.getComment(comment);
    }
    
    /**
     * Create or update document for the given Comment
     * @param comment to be put into documents
     */
    public void putDocument(CommentAttributes comment) {
        commentsLogic.putDocument(comment);
    }
    
    /**
     * Remove document for the given Comment
     * @param comment to be removed from the documents
     */
    public void deleteDocument(CommentAttributes comment) {
        commentsLogic.deleteDocument(comment);
    }
    
    /**
     * Search for Comment. Preconditions: all parameters are non-null.
     * @param queryString
     * @param instructors   a list of InstructorAttributes associated to a googleId,
     *                      used for filtering of search result
     * @param cursorString  used to support the pagination
     * @return Null if no match found
     */
    public CommentSearchResultBundle searchComment(String queryString, List<InstructorAttributes> instructors,
                                                   String cursorString) {
        Assumption.assertNotNull(queryString);
        Assumption.assertNotNull(instructors);
        Assumption.assertNotNull(cursorString);
        return commentsLogic.searchComment(queryString, instructors, cursorString);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @throws EntityDoesNotExistException
     */
    public Set<String> getRecipientEmailsForSendingComments(String courseId) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return commentsLogic.getRecipientEmailsForSendingComments(courseId);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void updateCommentsSendingState(String courseId, CommentSendingState oldState, CommentSendingState newState)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, oldState);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, newState);
        commentsLogic.updateCommentsSendingState(courseId, oldState, newState);
    }
    
    /**
     * Update a comment, and return the updated comment
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public CommentAttributes updateComment(CommentAttributes comment)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, comment);
        return commentsLogic.updateComment(comment);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     */
    public void deleteComment(CommentAttributes comment) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, comment);
        commentsLogic.deleteComment(comment);
    }
    
    /**
     * Currently giver is limited to instructors only
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of comments from the giver.
     * @throws EntityDoesNotExistException when the course with given courseId doesn't exist
     */
    public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, giverEmail);
        return commentsLogic.getCommentsForGiver(courseId, giverEmail);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of comments from the giver.
     * @throws EntityDoesNotExistException when the student's course doesn't exist
     */
    public List<CommentAttributes> getCommentsForStudent(StudentAttributes student)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, student);
        return commentsLogic.getCommentsForStudent(student);
    }
    
    /**
     * Currently giver is limited to instructors only
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of comments from the giver that have the specified comment status.
     * @throws EntityDoesNotExistException when the instructor doesn't exist
     */
    public List<CommentAttributes> getCommentsForInstructor(InstructorAttributes instructor)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructor);
        return commentsLogic.getCommentsForInstructor(instructor);
    }
    
    /**
     * Currently giver is limited to instructors only
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of comments from the giver that are drafts.
     */
    public List<CommentAttributes> getCommentDrafts(String giverEmail) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, giverEmail);
        return commentsLogic.getCommentDrafts(giverEmail);
    }
    
    /**
     * Currently receiver is limited to students only
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of comments for the receiver.
     * @throws EntityDoesNotExistException when the course with given courseId doesn't exist
     */
    public List<CommentAttributes> getCommentsForReceiver(String courseId,
            CommentParticipantType recipientType, String receiver) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, recipientType);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, receiver);
        return commentsLogic.getCommentsForReceiver(courseId, recipientType, receiver);
    }
    
    /**
     * Currently receiver is limited to students only
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of comments for the receiver.
     * @throws EntityDoesNotExistException when the course with given courseId doesn't exist
     */
    public List<CommentAttributes> getCommentsForReceiver(String courseId, String giverEmail,
            CommentParticipantType recipientType, String receiver) throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, giverEmail);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, recipientType);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, receiver);
        return commentsLogic.getCommentsForReceiver(courseId, giverEmail, recipientType, receiver);
    }
    
    public List<CommentAttributes> getCommentsForReceiverVisibleToInstructor(
            String courseId, CommentParticipantType recipientType, String receiver, String instructorEmail)
            throws EntityDoesNotExistException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, recipientType);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, receiver);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, instructorEmail);
        return commentsLogic.getCommentsForReceiverVisibleToInstructor(courseId, recipientType, receiver, instructorEmail);
    }
    
    /**
     * Preconditions: <br>
     * * All parameters are non-null.
     * @return a list of comments from the giver.
     * @throws EntityDoesNotExistException when the course with given courseId doesn't exist
     */
    public List<CommentAttributes> getCommentsForSendingState(String courseId, CommentSendingState sendingState)
            throws EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, courseId);
        return commentsLogic.getCommentsForSendingState(courseId, sendingState);
    }
    
    /**
     * This method is not scalable. Not to be used unless for admin features.
     * @return the list of all adminEmails in the database.
     * <br> Empty List if no admin email found
     */
    @SuppressWarnings("deprecation")
    public List<AdminEmailAttributes> getAllAdminEmails() {
        return adminEmailsLogic.getAllAdminEmails();
    }
    
    /**
     * get an admin email by email id
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmailById(String emailId) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, emailId);
        return adminEmailsLogic.getAdminEmailById(emailId);
    }
    
    public Date createAdminEmail(AdminEmailAttributes newAdminEmail) throws InvalidParametersException {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, newAdminEmail);
        return adminEmailsLogic.createAdminEmail(newAdminEmail);
    }
    
    /**
     * Move an admin email to trash bin.<br>
     * After this the attribute isInTrashBin will be set to true
     * @param adminEmailId
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public void moveAdminEmailToTrashBin(String adminEmailId)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, adminEmailId);
        adminEmailsLogic.moveAdminEmailToTrashBin(adminEmailId);
    }
    
    /**
     * Move an admin email out of trash bin.<br>
     * After this the attribute isInTrashBin will be set to false
     * @param adminEmailId
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public void moveAdminEmailOutOfTrashBin(String adminEmailId)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, adminEmailId);
        adminEmailsLogic.moveAdminEmailOutOfTrashBin(adminEmailId);
    }
    
    /**
     * Get all admin emails that have been sent and not in trash bin
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getSentAdminEmails() {
        return adminEmailsLogic.getSentAdminEmails();
    }
    
    /**
     * Get all admin email drafts that have NOT been sent and NOT in trash bin
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailDrafts() {
        return adminEmailsLogic.getAdminEmailDrafts();
    }
    
    /**
     * Get all admin emails that have been moved into trash bin
     * @return empty list if no email found
     */
    public List<AdminEmailAttributes> getAdminEmailsInTrashBin() {
        return adminEmailsLogic.getAdminEmailsInTrashBin();
    }
    
    /**
     * Update an admin email by email id
     * @param newAdminEmail
     * @param emailId
     * @throws InvalidParametersException
     * @throws EntityDoesNotExistException
     */
    public void updateAdminEmailById(AdminEmailAttributes newAdminEmail, String emailId)
            throws InvalidParametersException, EntityDoesNotExistException {
        
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, emailId);
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, newAdminEmail);
        
        adminEmailsLogic.updateAdminEmailById(newAdminEmail, emailId);
    }
    
    /**
     * get an admin email by subject and createDate
     * @return null if no matched email found
     */
    public AdminEmailAttributes getAdminEmail(String subject, Date createDate) {
        Assumption.assertNotNull(subject);
        Assumption.assertNotNull(createDate);
        
        return adminEmailsLogic.getAdminEmail(subject, createDate);
    }
    
    /**
     * deletes all emails in trash bin
     */
    public void deleteAllEmailsInTrashBin() {
        adminEmailsLogic.deleteAllEmailsInTrashBin();
    }
    
    /**
     * deletes files uploaded in admin email compose page
     * @param key, the GCS blobkey used to fetch the file in Google Cloud Storage
     */
    public void deleteAdminEmailUploadedFile(BlobKey key) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, key);
        adminEmailsLogic.deleteAdminEmailUploadedFile(key);
    }

    /**
     * Deletes uploaded file
     * @param key the GCS blobkey used to fetch the file in Google Cloud Storage
     */
    public void deleteUploadedFile(BlobKey key) {
        Assumption.assertNotNull(ERROR_NULL_PARAMETER, key);
        GoogleCloudStorageHelper.deleteFile(key);
    }

    public List<String> getArchivedCourseIds(List<CourseAttributes> allCourses,
                                             Map<String, InstructorAttributes> instructorsForCourses) {
        Assumption.assertNotNull(allCourses);
        Assumption.assertNotNull(instructorsForCourses);
        return coursesLogic.getArchivedCourseIds(allCourses, instructorsForCourses);
    }
    
}
