package teammates.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import teammates.common.Common;
import teammates.common.FeedbackParticipantType;
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
			if (isFeedbackSessionViewableTo(session, userEmail) == true) {
				viewableSessions.add(session);
				log.info(session.getIdentificationString() + " is viewable!");
			}
		}

		return viewableSessions;
	}
	
	// This method returns a list of feedback sessions which are relevant for a user.
	private boolean isFeedbackSessionViewableTo(FeedbackSessionAttributes session,
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
	public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsForUser(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException {

		FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
				feedbackSessionName, courseId);

		if (fsa == null) {
			throw new EntityDoesNotExistException(
					"Trying to get a feedback session that does not exist.");
		}

		if (fsa.isVisible() == false) {
			throw new UnauthorizedAccessException(
					"This feedback session is not yet visible.");
		}

		List<FeedbackQuestionAttributes> questions =
					fqLogic.getFeedbackQuestionsForUser(feedbackSessionName,
						courseId, userEmail);
		
		Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle
			= new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>(); 
		Map<String, Map<String,String>> recipientList
			= new HashMap<String, Map<String,String>>();
		
		for (FeedbackQuestionAttributes question : questions) {
			bundle.put(question,
					frLogic.getFeedbackResponsesFromGiver(question.getId(), userEmail));
			recipientList.put(question.getId(),
					fqLogic.getRecipientsForQuestion(question, userEmail));
		}
		
		return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
	}

	// This method gets the ResultsBundle i.e attributes + question + responses
	// for a single FS.
	public FeedbackSessionResultsBundle getFeedbackSessionResultsForUser(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException, UnauthorizedAccessException {

		FeedbackSessionAttributes session = fsDb.getFeedbackSession(
				feedbackSessionName, courseId);

		if (session == null) {
			throw new EntityDoesNotExistException(
					"Trying to view non-existent feedback session.");
		}
		
		// TODO: should we use isInstructor instead of creator?
		if (session.isPublished() == false && session.creatorEmail.equals(userEmail) == false) {
			throw new UnauthorizedAccessException(
					"This feedback sesion has not been published.");
		}
		
		List<FeedbackQuestionAttributes> allQuestions = 
				fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		List<FeedbackResponseAttributes> responses =
				new ArrayList<FeedbackResponseAttributes>();
		Map<String, FeedbackQuestionAttributes> relevantQuestions =
				new HashMap<String, FeedbackQuestionAttributes>();
		Map<String, String> emailNameTable =
				new HashMap<String, String>();
		Map<String, boolean[]> visibilityTable =
				new HashMap<String, boolean[]>();
		
		for (FeedbackQuestionAttributes question : allQuestions) {
			
			List<FeedbackResponseAttributes> responsesForThisQn;
			if (session.creatorEmail.equals(userEmail)) {
				// Allowing session creator to see all responses regardless of visibility.
				responsesForThisQn = frLogic.getFeedbackResponsesForQuestion(question.getId());
			} else {
				responsesForThisQn = frLogic.getViewableFeedbackResponsesForQuestion(
						question.getId(), userEmail);
			}

			if (responsesForThisQn.isEmpty() == false) {
				relevantQuestions.put(question.getId(), question);
				responses.addAll(responsesForThisQn);
				addEmailNamePairsToTable(emailNameTable, responsesForThisQn, question);
				addVisibilityToTable(visibilityTable, responsesForThisQn, userEmail);
			}
			
		}
		
		if (relevantQuestions.isEmpty()) {
			throw new UnauthorizedAccessException(
					"There are no questions that "+ userEmail +" can view the result of for feedback session:" +
					feedbackSessionName + "/" + courseId);
		}
		
		FeedbackSessionResultsBundle results = 
			new FeedbackSessionResultsBundle(session, responses, relevantQuestions, emailNameTable, visibilityTable);

		return results;
	}

	private void addVisibilityToTable(Map<String, boolean[]> visibilityTable,
			List<FeedbackResponseAttributes> responses,
			String userEmail) {
		for (FeedbackResponseAttributes response  : responses) {
			boolean[] visibility = new boolean[2];
			visibility[0] = frLogic.isNameVisibleTo(response, userEmail, true);
			visibility[1] = frLogic.isNameVisibleTo(response, userEmail, false);
			visibilityTable.put(response.getId(), visibility);
		}
	}

	private void addEmailNamePairsToTable(Map<String, String> emailNameTable,
			List<FeedbackResponseAttributes> responsesForThisQn,
			FeedbackQuestionAttributes question) throws EntityDoesNotExistException {
		
		for (FeedbackResponseAttributes response : responsesForThisQn) {
			if (question.giverType == FeedbackParticipantType.TEAMS){
				if (emailNameTable.containsKey(response.giverEmail + Common.TEAM_OF_EMAIL_OWNER) == false) {
					emailNameTable.put(
							response.giverEmail + Common.TEAM_OF_EMAIL_OWNER,
							getNameForEmail(question.giverType, response.giverEmail, question.courseId));
				}
			}
			else if(emailNameTable.containsKey(response.giverEmail) == false) {
				emailNameTable.put(
						response.giverEmail,
						getNameForEmail(question.giverType, response.giverEmail, question.courseId));
			}
			
			if(emailNameTable.containsKey(response.recipient) == false) {
				emailNameTable.put(
						response.recipient,
						getNameForEmail(question.recipientType, response.recipient, question.courseId));
			}
		}
		
	}

	private String getNameForEmail(FeedbackParticipantType type, String email, String courseId)
			throws EntityDoesNotExistException {
		
		String name = null;
		String team = null;
		
		StudentAttributes student = 
				studentsLogic.getStudentForEmail(courseId, email);
		if(student == null) {
			InstructorAttributes instructor =
					instructorsLogic.getInstructorForEmail(courseId, email);
			if (instructor == null) {
				// Assume that the email is actually a team name.
				name = "Unknown user";
				team = email;
			} else {
				name = instructor.name;
				team = "Instructors";
			}
		} else {
			name = student.name;
			team = student.team;
		}
		
		if (type == FeedbackParticipantType.TEAMS){
			return team;
		} else {
			return name;
		}
	}

	// This method is for manual adding of additional responses to a FS.
	public void addResponsesToFeedbackSession(List<FeedbackResponse> responses,
			String feedbackSessionName, String courseId)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Can't do manual adding of responses yet");
	}

	// TODO: String getFeedbackSessionResultsSummaryAsCsv
		
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
				&& newSession.isOpened() == false) {
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
		return (fs.creatorEmail.equals(userEmail));
	}

	public boolean isFeedbackSessionExists(String feedbackSessionName, String courseId) {
		return fsDb.getFeedbackSession(feedbackSessionName, courseId) != null;
	}
	
	private List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForCourse(
			String courseId)
			throws EntityDoesNotExistException {

		List<FeedbackSessionDetailsBundle> fsDetails =
				new ArrayList<FeedbackSessionDetailsBundle>();
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

		details.stats.expectedTotal = 0;
		details.stats.submittedTotal = 0;
		
		switch (fsa.feedbackSessionType) {
		case STANDARD:
			
			List<StudentAttributes> students = studentsLogic
					.getStudentsForCourse(fsa.courseId);
			List<InstructorAttributes> instructors = instructorsLogic
					.getInstructorsForCourse(fsa.courseId);
			
			for(StudentAttributes student : students) {
				List<FeedbackQuestionAttributes> questions = fqLogic.getFeedbackQuestionsForUser(
						fsa.feedbackSessionName, fsa.courseId, student.email);
				if (questions.isEmpty() == false) {
					details.stats.expectedTotal += 1;
					if (isFeedbackSessionCompletedByUser(fsa.feedbackSessionName,
							fsa.courseId, student.email)) {
						details.stats.submittedTotal += 1;
					}
				}				
			}
			for(InstructorAttributes instructor : instructors) {
				List<FeedbackQuestionAttributes> questions = fqLogic.getFeedbackQuestionsForUser(
						fsa.feedbackSessionName, fsa.courseId, instructor.email);
				if (questions.isEmpty() == false) {
					details.stats.expectedTotal += 1;
					if (isFeedbackSessionCompletedByUser(fsa.feedbackSessionName,
							fsa.courseId, instructor.email)) {
						details.stats.submittedTotal += 1;
					}
				}
			}
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

		return details;
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
			if(fqLogic.isQuestionAnsweredByUser(question, userEmail)){
				// If any question is answered, session is complete.
				return true;
			}
		}
		return false;
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