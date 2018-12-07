package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.ui.pagedata.InstructorCoursesPageData;

/**
 * Action: Restore all sessions from Recycle Bin for an instructor.
 */
public class InstructorFeedbackRestoreAllSoftDeletedSessionsAction extends Action {

    @Override
    public ActionResult execute() {

        InstructorCoursesPageData data = new InstructorCoursesPageData(account, sessionToken);
        List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(data.account.googleId);

        for (InstructorAttributes instructor : instructorList) {
            List<FeedbackSessionAttributes> feedbackSessionList =
                    logic.getSoftDeletedFeedbackSessionsListForInstructor(instructor);
            for (FeedbackSessionAttributes feedbackSession : feedbackSessionList) {
                gateKeeper.verifyAccessible(instructor,
                        feedbackSession,
                        false,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            }
        }

        try {
            /* Restore all sessions and setup status to be shown to user and admin */
            logic.restoreAllFeedbackSessionsFromRecycleBin(instructorList);
            String statusMessage = Const.StatusMessages.FEEDBACK_SESSION_ALL_RESTORED;
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            statusToAdmin = "All sessions in Recycle Bin restored";
        } catch (Exception e) {
            setStatusForException(e);
        }

        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
    }
}
