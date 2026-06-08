package teammates.ui.webapi;

import java.util.UUID;

import teammates.common.util.Const;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.InstructorPrivilegeData;

/**
 * Get the instructor privilege.
 */
public class GetInstructorPrivilegeAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (requestContext.isAdmin()) {
            return;
        }

        String courseId;
        UUID userId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);
        if (userId == null) {
            courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        } else {
            Instructor instructor = getInstructor(userId);
            courseId = instructor.getCourseId();
        }

        Instructor instructor = getInstructorFromRequest(courseId);

        if (instructor == null) {
            throw new UnauthorizedAccessException("Not instructor of the course");
        }
    }

    @Override
    public JsonResult execute() {
        UUID userId = getNullableUuidRequestParamValue(Const.ParamsNames.USER_ID);

        Instructor instructor;

        if (userId == null) {
            // Fetch privilege of logged in instructor
            String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
            instructor = getInstructorFromRequest(courseId);
        } else {
            // Fetch privilege of instructor with given userId
            instructor = getInstructor(userId);
        }

        InstructorPrivilegeData response = new InstructorPrivilegeData(instructor.getPrivileges());

        return new JsonResult(response);
    }

    private Instructor getInstructor(UUID userId) {
        Instructor instructor = logic.getInstructor(userId);

        if (instructor == null) {
            throw new EntityNotFoundException("Instructor does not exist.");
        }

        return instructor;
    }

}
