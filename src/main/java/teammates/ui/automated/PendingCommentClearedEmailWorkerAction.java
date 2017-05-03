package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.Const.ParamsNames;
import teammates.common.util.EmailWrapper;
import teammates.common.util.Logger;
import teammates.logic.api.EmailGenerator;

/**
 * Task queue worker action: prepares comments notification emails for a particular course to be sent.
 */
public class PendingCommentClearedEmailWorkerAction extends AutomatedAction {

    private static final Logger log = Logger.getLogger();

    @Override
    protected String getActionDescription() {
        return null;
    }

    @Override
    protected String getActionMessage() {
        return null;
    }

    @Override
    public void execute() {
        String courseId = getRequestParamValue(ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);

        List<EmailWrapper> emailsToBeSent = new EmailGenerator().generatePendingCommentsClearedEmails(courseId);
        try {
            taskQueuer.scheduleEmailsForSending(emailsToBeSent);
            logic.updateFeedbackResponseCommentsSendingState(
                    courseId, CommentSendingState.SENDING, CommentSendingState.SENT);
            logic.updateCommentsSendingState(courseId, CommentSendingState.SENDING, CommentSendingState.SENT);
        } catch (Exception e) {
            try {
                logic.updateFeedbackResponseCommentsSendingState(
                        courseId, CommentSendingState.SENDING, CommentSendingState.PENDING);
                logic.updateCommentsSendingState(courseId, CommentSendingState.SENDING, CommentSendingState.PENDING);
            } catch (EntityDoesNotExistException ednee) {
                log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(ednee));
            }
            log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
        }

    }

}
