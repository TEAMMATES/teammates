package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorRecoveryPageData;

/**
 * Action: Restore all courses from Recycle Bin for an instructor.
 */
public class InstructorRecoveryRestoreAllCoursesAction extends Action {

    @Override
    public ActionResult execute() {

        gateKeeper.verifyInstructorPrivileges(account);

        InstructorRecoveryPageData data = new InstructorRecoveryPageData(account, sessionToken);

        try {
            /* Restore all courses and setup status to be shown to user and admin */
            List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(data.account.googleId);
            logic.restoreAllCoursesFromRecovery(instructorList);
            String statusMessage = Const.StatusMessages.COURSE_ALL_RESTORED;
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            statusToAdmin = "All courses restored";
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
