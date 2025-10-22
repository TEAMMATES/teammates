package teammates.ui.webapi;

import teammates.common.datatransfer.DeletionPreviewData;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.logic.core.DeletionPreviewService;
import teammates.storage.sqlentity.Course;
import teammates.storage.sqlentity.Student;
import teammates.ui.output.DeletionPreviewOutput;

/**
 * Preview the deletion of a student to show what will be affected.
 */
public class PreviewStudentDeletionAction extends Action {

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
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        if (!isCourseMigrated(courseId)) {
            CourseAttributes courseAttributes = logic.getCourse(courseId);
            StudentAttributes studentAttributes = logic.getStudentForEmail(courseId, studentEmail);

            gateKeeper.verifyAccessible(logic.getInstructorForGoogleId(courseId, userInfo.id),
                    courseAttributes, studentAttributes,
                    Const.InstructorPermissions.CAN_MODIFY_STUDENT);
            return;
        }

        Course course = sqlLogic.getCourse(courseId);
        Student student = sqlLogic.getStudentForEmail(courseId, studentEmail);

        gateKeeper.verifyAccessible(sqlLogic.getInstructorByGoogleId(courseId, userInfo.id),
                course, student, Const.InstructorPermissions.CAN_MODIFY_STUDENT);
    }

    @Override
    public JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getNonNullRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);

        DeletionPreviewData previewData = deletionPreviewService.previewStudentDeletion(courseId, studentEmail);

        return new JsonResult(new DeletionPreviewOutput(previewData));
    }
}
