package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.ui.output.CourseData;

/**
 * Get a course for an instructor or student.
 */
class GetCourseAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (userInfo != null && userInfo.isAdmin) {
            return;
        }

        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String entityType = getNonNullRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        CourseAttributes course = logic.getCourse(courseId);

        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            gateKeeper.verifyAccessible(getPossiblyUnregisteredInstructor(courseId), course);
            return;
        }

        if (Const.EntityType.STUDENT.equals(entityType)) {
            gateKeeper.verifyAccessible(getPossiblyUnregisteredStudent(courseId), course);
            return;
        }

        throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        CourseAttributes courseAttributes = logic.getCourse(courseId);
        if (courseAttributes == null) {
            throw new EntityNotFoundException("No course with id: " + courseId);
        }
        CourseData output = new CourseData(courseAttributes);
        String entityType = getRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            InstructorAttributes instructor = getPossiblyUnregisteredInstructor(courseId);
            if (instructor != null) {
                InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, null);
                output.setPrivileges(privilege);
            }
        } else if (Const.EntityType.STUDENT.equals(entityType)) {
            output.hideInformationForStudent();
        }
        return new JsonResult(output);
    }
}
