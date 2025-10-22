package teammates.ui.webapi;

import teammates.common.datatransfer.DeletionPreviewData;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.util.Const;
import teammates.logic.core.DeletionPreviewService;
import teammates.storage.sqlentity.Course;
import teammates.ui.output.DeletionPreviewOutput;

/**
 * Preview the deletion of a course to show what will be affected.
 */
public class PreviewCourseDeletionAction extends Action {

    private final DeletionPreviewService deletionPreviewService = DeletionPreviewService.inst();

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        if (!isCourseMigrated(courseId)) {
            CourseAttributes courseAttributes = logic.getCourse(courseId);
            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.id),
                    courseAttributes,
                    Const.InstructorPermissions.CAN_MODIFY_COURSE);
            return;
        }

        Course course = sqlLogic.getCourse(courseId);
        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(courseId, userInfo.id),
                course, Const.InstructorPermissions.CAN_MODIFY_COURSE);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        DeletionPreviewData previewData = deletionPreviewService.previewCourseDeletion(courseId);

        return new JsonResult(new DeletionPreviewOutput(previewData));
    }
}
