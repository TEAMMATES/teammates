package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.FeedbackQuestionsLogic;
import teammates.logic.core.FeedbackResponseCommentsLogic;
import teammates.logic.core.FeedbackResponsesLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class FeedbackResponseCommentsLogicTest extends BaseComponentTestCase {

    private FeedbackResponseCommentsLogic frcLogic = FeedbackResponseCommentsLogic.inst();
    private FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
    private FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackResponseCommentsLogic.class);
    }
    
    @Test
    public void testAll() throws Exception {
        testCreateComment();
        testGetComments();
        testUpdateComment();
        testDeleteComment();
    }
    
    private void testCreateComment() throws Exception {
        
        restoreTypicalDataInDatastore();
        FeedbackResponseCommentAttributes existingFrComment = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        
        int questionNumber = 1;
        FeedbackQuestionAttributes commonQuestion = 
                fqLogic.getFeedbackQuestion(
                        existingFrComment.feedbackSessionName, 
                        existingFrComment.courseId, 
                        questionNumber);
        
        String responseGiverEmail = "student2InCourse1@gmail.com";
        String responseRecipient = "student2InCourse1@gmail.com";
        FeedbackResponseAttributes commonResponse =
                frLogic.getFeedbackResponse(
                        commonQuestion.getId(), 
                        responseGiverEmail, 
                        responseRecipient);
        
        ______TS("fail: non-existent course");
        
        FeedbackResponseCommentAttributes frComment = new FeedbackResponseCommentAttributes();
        frComment.courseId = "no-such-course";
        frComment.giverEmail = existingFrComment.giverEmail;
        
        verifyExceptionThrownWhenCreateFrComment(frComment,
                "Trying to get/create feedback response comments for a course that does not exist.");
        
        
        ______TS("fail: giver is not instructor");
        
        frComment.courseId = existingFrComment.courseId;
        frComment.giverEmail = "student2InCourse1@gmail.com";
        
        verifyExceptionThrownWhenCreateFrComment(frComment,
                "User student2InCourse1@gmail.com is not a registered instructor for course idOfTypicalCourse1.");
        
        
        ______TS("fail: giver is not an instructor for the course");
        
        frComment.giverEmail = "instructor1@course2.com";
        
        verifyExceptionThrownWhenCreateFrComment(frComment,
                "User instructor1@course2.com is not a registered instructor for course idOfTypicalCourse1.");
        
        
        ______TS("typical successful case");
        
        frComment.courseId = "idOfTypicalCourse1";
        frComment.giverEmail = "instructor2@course1.com";
        frComment.feedbackSessionName = existingFrComment.feedbackSessionName;
        frComment.feedbackQuestionId = commonQuestion.getId();
        frComment.feedbackResponseId = commonResponse.getId();
        frComment.commentText = existingFrComment.commentText;
        frComment.createdAt = new Date();
        frComment.commentText = new Text("New FeedbackResponseComment from instructor2 in course 1");
        
        frcLogic.createFeedbackResponseComment(frComment);
        TestHelper.verifyPresentInDatastore(frComment);
        
        ______TS("typical successful case: frComment already exists");
        
        frComment.commentText = new Text("Already existed FeedbackResponseComment from instructor2 in course 1");
        
        frcLogic.createFeedbackResponseComment(frComment);
        TestHelper.verifyPresentInDatastore(frComment);
        FeedbackResponseCommentAttributes actualFrComment = 
                frcLogic.getFeedbackResponseCommentForSession(
                        frComment.courseId, 
                        frComment.feedbackSessionName).get(1);
        
        assertEquals(frComment.commentText, actualFrComment.commentText);
        
        //delete afterwards
        frcLogic.deleteFeedbackResponseComment(frComment);
    }
    
    private void testGetComments() throws Exception {

        FeedbackResponseCommentAttributes existingFrComment = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        
        
        ______TS("fail: invalid parameters");
        
        FeedbackResponseCommentAttributes frComment = new FeedbackResponseCommentAttributes();
        frComment.courseId = "invalid course id";
        frComment.giverEmail = "invalid giver email";

        verifyExceptionThrownWhenGetFrCommentForSession(frComment, 
                "Trying to get/create feedback response comments for a course that does not exist.");
        
        frComment.feedbackResponseId = existingFrComment.feedbackResponseId;
        frComment.createdAt = existingFrComment.createdAt;
        
        verifyNullReturnedWhenGetFrComment(frComment);
        
        
        ______TS("Typical successful case");
        
        frComment.courseId = existingFrComment.courseId;
        frComment.giverEmail = existingFrComment.giverEmail;
        frComment.feedbackSessionName = existingFrComment.feedbackSessionName;
        updateFrCommentIds(existingFrComment, frComment);
        
        List<FeedbackResponseCommentAttributes> actualFrComments = 
                frcLogic.getFeedbackResponseCommentForSession(
                        frComment.courseId, 
                        frComment.feedbackSessionName);
        FeedbackResponseCommentAttributes actualFrComment = actualFrComments.get(0);
        
        assertEquals(1, actualFrComments.size());
        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.giverEmail, actualFrComment.giverEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);
        
        actualFrComment = 
                frcLogic.getFeedbackResponseComment(
                        frComment.feedbackResponseId, frComment.giverEmail, frComment.createdAt);
        
        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.giverEmail, actualFrComment.giverEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);
    }
    
    private void testUpdateComment() throws Exception{
        FeedbackResponseCommentAttributes existingFrComment = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("fail: invalid params");
        FeedbackResponseCommentAttributes frComment = new FeedbackResponseCommentAttributes();
        frComment.courseId = "invalid course name";
        frComment.giverEmail = existingFrComment.giverEmail;
        frComment.feedbackSessionName = existingFrComment.feedbackSessionName;
        frComment.createdAt = existingFrComment.createdAt;
        frComment.commentText = existingFrComment.commentText;
        
        updateFrCommentIds(existingFrComment, frComment);
        verifyExceptionThrownWhenUpdateFrComment(frComment,
                "not acceptable to TEAMMATES as a Course ID");
        
        ______TS("typical success case");
        frComment.courseId = existingFrComment.courseId;

        frcLogic.updateFeedbackResponseComment(frComment);
        TestHelper.verifyPresentInDatastore(frComment);
    }
    
    private void testDeleteComment() throws Exception{
        FeedbackResponseCommentAttributes existingFrComment = dataBundle.feedbackResponseComments.get("comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("silent fail nothing to delete");
        FeedbackResponseCommentAttributes frComment = new FeedbackResponseCommentAttributes();
        frComment.courseId = existingFrComment.courseId;
        frComment.giverEmail = existingFrComment.giverEmail;
        frComment.feedbackSessionName = existingFrComment.feedbackSessionName;
        frComment.createdAt = existingFrComment.createdAt;
        frComment.commentText = existingFrComment.commentText;
        
        //without proper frCommentId and its feedbackResponseId,
        //it cannot be deleted
        frcLogic.deleteFeedbackResponseComment(frComment);
        
        //now update its frCommentId and its feedbackResponseId
        updateFrCommentIds(existingFrComment, frComment);
        TestHelper.verifyPresentInDatastore(frComment);
        
        
        ______TS("typical success case");
        
        frcLogic.deleteFeedbackResponseComment(frComment);
        TestHelper.verifyAbsentInDatastore(frComment);
    }
    
    private void verifyExceptionThrownWhenCreateFrComment(
            FeedbackResponseCommentAttributes frComment, String expectedMessage)
            throws InvalidParametersException {
        try{
            frcLogic.createFeedbackResponseComment(frComment);
            signalFailureToDetectException();
        } catch(EntityDoesNotExistException e){
            assertEquals(expectedMessage, e.getMessage());
        }
    }
    
    private void verifyExceptionThrownWhenGetFrCommentForSession(
            FeedbackResponseCommentAttributes frComment, String expectedMessage) {
        try{
            frcLogic.getFeedbackResponseCommentForSession(frComment.courseId, frComment.feedbackSessionName);
            signalFailureToDetectException();
        } catch(EntityDoesNotExistException e){
            assertEquals(expectedMessage, e.getMessage());
        }
    }
    
    private void verifyNullReturnedWhenGetFrComment(
            FeedbackResponseCommentAttributes frComment) {
        FeedbackResponseCommentAttributes frCommentGot = 
                frcLogic.getFeedbackResponseComment(frComment.feedbackResponseId, frComment.giverEmail,
                    frComment.createdAt);
        assertEquals(null, frCommentGot);
    }
    
    private void verifyExceptionThrownWhenUpdateFrComment(
            FeedbackResponseCommentAttributes frComment, String expectedString)
            throws EntityDoesNotExistException {
        try{
            frcLogic.updateFeedbackResponseComment(frComment);
            signalFailureToDetectException();
        } catch(InvalidParametersException e){
            AssertHelper.assertContains(expectedString, e.getMessage());
        }
    }
    
    private void updateFrCommentIds(
            FeedbackResponseCommentAttributes existingFrComment,
            FeedbackResponseCommentAttributes frComment)
            throws EntityDoesNotExistException {
        FeedbackResponseCommentAttributes existingFrCommentWithId = 
                frcLogic.getFeedbackResponseCommentForSession(
                        existingFrComment.courseId, 
                        existingFrComment.feedbackSessionName).get(0);
        frComment.setId(existingFrCommentWithId.getId());
        frComment.feedbackResponseId = existingFrCommentWithId.feedbackResponseId;
    }
}
