package teammates.logic.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.StudentAttributes;
import teammates.common.datatransfer.StudentEnrollDetails;
import teammates.common.datatransfer.UserType;
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
	private static final FeedbackQuestionsLogic fqLogic = FeedbackQuestionsLogic.inst();
	private static final FeedbackResponsesDb frDb = new FeedbackResponsesDb();	
	
	public static FeedbackResponsesLogic inst() {
		if (instance == null)
			instance = new FeedbackResponsesLogic();
		return instance;
	}
	
	public void createFeedbackResponse(FeedbackResponseAttributes fra)
		throws InvalidParametersException{
			try {
				frDb.createEntity(fra);
			} catch(Exception EntityAlreadyExistsException){
					
			try{
				FeedbackResponseAttributes existingFeedback = new FeedbackResponseAttributes();
				
				existingFeedback = frDb.getFeedbackResponse(fra.feedbackQuestionId, fra.giverEmail, fra.recipientEmail);
				fra.setId(existingFeedback.getId());
				
				frDb.updateFeedbackResponse(fra);
				
			} catch(Exception EntityDoesNotExistException){
				Assumption.fail();
			}
		}
	}
	
	public FeedbackResponseAttributes getFeedbackResponse(String feedbackQuestionId,
			String giverEmail, String recipient) {
		return frDb.getFeedbackResponse(feedbackQuestionId, giverEmail, recipient);
	}
		
	public List<FeedbackResponseAttributes> getFeedbackResponsesForSession(
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
	
	/**
	 * Get existing feedback responses from student or his team for the given question.
	 */
	public List<FeedbackResponseAttributes> getFeedbackResponsesFromStudentOrTeamForQuestion(
			FeedbackQuestionAttributes question, StudentAttributes student) {
		if(question.giverType == FeedbackParticipantType.TEAMS) {
			return getFeedbackResponsesFromTeamForQuestion(
					question.getId(), question.courseId, student.team);
		} else {
			return frDb.getFeedbackResponsesFromGiverForQuestion(
					question.getId(), student.email);
		}
	}

	public List<FeedbackResponseAttributes> getViewableFeedbackResponsesForQuestion(
			FeedbackQuestionAttributes question, String userEmail, UserType.Role role) {

		List<FeedbackResponseAttributes> viewableResponses =
				new ArrayList<FeedbackResponseAttributes>();

		// Add responses that user is a receiver of when question is visible to receiver.
		if (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
			addNewResponses (
					viewableResponses,
					frDb.getFeedbackResponsesForReceiverForQuestion(question.getId(),	userEmail));
		}

		switch (role) {
		case STUDENT:
			addNewResponses(
					viewableResponses,
					//many queries
					getViewableFeedbackResponsesForStudentForQuestion(question,	userEmail));
			break;
		case INSTRUCTOR:
			if (question.isResponseVisibleTo(FeedbackParticipantType.INSTRUCTORS)) {
				addNewResponses(viewableResponses,
						getFeedbackResponsesForQuestion(question.getId()));
			}
			break;
		default:
			Assumption.fail("The role of the requesting use has to be Student or Instructor");
		}
		
		return viewableResponses;
	}
	
	public boolean isNameVisibleTo(
			FeedbackQuestionAttributes question, 
			FeedbackResponseAttributes response,
			String userEmail, boolean isGiverName, CourseRoster roster){
		
		if(question == null) {
			return false;
		}
		
		List<FeedbackParticipantType> showNameTo =
				isGiverName ? question.showGiverNameTo : question.showRecipientNameTo;
		
		for (FeedbackParticipantType type : showNameTo) {
			switch (type) {
			case INSTRUCTORS:
				if (roster.getInstructorForEmail(userEmail) != null) {
					return true;
				} else {
					break;
				}
			case OWN_TEAM_MEMBERS:
			case OWN_TEAM_MEMBERS_INCLUDING_SELF:
				// Refers to Giver's Team Members
				if (roster.isStudentsInSameTeam(response.giverEmail, userEmail)) {
					return true;
				} else {
					break;
				}
			case RECEIVER:
				// Response to team
				if (question.recipientType == FeedbackParticipantType.TEAMS) {
					if (roster.isStudentInTeam(userEmail, /*this is a team name*/response.recipientEmail)) {
						return true;
					}
				// Response to individual
				} else if (response.recipientEmail.equals(userEmail)) {
					return true;
				} else {
					break;
				}
			case RECEIVER_TEAM_MEMBERS:
				// Response to team; recipient = teamName
				if (question.recipientType == FeedbackParticipantType.TEAMS) {
					if (roster.isStudentInTeam(userEmail, /*this is a team name*/response.recipientEmail)) {
						return true;
					}
				// Response to individual
				} else if (roster.isStudentsInSameTeam(response.recipientEmail, userEmail)) {
					return true;
				} else {
					break;
				}
			case STUDENTS:
				if (roster.isStudentInCourse(userEmail)) {
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
	 * Updates a {@link FeedbackResponse} based on it's {@code id}.<br>
	 * If the giver/recipient field is changed,
	 * the {@link FeedbackResponse} is updated by by recreating the response<br> in order to 
	 * prevent an id clash if the previous email is reused later on.
	 */
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
		
		if (newResponse.responseMetaData == null) {
			newResponse.responseMetaData = oldResponse.responseMetaData;
		}
		if (newResponse.giverEmail == null) {
			newResponse.giverEmail = oldResponse.giverEmail;
		}
		if (newResponse.recipientEmail == null) {
			newResponse.recipientEmail = oldResponse.recipientEmail;
		}
		
		if (!newResponse.recipientEmail.equals(oldResponse.recipientEmail) ||
			!newResponse.giverEmail.equals(oldResponse.giverEmail)) {
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
	
	public void updateFeedbackResponseForChangingTeam(StudentEnrollDetails enrollment,
			FeedbackResponseAttributes response) {
		
		FeedbackQuestionAttributes question = fqLogic.getFeedbackQuestion(response.feedbackQuestionId);
		
		boolean isGiverOfResponse = response.giverEmail.equals(enrollment.email);
		boolean isReceiverOfResponse = response.recipientEmail.equals(enrollment.email);
		
		boolean shouldDeleteResponse = (isGiverOfResponse && (question.giverType == FeedbackParticipantType.TEAMS
										|| question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS)) ||
										(isReceiverOfResponse && question.recipientType == FeedbackParticipantType.OWN_TEAM_MEMBERS);
		
		if(shouldDeleteResponse) {
			frDb.deleteEntity(response);
		}
	}
	
	/**
	 * Updates responses for a student when his email changes.
	 */
	public void updateFeedbackResponsesForChangingEmail(
			String courseId, String oldEmail, String newEmail)
					throws InvalidParametersException, EntityDoesNotExistException {	
		
		List<FeedbackResponseAttributes> responsesFromUser =
				getFeedbackResponsesFromGiverForCourse(courseId, oldEmail);
		
		for (FeedbackResponseAttributes response : responsesFromUser) {
			response.giverEmail = newEmail;
			try {
				updateFeedbackResponse(response);
			} catch (EntityAlreadyExistsException e) {
				Assumption.fail("Feedback response failed to update successfully" +
						"as email was already in use.");
			}
		}
		
		List<FeedbackResponseAttributes> responsesToUser =
				getFeedbackResponsesForReceiverForCourse(courseId, oldEmail);
		
		for (FeedbackResponseAttributes response : responsesToUser) {
			response.recipientEmail = newEmail;
			try {
				updateFeedbackResponse(response);
			} catch (EntityAlreadyExistsException e) {
				Assumption.fail("Feedback response failed to update successfully" +
						"as email was already in use.");
			}
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
	
	/**
	 * Adds {@link FeedbackResponseAttributes} in {@code newResponses}
	 * that are not already in to {@code existingResponses} to {@code existingResponses}.
	 */
	private void addNewResponses(
			List<FeedbackResponseAttributes> existingResponses,
			List<FeedbackResponseAttributes> newResponses) {
		
		Map<String, FeedbackResponseAttributes> responses = 
				new HashMap<String, FeedbackResponseAttributes>();
		
		for (FeedbackResponseAttributes existingResponse : existingResponses) {
			responses.put(existingResponse.getId(), existingResponse);
		}
		for (FeedbackResponseAttributes newResponse : newResponses) {
			if(!responses.containsKey(newResponse.getId())) {
				responses.put(newResponse.getId(), newResponse);
				existingResponses.add(newResponse);
			}
		}
	}
	
	private List<FeedbackResponseAttributes> getFeedbackResponsesFromTeamForQuestion(
			String feedbackQuestionId, String courseId, String teamName) {
		
		List<FeedbackResponseAttributes> responses = 
				new ArrayList<FeedbackResponseAttributes>();
		List<StudentAttributes> studentsInTeam =
				studentsLogic.getStudentsForTeam(teamName, courseId);
		
		for(StudentAttributes student : studentsInTeam) {
			responses.addAll(frDb.getFeedbackResponsesFromGiverForQuestion(
					feedbackQuestionId, student.email));
		}
	
		return responses;
	}
	
	private List<FeedbackResponseAttributes> getFeedbackResponsesForTeamMembersOfStudent(
			String feedbackQuestionId, String userEmail) {

		List<FeedbackResponseAttributes> responses =
				getFeedbackResponsesForQuestion(feedbackQuestionId);
		List<FeedbackResponseAttributes> teamResponses =
				new ArrayList<FeedbackResponseAttributes>();

		for (FeedbackResponseAttributes response : responses) {					
			StudentAttributes student =
					studentsLogic.getStudentForEmail(
							response.courseId, response.recipientEmail);
			
			if(studentsLogic.isStudentInTeam(
					response.courseId, student.team, userEmail)) {
				teamResponses.add(response);
			}
		}
		return teamResponses;
	}
	
	private List<FeedbackResponseAttributes> getViewableFeedbackResponsesForStudentForQuestion(
			FeedbackQuestionAttributes question, String studentEmail) {
		
		List<FeedbackResponseAttributes> viewableResponses =
				new ArrayList<FeedbackResponseAttributes>();
		
		StudentAttributes student = 
				studentsLogic.getStudentForEmail(question.courseId, studentEmail);
		
		if (question.isResponseVisibleTo(FeedbackParticipantType.STUDENTS)) {
			addNewResponses(viewableResponses,
					getFeedbackResponsesForQuestion(question.getId()));
			
			// Early return as STUDENTS covers all other student types.
			return viewableResponses;
		}
		
		if (question.recipientType == FeedbackParticipantType.TEAMS &&
			question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER)) {
				addNewResponses(viewableResponses,
						getFeedbackResponsesForReceiverForQuestion(question.getId(), student.team));
		}
		
		if (question.isResponseVisibleTo(FeedbackParticipantType.OWN_TEAM_MEMBERS)) {
			addNewResponses(viewableResponses,
					getFeedbackResponsesFromTeamForQuestion(
							question.getId(), question.courseId, student.team));
		} 
		if (question.isResponseVisibleTo(FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)) {
			addNewResponses(viewableResponses,
					getFeedbackResponsesForTeamMembersOfStudent(question.getId(), studentEmail));
		}
	
		return viewableResponses;
	}
}
