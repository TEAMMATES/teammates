package teammates.storage.api;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackResponseComment;

public class FeedbackResponseCommentsDb extends EntitiesDb {

	private static final Logger log = Utils.getLogger();

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseCommentId);
		
		FeedbackResponseComment frc = 
				getFeedbackResponseCommentEntity(feedbackResponseCommentId);
		
		if (frc == null) {
			log.info("Trying to get non-existent response comment: " +
					feedbackResponseCommentId + ".");
			return null;
		}
		
		return new FeedbackResponseCommentAttributes(frc);	
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public FeedbackResponseCommentAttributes getFeedbackResponseComment(String feedbackResponseId, String giverEmail, Date createdAt) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, createdAt);
		
		FeedbackResponseComment frc = 
				getFeedbackResponseCommentEntity(feedbackResponseId, giverEmail, createdAt);
		
		if (frc == null) {
			log.info("Trying to get non-existent response comment: " +
					feedbackResponseId + "/from: " + giverEmail
					+ "created at: " + createdAt);
			return null;
		}
		
		return new FeedbackResponseCommentAttributes(frc);	
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null.
	 */
	public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForSession(String courseId, String feedbackSessionName) {
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
		
		List<FeedbackResponseComment> frcList = 
				getFeedbackResponseCommentEntitiesForSession(courseId, feedbackSessionName);
		
		List<FeedbackResponseCommentAttributes> resultList = new ArrayList<FeedbackResponseCommentAttributes>();
		for (FeedbackResponseComment frc : frcList) {
			resultList.add(new FeedbackResponseCommentAttributes(frc));
		}
		
		return resultList;	
	}
	
	
	@Override
	protected Object getEntity(EntityAttributes attributes) {
		FeedbackResponseCommentAttributes feedbackResponseCommentToGet =
				(FeedbackResponseCommentAttributes) attributes;
		
		if (feedbackResponseCommentToGet.getId() != null) {
			return getFeedbackResponseCommentEntity(feedbackResponseCommentToGet.getId());
		} else { 
			return getFeedbackResponseCommentEntity(
				feedbackResponseCommentToGet.feedbackResponseId,
				feedbackResponseCommentToGet.giverEmail,
				feedbackResponseCommentToGet.createdAt);
		}
	}

	private FeedbackResponseComment getFeedbackResponseCommentEntity(Long feedbackResponseCommentId) {
		Query q = getPM().newQuery(FeedbackResponseComment.class);
		q.declareParameters("String feedbackResponseCommentIdParam");
		q.setFilter("feedbackResponseCommentId == feedbackResponseCommentIdParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponseComment> feedbackResponseCommentList =
			(List<FeedbackResponseComment>) q.execute(feedbackResponseCommentId);
		
		if (feedbackResponseCommentList.isEmpty() || JDOHelper.isDeleted(feedbackResponseCommentList.get(0))) {
			return null;
		}
	
		return feedbackResponseCommentList.get(0);
	}
	
	private FeedbackResponseComment getFeedbackResponseCommentEntity(
			String feedbackResponseId, String giverEmail, Date createdAt) {
		
		Query q = getPM().newQuery(FeedbackResponseComment.class);
		q.declareParameters("String feedbackResponseIdParam, " +
				"String giverEmailParam, java.util.Date createdAtParam");
		q.setFilter("feedbackResponseId == feedbackResponseIdParam && " +
				"giverEmail == giverEmailParam && " +
				"createdAt == createdAtParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponseComment> feedbackResponseCommentList =
			(List<FeedbackResponseComment>) q.execute(feedbackResponseId, giverEmail, createdAt);
		
		if (feedbackResponseCommentList.isEmpty() || JDOHelper.isDeleted(feedbackResponseCommentList.get(0))) {
			return null;
		}
	
		return feedbackResponseCommentList.get(0);
	}
	
	private List<FeedbackResponseComment> getFeedbackResponseCommentEntitiesForSession(
			String courseId, String feedbackSessionName) {
		
		Query q = getPM().newQuery(FeedbackResponseComment.class);
		q.declareParameters("String courseIdParam, String feedbackSessionNameParam");
		q.setFilter("courseId == courseIdParam && " +
				"feedbackSessionName == feedbackSessionNameParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponseComment> feedbackResponseCommentList =
			(List<FeedbackResponseComment>) q.execute(courseId, feedbackSessionName);
		
		List<FeedbackResponseComment> resultList = new ArrayList<FeedbackResponseComment>();
		for (FeedbackResponseComment frc : feedbackResponseCommentList) {
			if (!JDOHelper.isDeleted(frc)) {
				resultList.add(frc);
			}
		}
		
		return resultList;
	}
}
