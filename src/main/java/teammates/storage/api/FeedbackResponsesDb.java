package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackResponsesDb extends EntitiesDb {

	private static final Logger log = Utils.getLogger();

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public FeedbackResponseAttributes getFeedbackResponse(String feedbackResponseId) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackResponseId);
		
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
			String feedbackQuestionId, String giverEmail, String receiverEmail) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiverEmail);
		
		FeedbackResponse fr = 
				getFeedbackResponseEntity(feedbackQuestionId, giverEmail, receiverEmail);
		
		if (fr == null) {
			log.info("Trying to get non-existent response: " +
					feedbackQuestionId + "/" + "from: " +
					giverEmail + " to: " + receiverEmail );
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
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
		
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
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		
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
	public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion (
			String feedbackQuestionId, String receiver) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

		
		List<FeedbackResponse> frList =
				getFeedbackResponseEntitiesForReceiverForQuestion(feedbackQuestionId, receiver);
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
	public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion (
			String feedbackQuestionId, String giverEmail) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackQuestionId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

		
		List<FeedbackResponse> frList =
				getFeedbackResponseEntitiesForGiverForQuestion(feedbackQuestionId, giverEmail);
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
	public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse (
			String courseId, String receiver) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, receiver);

		
		List<FeedbackResponse> frList =
				getFeedbackResponseEntitiesForReceiverForCourse(courseId, receiver);
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
	public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse (
			String courseId, String giverEmail) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, giverEmail);

		
		List<FeedbackResponse> frList =
				getFeedbackResponseEntitiesForGiverForCourse(courseId, giverEmail);
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
				Const.StatusCodes.DBLEVEL_NULL_INPUT, 
				newAttributes);
		
		//TODO: Sanitize values and update tests accordingly
		
		if (!newAttributes.isValid()) {
			throw new InvalidParametersException(newAttributes.getInvalidityInfo());
		}
		
		FeedbackResponse fr = (FeedbackResponse) getEntity(newAttributes);
		
		if (fr == null) {
			throw new EntityDoesNotExistException(
					ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
		}
		
		fr.setAnswer(newAttributes.responseMetaData);
		fr.setRecipientEmail(newAttributes.recipientEmail);
				
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
		q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackSessionName, courseId);
		
		return FeedbackResponseList;
	}
	
	private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForQuestion(
			String feedbackQuestionId, String receiver) {

		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String feedbackQuestionIdParam, String receiverParam");
		q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && receiver == receiverParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackQuestionId, receiver);
		
		return FeedbackResponseList;
	}
	
	private List<FeedbackResponse> getFeedbackResponseEntitiesForGiverForQuestion(
			String feedbackQuestionId, String giverEmail) {

		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String feedbackQuestionIdParam, String giverEmailParam");
		q.setFilter("feedbackQuestionId == feedbackQuestionIdParam && giverEmail == giverEmailParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(feedbackQuestionId, giverEmail);
		
		return FeedbackResponseList;
	}
	
	private List<FeedbackResponse> getFeedbackResponseEntitiesForReceiverForCourse(
			String courseId, String receiver) {

		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String courseIdParam, String receiverParam");
		q.setFilter("courseId == courseIdParam && receiver == receiverParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(courseId, receiver);
		
		return FeedbackResponseList;
	}
	
	private List<FeedbackResponse> getFeedbackResponseEntitiesForGiverForCourse(
			String courseId, String giverEmail) {

		Query q = getPM().newQuery(FeedbackResponse.class);
		q.declareParameters("String courseIdParam, String giverEmailParam");
		q.setFilter("courseId == courseIdParam && giverEmail == giverEmailParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackResponse> FeedbackResponseList =
			(List<FeedbackResponse>) q.execute(courseId, giverEmail);
		
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
				FeedbackResponseToGet.recipientEmail);
		}
	}
}
