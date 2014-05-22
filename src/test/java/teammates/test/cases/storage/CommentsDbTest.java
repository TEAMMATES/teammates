package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;

import java.util.Date;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.CommentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class CommentsDbTest extends BaseComponentTestCase {
    
    private CommentsDb commentsDb = new CommentsDb();
    private enum GetCommentsType { FOR_GIVER, FOR_RECEIVER, FOR_GIVER_AND_RECEIVER };
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(CommentsDb.class);
    }
    
    @Test
    public void testCreateComment() throws EntityAlreadyExistsException, InvalidParametersException {
        
        CommentAttributes c = new CommentAttributes();
        c.courseId = "course-id";
        c.giverEmail = "giver@mail.com";
        c.receiverEmail = "receiver@mail.com";
        c.createdAt = new Date();
        c.commentText = new Text("The receiver has performed well on this project");

        ______TS("fail : invalid params"); 
        c.courseId = "invalid id space";
        verifyExceptionThrownFromCreateEntity(c, 
                String.format(COURSE_ID_ERROR_MESSAGE, c.courseId, REASON_INCORRECT_FORMAT));

        TestHelper.verifyAbsentInDatastore(c);

        ______TS("success : valid params");

        c.courseId = "course-id";
        commentsDb.createEntity(c);
        TestHelper.verifyPresentInDatastore(c);
        
        ______TS("success: another comment with different text");
        
        c.commentText = new Text("Another comment");
        commentsDb.createEntity(c);
        TestHelper.verifyPresentInDatastore(c);
        
        ______TS("null params check");
        verifyExceptionThrownFromCreateEntity(null,
                Const.StatusCodes.DBLEVEL_NULL_INPUT);
    }
    
    @Test
    public void testGetComment() throws InvalidParametersException, EntityDoesNotExistException {

        CommentAttributes c = createNewComment();
        
        ______TS("typical success case: existent");
        CommentAttributes retrievedComment = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
        assertNotNull(retrievedComment);
        assertNotNull(commentsDb.getCommentsForReceiver(
                retrievedComment.courseId, retrievedComment.receiverEmail));
        assertNotNull(commentsDb.getCommentsForGiverAndReceiver(
                retrievedComment.courseId, retrievedComment.giverEmail, retrievedComment.receiverEmail));
        
        CommentAttributes anotherRetrievedComment = commentsDb.getComment(retrievedComment.getCommentId());
        assertEquals(retrievedComment.commentText, anotherRetrievedComment.commentText);
        assertEquals(retrievedComment.giverEmail, anotherRetrievedComment.giverEmail);
        assertEquals(retrievedComment.receiverEmail, anotherRetrievedComment.receiverEmail);
        assertEquals(retrievedComment.courseId, anotherRetrievedComment.courseId);
        
        ______TS("non existant comment case");
        List<CommentAttributes> retrievedList = commentsDb.getCommentsForGiver("any-course-id", "non-existent@email.com");
        assertEquals(0, retrievedList.size());
        
        long nonExistId = -1;
        c = commentsDb.getComment(nonExistId);
        assertNull(c);
        
        ______TS("null params case");
        retrievedComment.courseId = null;
        verifyExceptionThrownFromGetComments(retrievedComment, GetCommentsType.FOR_GIVER,
                Const.StatusCodes.DBLEVEL_NULL_INPUT);

        retrievedComment.courseId = "any-course-id";
        retrievedComment.giverEmail = null;
        retrievedComment.receiverEmail = null;
        verifyExceptionThrownFromGetComments(retrievedComment, GetCommentsType.FOR_RECEIVER,
                Const.StatusCodes.DBLEVEL_NULL_INPUT);

        verifyExceptionThrownFromGetComments(retrievedComment, GetCommentsType.FOR_GIVER_AND_RECEIVER,
                Const.StatusCodes.DBLEVEL_NULL_INPUT);
    }

    @Test
    public void testUpdateComment() throws InvalidParametersException, EntityDoesNotExistException {
        
        CommentAttributes c = new CommentAttributes();
        c.courseId = "course-id";
        c.giverEmail = "giver@mail.com";
        c.receiverEmail = "receiver@mail.com";
        c.commentText = new Text("The receiver has performed well on this project");
        verifyExceptionThrownFromUpdateComment(null,
                Const.StatusCodes.DBLEVEL_NULL_INPUT);
        
        ______TS("invalid comment attributes");
        c.receiverEmail = "invalid receiver email";
        verifyExceptionThrownFromUpdateComment(c, 
                String.format(EMAIL_ERROR_MESSAGE, c.receiverEmail, REASON_INCORRECT_FORMAT));
        
        ______TS("comment not exist");
        c.receiverEmail = "receiver@mail.com";
        c.setCommentId((long)-1); //non-existant comment
        verifyExceptionThrownFromUpdateComment(c, 
                CommentsDb.ERROR_UPDATE_NON_EXISTENT);
        
        ______TS("standard success case");
        CommentAttributes existing = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
        c.setCommentId(existing.getCommentId());
        c.createdAt = existing.createdAt;
        c.commentText = new Text("new comment");
        commentsDb.updateComment(c);
        TestHelper.verifyPresentInDatastore(c);
    }
    
    @Test
    public void testDeleteComment() throws InvalidParametersException, EntityDoesNotExistException {
        CommentAttributes c = new CommentAttributes();
        c.courseId = "course-id";
        c.giverEmail = "giver@mail.com";
        c.receiverEmail = "receiver@mail.com";
        c.createdAt = new Date();
        c.commentText = new Text("The receiver has performed well on this project");
        
        ______TS("standard delete existing comment");
        CommentAttributes currentComment = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
        c.setCommentId(currentComment.getCommentId());
        commentsDb.deleteEntity(currentComment);
        TestHelper.verifyAbsentInDatastore(c);
        
        ______TS("invalid delete non-existing comment fails silently");
        commentsDb.deleteEntity(currentComment); //currentComment doesn't exist anymore
    }
    
    private void verifyExceptionThrownFromCreateEntity(CommentAttributes comment, String expectedMessage)
            throws EntityAlreadyExistsException {
        try {
            commentsDb.createEntity(comment);
            Assert.fail();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    expectedMessage,
                    e.getMessage());
        } catch (AssertionError e){
            AssertHelper.assertContains(
                    expectedMessage,
                    e.getMessage());
        }
    }
    
    private void verifyExceptionThrownFromGetComments(
            CommentAttributes comment, GetCommentsType getCommentsType,
            String expectedMessage) {
        try {
            switch(getCommentsType){
            case FOR_GIVER:
                commentsDb.getCommentsForGiver(comment.courseId, comment.giverEmail);
                break;
            case FOR_RECEIVER:
                commentsDb.getCommentsForReceiver(comment.courseId, comment.receiverEmail);
                break;
            case FOR_GIVER_AND_RECEIVER:
                commentsDb.getCommentsForGiverAndReceiver(comment.courseId, 
                        comment.giverEmail, comment.receiverEmail);
                break;
            }
            signalFailureToDetectException();
        } catch (AssertionError e) {
            assertEquals(expectedMessage, e.getMessage());
        }
    }
    
    private void verifyExceptionThrownFromUpdateComment(CommentAttributes comment,
            String expectedMessage) {
        try {
            commentsDb.updateComment(comment);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(expectedMessage, e.getLocalizedMessage());
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(expectedMessage, e.getLocalizedMessage());
        } catch (AssertionError e) {
            AssertHelper.assertContains(expectedMessage, e.getLocalizedMessage());
        }
    }
    
    private CommentAttributes createNewComment() throws InvalidParametersException {
        CommentAttributes c = new CommentAttributes();
        c.courseId = "course-id";
        c.giverEmail = "giver@mail.com";
        c.receiverEmail = "receiver@mail.com";
        c.createdAt = new Date();
        c.commentText = new Text("The receiver has performed well on this project");
        
        try {
            commentsDb.createEntity(c);
        } catch (EntityAlreadyExistsException e) {
            ignoreExpectedException();
        }
        
        return c;
    }
    
    @AfterMethod
    public void caseTearDown() throws Exception {
        turnLoggingDown(CommentsDb.class);
    }
    
    @AfterClass
    public static void tearDownClass() throws Exception {
        turnLoggingDown(CommentsDb.class);
    }
}
