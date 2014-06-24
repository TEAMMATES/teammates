package teammates.ui.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentRecipientType;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

public class InstructorStudentCommentAddAction extends Action {

    @Override
    protected ActionResult execute()  throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        //used to redirect to studentDetailsPage or studentRecordsPage
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        
        Boolean isFromCommentsPage = getRequestParamAsBoolean(Const.ParamsNames.FROM_COMMENTS_PAGE);
        Boolean isFromStudentDetailsPage = getRequestParamAsBoolean(Const.ParamsNames.FROM_STUDENT_DETAILS_PAGE);
        Boolean isFromCourseDetailsPage = getRequestParamAsBoolean(Const.ParamsNames.FROM_COURSE_DETAILS_PAGE);
        
        String commentText = getRequestParamValue(Const.ParamsNames.COMMENT_TEXT); 
        Assumption.assertNotNull(commentText);
        Assumption.assertNotEmpty(commentText);
        
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
        
        if(isFromCommentsPage){
            return createRedirectResult((new PageData(account).getInstructorCommentsLink()) + "&" + Const.ParamsNames.COURSE_ID + "=" + courseId);
        } else if(isFromStudentDetailsPage){
            return createRedirectResult(getCourseStudentDetailsLink(courseId, studentEmail));
        } else if(isFromCourseDetailsPage){
            return createRedirectResult(new PageData(account).getInstructorCourseDetailsLink(courseId));
        } else {//studentRecordsPage by default
            return createRedirectResult(new PageData(account).getInstructorStudentRecordsLink(courseId, studentEmail));
        }
    }

    private CommentAttributes extractCommentData() {
        CommentAttributes comment = new CommentAttributes();
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        
        InstructorAttributes instructorDetailForCourse = logic.getInstructorForGoogleId(courseId, account.googleId);
        Assumption.assertNotNull("Account trying to add comment is not an instructor of the course", instructorDetailForCourse);
        
        String recipientType = getRequestParamValue(Const.ParamsNames.RECIPIENT_TYPE);
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        String recipients = getRequestParamValue(Const.ParamsNames.RECIPIENTS);
        String showCommentTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWCOMMENTSTO);
        String showGiverTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWGIVERTO);
        String showRecipientTo = getRequestParamValue(Const.ParamsNames.COMMENTS_SHOWRECIPIENTTO);
        Text commentText = new Text(getRequestParamValue(Const.ParamsNames.COMMENT_TEXT));
        
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
                comment.showCommentTo.add(CommentRecipientType.valueOf(sct.trim()));
            }
        }
        
        comment.showGiverNameTo = new ArrayList<CommentRecipientType>();
        if(showGiverTo != null && !showGiverTo.isEmpty()){
            String[] showGiverToArray = showGiverTo.split(",");
            for(String sgt : showGiverToArray){
                comment.showGiverNameTo.add(CommentRecipientType.valueOf(sgt.trim()));
            }
        }
        
        comment.showRecipientNameTo = new ArrayList<CommentRecipientType>();
        if(showRecipientTo != null && !showRecipientTo.isEmpty()){
            String[] showRecipientToArray = showRecipientTo.split(",");
            for(String srt : showRecipientToArray){
                comment.showRecipientNameTo.add(CommentRecipientType.valueOf(srt.trim()));
            }
        }
        
        comment.createdAt = new Date();
        comment.commentText = commentText;
        
        return comment;
    }
    
    public String getCourseStudentDetailsLink(String courseId, String studentEmail){
        String link = Const.ActionURIs.INSTRUCTOR_COURSE_STUDENT_DETAILS_PAGE;
        link = Url.addParamToUrl(link,Const.ParamsNames.COURSE_ID, courseId);
        link = Url.addParamToUrl(link,Const.ParamsNames.STUDENT_EMAIL, studentEmail);
        link = new PageData(account).addUserIdToUrl(link);
        return link;
    }
}
