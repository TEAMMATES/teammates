package teammates.ui.webapi.action;

import java.util.List;

import org.apache.http.HttpStatus;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;

/**
 * Action: Restores all soft-deleted courses from Recycle Bin.
 */
public class RestoreAllInstructorSoftDeletedCoursesAction extends Action {

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
        String statusMessage;

        try {
            logic.restoreAllCoursesFromRecycleBin(instructorList);

            statusMessage = "All courses have been restored.";
        } catch (EntityDoesNotExistException e) {
            return new JsonResult(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }

        return new JsonResult(statusMessage);
    }
}
