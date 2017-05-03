package teammates.ui.controller;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.Logger;
import teammates.common.util.StatusMessage;
import teammates.common.util.StatusMessageColor;
import teammates.common.util.ThreadHelper;
import teammates.ui.pagedata.PageData;

/**
 * Action: Clear pending student comments and response comments,
 * and set up notification emails in the EmailsQueue.
 */
public class InstructorStudentCommentClearPendingAction extends Action {

    private static final Logger log = Logger.getLogger();

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);

        gateKeeper.verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));

        logic.updateCommentsSendingState(courseId, CommentSendingState.PENDING, CommentSendingState.SENDING);
        logic.updateFeedbackResponseCommentsSendingState(
                      courseId, CommentSendingState.PENDING, CommentSendingState.SENDING);

        // Wait for the operation to persist
        if (Config.PERSISTENCE_CHECK_DURATION > 0) {
            int elapsedTime = 0;
            int pendingCommentsSize = getPendingCommentsSize(courseId);
            while (pendingCommentsSize != 0 && elapsedTime < Config.PERSISTENCE_CHECK_DURATION) {
                ThreadHelper.waitBriefly();
                pendingCommentsSize = getPendingCommentsSize(courseId);
                //check before incrementing to avoid boundary case problem
                if (pendingCommentsSize != 0) {
                    elapsedTime += ThreadHelper.WAIT_DURATION;
                }
            }
            if (elapsedTime >= Config.PERSISTENCE_CHECK_DURATION) {
                isError = true;
                log.info("Operation did not persist in time: update comments from state PENDING to SENDING");
            } else {
                //Set up emails notification
                taskQueuer.scheduleCommentsNotificationsForCourse(courseId);
            }
        }

        if (isError) {
            statusToUser.add(new StatusMessage(Const.StatusMessages.COMMENT_CLEARED_UNSUCCESSFULLY,
                                               StatusMessageColor.DANGER));
            statusToAdmin = "Unsuccessful: " + account.googleId + " cleared pending comments for course " + courseId;
        } else {
            statusToUser.add(new StatusMessage(Const.StatusMessages.COMMENT_CLEARED, StatusMessageColor.SUCCESS));
            statusToAdmin = "Successful: " + account.googleId + " cleared pending comments for course " + courseId;
        }

        return createRedirectResult(new PageData(account).getInstructorCommentsLink() + "&"
                                    + Const.ParamsNames.COURSE_ID + "=" + courseId);
    }

    private int getPendingCommentsSize(String courseId) throws EntityDoesNotExistException {
        return logic.getCommentsForSendingState(courseId, CommentSendingState.PENDING).size()
                + logic.getFeedbackResponseCommentsForSendingState(courseId, CommentSendingState.PENDING).size();
    }
}
