package teammates.test.cases.storage;

import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNotNull;
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
import teammates.test.cases.logic.LogicTest;
import teammates.test.driver.AssertHelper;

public class CommentsDbTest extends BaseComponentTestCase {
	
	private CommentsDb commentsDb = new CommentsDb();
	
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
		try {
			commentsDb.createEntity(c);
			Assert.fail();
		} catch (InvalidParametersException e) {
			AssertHelper.assertContains(
					String.format(COURSE_ID_ERROR_MESSAGE, c.courseId, REASON_INCORRECT_FORMAT),
					e.getMessage());
		}

		LogicTest.verifyAbsentInDatastore(c);

		______TS("success : valid params");

		c.courseId = "course-id";
		commentsDb.createEntity(c);
		LogicTest.verifyPresentInDatastore(c);
		
		______TS("success: another comment with different text");
		
		c.commentText = new Text("Another comment");
		commentsDb.createEntity(c);
		LogicTest.verifyPresentInDatastore(c);
		
		______TS("null params check");
		try {
			commentsDb.createEntity(null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testGetComment() throws InvalidParametersException, EntityDoesNotExistException {

		CommentAttributes c = createNewComment();
		
		______TS("typical success case: existent");
		CommentAttributes retrieved = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
		assertNotNull(retrieved);
		assertNotNull(commentsDb.getCommentsForReceiver(retrieved.courseId, retrieved.receiverEmail));
		assertNotNull(commentsDb.getCommentsForGiverAndReceiver(retrieved.courseId, retrieved.giverEmail, retrieved.receiverEmail));
		
		______TS("non existant comment case");
		List<CommentAttributes> retrievedList = commentsDb.getCommentsForGiver("any-course-id", "non-existent@email.com");
		assertEquals(0, retrievedList.size());
		
		______TS("null params case");
		try {
			commentsDb.getCommentsForGiver(null, "valid@email.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
		}		
		try {
			commentsDb.getCommentsForReceiver("any-course-id", null);
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
		}
		try {
			commentsDb.getCommentsForGiverAndReceiver("any-course-id", null, "valid@email.com");
			Assert.fail();
		} catch (AssertionError a) {
			assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, a.getMessage());
		}
	}
	
	@Test
	public void testUpdateComment() throws InvalidParametersException, EntityDoesNotExistException {
		
		CommentAttributes c = new CommentAttributes();
		c.courseId = "course-id";
		c.giverEmail = "giver@mail.com";
		c.receiverEmail = "receiver@mail.com";
		c.commentText = new Text("The receiver has performed well on this project");
		
		______TS("null params");		
		try {
			commentsDb.updateComment(null);
			signalFailureToDetectException();
		} catch (AssertionError e) {
			AssertHelper.assertContains(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
		}
		
		______TS("invalid comment attributes");
		c.receiverEmail = "invalid receiver email";
		try {
			commentsDb.updateComment(c);
			signalFailureToDetectException();
		} catch (InvalidParametersException e) {
			assertEquals(
					String.format(EMAIL_ERROR_MESSAGE, c.receiverEmail, REASON_INCORRECT_FORMAT),
							e.getLocalizedMessage());
		}
		______TS("comment not exist");
		c.receiverEmail = "receiver@mail.com";
		c.setCommentId((long)-1); //non-existant comment
		
		try {
			commentsDb.updateComment(c);
			signalFailureToDetectException();
		} catch (EntityDoesNotExistException e) {
			AssertHelper.assertContains(CommentsDb.ERROR_UPDATE_NON_EXISTENT, e.getLocalizedMessage());
		}
		
		______TS("standard success case");
		CommentAttributes existing = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
		c.setCommentId(existing.getCommentId());
		c.createdAt = existing.createdAt;
		c.commentText = new Text("new comment");
		commentsDb.updateComment(c);
		LogicTest.verifyPresentInDatastore(c);
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
		LogicTest.verifyAbsentInDatastore(c);
		
		______TS("invalid delete non-existing comment fails silently");
		commentsDb.deleteEntity(currentComment); //currentComment doesn't exist anymore
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
			// Okay if it's already inside
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
