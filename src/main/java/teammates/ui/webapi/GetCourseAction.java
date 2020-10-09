package teammates.ui.webapi;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;

/**
 * Get a course for an instructor or student.
 */
class GetCourseAction extends Action {

    private final Map<String, CourseAttributes> courseIdToCourseAttributesMap;

    GetCourseAction() {
        this.courseIdToCourseAttributesMap = new HashMap<>();
    }

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() {
        if (userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);

        if (userInfo.isInstructor && Const.EntityType.INSTRUCTOR.equals(entityType)) {
            CourseAttributes course = ActionUtils.getCourseAttributes(courseIdToCourseAttributesMap, courseId, logic);
            gateKeeper.verifyAccessible(
                    logic.getInstructorForGoogleId(courseId, userInfo.getId()), course);
            return;
        }

        if (userInfo.isStudent && Const.EntityType.STUDENT.equals(entityType)) {
            CourseAttributes course = ActionUtils.getCourseAttributes(courseIdToCourseAttributesMap, courseId, logic);
            gateKeeper.verifyAccessible(logic.getStudentForGoogleId(courseId, userInfo.getId()), course);
            return;
        }

        throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes courseAttributes =
                ActionUtils.getCourseAttributes(courseIdToCourseAttributesMap, courseId, logic);
        if (courseAttributes == null) {
            return new JsonResult("No course with id: " + courseId, HttpStatus.SC_NOT_FOUND);
        }
        return new JsonResult(new CourseData(courseAttributes));
    }
}
