package teammates.ui.webapi;

import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;

/**
 * Edits an instructor in a course.
 */
public class UpdateInstructorAction extends UpdateInstructorPrivilegesAbstractAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(instructor, logic.getCourse(courseId),
                Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
    }

    @Override
    public ActionResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorCreateRequest instructorRequest = getAndValidateRequestBody(InstructorCreateRequest.class);
        InstructorAttributes instructorToEdit = extractUpdatedInstructor(courseId, instructorRequest);
        updateToEnsureValidityOfInstructorsForTheCourse(courseId, instructorToEdit);

        try {
            InstructorAttributes updatedInstructor;
            if (instructorRequest.getId() == null) {
                updatedInstructor = logic.updateInstructor(
                        InstructorAttributes
                                .updateOptionsWithEmailBuilder(instructorToEdit.courseId, instructorRequest.getEmail())
                                .withName(instructorToEdit.name)
                                .withDisplayedName(instructorToEdit.displayedName)
                                .withIsDisplayedToStudents(instructorToEdit.isDisplayedToStudents)
                                .withPrivileges(instructorToEdit.privileges)
                                .withRole(instructorToEdit.role)
                                .build());
            } else {
                updatedInstructor = logic.updateInstructorCascade(
                        InstructorAttributes
                                .updateOptionsWithGoogleIdBuilder(instructorToEdit.courseId, instructorRequest.getId())
                                .withEmail(instructorToEdit.email)
                                .withName(instructorToEdit.name)
                                .withDisplayedName(instructorToEdit.displayedName)
                                .withIsDisplayedToStudents(instructorToEdit.isDisplayedToStudents)
                                .withPrivileges(instructorToEdit.privileges)
                                .withRole(instructorToEdit.role)
                                .build());
            }
            InstructorData newInstructorData = new InstructorData(updatedInstructor);
            newInstructorData.setGoogleId(updatedInstructor.getGoogleId());
            return new JsonResult(newInstructorData);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        } catch (EntityDoesNotExistException ednee) {
            return new JsonResult(ednee.getMessage(), HttpStatus.SC_NOT_FOUND);
        }
    }

    /**
     * Checks if there are any other registered instructors that can modify instructors.
     * If there are none, the instructor currently being edited will be granted the privilege
     * of modifying instructors automatically.
     *
     * @param courseId         Id of the course.
     * @param instructorToEdit Instructor that will be edited.
     *                             This may be modified within the method.
     */
    private void updateToEnsureValidityOfInstructorsForTheCourse(String courseId, InstructorAttributes instructorToEdit) {
        List<InstructorAttributes> instructors = logic.getInstructorsForCourse(courseId);
        int numOfInstrCanModifyInstructor = 0;
        InstructorAttributes instrWithModifyInstructorPrivilege = null;
        for (InstructorAttributes instructor : instructors) {
            if (instructor.isAllowedForPrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR)) {
                numOfInstrCanModifyInstructor++;
                instrWithModifyInstructorPrivilege = instructor;
            }
        }
        boolean isLastRegInstructorWithPrivilege = numOfInstrCanModifyInstructor <= 1
                && instrWithModifyInstructorPrivilege != null
                && (!instrWithModifyInstructorPrivilege.isRegistered()
                || instrWithModifyInstructorPrivilege.googleId
                .equals(instructorToEdit.googleId));
        if (isLastRegInstructorWithPrivilege) {
            instructorToEdit.privileges.updatePrivilege(Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR, true);
        }
    }

    /**
     * Creates a new instructor representing the updated instructor with all information filled in,
     * using request parameters.
     * This includes basic information as well as custom privileges (if applicable).
     *
     * @param courseId  Id of the course the instructor is being added to.
     * @param instructorRequest create request.
     * @return The updated instructor with all relevant info filled in.
     */
    private InstructorAttributes extractUpdatedInstructor(String courseId, InstructorCreateRequest instructorRequest) {

        InstructorAttributes instructorToEdit =
                retrieveEditedInstructor(courseId, instructorRequest.getId(),
                        instructorRequest.getName(), instructorRequest.getEmail(),
                        instructorRequest.getRoleName(), instructorRequest.getIsDisplayedToStudent(),
                        instructorRequest.getDisplayName());

        if (instructorToEdit.getRole().equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            updateInstructorCourseLevelPrivileges(instructorToEdit);
        }

        updateInstructorWithSectionLevelPrivileges(courseId, instructorToEdit);

        instructorToEdit.privileges.validatePrivileges();

        return instructorToEdit;
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
     * @return The edited instructor with updated basic info, and its old custom privileges (if applicable)
     */
    private InstructorAttributes retrieveEditedInstructor(String courseId, String instructorId, String instructorName,
                                                          String instructorEmail, String instructorRole,
                                                          boolean isDisplayedToStudents, String displayedName) {
        InstructorAttributes instructorToEdit = null;
        if (instructorId == null) {
            instructorToEdit = logic.getInstructorForEmail(courseId, instructorEmail);
        } else {
            instructorToEdit = logic.getInstructorForGoogleId(courseId, instructorId);
        }

        String newDisplayedName = displayedName;
        if (newDisplayedName == null || newDisplayedName.isEmpty()) {
            newDisplayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        }

        instructorToEdit.name = SanitizationHelper.sanitizeName(instructorName);
        instructorToEdit.email = SanitizationHelper.sanitizeEmail(instructorEmail);
        instructorToEdit.role = SanitizationHelper.sanitizeName(instructorRole);
        instructorToEdit.displayedName = SanitizationHelper.sanitizeName(newDisplayedName);
        instructorToEdit.isDisplayedToStudents = isDisplayedToStudents;
        instructorToEdit.privileges = new InstructorPrivileges(instructorToEdit.role);

        return instructorToEdit;
    }

}
