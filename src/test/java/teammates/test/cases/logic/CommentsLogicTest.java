package teammates.test.cases.logic;

import static org.testng.AssertJUnit.assertEquals;

import java.util.Date;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.DataBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.CommentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class CommentsLogicTest extends BaseComponentTestCase {

    private CommentsLogic commentsLogic = new CommentsLogic();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(CommentsLogic.class);
    }

    
    @Test
    public void testCreateComment() throws Exception {
        
        restoreTypicalDataInDatastore();
        CommentAttributes existingComment1 = dataBundle.comments.get("comment1FromI1C1toS1C1");
        
        ______TS("fail: non-existent course");
        CommentAttributes c = new CommentAttributes();
        c.courseId = "no-such-course";
        c.giverEmail = existingComment1.giverEmail;
        c.receiverEmail = existingComment1.receiverEmail;
        c.commentText = existingComment1.commentText;

        verifyExceptionThrownFromCreateFrComment(c, 
                "Trying to create comments for a course that does not exist.");
        
        ______TS("fail: giver is not instructor");
        c.courseId = existingComment1.courseId;
        c.giverEmail = "student2InCourse1@gmail.com";
        c.commentText = new Text("Invalid comment from student to student");
        
        verifyExceptionThrownFromCreateFrComment(c,
                "User " + c.giverEmail + " is not a registered instructor for course " 
                + c.courseId + ".");
        
        ______TS("fail: giver is not an instructor for the course");
        c.giverEmail = "instructor1@course2.com";
        c.commentText = new Text("Invalid Comment from instructor1Course2 to student1Course1");

        verifyExceptionThrownFromCreateFrComment(c,
                "User " + c.giverEmail + " is not a registered instructor for course " 
                + c.courseId + ".");
        
        ______TS("fail: receiver is not student");
        c.giverEmail = existingComment1.giverEmail;
        c.receiverEmail = "instructor1@course2.com";
        c.commentText = new Text("Invalid comment from inst1c1 to inst1c2");

        verifyExceptionThrownFromCreateFrComment(c, 
                "User " + c.receiverEmail + " is not a registered student for course " + c.courseId + ".");
        
        ______TS("fail: receiver is not a student for the course");
        c.receiverEmail = "student1InCourse2@gmail.com";
        c.commentText = new Text("Invalid Comment from instructor1Course1 to student1Course2");

        verifyExceptionThrownFromCreateFrComment(c, 
                "User " + c.receiverEmail + " is not a registered student for course " + c.courseId + ".");
        
        ______TS("typical case");
        
        c.courseId = "idOfTypicalCourse1";
        c.giverEmail = "instructor2@course1.com";
        c.receiverEmail = "student3InCourse1@gmail.com";
        c.createdAt = new Date();
        c.commentText = new Text("New Comment from instructor2 to student3 in course 1");
        
        commentsLogic.createComment(c);
        TestHelper.verifyPresentInDatastore(c);
        
        //delete afterwards
        commentsLogic.deleteComment(c);
    }

    @Test
    public void testGetComments() throws Exception {
        
        restoreTypicalDataInDatastore();
        CommentAttributes existingComment1 = dataBundle.comments.get("comment1FromI1C1toS1C1");
        CommentAttributes existingComment2 = dataBundle.comments.get("comment1FromI3C1toS2C1");
        
        ______TS("fail: invalid parameters");
        CommentAttributes c = new CommentAttributes();
        c.courseId = "invalid course id";
        c.giverEmail = existingComment1.giverEmail;
        c.receiverEmail = existingComment1.receiverEmail;
        c.commentText = existingComment1.commentText;

        verifyExceptionThrownFromGetCommentsForGiver(c,
                "Trying to get comments for a course that does not exist.");
        
        ______TS("fail: non-existent course");
        c.courseId = "no-such-course";
        
        verifyExceptionThrownFromGetCommentsForGiver(c,
                "Trying to get comments for a course that does not exist.");
        
        verifyExceptionThrownFromGetCommentsForReceiver(c,
                "Trying to get comments for a course that does not exist.");
        
        verifyExceptionThrownFromGetCommentsForGiverAndReceiver(c,
                "Trying to get comments for a course that does not exist.");
        
        ______TS("success: get comment for giver");
        
        c.courseId = "idOfTypicalCourse1";
        List<CommentAttributes> commentsForGiver = commentsLogic.getCommentsForGiver(c.courseId, c.giverEmail);
        assertEquals(2, commentsForGiver.size());
        for(CommentAttributes comment : commentsForGiver){
            assertEquals(c.courseId, comment.courseId);
            assertEquals(c.giverEmail, comment.giverEmail);
        }
        
        ______TS("success: get comment for receiver");
        
        List<CommentAttributes> commentsForReceiver = commentsLogic.getCommentsForReceiver(c.courseId, c.receiverEmail);
        assertEquals(2, commentsForReceiver.size());
        for(CommentAttributes comment : commentsForReceiver){
            assertEquals(c.courseId, comment.courseId);
            assertEquals(c.receiverEmail, comment.receiverEmail);
        }
        
        ______TS("success: get comment for giver and receiver");
        c.courseId = existingComment2.courseId;
        c.giverEmail = existingComment2.giverEmail;
        c.receiverEmail = existingComment2.receiverEmail;
        c.commentText = existingComment2.commentText;

        List<CommentAttributes> commentsForGiverAndReceiver = 
                commentsLogic.getCommentsForGiverAndReceiver(c.courseId, c.giverEmail, c.receiverEmail);
        assertEquals(1, commentsForGiverAndReceiver.size());
        CommentAttributes actual = commentsForGiverAndReceiver.get(0);
        assertEquals(c.courseId, actual.courseId);
        assertEquals(c.giverEmail, actual.giverEmail);
        assertEquals(c.receiverEmail, actual.receiverEmail);
        assertEquals(c.commentText, actual.commentText);
    }

    @Test
    public void testUpdateComment() throws Exception{
        restoreTypicalDataInDatastore();
        CommentAttributes existingComment = dataBundle.comments.get("comment1FromI3C1toS2C1");
        
        ______TS("fail: invalid params");
        CommentAttributes c = new CommentAttributes();
        c.courseId = "invalid course name";
        c.giverEmail = existingComment.giverEmail;
        c.receiverEmail = existingComment.receiverEmail;
        c.createdAt = existingComment.createdAt;
        c.commentText = existingComment.commentText;
        
        verifyExceptionThrownFromUpdateComment(c, 
                "not acceptable to TEAMMATES as a Course ID");
        
        ______TS("fail: non existent entity");
        c.courseId = "no-such-course";
        
        verifyExceptionThrownFromUpdateComment(c, 
                "Trying to update non-existent Comment");
        
        ______TS("typical success case");
        c.courseId = existingComment.courseId;
        
        Long id = commentsLogic.getCommentsForGiverAndReceiver(c.courseId, c.giverEmail, c.receiverEmail).get(0).getCommentId();
        c.setCommentId(id);
        c.commentText = new Text("Edited comment from Instructor 3 Course 1 to Student 2 Course 1");

        commentsLogic.updateComment(c);
        TestHelper.verifyPresentInDatastore(c);
        
        List<CommentAttributes> actual = commentsLogic.getCommentsForGiverAndReceiver(c.courseId, c.giverEmail, c.receiverEmail);
        assertEquals(1, actual.size());
        assertEquals(c.commentText, actual.get(0).commentText);
    }

    @Test
    public void testDeleteComment() throws Exception{
        restoreTypicalDataInDatastore();
        CommentAttributes existingComment1 = dataBundle.comments.get("comment1FromI1C1toS1C1");
        
        ______TS("silent fail nothing to delete");
        CommentAttributes c = new CommentAttributes();
        c.courseId = "no-such-course";
        c.giverEmail = existingComment1.giverEmail;
        c.receiverEmail = existingComment1.receiverEmail;
        c.createdAt = existingComment1.createdAt;
        c.commentText = existingComment1.commentText;
        
        commentsLogic.deleteComment(c);
        c.courseId = existingComment1.courseId;
        TestHelper.verifyPresentInDatastore(c);
        
        ______TS("typical success case");
        
        commentsLogic.deleteComment(c);
        TestHelper.verifyAbsentInDatastore(c);
    }
    
    private void verifyExceptionThrownFromCreateFrComment(
            CommentAttributes comment, String message) 
            throws InvalidParametersException, EntityAlreadyExistsException {
        try{
            commentsLogic.createComment(comment);
            signalFailureToDetectException();
        } catch(EntityDoesNotExistException e){
            assertEquals(message, e.getMessage());
        }
    }
    
    private void verifyExceptionThrownFromGetCommentsForGiver(
            CommentAttributes comment, String message) {
        try{
            commentsLogic.getCommentsForGiver(comment.courseId, comment.giverEmail);
            signalFailureToDetectException();
        } catch(EntityDoesNotExistException e){
            assertEquals(message, e.getMessage());
        }
    }
    
    private void verifyExceptionThrownFromGetCommentsForReceiver(
            CommentAttributes comment, String message) {
        try{
            commentsLogic.getCommentsForReceiver(comment.courseId, comment.receiverEmail);
            signalFailureToDetectException();
        } catch(EntityDoesNotExistException e){
            assertEquals(message, e.getMessage());
        }
    }
    
    private void verifyExceptionThrownFromGetCommentsForGiverAndReceiver(
            CommentAttributes comment, String message) {
        try{
            commentsLogic.getCommentsForGiverAndReceiver(
                    comment.courseId, comment.giverEmail, comment.receiverEmail);
            signalFailureToDetectException();
        } catch(EntityDoesNotExistException e){
            assertEquals(message, e.getMessage());
        }
    }
    
    private void verifyExceptionThrownFromUpdateComment(CommentAttributes c, String message)
            throws EntityDoesNotExistException {
        try{
            commentsLogic.updateComment(c);
        } catch(InvalidParametersException e){
            AssertHelper.assertContains(message, e.getMessage());
        } catch(EntityDoesNotExistException e){
            AssertHelper.assertContains(message, e.getMessage());
        }
    }
}
