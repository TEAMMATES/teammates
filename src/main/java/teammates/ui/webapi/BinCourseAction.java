package teammates.ui.webapi;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Const;
import teammates.storage.entity.Course;
import teammates.storage.entity.Instructor;
import teammates.ui.exception.EntityNotFoundException;
import teammates.ui.exception.UnauthorizedAccessException;
import teammates.ui.output.CourseData;
import teammates.ui.output.CourseViewData;
import teammates.ui.output.InstructorCoursePermissionsData;

/**
 * Move a course to the recycle bin.
 */
public class BinCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        gateKeeper.verifyInstructorHasPrivilege(requestContext, idOfCourseToBin,
                Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            Course binnedCourse = logic.moveCourseToRecycleBin(idOfCourseToBin);
            CourseViewData output = new CourseViewData(new CourseData(binnedCourse));
            Instructor instructor = getInstructorFromRequest(idOfCourseToBin);
            if (instructor != null) {
                output.setInstructorPermissions(new InstructorCoursePermissionsData(
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_COURSE),
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_STUDENT),
                        logic.hasInstructorPermissions(instructor, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR)));
            }
            return new JsonResult(output);
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }
}
