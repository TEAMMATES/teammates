package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import teammates.common.Assumption;

/**
 * Represents detailed results for an feedback session.
 * <br> Contains:
 * <br> * The basic {@link FeedbackSessionAttributes} 
 * <br> * {@link List} of viewable responses as {@link FeedbackResponseAttributes} objects.
 */
public class FeedbackSessionResultsBundle {
	public FeedbackSessionAttributes feedbackSession = null;
	public List<FeedbackQuestionAttributes> questions = null;
	public List<FeedbackResponseAttributes> responses = null;
	public Map<String, String> emailNameTable = null;
	
	public FeedbackSessionResultsBundle (FeedbackSessionAttributes feedbackSession,
			List<FeedbackQuestionAttributes> questions,
			List<FeedbackResponseAttributes> responses,
			Map<String, String> emailNameTable) {
		this.feedbackSession = feedbackSession;
		this.questions = questions;
		this.responses = responses;
		this.emailNameTable = emailNameTable;
	}
	
	public Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> getQuestionResponseMapForStudent() {
		
		if (questions == null || responses == null) {
			return null;
		}
		
		Map<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>> sortedMap
		 	= new LinkedHashMap<FeedbackQuestionAttributes, List<FeedbackResponseAttributes>>();
		
		Collections.sort(questions);
		
		for (FeedbackQuestionAttributes question : questions) {
			List<FeedbackResponseAttributes> responsesForQn =
					new ArrayList<FeedbackResponseAttributes>();
			for (FeedbackResponseAttributes response : responses) {
				if(response.feedbackQuestionId.equals(question.getId())) {
					responsesForQn.add(response);
				}
			}
			Collections.sort(responses, new Comparator<FeedbackResponseAttributes>() {
				@Override
				public int compare(FeedbackResponseAttributes o1,
						FeedbackResponseAttributes o2) {
					String giverName1 = emailNameTable.get(o1.giverEmail);
					String giverName2 = emailNameTable.get(o2.giverEmail);
					if(giverName1 == null || giverName2 == null) {
						Assumption.fail("email-name table is missing the requested giver email.");
					}
					return giverName1.compareTo(giverName2); 
				}
			});
			sortedMap.put(question, responsesForQn);
		}
		
		return sortedMap;		
	}
	
	public Map<String, List<FeedbackResponseAttributes>> getRecipientResponseMapForInstructor() {
		
		if (questions == null || responses == null) {
			return null;
		}
		
		Map<String, List<FeedbackResponseAttributes>> sortedMap
		 	= new LinkedHashMap<String, List<FeedbackResponseAttributes>>();
		
		Collections.sort(responses, compareByRecipientName);
		
		for (FeedbackResponseAttributes response : responses) {
			String recipientName = emailNameTable.get(response.recipient);
			if(sortedMap.containsKey(recipientName) == false) {
				// Create entry if no such recipient yet
				List<FeedbackResponseAttributes> responsesForRecipient =
						new ArrayList<FeedbackResponseAttributes>();
				responsesForRecipient.add(response);
				sortedMap.put(recipientName, responsesForRecipient);
			} else {
				sortedMap.get(recipientName).add(response);
			}
		}
		
		for (List<FeedbackResponseAttributes> responseForRecipient : sortedMap.values()) {
			Collections.sort(responseForRecipient, compareByGiverName);
		}
		
		return sortedMap;
	}
	
	/* COMPARATORS */
	
	// Sorts by giverName > qnNumber > recipientName
	private Comparator<FeedbackResponseAttributes> compareByGiverName
		= new Comparator<FeedbackResponseAttributes>() {
		@Override
		public int compare(FeedbackResponseAttributes o1,
				FeedbackResponseAttributes o2) {
			String giverName1 = emailNameTable.get(o1.giverEmail);
			String giverName2 = emailNameTable.get(o2.giverEmail);
			String recipientName1 = emailNameTable.get(o1.recipient);
			String recipientName2 = emailNameTable.get(o2.recipient);
			int order = giverName1.compareTo(giverName2);
			order = (order == 0? compareByQuestionNumber(o1, o2) : order);
			return order == 0 ? recipientName1.compareTo(recipientName2) : order; 
		}
	};
	
	//Sorts by recipientName > qnNumber > giverName
	private final Comparator<FeedbackResponseAttributes> compareByRecipientName
		= new Comparator<FeedbackResponseAttributes>() {
		@Override
		public int compare(FeedbackResponseAttributes o1,
				FeedbackResponseAttributes o2) {
			String giverName1 = emailNameTable.get(o1.giverEmail);
			String giverName2 = emailNameTable.get(o2.giverEmail);
			String recipientName1 = emailNameTable.get(o1.recipient);
			String recipientName2 = emailNameTable.get(o2.recipient);
			int order = recipientName1.compareTo(recipientName2);
			order = (order == 0? compareByQuestionNumber(o1, o2) : order);
			return order == 0 ? giverName1.compareTo(giverName2) : order; 
		}
	};
	
	private int compareByQuestionNumber(FeedbackResponseAttributes r1, FeedbackResponseAttributes r2) {
		FeedbackQuestionAttributes q1 = null;
		FeedbackQuestionAttributes q2 = null;
		for (FeedbackQuestionAttributes question : questions) {
			if(question.getId().equals(r1.feedbackQuestionId)) {
				q1 = question;
			}
			if(question.getId().equals(r2.feedbackQuestionId)) {
				q2 = question;
			}
			if (q1 != null && q2 != null) {
				break;
			}
		}
		if (q1 == null || q2 == null) {
			return 0;
		} else {
			return q1.compareTo(q2);
		}
	}
}

