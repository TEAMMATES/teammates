package teammates.ui.controller;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.ThreadHelper;
import teammates.logic.api.GateKeeper;
import teammates.logic.core.Emails;
import teammates.logic.core.Emails.EmailType;

public class InstructorStudentCommentClearPendingAction extends Action {

    @Override
    protected ActionResult execute() throws EntityDoesNotExistException {
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
        
        logic.updateComments(courseId, CommentSendingState.PENDING, CommentSendingState.SENDING);
        logic.updateFeedbackResponseComments(courseId, CommentSendingState.PENDING, CommentSendingState.SENDING);
        
        // Wait for the operation to persist
        int elapsedTime = 0;
        int pendingCommentsSize = getPendingCommentsSize(courseId);
        while ((pendingCommentsSize != 0)
                && (elapsedTime < Config.PERSISTENCE_CHECK_DURATION)) {
            ThreadHelper.waitBriefly();
            pendingCommentsSize = getPendingCommentsSize(courseId);
            //check before incrementing to avoid boundary case problem
            if (pendingCommentsSize != 0) {
                elapsedTime += ThreadHelper.WAIT_DURATION;
            }
        }
        if (elapsedTime == Config.PERSISTENCE_CHECK_DURATION) {
            isError = true;
            log.severe("Operation did not persist in time: update comments from state PENDING to SENDING");
        } else {
            Emails emails = new Emails();
            emails.addCommentReminderToEmailsQueue(courseId, EmailType.PENDING_COMMENT_CLEARED);
        }
        
        if(!isError){
            statusToUser.add(Const.StatusMessages.COMMENT_CLEARED);
            statusToAdmin = "Successful: " + account.googleId + " cleared pending comments for course " + courseId;
        } else {
            statusToUser.add(Const.StatusMessages.COMMENT_CLEARED_UNSUCCESSFULLY);
            statusToAdmin = "Unsuccessful: " + account.googleId + " cleared pending comments for course " + courseId;
        }
        
        return createRedirectResult((new PageData(account).getInstructorCommentsLink()) + "&" + Const.ParamsNames.COURSE_ID + "=" + courseId);
    }
    
    private int getPendingCommentsSize(String courseId) throws EntityDoesNotExistException{
        return logic.getCommentsForSendingState(courseId, CommentSendingState.PENDING).size()
                + logic.getFeedbackResponseCommentsForSendingState(courseId, CommentSendingState.PENDING).size();
    }
}
