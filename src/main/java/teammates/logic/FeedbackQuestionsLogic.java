package teammates.logic;

import static teammates.common.FeedbackParticipantType.*;

import java.util.ArrayList;
import java.util.List;

import teammates.common.Assumption;
import teammates.common.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackQuestionsDb;

public class FeedbackQuestionsLogic {
	
	private static FeedbackQuestionsLogic instance = null;
	
	private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
	private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
	private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
	private static final StudentsLogic studentsLogic = StudentsLogic.inst();
	private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
	
	public static FeedbackQuestionsLogic inst() {
		if (instance == null)
			instance = new FeedbackQuestionsLogic();
		return instance;
	}
	
	public void createFeedbackQuestion(FeedbackQuestionAttributes fqa)
			throws InvalidParametersException, EntityAlreadyExistsException {
		fqDb.createEntity(fqa);
	}
	
	public void deleteFeedbackQuestion(
			String feedbackSessionName, String courseId, int questionNumber) {
		
		FeedbackQuestionAttributes questionToDelete =
				getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
		
		if (questionToDelete == null) {
			return; // Silently fail if question does not exist.
		}
		
		fqDb.deleteEntity(questionToDelete);
	}
	
	// gets a single qn using questionId
	// NOTE: can only be used if the question has already been created in datastore.
	public FeedbackQuestionAttributes getFeedbackQuestion(String feedbackQuestionId) {	
		return fqDb.getFeedbackQuestion(feedbackQuestionId);
	}
	
	// gets a single qn using feedbackSession and questionNumber
	public FeedbackQuestionAttributes getFeedbackQuestion(
			String feedbackSessionName,
			String courseId,
			int questionNumber) {	
		return fqDb.getFeedbackQuestion(feedbackSessionName,
				courseId, questionNumber);
	}
	
	
	// gets qns available for user to do
	public List<FeedbackQuestionAttributes> getFeedbackQuestionsForUser(
			String feedbackSessionName, String courseId, String userEmail)
			throws EntityDoesNotExistException {

		if (fsLogic.getFeedbackSession(feedbackSessionName, courseId) == null) {
			throw new EntityDoesNotExistException(
					"Trying to get questions for a feedback session that does not exist.");
		}

		List<FeedbackQuestionAttributes> questions =
				new ArrayList<FeedbackQuestionAttributes>();
		
		// Return all self-only questions if creator.
		if (fsLogic.isCreatorOfSession(feedbackSessionName, courseId, userEmail)) {
			questions.addAll(fqDb.getFeedbackQuestionsForGiverType(feedbackSessionName,
					courseId, SELF));
		}

		// Return student questions and team questions 
		// that aren't answered by others if student.
		if (studentsLogic.isStudentInCourse(courseId, userEmail)) {
			questions.addAll(
					fqDb.getFeedbackQuestionsForGiverType(
							feedbackSessionName, courseId, STUDENTS));
			questions.addAll(
					getUnstolenTeamQuestions(
							feedbackSessionName, courseId, userEmail));
		}

		// Return instructor questions if instructor.
		InstructorAttributes instructor = 
				instructorsLogic.getInstructorForEmail(courseId, userEmail);
		
		if (instructor != null) {
			if (instructorsLogic.isInstructorOfCourse(instructor.googleId, courseId)) {
				questions.addAll(fqDb.getFeedbackQuestionsForGiverType(
						feedbackSessionName, courseId, INSTRUCTORS));
			}
		}
		
		return questions;
	}

	private List<FeedbackQuestionAttributes> getUnstolenTeamQuestions(
			String feedbackSessionName,	String courseId, String studentEmail) {

		List<FeedbackQuestionAttributes> teamQuestions =
				fqDb.getFeedbackQuestionsForGiverType(
						feedbackSessionName, courseId, TEAMS);
		List<FeedbackQuestionAttributes> unstolenQuestions =
				new ArrayList<FeedbackQuestionAttributes>();
		
		StudentAttributes student =
				studentsLogic.getStudentForEmail(courseId, studentEmail);
		
		Assumption.assertNotNull("Student disappeared!", student);
				
		for (FeedbackQuestionAttributes question : teamQuestions) {
			if (isQuestionAnsweredByTeam(
					question.getId(),
					courseId, student.team) == true) {
				if (isQuestionAnsweredByUser(question.getId(), student.email)) {
					// question is answered by this student
					unstolenQuestions.add(question);
				}
			} else {
				// question has not been answered
				unstolenQuestions.add(question);
			}
		}

		return unstolenQuestions;
	}

	// gets undone qns available for a team to do
	public List<FeedbackQuestionAttributes> getFeedbackQuestionsForTeam(
			String feedbackSessionName, String courseId, String teamName) {
		
		List<FeedbackQuestionAttributes> questions =
				fqDb.getFeedbackQuestionsForGiverType(
				feedbackSessionName, courseId, TEAMS);
		
		List<FeedbackQuestionAttributes> unansweredQuestions =
				new ArrayList<FeedbackQuestionAttributes>();
		
		for (FeedbackQuestionAttributes question : questions) {
			if (isQuestionAnsweredByTeam(
					question.getId(),
					courseId, teamName) == false)
				unansweredQuestions.add(question);
		}
		
		return unansweredQuestions;
	}
	
	// gets all qns for a FS (for editing / instructor results viewing etc.)
	public List<FeedbackQuestionAttributes> getFeedbackQuestionsForSession(
			String feedbackSessionName, String courseId) throws EntityDoesNotExistException {
		
		if (fsLogic.getFeedbackSession(feedbackSessionName, courseId) == null) {
			throw new EntityDoesNotExistException(
					"Trying to get questions for a feedback session that does not exist.");
		}
		
		return fqDb.getFeedbackQuestionsForSession(
				feedbackSessionName, courseId);
	}
	
	public boolean isQuestionAnswersVisibleTo (
			FeedbackQuestionAttributes question,
			FeedbackParticipantType userType) {
		
		return (question.showGiverNameTo.contains(userType) 
				|| question.showRecipientNameTo.contains(userType));
	}
	
	public boolean isQuestionAnsweredByUser(String feedbackQuestionId, String email) {
		
		int numberOfResponsesGiven = 
				frLogic.getFeedbackResponsesFromGiver(feedbackQuestionId, email).size();
		int numberOfResponsesNeeded =
				getFeedbackQuestion(feedbackQuestionId).numberOfEntitiesToGiveFeedbackTo;
		
		return numberOfResponsesGiven >= numberOfResponsesNeeded ? true : false;
		
	}

	public boolean isQuestionAnsweredByTeam(String feedbackQuestionId,
			String courseId, String teamName) {

		List<StudentAttributes> studentsInTeam =
				studentsLogic.getStudentsForTeam(courseId, teamName);
		
		int numberOfResponsesNeeded =
				getFeedbackQuestion(feedbackQuestionId).numberOfEntitiesToGiveFeedbackTo;
		
		for (StudentAttributes student : studentsInTeam) {
			List<FeedbackResponseAttributes> responses = 
					frLogic.getFeedbackResponsesFromGiver(feedbackQuestionId, student.email);
			for (FeedbackResponseAttributes response : responses) {
				if (response.giverEmail.equals(student.email)) {
					numberOfResponsesNeeded -= 1;
				}
			}
		}
		return numberOfResponsesNeeded <= 0 ? true : false;
	}
	
	// TODO: isQuestionAnswerableByUser
}
