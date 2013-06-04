package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackResponsesDb extends EntitiesDb {

	private static final Logger log = Common.getLogger();

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public FeedbackResponseAttributes getFeedbackResponse (
			String feedbackQuestionId, String giverEmail, String receiver) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, feedbackQuestionId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, receiver);
		
		FeedbackResponse fr = 
				getFeedbackResponseEntity(feedbackQuestionId, giverEmail, receiver);
		
		if (fr == null) {
			log.info("Trying to get non-existent response: " +
					feedbackQuestionId + "/" + "from: " +
					giverEmail + " to: " + receiver );
			return null;
		}
		return new FeedbackResponseAttributes(fr);		
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return An empty list if no such responses are found.
	 */
	public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion (
			String feedbackQuestionId) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, feedbackQuestionId);
		
		List<FeedbackResponse> frList =
				getFeedbackResponseEntitiesForQuestion(feedbackQuestionId);
		List<FeedbackResponseAttributes> fraList =
				new ArrayList<FeedbackResponseAttributes>();
		
		for (FeedbackResponse fr : frList) {
				fraList.add(new FeedbackResponseAttributes(fr));
		}
		
		return fraList;		
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return An empty list if no such responses are found.
	 */
	public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
			String feedbackSessionName, String courseId) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, feedbackSessionName);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, courseId);
		
		List<FeedbackResponse> frList =
				getFeedbackResponseEntitiesForSession(feedbackSessionName, courseId);
		List<FeedbackResponseAttributes> fraList =
				new ArrayList<FeedbackResponseAttributes>();
		
		for (FeedbackResponse fr : frList) {
				fraList.add(new FeedbackResponseAttributes(fr));
		}
		
		return fraList;		
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return An empty list if no such responses are found.
	 */
	public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiver (
			String feedbackQuestionId, String receiver) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, feedbackQuestionId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, receiver);

		
		List<FeedbackResponse> frList =
				getFeedbackResponseEntitiesForReceiver(feedbackQuestionId, receiver);
		List<FeedbackResponseAttributes> fraList =
				new ArrayList<FeedbackResponseAttributes>();
		
		for (FeedbackResponse fr : frList) {
				fraList.add(new FeedbackResponseAttributes(fr));
		}
		
		return fraList;
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return An empty list if no such responses are found.
	 */
	public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiver (
			String feedbackQuestionId, String giverEmail) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, feedbackQuestionId);
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, giverEmail);

		
		List<FeedbackResponse> frList =
				getFeedbackResponseEntitiesForGiver(feedbackQuestionId, giverEmail);
		List<FeedbackResponseAttributes> fraList =
				new ArrayList<FeedbackResponseAttributes>();
		
		for (FeedbackResponse fr : frList) {
				fraList.add(new FeedbackResponseAttributes(fr));
		}
		
		return fraList;
	}
		
	private FeedbackResponse getFeedbackResponseEntity(
			String feedbackQuestionId, String giverEmail, String receiver) {
		
		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String feedbackQuestionIdParam, " +
				"String giverEmailParam, String receiverParam");
		q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && " +
				"giverEmail == giverEmailParam && " +
				"receiver == receiverParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail, receiver);
		
		if (FeedbackResponseList.isEmpty() || JDOHelper.isDeleted(FeedbackResponseList.get(0))) {
			return null;
		}
	
		return FeedbackResponseList.get(0);
	}
		
	private List<FeedbackResponse> getFeedbackResponseEntitiesForQuestion(	
				String feedbackQuestionId) {
	
		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String feedbackQuestionIdParam");
		q.setFilter("feedbackQuestionId == feedbackQuestionIdParam ");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackQuestionId);
		
		if (FeedbackResponseList.isEmpty() || JDOHelper.isDeleted(FeedbackResponseList.get(0))) {
			return null;
		}
		
		return FeedbackResponseList;
	}
	
	private List<FeedbackResponse> getFeedbackResponseEntitiesForSession(
			String feedbackSessionName, String courseId) {
		
		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
		q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId = courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackSessionName, courseId);
		
		if (FeedbackResponseList.isEmpty() || JDOHelper.isDeleted(FeedbackResponseList.get(0))) {
			return null;
		}
		
		return FeedbackResponseList;
	}
	
	private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiver(
			String feedbackQuestionId, String receiver) {

		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String feedbackQuestionIdParam, String receiverParam");
		q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver);
		
		if (FeedbackResponseList.isEmpty() || JDOHelper.isDeleted(FeedbackResponseList.get(0))) {
			return null;
		}
		
		return FeedbackResponseList;
	}
	
	private List<FeedbackResponse> getFeedbackResponseEntitiesForGiver(
			String feedbackQuestionId, String giverEmail) {

		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam");
		q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail);
		
		if (FeedbackResponseList.isEmpty() || JDOHelper.isDeleted(FeedbackResponseList.get(0))) {
			return null;
		}
		
		return FeedbackResponseList;
	}
	
	@Override
	protected Object getEntity(EntityAttributes attributes) {
		
		FeedbackResponseAttributes FeedbackResponseToGet =
				(FeedbackResponseAttributes) attributes;
		
		return getFeedbackResponseEntity(
				FeedbackResponseToGet.feedbackQuestionId,
				FeedbackResponseToGet.giverEmail,
				FeedbackResponseToGet.receiver);		
	}

}
