package teammates.logic.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Utils;
import teammates.storage.api.FeedbackResponsesDb;

public class FeedbackResponsesLogic {

	private static final Logger log = Utils.getLogger();
	
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
	
	public void updateFeedbackResponse(FeedbackResponseAttributes responseToUpdate)
			throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
		
		// Create a copy.
		FeedbackResponseAttributes newResponse = new FeedbackResponseAttributes(responseToUpdate);
		FeedbackResponseAttributes oldResponse = frDb.getFeedbackResponse(newResponse.getId());
		
		if (oldResponse == null) {
			throw new EntityDoesNotExistException(
					"Trying to update a feedback response that does not exist.");
		}
		
		// Copy values that cannot be changed to defensively avoid invalid parameters.
		newResponse.courseId = oldResponse.courseId;
		newResponse.feedbackSessionName = oldResponse.feedbackSessionName;
		newResponse.feedbackQuestionId = oldResponse.feedbackQuestionId;
		newResponse.feedbackQuestionType = oldResponse.feedbackQuestionType;
		newResponse.giverEmail = oldResponse.giverEmail;
		
		if (newResponse.answer == null) {
			newResponse.answer = oldResponse.answer;
		}
		if (newResponse.recipient == null) {
			newResponse.recipient = oldResponse.recipient;
		}
		
		if (!newResponse.recipient.equals(oldResponse.recipient)) {
			// Recreate response to prevent possible future id conflict.
			try {
				newResponse.setId(null);
				frDb.createEntity(newResponse);
				frDb.deleteEntity(oldResponse);
			} catch (EntityAlreadyExistsException e){
				log.warning("Trying to update an existing response to one that already exists.");
				throw new EntityAlreadyExistsException(
						e.getMessage() + Const.EOL +
						"Trying to update recipient for response to one that already exists for this giver.");
			}
		} else {
			frDb.updateFeedbackResponse(newResponse);
		}
	}
	
	public void deleteFeedbackResponse(FeedbackResponseAttributes responseToDelete) {
		frDb.deleteEntity(responseToDelete);
	}
	
	public void deleteFeedbackResponsesForQuestion(String feedbackQuestionId) {
		List<FeedbackResponseAttributes> responsesForQuestion =
				getFeedbackResponsesForQuestion(feedbackQuestionId);		
		for(FeedbackResponseAttributes response : responsesForQuestion) {
			frDb.deleteEntity(response);
		}
	}
	
	public FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId,
			String giverEmail, String recipient) {
		return frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
	}
		
	public List<FeedbackResponseAttributes> getFeedbackResponsesForFeedbackSession(
			String feedbackSessionName, String courseId) {
		return frDb.getFeedbackResponsesForSession(feedbackSessionName, courseId);
	}
		
	public List<FeedbackResponseAttributes> getFeedbackResponsesForQuestion(
			String feedbackQuestionId) {
		return frDb.getFeedbackResponsesForQuestion(feedbackQuestionId);
	}
	
	public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForQuestion(
			String feedbackQuestionId, String userEmail) {
		return frDb.getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId, userEmail);
	}
	
	public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForQuestion(
			String feedbackQuestionId, String userEmail) {
		return frDb.getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, userEmail);
	}

	public List<FeedbackResponseAttributes> getFeedbackResponsesForReceiverForCourse(
			String courseId, String userEmail) {
		return frDb.getFeedbackResponsesForReceiverForCourse(courseId, userEmail);
	}
	
	public List<FeedbackResponseAttributes> getFeedbackResponsesFromGiverForCourse(
			String courseId, String userEmail) {
		return frDb.getFeedbackResponsesFromGiverForCourse(courseId, userEmail);
	}
	
	public int getNumberOfResponsesFromTeamForQuestion(
			String courseId, String feedbackQuestionId, String teamName) {
		
		int count = 0;
		
		List<StudentAttributes> students = 
				studentsLogic.getStudentsForTeam(teamName, courseId);
		
		for (StudentAttributes student : students) {
			count += frDb.getFeedbackResponsesFromGiverForQuestion(feedbackQuestionId, student.email).size();
		}
		
		return count;
	}
	
	//TODO: This method is too long and nesting is too deep.
	public List<FeedbackResponseAttributes> getViewableFeedbackResponsesForQuestion(
			String feedbackQuestionId, String userEmail) {

		List<FeedbackResponseAttributes> viewableResponses =
				new ArrayList<FeedbackResponseAttributes>();

		FeedbackQuestionAttributes question = 
				fqLogic.getFeedbackQuestion(feedbackQuestionId);
		
		// Add responses that user is a receiver of when question is visible to receiver.
		if (fqLogic.isQuestionAnswersVisibleTo(question,
				FeedbackParticipantType.RECEIVER)) {
			addNewResponses (viewableResponses,
					frDb.getFeedbackResponsesForReceiverForQuestion(feedbackQuestionId,	userEmail));
		}

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
				addNewResponses(viewableResponses,
						getFeedbackResponsesFromTeamMembersOfUser(
								feedbackQuestionId, userEmail));
			}
		}

		// Add all responses if user is instructor and question is visible to instructors.
		if (instructorsLogic.getInstructorForEmail(question.courseId, userEmail) != null &&
				fqLogic.isQuestionAnswersVisibleTo(question,
						FeedbackParticipantType.INSTRUCTORS)) {
			addNewResponses(viewableResponses,
					getFeedbackResponsesForQuestion(feedbackQuestionId));
		}
		
		return viewableResponses;
	}

	private List<FeedbackResponseAttributes> getFeedbackResponsesFromTeamMembersOfUser(
			String feedbackQuestionId, String userEmail) {
		List<FeedbackResponseAttributes> responses =
				getFeedbackResponsesForQuestion(feedbackQuestionId);
		List<FeedbackResponseAttributes> teamResponses =
				new ArrayList<FeedbackResponseAttributes>();

		for (FeedbackResponseAttributes response : responses) {					
			StudentAttributes student =
					studentsLogic.getStudentForEmail(
							response.courseId, response.giverEmail);
			
			if(studentsLogic.isStudentInTeam(
					response.courseId, student.team, userEmail)) {
				teamResponses.add(response);
			}
		}
		return teamResponses;
	}
	
	// Adds FeedbackResponseAttributes in newResponses that are not already
	// in existingResponses to existingResponses. 
	// TODO: use a Map with ID as key for a faster implementation?
	private void addNewResponses(
			List<FeedbackResponseAttributes> existingResponses,
			List<FeedbackResponseAttributes> newResponses) {
		
		boolean alreadyAdded;
		
		for (FeedbackResponseAttributes newResponse : newResponses) {
			alreadyAdded = false;
			for (FeedbackResponseAttributes existingResponse : existingResponses) {
				if(newResponse.getId().equals(existingResponse.getId())) {
					alreadyAdded = true;
					break;
				}
			}
			if (!alreadyAdded) {
				existingResponses.add(newResponse);
			}
		}
		
	}
	
	public boolean isNameVisibleTo(FeedbackResponseAttributes response,
			String userEmail, boolean isGiverName){
		
		FeedbackQuestionAttributes question = 
				fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
		
		if(question == null) {
			return false;
		}
		
		List<FeedbackParticipantType> showNameTo =
				isGiverName ? question.showGiverNameTo : question.showRecipientNameTo;
		
		for (FeedbackParticipantType type : showNameTo) {
			switch (type) {
			case INSTRUCTORS:
				if (instructorsLogic.getInstructorForEmail(response.courseId, userEmail) != null) {
					return true;
				} else {
					break;
				}
			case OWN_TEAM_MEMBERS:
				// Refers to Giver's Team Members
				if (studentsLogic.isStudentsInSameTeam(response.courseId, response.giverEmail, userEmail)) {
					return true;
				} else {
					break;
				}
			case RECEIVER:
				// Response to team
				if (question.recipientType == FeedbackParticipantType.TEAMS) {
					if (studentsLogic.isStudentInTeam(response.courseId, response.recipient, userEmail)) {
						return true;
					}
				// Response to individual
				} else if (response.recipient.equals(userEmail)) {
					return true;
				} else {
					break;
				}
			case RECEIVER_TEAM_MEMBERS:
				// Response to team; recipient = teamName
				if (question.recipientType == FeedbackParticipantType.TEAMS) {
					if (studentsLogic.isStudentInTeam(response.courseId, response.recipient, userEmail)) {
						return true;
					}
				// Response to individual
				} else if (studentsLogic.isStudentsInSameTeam(response.courseId, response.recipient, userEmail)) {
					return true;
				} else {
					break;
				}
			case STUDENTS:
				if (studentsLogic.isStudentInCourse(response.courseId, userEmail)) {
					return true;
				} else {
					break;
				}
			default:
				Assumption.fail("Invalid FeedbackPariticipantType for showNameTo in " +
						"FeedbackResponseLogic.isNameVisible()");
				break;
			}
		}
		return false;
	}
	
	/**
	 * Updates responses for a student when his team changes. This is done by deleting
	 * responses that are no longer relevant to him in his new team.
	 */
	public void updateFeedbackResponsesForChangingTeam(
			String courseId, String userEmail, String oldTeam, String newTeam)
					throws EntityDoesNotExistException {
		
		FeedbackQuestionAttributes question;
		
		List<FeedbackResponseAttributes> responsesFromUser =
				getFeedbackResponsesFromGiverForCourse(courseId, userEmail);
		
		for (FeedbackResponseAttributes response : responsesFromUser) {
			question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
			if (question.giverType == FeedbackParticipantType.TEAMS || 
				question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
				frDb.deleteEntity(response);
			}
		}
		
		List<FeedbackResponseAttributes> responsesToUser =
				getFeedbackResponsesForReceiverForCourse(courseId, userEmail);
		
		for (FeedbackResponseAttributes response : responsesToUser) {
			question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
			if (question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS) {
				frDb.deleteEntity(response);
			}
		}
		
		if(studentsLogic.getStudentsForTeam(oldTeam, courseId).isEmpty()){
			List<FeedbackResponseAttributes> responsesToTeam =
					getFeedbackResponsesForReceiverForCourse(courseId, oldTeam);
			for (FeedbackResponseAttributes response : responsesToTeam) {
				frDb.deleteEntity(response);
			}
		}
	}

	/**
	 * Updates responses for a student when his email changes. This is done by recreating
	 * all responses from the student to prevent an id clash if
	 * the previous email is reused later on.
	 */
	public void updateFeedbackResponsesForChangingEmail(
			String courseId, String oldEmail, String newEmail)
					throws InvalidParametersException, EntityDoesNotExistException {	
		
		List<FeedbackResponseAttributes> responsesFromUser =
				getFeedbackResponsesFromGiverForCourse(courseId, oldEmail);
		
		for (FeedbackResponseAttributes response : responsesFromUser) {
			response.giverEmail = newEmail;
			response.setId(null); // so that persistence checks do not use old id.
			try {
				frDb.createEntity(response);
				response.giverEmail = oldEmail;
				frDb.deleteEntity(response);
			} catch (EntityAlreadyExistsException e) {
				Assumption.fail("Feedback response failed to update successfully" +
						"as email was already in use.");
			}
		}
		
		List<FeedbackResponseAttributes> responsesToUser =
				getFeedbackResponsesForReceiverForCourse(courseId, oldEmail);
		
		for (FeedbackResponseAttributes response : responsesToUser) {
			response.recipient = newEmail;
			try {
				updateFeedbackResponse(response);
			} catch (EntityAlreadyExistsException e) {
				Assumption.fail("Feedback response failed to update successfully" +
						"as email was already in use.");
			}
		}
	}

	public void deleteFeedbackResponsesForStudent(String courseId,
			String studentEmail) {
		
		String studentTeam = "";
		StudentAttributes student = studentsLogic.getStudentForEmail(courseId, studentEmail);
		
		if (student != null) {
			studentTeam = student.team;
		}
		
		List<FeedbackResponseAttributes> responses =
				getFeedbackResponsesFromGiverForCourse(courseId, studentEmail);
		responses.addAll(
				getFeedbackResponsesForReceiverForCourse(courseId, studentEmail));		
		// Delete responses to team as well if student is last person in team.
		if(studentsLogic.getStudentsForTeam(studentTeam, courseId).size() <= 1) {
			responses.addAll(getFeedbackResponsesForReceiverForCourse(courseId, studentTeam));
		}
		
		for(FeedbackResponseAttributes response : responses){
			frDb.deleteEntity(response);
		}
	}
	
}
