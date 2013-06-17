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
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackResponsesDb extends EntitiesDb {

	private static final Logger log = Common.getLogger();

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
		
		Assumption.assertNotNull(Common.ERROR_DBLEVEL_NULL_INPUT, feedbackResponseId);
		
		FeedbackResponse fr = 
				getFeedbackResponseEntity(feedbackResponseId);
		
		if (fr == null) {
			log.info("Trying to get non-existent response: " +
					feedbackResponseId + ".");
			return null;
		}
		
		return new FeedbackResponseAttributes(fr);	
	}

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
	
	/**
	 * Updates the feedback response identified by {@code newAttributes.getId()} 
	 * For the remaining parameters, the existing value is preserved 
	 *   if the parameter is null (due to 'keep existing' policy).<br> 
	 * Preconditions: <br>
	 * * {@code newAttributes.getId()} is non-null and correspond to an existing feedback response.
	 */
	public void updateFeedbackResponse(FeedbackResponseAttributes newAttributes) 
		throws InvalidParametersException, EntityDoesNotExistException {
		
		Assumption.assertNotNull(
				Common.ERROR_DBLEVEL_NULL_INPUT, 
				newAttributes);
		
		if (!newAttributes.isValid()) {
			throw new InvalidParametersException(newAttributes.getInvalidityInfo());
		}
		
		FeedbackResponse fr = (FeedbackResponse) getEntity(newAttributes);
		
		if (fr == null) {
			throw new EntityDoesNotExistException(
					ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
		}
		
		fr.setAnswer(newAttributes.answer);
		fr.setRecipient(newAttributes.recipient);
				
		getPM().close();
	}
	
	
	private FeedbackResponse getFeedbackResponseEntity(String feedbackResponseId) {
		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String feedbackResponseIdParam");
		q.setFilter("feedbackResponseId == feedbackResponseIdParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackResponseId);
		
		if (FeedbackResponseList.isEmpty() || JDOHelper.isDeleted(FeedbackResponseList.get(0))) {
			return null;
		}
	
		return FeedbackResponseList.get(0);
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
		
		return FeedbackResponseList;
	}
	
	@Override
	protected Object getEntity(EntityAttributes attributes) {
		
		FeedbackResponseAttributes FeedbackResponseToGet =
				(FeedbackResponseAttributes) attributes;
		
		if (FeedbackResponseToGet.getId() != null) {
			return getFeedbackResponseEntity(FeedbackResponseToGet.getId());
		} else { 
			return getFeedbackResponseEntity(
				FeedbackResponseToGet.feedbackQuestionId,
				FeedbackResponseToGet.giverEmail,
				FeedbackResponseToGet.recipient);
		}
	}
}
