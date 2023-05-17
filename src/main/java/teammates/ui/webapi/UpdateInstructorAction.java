package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InstructorUpdateException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Edits an instructor in a course.
 */
public class UpdateInstructorAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (isCourseMigrated(courseId)) {
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            gateKeeper.verifyAccessible(
                    instructor, sqlLogic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
        } else {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
            gateKeeper.verifyAccessible(
                    instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
        }
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorCreateRequest instructorRequest = getAndValidateRequestBody(InstructorCreateRequest.class);

        if (!isCourseMigrated(courseId)) {
            return executeWithDatastore(courseId, instructorRequest);
        }

        Instructor updatedInstructor;
        try {
            updatedInstructor = sqlLogic.updateInstructorCascade(courseId, instructorRequest);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (InstructorUpdateException e) {
            throw new InvalidOperationException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }

        sqlLogic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, updatedInstructor);

        InstructorData newInstructorData = new InstructorData(updatedInstructor);
        newInstructorData.setGoogleId(updatedInstructor.getGoogleId());

        taskQueuer.scheduleInstructorForSearchIndexing(updatedInstructor.getCourseId(), updatedInstructor.getEmail());

        return new JsonResult(newInstructorData);
    }

    private JsonResult executeWithDatastore(String courseId, InstructorCreateRequest instructorRequest)
            throws InvalidHttpRequestBodyException, InvalidOperationException {
        InstructorAttributes instructorToEdit =
                retrieveEditedInstructor(courseId, instructorRequest.getId(),
                        instructorRequest.getName(), instructorRequest.getEmail(),
                        instructorRequest.getRoleName(), instructorRequest.getIsDisplayedToStudent(),
                        instructorRequest.getDisplayName());

        logic.updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);

        try {
            InstructorAttributes updatedInstructor;
            if (instructorRequest.getId() == null) {
                updatedInstructor = logic.updateInstructor(
                        InstructorAttributes
                                .updateOptionsWithEmailBuilder(instructorToEdit.getCourseId(), instructorRequest.getEmail())
                                .withName(instructorToEdit.getName())
                                .withDisplayedName(instructorToEdit.getDisplayedName())
                                .withIsDisplayedToStudents(instructorToEdit.isDisplayedToStudents())
                                .withRole(instructorToEdit.getRole())
                                .withPrivileges(instructorToEdit.getPrivileges())
                                .build());
            } else {
                updatedInstructor = logic.updateInstructorCascade(
                        InstructorAttributes
                                .updateOptionsWithGoogleIdBuilder(instructorToEdit.getCourseId(), instructorRequest.getId())
                                .withEmail(instructorToEdit.getEmail())
                                .withName(instructorToEdit.getName())
                                .withDisplayedName(instructorToEdit.getDisplayedName())
                                .withIsDisplayedToStudents(instructorToEdit.isDisplayedToStudents())
                                .withRole(instructorToEdit.getRole())
                                .withPrivileges(instructorToEdit.getPrivileges())
                                .build());
            }
            InstructorData newInstructorData = new InstructorData(updatedInstructor);
            newInstructorData.setGoogleId(updatedInstructor.getGoogleId());

            taskQueuer.scheduleInstructorForSearchIndexing(updatedInstructor.getCourseId(), updatedInstructor.getEmail());

            return new JsonResult(newInstructorData);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        } catch (InstructorUpdateException e) {
            throw new InvalidOperationException(e);
        } catch (EntityDoesNotExistException ednee) {
            throw new EntityNotFoundException(ednee);
        }
    }

    /**
     * Creates a new Instructor based on given information.
     * This consists of everything apart from custom privileges.
     *
     * @param courseId              Id of the course the instructor is being added to.
     * @param instructorId          Id of the instructor.
     * @param instructorName        Name of the instructor.
     * @param instructorEmail       Email of the instructor.
     * @param instructorRole        Role of the instructor.
     * @param isDisplayedToStudents Whether the instructor should be visible to students.
     * @param displayedName         Name to be visible to students.
     *                                  Should not be {@code null} even if {@code isDisplayedToStudents} is false.
     * @return The edited instructor with updated basic info
     */
    private InstructorAttributes retrieveEditedInstructor(String courseId, String instructorId, String instructorName,
                                                          String instructorEmail, String instructorRole,
                                                          boolean isDisplayedToStudents, String displayedName) {
        InstructorAttributes instructorToEdit;
        if (instructorId == null) {
            instructorToEdit = logic.getInstructorForEmail(courseId, instructorEmail);
        } else {
            instructorToEdit = logic.getInstructorForGoogleId(courseId, instructorId);
        }

        String newDisplayedName = displayedName;
        if (newDisplayedName == null || newDisplayedName.isEmpty()) {
            newDisplayedName = Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR;
        }

        instructorToEdit.setName(SanitizationHelper.sanitizeName(instructorName));
        instructorToEdit.setEmail(SanitizationHelper.sanitizeEmail(instructorEmail));
        instructorToEdit.setRole(SanitizationHelper.sanitizeName(instructorRole));
        instructorToEdit.setPrivileges(new InstructorPrivileges(instructorToEdit.getRole()));
        instructorToEdit.setDisplayedName(SanitizationHelper.sanitizeName(newDisplayedName));
        instructorToEdit.setDisplayedToStudents(isDisplayedToStudents);

        return instructorToEdit;
    }

}
