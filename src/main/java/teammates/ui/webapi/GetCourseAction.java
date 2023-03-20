package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.CourseData;

/**
 * Get a course for an instructor or student.
 */
public class GetCourseAction extends Action {

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

        if (!isCourseMigrated(courseId)) {
            CourseAttributes courseAttributes = logic.getCourse(courseId);
            if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
                gateKeeper.verifyAccessible(getPossiblyUnregisteredInstructor(courseId), courseAttributes);
                return;
            }

            if (Const.EntityType.STUDENT.equals(entityType)) {
                gateKeeper.verifyAccessible(getPossiblyUnregisteredStudent(courseId), courseAttributes);
                return;
            }

            throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
        }

        Course course = sqlLogic.getCourse(courseId);
        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            gateKeeper.verifyAccessible(getPossiblyUnregisteredSqlInstructor(courseId), course);
            return;
        }

        if (Const.EntityType.STUDENT.equals(entityType)) {
            gateKeeper.verifyAccessible(getPossiblyUnregisteredSqlStudent(courseId), course);
            return;
        }

        throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            return this.getFromDatastore(courseId);
        }

        Course course = sqlLogic.getCourse(courseId);
        if (course == null) {
            throw new EntityNotFoundException("No course with id: " + courseId);
        }

        CourseData output = new CourseData(course);
        String entityType = getRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            Instructor instructor = getPossiblyUnregisteredSqlInstructor(courseId);
            if (instructor != null) {
                InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, null);
                output.setPrivileges(privilege);
            }
        } else if (Const.EntityType.STUDENT.equals(entityType)) {
            output.hideInformationForStudent();
        }
        return new JsonResult(output);
    }

    private JsonResult getFromDatastore(String courseId) {
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
