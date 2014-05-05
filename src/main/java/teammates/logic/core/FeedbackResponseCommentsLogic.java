package teammates.logic.core;

import java.util.Date;
import java.util.List;

import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.storage.api.FeedbackResponseCommentsDb;

public class FeedbackResponseCommentsLogic {
    private static FeedbackResponseCommentsLogic instance;

    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

    public static FeedbackResponseCommentsLogic inst() {
        if (instance == null)
            instance = new FeedbackResponseCommentsLogic();
        return instance;
    }

    public void createFeedbackResponseComment(
            FeedbackResponseCommentAttributes frca)
            throws InvalidParametersException {
        try{
            frcDb.createEntity(frca);
        } catch (EntityAlreadyExistsException e) {
            try{
                FeedbackResponseCommentAttributes existingComment = new FeedbackResponseCommentAttributes();
                
                existingComment = frcDb.getFeedbackResponseComment(frca.feedbackResponseId, frca.giverEmail, frca.createdAt);
                frca.setId(existingComment.getId());
                
                frcDb.updateFeedbackResponseComment(frca);            
            } catch(Exception EntityDoesNotExistException){
                Assumption.fail();
            }
        }
    }
    
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String responseId, String giverEmail, Date creationDate) {
        return frcDb.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForSession(String courseId,
            String feedbackSessionName) {
        return frcDb.getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);
    }

    public void updateFeedbackResponseComment(
            FeedbackResponseCommentAttributes feedbackResponseComment) throws InvalidParametersException, EntityDoesNotExistException {
        frcDb.updateFeedbackResponseComment(feedbackResponseComment);    
    }
    
    public void deleteFeedbackResponseComment(
            FeedbackResponseCommentAttributes feedbackResponseComment) {
        frcDb.deleteEntity(feedbackResponseComment);    
    }
}
