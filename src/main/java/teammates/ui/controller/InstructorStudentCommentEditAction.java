package teammates.ui.controller;


import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
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
						comment.receiverEmail + ")</span> for Course <span class=\"bold\">[" +
						comment.courseId + "]</span><br>" +
						"<span class=\"bold\">Comment:</span> " + comment.commentText;
			} else if(editType.equals("delete")){
				logic.deleteComment(comment);
				statusToUser.add(Const.StatusMessages.COMMENT_DELETED);
				statusToAdmin = "Deleted Comment for Student:<span class=\"bold\">(" +
						comment.receiverEmail + ")</span> for Course <span class=\"bold\">[" +
						comment.courseId + "]</span><br>" +
						"<span class=\"bold\">Comment:</span> " + comment.commentText;
			}
		} catch (InvalidParametersException e) {
			statusToUser.add(e.getMessage());
			statusToAdmin = e.getMessage();
			isError = true;
		}
		
		return createRedirectResult(new PageData(account).getInstructorStudentRecordsLink(courseId,studentEmail));
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
		comment.receiverEmail = studentEmail;
		comment.commentText = commentText;
		
		return comment;
	}
}
