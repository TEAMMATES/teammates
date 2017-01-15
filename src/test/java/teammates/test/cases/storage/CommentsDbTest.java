package teammates.test.cases.storage;

import static teammates.common.util.FieldValidator.COURSE_ID_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.EMAIL_ERROR_MESSAGE;
import static teammates.common.util.FieldValidator.REASON_INCORRECT_FORMAT;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Const;
import teammates.common.util.FieldValidator;
import teammates.storage.api.CommentsDb;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;

public class CommentsDbTest extends BaseComponentTestCase {
    
    private String courseId = "CDT.courseId";
    private String giverEmail = "CDT.giver@mail.com";
    private String recipient = "CDT.receiver@mail.com";
    private String commentText = "comment text";
    private CommentParticipantType recipientType = CommentParticipantType.PERSON;
    
    private CommentsDb commentsDb = new CommentsDb();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
    }
    
    @Test
    public void testCreateComment() throws Exception {
        
        CommentAttributes c = createNewComment();

        ______TS("fail : invalid params");
        c.courseId = "invalid id with space";
        try {
            commentsDb.createEntity(c);
        } catch (InvalidParametersException e) {
            assertEquals(getPopulatedErrorMessage(
                             COURSE_ID_ERROR_MESSAGE, c.courseId,
                             FieldValidator.COURSE_ID_FIELD_NAME, REASON_INCORRECT_FORMAT,
                             FieldValidator.COURSE_ID_MAX_LENGTH),
                         e.getLocalizedMessage());
        }

        verifyAbsentInDatastore(c);

        ______TS("success : valid params");

        c.courseId = "course-id";
        commentsDb.createEntity(c);
        verifyPresentInDatastore(c);
        commentsDb.deleteEntity(c);
        
        ______TS("success: another comment with different text");
        
        c.createdAt = new Date();
        commentsDb.createEntity(c);
        verifyPresentInDatastore(c);
        commentsDb.deleteEntity(c);
        
        ______TS("null params check");
        verifyExceptionThrownFromCreateEntity(null,
                Const.StatusCodes.DBLEVEL_NULL_INPUT);
    }
    
    @Test
    public void testGetComment()
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {

        CommentAttributes c = createNewComment();
        commentsDb.createEntity(c);
        
        ______TS("typical success case: existent");
        CommentAttributes retrievedComment = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
        assertNotNull(retrievedComment);
        assertNotNull(commentsDb.getCommentsForReceiver(
                c.courseId, c.recipientType, recipient));
        
        CommentAttributes anotherRetrievedComment = commentsDb.getComment(retrievedComment.getCommentId());
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.getComment(retrievedComment);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment =
                commentsDb.getCommentsForGiverAndStatus(retrievedComment.courseId, retrievedComment.giverEmail,
                                                        retrievedComment.status)
                          .get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        retrievedComment.status = CommentStatus.DRAFT;
        retrievedComment.showCommentTo = new ArrayList<CommentParticipantType>();
        retrievedComment.showCommentTo.add(CommentParticipantType.PERSON);
        retrievedComment.showCommentTo.add(CommentParticipantType.TEAM);
        retrievedComment.showCommentTo.add(CommentParticipantType.SECTION);
        retrievedComment.showCommentTo.add(CommentParticipantType.COURSE);
        commentsDb.updateComment(retrievedComment);
        
        anotherRetrievedComment = commentsDb.getCommentDrafts(retrievedComment.giverEmail).get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.getCommentsForCommentViewer(retrievedComment.courseId,
                                                                         CommentParticipantType.PERSON)
                                            .get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.getCommentsForCommentViewer(retrievedComment.courseId,
                                                                         CommentParticipantType.TEAM)
                                            .get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.getCommentsForCommentViewer(retrievedComment.courseId,
                                                                         CommentParticipantType.SECTION)
                                            .get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        anotherRetrievedComment = commentsDb.getCommentsForCommentViewer(retrievedComment.courseId,
                                                                         CommentParticipantType.COURSE)
                                            .get(0);
        compareComments(retrievedComment, anotherRetrievedComment);
        
        ______TS("non existant comment case");
        List<CommentAttributes> retrievedList = commentsDb.getCommentsForGiver("any-course-id", "non-existent@email.com");
        assertEquals(0, retrievedList.size());
        
        long nonExistId = -1;
        c = commentsDb.getComment(nonExistId);
        assertNull(c);
        
        ______TS("null params case");
        retrievedComment.courseId = null;
        try {
            commentsDb.getCommentsForGiver(retrievedComment.courseId, retrievedComment.giverEmail);
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }

        retrievedComment.courseId = "any-course-id";
        retrievedComment.giverEmail = null;
        retrievedComment.recipients = null;
        try {
            commentsDb.getCommentsForReceiver(retrievedComment.courseId, retrievedComment.recipientType,
                                              retrievedComment.giverEmail);
        } catch (AssertionError e) {
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
    public void testUpdateComment() throws Exception {

        CommentAttributes c = createNewComment();
        commentsDb.createEntity(c);
        
        ______TS("invalid comment attributes");
        try {
            commentsDb.updateComment(null);
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("invalid comment attributes");
        c.recipients = new HashSet<String>();
        c.recipients.add("invalid receiver email");
        try {
            commentsDb.updateComment(c);
        } catch (InvalidParametersException e) {
            assertEquals(getPopulatedErrorMessage(
                             EMAIL_ERROR_MESSAGE, "invalid receiver email",
                             FieldValidator.EMAIL_FIELD_NAME, REASON_INCORRECT_FORMAT,
                             FieldValidator.EMAIL_MAX_LENGTH),
                         e.getLocalizedMessage());
        }
        
        ______TS("comment not exist");
        c.recipients = new HashSet<String>();
        c.recipients.add("receiver@mail.com");
        c.setCommentId((long) -1); //non-existent comment
        try {
            commentsDb.updateComment(c);
        } catch (EntityDoesNotExistException e) {
            assertTrue(e.getLocalizedMessage().contains(CommentsDb.ERROR_UPDATE_NON_EXISTENT));
        }
        
        ______TS("standard success case");
        CommentAttributes existing = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
        c.setCommentId(existing.getCommentId());
        c.createdAt = existing.createdAt;
        c.commentText = new Text("new comment");
        commentsDb.updateComment(c);
        verifyPresentInDatastore(c);
    }
    
    @Test
    public void testUpdateInstructorEmailAndStudentEmail()
            throws InvalidParametersException, EntityAlreadyExistsException {
        
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
        recipient = recipientEmailNew;
        c = createNewComment();
        commentsDb.createEntity(c);
        courseId = courseId2;
        giverEmail = giverEmail1;
        c = createNewComment();
        commentsDb.createEntity(c);
        
        giverEmail = "CDT.giver@mail.com";
        courseId = courseId1;
        recipient = recipientEmail1;
        c = createNewComment();
        commentsDb.createEntity(c);
        giverEmail = giverEmailNew;
        c = createNewComment();
        commentsDb.createEntity(c);
        courseId = courseId2;
        recipient = recipientEmail1;
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
        assertEquals(2, commentsDb.getCommentsForReceiver(courseId1, CommentParticipantType.PERSON, recipientEmail1).size());
        commentsDb.updateStudentEmail(courseId1, recipientEmail1, recipientEmail2);
        // after update: 2 comments for new giver email and others are not affected
        assertEquals(0, commentsDb.getCommentsForReceiver(courseId1, CommentParticipantType.PERSON, recipientEmail1).size());
        assertEquals(2, commentsDb.getCommentsForReceiver(courseId1, CommentParticipantType.PERSON, recipientEmail2).size());
        assertEquals(1, commentsDb.getCommentsForReceiver(courseId2, CommentParticipantType.PERSON, recipientEmail1).size());
        
        ______TS("failure: null input when updating instr email");
        
        try {
            commentsDb.updateInstructorEmail(courseId1, null, giverEmail2);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        try {
            commentsDb.updateInstructorEmail(null, giverEmail1, giverEmail2);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        try {
            commentsDb.updateInstructorEmail(courseId1, giverEmail1, null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getLocalizedMessage());
        }
        
        ______TS("failure: null input when updating student email");
        
        try {
            commentsDb.updateStudentEmail(courseId1, null, giverEmail2);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        try {
            commentsDb.updateStudentEmail(null, giverEmail1, giverEmail2);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        try {
            commentsDb.updateStudentEmail(courseId1, giverEmail1, null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        // restore variable
        courseId = "CDT.courseId";
        giverEmail = "CDT.giver@mail.com";
        recipient = "CDT.receiver@mail.com";
    }
    
    @Test
    public void testDeleteComment() throws InvalidParametersException, EntityAlreadyExistsException {
        
        CommentAttributes c = createNewComment();
        commentsDb.createEntity(c);
        
        ______TS("standard delete existing comment");
        CommentAttributes currentComment = commentsDb.getCommentsForGiver(c.courseId, c.giverEmail).get(0);
        c.setCommentId(currentComment.getCommentId());
        commentsDb.deleteEntity(currentComment);
        verifyAbsentInDatastore(c);
        
        ______TS("invalid delete non-existing comment fails silently");
        commentsDb.deleteEntity(currentComment); //currentComment doesn't exist anymore
    }
    
    @Test
    public void testDeleteComments() throws InvalidParametersException, EntityAlreadyExistsException {
        
        String instr1 = "CDT.del.instr1@mail.com";
        String instr2 = "CDT.del.instr2@mail.com";
        String courseId1 = "CDT.del.courseId1";
        String student1 = "CDT.del.student1@mail.com";
        String student2 = "CDT.del.student2@mail.com";
        String courseId2 = "CDT.del.courseId2";
        String instr3 = "CDT.del.instr3@mail.com";
        String student3 = "CDT.del.student3@mail.com";
        String team1 = "CDT.del.team1";
        String team2 = "CDT.del.team2";
        String section1 = "CDT.del.section1";
        String section2 = "CDT.del.section2";
        
        // create two comments for instructor1 and 1 for instructor2 in course1
        // also student1 2 3 will have 1 comment
        courseId = courseId1;
        giverEmail = instr1;
        recipient = student1;
        CommentAttributes c = createNewComment();
        commentsDb.createEntity(c);
        recipient = student2;
        c = createNewComment();
        commentsDb.createEntity(c);
        giverEmail = instr2;
        recipient = student3;
        c = createNewComment();
        commentsDb.createEntity(c);
        
        // course 1 team 1 and team2, instructor 3
        recipientType = CommentParticipantType.TEAM;
        recipient = team1;
        giverEmail = instr3;
        c = createNewComment();
        commentsDb.createEntity(c);
        recipient = team2;
        c = createNewComment();
        commentsDb.createEntity(c);
        
        // course 1 section 1 and section 2, instructor 3
        recipientType = CommentParticipantType.SECTION;
        recipient = section1;
        c = createNewComment();
        commentsDb.createEntity(c);
        recipient = section2;
        c = createNewComment();
        commentsDb.createEntity(c);
        
        // course2 instr3 and student3
        courseId = courseId2;
        recipientType = CommentParticipantType.PERSON;
        recipient = student3;
        c = createNewComment();
        commentsDb.createEntity(c);
        
        ______TS("success: delete instructor comments");
        
        commentsDb.deleteCommentsByInstructorEmail(courseId1, instr2);
        assertEquals(2, commentsDb.getCommentsForGiver(courseId1, instr1).size());
        assertEquals(0, commentsDb.getCommentsForGiver(courseId1, instr2).size());
        
        ______TS("success: delete student comments");
        
        commentsDb.deleteCommentsByStudentEmail(courseId1, student2);
        List<CommentAttributes> comments = commentsDb.getCommentsForGiver(courseId1, instr1);
        assertEquals(1, comments.size());
        assertTrue(comments.get(0).recipients.contains(student1));
        
        ______TS("success: delete team comments");
        
        commentsDb.deleteCommentsForTeam(courseId1, team2);
        assertEquals(0, commentsDb.getCommentsForReceiver(courseId1, CommentParticipantType.TEAM, team2).size());
        assertEquals(1, commentsDb.getCommentsForReceiver(courseId1, CommentParticipantType.TEAM, team1).size());
        
        ______TS("success: delete section comments");
        
        commentsDb.deleteCommentsForSection(courseId1, section2);
        assertEquals(0, commentsDb.getCommentsForReceiver(courseId1, CommentParticipantType.SECTION, section2).size());
        assertEquals(1, commentsDb.getCommentsForReceiver(courseId1, CommentParticipantType.SECTION, section1).size());
        
        ______TS("success: delete course comments");
        
        assertEquals(1, commentsDb.getCommentsForGiver(courseId1, instr1).size());
        assertEquals(0, commentsDb.getCommentsForGiver(courseId1, instr2).size());
        assertEquals(2, commentsDb.getCommentsForGiver(courseId1, instr3).size());
        commentsDb.deleteCommentsForCourse(courseId1);
        assertEquals(0, commentsDb.getCommentsForGiver(courseId1, instr1).size());
        assertEquals(0, commentsDb.getCommentsForGiver(courseId1, instr2).size());
        assertEquals(0, commentsDb.getCommentsForGiver(courseId1, instr2).size());
        assertEquals(1, commentsDb.getCommentsForGiver(courseId2, instr3).size());
        
        ______TS("failure: null input");
        
        try {
            commentsDb.deleteCommentsByInstructorEmail(courseId1, null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        try {
            commentsDb.deleteCommentsByInstructorEmail(null, instr1);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        try {
            commentsDb.deleteCommentsByStudentEmail(courseId1, null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        try {
            commentsDb.deleteCommentsByStudentEmail(null, student1);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        try {
            commentsDb.deleteCommentsForTeam(courseId1, null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        try {
            commentsDb.deleteCommentsForTeam(null, team1);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        try {
            commentsDb.deleteCommentsForSection(courseId1, null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        try {
            commentsDb.deleteCommentsForSection(null, section1);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        try {
            commentsDb.deleteCommentsForCourse(null);
            this.signalFailureToDetectException("Assertion error not detected properly");
        } catch (AssertionError e) {
            assertEquals(Const.StatusCodes.DBLEVEL_NULL_INPUT, e.getMessage());
        }
        
        // restore variable
        courseId = "CDT.courseId";
        giverEmail = "CDT.giver@mail.com";
        recipient = "CDT.receiver@mail.com";
        recipientType = CommentParticipantType.PERSON;
    }
    
    private void verifyExceptionThrownFromCreateEntity(CommentAttributes comment, String expectedMessage)
            throws EntityAlreadyExistsException {
        try {
            commentsDb.createEntity(comment);
            signalFailureToDetectException();
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(
                    expectedMessage,
                    e.getMessage());
        } catch (AssertionError e) {
            AssertHelper.assertContains(
                    expectedMessage,
                    e.getMessage());
        }
    }
    
    private CommentAttributes createNewComment() {
        CommentAttributes c = new CommentAttributes();
        c.courseId = courseId;
        c.giverEmail = giverEmail;
        c.recipientType = recipientType;
        c.recipients = new HashSet<String>();
        c.recipients.add(recipient);
        c.createdAt = new Date();
        c.commentText = new Text(commentText);
        c.status = CommentStatus.FINAL;
        return c;
    }
    
}
