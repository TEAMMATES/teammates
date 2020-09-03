package teammates.ui.webapi;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;

/**
 * Action: adds another instructor to a course that already exists.
 */
class CreateInstructorAction extends UpdateInstructorPrivilegesAbstractAction {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (userInfo.isAdmin) {
            return;
        }

        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_INSTRUCTOR);
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructorToAdd = extractCompleteInstructor(courseId);

        /* Process adding the instructor and setup status to be shown to user and admin */
        try {
            InstructorAttributes createdInstructor = logic.createInstructor(instructorToAdd);
            taskQueuer.scheduleCourseRegistrationInviteToInstructor(
                    userInfo.id, instructorToAdd.email, instructorToAdd.courseId, null, false);
            return new JsonResult(new InstructorData(createdInstructor));
        } catch (EntityAlreadyExistsException e) {
            return new JsonResult("An instructor with the same email address already exists in the course.",
                    HttpStatus.SC_CONFLICT);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

    }

    /**
     * Creates a new instructor with all information filled in, using request parameters.
     *
     * <p>This includes basic information as well as custom privileges (if applicable).
     *
     * @param courseId Id of the course the instructor is being added to.
     * @return An instructor with all relevant info filled in.
     */
    private InstructorAttributes extractCompleteInstructor(String courseId) {
        InstructorCreateRequest instructorRequest = getAndValidateRequestBody(InstructorCreateRequest.class);
        InstructorAttributes instructorToAdd = createInstructorWithBasicAttributes(courseId,
                instructorRequest.getName(), instructorRequest.getEmail(), instructorRequest.getRoleName(),
                instructorRequest.getIsDisplayedToStudent(), instructorRequest.getDisplayName());

        if (instructorToAdd.getRole().equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
            updateInstructorCourseLevelPrivileges(instructorToAdd);
        }

        updateInstructorWithSectionLevelPrivileges(courseId, instructorToAdd);
        instructorToAdd.privileges.validatePrivileges();
        return instructorToAdd;
    }

    /**
     * Creates a new instructor with basic information.
     * This consists of everything apart from custom privileges.
     *
     * @param courseId              Id of the course the instructor is being added to.
     * @param instructorName        Name of the instructor.
     * @param instructorEmail       Email of the instructor.
     * @param instructorRole        Role of the instructor.
     * @param isDisplayedToStudents Whether the instructor should be visible to students.
     * @param displayedName         Name to be visible to students.
     *                                  Should not be {@code null} even if {@code isDisplayedToStudents} is false.
     * @return An instructor with basic info, excluding custom privileges
     */
    private InstructorAttributes createInstructorWithBasicAttributes(String courseId, String instructorName,
                                                                     String instructorEmail, String instructorRole,
                                                                     boolean isDisplayedToStudents, String displayedName) {

        String instrName = SanitizationHelper.sanitizeName(instructorName);
        String instrEmail = SanitizationHelper.sanitizeEmail(instructorEmail);
        String instrRole = SanitizationHelper.sanitizeName(instructorRole);

        String instrDisplayedName = displayedName;
        if (displayedName == null || displayedName.isEmpty()) {
            instrDisplayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        }

        instrDisplayedName = SanitizationHelper.sanitizeName(instrDisplayedName);
        InstructorPrivileges privileges = new InstructorPrivileges(instructorRole);

        return InstructorAttributes.builder(courseId, instrEmail)
                .withName(instrName)
                .withRole(instrRole)
                .withIsDisplayedToStudents(isDisplayedToStudents)
                .withDisplayedName(instrDisplayedName)
                .withPrivileges(privileges)
                .build();
    }

}
