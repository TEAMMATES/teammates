package teammates.logic;

import static teammates.common.FeedbackParticipantType.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import teammates.common.Assumption;
import teammates.common.Common;
import teammates.common.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.TeamDetailsBundle;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackQuestionsDb;

public class FeedbackQuestionsLogic {
	
	@SuppressWarnings("unused")
	private static final Logger log = Common.getLogger();

	private static FeedbackQuestionsLogic instance = null;
	
	private static final FeedbackQuestionsDb fqDb = new FeedbackQuestionsDb();
	private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
	private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
	private static final CoursesLogic coursesLogic = CoursesLogic.inst();
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
	
	/**
	 * Updates the feedback session identified by {@code newAttributes.getId()}.
	 * For the remaining parameters, the existing value is preserved 
	 *   if the parameter is null (due to 'keep existing' policy).<br> 
	 * Preconditions: <br>
	 * * {@code newAttributes} is non-null and it's ID corresponds to an 
	 * existing feedback question. <br>
	 */
	public void updateFeedbackQuestion(FeedbackQuestionAttributes newAttributes)
			throws InvalidParametersException, EntityDoesNotExistException {
		
		FeedbackQuestionAttributes oldQuestion = 
				fqDb.getFeedbackQuestion(newAttributes.getId());
		
		if (oldQuestion == null) {
			throw new EntityDoesNotExistException(
					"Trying to update a feedback question that does not exist.");
		}
		
		// These can't be changed anyway. Copy values to defensively avoid invalid parameters.
		newAttributes.feedbackSessionName = oldQuestion.feedbackSessionName;
		newAttributes.courseId = oldQuestion.courseId;
		newAttributes.creatorEmail = oldQuestion.creatorEmail;
		
		if(newAttributes.questionText == null) {
			newAttributes.questionText = oldQuestion.questionText;
		}
		if(newAttributes.questionType == null){
			newAttributes.questionType = oldQuestion.questionType;
		}
		if(newAttributes.giverType == null){
			newAttributes.giverType = oldQuestion.giverType;
		}
		if(newAttributes.recipientType == null){
			newAttributes.recipientType = oldQuestion.recipientType;
		}
		if(newAttributes.showResponsesTo == null){
			newAttributes.showResponsesTo = oldQuestion.showResponsesTo;
		}
		if(newAttributes.showGiverNameTo == null){
			newAttributes.showGiverNameTo = oldQuestion.showGiverNameTo;
		}
		if(newAttributes.showRecipientNameTo == null){
			newAttributes.showRecipientNameTo = oldQuestion.showRecipientNameTo;
		}
		
		fqDb.updateFeedbackQuestion(newAttributes);
	}
	
	// Cascades question number
	public void deleteFeedbackQuestionCascade(
			String feedbackSessionName, String courseId, int questionNumber) {
		
		FeedbackQuestionAttributes questionToDelete =
				getFeedbackQuestion(feedbackSessionName, courseId, questionNumber);
		
		if (questionToDelete == null) {
			return; // Silently fail if question does not exist.
		}
		List<FeedbackQuestionAttributes> questionsToCascade = null;
		try {
			questionsToCascade = getFeedbackQuestionsForSession(feedbackSessionName, courseId);
		} catch (EntityDoesNotExistException e) {
			Assumption.fail("Session disappeared.");
		}
		
		fqDb.deleteEntity(questionToDelete);
		
		if(questionToDelete.questionNumber < questionsToCascade.size()) {
			shiftQuestionNumbersDown(questionToDelete.questionNumber, questionsToCascade);
		}
	}

	// Shifts all question numbers after questionNumberToShiftFrom down by one.
	private void shiftQuestionNumbersDown(int questionNumberToShiftFrom,
			List<FeedbackQuestionAttributes> questionsToShift) {
		for (FeedbackQuestionAttributes question : questionsToShift) {				
			if(question.questionNumber > questionNumberToShiftFrom){
				question.questionNumber -= 1;
				try {
					updateFeedbackQuestion(question);
				} catch (InvalidParametersException e) {
					Assumption.fail("Invalid question.");
				} catch (EntityDoesNotExistException e) {
					Assumption.fail("Question disappeared.");
				}
			}
		}
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
			String feedbackSessionName,	String courseId, String studentEmail)
					throws EntityDoesNotExistException {

		List<FeedbackQuestionAttributes> teamQuestions =
				fqDb.getFeedbackQuestionsForGiverType(
						feedbackSessionName, courseId, TEAMS);
		List<FeedbackQuestionAttributes> unstolenQuestions =
				new ArrayList<FeedbackQuestionAttributes>();
		
		StudentAttributes student =
				studentsLogic.getStudentForEmail(courseId, studentEmail);
		
		Assumption.assertNotNull("Student disappeared!", student);
				
		for (FeedbackQuestionAttributes question : teamQuestions) {
			if (isQuestionAnsweredByTeam(question, student.team) == true) {
				if (frLogic.getFeedbackResponsesFromGiver(
						question.getId(), studentEmail).isEmpty() == false) {
					// question has at least one response by this student
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
			String feedbackSessionName, String courseId, String teamName)
					throws EntityDoesNotExistException {
		
		List<FeedbackQuestionAttributes> questions =
				fqDb.getFeedbackQuestionsForGiverType(
				feedbackSessionName, courseId, TEAMS);
		
		List<FeedbackQuestionAttributes> unansweredQuestions =
				new ArrayList<FeedbackQuestionAttributes>();
		
		for (FeedbackQuestionAttributes question : questions) {
			if (isQuestionAnsweredByTeam(
					question, teamName) == false)
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
		
		return (question.showResponsesTo.contains(userType) || 
				// general feedback; everyone can see results. TODO: hide visibility options in UI.
				question.recipientType == FeedbackParticipantType.NONE);
	}
	
	public boolean isQuestionAnsweredByUser(FeedbackQuestionAttributes question, String email) 
			throws EntityDoesNotExistException {
		
		int numberOfResponsesGiven = 
				frLogic.getFeedbackResponsesFromGiver(question.getId(), email).size();
		/*
		int numberOfResponsesNeeded =
				question.numberOfEntitiesToGiveFeedbackTo;
		
		if (numberOfResponsesNeeded == Common.MAX_POSSIBLE_RECIPIENTS) {
			numberOfResponsesNeeded = getRecipientsForQuestion(question, email).size();
		}
		
		return numberOfResponsesGiven >= numberOfResponsesNeeded ? true : false;
		*/
		// As long as a user has responded, we count the question as answered.
		return numberOfResponsesGiven > 0 ? true : false;
	}

	public boolean isQuestionAnsweredByTeam(FeedbackQuestionAttributes question, 
			String teamName) throws EntityDoesNotExistException {

		List<StudentAttributes> studentsInTeam =
				studentsLogic.getStudentsForTeam(question.courseId, teamName);
		
		int numberOfResponsesNeeded =
				question.numberOfEntitiesToGiveFeedbackTo;
		
		if (numberOfResponsesNeeded == Common.MAX_POSSIBLE_RECIPIENTS) {
			numberOfResponsesNeeded = getRecipientsForQuestion(question, teamName).size();
		}
				
		for (StudentAttributes student : studentsInTeam) {
			List<FeedbackResponseAttributes> responses = 
					frLogic.getFeedbackResponsesFromGiver(question.getId(), student.email);
			for (FeedbackResponseAttributes response : responses) {
				if (response.giverEmail.equals(student.email)) {
					numberOfResponsesNeeded -= 1;
				}
			}
		}
		return numberOfResponsesNeeded <= 0 ? true : false;
	}
	
	public Map<String,String> getRecipientsForQuestion(
			FeedbackQuestionAttributes question, String giverEmail)
					throws EntityDoesNotExistException {

		Map<String,String> recipients = new HashMap<String,String>();
		
		FeedbackParticipantType recipientType = question.recipientType;
		
		String giverName = null;
		String giverTeam = null;
		
		InstructorAttributes instructor =
				instructorsLogic.getInstructorForEmail(question.courseId, giverEmail);
		if(instructor == null) {
			StudentAttributes student = 
					studentsLogic.getStudentForEmail(question.courseId, giverEmail);
			if (student == null) {
				throw new EntityDoesNotExistException("No entity with corresponding "+giverEmail+" can respond to question: "+question.getIdentificationString());
			} else {
				giverName = student.name;
				giverTeam = student.team;
			}
		} else {
			giverName = instructor.name;
			giverTeam = "Instructors";
		}
		
		switch (recipientType) {
		case SELF:
			recipients.put(giverEmail, giverName);
			break;
		case STUDENTS:
			List<StudentAttributes> studentsInCourse =
				studentsLogic.getStudentsForCourse(question.courseId);
			for(StudentAttributes student : studentsInCourse) {
				// Ensure student does not evaluate himself
				if(giverEmail.equals(student.email) == false) {
					recipients.put(student.email, student.name);
				}
			}
			break;
		case INSTRUCTORS:
			List<InstructorAttributes> instructorsInCourse =
				instructorsLogic.getInstructorsForCourse(question.courseId);
			for(InstructorAttributes instr : instructorsInCourse) {
				// Ensure instructor does not evaluate himself
				if(giverEmail.equals(instr.email) == false) {
					recipients.put(instr.email, instr.name);
				}
			}
			break;
		case TEAMS:
			List<TeamDetailsBundle> teams =
				coursesLogic.getTeamsForCourse(question.courseId).teams;
			for(TeamDetailsBundle team : teams) {
				// Ensure student('s team) does not evaluate own team.
				if (giverTeam.equals(team.name) == false) {
					// recipientEmail doubles as team name in this case.
					recipients.put(team.name, team.name);
				}
			}
			break;
		case OWN_TEAM:
			recipients.put(giverTeam, giverTeam);
			break;
		case OWN_TEAM_MEMBERS:
			List<StudentAttributes> students = 
				studentsLogic.getStudentsForTeam(giverTeam, question.courseId);
			for (StudentAttributes student : students) {
				if(student.email.equals(giverEmail) == false) {
					recipients.put(student.email, student.name);
				}
			}
		case NONE:
			break;
		default:
			break;
		}
		return recipients;
	}
}
