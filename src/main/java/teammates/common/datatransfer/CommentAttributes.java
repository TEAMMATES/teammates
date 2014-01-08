package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import teammates.common.util.FieldValidator;
import teammates.common.util.Sanitizer;
import teammates.common.util.FieldValidator.FieldType;
import teammates.storage.entity.Comment;

import com.google.appengine.api.datastore.Text;

public class CommentAttributes extends EntityAttributes{
	
	private Long commentId = null;
	public String courseId;
	public String giverEmail;
	public String receiverEmail;
	public Text commentText;
	public Date createdAt;
	
	public CommentAttributes(){
		
	}
	
	public CommentAttributes(String courseId, String giverEmail, String receiverEmail, Date createdAt, Text commentText){
		this.courseId = courseId;
		this.giverEmail = giverEmail;
		this.receiverEmail = receiverEmail;
		this.commentText = commentText;
		this.createdAt = createdAt;
	}
	
	public CommentAttributes(Comment comment){
		this.commentId = comment.getId();
		this.courseId = comment.getCourseId();
		this.giverEmail = comment.getGiverEmail();
		this.receiverEmail = comment.getReceiverEmail();
		this.createdAt = comment.getCreatedAt();
		this.commentText = comment.getCommentText();
	}
	
	public Long getCommentId(){
		return this.commentId;
	}
	
	//Use only to match existing and known Comment
	public void setCommentId(Long commentId){
		this.commentId = commentId;
	}
	
	public List<String> getInvalidityInfo() {
		
		FieldValidator validator = new FieldValidator();
		List<String> errors = new ArrayList<String>();
		String error;
		
		error= validator.getInvalidityInfo(FieldType.COURSE_ID, courseId);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.EMAIL, giverEmail);
		if(!error.isEmpty()) { errors.add(error); }
		
		error= validator.getInvalidityInfo(FieldType.EMAIL, receiverEmail);
		if(!error.isEmpty()) { errors.add(error); }
		
		return errors;
	}

	public Comment toEntity() {
		return new Comment(courseId, giverEmail, receiverEmail, commentText, createdAt);
	}
	
	@Override
	public String toString() {
		return "CommentAttributes [commentId = " + commentId +
				", courseId = " + courseId + 
				", giverEmail = " + giverEmail + 
				", receiverEmail = " + receiverEmail +
				", commentText = " + commentText +
				", createdAt = " + createdAt + "]";
	}

	@Override
	public String getIdentificationString() {
		return toString();
	}

	@Override
	public String getEntityTypeAsString() {
		return "Comment";
	}

	@Override
	public void sanitizeForSaving() {
		this.courseId = this.courseId.trim();
		this.commentText = Sanitizer.sanitizeTextField(this.commentText);
	}
	
	public static void sortCommentsByCreationTime(List<CommentAttributes> comments){
		Collections.sort(comments, new Comparator<CommentAttributes>() {
			public int compare(CommentAttributes comment1, CommentAttributes comment2) {
				return comment1.createdAt.compareTo(comment2.createdAt);
			}
		});
	}
	
	public static void sortCommentsByCreationTimeDescending(List<CommentAttributes> comments){
		Collections.sort(comments, new Comparator<CommentAttributes>() {
			public int compare(CommentAttributes comment1, CommentAttributes comment2) {
				return comment2.createdAt.compareTo(comment1.createdAt);
			}
		});
	}
}
