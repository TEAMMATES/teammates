package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.util.Config;

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
				response.giverEmail += Config.TEAM_OF_EMAIL_OWNER;
			}
		}
	}
	
	// TODO: make responses to student always on top.
	// Method returns an ordered map with keys sorted by questionNumber. 
	// Values are sorted by recipientName > giverName
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
				// Put final giver responses
				responsesToOneRecipient.put(prevGiver,
						responsesFromOneGiverToOneRecipient);
				// Put responses for previous recipient into map
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
				// Put final recipient responses
				responsesFromOneGiver.put(prevRecipient,
						responsesFromOneGiverToOneRecipient);
				// Put responses for previous giver into map
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

		return sortedMap;
	}
	
	/* COMPARATORS */
	@SuppressWarnings("unused")
	private void ________________COMPARATORS_____________(){}
	
	// Sorts by giverName > recipientName > qnNumber
	public Comparator<FeedbackResponseAttributes> compareByGiverName
		= new Comparator<FeedbackResponseAttributes>() {
//		@Override
		public int compare(FeedbackResponseAttributes o1,
				FeedbackResponseAttributes o2) {
			String giverName1 = emailNameTable.get(o1.giverEmail);
			String giverName2 = emailNameTable.get(o2.giverEmail);
			String recipientName1 = emailNameTable.get(o1.recipient);
			String recipientName2 = emailNameTable.get(o2.recipient);
			int order = giverName1.compareTo(giverName2);
			order = (order == 0 ? recipientName1.compareTo(recipientName2) : order);
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
			int order = recipientName1.compareTo(recipientName2);
			order = (order == 0 ? giverName1.compareTo(giverName2) : order);
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
}

