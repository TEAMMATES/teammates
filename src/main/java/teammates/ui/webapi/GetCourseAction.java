package teammates.ui.webapi;

import teammates.common.datatransfer.InstructorPermissionSet;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.storage.sqlentity.Course;
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

        CourseAttributes courseAttributes = logic.getCourse(courseId);
        if (!courseAttributes.isMigrated()) {
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

        Course _course = sqlLogic.getCourse(courseId);
        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            // TODO: Migrate once GateKeeper class is ready.
            // gateKeeper.verifyAccessible(getPossiblyUnregisteredInstructor(courseId), courseAttributes);
            return;
        }

        if (Const.EntityType.STUDENT.equals(entityType)) {
            // TODO: Migrate once GateKeeper class is ready.
            // gateKeeper.verifyAccessible(getPossiblyUnregisteredStudent(courseId), courseAttributes);
            return;
        }

        throw new UnauthorizedAccessException("Student or instructor account is required to access this resource.");
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        Course course = sqlLogic.getCourse(courseId);
        if (course == null) {
            return this.getFromDatastore(courseId);
        }

        CourseData output = new CourseData(course);
        
        String entityType = getRequestParamValue(Const.ParamsNames.ENTITY_TYPE);
        if (Const.EntityType.INSTRUCTOR.equals(entityType)) {
            // TODO: Migrate once Instructor class is ready.
            // InstructorAttributes instructor = getPossiblyUnregisteredInstructor(courseId);
            // if (instructor != null) {
            //     InstructorPermissionSet privilege = constructInstructorPrivileges(instructor, null);
            //     output.setPrivileges(privilege);
            // }
        } else if (Const.EntityType.STUDENT.equals(entityType)) {
            output.hideInformationForStudent();
        }
        return new JsonResult(output);
    }

    private JsonResult getFromDatastore(String courseId) throws EntityNotFoundException {
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
