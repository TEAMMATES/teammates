package teammates.ui.controller;


import java.util.Date;
import java.util.HashSet;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentCommentAddAction extends Action {

    @Override
    protected ActionResult execute()  throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL); 
        Assumption.assertNotNull(studentEmail);
        
        String commentText = getRequestParamValue(Const.ParamsNames.COMMENT_TEXT); 
        Assumption.assertNotNull(commentText);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
        
        CommentAttributes comment = extractCommentData();
        
        try {
            logic.createComment(comment);
            statusToUser.add(Const.StatusMessages.COMMENT_ADDED);
            statusToAdmin = "Created Comment for Student:<span class=\"bold\">(" +
                    comment.recipients + ")</span> for Course <span class=\"bold\">[" +
                    comment.courseId + "]</span><br>" +
                    "<span class=\"bold\">Comment:</span> " + comment.commentText;
        } catch (EntityAlreadyExistsException e) {  // this exception should not be thrown normally unless GAE creates duplicate commentId
            Assumption.fail("Creating a duplicate comment should not be possible as comments should have different timestamp\n");
        } catch (InvalidParametersException e) {
            // TODO: add a test to cover this branch
            statusToUser.add(e.getMessage());
            statusToAdmin = e.getMessage();
            isError = true;
        }
        
        return createRedirectResult(new PageData(account).getInstructorStudentRecordsLink(courseId,studentEmail));
    }

    private CommentAttributes extractCommentData() {
        CommentAttributes comment = new CommentAttributes();
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Text commentText = new Text(getRequestParamValue(Const.ParamsNames.COMMENT_TEXT));
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);
        Assumption.assertNotNull("Account trying to add comment is not an instructor of the course", instructorDetailForCourse);
        
        comment.courseId = courseId;
        comment.giverEmail = instructorDetailForCourse.email;
        comment.recipientType = CommentRecipientType.PERSON;
        comment.recipients = new HashSet<String>();
        comment.recipients.add(studentEmail);
        comment.createdAt = new Date();
        comment.commentText = commentText;
        
        return comment;
    }
}
