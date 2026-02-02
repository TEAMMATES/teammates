package teammates.logic.core;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.SearchServiceException;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.storage.api.InstructorsDb;

/**
 * Handles operations related to instructors.
 *
 * @see InstructorAttributes
 * @see InstructorsDb
 */
public final class InstructorsLogic {

    private static final Logger log = Logger.getLogger();

    private static final InstructorsLogic instance = new InstructorsLogic();

    private final InstructorsDb instructorsDb = InstructorsDb.inst();

    private FeedbackResponsesLogic frLogic;
    private FeedbackResponseCommentsLogic frcLogic;
    private FeedbackQuestionsLogic fqLogic;
    private FeedbackSessionsLogic fsLogic;
    private DeadlineExtensionsLogic deLogic;

    private InstructorsLogic() {
        // prevent initialization
    }

    public static InstructorsLogic inst() {
        return instance;
    }

    void initLogicDependencies() {
        fqLogic = FeedbackQuestionsLogic.inst();
        frLogic = FeedbackResponsesLogic.inst();
        frcLogic = FeedbackResponseCommentsLogic.inst();
        fsLogic = FeedbackSessionsLogic.inst();
        deLogic = DeadlineExtensionsLogic.inst();
    }

    /**
     * Creates or updates search document for the given instructor.
     *
     * @param instructor the instructor to be put into documents
     */
    public void putDocument(InstructorAttributes instructor) throws SearchServiceException {
        instructorsDb.putDocument(instructor);
    }

    /**
     * This method should be used by admin only since the searching does not restrict the
     * visibility according to the logged-in user's google ID. This is used by admin to
     * search instructors in the whole system.
     * @return null if no result found
     */
    public List<InstructorAttributes> searchInstructorsInWholeSystem(String queryString)
            throws SearchServiceException {
        return instructorsDb.searchInstructorsInWholeSystem(queryString);
    }

    /**
     * Creates an instructor.
     *
     * @return the created instructor
     * @throws InvalidParametersException if the instructor is not valid
     * @throws EntityAlreadyExistsException if the instructor already exists in the database
     */
    public InstructorAttributes createInstructor(InstructorAttributes instructorToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return instructorsDb.createEntity(instructorToAdd);
    }

    /**
     * Sets the archive status of an instructor (i.e. whether the instructor
     * decides to archive the associated course or not).
     */
    public void setArchiveStatusOfInstructor(String googleId, String courseId, boolean archiveStatus)
            throws InvalidParametersException, EntityDoesNotExistException {
        instructorsDb.updateInstructorByGoogleId(
                InstructorAttributes.updateOptionsWithGoogleIdBuilder(courseId, googleId)
                        .withIsArchived(archiveStatus)
                        .build()
        );
    }

    /**
     * Checks if all the given instructors exist in the given course.
     *
     * @throws EntityDoesNotExistException If some instructor does not exist in the course.
     */
    public void verifyAllInstructorsExistInCourse(String courseId, Collection<String> instructorEmailAddresses)
            throws EntityDoesNotExistException {
        boolean hasOnlyExistingInstructors = instructorsDb
                .hasExistingInstructorsInCourse(courseId, instructorEmailAddresses);
        if (!hasOnlyExistingInstructors) {
            throw new EntityDoesNotExistException("There are instructors that do not exist in the course.");
        }
    }

    /**
     * Gets an instructor by unique constraint courseId-email.
     */
    public InstructorAttributes getInstructorForEmail(String courseId, String email) {
        return instructorsDb.getInstructorForEmail(courseId, email);
    }

    /**
     * Gets an instructor by unique ID.
     */
    public InstructorAttributes getInstructorById(String courseId, String email) {
        return instructorsDb.getInstructorById(courseId, email);
    }

    /**
     * Gets an instructor by unique constraint courseId-googleId.
     */
    public InstructorAttributes getInstructorForGoogleId(String courseId, String googleId) {
        return instructorsDb.getInstructorForGoogleId(courseId, googleId);
    }

    /**
     * Gets an instructor by unique constraint registrationKey.
     */
    public InstructorAttributes getInstructorForRegistrationKey(String registrationKey) {
        return instructorsDb.getInstructorForRegistrationKey(registrationKey);
    }

    /**
     * Gets emails of all instructors of a course.
     */
    public List<String> getInstructorEmailsForCourse(String courseId) {
        List<String> instructorEmails = instructorsDb.getInstructorEmailsForCourse(courseId);
        instructorEmails.sort(Comparator.naturalOrder());

        return instructorEmails;
    }

    /**
     * Gets all instructors of a course.
     */
    public List<InstructorAttributes> getInstructorsForCourse(String courseId) {
        List<InstructorAttributes> instructorReturnList = instructorsDb.getInstructorsForCourse(courseId);
        InstructorAttributes.sortByName(instructorReturnList);

        return instructorReturnList;
    }

    /**
     * Gets all non-archived instructors associated with a googleId.
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId) {
        return getInstructorsForGoogleId(googleId, false);
    }

    /**
     * Gets all instructors associated with a googleId.
     *
     * @param omitArchived whether archived instructors should be omitted or not
     */
    public List<InstructorAttributes> getInstructorsForGoogleId(String googleId, boolean omitArchived) {
        return instructorsDb.getInstructorsForGoogleId(googleId, omitArchived);
    }

    /**
     * Verifies that at least one instructor is displayed to student.
     *
     * @throws InstructorUpdateException if there is no instructor displayed to student.
     */
    void verifyAtLeastOneInstructorIsDisplayed(String courseId, boolean isOriginalInstructorDisplayed,
                                               boolean isEditedInstructorDisplayed)
            throws InstructorUpdateException {
        List<InstructorAttributes> instructorsDisplayed = instructorsDb.getInstructorsDisplayedToStudents(courseId);
        boolean isEditedInstructorChangedToNonVisible = isOriginalInstructorDisplayed && !isEditedInstructorDisplayed;
        boolean isNoInstructorMadeVisible = instructorsDisplayed.isEmpty() && !isEditedInstructorDisplayed;

        if (isNoInstructorMadeVisible || instructorsDisplayed.size() == 1 && isEditedInstructorChangedToNonVisible) {
            throw new InstructorUpdateException("At least one instructor must be displayed to students");
        }
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithGoogleId}.
     *
     * <p>Cascade update the comments, responses and deadline extensions associated with the instructor.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorByGoogleIdCascade(
            InstructorAttributes.UpdateOptionsWithGoogleId updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {

        InstructorAttributes originalInstructor =
                instructorsDb.getInstructorForGoogleId(updateOptions.getCourseId(), updateOptions.getGoogleId());

        if (originalInstructor == null) {
            throw new EntityDoesNotExistException("Trying to update non-existent Entity: " + updateOptions);
        }

        InstructorAttributes newInstructor = originalInstructor.getCopy();
        newInstructor.update(updateOptions);

        boolean isOriginalInstructorDisplayed = originalInstructor.isDisplayedToStudents();
        verifyAtLeastOneInstructorIsDisplayed(originalInstructor.getCourseId(), isOriginalInstructorDisplayed,
                newInstructor.isDisplayedToStudents());

        InstructorAttributes updatedInstructor = instructorsDb.updateInstructorByGoogleId(updateOptions);

        if (!originalInstructor.getEmail().equals(updatedInstructor.getEmail())) {
            // cascade responses
            List<FeedbackResponseAttributes> responsesFromUser =
                    frLogic.getFeedbackResponsesFromGiverForCourse(
                            originalInstructor.getCourseId(), originalInstructor.getEmail());
            for (FeedbackResponseAttributes responseFromUser : responsesFromUser) {
                FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(responseFromUser.getFeedbackQuestionId());
                if (question.getGiverType() == FeedbackParticipantType.INSTRUCTORS
                        || question.getGiverType() == FeedbackParticipantType.SELF) {
                    try {
                        frLogic.updateFeedbackResponseCascade(
                                FeedbackResponseAttributes.updateOptionsBuilder(responseFromUser.getId())
                                        .withGiver(updatedInstructor.getEmail())
                                        .build());
                    } catch (EntityAlreadyExistsException e) {
                        log.severe("Fail to adjust 'from' responses when updating instructor: " + e.getMessage());
                    }
                }
            }
            List<FeedbackResponseAttributes> responsesToUser =
                    frLogic.getFeedbackResponsesForReceiverForCourse(
                            originalInstructor.getCourseId(), originalInstructor.getEmail());
            for (FeedbackResponseAttributes responseToUser : responsesToUser) {
                FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(responseToUser.getFeedbackQuestionId());
                if (question.getRecipientType() == FeedbackParticipantType.INSTRUCTORS
                        || question.getGiverType() == FeedbackParticipantType.INSTRUCTORS
                        && question.getRecipientType() == FeedbackParticipantType.SELF) {
                    try {
                        frLogic.updateFeedbackResponseCascade(
                                FeedbackResponseAttributes.updateOptionsBuilder(responseToUser.getId())
                                        .withRecipient(updatedInstructor.getEmail())
                                        .build());
                    } catch (EntityAlreadyExistsException e) {
                        log.severe("Fail to adjust 'to' responses when updating instructor: " + e.getMessage());
                    }
                }
            }
            // cascade comments
            frcLogic.updateFeedbackResponseCommentsEmails(
                    updatedInstructor.getCourseId(), originalInstructor.getEmail(), updatedInstructor.getEmail());
            // cascade deadline extensions
            fsLogic.updateFeedbackSessionsInstructorDeadlinesWithNewEmail(updatedInstructor.getCourseId(),
                    originalInstructor.getEmail(), updatedInstructor.getEmail());
            deLogic.updateDeadlineExtensionsWithNewEmail(updatedInstructor.getCourseId(),
                    originalInstructor.getEmail(), updatedInstructor.getEmail(), true);
        }

        return updatedInstructor;
    }

    /**
     * Updates an instructor by {@link InstructorAttributes.UpdateOptionsWithEmail}.
     *
     * @return updated instructor
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the instructor cannot be found
     */
    public InstructorAttributes updateInstructorByEmail(InstructorAttributes.UpdateOptionsWithEmail updateOptions)
            throws InstructorUpdateException, InvalidParametersException, EntityDoesNotExistException {
        assert updateOptions != null;

        InstructorAttributes originalInstructor =
                instructorsDb.getInstructorForEmail(updateOptions.getCourseId(), updateOptions.getEmail());

        if (originalInstructor == null) {
            throw new EntityDoesNotExistException("Trying to update non-existent Entity: " + updateOptions);
        }

        InstructorAttributes newInstructor = originalInstructor.getCopy();
        newInstructor.update(updateOptions);

        boolean isOriginalInstructorDisplayed = originalInstructor.isDisplayedToStudents();
        verifyAtLeastOneInstructorIsDisplayed(originalInstructor.getCourseId(), isOriginalInstructorDisplayed,
                newInstructor.isDisplayedToStudents());

        return instructorsDb.updateInstructorByEmail(updateOptions);
    }

    /**
     * Deletes instructors using {@link AttributesDeletionQuery}.
     */
    public void deleteInstructors(AttributesDeletionQuery query) {
        instructorsDb.deleteInstructors(query);
    }

    /**
     * Deletes an instructor cascade its associated feedback responses, deadline extensions and comments.
     *
     * <p>Fails silently if the student does not exist.
     */
    public void deleteInstructorCascade(String courseId, String email) {
        InstructorAttributes instructorAttributes = getInstructorForEmail(courseId, email);
        if (instructorAttributes == null) {
            return;
        }

        frLogic.deleteFeedbackResponsesInvolvedEntityOfCourseCascade(courseId, email);
        instructorsDb.deleteInstructor(courseId, email);
        fsLogic.deleteFeedbackSessionsDeadlinesForInstructor(courseId, email);
        deLogic.deleteDeadlineExtensions(courseId, email, true);
    }

    /**
     * Deletes all instructors associated with a googleId and cascade delete its associated feedback responses,
     * deadline extensions and comments.
     */
    public void deleteInstructorsForGoogleIdCascade(String googleId) {
        List<InstructorAttributes> instructors = instructorsDb.getInstructorsForGoogleId(googleId, false);

        // cascade delete instructors
        for (InstructorAttributes instructor : instructors) {
            deleteInstructorCascade(instructor.getCourseId(), instructor.getEmail());
        }
    }

    /**
     * Gets the list of instructors with co-owner privileges in a course.
     */
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

    /**
     * Resets the associated googleId of an instructor.
     */
    public void resetInstructorGoogleId(String originalEmail, String courseId) throws EntityDoesNotExistException {
        try {
            instructorsDb.updateInstructorByEmail(
                    InstructorAttributes.updateOptionsWithEmailBuilder(courseId, originalEmail)
                            .withGoogleId(null)
                            .build());
        } catch (InvalidParametersException e) {
            assert false : "Unexpected invalid parameter.";
        }
    }

    /**
     * Checks if there are any other registered instructors that can modify instructors.
     * If there are none, the instructor currently being edited will be granted the privilege
     * of modifying instructors automatically.
     *
     * @param courseId         Id of the course.
     * @param instructorToEdit Instructor that will be edited.
     *                         This may be modified within the method.
     */
    public void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {
        List<InstructorAttributes> instructors = getInstructorsForCourse(courseId);
        int numOfInstrCanModifyInstructor = 0;
        InstructorAttributes instrWithModifyInstructorPrivilege = null;
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)) {
                numOfInstrCanModifyInstructor++;
                instrWithModifyInstructorPrivilege = instructor;
            }
        }
        boolean isLastRegInstructorWithPrivilege = numOfInstrCanModifyInstructor <= 1
                && instrWithModifyInstructorPrivilege != null
                && (!instrWithModifyInstructorPrivilege.isRegistered()
                || instrWithModifyInstructorPrivilege.getGoogleId()
                .equals(instructorToEdit.getGoogleId()));
        if (isLastRegInstructorWithPrivilege) {
            instructorToEdit.getPrivileges().updatePrivilege(Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR, true);
        }
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

        InstructorAttributes originalInstructor = instructorsDb.getInstructorForEmail(courseId, email);
        if (originalInstructor == null) {
            String errorMessage = String.format(
                    "The instructor with the email %s could not be found for the course with ID [%s].", email, courseId);
            throw new EntityDoesNotExistException(errorMessage);
        }

        return instructorsDb.regenerateEntityKey(originalInstructor);
    }

    /**
     * Returns true if the user associated with the googleId is an instructor in any course in the system.
     */
    public boolean isInstructorInAnyCourse(String googleId) {
        return instructorsDb.hasInstructorsForGoogleId(googleId);
    }

    /**
     * Gets the number of instructors created within a specified time range.
     */
    int getNumInstructorsByTimeRange(Instant startTime, Instant endTime) {
        return instructorsDb.getNumInstructorsByTimeRange(startTime, endTime);
    }

}
