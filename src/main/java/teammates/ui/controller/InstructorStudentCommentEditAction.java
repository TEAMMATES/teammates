package teammates.ui.controller;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.CommentParticipantType;
import teammates.common.datatransfer.CommentSendingState;
import teammates.common.datatransfer.CommentStatus;
import teammates.common.datatransfer.CourseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.StatusMessage;
import teammates.common.util.Const.StatusMessageColor;
import teammates.logic.api.GateKeeper;

/**
 * Action: Edit or delete the {@link CommentAttributes} based on the given editType (edit|delete)
 */
public class InstructorStudentCommentEditAction extends Action {

    @Override
    protected ActionResult execute()  throws EntityDoesNotExistException {
        
        String courseId = getRequestParamValue(Const.ParamsNames.COURSE_ID);
        Assumption.assertNotNull(courseId);
        
        String studentEmail = getRequestParamValue(Const.ParamsNames.STUDENT_EMAIL);
        
        Boolean isFromCommentPage = getRequestParamAsBoolean(Const.ParamsNames.FROM_COMMENTS_PAGE);
        
        String commentId = getRequestParamValue(Const.ParamsNames.COMMENT_ID);
        Assumption.assertNotNull(commentId);
        
        verifyAccessibleByInstructor(courseId, commentId);
        
        CommentAttributes comment = extractCommentData();
        String editType = getRequestParamValue(Const.ParamsNames.COMMENT_EDITTYPE);
        
        try {
            if (editType.equals("edit")) {
                CommentAttributes updatedComment = logic.updateComment(comment);
                //TODO: move putDocument to task queue
                logic.putDocument(updatedComment);
                
                statusToUser.add(new StatusMessage(Const.StatusMessages.COMMENT_EDITED, StatusMessageColor.SUCCESS));
                statusToAdmin = "Edited Comment for Student:<span class=\"bold\">(" +
                        comment.recipients + ")</span> for Course <span class=\"bold\">[" +
                        comment.courseId + "]</span><br>" +
                        "<span class=\"bold\">Comment:</span> " + comment.commentText;
            } else if (editType.equals("delete")) {
                logic.deleteDocument(comment);
                logic.deleteComment(comment);
                statusToUser.add(new StatusMessage(Const.StatusMessages.COMMENT_DELETED, StatusMessageColor.SUCCESS));
                statusToAdmin = "Deleted Comment for Student:<span class=\"bold\">(" +
                        comment.recipients + ")</span> for Course <span class=\"bold\">[" +
                        comment.courseId + "]</span><br>" +
                        "<span class=\"bold\">Comment:</span> " + comment.commentText;
            }
        } catch (InvalidParametersException e) {
            // TODO: add a test to cover this path
            statusToUser.add(new StatusMessage(e.getMessage(), StatusMessageColor.DANGER));
            statusToAdmin = e.getMessage();
            isError = true;
        }
        
        return !isFromCommentPage ? 
               createRedirectResult(new PageData(account).getInstructorStudentRecordsLink(courseId,studentEmail)):
               createRedirectResult(
                       (new PageData(account).getInstructorCommentsLink()) + "&" 
                     + Const.ParamsNames.COURSE_ID + "=" + courseId);
    }

    private void verifyAccessibleByInstructor(String courseId, String commentId) {
        // TODO: update this if Comment recipient is updated
        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, account.googleId);
        CourseAttributes course = logic.getCourse(courseId);
        CommentAttributes commentInDb = logic.getComment(Long.valueOf(commentId));
        
        if (commentInDb != null && instructor != null && commentInDb.giverEmail.equals(instructor.email)) {
            // if comment giver and instructor are the same, allow access
            return ;
        }
        if (commentInDb == null) {
            Assumption.fail("Comment or instructor cannot be null for editing comment");
        }
        CommentParticipantType commentRecipientType = commentInDb.recipientType;
        String recipients = commentInDb.recipients.iterator().next();
        if (commentRecipientType == CommentParticipantType.COURSE) {
            new GateKeeper().verifyAccessible(instructor, course,
                                              Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS);
        } else if (commentRecipientType == CommentParticipantType.SECTION) {
            new GateKeeper().verifyAccessible(instructor, course, recipients,
                                              Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS);
        } else if (commentRecipientType == CommentParticipantType.TEAM) {
            List<StudentAttributes> students = logic.getStudentsForTeam(recipients, courseId);

            if (students.isEmpty()) { // considered as a serious bug in coding or user submitted corrupted data
                Assumption.fail();
            } else {
                new GateKeeper().verifyAccessible(instructor, course, students.get(0).section,
                                                  Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS);
            }
        } else { // TODO: modify this after comment for instructor is enabled
            StudentAttributes student = logic.getStudentForEmail(courseId, recipients);
            if (student == null) { // considered as a serious bug in coding or user submitted corrupted data
                Assumption.fail();
            } else {
                new GateKeeper().verifyAccessible(instructor, course, student.section,
                                                  Const.ParamsNames.INSTRUCTOR_PERMISSION_MODIFY_COMMENT_IN_SECTIONS);
            }
        }
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
        Assumption.assertNotNull("Account trying to update comment is not an instructor of the course",
                                 instructorDetailForCourse);
        
        comment.setCommentId(Long.valueOf(commentId));
        comment.courseId = courseId;
        comment.giverEmail = instructorDetailForCourse.email; 
        if (recipientType != null) {
            comment.recipientType = CommentParticipantType.valueOf(recipientType);
        } else {
            comment.recipientType = null;
        }
        
        if (recipients != null) {
            comment.recipients = new HashSet<String>();
            if (!recipients.isEmpty()) {
                String[] recipientsArray = recipients.split(",");
                for (String recipient : recipientsArray) {
                    comment.recipients.add(recipient.trim());
                }
            } else {
                comment.recipients.add(studentEmail);
            }
        }
        comment.status = CommentStatus.FINAL;
        
        comment.showCommentTo = new ArrayList<CommentParticipantType>();
        if (showCommentTo != null && !showCommentTo.isEmpty()) {
            String[] showCommentToArray = showCommentTo.split(",");
            for (String sct : showCommentToArray) {
                comment.showCommentTo.add(CommentParticipantType.valueOf(sct.trim()));
            }
        }
        
        comment.showGiverNameTo = new ArrayList<CommentParticipantType>();
        if (showGiverTo != null && !showGiverTo.isEmpty()) {
            String[] showGiverToArray = showGiverTo.split(",");
            for (String sgt : showGiverToArray) {
                comment.showGiverNameTo.add(CommentParticipantType.valueOf(sgt.trim()));
            }
        }
        
        comment.showRecipientNameTo = new ArrayList<CommentParticipantType>();
        if (showRecipientTo != null && !showRecipientTo.isEmpty()) {
            String[] showRecipientToArray = showRecipientTo.split(",");
            for (String srt : showRecipientToArray) {
                comment.showRecipientNameTo.add(CommentParticipantType.valueOf(srt.trim()));
            }
        }
        //if a comment is public to recipient (except Instructor), it's a pending comment
        if (isCommentPublicToRecipient(comment)) {
            comment.sendingState = CommentSendingState.PENDING;
        }
        comment.commentText = commentText;
        comment.createdAt = new Date();
        
        return comment;
    }

    private boolean isCommentPublicToRecipient(CommentAttributes comment) {
        return comment.showCommentTo != null
                && (comment.isVisibleTo(CommentParticipantType.PERSON)
                    || comment.isVisibleTo(CommentParticipantType.TEAM)
                    || comment.isVisibleTo(CommentParticipantType.SECTION)
                    || comment.isVisibleTo(CommentParticipantType.COURSE));
    }
}
