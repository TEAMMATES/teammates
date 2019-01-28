package teammates.ui.controller;

import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;

/**
 * Action: Permanently delete all sessions from Recycle Bin for an instructor.
 */
public class InstructorFeedbackDeleteAllSoftDeletedSessionsAction extends Action {

    @Override
    public ActionResult execute() {

        List<InstructorAttributes> instructorList = logic.getInstructorsForGoogleId(account.googleId);

        for (InstructorAttributes instructor : instructorList) {
            List<FeedbackSessionAttributes> feedbackSessionList =
                    logic.getSoftDeletedFeedbackSessionsListForInstructor(instructor);
            for (FeedbackSessionAttributes feedbackSession : feedbackSessionList) {
                gateKeeper.verifyAccessible(instructor,
                        feedbackSession,
                        Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_SESSION);
            }
        }

        try {
            logic.deleteAllFeedbackSessions(instructorList);
            String statusMessage = Const.StatusMessages.FEEDBACK_SESSION_ALL_DELETED;
            statusToUser.add(new StatusMessage(statusMessage, StatusMessageColor.SUCCESS));
            statusToAdmin = "All sessions in Recycle Bin deleted";
        } catch (Exception e) {
            setStatusForException(e);
        }

        return createRedirectResult(Const.ActionURIs.INSTRUCTOR_FEEDBACK_SESSIONS_PAGE);
    }
}
