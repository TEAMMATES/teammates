package teammates.logic;

import java.util.ArrayList;
import java.util.List;

import teammates.common.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackResponsesDb;

public class FeedbackResponsesLogic {
	
	private static FeedbackResponsesLogic instance = null;
	private static final StudentsLogic studentsLogic = StudentsLogic.inst();
	private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
	private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
	private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();	
	
	public static FeedbackResponsesLogic inst() {
		if (instance == null)
			instance = new FeedbackResponsesLogic();
		return instance;
	}
	
	public void createFeedbackResponse(FeedbackResponseAttributes fra)
			throws InvalidParametersException, EntityAlreadyExistsException {
		frDb.createEntity(fra);
	}
		
	public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(
			String feedbackQuestionId) {
		return frDb.getFeedbackResponsesForQuestion(feedbackQuestionId);
	}
	
	
	public List<FeedbackResponseAttributes> getFeedbackResponsesForFeedbackSession(
			String feedbackSessionName, String courseId) {
		return frDb.getFeedbackResponsesForSession(feedbackSessionName, courseId);
	}
	
	public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiver(
			String feedbackQuestionId, String userEmail) {
		return frDb.getFeedbackResponsesForReceiver(feedbackQuestionId, userEmail);
	}
	
	public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiver(
			String feedbackQuestionId, String userEmail) {
		return frDb.getFeedbackResponsesFromGiver(feedbackQuestionId, userEmail);

	}
	
	public List<FeedbackResponseAttributes> getViewableFeedbackResponsesForQuestion(
			String feedbackQuestionId, String userEmail) {

		List<FeedbackResponseAttributes> viewableResponses =
				new ArrayList<FeedbackResponseAttributes>();

		FeedbackQuestionAttributes question = 
				fqLogic.getFeedbackQuestion(feedbackQuestionId);

		// Add responses for if user is a student
		if (studentsLogic.isStudentInCourse(question.courseId, userEmail)) {

			if (fqLogic.isQuestionAnswersVisibleTo(question,
					FeedbackParticipantType.STUDENTS)) {
				addNewResponses(viewableResponses,
						getFeedbackResponsesForQuestion(feedbackQuestionId));
			}

			// Add only if user is a team member of response giver if question visibility is
			// teammates.
			// Use "Else If" because it is a subset of STUDENTS, don't need to add twice.
			else if (fqLogic.isQuestionAnswersVisibleTo(question,
					FeedbackParticipantType.OWN_TEAM_MEMBERS)) {

				List<FeedbackResponseAttributes> responses =
						getFeedbackResponsesForQuestion(feedbackQuestionId);
				List<FeedbackResponseAttributes> teamResponses =
						new ArrayList<FeedbackResponseAttributes>();

				for (FeedbackResponseAttributes response : responses) {
					
					List<StudentAttributes> studentsInTeam =
							studentsLogic.getStudentsForTeam(
									response.giverEmail, question.courseId);
					
					for (StudentAttributes student : studentsInTeam) {
						if (student.email.equals(userEmail)) {
							teamResponses.add(response);
						}
					}
					
				}				
				addNewResponses(viewableResponses, teamResponses);
			}
		}

		// Add all responses if user is instructor and question is visible to instructors.
		if (instructorsLogic.getInstructorForEmail(question.courseId, userEmail) != null &&
				fqLogic.isQuestionAnswersVisibleTo(question,
						FeedbackParticipantType.INSTRUCTORS)) {
			addNewResponses(viewableResponses,
					getFeedbackResponsesForQuestion(feedbackQuestionId));
		}

		// Add all responses that user is a receiver of and question is visible to receiver.
		if (fqLogic.isQuestionAnswersVisibleTo(question,
				FeedbackParticipantType.RECEIVER)) {
			addNewResponses (viewableResponses,
					getFeedbackResponsesForReceiver(feedbackQuestionId,	userEmail));
		}

		return viewableResponses;
	}
	
	// Adds FeedbackResponseAttributes in newResponses that are not already
	// in existingResponses to existingResponses. 
	private void addNewResponses(
			List<FeedbackResponseAttributes> existingResponses,
			List<FeedbackResponseAttributes> newResponses) {
		
		boolean alreadyAdded;
		
		for (FeedbackResponseAttributes newResponse : newResponses) {
			alreadyAdded = false;
			for (FeedbackResponseAttributes existingResponse : existingResponses) {
				if(newResponse.getId() == existingResponse.getId()) {
					alreadyAdded = true;
					break;
				}
			}
			if (!alreadyAdded) {
				existingResponses.add(newResponse);
			}
		}
		
	}

	public void updateFeedbackResponsesForChangingTeam(String userEmail, String newTeam) {
		// This method is not required.
		// for giverEmail, a change in team will mean when we search 
		// for userEmail again later, he will already be under a new team.
		// he will be under the new team already.
		// for receiver, we already track by team name
	}

	public void updateFeedbackResponsesForChangingEmail(String oldEmail, String newEmail) {
		// TODO:
		// for giver, change needs to be cascaded.
		// for receiver, change needs to be cascaded only if question type
		// is not TEAMS
	}
}
