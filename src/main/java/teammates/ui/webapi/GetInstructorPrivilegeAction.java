package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Instructor;
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
        if (userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
            if (instructor == null) {
                throw new UnauthorizedAccessException("Not instructor of the course");
            }

            return;
        }

        Instructor instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());

        if (instructor == null) {
            throw new UnauthorizedAccessException("Not instructor of the course");
        }
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String instructorId = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_ID);
        String instructorEmail = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (!isCourseMigrated(courseId)) {
            InstructorAttributes instructor;
            if (instructorId == null) {
                if (instructorEmail == null) {
                    instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
                } else {
                    instructor = logic.getInstructorForEmail(courseId, instructorEmail);
                    if (instructor == null) {
                        throw new EntityNotFoundException("Instructor does not exist.");
                    }
                }
            } else {
                instructor = logic.getInstructorForGoogleId(courseId, instructorId);
                if (instructor == null) {
                    throw new EntityNotFoundException("Instructor does not exist.");
                }
            }

            InstructorPrivilegeData response = new InstructorPrivilegeData(instructor.getPrivileges());

            return new JsonResult(response);
        }

        Instructor instructor;

        if (instructorId == null) {
            if (instructorEmail == null) {
                instructor = sqlLogic.getInstructorByGoogleId(courseId, userInfo.getId());
            } else {
                instructor = sqlLogic.getInstructorForEmail(courseId, instructorEmail);

                if (instructor == null) {
                    throw new EntityNotFoundException("Instructor does not exist.");
                }
            }
        } else {
            instructor = sqlLogic.getInstructorByGoogleId(courseId, instructorId);

            if (instructor == null) {
                throw new EntityNotFoundException("Instructor does not exist.");
            }
        }

        InstructorPrivilegeData response = new InstructorPrivilegeData(instructor.getPrivileges());

        return new JsonResult(response);
    }

}
