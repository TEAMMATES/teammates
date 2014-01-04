package teammates.storage.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Text;

import teammates.common.datatransfer.CommentAttributes;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.Comment;

public class CommentsDb extends EntitiesDb{
	
	public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Comment: ";
	private static final Logger log = Utils.getLogger();
	
	public CommentAttributes getComment(Long commentId){
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, commentId);
		
		Comment comment = getCommentEntity(commentId);
		if(comment == null){
			log.info("Trying to get non-existent Comment: " + commentId);
			return null;
		} else{
			return new CommentAttributes(comment);
		}
	}
	
	public CommentAttributes getComment(String courseId, String giverEmail,
			String receiverEmail, Text commentText, Date date) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT,giverEmail);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT,receiverEmail);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT,commentText);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, date);

		Comment comment = getCommentEntity(courseId, giverEmail, receiverEmail,
				commentText, date);
		if (comment == null) {
			log.info("Trying to get non-existent Comment: " + courseId + ", "
					+ giverEmail + ", " + receiverEmail + ", " + commentText);
			return null;
		} else {
			return new CommentAttributes(comment);
		}
	}
	
	public List<CommentAttributes> getCommentsForGiver(String courseId, String giverEmail){
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
		
		List<Comment> comments = getCommentEntitiesForGiver(courseId, giverEmail);
		List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
		
		for(Comment comment: comments){
			commentAttributesList.add(new CommentAttributes(comment));
		}
		return commentAttributesList;
	}
	
	public List<CommentAttributes> getCommentsForReceiver(String courseId, String receiverEmail){
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);
		
		List<Comment> comments = getCommentEntitiesForReceiver(courseId, receiverEmail);
		List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
		
		for(Comment comment: comments){
			commentAttributesList.add(new CommentAttributes(comment));
		}
		return commentAttributesList;
	}
	
	public List<CommentAttributes> getCommentsForGiverAndReceiver(String courseId, String giverEmail, String receiverEmail){
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);
		
		List<Comment> comments = getCommentEntitiesForGiverAndReceiver(courseId, giverEmail, receiverEmail);
		List<CommentAttributes> commentAttributesList = new ArrayList<CommentAttributes>();
		
		for(Comment comment: comments){
			commentAttributesList.add(new CommentAttributes(comment));
		}
		return commentAttributesList;
	}
	
	public void updateComment(CommentAttributes newAttributes) throws InvalidParametersException, EntityDoesNotExistException{
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT,  newAttributes);
		
		newAttributes.sanitizeForSaving();
		
		if (!newAttributes.isValid()) {
			throw new InvalidParametersException(newAttributes.getInvalidityInfo());
		}
		Comment comment = (Comment) getEntity(newAttributes);
		
		if (comment == null) {
			throw new EntityDoesNotExistException(ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
		}
		
		comment.setCommentText(newAttributes.commentText);
		
		getPM().close();
	}
	
	// Gets a comment entity if the ID is known
	private Comment getCommentEntity(Long commentId) {
		Query q = getPM().newQuery(Comment.class);
		q.declareParameters("Long commentIdParam");
		q.setFilter("commentId == commentIdParam");
		
		@SuppressWarnings("unchecked")
		List<Comment> commentList = (List<Comment>) q.execute(commentId);

		if (commentList.isEmpty() || JDOHelper.isDeleted(commentList.get(0))) {
			return null;
		}
		return commentList.get(0);
	}
	
	private Comment getCommentEntity(String courseId, String giverEmail,
			String receiverEmail, Text commentText, Date date) {

		List<Comment> commentList = getCommentEntitiesForGiverAndReceiver(courseId, giverEmail, receiverEmail);
		
		if(commentList.isEmpty()){
			return null;
		}
		
		//JDO query can't seem to handle Text comparison correctly,
		//we have to compare the texts separately.
		for(Comment comment : commentList){
			if(comment.getCommentText().equals(commentText) && comment.getCreatedAt().equals(date)){
				return comment;
			}
		}
		
		return null;
	}
	
	private List<Comment> getCommentEntitiesForGiver(String courseId, String giverEmail){
		Query q = getPM().newQuery(Comment.class);
		q.declareParameters("String courseIdParam, String giverEmailParam");
		q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
		
		@SuppressWarnings("unchecked")
		List<Comment> commentList = (List<Comment>) q.execute(courseId, giverEmail);
		
		return commentList;
	}
	
	private List<Comment> getCommentEntitiesForReceiver(String courseId, String receiverEmail){
		Query q = getPM().newQuery(Comment.class);
		q.declareParameters("String courseIdParam, String receiverEmailParam");
		q.setFilter("courseId == courseIdParam && receiverEmail == receiverEmailParam");
		
		@SuppressWarnings("unchecked")
		List<Comment> commentList = (List<Comment>) q.execute(courseId, receiverEmail);
		
		return commentList;
	}
	
	private List<Comment> getCommentEntitiesForGiverAndReceiver(String courseId, String giverEmail, String receiverEmail){
		Query q = getPM().newQuery(Comment.class);
		q.declareParameters("String courseIdParam, String giverEmailParam, String receiverEmailParam");
		q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam && receiverEmail == receiverEmailParam");
		
		@SuppressWarnings("unchecked")
		List<Comment> commentList = (List<Comment>) q.execute(courseId, giverEmail, receiverEmail);
		
		return commentList;
	}

	@Override
	protected Object getEntity(EntityAttributes attributes) {
		CommentAttributes commentToGet = (CommentAttributes) attributes;
		if(commentToGet.getCommentId() != null){
			return getCommentEntity(commentToGet.getCommentId());
		} else{
			return getCommentEntity(commentToGet.courseId, commentToGet.giverEmail,
					commentToGet.receiverEmail, commentToGet.commentText, commentToGet.createdAt);
		}
	}
}
