package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Const;

/**
 * Represents detailed results for an feedback session.
 * <br> Contains:
 * <br> * The basic {@link FeedbackSessionAttributes} 
 * <br> * {@link List} of viewable responses as {@link FeedbackResponseAttributes} objects.
 */
public class FeedbackSessionResultsBundle {
	public FeedbackSessionAttributes feedbackSession = null;
	public List<FeedbackResponseAttributes> responses = null;
	public Map<String, FeedbackQuestionAttributes> questions = null;
	public Map<String, String> emailNameTable = null;
	public Map<String, boolean[]> visibilityTable = null;
	
	public FeedbackSessionResultsBundle (FeedbackSessionAttributes feedbackSession,
			List<FeedbackResponseAttributes> responses,
			Map<String, FeedbackQuestionAttributes> questions,
			Map<String, String> emailNameTable,
			Map<String, boolean[]> visibilityTable) {
		this.feedbackSession = feedbackSession;
		this.questions = questions;
		this.responses = responses;
		this.emailNameTable = emailNameTable;
		this.visibilityTable = visibilityTable;

		// We change user email to team name here for display purposes.
		for (FeedbackResponseAttributes response : responses) {
			if (questions.get(response.feedbackQuestionId).giverType == FeedbackParticipantType.TEAMS){ 
				response.giverEmail += Const.TEAM_OF_EMAIL_OWNER;
			}
		}
	}
	
	public String getNameForEmail(String email) {
		String name = emailNameTable.get(email);		
		if (name.equals(Const.USER_IS_NOBODY)) {
			return "Class";
		} else if (name.equals(Const.USER_IS_TEAM)) {
			return "Unknown user";
		} else {
			return name;
		}
	}
	
	// TODO: make responses to the student calling this method always on top.
	/**
	 * Gets the questions and responses in this bundle as a map. 
	 * 
	 * @return An ordered {@code Map} with keys as {@link FeedbackQuestionAttributes}
	 *  sorted by questionNumber.
	 * The mapped values for each key are the corresponding
	 *  {@link FeedbackResponseAttributes} as a {@code List}. 
	 */
	public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getQuestionResponseMap() {
		
		if (questions == null || responses == null) {
			return null;
		}
		
		Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap
		 	= new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
		
		List<FeedbackQuestionAttributes> questionList =
				new ArrayList<FeedbackQuestionAttributes>(questions.values());
		Collections.sort(questionList);
		
		for (FeedbackQuestionAttributes question : questionList) {
			List<FeedbackResponseAttributes> responsesForQn =
					new ArrayList<FeedbackResponseAttributes>();
			for (FeedbackResponseAttributes response : responses) {
				if(response.feedbackQuestionId.equals(question.getId())) {
					responsesForQn.add(response);
				}
			}
			Collections.sort(responsesForQn, compareByRecipientName);
			sortedMap.put(question, responsesForQn);
		}
		
		return sortedMap;		
	}
	
	/**
	 * Returns the responses in this bundle as a {@code Tree} structure with no base node using a {@code LinkedHashMap} implementation.
	 * <br>The tree is sorted by recipientName > giverName > questionNumber.
	 * <br>The key of each map represents the parent node, while the value represents the leaf.
	 * <br>The top-most parent {@code String recipientName} is the recipient's name of all it's leafs.
	 * <br>The inner parent {@code String giverName} is the giver's name of all it's leafs.
	 * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
	 * <br>with attributes corresponding to it's parents.
	 * @return The responses in this bundle sorted by recipient's name > giver's name > question number.
	 */
	public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByRecipient() {

		Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
				new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

		Collections.sort(responses, compareByRecipientName);

		String prevGiver = null;
		String prevRecipient = null;

		List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
				new ArrayList<FeedbackResponseAttributes>();
		Map<String, List<FeedbackResponseAttributes>> responsesToOneRecipient =
				new LinkedHashMap<String, List<FeedbackResponseAttributes>>();

		for (FeedbackResponseAttributes response : responses) {
			// New recipient, add response package to map.
			if (response.recipient.equals(prevRecipient) == false
					&& prevRecipient != null) {
				// Put previous giver responses into inner map. 
				responsesToOneRecipient.put(prevGiver,
						responsesFromOneGiverToOneRecipient);
				// Put all responses for previous recipient into outer map.
				sortedMap.put(prevRecipient, responsesToOneRecipient);
				// Clear responses
				responsesToOneRecipient = new LinkedHashMap<String,
						List<FeedbackResponseAttributes>>();
				responsesFromOneGiverToOneRecipient = new 
						ArrayList<FeedbackResponseAttributes>();
			} else if (response.giverEmail.equals(prevGiver) == false 
					&& prevGiver != null) {
				// New giver, add giver responses to response package for
				// one recipient
				responsesToOneRecipient.put(prevGiver,
						responsesFromOneGiverToOneRecipient);
				// Clear response list
				responsesFromOneGiverToOneRecipient = new
						ArrayList<FeedbackResponseAttributes>();
			}
			
			responsesFromOneGiverToOneRecipient.add(response);

			prevGiver = response.giverEmail;
			prevRecipient = response.recipient;
		}
		
		if (responses.isEmpty() == false ) {
			// Put responses for final giver
			responsesToOneRecipient.put(prevGiver,
					responsesFromOneGiverToOneRecipient);
			sortedMap.put(prevRecipient, responsesToOneRecipient);
		}

		return sortedMap;
	}
	
	/**
	 * Returns the responses in this bundle as a {@code Tree} structure with no base node using a {@code LinkedHashMap} implementation.
	 * <br>The tree is sorted by giverName > recipientName > questionNumber.
	 * <br>The key of each map represents the parent node, while the value represents the leaf.
	 * <br>The top-most parent {@code String giverName} is the recipient's name of all it's leafs.
	 * <br>The inner parent {@code String recipientName} is the giver's name of all it's leafs.
	 * <br>The inner-most child is a {@code List<FeedbackResponseAttributes} of all the responses
	 * <br>with attributes corresponding to it's parents.
	 * @return The responses in this bundle sorted by giver's name > recipient's name > question number.
	 */
	public Map<String, Map<String, List<FeedbackResponseAttributes>>> getResponsesSortedByGiver() {

		Map<String, Map<String, List<FeedbackResponseAttributes>>> sortedMap =
				new LinkedHashMap<String, Map<String, List<FeedbackResponseAttributes>>>();

		Collections.sort(responses, compareByGiverName);

		String prevRecipient = null;
		String prevGiver = null;

		List<FeedbackResponseAttributes> responsesFromOneGiverToOneRecipient =
				new ArrayList<FeedbackResponseAttributes>();
		Map<String, List<FeedbackResponseAttributes>> responsesFromOneGiver =
				new LinkedHashMap<String, List<FeedbackResponseAttributes>>();

		for (FeedbackResponseAttributes response : responses) {
			// New recipient, add response package to map.
			if (response.giverEmail.equals(prevGiver) == false
					&& prevGiver != null) {
				// Put previous recipient responses into inner map. 
				responsesFromOneGiver.put(prevRecipient,
						responsesFromOneGiverToOneRecipient);
				// Put all responses for previous giver into outer map.
				sortedMap.put(prevGiver, responsesFromOneGiver);
				// Clear responses
				responsesFromOneGiver = new LinkedHashMap<String,
						List<FeedbackResponseAttributes>>();
				responsesFromOneGiverToOneRecipient = new 
						ArrayList<FeedbackResponseAttributes>();
			} else if (response.recipient.equals(prevRecipient) == false 
					&& prevRecipient != null) {
				// New recipient, add recipient responses to response package for
				// one giver
				responsesFromOneGiver.put(prevRecipient,
						responsesFromOneGiverToOneRecipient);
				// Clear response list
				responsesFromOneGiverToOneRecipient = new
						ArrayList<FeedbackResponseAttributes>();
			}
			
			responsesFromOneGiverToOneRecipient.add(response);

			prevRecipient = response.recipient;
			prevGiver = response.giverEmail;
		}
		
		if (responses.isEmpty() == false ) {
			// Put responses for final recipient
			responsesFromOneGiver.put(prevRecipient,
					responsesFromOneGiverToOneRecipient);
			sortedMap.put(prevGiver, responsesFromOneGiver);
		}

		return sortedMap;
	}
	
	/* COMPARATORS */
	@SuppressWarnings("unused")
	private void ________________COMPARATORS_____________(){}
	
	// Sorts by giverName > recipientName > qnNumber
	// General questions and team questions at the bottom.
	public Comparator<FeedbackResponseAttributes> compareByGiverName
		= new Comparator<FeedbackResponseAttributes>() {
		@Override
		public int compare(FeedbackResponseAttributes o1,
				FeedbackResponseAttributes o2) {
			String giverName1 = emailNameTable.get(o1.giverEmail);
			String giverName2 = emailNameTable.get(o2.giverEmail);
			String recipientName1 = emailNameTable.get(o1.recipient);
			String recipientName2 = emailNameTable.get(o2.recipient);			
				
			int order = compareByNames(giverName1, giverName2);
			order = (order == 0 ? compareByNames(recipientName1, recipientName2) : order);
			return order == 0? compareByQuestionNumber(o1, o2) : order; 
		}
	};
	
	//Sorts by recipientName > giverName > qnNumber
	public final Comparator<FeedbackResponseAttributes> compareByRecipientName
		= new Comparator<FeedbackResponseAttributes>() {
		@Override
		public int compare(FeedbackResponseAttributes o1,
				FeedbackResponseAttributes o2) {
			String giverName1 = emailNameTable.get(o1.giverEmail);
			String giverName2 = emailNameTable.get(o2.giverEmail);
			String recipientName1 = emailNameTable.get(o1.recipient);
			String recipientName2 = emailNameTable.get(o2.recipient);
			int order = compareByNames(recipientName1, recipientName2);
			order = (order == 0 ? compareByNames(giverName1, giverName2) : order);
			return order == 0 ? compareByQuestionNumber(o1, o2) : order; 
		}
	};
	
	private int compareByQuestionNumber(FeedbackResponseAttributes r1, FeedbackResponseAttributes r2) {
		FeedbackQuestionAttributes q1 = questions.get(r1.feedbackQuestionId);
		FeedbackQuestionAttributes q2 = questions.get(r2.feedbackQuestionId);		
		if (q1 == null || q2 == null) {
			return 0;
		} else {
			return q1.compareTo(q2);
		}
	}
	
	private int compareByNames(String n1, String n2) {
		
		// Make class feedback always appear on top, and team responses at bottom.
		int n1Priority = 0;
		int n2Priority = 0;
		
		if (n1.equals(Const.USER_IS_NOBODY)) {
			n1Priority = -1;
		} else if(n1.equals(Const.USER_IS_TEAM)) {
			n1Priority = 1;
		}
		if (n2.equals(Const.USER_IS_NOBODY)) {
			n2Priority = -1;
		} else if(n2.equals(Const.USER_IS_TEAM)) {
			n2Priority = 1;
		}
		
		int order = Integer.compare(n1Priority, n2Priority);
		return order == 0 ? n1.compareTo(n2) : order; 
	}
}
