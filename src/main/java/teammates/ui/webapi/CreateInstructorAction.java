package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.InstructorPermissionRole;
import teammates.common.datatransfer.InstructorPrivileges;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.SanitizationHelper;
import teammates.logic.entity.Account;
import teammates.logic.entity.Course;
import teammates.logic.entity.Instructor;
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

        Instructor instructor = logic.getInstructorByGoogleId(courseId, userInfo.id);
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorCreateRequest instructorRequest = getAndValidateRequestBody(InstructorCreateRequest.class);

        try {
            Course course = logic.getCourse(courseId);

            Instructor instructorToAdd = createInstructorWithBasicAttributes(course,
                    SanitizationHelper.sanitizeName(instructorRequest.getName()),
                    SanitizationHelper.sanitizeEmail(instructorRequest.getEmail()), instructorRequest.getRoleName(),
                    instructorRequest.getIsDisplayedToStudent(),
                    SanitizationHelper.sanitizeName(instructorRequest.getDisplayName()));

            Instructor createdInstructor = logic.createInstructor(instructorToAdd);

            // Generate and queue invitation email to priority queue (user-triggered)
            Account inviter = logic.getAccountForGoogleId(userInfo.id);
            if (inviter == null) {
                throw new EntityNotFoundException("Inviter account does not exist.");
            }
            EmailWrapper email = emailGenerator.generateInstructorCourseJoinEmail(inviter, createdInstructor, course);
            List<EmailWrapper> emails = new ArrayList<>();
            emails.add(email);
            taskQueuer.scheduleEmailsForPrioritySending(emails);

            return new JsonResult(new InstructorData(createdInstructor));
        } catch (EntityAlreadyExistsException e) {
            throw new InvalidOperationException(
                    "An instructor with the same email address already exists in the course.", e);
        } catch (InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e);
        }
    }

    /**
     * Creates a new instructor with basic information.
     * This consists of everything apart from custom privileges.
     *
     * @param course                The course the instructor is being added to.
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
    private Instructor createInstructorWithBasicAttributes(Course course, String instructorName,
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

        return new Instructor(course, instrName, instrEmail, isDisplayedToStudents, instrDisplayedName, role,
                privileges);
    }

}
