package teammates.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.NotImplementedException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.entity.FeedbackResponse;
import teammates.storage.entity.FeedbackSession.FeedbackSessionType;

public class FeedbackSessionsLogic {

	private static final Logger log = Common.getLogger();

	private static FeedbackSessionsLogic instance = null;

	private static final FeedbackSessionsDb fsDb = new FeedbackSessionsDb();
	private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
	private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
	private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
	private static final CoursesLogic coursesLogic = CoursesLogic.inst();
	private static final StudentsLogic studentsLogic = StudentsLogic.inst();

	public static FeedbackSessionsLogic inst() {
		if (instance == null)
			instance = new FeedbackSessionsLogic();
		return instance;
	}

	public void createFeedbackSession(FeedbackSessionAttributes fsa)
			throws InvalidParametersException, EntityAlreadyExistsException {
		fsDb.createEntity(fsa);
	}

	// This method deletes a specific feedback session
	// No cascade done to preserve feedback responses already made?
	public void deleteFeedbackSession(String feedbackSessionName,
			String courseId) {

		FeedbackSessionAttributes fsa = new FeedbackSessionAttributes();
		fsa.feedbackSessionName = feedbackSessionName;
		fsa.courseId = courseId;
		fsDb.deleteEntity(fsa);

	}

	// This method returns a single feedback session. Returns null if not found.
	public FeedbackSessionAttributes getFeedbackSession(
			String feedbackSessionName, String courseId) {
		return fsDb.getFeedbackSession(feedbackSessionName, courseId);
	}
	
	// This method returns a list of feedback session with stats for
	// instructors.
	// Make sure the call for this passes through a GateKeeper.
	public List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForInstructor(
			String googleId)
			throws EntityDoesNotExistException {

		List<FeedbackSessionDetailsBundle> fsDetails = new ArrayList<FeedbackSessionDetailsBundle>();
		List<InstructorAttributes> instructors = 
				instructorsLogic.getInstructorsForGoogleId(googleId);

		for (InstructorAttributes instructor : instructors) {
			fsDetails.addAll(getFeedbackSessionDetailsForCourse(instructor.courseId));
		}

		return fsDetails;
	}

	// This method returns a list of viewable feedback sessions for any user for his course.
	public List<FeedbackSessionAttributes> getFeedbackSessionsForCourse(
			String courseId, String userEmail) throws EntityDoesNotExistException {

		if (coursesLogic.isCoursePresent(courseId) == false) {
			throw new EntityDoesNotExistException(
					"Trying to get feedback sessions for a course that does not exist.");
		}
		
		List<FeedbackSessionAttributes> sessions = 
				fsDb.getFeedbackSessionsForCourse(courseId);
		List<FeedbackSessionAttributes> viewableSessions = new ArrayList<FeedbackSessionAttributes>();
		log.info("FOUND: " + sessions.size() + " sessions for course.");
		for (FeedbackSessionAttributes session : sessions) {
			if (isFeedbackViewableTo(session, userEmail) == true) {
				viewableSessions.add(session);
				log.info(session.getIdentificationString() + " is viewable!");
			}
		}

		return viewableSessions;
	}
	
	// This method returns a list of viewable feedback sessions for any user.
	private boolean isFeedbackViewableTo(FeedbackSessionAttributes session,
			String userEmail) throws EntityDoesNotExistException {
				
		if (fsDb.getFeedbackSession(
				session.feedbackSessionName,
				session.courseId) == null) {
			throw new EntityDoesNotExistException(
					"Trying to get a feedback session that does not exist.");
		}
		
		// Check for private type first.
		if (session.feedbackSessionType == FeedbackSessionType.PRIVATE) {
			if (session.creatorEmail.equals(userEmail)) {
				return true;
			} else {
				return false;
			}
		}
		
		// Allow all instructors to view
		InstructorAttributes instructor = instructorsLogic.
				getInstructorForEmail(session.courseId, userEmail);
		if (instructor != null) {
			if (instructorsLogic.isInstructorOfCourse(instructor.googleId, session.courseId)) {
				return true;
			}
		}

		if (session.isPublished() == false) {
			
			List<FeedbackQuestionAttributes> questions = 
					fqLogic.getFeedbackQuestionsForUser(
							session.feedbackSessionName,
							session.courseId, userEmail);
			
			if (questions.isEmpty() == false) {
				// Session should be visible only if there are questions
				// available for this student/feedback session.
				return true;
			}

		} else {
			// After publishing, only list if there are
			// non-zero viewable responses			
			try {
				getFeedbackSessionResultsForUser(session.feedbackSessionName,
						session.courseId, userEmail);
			} catch (UnauthorizedAccessException e) {
				// We will not be given access if there are zero
				// viewable responses.
				return false;
			}			
			return true;
		}

		return false;
	}
	
	// This method returns a single feedback session with
	// it's questions for the user to fill up responses.
	public FeedbackSessionQuestionsBundle getFeedbackSessionBundleForUser(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException {

		FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
				feedbackSessionName, courseId);

		if (fsa == null) {
			throw new EntityDoesNotExistException(
					"Trying to get a feedback session that does not exist.");
		}

		if (fsa.isClosed()) {
			throw new UnauthorizedAccessException(
					"This feedback session is already closed.");
		}

		List<FeedbackQuestionAttributes> questions =
					fqLogic.getFeedbackQuestionsForUser(feedbackSessionName,
						courseId, userEmail);

		if (questions.isEmpty()) {
			throw new UnauthorizedAccessException(
					"There are no questions for you to answer for this session.");
		}
		
		return new FeedbackSessionQuestionsBundle(fsa, questions);

	}

	// This method gets the ResultsBundle i.e attributes + question + responses
	// for a single FS.
	public FeedbackSessionResultsBundle getFeedbackSessionResultsForUser(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException, UnauthorizedAccessException {

		FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
				feedbackSessionName, courseId);

		if (fsa == null) {
			throw new EntityDoesNotExistException(
					"Trying to view non-existent feedback session.");
		}
		
		// TODO: should we use isInstructor instead of creator?
		if (fsa.isPublished() == false && fsa.creatorEmail.equals(userEmail) == false) {
			throw new UnauthorizedAccessException(
					"This feedback sesion has not been published!");
		}
		
		List<FeedbackQuestionAttributes> allQuestions = 
				fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		List<FeedbackQuestionAttributes> relevantQuestions =
				new ArrayList<FeedbackQuestionAttributes>();	
		List<FeedbackResponseAttributes> responses =
				new ArrayList<FeedbackResponseAttributes>();
		
		for (FeedbackQuestionAttributes question : allQuestions) {
			
			List<FeedbackResponseAttributes> responsesForThisQn =
					frLogic.getViewableFeedbackResponsesForQuestion(
							question.getId(), userEmail);
										
			if (responsesForThisQn.isEmpty() == false) {
				responses.addAll(responsesForThisQn);
				relevantQuestions.add(question);
			}
			
		}
		
		if (relevantQuestions.isEmpty()) {
			throw new UnauthorizedAccessException(
					"There is nothing for you to see here.");
		}
		
		FeedbackSessionResultsBundle results = 
			new FeedbackSessionResultsBundle(fsa, relevantQuestions, responses);

		// TODO: Do sorting within results bundle.

		return results;
	}

	// This method is for manual adding of additional responses to a FS.
	public void addResponsesToFeedbackSession(List<FeedbackResponse> responses,
			String feedbackSessionName, String courseId)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Can't do manual adding of responses yet");
	}

	// TODO: String getFeedbackSessionResultsSummaryAsCsv
	
	// TODO: List<String> getPossibleReceiversForQuestion(String feedbackQuestionId, String giverEmail)
	
	// TODO: int getRemainingNumberOfReceiverEntities(String feedbackQuestionId, String giverEmail)

	public void updateFeedbackSession(FeedbackSessionAttributes newSession)
			throws InvalidParametersException, EntityDoesNotExistException {

		FeedbackSessionAttributes oldSession =
				fsDb.getFeedbackSession(newSession.feedbackSessionName,
						newSession.courseId);

		if (oldSession == null) {
			throw new EntityDoesNotExistException(
					"Trying to update a feedback session that does not exist.");
		}
		
		// These can't be changed anyway. Copy values to defensively avoid invalid parameters.
		newSession.creatorEmail = oldSession.creatorEmail;
		newSession.createdTime = oldSession.createdTime;
		
		if (newSession.instructions == null) {
			newSession.instructions = oldSession.instructions;
		}
		if (newSession.startTime == null) {
			newSession.startTime = oldSession.startTime;
		}
		if (newSession.endTime == null) {
			newSession.endTime = oldSession.endTime;
		}
		if (newSession.sessionVisibleFromTime == null) {
			newSession.sessionVisibleFromTime = oldSession.sessionVisibleFromTime; 
		}
		if (newSession.resultsVisibleFromTime == null) {
			newSession.resultsVisibleFromTime = oldSession.resultsVisibleFromTime; 
		}
		if (newSession.feedbackSessionType == null) {
			newSession.feedbackSessionType = oldSession.feedbackSessionType; 
		}
		makeEmailStateConsistent(oldSession, newSession);
		
		fsDb.updateFeedbackSession(newSession);
	}

	private void makeEmailStateConsistent(FeedbackSessionAttributes oldSession,
			FeedbackSessionAttributes newSession) {
		
		// reset sentOpenEmail if the session has opened but is being closed and
		// postponed.
		if (oldSession.sentOpenEmail == true
				&& newSession.startTime.after(oldSession.startTime)
				&& newSession.isStarted() == false) {
			newSession.sentOpenEmail = false;
		} else { 
			// or else the sent state should remain the same.
			newSession.sentOpenEmail = oldSession.sentOpenEmail;
		}

		// reset sentPublishedEmail if the session has been published but is
		// going to be unpublished now.
		if (oldSession.sentPublishedEmail == true
				&& newSession.resultsVisibleFromTime
						.after(oldSession.resultsVisibleFromTime)
				&& newSession.isPublished() == false) {
			newSession.sentPublishedEmail = false;
		} else {
			// or else the sent state should remain the same.
			newSession.sentPublishedEmail = oldSession.sentPublishedEmail;
		}
	}

	public void sendFeedbackResultsPublishedEmail() {
		// TODO
	}

	public void sendFeedbackSessionOpenEmail() {
		// TODO
	}
	
	public boolean isCreatorOfSession(String feedbackSessionName, String courseId, String userEmail) {
		FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
		return (fs.creatorEmail == userEmail);
	}

	public boolean isFeedbackSessionExists(String feedbackSessionName, String courseId) {
		return fsDb.getFeedbackSession(feedbackSessionName, courseId) != null;
	}
	
	private List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForCourse(
			String courseId)
			throws EntityDoesNotExistException {

		List<FeedbackSessionDetailsBundle> fsDetails = new ArrayList<FeedbackSessionDetailsBundle>();
		List<FeedbackSessionAttributes> fsInCourse =
				fsDb.getFeedbackSessionsForCourse(courseId);

		for (FeedbackSessionAttributes fsa : fsInCourse) {
			fsDetails.add(getFeedbackSessionDetails(fsa));
		}

		return fsDetails;
	}

	private FeedbackSessionDetailsBundle getFeedbackSessionDetails(
			FeedbackSessionAttributes fsa) throws EntityDoesNotExistException {

		FeedbackSessionDetailsBundle details =
				new FeedbackSessionDetailsBundle(fsa);

		switch (fsa.feedbackSessionType) {
		case STANDARD:
			// TODO: Properly check if instructors should be counted as well by:
			// Get all questions
			// Get givers
			// Check if any of them are instructors
			List<StudentAttributes> students = studentsLogic
					.getStudentsForCourse(fsa.courseId);
			List<InstructorAttributes> instructors = instructorsLogic
					.getInstructorsForCourse(fsa.courseId);
			details.stats.expectedTotal += students.size();
			details.stats.expectedTotal += instructors.size();

			int usersSubmitted = 0;
			for (StudentAttributes student : students) {
				if (isFeedbackSessionCompletedByUser(fsa.feedbackSessionName,
						fsa.courseId, student.email)) {
					usersSubmitted += 1;
				}
			}
			for (InstructorAttributes instructor : instructors) {
				if (isFeedbackSessionCompletedByUser(fsa.feedbackSessionName,
						fsa.courseId, instructor.email)) {
					usersSubmitted += 1;
				}
			}
			details.stats.submittedTotal = usersSubmitted;
			break;

		case TEAM:
			details.stats.expectedTotal = coursesLogic
					.getNumberOfTeams(fsa.courseId);
			List<TeamDetailsBundle> teams = coursesLogic
					.getTeamsForCourse(fsa.courseId).teams;

			int teamsSubmitted = 0;
			for (TeamDetailsBundle team : teams) {
				if (isFeedbackSessionCompletedByTeam(fsa.feedbackSessionName,
						fsa.courseId, team.name)) {
					teamsSubmitted += 1;
				}
			}
			details.stats.submittedTotal = teamsSubmitted;
			break;

		default:
			break;
		}

		return null;
	}

	private boolean isFeedbackSessionCompletedByUser(String feedbackSessionName,
			String courseId, String userEmail)
			throws EntityDoesNotExistException {

		if (isFeedbackSessionExists(feedbackSessionName, courseId) == false) {
			throw new EntityDoesNotExistException(
					"Trying to check a feedback session that does not exist.");
		}

		List<FeedbackQuestionAttributes> allQuestions =
				fqLogic.getFeedbackQuestionsForUser(feedbackSessionName, courseId,
						userEmail);
		
		for (FeedbackQuestionAttributes question : allQuestions) {
			if(fqLogic.isQuestionAnsweredByUser(
					question.getId(), userEmail) == false){
				// If any question is unanswered, session is incomplete.
				return false;
			}
		}
		return true;
	}

	private boolean isFeedbackSessionCompletedByTeam(String feedbackSessionName,
			String courseId, String teamName)
			throws EntityDoesNotExistException {

		if (isFeedbackSessionExists(feedbackSessionName, courseId) == false) {
			throw new EntityDoesNotExistException(
					"Trying to check a feedback session that does not exist.");
		}

		List<FeedbackQuestionAttributes> questions =
				fqLogic.getFeedbackQuestionsForTeam(
						feedbackSessionName, courseId, teamName);
		
		return questions.isEmpty();
	}
}