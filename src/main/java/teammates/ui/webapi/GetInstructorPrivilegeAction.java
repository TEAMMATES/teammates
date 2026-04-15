package teammates.ui.webapi;

import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.InstructorPrivilegeData;

import java.util.UUID;

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
        if (userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Instructor instructor = sqlLogic.getInstructorByAccountId(courseId, userInfo.getAccountId());

        if (instructor == null) {
            throw new UnauthorizedAccessException("Not instructor of the course");
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        UUID instructorAccountId = getUuidRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        Instructor instructor;

        if (instructorAccountId == null) {
            if (instructorEmail == null) {
                instructor = sqlLogic.getInstructorByAccountId(courseId, userInfo.getAccountId());
            } else {
                instructor = sqlLogic.getInstructorForEmail(courseId, instructorEmail);

                if (instructor == null) {
                    throw new EntityNotFoundException("Instructor does not exist.");
                }
            }
        } else {
            instructor = sqlLogic.getInstructorByAccountId(courseId, instructorAccountId);

            if (instructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }
        }

        InstructorPrivilegeData response = new InstructorPrivilegeData(instructor.getPrivileges());

        return new JsonResult(response);
    }

}
