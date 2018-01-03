package teammates.logic.core;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.InstructorSearchResultBundle;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.FieldValidator;
import teammates.common.util.Logger;
import teammates.common.util.StringHelper;
import teammates.storage.api.InstructorsDb;

/**
 * Handles operations related to instructors.
 *
 * @see InstructorAttributes
 * @see InstructorsDb
 */
public final class InstructorsLogic {

    private static final Logger log = Logger.getLogger();

    private static InstructorsLogic instance = new InstructorsLogic();

    private static final InstructorsDb instructorsDb = new InstructorsDb();

    private static final AccountsLogic accountsLogic = AccountsLogic.inst();
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();

    private InstructorsLogic() {
        // prevent initialization
    }

    public static InstructorsLogic inst() {
        return instance;
    }

    /* ====================================
     * methods related to google search API
     * ====================================
     */

    /**
     * Creates or updates document for the given Instructor.
     * @param instructor to be put into documents
     */
    public void putDocument(InstructorAttributes instructor) {
        instructorsDb.putDocument(instructor);
    }

    /**
     * Batch creates or updates documents for the given Instructors.
     * @param instructors a list of instructors to be put into documents
     */
    public void putDocuments(List<InstructorAttributes> instructors) {
        instructorsDb.putDocuments(instructors);
    }

    /**
     * Removes document for the given Instructor.
     * @param instructor to be removed from documents
     */
    public void deleteDocument(InstructorAttributes instructor) {
        instructorsDb.deleteDocument(instructor);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     * @return null if no result found
     */
    public InstructorSearchResultBundle searchInstructorsInWholeSystem(String queryString) {
        return instructorsDb.searchInstructorsInWholeSystem(queryString);
    }

    /* ====================================
     * ====================================
     */

    public InstructorAttributes createInstructor(InstructorAttributes instructorToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {

        Assumption.assertNotNull("Supplied parameter was null", instructorToAdd);

        log.info("going to create instructor :\n" + instructorToAdd.toString());

        return instructorsDb.createInstructor(instructorToAdd);
    }

    public void setArchiveStatusOfInstructor(String googleId, String courseId, boolean archiveStatus)
            throws InvalidParametersException, EntityDoesNotExistException {

        InstructorAttributes instructor = instructorsDb.getInstructorForGoogleId(courseId, googleId);
        instructor.isArchived = archiveStatus;
        instructorsDb.updateInstructorByGoogleId(instructor);
    }

    public InstructorAttributes getInstructorForEmail(String courseId, String email) {

        return instructorsDb.getInstructorForEmail(courseId, email);
    }

    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {

        return instructorsDb.getInstructorForGoogleId(courseId, googleId);
    }

    public InstructorAttributes getInstructorForRegistrationKey(String encryptedKey) {

        return instructorsDb.getInstructorForRegistrationKey(encryptedKey);
    }

    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        List<InstructorAttributes> instructorReturnList = instructorsDb.getInstructorsForCourse(courseId);
        instructorReturnList.sort(InstructorAttributes.compareByName);

        return instructorReturnList;
    }

    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {

        return getInstructorsForGoogleId(googleId, false);
    }

    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean omitArchived) {

        return instructorsDb.getInstructorsForGoogleId(googleId, omitArchived);
    }

    public String getEncryptedKeyForInstructor(String courseId, String email)
            throws EntityDoesNotExistException {

        verifyIsEmailOfInstructorOfCourse(email, courseId);

        InstructorAttributes instructor = getInstructorForEmail(courseId, email);

        return StringHelper.encrypt(instructor.key);
    }

    /**
     * Gets all instructors in the Datastore.
     *
     * @deprecated Not scalable. Use only for admin features.
     */
    @Deprecated
    public List<InstructorAttributes> getAllInstructors() {

        return instructorsDb.getAllInstructors();
    }

    public boolean isGoogleIdOfInstructorOfCourse(String instructorId, String courseId) {

        return instructorsDb.getInstructorForGoogleId(courseId, instructorId) != null;
    }

    public boolean isEmailOfInstructorOfCourse(String instructorEmail, String courseId) {

        return instructorsDb.getInstructorForEmail(courseId, instructorEmail) != null;
    }

    /**
     * Returns whether the instructor is a new user, according to one of the following criteria:
     * <ul>
     * <li>There is only a sample course (created by system) for the instructor.</li>
     * <li>There is no any course for the instructor.</li>
     * </ul>
     */
    public boolean isNewInstructor(String googleId) {
        List<InstructorAttributes> instructorList = getInstructorsForGoogleId(googleId);
        return instructorList.isEmpty()
               || instructorList.size() == 1 && coursesLogic.isSampleCourse(instructorList.get(0).courseId);
    }

    public void verifyInstructorExists(String instructorId)
            throws EntityDoesNotExistException {

        if (!accountsLogic.isAccountAnInstructor(instructorId)) {
            throw new EntityDoesNotExistException("Instructor does not exist :"
                    + instructorId);
        }
    }

    public void verifyIsEmailOfInstructorOfCourse(String instructorEmail, String courseId)
            throws EntityDoesNotExistException {

        if (!isEmailOfInstructorOfCourse(instructorEmail, courseId)) {
            throw new EntityDoesNotExistException("Instructor " + instructorEmail
                    + " does not belong to course " + courseId);
        }
    }

    /**
     * Update the name and email address of an instructor with the specific Google ID.
     * @param instructor InstructorAttributes object containing the details to be updated
     */
    public void updateInstructorByGoogleId(String googleId, InstructorAttributes instructor)
            throws InvalidParametersException, EntityDoesNotExistException {

        // TODO: either refactor this to constant or just remove it. check not null should be in db
        Assumption.assertNotNull("Supplied parameter was null", instructor);

        coursesLogic.verifyCourseIsPresent(instructor.courseId);
        verifyInstructorInDbAndCascadeEmailChange(googleId, instructor);
        checkForUpdatingRespondents(instructor);

        instructorsDb.updateInstructorByGoogleId(instructor);
    }

    private void checkForUpdatingRespondents(InstructorAttributes instructor)
            throws InvalidParametersException, EntityDoesNotExistException {

        InstructorAttributes currentInstructor = getInstructorForGoogleId(instructor.courseId, instructor.googleId);
        if (!currentInstructor.email.equals(instructor.email)) {
            fsLogic.updateRespondentsForInstructor(currentInstructor.email, instructor.email, instructor.courseId);
        }
    }

    private void verifyInstructorInDbAndCascadeEmailChange(String googleId,
            InstructorAttributes instructor) throws EntityDoesNotExistException {
        InstructorAttributes instructorInDb = instructorsDb.getInstructorForGoogleId(instructor.courseId, googleId);
        if (instructorInDb == null) {
            throw new EntityDoesNotExistException("Instructor " + googleId
                    + " does not belong to course " + instructor.courseId);
        }
        // cascade comments
        if (!instructorInDb.email.equals(instructor.email)) {
            frcLogic.updateFeedbackResponseCommentsEmails(
                    instructor.courseId, instructorInDb.email, instructor.email);
        }
    }

    /**
     * Update the Google ID and name of an instructor with the specific email.
     * @param instructor InstructorAttributes object containing the details to be updated
     */
    public void updateInstructorByEmail(String email, InstructorAttributes instructor)
            throws InvalidParametersException, EntityDoesNotExistException {

        Assumption.assertNotNull("Supplied parameter was null", instructor);

        coursesLogic.verifyCourseIsPresent(instructor.courseId);
        verifyIsEmailOfInstructorOfCourse(email, instructor.courseId);

        instructorsDb.updateInstructorByEmail(instructor);
    }

    public List<String> getInvalidityInfoForNewInstructorData(String shortName, String name,
                                                              String institute, String email) {

        FieldValidator validator = new FieldValidator();
        List<String> errors = new ArrayList<>();
        String error;

        error = validator.getInvalidityInfoForPersonName(shortName);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getInvalidityInfoForPersonName(name);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getInvalidityInfoForEmail(email);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        error = validator.getInvalidityInfoForInstituteName(institute);
        if (!error.isEmpty()) {
            errors.add(error);
        }

        //No validation for isInstructor and createdAt fields.
        return errors;
    }

    public void deleteInstructorCascade(String courseId, String email) {
        fsLogic.deleteInstructorFromRespondentsList(getInstructorForEmail(courseId, email));
        instructorsDb.deleteInstructor(courseId, email);
    }

    public void deleteInstructorsForGoogleIdAndCascade(String googleId) {
        List<InstructorAttributes> instructors = instructorsDb.getInstructorsForGoogleId(googleId, false);

        //Cascade delete instructors
        for (InstructorAttributes instructor : instructors) {
            deleteInstructorCascade(instructor.courseId, instructor.email);
        }
    }

    // this method is only being used in course logic. cascade to comments is therefore not necessary
    // as it it taken care of when deleting course
    public void deleteInstructorsForCourse(String courseId) {

        instructorsDb.deleteInstructorsForCourse(courseId);
    }

    public List<InstructorAttributes> getCoOwnersForCourse(String courseId) {
        List<InstructorAttributes> instructors = getInstructorsForCourse(courseId);
        List<InstructorAttributes> instructorsWithCoOwnerPrivileges = new ArrayList<>();
        for (InstructorAttributes instructor : instructors) {
            if (!instructor.hasCoownerPrivileges()) {
                continue;
            }
            instructorsWithCoOwnerPrivileges.add(instructor);
        }
        return instructorsWithCoOwnerPrivileges;
    }

}
