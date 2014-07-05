package teammates.logic.core;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentSearchResultBundle;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackResponseCommentsDb;

public class FeedbackResponseCommentsLogic {
    @SuppressWarnings("unused") //used by test
    private static final Logger log = Utils.getLogger();
    
    private static FeedbackResponseCommentsLogic instance;

    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    public static FeedbackResponseCommentsLogic inst() {
        if (instance == null)
            instance = new FeedbackResponseCommentsLogic();
        return instance;
    }

    public void createFeedbackResponseComment(
            FeedbackResponseCommentAttributes frComment)
            throws InvalidParametersException, EntityDoesNotExistException {
        verifyIsCoursePresent(frComment.courseId);
        verifyIsInstructorOfCourse(frComment.courseId, frComment.giverEmail);
        verifyIsFeedbackSessionOfCourse(frComment.courseId, frComment.feedbackSessionName);
        
        try{
            frcDb.createEntity(frComment);
        } catch (EntityAlreadyExistsException e) {
            try{
                FeedbackResponseCommentAttributes existingComment = new FeedbackResponseCommentAttributes();
                
                existingComment = frcDb.getFeedbackResponseComment(frComment.feedbackResponseId, frComment.giverEmail, frComment.createdAt);
                frComment.setId(existingComment.getId());
                
                frcDb.updateFeedbackResponseComment(frComment);            
            } catch(Exception EntityDoesNotExistException){
                Assumption.fail();
            }
        }
    }
    
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        return frcDb.getFeedbackResponseComment(feedbackResponseCommentId);
    }
    
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String responseId, String giverEmail, Date creationDate) {
        return frcDb.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForResponse(String feedbackResponseId){
        return frcDb.getFeedbackResponseCommentsForResponse(feedbackResponseId);
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForSession(String courseId,
            String feedbackSessionName) {
        return frcDb.getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForSessionInSection(String courseId, String feedbackSessionName, String section){
        if(section == null){
            return getFeedbackResponseCommentForSession(courseId, feedbackSessionName);
        } else {
            return frcDb.getFeedbackResponseCommentsForSessionInSection(courseId, feedbackSessionName, section);
        }
    }

    public void updateFeedbackResponseCommentForResponse(String feedbackResponseId) throws InvalidParametersException, EntityDoesNotExistException{
        List<FeedbackResponseCommentAttributes> comments = getFeedbackResponseCommentForResponse(feedbackResponseId);
        FeedbackResponseAttributes response = frLogic.getFeedbackResponse(feedbackResponseId);
        for(FeedbackResponseCommentAttributes comment : comments){
            comment.giverSection = response.giverSection;
            comment.receiverSection = response.recipientSection;
            frcDb.updateFeedbackResponseComment(comment);
        }
    }

    public void updateFeedbackResponseComment(
            FeedbackResponseCommentAttributes feedbackResponseComment) throws InvalidParametersException, EntityDoesNotExistException {
        frcDb.updateFeedbackResponseComment(feedbackResponseComment);    
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSendingState(String courseId, CommentSendingState state) 
            throws EntityDoesNotExistException{
        verifyIsCoursePresent(courseId);
        
        List<FeedbackResponseCommentAttributes> frcList = new ArrayList<FeedbackResponseCommentAttributes>();
        List<FeedbackSessionAttributes> feedbackSessions = fsLogic.getFeedbackSessionsForCourse(courseId);
        for(FeedbackSessionAttributes fs:feedbackSessions){
            if(fs.isPublished()){
                frcList = frcDb.getFeedbackResponseCommentsForSendingState(courseId, fs.feedbackSessionName, state);
            }
        }
        return frcList;
    }
    
    public void updateFeedbackResponseCommentsSendingState(
            String courseId, CommentSendingState oldState, CommentSendingState newState) throws EntityDoesNotExistException {
        verifyIsCoursePresent(courseId);
        
        List<FeedbackSessionAttributes> feedbackSessions = fsLogic.getFeedbackSessionsForCourse(courseId);
        for(FeedbackSessionAttributes fs:feedbackSessions){
            if(fs.isPublished()){
                frcDb.updateFeedbackResponseComments(courseId, fs.feedbackSessionName, oldState, newState);    
            }
        }
    }
    
    public FeedbackResponseCommentSearchResultBundle searchFeedbackResponseComments(
            String queryString, String googleId, String cursorString) {
        return frcDb.search(queryString, googleId, cursorString);
    }
    
    public void deleteFeedbackResponseComment(
            FeedbackResponseCommentAttributes feedbackResponseComment) {
        frcDb.deleteEntity(feedbackResponseComment);    
    }
    
    private void verifyIsCoursePresent(String courseId) throws EntityDoesNotExistException{
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to create feedback response comments for a course that does not exist.");
        }
    }
    
    private void verifyIsInstructorOfCourse(String courseId, String email) throws EntityDoesNotExistException{
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        if(instructor == null){
            throw new EntityDoesNotExistException(
                    "User " + email + " is not a registered instructor for course "+ courseId + ".");
        }
    }
    
    private void verifyIsFeedbackSessionOfCourse(String courseId, String feedbackSessionName) throws EntityDoesNotExistException{
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if(session == null){
            throw new EntityDoesNotExistException(
                    "Feedback session " + feedbackSessionName 
                    + " is not a session for course "+ courseId + ".");
        }
    }
}
