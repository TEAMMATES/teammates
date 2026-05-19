package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.common.util.SanitizationHelper;
import teammates.storage.entity.Account;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
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
        if (authContext.isAdmin()) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = logic.getInstructorByGoogleId(courseId, getCurrentUserGoogleId());
        gateKeeper.verifyAccessible(
                instructor, logic.getCourse(courseId), Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorCreateRequest instructorRequest = getAndValidateRequestBody(InstructorCreateRequest.class);

        try {
            Course course = logic.getCourse(courseId);

            Instructor instructorToAdd = logic.createInstructorFromRequest(course,
                    SanitizationHelper.sanitizeName(instructorRequest.getName()),
                    SanitizationHelper.sanitizeEmail(instructorRequest.getEmail()), instructorRequest.getRoleName(),
                    instructorRequest.getIsDisplayedToStudent(),
                    SanitizationHelper.sanitizeName(instructorRequest.getDisplayName()));

            Instructor createdInstructor = logic.createInstructor(instructorToAdd);

            // Generate and queue invitation email to priority queue (user-triggered)
            Account inviter = logic.getAccountForGoogleId(getCurrentUserGoogleId());
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
}
