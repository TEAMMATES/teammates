package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;
import teammates.ui.request.InvalidHttpRequestBodyException;

/**
 * Action: adds another instructor to a course that already exists.
 */
public class CreateInstructorAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo.isAdmin) {
            return;
        }

        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (isCourseMigrated(courseId)) {
            Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.id);
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

        try {
            if (isCourseMigrated(courseId)) {
                return executeWithSql(courseId, instructorRequest);
            } else {
                return executeWithDataStore(courseId, instructorRequest);
            }
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(
                    "An instructor with the same email address already exists in the course.", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }

    /**
     * Executes the action using SQL storage.
     *
     * @param courseId          Id of the course the instructor is being added
     *                          to.
     * @param instructorRequest Request body containing the instructor's info.
     * @return The Json result of the created Instructor
     * @throws InvalidParametersException   If a parameter is invalid
     * @throws EntityAlreadyExistsException If there is a conflict at the email
     *                                      field
     */
    private JsonResult executeWithSql(String courseId, InstructorCreateRequest instructorRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {

        Instructor instructorToAdd = createInstructorWithBasicAttributesSql(courseId,
                SanitizationHelper.sanitizeName(instructorRequest.getName()),
                SanitizationHelper.sanitizeEmail(instructorRequest.getEmail()), instructorRequest.getRoleName(),
                instructorRequest.getIsDisplayedToStudent(),
                SanitizationHelper.sanitizeName(instructorRequest.getDisplayName()));

        Instructor createdInstructor = sqlLogic.createInstructor(instructorToAdd);

        taskQueuer.scheduleCourseRegistrationInviteToInstructor(
                this.userInfo.id, instructorToAdd.getEmail(), courseId, false);
        taskQueuer.scheduleInstructorForSearchIndexing(createdInstructor.getCourseId(), createdInstructor.getEmail());

        return new JsonResult(new InstructorData(createdInstructor));
    }

    /**
     * Executes the action using Datastore storage.
     *
     * @param courseId          Id of the course the instructor is being added
     *                          to.
     * @param instructorRequest Request body containing the instructor's info.
     * @return The Json result of the created Instructor
     * @throws InvalidParametersException   If a parameter is invalid
     * @throws EntityAlreadyExistsException If there is a conflict at the email
     *                                      field
     */
    private JsonResult executeWithDataStore(String courseId, InstructorCreateRequest instructorRequest)
            throws InvalidParametersException, EntityAlreadyExistsException {
        InstructorAttributes instructorToAdd = createInstructorWithBasicAttributes(courseId,
                instructorRequest.getName(), instructorRequest.getEmail(), instructorRequest.getRoleName(),
                instructorRequest.getIsDisplayedToStudent(), instructorRequest.getDisplayName());

        InstructorAttributes createdInstructor = logic.createInstructor(instructorToAdd);

        taskQueuer.scheduleCourseRegistrationInviteToInstructor(
                userInfo.id, instructorToAdd.getEmail(), instructorToAdd.getCourseId(), false);
        taskQueuer.scheduleInstructorForSearchIndexing(createdInstructor.getCourseId(), createdInstructor.getEmail());

        return new JsonResult(new InstructorData(createdInstructor));
    }

    /**
     * Creates a new instructor with basic information.
     * This consists of everything apart from custom privileges.
     *
     * @param courseId              Id of the course the instructor is being added
     *                              to.
     * @param instructorName        Name of the instructor.
     * @param instructorEmail       Email of the instructor.
     * @param instructorRole        Role of the instructor.
     * @param isDisplayedToStudents Whether the instructor should be visible to
     *                              students.
     * @param displayedName         Name to be visible to students.
     *                              Should not be {@code null} even if
     *                              {@code isDisplayedToStudents} is false.
     * @return An instructor with basic info, excluding custom privileges
     */
    private Instructor createInstructorWithBasicAttributesSql(String courseId, String instructorName,
            String instructorEmail, String instructorRole,
            boolean isDisplayedToStudents, String displayedName) {

        String instrName = SanitizationHelper.sanitizeName(instructorName);
        String instrEmail = SanitizationHelper.sanitizeEmail(instructorEmail);
        String instrRole = SanitizationHelper.sanitizeName(instructorRole);

        String instrDisplayedName = displayedName;
        if (displayedName == null || displayedName.isEmpty()) {
            instrDisplayedName = Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR;
        }

        InstructorPrivileges privileges = new InstructorPrivileges(instrRole);
        InstructorPermissionRole role = InstructorPermissionRole.getEnum(instrRole);
        Course course = sqlLogic.getCourse(courseId);

        return new Instructor(course, instrName, instrEmail, isDisplayedToStudents, instrDisplayedName, role,
                privileges);
    }

    /**
     * Creates a new instructor with basic information.
     * This consists of everything apart from custom privileges.
     *
     * @param courseId              Id of the course the instructor is being added
     *                              to.
     * @param instructorName        Name of the instructor.
     * @param instructorEmail       Email of the instructor.
     * @param instructorRole        Role of the instructor.
     * @param isDisplayedToStudents Whether the instructor should be visible to
     *                              students.
     * @param displayedName         Name to be visible to students.
     *                              Should not be {@code null} even if
     *                              {@code isDisplayedToStudents} is false.
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
            instrDisplayedName = Const.DEFAULT_DISPLAY_NAME_FOR_INSTRUCTOR;
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
