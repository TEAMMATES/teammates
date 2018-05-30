package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorRecoveryPageData;

/**
 * Action: Permanently delete all courses from Recycle Bin for an instructor.
 */
public class InstructorRecoveryDeleteAllCoursesAction extends Action {

    @Override
    public ActionResult execute() {

        gateKeeper.verifyInstructorPrivileges(account);

        InstructorRecoveryPageData data = new InstructorRecoveryPageData(account, sessionToken);

        try {
            /* Permanently delete all courses and setup status to be shown to user and admin */
            List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(data.account.googleId);
            logic.deleteAllCourses(instructorList);
            String statusMessage = Const.StatusMessages.COURSE_ALL_DELETED;
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            statusToAdmin = "All courses deleted";
        } catch (Exception e) {
            setStatusForException(e);
        }

        if (isRedirectedToHomePage()) {
            return createRedirectResult(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
        }
        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_RECOVERY_PAGE);
    }

    /**
     * Checks if the action is executed in homepage or 'Recovery' page based on its redirection.
     */
    private boolean isRedirectedToHomePage() {
        String nextUrl = getRequestParamValue(Const.ParamsNames.NEXT_URL);
        return nextUrl != null && nextUrl.equals(Const.ActionURIs.INSTRUCTOR_HOME_PAGE);
    }
}
