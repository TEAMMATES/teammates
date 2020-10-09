package teammates.ui.webapi;

import java.util.HashMap;
import java.util.Map;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;

/**
 * Move a course to the recycle bin.
 */
class BinCourseAction extends Action {

    private Map<String, CourseAttributes> courseIdToCourseAttributesMap;

    BinCourseAction() {
        this.courseIdToCourseAttributesMap = new HashMap<>();
    }

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }

        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes courseAttributes =
                ActionUtils.getCourseAttributes(courseIdToCourseAttributesMap, idOfCourseToBin, logic);
        gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(idOfCourseToBin, userInfo.id),
                courseAttributes, Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
    }

    @Override
    JsonResult execute() {
        String idOfCourseToBin = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        try {
            CourseAttributes courseAttributes =
                    ActionUtils.getCourseAttributes(courseIdToCourseAttributesMap, idOfCourseToBin, logic);
            courseAttributes.deletedAt = logic.moveCourseToRecycleBin(idOfCourseToBin);

            return new JsonResult(new CourseData(courseAttributes));
        } catch (EntityDoesNotExistException e) {
            throw new EntityNotFoundException(e);
        }
    }

}
