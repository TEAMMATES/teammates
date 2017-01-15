package teammates.test.cases.logic;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.DataBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.logic.core.CommentsLogic;
import teammates.test.cases.BaseComponentTestCase;
import teammates.test.driver.AssertHelper;

import com.google.appengine.api.datastore.Text;

public class CommentsLogicTest extends BaseComponentTestCase {

    private static final CommentsLogic commentsLogic = CommentsLogic.inst();
    private static DataBundle dataBundle = getTypicalDataBundle();
    
    @BeforeClass
    public void classSetup() {
        printTestClassHeader();
        removeAndRestoreTypicalDataBundle();
    }
    
    @Test
    public void testCreateComment() throws Exception {
        
        CommentAttributes existingComment1 = dataBundle.comments.get("comment1FromI1C1toS1C1");
        
        ______TS("fail: non-existent course");
        CommentAttributes c = new CommentAttributes();
        c.courseId = "no-such-course";
        c.giverEmail = existingComment1.giverEmail;
        c.recipientType = existingComment1.recipientType;
        c.recipients = existingComment1.recipients;
        c.commentText = existingComment1.commentText;

        verifyExceptionThrownFromCreateFrComment(c,
                "Trying to create comments for a course that does not exist.");
        
        ______TS("fail: giver is not instructor");
        c.courseId = existingComment1.courseId;
        c.giverEmail = "student2InCourse1@gmail.tmt";
        c.commentText = new Text("Invalid comment from student to student");
        
        verifyExceptionThrownFromCreateFrComment(c,
                "User " + c.giverEmail + " is not a registered instructor for course "
                + c.courseId + ".");
        
        ______TS("fail: giver is not an instructor for the course");
        c.giverEmail = "instructor1@course2.tmt";
        c.commentText = new Text("Invalid Comment from instructor1Course2 to student1Course1");

        verifyExceptionThrownFromCreateFrComment(c,
                "User " + c.giverEmail + " is not a registered instructor for course "
                + c.courseId + ".");
        
        ______TS("typical case");
        
        c.courseId = "idOfTypicalCourse1";
        c.giverEmail = "instructor2@course1.tmt";
        c.recipients = new HashSet<String>();
        c.recipients.add("student3InCourse1@gmail.tmt");
        c.createdAt = new Date();
        c.commentText = new Text("New Comment from instructor2 to student3 in course 1");
        
        commentsLogic.createComment(c);
        verifyPresentInDatastore(c);
        
        //delete afterwards
        commentsLogic.deleteComment(c);
    }

    @Test
    public void testGetComments() throws Exception {
        
        CommentAttributes existingComment1 = dataBundle.comments.get("comment1FromI1C1toS1C1");
        
        ______TS("fail: invalid parameters");
        CommentAttributes c = new CommentAttributes();
        c.courseId = "invalid course id";
        c.giverEmail = existingComment1.giverEmail;
        c.recipients = existingComment1.recipients;
        c.commentText = existingComment1.commentText;

        verifyExceptionThrownFromGetCommentsForGiver(c,
                "Trying to get comments for a course that does not exist.");
        
        ______TS("fail: non-existent course");
        c.courseId = "no-such-course";
        
        verifyExceptionThrownFromGetCommentsForGiver(c,
                "Trying to get comments for a course that does not exist.");
        
        verifyExceptionThrownFromGetCommentsForReceiver(c,
                "Trying to get comments for a course that does not exist.");
        
        ______TS("success: get comment for giver");
        
        c.courseId = "idOfTypicalCourse1";
        List<CommentAttributes> commentsForGiver = commentsLogic.getCommentsForGiver(c.courseId, c.giverEmail);
        for (CommentAttributes comment : commentsForGiver) {
            assertEquals(c.courseId, comment.courseId);
            assertEquals(c.giverEmail, comment.giverEmail);
        }
        
        ______TS("success: get comment for receiver");
        
        c.recipientType = CommentParticipantType.PERSON;
        List<CommentAttributes> comments =
                commentsLogic.getCommentsForReceiver(c.courseId, c.recipientType, c.recipients.iterator().next());
        for (CommentAttributes comment : comments) {
            assertEquals(c.courseId, comment.courseId);
            assertEquals(c.recipients, comment.recipients);
        }
        
        ______TS("success: get comment for drafts");
        
        //change status to draft
        c.setCommentId(comments.get(0).getCommentId());
        c.status = CommentStatus.DRAFT;
        commentsLogic.updateComment(c);
        
        comments = commentsLogic.getCommentDrafts(c.giverEmail);
        for (CommentAttributes comment : comments) {
            assertEquals(c.courseId, comment.courseId);
            assertEquals(c.recipients, comment.recipients);
            assertEquals(c.status, comment.status);
        }
        
        ______TS("success: get comment for instructor");
        
        //add visibility options for instructor
        c.status = CommentStatus.FINAL;
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        c.showCommentTo.add(CommentParticipantType.INSTRUCTOR);
        c.showGiverNameTo = new ArrayList<CommentParticipantType>();
        c.showGiverNameTo.add(CommentParticipantType.INSTRUCTOR);
        c.showRecipientNameTo = new ArrayList<CommentParticipantType>();
        c.showRecipientNameTo.add(CommentParticipantType.INSTRUCTOR);
        commentsLogic.updateComment(c);
        
        InstructorAttributes giver = dataBundle.instructors.get("instructor1OfCourse1");
        comments = commentsLogic.getCommentsForInstructor(giver);
        verifyCommentsGotForInstructor(comments, giver);
        verifyCommentsGiverNameVisible(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        InstructorAttributes instructor = dataBundle.instructors.get("instructor2OfCourse1");
        comments = commentsLogic.getCommentsForInstructor(instructor);
        verifyCommentsGotForInstructor(comments, instructor);
        verifyCommentsGiverNameVisible(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        //remove name visibility options for instructor
        c.showGiverNameTo = new ArrayList<CommentParticipantType>();
        c.showRecipientNameTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);
        
        comments = commentsLogic.getCommentsForInstructor(instructor);
        verifyCommentsGotForInstructor(comments, instructor);
        verifyCommentsGiverNameHidden(comments);
        verifyCommentsRecipientNameHidden(comments);
        
        //remove all visibility options for instructor
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);
        
        comments = commentsLogic.getCommentsForInstructor(instructor);
        assertEquals(comments.size(), 0);
        
        ______TS("success: get comment for student");
        //TODO: refactor this test
        //add visibility options for person
        
        //init
        StudentAttributes student = dataBundle.students.get("student1InCourse1");
        comments = commentsLogic.getCommentsForStudent(student);
        for (CommentAttributes comment : comments) {
            comment.showCommentTo = new ArrayList<CommentParticipantType>();
            comment.showGiverNameTo = new ArrayList<CommentParticipantType>();
            comment.showRecipientNameTo = new ArrayList<CommentParticipantType>();
            commentsLogic.updateComment(comment);
        }
        
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        c.showCommentTo.add(CommentParticipantType.PERSON);
        c.showGiverNameTo = new ArrayList<CommentParticipantType>();
        c.showGiverNameTo.add(CommentParticipantType.PERSON);
        commentsLogic.updateComment(c);

        comments = commentsLogic.getCommentsForStudent(student);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameVisible(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        //remove name visibility options for person
        c.showGiverNameTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);

        comments = commentsLogic.getCommentsForStudent(student);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameHidden(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        //remove all visibility options for person
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);

        comments = commentsLogic.getCommentsForStudent(student);
        assertEquals(comments.size(), 0);
        
        //add visibility options for team
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        c.showCommentTo.add(CommentParticipantType.TEAM);
        commentsLogic.updateComment(c);

        comments = commentsLogic.getCommentsForStudent(student);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameHidden(comments);
        verifyCommentsRecipientNameHidden(comments);
        
        //add visibility options for course
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        c.showCommentTo.add(CommentParticipantType.COURSE);
        commentsLogic.updateComment(c);
        
        comments = commentsLogic.getCommentsForStudent(student);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameHidden(comments);
        verifyCommentsRecipientNameHidden(comments);
        
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        c.showCommentTo.add(CommentParticipantType.TEAM);
        c.showGiverNameTo = new ArrayList<CommentParticipantType>();
        c.showGiverNameTo.add(CommentParticipantType.TEAM);
        c.showRecipientNameTo = new ArrayList<CommentParticipantType>();
        c.showRecipientNameTo.add(CommentParticipantType.TEAM);
        commentsLogic.updateComment(c);
        
        //for teammates to receive peer's comment
        StudentAttributes student2 = dataBundle.students.get("student2InCourse1");
        comments = commentsLogic.getCommentsForStudent(student2);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameVisible(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        c.recipientType = CommentParticipantType.TEAM;
        c.recipients = new HashSet<String>();
        c.recipients.add(student.team);
        commentsLogic.updateComment(c);
        
        //for teammates to receive team's comment
        comments = commentsLogic.getCommentsForStudent(student2);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameVisible(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        //remove name visibility options for team
        c.showGiverNameTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);
        
        comments = commentsLogic.getCommentsForStudent(student);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameHidden(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        //remove all visibility options for team
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);
        
        comments = commentsLogic.getCommentsForStudent(student);
        assertEquals(comments.size(), 0);
        
        //add visibility options for course
        c.recipientType = CommentParticipantType.COURSE;
        c.recipients = new HashSet<String>();
        c.recipients.add(student.course);
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        c.showCommentTo.add(CommentParticipantType.COURSE);
        c.showGiverNameTo = new ArrayList<CommentParticipantType>();
        c.showGiverNameTo.add(CommentParticipantType.COURSE);
        c.showRecipientNameTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);

        comments = commentsLogic.getCommentsForStudent(student);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameVisible(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        //remove name visibility options for course
        c.showGiverNameTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);
        
        comments = commentsLogic.getCommentsForStudent(student);
        verifyCommentsGotForStudent(comments);
        verifyCommentsGiverNameHidden(comments);
        verifyCommentsRecipientNameVisible(comments);
        
        //remove all visibility options for person
        c.showCommentTo = new ArrayList<CommentParticipantType>();
        commentsLogic.updateComment(c);

        comments = commentsLogic.getCommentsForStudent(student);
        assertEquals(comments.size(), 0);
    }

    private void verifyCommentsGotForStudent(
            List<CommentAttributes> commentsForReceiver) {
        for (CommentAttributes comment : commentsForReceiver) {
            assertTrue(comment.showCommentTo.contains(CommentParticipantType.PERSON)
                    || comment.showCommentTo.contains(CommentParticipantType.TEAM)
                    || comment.showCommentTo.contains(CommentParticipantType.SECTION)
                    || comment.showCommentTo.contains(CommentParticipantType.COURSE));
        }
    }

    private void verifyCommentsGotForInstructor(
            List<CommentAttributes> commentsForReceiver,
            InstructorAttributes instructor) {
        for (CommentAttributes comment : commentsForReceiver) {
            assertTrue(comment.showCommentTo.contains(CommentParticipantType.INSTRUCTOR)
                    || comment.courseId.equals(instructor.courseId));
        }
    }

    @Test
    public void testUpdateComment() throws Exception {
        CommentAttributes existingComment = dataBundle.comments.get("comment1FromI3C1toS2C1");
        
        ______TS("fail: invalid params");
        CommentAttributes c = new CommentAttributes();
        c.courseId = "invalid course name";
        c.giverEmail = existingComment.giverEmail;
        c.recipientType = CommentParticipantType.PERSON;
        c.recipients = existingComment.recipients;
        c.createdAt = existingComment.createdAt;
        c.commentText = existingComment.commentText;
        
        verifyExceptionThrownFromUpdateComment(c,
                "Trying to update comments for a course that does not exist.");
        
        ______TS("fail: non existent entity");
        c.courseId = "no-such-course";
        
        verifyExceptionThrownFromUpdateComment(c,
                "Trying to update comments for a course that does not exist.");
        
        ______TS("typical success case");
        c.courseId = existingComment.courseId;

        commentsLogic.updateComment(c);
        verifyPresentInDatastore(c);
        
        List<CommentAttributes> actual =
                commentsLogic.getCommentsForReceiver(c.courseId, CommentParticipantType.PERSON,
                                                     c.recipients.iterator().next());
        assertEquals(1, actual.size());
        assertEquals(c.commentText, actual.get(0).commentText);
    }
    
    @Test
    public void testUpdateCommentEmail() throws Exception {
        CommentAttributes existingComment1 = dataBundle.comments.get("comment1FromI1C1toS1C1");
        
        ______TS("typical success case: email changed");
        String courseId = existingComment1.courseId;
        String oldInstrEmail = existingComment1.lastEditorEmail;
        String updatedInstrEmail = "otheremail@gmail.tmt";
        commentsLogic.updateInstructorEmail(courseId, oldInstrEmail, updatedInstrEmail);
        
        List<CommentAttributes> updatedComments = commentsLogic.getCommentsForGiver(courseId, updatedInstrEmail);
        for (CommentAttributes updatedComment : updatedComments) {
            assertEquals(updatedInstrEmail, updatedComment.giverEmail);
            if (updatedComment.lastEditorEmail != null) {
                assertEquals(updatedInstrEmail, updatedComment.lastEditorEmail);
            }
        }
    }

    @Test
    public void testDeleteComment() {
        CommentAttributes existingComment1 = dataBundle.comments.get("comment1FromI1C1toS1C1");
        
        ______TS("silent fail nothing to delete");
        CommentAttributes c = new CommentAttributes();
        c.courseId = "no-such-course";
        c.giverEmail = existingComment1.giverEmail;
        c.recipientType = existingComment1.recipientType;
        c.recipients = existingComment1.recipients;
        c.createdAt = existingComment1.createdAt;
        c.commentText = existingComment1.commentText;
        
        commentsLogic.deleteComment(c);
        c.courseId = existingComment1.courseId;
        verifyPresentInDatastore(c);
        
        ______TS("typical success case");
        
        commentsLogic.deleteComment(c);
        verifyAbsentInDatastore(c);
    }
    
    // TODO: add tests for those one level down api call if test coverage is considered
    
    private void verifyCommentsGiverNameVisible(List<CommentAttributes> comments) {
        for (CommentAttributes c : comments) {
            assertFalse("Anonymouse".equals(c.giverEmail));
        }
    }
    
    private void verifyCommentsGiverNameHidden(List<CommentAttributes> comments) {
        for (CommentAttributes c : comments) {
            assertEquals("Anonymous", c.giverEmail);
        }
    }
    
    private void verifyCommentsRecipientNameHidden(List<CommentAttributes> comments) {
        for (CommentAttributes c : comments) {
            assertEquals("Anonymous", c.recipients.iterator().next());
        }
    }
    
    private void verifyCommentsRecipientNameVisible(List<CommentAttributes> comments) {
        for (CommentAttributes c : comments) {
            assertFalse("Anonymous".equals(c.recipients.iterator().next()));
        }
    }
    
    private void verifyExceptionThrownFromCreateFrComment(
            CommentAttributes comment, String message)
            throws InvalidParametersException, EntityAlreadyExistsException {
        try {
            commentsLogic.createComment(comment);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(message, e.getMessage());
        }
    }
    
    private void verifyExceptionThrownFromGetCommentsForGiver(
            CommentAttributes comment, String message) {
        try {
            commentsLogic.getCommentsForGiver(comment.courseId, comment.giverEmail);
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(message, e.getMessage());
        }
    }
    
    private void verifyExceptionThrownFromGetCommentsForReceiver(
            CommentAttributes comment, String message) {
        try {
            commentsLogic.getCommentsForReceiver(comment.courseId, comment.recipientType,
                                                 comment.recipients.iterator().next());
            signalFailureToDetectException();
        } catch (EntityDoesNotExistException e) {
            assertEquals(message, e.getMessage());
        }
    }

    private void verifyExceptionThrownFromUpdateComment(CommentAttributes c, String message) {
        try {
            commentsLogic.updateComment(c);
        } catch (InvalidParametersException e) {
            AssertHelper.assertContains(message, e.getMessage());
        } catch (EntityDoesNotExistException e) {
            AssertHelper.assertContains(message, e.getMessage());
        }
    }
}
