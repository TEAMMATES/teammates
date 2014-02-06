package teammates.ui.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.AccountAttributes;
import teammates.common.datatransfer.FeedbackSessionQuestionsBundle;

public class FeedbackSubmissionEditPageData extends PageData {

	public FeedbackSessionQuestionsBundle bundle = null;
	public boolean isPreview;
	public String previewName;
	public String previewEmail;
	
	public FeedbackSubmissionEditPageData(AccountAttributes account) {
		super(account);
		isPreview = false;
		this.previewName = account.name;
		this.previewEmail = account.email;
	}

	/**
	 * To allow previewing for students who have not joined the course
	 */
	public FeedbackSubmissionEditPageData(String previewName, String previewEmail) {
		super(null);
		isPreview = true;
		this.previewName = previewName;
		this.previewEmail = previewEmail;
	}
	
	public List<String> getRecipientOptionsForQuestion(String feedbackQuestionId, String currentlySelectedOption) {
		ArrayList<String> result = new ArrayList<String>();		
		if(this.bundle == null) {
			return null;
		}
		
		Map<String, String> emailNamePair = this.bundle.getSortedRecipientList(feedbackQuestionId);
		
		// Add an empty option first.
		result.add("<option value=\"\" " +
				(currentlySelectedOption==null ? "selected=\"selected\">" : ">") +
				"</option>");
		
		for(Map.Entry<String, String> pair : emailNamePair.entrySet()) {
			result.add("<option value=\""+pair.getKey()+"\"" +
					(pair.getKey().equals(currentlySelectedOption) 
						? " selected=\"selected\"" : "") +
					">"+sanitizeForHtml(pair.getValue())+"</option>");			
		}

		return result;
	}
}
