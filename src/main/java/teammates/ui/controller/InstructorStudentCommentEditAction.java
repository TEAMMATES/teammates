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
        String recipientType = getRequestParamValue(Const.ParamsNames.RECIPIENT_TYPE);
        String recipients = getRequestParamValue(Const.ParamsNames.RECIPIENTS);
        String showCommentTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO);
        String showGiverTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWGIVERTO);
        String showRecipientTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO);

        String commentTextString = getRequestParamValue(Const.ParamsNames.COMMENT_TEXT);
        Assumption.assertNotNull(commentTextString);
        Assumption.assertNotEmpty(commentTextString);
        
        Text commentText = new Text(commentTextString);
        
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);
        Assumption.assertNotNull("Account trying to update comment is not an instructor of the course", instructorDetailForCourse);
        
        comment.setCommentId(Long.valueOf(commentId));
        comment.courseId = courseId;
        comment.giverEmail = instructorDetailForCourse.email; 
        comment.recipientType = recipientType == null ? CommentRecipientType.PERSON : CommentRecipientType.valueOf(recipientType);
        comment.recipients = new HashSet<String>();
        if(recipients != null && !recipients.isEmpty()){
            String[] recipientsArray = recipients.split(",");
            for(String recipient : recipientsArray){
                comment.recipients.add(recipient.trim());
            }
        } else {
            comment.recipients.add(studentEmail);
        }
        comment.status = CommentStatus.FINAL;
        
        comment.showCommentTo = new ArrayList<CommentRecipientType>();
        if(showCommentTo != null && !showCommentTo.isEmpty()){
            String[] showCommentToArray = showCommentTo.split(",");
            for(String sct : showCommentToArray){
                comment.showCommentTo.add(CommentRecipientType.valueOf(sct));
            }
        }
        
        comment.showGiverNameTo = new ArrayList<CommentRecipientType>();
        if(showGiverTo != null && !showGiverTo.isEmpty()){
            String[] showGiverToArray = showGiverTo.split(",");
            for(String sgt : showGiverToArray){
                comment.showGiverNameTo.add(CommentRecipientType.valueOf(sgt));
            }
        }
        
        comment.showRecipientNameTo = new ArrayList<CommentRecipientType>();
        if(showRecipientTo != null && !showRecipientTo.isEmpty()){
            String[] showRecipientToArray = showRecipientTo.split(",");
            for(String srt : showRecipientToArray){
                comment.showRecipientNameTo.add(CommentRecipientType.valueOf(srt));
            }
        }
        comment.commentText = commentText;
        
        return comment;
    }
}
