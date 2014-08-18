package teammates.logic.automated;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.Emails;
import teammates.logic.core.FeedbackResponseCommentsLogic;

/**
 * The mail action that is to execute, when pending comments are cleared
 */
public class PendingCommentClearedMailAction extends EmailAction {
    private String courseId;
    private CommentsLogic commentsLogic = CommentsLogic.inst();
    private FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    
    public PendingCommentClearedMailAction(HttpServletRequest req) {
        super(req);
        initializeNameAndDescription();
        
        courseId = HttpRequestHelper
                .getValueFromRequestParameterMap(req, ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }

    public PendingCommentClearedMailAction(HashMap<String, String> paramMap) {
        super(paramMap);
        initializeNameAndDescription();
        
        courseId = paramMap.get(ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
    }

    @Override
    protected void doPostProcessingForSuccesfulSend() throws EntityDoesNotExistException {
        frcLogic.updateFeedbackResponseCommentsSendingState(courseId, CommentSendingState.SENDING, CommentSendingState.SENT);
        commentsLogic.updateCommentsSendingState(courseId, CommentSendingState.SENDING, CommentSendingState.SENT);
    }

    protected void doPostProcessingForUnsuccesfulSend() throws EntityDoesNotExistException {
        //recover the pending state when it fails
        frcLogic.updateFeedbackResponseCommentsSendingState(courseId, CommentSendingState.SENDING, CommentSendingState.PENDING);
        commentsLogic.updateCommentsSendingState(courseId, CommentSendingState.SENDING, CommentSendingState.PENDING);
    }

    @Override
    protected List<MimeMessage> prepareMailToBeSent()
            throws MessagingException, IOException, EntityDoesNotExistException {
        Emails emailManager = new Emails();
        List<MimeMessage> preparedEmails = null;
        
        log.info("Fetching recipient emails for pending comments in course : "
                + courseId);
        Set<String> recipients = commentsLogic.getRecipientEmailsForSendingComments(courseId);
        
        if(recipients != null) {
            preparedEmails = emailManager
                            .generatePendingCommentsClearedEmails(courseId, recipients);
        } else {
            log.severe("Recipient emails for pending comments in course : " + courseId +
                       " could not be fetched");
        }
        return preparedEmails;
    }

    private void initializeNameAndDescription() {
        actionName = "pendingCommentClearedMailAction";
        actionDescription = "clear pending comments";
    }
}
