package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;

/**
 * Action: gets the session access logs of feedback sessions of a course.
 */
public class GetSessionAccessLogsAction extends Action {
    @Override
    AuthType getMinAuthLevel() {
        return AuthType.PUBLIC;
    }

    @Override
    void checkSpecificAccessControl() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        CourseAttributes courseAttributes = logic.getCourse(courseId);

        if (courseAttributes == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Course is not found"));
        }

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        gateKeeper.verifyAccessible(instructor, courseAttributes);
    }

    @Override
    ActionResult execute() {
        return null;
    }
}
