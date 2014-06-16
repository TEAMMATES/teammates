package teammates.ui.controller;


import java.util.ArrayList;
import java.util.HashSet;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.logic.api.GateKeeper;

public class InstructorStudentCommentEditAction extends Action {

    @Override
    protected ActionResult execute()  throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL); 
        Assumption.assertNotNull(studentEmail);
        
        Boolean isFromCommentPage = getRequestParamAsBoolean(Const.ParamsNames.FROM_COMMENTS_PAGE);
        
        String commentId = getRequestParamValue(Const.ParamsNames.COMMENT_ID);
        Assumption.assertNotNull(commentId);
        
        new GateKeeper().verifyAccessible(
                logic.getInstructorForGoogleId(courseId, account.googleId),
                logic.getCourse(courseId));
        
        CommentAttributes comment = extractCommentData();
        String editType = getRequestParamValue(Const.ParamsNames.COMMENT_EDITTYPE);
        
        try {
            if(editType.equals("edit")){
                logic.updateComment(comment);
                statusToUser.add(Const.StatusMessages.COMMENT_EDITED);
                statusToAdmin = "Edited Comment for Student:<span class=\"bold\">(" +
                        comment.recipients + ")</span> for Course <span class=\"bold\">[" +
                        comment.courseId + "]</span><br>" +
                        "<span class=\"bold\">Comment:</span> " + comment.commentText;
            } else if(editType.equals("delete")){
                logic.deleteComment(comment);
                statusToUser.add(Const.StatusMessages.COMMENT_DELETED);
                statusToAdmin = "Deleted Comment for Student:<span class=\"bold\">(" +
                        comment.recipients + ")</span> for Course <span class=\"bold\">[" +
                        comment.courseId + "]</span><br>" +
                        "<span class=\"bold\">Comment:</span> " + comment.commentText;
            }
        } catch (InvalidParametersException e) {
            // TODO: add a test to cover this path
            statusToUser.add(e.getMessage());
            statusToAdmin = e.getMessage();
            isError = true;
        }
        
        return !isFromCommentPage? createRedirectResult(new PageData(account).getInstructorStudentRecordsLink(courseId,studentEmail)):
            createRedirectResult((new PageData(account).getInstructorCommentsLink()) + "&" + Const.ParamsNames.COURSE_ID + "=" + courseId);
    }

    private CommentAttributes extractCommentData() {
        CommentAttributes comment = new CommentAttributes();
        
        String commentId = getRequestParamValue(Const.ParamsNames.COMMENT_ID);
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        Text commentText = new Text(getRequestParamValue(Const.ParamsNames.COMMENT_TEXT));
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);
        Assumption.assertNotNull("Account trying to update comment is not an instructor of the course", instructorDetailForCourse);
        
        comment.setCommentId(Long.valueOf(commentId));
        comment.courseId = courseId;
        comment.giverEmail = instructorDetailForCourse.email; 
        comment.recipientType = CommentRecipientType.PERSON;
        comment.recipients = new HashSet<String>();
        comment.recipients.add(studentEmail);
        comment.status = CommentStatus.FINAL;
        comment.showCommentTo = new ArrayList<CommentRecipientType>();
        comment.showGiverNameTo = new ArrayList<CommentRecipientType>();
        comment.showRecipientNameTo = new ArrayList<CommentRecipientType>();
        comment.commentText = commentText;
        
        return comment;
    }
}
