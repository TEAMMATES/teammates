package teammates.logic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackSessionAttributes;
import teammates.common.datatransfer.FeedbackSessionDetailsBundle;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;
import teammates.common.datatransfer.FeedbackSessionResponseStatus;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.FeedbackSessionType;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.datatransfer.UserType;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.NotImplementedException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.Const.SystemParams;
import teammates.common.util.Sanitizer;
import teammates.common.util.TimeHelper;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackSessionsDb;
import teammates.storage.api.InstructorsDb;
import teammates.storage.api.StudentsDb;
import teammates.storage.entity.FeedbackResponse;

public class FeedbackSessionsLogic {

	private static FeedbackSessionsLogic instance = null;
	@SuppressWarnings("unused")
	private static Logger log = Utils.getLogger();
	//Used by the FeedbackSessionsLogicTest for logging

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
	
	//TODO: in general, try to reduce method length and nesting-level in Feedback*Logic classes.

	public void createFeedbackSession(FeedbackSessionAttributes fsa)
			throws InvalidParametersException, EntityAlreadyExistsException {
		fsDb.createEntity(fsa);
	}

	/**
	 *  This method returns a single feedback session. Returns null if not found.
	 */
	public FeedbackSessionAttributes getFeedbackSession(
			String feedbackSessionName, String courseId) {
		return fsDb.getFeedbackSession(courseId, feedbackSessionName);
	}

	/**
	 *  This method returns a list of viewable feedback sessions for any user for his course.
	 */
	public List<FeedbackSessionAttributes> getFeedbackSessionsForUserInCourse(
			String courseId, String userEmail) throws EntityDoesNotExistException {

		if (coursesLogic.isCoursePresent(courseId) == false) {
			throw new EntityDoesNotExistException(
					"Trying to get feedback sessions for a course that does not exist.");
		}
		
		List<FeedbackSessionAttributes> sessions = 
				fsDb.getFeedbackSessionsForCourse(courseId);
		List<FeedbackSessionAttributes> viewableSessions = new ArrayList<FeedbackSessionAttributes>();
		for (FeedbackSessionAttributes session : sessions) {
			if (isFeedbackSessionViewableTo(session, userEmail)) {
				viewableSessions.add(session);
			}
		}

		return viewableSessions;
	}
	
	/**
	 * Returns a {@code List} of all feedback sessions bundled with their response statistics for
	 * a instructor given by his googleId.<br>Does not return private sessions unless the instructor is
	 * the creator.
	 * @param googleId
	 * @return
	 * @throws EntityDoesNotExistException
	 */
	public List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForInstructor(
			String googleId)
			throws EntityDoesNotExistException {

		List<FeedbackSessionDetailsBundle> fsDetails = new ArrayList<FeedbackSessionDetailsBundle>();
		List<InstructorAttributes> instructors = 
				instructorsLogic.getInstructorsForGoogleId(googleId);

		for (InstructorAttributes instructor : instructors) {
			fsDetails.addAll(getFeedbackSessionDetailsForCourse(instructor.courseId, instructor.email));
		}

		return fsDetails;
	}
	
	/**
	 * Returns a {@code List} of all feedback sessions WITHOUT their response statistics for
	 * a instructor given by his googleId.<br>Does not return private sessions unless the instructor is
	 * the creator.
	 * @param googleId
	 * @return
	 * @throws EntityDoesNotExistException
	 */
	public List<FeedbackSessionAttributes> getFeedbackSessionsListForInstructor(
			String googleId)
			throws EntityDoesNotExistException {

		List<FeedbackSessionAttributes> fsList = new ArrayList<FeedbackSessionAttributes>();
		List<InstructorAttributes> instructors = 
				instructorsLogic.getInstructorsForGoogleId(googleId);

		for (InstructorAttributes instructor : instructors) {
			fsList.addAll(getFeedbackSessionsListForCourse(instructor.courseId, instructor.email));
		}

		return fsList;
	}
	
	/**
	 * Gets {@code FeedbackQuestions} and previously filled {@code FeedbackResponses} 
	 * that an instructor can view/submit as
	 * a {@link FeedbackSessionQuestionsBundle}
	 */
	public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsForInstructor(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException {

		FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
				courseId, feedbackSessionName);

		if (fsa == null) {
			throw new EntityDoesNotExistException(
					"Trying to get a feedback session that does not exist.");
		}
		
		Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle
			= new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>(); 
		Map<String, Map<String,String>> recipientList
			= new HashMap<String, Map<String,String>>();
		
		List<FeedbackQuestionAttributes> questions =
				fqLogic.getFeedbackQuestionsForInstructor(feedbackSessionName,
					courseId, userEmail);
		
		for (FeedbackQuestionAttributes question : questions) {
			
			List<FeedbackResponseAttributes> responses = 
					frLogic.getFeedbackResponsesFromGiverForQuestion(question.getId(), userEmail);
			Map<String, String> recipients =
					fqLogic.getRecipientsForQuestion(question, userEmail);			
			normalizeMaximumResponseEntities(question, recipients);			
			
			bundle.put(question, responses);
			recipientList.put(question.getId(), recipients);
		}
		
		return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
	}
	
	/**
	 * Gets {@code FeedbackQuestions} and previously filled {@code FeedbackResponses} 
	 * that a student can view/submit as
	 * a {@link FeedbackSessionQuestionsBundle}
	 */
	public FeedbackSessionQuestionsBundle getFeedbackSessionQuestionsForStudent(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException {

		FeedbackSessionAttributes fsa = fsDb.getFeedbackSession(
				courseId, feedbackSessionName);
		StudentAttributes student = studentsLogic.getStudentForEmail(courseId, userEmail);
		
		if (fsa == null) {
			throw new EntityDoesNotExistException(
					"Trying to get a feedback session that does not exist.");
		}
		if (student == null) {
			throw new EntityDoesNotExistException(
					"Trying to get a feedback session for student that does not exist.");
		}
		
		Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> bundle
			= new HashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>(); 
		Map<String, Map<String,String>> recipientList
			= new HashMap<String, Map<String,String>>();
		
		List<FeedbackQuestionAttributes> questions =
				fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
		
		for (FeedbackQuestionAttributes question : questions) {
			
			List<FeedbackResponseAttributes> responses =
					frLogic.getFeedbackResponsesFromStudentOrTeamForQuestion(question, student);			
			Map<String, String> recipients =
					fqLogic.getRecipientsForQuestion(question, userEmail);
			normalizeMaximumResponseEntities(question, recipients);			
			
			bundle.put(question, responses);
			recipientList.put(question.getId(), recipients);
		}
		
		return new FeedbackSessionQuestionsBundle(fsa, bundle, recipientList);
	}
	
	/**
	 * Gets results of a feedback session to show to an instructor. 
	 */
	public FeedbackSessionResultsBundle getFeedbackSessionResultsForInstructor(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException {
		return getFeedbackSessionResultsForUser(feedbackSessionName, courseId, userEmail, UserType.Role.INSTRUCTOR);
	}
	
	/**
	 * Gets results of a feedback session to show to a student. 
	 */
	public FeedbackSessionResultsBundle getFeedbackSessionResultsForStudent(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException {
		return getFeedbackSessionResultsForUser(feedbackSessionName, courseId, userEmail, UserType.Role.STUDENT);
	}

	
	public String getFeedbackSessionResultsSummaryAsCsv(
			String feedbackSessionName, String courseId, String userEmail) 
					throws UnauthorizedAccessException, EntityDoesNotExistException {
		
		FeedbackSessionResultsBundle results =
				getFeedbackSessionResultsForInstructor(feedbackSessionName, courseId, userEmail);
		
		// sort responses by giver > recipient > qnNumber
		Collections.sort(results.responses, results.compareByGiverName);
		
		String export = "";
		
		export += "Course" + "," + Sanitizer.sanitizeForCsv(results.feedbackSession.courseId) + Const.EOL
				+ "Session Name" + "," + Sanitizer.sanitizeForCsv(results.feedbackSession.feedbackSessionName) 
				+ Const.EOL + Const.EOL + Const.EOL;
		
		for (Map.Entry<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> entry :
								results.getQuestionResponseMap().entrySet()) {
			export += "Question " + Integer.toString(entry.getKey().questionNumber) + "," +
					 Sanitizer.sanitizeForCsv(entry.getKey().getQuestionDetails().questionText) 
					+ Const.EOL + Const.EOL;
			export += "Giver" + "," + "Recipient" + "," + "Feedback" + Const.EOL;
			
			for(FeedbackResponseAttributes response : entry.getValue()){
				export += Sanitizer.sanitizeForCsv(results.getNameForEmail(response.giverEmail)) + "," + 
						Sanitizer.sanitizeForCsv(results.getNameForEmail(response.recipientEmail)) + "," +
						Sanitizer.sanitizeForCsv(response.getResponseDetails().getAnswerString()) + Const.EOL;
			}
			export += Const.EOL + Const.EOL;
		}
		return export;
	}
	
	public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedPublishedEmailsToBeSent() {
		List<FeedbackSessionAttributes> sessions =
				fsDb.getFeedbackSessionsWithUnsentPublishedEmail();
		List<FeedbackSessionAttributes> sessionsToSendEmailsFor =
				new ArrayList<FeedbackSessionAttributes>();
		
		for (FeedbackSessionAttributes session : sessions){
			// Don't send email if publish time is same as open time or not automated.
			if(session.isPublished() && !TimeHelper.isSpecialTime(session.resultsVisibleFromTime)) {
				sessionsToSendEmailsFor.add(session);
			}
		}		
		return sessionsToSendEmailsFor;
	}
	
	public List<FeedbackSessionAttributes> getFeedbackSessionsWhichNeedOpenEmailsToBeSent(){
		List<FeedbackSessionAttributes> sessions =
				fsDb.getFeedbackSessionsWithUnsentOpenEmail();
		List<FeedbackSessionAttributes> sessionsToSendEmailsFor =
				new ArrayList<FeedbackSessionAttributes>();
		
		for (FeedbackSessionAttributes session : sessions){
			if(session.isOpened()) {
				sessionsToSendEmailsFor.add(session);
			}
		}		
		return sessionsToSendEmailsFor;		
	}

	public boolean isCreatorOfSession(String feedbackSessionName, String courseId, String userEmail) {
		FeedbackSessionAttributes fs = getFeedbackSession(feedbackSessionName, courseId);
		return (fs.creatorEmail.equals(userEmail));
	}
	
	public boolean isFeedbackSessionExists(String feedbackSessionName, String courseId) {
		return fsDb.getFeedbackSession(courseId, feedbackSessionName) != null;
	}
	
	public boolean isFeedbackSessionCompletedByStudent(String feedbackSessionName,
			String courseId, String userEmail)
			throws EntityDoesNotExistException {

		if (isFeedbackSessionExists(feedbackSessionName, courseId) == false) {
			throw new EntityDoesNotExistException(
					"Trying to check a feedback session that does not exist.");
		}

		List<FeedbackQuestionAttributes> allQuestions =
				fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
		
		for (FeedbackQuestionAttributes question : allQuestions) {
			if(fqLogic.isQuestionAnsweredByUser(question, userEmail)){
				// If any question is answered, session is complete.
				return true;
			}
		}
		return false;
	}

	// This method is for manual adding of additional responses to a FS.
	public void addResponsesToFeedbackSession(List<FeedbackResponse> responses,
			String feedbackSessionName, String courseId)
			throws NotImplementedException {
		throw new NotImplementedException(
				"Can't do manual adding of responses yet");
	}
		
	public void updateFeedbackSession(FeedbackSessionAttributes newSession)
			throws InvalidParametersException, EntityDoesNotExistException {

		FeedbackSessionAttributes oldSession =
				fsDb.getFeedbackSession(newSession.courseId,
						newSession.feedbackSessionName);

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
		if (newSession.feedbackSessionType == null) {
			newSession.feedbackSessionType = oldSession.feedbackSessionType;
		}
		if (newSession.sessionVisibleFromTime == null) {
			newSession.sessionVisibleFromTime = oldSession.sessionVisibleFromTime; 
		}
		if (newSession.resultsVisibleFromTime == null) {
			newSession.resultsVisibleFromTime = oldSession.resultsVisibleFromTime; 
		}
		
		makeEmailStateConsistent(oldSession, newSession);
		
		fsDb.updateFeedbackSession(newSession);
	}

	/**
	 * This method is called when the user publishes a feedback session manually.
	 * Preconditions:
	 * *	The feedback session has to be set as manually/automatically published.
	 *      The feedback session can't be private
	 */
	public void publishFeedbackSession(String feedbackSessionName, String courseId)
			throws EntityDoesNotExistException, InvalidParametersException {

		FeedbackSessionAttributes sessionToPublish =
				getFeedbackSession(feedbackSessionName, courseId);

		if(sessionToPublish == null) {
			throw new EntityDoesNotExistException("Trying to publish a non-existant session.");
		}
		
		if(sessionToPublish.isPrivateSession() == true) {
			throw new InvalidParametersException(
					"Private session can't be published.");
		}

		if (sessionToPublish.isPublished()) {
			throw new InvalidParametersException(
					"Session is already published.");
		}
		
		sessionToPublish.resultsVisibleFromTime = Const.TIME_REPRESENTS_NOW;

		updateFeedbackSession(sessionToPublish);

		sendFeedbackSessionPublishedEmail(sessionToPublish);
	}

	/**
	 * This method is called when the user unpublishes a feedback session manually.
	 * Preconditions:
	 * *	The feedback session has to be set as manually published.
	 */
	public void unpublishFeedbackSession(String feedbackSessionName, String courseId)
			throws EntityDoesNotExistException, InvalidParametersException {

		FeedbackSessionAttributes sessionToUnpublish =
				getFeedbackSession(feedbackSessionName, courseId);

		if(sessionToUnpublish == null) {
			throw new EntityDoesNotExistException("Trying to publish a non-existant session.");
		}
		
		if(sessionToUnpublish.isPrivateSession() == true) {
			throw new InvalidParametersException(
					"Private session can't be published.");
		}
		
		if (sessionToUnpublish.isPublished() == false) {
			throw new InvalidParametersException(
					"Session is not currently published.");
		}

		sessionToUnpublish.resultsVisibleFromTime = Const.TIME_REPRESENTS_LATER;

		updateFeedbackSession(sessionToUnpublish);
	}
	
	public void scheduleFeedbackSessionOpeningEmails() {
		List<FeedbackSessionAttributes> sessions = getFeedbackSessionsWhichNeedOpenEmailsToBeSent();
		
		for(FeedbackSessionAttributes session : sessions){			
			Emails emails = new Emails();
			emails.addFeedbackSessionReminderToEmailsQueue(session, Emails.EmailType.FEEDBACK_OPENING);
		}
	}
	
	public List<FeedbackSessionAttributes> getFeedbackSessionsClosingWithinTimeLimit() {
		ArrayList<FeedbackSessionAttributes> requiredSessions = new 
				ArrayList<FeedbackSessionAttributes>();
		
		List<FeedbackSessionAttributes> nonPrivateSessions = fsDb.getNonPrivateFeedbackSessions();
		
		for(FeedbackSessionAttributes session : nonPrivateSessions) {
			if (session.isClosingWithinTimeLimit(
					SystemParams.NUMBER_OF_HOURS_BEFORE_CLOSING_ALERT) == false) {
				continue;
			}
			requiredSessions.add(session);
		}
		
		return requiredSessions;
	}
	
	public void scheduleFeedbackSessionClosingEmails() {
		
		List<FeedbackSessionAttributes> sessions = getFeedbackSessionsClosingWithinTimeLimit();
		
		for (FeedbackSessionAttributes session : sessions) {
			Emails emails = new Emails();
			emails.addFeedbackSessionReminderToEmailsQueue(session, Emails.EmailType.FEEDBACK_CLOSING);
		}
	}

	public void scheduleFeedbackSessionPublishedEmails() {		
		List<FeedbackSessionAttributes> sessions = getFeedbackSessionsWhichNeedPublishedEmailsToBeSent();
		
		for(FeedbackSessionAttributes session : sessions){
			sendFeedbackSessionPublishedEmail(session);
		}
	}

	
	public void deleteFeedbackSessionsForCourse(String courseId) {
		List<FeedbackSessionAttributes> sessionsToDelete =
				fsDb.getFeedbackSessionsForCourse(courseId);
		
		for (FeedbackSessionAttributes session : sessionsToDelete){
			deleteFeedbackSessionCascade(
					session.feedbackSessionName,
					session.courseId);
		}
	}

	/**
	 * This method deletes a specific feedback session, and all it's question and responses
	 */
	public void deleteFeedbackSessionCascade(String feedbackSessionName,
			String courseId) {

		try {
			fqLogic.deleteFeedbackQuestionsForSession(feedbackSessionName, courseId);
		} catch (EntityDoesNotExistException e) {
			// Silently fail if session does not exist
		}
		
		FeedbackSessionAttributes sessionToDelete = new FeedbackSessionAttributes();
		sessionToDelete.feedbackSessionName = feedbackSessionName;
		sessionToDelete.courseId = courseId;
		
		fsDb.deleteEntity(sessionToDelete);

	}
	
	public FeedbackSessionDetailsBundle getFeedbackSessionDetails(
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
			List<FeedbackResponseAttributes> responses = frLogic
					.getFeedbackResponsesForSession(fsa.feedbackSessionName, fsa.courseId);
			List<FeedbackQuestionAttributes> questions = fqLogic
					.getFeedbackQuestionsForSession(fsa.feedbackSessionName, fsa.courseId);
			List<FeedbackQuestionAttributes> studentQns = fqLogic
					.getFeedbackQuestionsForStudents(questions);
			
			
			for (StudentAttributes student : students) {
				if (!studentQns.isEmpty()) {
					details.stats.expectedTotal += 1;
				}
				for (FeedbackQuestionAttributes question : studentQns) {
					if(fqLogic.isQuestionAnsweredByUser(question, student.email, responses)) {
						details.stats.submittedTotal += 1;
						break;
					}
				}
			}
			for (InstructorAttributes instructor : instructors) {
				List<FeedbackQuestionAttributes> instructorQns = fqLogic
						.getFeedbackQuestionsForInstructor(questions, fsa.isCreator(instructor.email));
				if (!instructorQns.isEmpty()) {
					details.stats.expectedTotal += 1;
				}
				for (FeedbackQuestionAttributes question : instructorQns) {
					if(fqLogic.isQuestionAnsweredByUser(question, instructor.email, responses)) {
						details.stats.submittedTotal += 1;
						break;
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
				if (isFeedbackSessionFullyCompletedByTeam(
						fsa.feedbackSessionName,
						fsa.courseId, team.name)) {
					teamsSubmitted += 1;
				}
			}
			details.stats.submittedTotal = teamsSubmitted;
			break;

		case PRIVATE:
			if (fqLogic.getFeedbackQuestionsForSession(
					fsa.feedbackSessionName, fsa.courseId).isEmpty()) {
				break;
			}
			details.stats.expectedTotal = 1;
			if (this.isFeedbackSessionFullyCompletedByInstructor(
					fsa.feedbackSessionName, fsa.courseId, fsa.creatorEmail)) {
				details.stats.submittedTotal = 1;
			}
			break;

		default:
			break;
		}

		return details;
	}
	
	private FeedbackSessionResultsBundle getFeedbackSessionResultsForUser(
			String feedbackSessionName, String courseId, String userEmail, UserType.Role role)
			throws EntityDoesNotExistException {
	
		FeedbackSessionAttributes session = fsDb.getFeedbackSession(
				courseId, feedbackSessionName);
		
		if (session == null) {
			throw new EntityDoesNotExistException(
					"Trying to view non-existent feedback session.");
		}
		
		List<FeedbackQuestionAttributes> allQuestions = 
				fqLogic.getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		
		//Load details of students and instructors once and pass it to callee methods
		//  (rather than loading them many times).
		CourseRoster roster = new CourseRoster(
				new StudentsDb().getStudentsForCourse(courseId),
				new InstructorsDb().getInstructorsForCourse(courseId));
		
		//create empty data containers to store results
		List<FeedbackResponseAttributes> responses =
				new ArrayList<FeedbackResponseAttributes>();
		Map<String, FeedbackQuestionAttributes> relevantQuestions =
				new HashMap<String, FeedbackQuestionAttributes>();
		Map<String, String> emailNameTable =
				new HashMap<String, String>();
		Map<String, boolean[]> visibilityTable =
				new HashMap<String, boolean[]>();
		
		boolean isPrivateSessionNotCreatedByThisUser = session.isPrivateSession() && !session.isCreator(userEmail);
		if (isPrivateSessionNotCreatedByThisUser) {
			//return empty result set
			return new FeedbackSessionResultsBundle(
					session, responses, relevantQuestions,
					emailNameTable, visibilityTable);
		}
				
		for (FeedbackQuestionAttributes question : allQuestions) {
			
			List<FeedbackResponseAttributes> responsesForThisQn;
	
			boolean isPrivateSessionCreatedByThisUser = session.isCreator(userEmail) && session.isPrivateSession();
			if(isPrivateSessionCreatedByThisUser) {
				responsesForThisQn = frLogic.getFeedbackResponsesForQuestion(question.getId());
			} else {
				responsesForThisQn = frLogic.getViewableFeedbackResponsesForQuestion(
						question, userEmail, role);
			}
			
			boolean thisQuestionHasResponses = (!responsesForThisQn.isEmpty());
			if (thisQuestionHasResponses) {
				relevantQuestions.put(question.getId(), question);
				responses.addAll(responsesForThisQn);
				addEmailNamePairsToTable(emailNameTable, responsesForThisQn, question, roster);
				addVisibilityToTable( visibilityTable, question, responsesForThisQn, userEmail, roster);
			}
			
		}
		
		FeedbackSessionResultsBundle results = 
			new FeedbackSessionResultsBundle(session, responses, relevantQuestions, emailNameTable, visibilityTable);
	
		return results;
	}

	private void addVisibilityToTable(Map<String, boolean[]> visibilityTable,
			FeedbackQuestionAttributes question,
			List<FeedbackResponseAttributes> responses,
			String userEmail,
			CourseRoster roster) {
		for (FeedbackResponseAttributes response  : responses) {
			boolean[] visibility = new boolean[2];
			visibility[Const.VISIBILITY_TABLE_GIVER] = frLogic.isNameVisibleTo(question, response, userEmail, true, roster);
			visibility[Const.VISIBILITY_TABLE_RECIPIENT] = frLogic.isNameVisibleTo(question, response, userEmail, false, roster);
			visibilityTable.put(response.getId(), visibility);
		}
	}

	private void addEmailNamePairsToTable(Map<String, String> emailNameTable,
			List<FeedbackResponseAttributes> responsesForThisQn,
			FeedbackQuestionAttributes question, CourseRoster roster) throws EntityDoesNotExistException {
		
		for (FeedbackResponseAttributes response : responsesForThisQn) {
			if (question.giverType == FeedbackParticipantType.TEAMS){
				if (emailNameTable.containsKey(response.giverEmail + Const.TEAM_OF_EMAIL_OWNER) == false) {
					emailNameTable.put(
							response.giverEmail + Const.TEAM_OF_EMAIL_OWNER,
							getNameForEmail(question.giverType, response.giverEmail, roster));
				}
			} else if(emailNameTable.containsKey(response.giverEmail) == false) {
				emailNameTable.put(
						response.giverEmail,
						getNameForEmail(question.giverType, response.giverEmail, roster));
			}
			
			if(emailNameTable.containsKey(response.recipientEmail) == false) {
				emailNameTable.put(
						response.recipientEmail,
						getNameForEmail(question.recipientType, response.recipientEmail, roster));
			}
		}
		
	}
		
	private List<FeedbackSessionDetailsBundle> getFeedbackSessionDetailsForCourse(
			String courseId, String instructorEmail)
			throws EntityDoesNotExistException {

		List<FeedbackSessionDetailsBundle> fsDetails =
				new ArrayList<FeedbackSessionDetailsBundle>();
		List<FeedbackSessionAttributes> fsInCourse =
				fsDb.getFeedbackSessionsForCourse(courseId);

		for (FeedbackSessionAttributes fsa : fsInCourse) {
			if((fsa.isPrivateSession() && !fsa.isCreator(instructorEmail)) == false)
				fsDetails.add(getFeedbackSessionDetails(fsa));
		}

		return fsDetails;
	}
	
	private List<FeedbackSessionAttributes> getFeedbackSessionsListForCourse(
			String courseId, String instructorEmail)
			throws EntityDoesNotExistException {
		
		List<FeedbackSessionAttributes> fsInCourse =
				fsDb.getFeedbackSessionsForCourse(courseId);

		return fsInCourse;
	}

	
	// TODO Note: This method is for use in Issue 1061. Can be further refactored.
	private FeedbackSessionResponseStatus getFeedbackSessionResponseStatus(
			FeedbackSessionAttributes fsa)
			throws EntityDoesNotExistException {

		List<StudentAttributes> students = studentsLogic
				.getStudentsForCourse(fsa.courseId);
		List<InstructorAttributes> instructors = instructorsLogic
				.getInstructorsForCourse(fsa.courseId);
		List<FeedbackResponseAttributes> responses = frLogic
				.getFeedbackResponsesForSession(fsa.feedbackSessionName,
						fsa.courseId);
		List<FeedbackQuestionAttributes> questions = fqLogic
				.getFeedbackQuestionsForSession(fsa.feedbackSessionName,
						fsa.courseId);

		FeedbackSessionResponseStatus responseStatus = new FeedbackSessionResponseStatus();

		for (FeedbackQuestionAttributes question : questions) {
			boolean responded = false;
			if (question.giverType == FeedbackParticipantType.STUDENTS ||
					question.giverType == FeedbackParticipantType.TEAMS) {
				for (StudentAttributes student : students) {
					if(fqLogic.isQuestionAnsweredByUser(question, student.email, responses)) {
						responded = true;
						break;
					}
					responseStatus.add(question.getId(), student.name, responded);
				}
			} else if (question.giverType == FeedbackParticipantType.INSTRUCTORS) {
				for (InstructorAttributes instructor : instructors) {
					if(fqLogic.isQuestionAnsweredByUser(question, instructor.email, responses)) {
						responded = true;
						break;
					}
					responseStatus.add(question.getId(), instructor.name, responded);
				}
			}
		}

		return responseStatus;
	}
	
	private String getNameForEmail(FeedbackParticipantType type, String email, CourseRoster roster)
			throws EntityDoesNotExistException {
		
		String name = null;
		String team = null;
		
		StudentAttributes student = roster.getStudentForEmail(email);
		if(student != null) {
			name = student.name;
			team = student.team;
		} else {
			InstructorAttributes instructor = roster.getInstructorForEmail(email);
			if (instructor == null) {
				if(email.equals(Const.GENERAL_QUESTION)) {
					// Email represents that there is no specific recipient. 
					name = Const.USER_IS_NOBODY;
					team = email;
				} else {
					// Assume that the email is actually a team name.
					name = Const.USER_IS_TEAM;
					team = email;
				}
			} else {
				name = instructor.name;
				team = "Instructors";
			}
		}
		
		if (type == FeedbackParticipantType.TEAMS || type == FeedbackParticipantType.OWN_TEAM){
			return team;
		} else {
			return name;
		}
	}
	
	public boolean isFeedbackSessionFullyCompletedByStudent(String feedbackSessionName,
			String courseId, String userEmail)
			throws EntityDoesNotExistException {

		if (isFeedbackSessionExists(feedbackSessionName, courseId) == false) {
			throw new EntityDoesNotExistException(
					"Trying to check a feedback session that does not exist.");
		}

		List<FeedbackQuestionAttributes> allQuestions =
				fqLogic.getFeedbackQuestionsForStudents(feedbackSessionName, courseId);
		
		for (FeedbackQuestionAttributes question : allQuestions) {
			if(!fqLogic.isQuestionFullyAnsweredByUser(question, userEmail)){
				// If any question is not completely answered, session is not completed
				return false;
			}
		}
		return true;
	}
	
	private boolean isFeedbackSessionFullyCompletedByInstructor(String feedbackSessionName,
			String courseId, String userEmail)
			throws EntityDoesNotExistException {

		if (isFeedbackSessionExists(feedbackSessionName, courseId) == false) {
			throw new EntityDoesNotExistException(
					"Trying to check a feedback session that does not exist.");
		}

		List<FeedbackQuestionAttributes> allQuestions =
				fqLogic.getFeedbackQuestionsForInstructor(feedbackSessionName, courseId,
						userEmail);
		
		for (FeedbackQuestionAttributes question : allQuestions) {
			if(!fqLogic.isQuestionFullyAnsweredByUser(question, userEmail)){
				// If any question is not completely answered, session is not completed
				return false;
			}
		}
		return true;
	}
	
	private boolean isFeedbackSessionFullyCompletedByTeam(String feedbackSessionName,
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
	
	/**
	 * This method returns a list of feedback sessions which are relevant for a user.
	 */
	private boolean isFeedbackSessionViewableTo(FeedbackSessionAttributes session,
			String userEmail) throws EntityDoesNotExistException {
				
		if (fsDb.getFeedbackSession(
				session.courseId,
				session.feedbackSessionName) == null) {
			throw new EntityDoesNotExistException(
					"Trying to get a feedback session that does not exist.");
		}
		
		// Check for private type first.
		if (session.feedbackSessionType == FeedbackSessionType.PRIVATE) {
			return session.creatorEmail.equals(userEmail);
		}
		
		// Allow all instructors to view always
		InstructorAttributes instructor = instructorsLogic.
				getInstructorForEmail(session.courseId, userEmail);
		if (instructor != null) {
			return instructorsLogic.isInstructorOfCourse(instructor.googleId, session.courseId);
		}
	
		// Allow viewing if session is viewable to students
		return isFeedbackSessionViewableToStudents(session);
	}
	
	public boolean isFeedbackSessionViewableToStudents(FeedbackSessionAttributes session) 
			throws EntityDoesNotExistException {
		// Allow students to view if there are questions for them
		List<FeedbackQuestionAttributes> questions = 
				fqLogic.getFeedbackQuestionsForStudents(
					session.feedbackSessionName, session.courseId);
					
		return (session.isVisible() && !questions.isEmpty()) ? true : false;
	}
	
	private void normalizeMaximumResponseEntities(FeedbackQuestionAttributes question,
			Map<String, String> recipients) {

		// change constant to actual maximum size.
		if (question.numberOfEntitiesToGiveFeedbackTo == Const.MAX_POSSIBLE_RECIPIENTS) {
			question.numberOfEntitiesToGiveFeedbackTo = recipients.size();
		}
	}
	
	private void makeEmailStateConsistent(FeedbackSessionAttributes oldSession,
			FeedbackSessionAttributes newSession) {
		
		// reset sentOpenEmail if the session has opened but is being closed now.
		if (oldSession.sentOpenEmail && !newSession.isOpened()) {
			newSession.sentOpenEmail = false;
		} else if(oldSession.sentOpenEmail){ 
			// or else leave it as sent if so.
			newSession.sentOpenEmail = true;
		}
		
		// reset sentPublishedEmail if the session has been published but is
		// going to be unpublished now.
		if (oldSession.sentPublishedEmail && !newSession.isPublished()) {
			newSession.sentPublishedEmail = false;
		} else if(oldSession.sentPublishedEmail) {
			// or else leave it as sent if so.
			newSession.sentPublishedEmail = true;
		}
	}
	
	private void sendFeedbackSessionPublishedEmail(FeedbackSessionAttributes session) {
		Emails emails = new Emails();
		emails.addFeedbackSessionReminderToEmailsQueue(session, Emails.EmailType.FEEDBACK_PUBLISHED);
	}
}