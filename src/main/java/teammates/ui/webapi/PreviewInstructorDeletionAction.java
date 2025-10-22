package teammates.ui.webapi;

import teammates.common.datatransfer.DeletionPreviewData;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.logic.core.DeletionPreviewService;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Instructor;
import teammates.ui.output.DeletionPreviewOutput;

/**
 * Preview the deletion of an instructor to show what will be affected.
 */
public class PreviewInstructorDeletionAction extends Action {

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
        String instructorEmail = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        if (!isCourseMigrated(courseId)) {
            CourseAttributes courseAttributes = logic.getCourse(courseId);
            InstructorAttributes instructorAttributes = logic.getInstructorForEmail(courseId, instructorEmail);

            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.id),
                    courseAttributes, instructorAttributes,
                    Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
            return;
        }

        Course course = sqlLogic.getCourse(courseId);
        Instructor instructor = sqlLogic.getInstructorForEmail(courseId, instructorEmail);

        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(courseId, userInfo.id),
                course, instructor, Const.InstructorPermissions.CAN_MODIFY_INSTRUCTOR);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String instructorEmail = getNonNullRequestParamValue(Const.ParamsNames.INSTRUCTOR_EMAIL);

        DeletionPreviewData previewData = deletionPreviewService.previewInstructorDeletion(courseId, instructorEmail);

        return new JsonResult(new DeletionPreviewOutput(previewData));
    }
}
