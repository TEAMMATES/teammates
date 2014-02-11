package teammates.storage.api;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.JDOHelper;
import javax.jdo.Query;

import teammates.common.datatransfer.EntityAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.entity.FeedbackSession;

public class FeedbackSessionsDb extends EntitiesDb {
	
	public static final String ERROR_UPDATE_NON_EXISTENT = "Trying to update non-existent Feedback Session : ";
	private static final Logger log = Utils.getLogger();

	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return Null if not found.
	 */
	public FeedbackSessionAttributes getFeedbackSession(String courseId, String feedbackSessionName) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, feedbackSessionName);
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		
		FeedbackSession fs = getFeedbackSessionEntity(feedbackSessionName, courseId);
		
		if (fs == null) {
			log.info("Trying to get non-existent Session: " + feedbackSessionName + "/" + courseId);
			return null;
		}
		return new FeedbackSessionAttributes(fs);	
		
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return An empty list if no non-private sessions are found.
	 */
	public List<FeedbackSessionAttributes> getNonPrivateFeedbackSessions() {
		
		List<FeedbackSession> fsList = getNonPrivateFeedbackSessionEntities();
		List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();
		
		for (FeedbackSession fs : fsList) {
			fsaList.add(new FeedbackSessionAttributes(fs));
		}
		return fsaList;
	}
		
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return An empty list if no sessions are found for the given course.
	 */
	public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(String courseId) {
		
		Assumption.assertNotNull(Const.StatusCodes.DBLEVEL_NULL_INPUT, courseId);
		
		List<FeedbackSession> fsList = getFeedbackSessionEntitiesForCourse(courseId);
		List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();
		
		for (FeedbackSession fs : fsList) {
			fsaList.add(new FeedbackSessionAttributes(fs));
		}
		return fsaList;
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return An empty list if no sessions are found that have unsent open emails.
	 */
	public List<FeedbackSessionAttributes> getFeedbackSessionsWithUnsentOpenEmail() {
				
		List<FeedbackSession> fsList = getFeedbackSessionEntitiesWithUnsentOpenEmail();
		List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();
		
		for (FeedbackSession fs : fsList) {
			fsaList.add(new FeedbackSessionAttributes(fs));
		}
		return fsaList;
	}
	
	/**
	 * Preconditions: <br>
	 * * All parameters are non-null. 
	 * @return An empty list if no sessions are found that have unsent published emails.
	 */
	public List<FeedbackSessionAttributes> getFeedbackSessionsWithUnsentPublishedEmail() {
		
		
		List<FeedbackSession> fsList = getFeedbackSessionEntitiesWithUnsentPublishedEmail();
		List<FeedbackSessionAttributes> fsaList = new ArrayList<FeedbackSessionAttributes>();
		
		for (FeedbackSession fs : fsList) {
			fsaList.add(new FeedbackSessionAttributes(fs));
		}
		return fsaList;
	}
	
	/**
	 * Updates the feedback session identified by {@code newAttributes.feedbackSesionName} 
	 * and {@code newAttributes.courseId}. 
	 * For the remaining parameters, the existing value is preserved 
	 *   if the parameter is null (due to 'keep existing' policy).<br> 
	 * Preconditions: <br>
	 * * {@code newAttributes.feedbackSesionName} and {@code newAttributes.courseId}
	 *  are non-null and correspond to an existing feedback session. <br>
	 */
	public void updateFeedbackSession(FeedbackSessionAttributes newAttributes) 
		throws InvalidParametersException, EntityDoesNotExistException {
		
		Assumption.assertNotNull(
				Const.StatusCodes.DBLEVEL_NULL_INPUT, 
				newAttributes);
		
		//TODO: Sanitize values and update tests accordingly
		
		if (!newAttributes.isValid()) {
			throw new InvalidParametersException(newAttributes.getInvalidityInfo());
		}
		
		FeedbackSession fs = (FeedbackSession) getEntity(newAttributes);
		
		if (fs == null) {
			throw new EntityDoesNotExistException(
					ERROR_UPDATE_NON_EXISTENT + newAttributes.toString());
		}
		fs.setInstructions(newAttributes.instructions);
		fs.setStartTime(newAttributes.startTime);
		fs.setEndTime(newAttributes.endTime);
		fs.setSessionVisibleFromTime(newAttributes.sessionVisibleFromTime);
		fs.setResultsVisibleFromTime(newAttributes.resultsVisibleFromTime);
		fs.setTimeZone(newAttributes.timeZone);
		fs.setGracePeriod(newAttributes.gracePeriod);
		fs.setFeedbackSessionType(newAttributes.feedbackSessionType);
		fs.setSentOpenEmail(newAttributes.sentOpenEmail);
		fs.setSentPublishedEmail(newAttributes.sentPublishedEmail);
		fs.setIsOpeningEmailEnabled(newAttributes.isOpeningEmailEnabled);
		fs.setSendClosingEmail(newAttributes.isClosingEmailEnabled);
		fs.setSendPublishedEmail(newAttributes.isPublishedEmailEnabled);
				
		getPM().close();
	}
	
	private List<FeedbackSession> getNonPrivateFeedbackSessionEntities() {		
		Query q = getPM().newQuery(FeedbackSession.class);
		q.declareParameters("Enum private");
		q.setFilter("feedbackSessionType != private");
		
		@SuppressWarnings("unchecked")
		List<FeedbackSession> fsList = (List<FeedbackSession>) q.execute(FeedbackSessionType.PRIVATE);
		return fsList;
	}
	
	private List<FeedbackSession> getFeedbackSessionEntitiesForCourse(String courseId) {		
		Query q = getPM().newQuery(FeedbackSession.class);
		q.declareParameters("String courseIdParam");
		q.setFilter("courseId == courseIdParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackSession> fsList = (List<FeedbackSession>) q.execute(courseId);
		return fsList;
	}
	
	private List<FeedbackSession> getFeedbackSessionEntitiesWithUnsentOpenEmail() {
		Query q = getPM().newQuery(FeedbackSession.class);
		q.declareParameters("boolean sentParam, Enum notTypeParam");
		q.setFilter("sentOpenEmail == sentParam && feedbackSessionType != notTypeParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackSession> fsList = (List<FeedbackSession>) q.execute(false, FeedbackSessionType.PRIVATE);
		return fsList;
	}	
	
	private List<FeedbackSession> getFeedbackSessionEntitiesWithUnsentPublishedEmail() {		
		Query q = getPM().newQuery(FeedbackSession.class);
		q.declareParameters("boolean sentParam, Enum notTypeParam");
		q.setFilter("sentPublishedEmail == sentParam && feedbackSessionType != notTypeParam");
		
		@SuppressWarnings("unchecked")
		List<FeedbackSession> fsList = (List<FeedbackSession>) q.execute(false, FeedbackSessionType.PRIVATE);
		return fsList;
	}
	
	private FeedbackSession getFeedbackSessionEntity(String feedbackSessionName, String courseId) {
		
		Query q = getPM().newQuery(FeedbackSession.class);
		q.declareParameters("String feedbackSessionNameParam, String courseIdParam");
		q.setFilter("feedbackSessionName == feedbackSessionNameParam && courseId == courseIdParam");

		@SuppressWarnings("unchecked")
		List<FeedbackSession> feedbackSessionList =
				(List<FeedbackSession>) q.execute(feedbackSessionName, courseId);
		
		if (feedbackSessionList.isEmpty() || JDOHelper.isDeleted(feedbackSessionList.get(0))) {
			return null;
		}
	
		return feedbackSessionList.get(0);
	}

	@Override
	protected Object getEntity(EntityAttributes attributes) {
		FeedbackSessionAttributes feedbackSessionToGet = (FeedbackSessionAttributes) attributes;
		return getFeedbackSessionEntity(feedbackSessionToGet.feedbackSessionName, feedbackSessionToGet.courseId);
	}	
}
