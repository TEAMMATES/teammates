package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertTrue;
import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.storage.api.CommentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;
import teammates.test.util.TestHelper;

public class CommentsDbTest extends BaseComponentTestCase {
    
    private String courseId = "CDT.courseId";
    private String giverEmail = "CDT.giver@mail.com";
    private String recipientEmail = "CDT.receiver@mail.com";
    private String commentText = "comment text";
    
    private CommentsDb commentsDb = new CommentsDb();
    
    @BeforeClass
    public static void setupClass() throws Exception {
        printTestClassHeader();
        turnLoggingUp(CommentsDb.class);
    }
    
    @Test
    public void testCreateComment() throws EntityAlreadyExistsException, InvalidParametersException {
        
        CommentAttributes c = createNewComment();

        ______TS("fail : invalid params"); 
        c.courseId = "invalid id with space";
        try{
            commentsDb.createEntity(c);
        } catch (InvalidParametersException e){
            assertEquals(String.format(COURSE_ID_ERROR_MESSAGE, c.courseId, REASON_INCORRECT_FORMAT), 
                    e.getLocalizedMessage());
        }

        TestHelper.verifyAbsentInDatastore(c);

        ______TS("success : valid params");

        c.courseId = "course-id";
        commentsDb.createEntity(c);
        TestHelper.verifyPresentInDatastore(c);
        commentsDb.deleteEntity(c);
        
        ______TS("success: another comment with different text");
        
        c.createdAt = new Date();
        commentsDb.createEntity(c);
        TestHelper.verifyPresentInDatastore(c);
        commentsDb.deleteEntity(c);
        
        ______TS("null params check");
        verifyExceptionThrownFromCreateEntity(null,
                Const.StatusCodes.DBLEVEL_NULL_INPUT);
    }
    
    @Test
    public void testGetComment() throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        CommentAttributes c = createNewComment();
        commentsDb.createEntity(c);
        
        ______TS("typical success case: existent");
        CommentAttributes retrievedComment = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
        assertNotNull(retrievedComment);
        assertNotNull(commentsDb.getCommentsForReceiver(
                c.courseId, c.recipientType, recipientEmail));
        
        CommentAttributes anotherRetrievedComment = commentsDb.getComment(retrievedComment.getCommentId());
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.getComment(retrievedComment);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.
                getCommentsForGiverAndStatus(retrievedComment.courseId, retrievedComment.giverEmail, retrievedComment.status).get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        retrievedComment.status = CommentStatus.DRAFT;
        retrievedComment.showCommentTo = new ArrayList<CommentRecipientType>();
        retrievedComment.showCommentTo.add(CommentRecipientType.PERSON);
        retrievedComment.showCommentTo.add(CommentRecipientType.TEAM);
        retrievedComment.showCommentTo.add(CommentRecipientType.SECTION);
        retrievedComment.showCommentTo.add(CommentRecipientType.COURSE);
        commentsDb.updateComment(retrievedComment);
        
        anotherRetrievedComment = commentsDb.
                getCommentDrafts(retrievedComment.giverEmail).get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.
                getCommentsForCommentViewer(retrievedComment.courseId, CommentRecipientType.PERSON).get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.
                getCommentsForCommentViewer(retrievedComment.courseId, CommentRecipientType.TEAM).get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.
                getCommentsForCommentViewer(retrievedComment.courseId, CommentRecipientType.SECTION).get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.
                getCommentsForCommentViewer(retrievedComment.courseId, CommentRecipientType.COURSE).get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        ______TS("non existant comment case");
        List<CommentAttributes> retrievedList = commentsDb.getCommentsForGiver("any-course-id", "non-existent@email.com");
        assertEquals(0, retrievedList.size());
        
        long nonExistId = -1;
        c = commentsDb.getComment(nonExistId);
        assertNull(c);
        
        ______TS("null params case");
        retrievedComment.courseId = null;
        try{
            commentsDb.getCommentsForGiver(retrievedComment.courseId, retrievedComment.giverEmail);
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        retrievedComment.courseId = "any-course-id";
        retrievedComment.giverEmail = null;
        retrievedComment.recipients = null;
        try{
            commentsDb.getCommentsForReceiver(retrievedComment.courseId, retrievedComment.recipientType, retrievedComment.giverEmail);
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
    }

    private void compareComments(CommentAttributes retrievedComment,
            CommentAttributes anotherRetrievedComment) {
        assertEquals(retrievedComment.commentText, anotherRetrievedComment.commentText);
        assertEquals(retrievedComment.giverEmail, anotherRetrievedComment.giverEmail);
        assertEquals(retrievedComment.recipients, anotherRetrievedComment.recipients);
        assertEquals(retrievedComment.courseId, anotherRetrievedComment.courseId);
    }

    @Test
    public void testUpdateComment() throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        CommentAttributes c = createNewComment();
        commentsDb.createEntity(c);
        
        ______TS("invalid comment attributes");
        try{
            commentsDb.updateComment(null);
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("invalid comment attributes");
        c.recipients = new HashSet<String>();
        c.recipients.add("invalid receiver email");
        try{
            commentsDb.updateComment(c);
        } catch(InvalidParametersException e) {
            assertEquals(String.format(EMAIL_ERROR_MESSAGE, "invalid receiver email", REASON_INCORRECT_FORMAT), 
                    e.getLocalizedMessage());
        }
        
        ______TS("comment not exist");
        c.recipients = new HashSet<String>();
        c.recipients.add("receiver@mail.com");
        c.setCommentId((long)-1); //non-existent comment
        try{
            commentsDb.updateComment(c);
        } catch(EntityDoesNotExistException e) {
            assertTrue(e.getLocalizedMessage().contains(CommentsDb.ERROR_UPDATE_NON_EXISTENT));
        }
        
        ______TS("standard success case");
        CommentAttributes existing = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
        c.setCommentId(existing.getCommentId());
        c.createdAt = existing.createdAt;
        c.commentText = new Text("new comment");
        commentsDb.updateComment(c);
        TestHelper.verifyPresentInDatastore(c);
    }
    
    @Test
    public void testUpdateInstructorEmailAndStudentEmail() 
            throws InvalidParametersException, EntityAlreadyExistsException, EntityDoesNotExistException {
        
        String courseId1 = "CDT.upd.courseId1";
        String courseId2 = "CDT.upd.courseId2";
        String giverEmail1 = "CDT.upd.giverInstr1@mail.com";
        String giverEmail2 = "CDT.upd.giverInstr2@mail.com";
        String giverEmailNew = "CDT.upd.giverInstrNew@mail.com";
        String recipientEmail1 = "CDT.upd.receiverStudent1@mail.com";
        String recipientEmail2 = "CDT.upd.receiverStudent2@mail.com";
        String recipientEmailNew = "CDT.upd.receiverStudentNew@mail.com";
        
        courseId = courseId1;
        giverEmail = giverEmail1;
        CommentAttributes c = createNewComment();
        commentsDb.createEntity(c);
        recipientEmail = recipientEmailNew;
        c = createNewComment();
        commentsDb.createEntity(c);
        courseId = courseId2;
        giverEmail = giverEmail1;
        c = createNewComment();
        commentsDb.createEntity(c);
        
        giverEmail = "CDT.giver@mail.com";
        courseId = courseId1;
        recipientEmail = recipientEmail1;
        c = createNewComment();
        commentsDb.createEntity(c);
        giverEmail = giverEmailNew;
        c = createNewComment();
        commentsDb.createEntity(c);
        courseId = courseId2;
        recipientEmail = recipientEmail1;
        c = createNewComment();
        commentsDb.createEntity(c);
        
        ______TS("success: update instructor email");
        
        // before update: 2 comments for this giver email
        assertEquals(2, commentsDb.getCommentsForGiver(courseId1, giverEmail1).size());
        commentsDb.updateInstructorEmail(courseId1, giverEmail1, giverEmail2);
        // after update: 2 comments for new giver email and others are not affected
        assertEquals(0, commentsDb.getCommentsForGiver(courseId1, giverEmail1).size());
        assertEquals(2, commentsDb.getCommentsForGiver(courseId1, giverEmail2).size());
        assertEquals(1, commentsDb.getCommentsForGiver(courseId2, giverEmail1).size());
        
        ______TS("success: update student email");
        
        // before update: 2 comments for this recipient email
        assertEquals(2, commentsDb.getCommentsForReceiver(courseId1, CommentRecipientType.PERSON, recipientEmail1).size());
        commentsDb.updateStudentEmail(courseId1, recipientEmail1, recipientEmail2);
        // after update: 2 comments for new giver email and others are not affected
        assertEquals(0, commentsDb.getCommentsForReceiver(courseId1, CommentRecipientType.PERSON, recipientEmail1).size());
        assertEquals(2, commentsDb.getCommentsForReceiver(courseId1, CommentRecipientType.PERSON, recipientEmail2).size());
        assertEquals(1, commentsDb.getCommentsForReceiver(courseId2, CommentRecipientType.PERSON, recipientEmail1).size());
        
        ______TS("failure: null input when updating instr email");
        
        try {
            commentsDb.updateInstructorEmail(courseId1, null, giverEmail2);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        try {
            commentsDb.updateInstructorEmail(null, giverEmail1, giverEmail2);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        try {
            commentsDb.updateInstructorEmail(courseId1, giverEmail1, null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("failure: null input when updating student email");
        
        try {
            commentsDb.updateStudentEmail(courseId1, null, giverEmail2);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        try {
            commentsDb.updateStudentEmail(null, giverEmail1, giverEmail2);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        try {
            commentsDb.updateStudentEmail(courseId1, giverEmail1, null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e){
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        // restore variable
        courseId = "CDT.courseId";
        giverEmail = "CDT.giver@mail.com";
        recipientEmail = "CDT.receiver@mail.com";
    }
    
    @Test
    public void testDeleteComment() throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        
        CommentAttributes c = createNewComment();
        commentsDb.createEntity(c);
        
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
    
    private CommentAttributes createNewComment() {
        CommentAttributes c = new CommentAttributes();
        c.courseId = courseId;
        c.giverEmail = giverEmail;
        c.recipientType = CommentRecipientType.PERSON;
        c.recipients = new HashSet<String>();
        c.recipients.add(recipientEmail);
        c.createdAt = new Date();
        c.commentText = new Text(commentText);
        c.status = CommentStatus.FINAL;
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
