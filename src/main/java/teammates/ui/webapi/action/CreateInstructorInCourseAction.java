package teammates.ui.webapi.action;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;

/**
 * Action: adds another instructor to a course that already exists.
 */
public class CreateInstructorInCourseAction extends UpdateInstructorPrivilegesAbstractAction {

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
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
    public ActionResult execute() {

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorAttributes instructorToAdd = extractCompleteInstructor(courseId);

        /* Process adding the instructor and setup status to be shown to user and admin */
        try {
            logic.createInstructor(instructorToAdd);
            taskQueuer.scheduleCourseRegistrationInviteToInstructor(
                    userInfo.id, instructorToAdd.email, instructorToAdd.courseId, null, false);

        } catch (EntityAlreadyExistsException e) {
            return new JsonResult("An instructor with the same email address already exists in the course.",
                    HttpStatus.SC_CONFLICT);
        } catch (InvalidParametersException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult("The instructor " + instructorToAdd.name + " has been added successfully. "
                + "An email containing how to 'join' this course will be sent to "
                + instructorToAdd.email + " in a few minutes.", HttpStatus.SC_OK);

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
        String instructorName = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_NAME);
        String instructorEmail = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);
        String instructorRole = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_ROLE_NAME);

        boolean isDisplayedToStudents = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_IS_DISPLAYED_TO_STUDENT) != null;
        String displayedName = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_DISPLAY_NAME);
        if (displayedName == null || displayedName.isEmpty()) {
            displayedName = InstructorAttributes.DEFAULT_DISPLAY_NAME;
        }
        instructorRole = SanitizationHelper.sanitizeName(instructorRole);
        displayedName = SanitizationHelper.sanitizeName(displayedName);

        InstructorAttributes instructorToAdd = createInstructorWithBasicAttributes(courseId, instructorName,
                instructorEmail, instructorRole, isDisplayedToStudents, displayedName);

        if (instructorRole.equals(Const.InstructorPermissionRoleNames.INSTRUCTOR_PERMISSION_ROLE_CUSTOM)) {
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
        String instrDisplayedName = SanitizationHelper.sanitizeName(displayedName);
        InstructorPrivileges privileges = new InstructorPrivileges(instructorRole);

        return InstructorAttributes.builder(null, courseId, instrName, instrEmail)
                .withRole(instrRole)
                .withIsDisplayedToStudents(isDisplayedToStudents)
                .withDisplayedName(instrDisplayedName)
                .withPrivileges(privileges)
                .build();
    }

}
