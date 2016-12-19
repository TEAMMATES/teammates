package teammates.ui.automated;

import java.util.List;

import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.TeammatesException;
import teammates.common.util.Assumption;
import teammates.common.util.EmailType;
import teammates.common.util.EmailWrapper;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.Const.ParamsNames;
import teammates.logic.core.CommentsLogic;
import teammates.logic.core.EmailGenerator;
import teammates.logic.core.EmailSender;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackSessionsLogic;

/**
 * Task queue worker action: prepares large number of emails to be sent via task queue.
 */
public class PrepareEmailWorkerAction extends AutomatedAction {
    
    private final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private final CommentsLogic commentsLogic = CommentsLogic.inst();
    private final FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    
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
        String emailTypeString = getRequestParamValue(ParamsNames.EMAIL_TYPE);
        EmailType emailType = EmailType.valueOf(emailTypeString);
        Assumption.assertNotNull(emailType);
        
        String feedbackSessionName = getRequestParamValue(ParamsNames.EMAIL_FEEDBACK);
        
        String courseId = getRequestParamValue(ParamsNames.EMAIL_COURSE);
        Assumption.assertNotNull(courseId);
        
        log.info("Prepare email worker activated for: " + HttpRequestHelper.printRequestParameters(request));
        
        List<EmailWrapper> emailsToBeSent = prepareEmailsToBeSent(emailType, feedbackSessionName, courseId);
        
        if (emailsToBeSent == null) {
            log.severe("Emails to be sent is null for type: " + emailTypeString);
            setErrorResponse();
            return;
        }
        
        try {
            new EmailSender().sendEmails(emailsToBeSent);
            doPostProcessingForSuccesfulSend(emailType, feedbackSessionName, courseId);
        } catch (Exception e) {
            try {
                doPostProcessingForUnsuccesfulSend(emailType, courseId);
            } catch (EntityDoesNotExistException ednee) {
                log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(ednee));
            }
            log.severe("Unexpected error: " + TeammatesException.toStringWithStackTrace(e));
            setErrorResponse();
        }
    }
    
    private void doPostProcessingForSuccesfulSend(EmailType emailType, String feedbackSessionName, String courseId)
            throws InvalidParametersException, EntityDoesNotExistException {
        switch (emailType) {
        case FEEDBACK_CLOSING:
        case FEEDBACK_CLOSED:
        case FEEDBACK_OPENING:
        case FEEDBACK_PUBLISHED:
        case FEEDBACK_UNPUBLISHED:
            FeedbackSessionAttributes feedbackSession = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
            switch (emailType) {
            case FEEDBACK_CLOSING:
                feedbackSession.setSentClosingEmail(true);
                break;
            case FEEDBACK_CLOSED:
                feedbackSession.setSentClosedEmail(true);
                break;
            case FEEDBACK_OPENING:
                feedbackSession.setSentOpenEmail(true);
                break;
            case FEEDBACK_PUBLISHED:
                feedbackSession.setSentPublishedEmail(true);
                break;
            case FEEDBACK_UNPUBLISHED:
                feedbackSession.setSentPublishedEmail(false);
                break;
            default: // impossible to reach
                break;
            }
            fsLogic.updateFeedbackSession(feedbackSession);
            break;
            
        case PENDING_COMMENT_CLEARED:
            frcLogic.updateFeedbackResponseCommentsSendingState(
                    courseId, CommentSendingState.SENDING, CommentSendingState.SENT);
            commentsLogic.updateCommentsSendingState(courseId, CommentSendingState.SENDING, CommentSendingState.SENT);
            break;
            
        default:
            break;
        }
    }
    
    private void doPostProcessingForUnsuccesfulSend(EmailType emailType, String courseId)
            throws EntityDoesNotExistException {
        if (emailType == EmailType.PENDING_COMMENT_CLEARED) {
            frcLogic.updateFeedbackResponseCommentsSendingState(
                    courseId, CommentSendingState.SENDING, CommentSendingState.PENDING);
            commentsLogic.updateCommentsSendingState(courseId, CommentSendingState.SENDING, CommentSendingState.PENDING);
        }
    }
    
    private List<EmailWrapper> prepareEmailsToBeSent(EmailType emailType, String feedbackSessionName, String courseId) {
        switch (emailType) {
        case FEEDBACK_CLOSING:
        case FEEDBACK_CLOSED:
        case FEEDBACK_OPENING:
        case FEEDBACK_PUBLISHED:
        case FEEDBACK_UNPUBLISHED:
            FeedbackSessionAttributes feedbackSession = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
            if (feedbackSession == null) {
                log.severe("Feedback session object for feedback session name: " + feedbackSessionName
                           + " for course: " + courseId + " could not be fetched.");
                return null;
            }
            switch (emailType) {
            case FEEDBACK_CLOSING:
                return new EmailGenerator().generateFeedbackSessionClosingEmails(feedbackSession);
            case FEEDBACK_CLOSED:
                return new EmailGenerator().generateFeedbackSessionClosedEmails(feedbackSession);
            case FEEDBACK_OPENING:
                return new EmailGenerator().generateFeedbackSessionOpeningEmails(feedbackSession);
            case FEEDBACK_PUBLISHED:
                return new EmailGenerator().generateFeedbackSessionPublishedEmails(feedbackSession);
            case FEEDBACK_UNPUBLISHED:
                return new EmailGenerator().generateFeedbackSessionUnpublishedEmails(feedbackSession);
            default: // impossible to reach
                return null;
            }
            
        case PENDING_COMMENT_CLEARED:
            return new EmailGenerator().generatePendingCommentsClearedEmails(courseId);
            
        default:
            return null;
        
        }
    }
    
}
