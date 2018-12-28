package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorCoursesPageData;

/**
 * Action: Restore all courses from Recycle Bin for an instructor.
 */
public class InstructorCourseRestoreAllSoftDeletedCoursesAction extends Action {

    @Override
    public ActionResult execute() {

        InstructorCoursesPageData data = new InstructorCoursesPageData(account, sessionToken);
        List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(data.account.googleId);

        for (InstructorAttributes instructor : instructorList) {
            CourseAttributes course = logic.getSoftDeletedCourseForInstructor(instructor);
            if (course != null) {
                gateKeeper.verifyAccessible(instructor,
                        course,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COURSE);
            }
        }

        try {
            /* Restore all courses and setup status to be shown to user and admin */
            logic.restoreAllCoursesFromRecycleBin(instructorList);
            String statusMessage = Const.StatusMessages.COURSE_ALL_RESTORED;
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            statusToAdmin = "All courses restored";
        } catch (Exception e) {
            setStatusForException(e);
        }

        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_COURSES_PAGE);
    }
}
