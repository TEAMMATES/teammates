package teammates.ui.webapi;

import java.util.ArrayList;
import java.util.List;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.EmailWrapper;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.InvalidHttpRequestBodyException;
import teammates.ui.exception.InvalidOperationException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorData;
import teammates.ui.request.InstructorCreateRequest;

/**
 * Action: adds another instructor to a course that already exists.
 */
public class CreateInstructorAction extends LoggedInAction {
    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        gateKeeper.verifyInstructorHasPrivilege(requestContext, courseId,
                Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() throws InvalidHttpRequestBodyException, InvalidOperationException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        InstructorCreateRequest instructorRequest = getAndValidateRequestBody(InstructorCreateRequest.class);

        try {
            Instructor createdInstructor = logic.createInstructor(courseId, instructorRequest);

            Instructor inviter = getInstructorFromRequest(courseId);
            if (inviter == null) {
                throw new EntityNotFoundException("Inviter does not exist.");
            }
            Course course = logic.getCourse(courseId);
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
