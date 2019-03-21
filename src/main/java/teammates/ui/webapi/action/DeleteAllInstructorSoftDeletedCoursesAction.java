package teammates.ui.webapi.action;

import java.util.List;
import java.util.stream.Collectors;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Permanently deletes all courses in Recycle Bin.
 */
public class DeleteAllInstructorSoftDeletedCoursesAction extends Action {

    private List<InstructorAttributes> instructorList;

    @Override
    protected AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    public void checkSpecificAccessControl() {
        if (!userInfo.isInstructor) {
            throw new UnauthorizedAccessException("Instructor privilege is required to access this resource.");
        }
        instructorList = logic.getInstructorsForGoogleId(userInfo.id);
        for (InstructorAttributes instructor : instructorList) {
            CourseAttributes course = logic.getSoftDeletedCourseForInstructor(instructor);
            if (course != null) {
                gateKeeper.verifyAccessible(instructor,
                        course,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
            }
        }
    }

    @Override
    public ActionResult execute() {

        instructorList = logic.getInstructorsForGoogleId(userInfo.id);

        List<String> softDeletedCourseIdList = instructorList.stream()
                .filter(instructor -> logic.getCourse(instructor.courseId).isCourseDeleted())
                .map(InstructorAttributes::getCourseId)
                .collect(Collectors.toList());

        for (String courseId : softDeletedCourseIdList) {
            logic.deleteCourseCascade(courseId);
        }

        String statusMessage = "All courses in Recycle Bin have been permanently deleted.";
        return new JsonResult(statusMessage);
    }
}
