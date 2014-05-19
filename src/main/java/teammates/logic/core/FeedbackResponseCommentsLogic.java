package teammates.logic.core;

import java.util.Date;
import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.storage.api.FeedbackResponseCommentsDb;

public class FeedbackResponseCommentsLogic {
    private static FeedbackResponseCommentsLogic instance;

    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();
    
    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();

    public static FeedbackResponseCommentsLogic inst() {
        if (instance == null)
            instance = new FeedbackResponseCommentsLogic();
        return instance;
    }

    public void createFeedbackResponseComment(
            FeedbackResponseCommentAttributes frComment)
            throws InvalidParametersException, EntityDoesNotExistException {
        Assumption.assertTrue(isCoursePresent(frComment.courseId));
        Assumption.assertTrue(isInstructorOfCourse(frComment.courseId, frComment.giverEmail));
        Assumption.assertTrue(isFeedbackSessionOfCourse(frComment.courseId, frComment.feedbackSessionName));
        Assumption.assertTrue(isFeedbackQuestionOfSession(frComment.feedbackSessionName, frComment.feedbackQuestionId));
        Assumption.assertTrue(isFeedbackResponseOfQuestion(frComment.feedbackQuestionId, frComment.feedbackResponseId));
        
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
    
    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String responseId, String giverEmail, Date creationDate) {
        return frcDb.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }
    
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForSession(String courseId,
            String feedbackSessionName) throws EntityDoesNotExistException {
        Assumption.assertTrue(isCoursePresent(courseId));
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
    
    private boolean isCoursePresent(String courseId) throws EntityDoesNotExistException{
        if (coursesLogic.isCoursePresent(courseId) == false) {
            throw new EntityDoesNotExistException(
                    "Trying to create comments for a course that does not exist.");
        }
        return true;
    }
    
    private boolean isInstructorOfCourse(String courseId, String email) throws EntityDoesNotExistException{
        InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, email);
        if(instructor == null){
            throw new EntityDoesNotExistException(
                    "User " + email + " is not a registered instructor for course "+ courseId + ".");
        }
        return true;
    }
    
    private boolean isFeedbackSessionOfCourse(String courseId, String feedbackSessionName) throws EntityDoesNotExistException{
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if(session == null){
            throw new EntityDoesNotExistException(
                    "Feedback session " + feedbackSessionName + " is not a session for course "+ courseId + ".");
        }
        return true;
    }
    
    private boolean isFeedbackQuestionOfSession(String feedbackSessionName, String questionId) throws EntityDoesNotExistException{
        FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(questionId);
        if(!question.feedbackSessionName.equals(feedbackSessionName)){
            throw new EntityDoesNotExistException(
                    "Feedback question of id " + questionId + " is not a question for session "+ feedbackSessionName + ".");
        }
        return true;
    }
    
    private boolean isFeedbackResponseOfQuestion(String questionId, String responseId) throws EntityDoesNotExistException{
        FeedbackResponseAttributes response = frLogic.getFeedbackResponse(responseId);
        if(!response.feedbackQuestionId.equals(questionId)){
            throw new EntityDoesNotExistException(
                    "Feedback response of id " + responseId + " is not a response for question of id "+ questionId + ".");
        }
        return true;
    }
}
