package teammates.test.cases.logic;

import static org.testng.Assert.assertNotEquals;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
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
    public void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(FeedbackResponseCommentsLogic.class);
    }
    
    @BeforeMethod
    public void refreshTestData() throws Exception {
        removeAndRestoreTypicalDataInDatastore();
    }
    
    @Test
    public void testCreateFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = new FeedbackResponseCommentAttributes();
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("fail: non-existent course");

        frComment.courseId = "no-such-course";
        
        verifyExceptionThrownFromCreateFrComment(frComment,
                "Trying to create feedback response comments for a course that does not exist.");
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("fail: giver is not instructor");
        
        frComment.giverEmail = "student2InCourse1@gmail.com";
        
        verifyExceptionThrownFromCreateFrComment(frComment,
                "User " + frComment.giverEmail + " is not a registered instructor for course " 
                + frComment.courseId + ".");
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("fail: giver is not an instructor for the course");
        
        frComment.giverEmail = "instructor1@course2.com";
        
        verifyExceptionThrownFromCreateFrComment(frComment,
                "User " + frComment.giverEmail + " is not a registered instructor for course " 
                + frComment.courseId + ".");
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("fail: feedback session is not a session for the course");

        frComment.feedbackSessionName = "Private feedback session";
        
        verifyExceptionThrownFromCreateFrComment(frComment,
                "Feedback session " + frComment.feedbackSessionName + " is not a session for course " 
                + frComment.courseId + ".");
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("typical successful case");

        frComment.setId(null);
        frComment.feedbackQuestionId = getQuestionIdInDataBundle("qn1InSession1InCourse1");
        frComment.feedbackResponseId = getResponseIdInDataBundle("response2ForQ1S1C1", "qn1InSession1InCourse1");
        
        frcLogic.createFeedbackResponseComment(frComment);
        TestHelper.verifyPresentInDatastore(frComment);
        
        ______TS("typical successful case: frComment already exists");
        frcLogic.createFeedbackResponseComment(frComment);
        List<FeedbackResponseCommentAttributes> actualFrComments = 
                frcLogic.getFeedbackResponseCommentForSession(frComment.courseId, frComment.feedbackSessionName);
        
        FeedbackResponseCommentAttributes actualFrComment = null;
        for (int i = 0; i < actualFrComments.size(); i++) {
            if (actualFrComments.get(i).commentText.equals(frComment.commentText)) {
                actualFrComment = actualFrComments.get(i);
                break;
            }
        }
        
        assertTrue(actualFrComment != null);
        
        //delete afterwards
        frcLogic.deleteFeedbackResponseComment(frComment);
    }

    @Test
    public void testGetFeedbackResponseComments() throws Exception {
        FeedbackResponseCommentAttributes frComment = new FeedbackResponseCommentAttributes();
        List<FeedbackResponseCommentAttributes> expectedFrComments = 
                new ArrayList<FeedbackResponseCommentAttributes>();
        this.restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("fail: invalid parameters");
        
        frComment.courseId = "invalid course id";
        frComment.giverEmail = "invalid giver email";

        verifyNullFromGetFrCommentForSession(frComment);
        verifyNullFromGetFrComment(frComment);
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("Typical successful case");
        
        List<FeedbackResponseCommentAttributes> actualFrComments = 
                frcLogic.getFeedbackResponseCommentForSession(
                                 frComment.courseId, frComment.feedbackSessionName);
        FeedbackResponseCommentAttributes actualFrComment = actualFrComments.get(0);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.giverEmail, actualFrComment.giverEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);
        
        ______TS("Typical successful case by feedback response comment details");
        
        actualFrComment = 
                frcLogic.getFeedbackResponseComment(
                                 frComment.feedbackResponseId, frComment.giverEmail, frComment.createdAt);
        
        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.giverEmail, actualFrComment.giverEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);
        
        ______TS("Typical successful case by feedback response id");
        
        actualFrComments = frcLogic.getFeedbackResponseCommentForResponse(frComment.feedbackResponseId);
        actualFrComment = actualFrComments.get(0);
        
        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.giverEmail, actualFrComment.giverEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);
        
        ______TS("Typical successful case by feedback response comment id");
        
        actualFrComment = frcLogic.getFeedbackResponseComment(frComment.getId());
        
        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(frComment.giverEmail, actualFrComment.giverEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);
        
        ______TS("Typical successful case for giver");
        
        actualFrComments = frcLogic.getFeedbackResponseCommentsForGiver(
                                            frComment.courseId, frComment.giverEmail);
        FeedbackResponseCommentAttributes tempFrComment = new FeedbackResponseCommentAttributes();
        this.restoreFrCommentFromDataBundle(tempFrComment, "comment1FromT1C1ToR1Q1S1C1");
        expectedFrComments.add(tempFrComment);
        tempFrComment = new FeedbackResponseCommentAttributes();
        this.restoreFrCommentFromDataBundle(tempFrComment, "comment1FromT1C1ToR1Q2S1C1");
        expectedFrComments.add(tempFrComment);
        tempFrComment = new FeedbackResponseCommentAttributes();
        this.restoreFrCommentFromDataBundle(tempFrComment, "comment1FromT1C1ToR1Q3S1C1");
        expectedFrComments.add(tempFrComment);
        
        assertEquals(expectedFrComments.size(), actualFrComments.size());
        
        for (int i = 0; i < expectedFrComments.size(); i++) {
            assertEquals(expectedFrComments.get(i).courseId, actualFrComments.get(i).courseId);
            assertEquals(expectedFrComments.get(i).giverEmail, actualFrComments.get(i).giverEmail);
            assertEquals(expectedFrComments.get(i).feedbackSessionName, 
                         actualFrComments.get(i).feedbackSessionName);
        }
        
    }
    
    @Test
    public void testUpdateFeedbackResponseComment() throws Exception {
        FeedbackResponseCommentAttributes frComment = new FeedbackResponseCommentAttributes();
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("fail: invalid params");
        
        frComment.courseId = "invalid course name";
        verifyExceptionThrownWhenUpdateFrComment(frComment, "not acceptable to TEAMMATES as a Course ID");
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        
        ______TS("typical success case");

        frComment.commentText = new Text("Updated feedback response comment");
        frcLogic.updateFeedbackResponseComment(frComment);
        TestHelper.verifyPresentInDatastore(frComment);
        List<FeedbackResponseCommentAttributes> actualFrComments = 
                frcLogic.getFeedbackResponseCommentForSession(frComment.courseId, frComment.feedbackSessionName);
        
        FeedbackResponseCommentAttributes actualFrComment = null;
        for(int i = 0; i < actualFrComments.size(); i++){
            if(actualFrComments.get(i).commentText.equals(frComment.commentText)){
                actualFrComment = actualFrComments.get(i);
                break;
            }
        }
        assertTrue(actualFrComment != null);
        
        ______TS("typical success case update feedback response comment giver email");
        
        String oldEmail = frComment.giverEmail;
        String updatedEmail = "newEmail@gmail.tmt";
        frcLogic.updateFeedbackResponseCommentsGiverEmail(frComment.courseId, oldEmail, updatedEmail);
        
        actualFrComment = frcLogic.getFeedbackResponseComment(
                                           frComment.feedbackResponseId, updatedEmail, frComment.createdAt);

        assertEquals(frComment.courseId, actualFrComment.courseId);
        assertEquals(updatedEmail, actualFrComment.giverEmail);
        assertEquals(frComment.feedbackSessionName, actualFrComment.feedbackSessionName);
        
        // reset email
        frcLogic.updateFeedbackResponseCommentsGiverEmail(frComment.courseId, updatedEmail, oldEmail);
    }
    
    @Test
    public void testDeleteFeedbackResponseComment() throws Exception {
        //create a frComment to delete
        FeedbackResponseCommentAttributes frComment = new FeedbackResponseCommentAttributes();
        restoreFrCommentFromDataBundle(frComment, "comment1FromT1C1ToR1Q1S1C1");
        frComment.setId(null);
        frComment.feedbackQuestionId = getQuestionIdInDataBundle("qn2InSession1InCourse1");
        frComment.feedbackResponseId = getResponseIdInDataBundle("response2ForQ2S1C1", "qn2InSession1InCourse1");
        
        frcLogic.createFeedbackResponseComment(frComment);
        
        ______TS("silent fail nothing to delete");

        frComment.feedbackResponseId = "invalid responseId";
        //without proper frCommentId and its feedbackResponseId,
        //it cannot be deleted
        frcLogic.deleteFeedbackResponseComment(frComment);

        FeedbackResponseCommentAttributes actualFrComment = 
                frcLogic.getFeedbackResponseCommentForSession(
                                 frComment.courseId, frComment.feedbackSessionName).get(0);
        TestHelper.verifyPresentInDatastore(actualFrComment);
        
        ______TS("typical success case");
        
        frcLogic.deleteFeedbackResponseComment(actualFrComment);
        TestHelper.verifyAbsentInDatastore(actualFrComment);
        

        ______TS("typical success case for response");
        
        FeedbackResponseCommentAttributes anotherFrComment = new FeedbackResponseCommentAttributes();
        restoreFrCommentFromDataBundle(anotherFrComment, "comment1FromT1C1ToR1Q2S1C1");
        TestHelper.verifyPresentInDatastore(anotherFrComment);
        frcLogic.deleteFeedbackResponseCommentsForResponse(anotherFrComment.feedbackResponseId);
        TestHelper.verifyAbsentInDatastore(anotherFrComment);
        
    }
    

    @Test
    public void testDeleteFeedbackResponseCommentFromCourse() throws Exception {
        
        ______TS("typical case");
        String courseId = "idOfTypicalCourse1";
        
        List<FeedbackResponseCommentAttributes> frcList 
            = frcLogic.getFeedbackResponseCommentForSession(courseId, "First feedback session");
        assertNotEquals(0, frcList.size());
        
        frcLogic.deleteFeedbackResponseCommentsForCourse(courseId);
        
        frcList = frcLogic.getFeedbackResponseCommentForSession(courseId, "First feedback session");
        assertEquals(0, frcList.size());
    }
    
    private void verifyExceptionThrownFromCreateFrComment(
            FeedbackResponseCommentAttributes frComment, String expectedMessage) 
            throws InvalidParametersException {
        try {
            frcLogic.createFeedbackResponseComment(frComment);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
    
    private void verifyNullFromGetFrCommentForSession(FeedbackResponseCommentAttributes frComment) {
        List<FeedbackResponseCommentAttributes> frCommentsGot = 
                frcLogic.getFeedbackResponseCommentForSession(frComment.courseId, frComment.feedbackSessionName);
        assertEquals(0, frCommentsGot.size());
    }
    
    private void verifyNullFromGetFrComment(FeedbackResponseCommentAttributes frComment) {
        FeedbackResponseCommentAttributes frCommentGot = 
                frcLogic.getFeedbackResponseComment(
                                 frComment.feedbackResponseId, frComment.giverEmail, frComment.createdAt);
        assertEquals(null, frCommentGot);
    }
    
    private void verifyExceptionThrownWhenUpdateFrComment(
            FeedbackResponseCommentAttributes frComment, String expectedString)
            throws EntityDoesNotExistException {
        try {
            frcLogic.updateFeedbackResponseComment(frComment);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(expectedString, e.getMessage());
        }
    }
    
    private void restoreFrCommentFromDataBundle(
            FeedbackResponseCommentAttributes frComment, String existingFrCommentInDataBundle) {
        
        FeedbackResponseCommentAttributes existingFrComment = 
                dataBundle.feedbackResponseComments.get(existingFrCommentInDataBundle);
        frComment.courseId = existingFrComment.courseId;
        frComment.giverEmail = existingFrComment.giverEmail;
        frComment.feedbackSessionName = existingFrComment.feedbackSessionName;
        frComment.feedbackQuestionId = existingFrComment.feedbackQuestionId;
        frComment.commentText = existingFrComment.commentText;
        frComment.createdAt = existingFrComment.createdAt;
        restoreFrCommentIdFromExistingOne(frComment, existingFrComment);
    }
    
    private void restoreFrCommentIdFromExistingOne(
            FeedbackResponseCommentAttributes frComment,
            FeedbackResponseCommentAttributes existingFrComment) {
        
        List<FeedbackResponseCommentAttributes> existingFrComments = 
                frcLogic.getFeedbackResponseCommentForSession(
                                 existingFrComment.courseId, 
                                 existingFrComment.feedbackSessionName);
        
        FeedbackResponseCommentAttributes existingFrCommentWithId = null;
        for (FeedbackResponseCommentAttributes c: existingFrComments) {
            if (c.commentText.equals(existingFrComment.commentText)) {
                existingFrCommentWithId = c;
                break;
            }
        }
        frComment.setId(existingFrCommentWithId.getId());
        frComment.feedbackResponseId = existingFrCommentWithId.feedbackResponseId;
    }
    
    private String getQuestionIdInDataBundle(String questionInDataBundle) {
        FeedbackQuestionAttributes question = dataBundle.feedbackQuestions.get(questionInDataBundle);
        question = fqLogic.getFeedbackQuestion(
                                   question.feedbackSessionName, question.courseId, question.questionNumber);
        return question.getId();
    }
    
    private String getResponseIdInDataBundle(String responseInDataBundle, String questionInDataBundle) {
        FeedbackResponseAttributes response = dataBundle.feedbackResponses.get(responseInDataBundle);
        response = frLogic.getFeedbackResponse(
                                   getQuestionIdInDataBundle(questionInDataBundle), 
                                   response.giverEmail, 
                                   response.recipientEmail);
        return response.getId();
    }
}
