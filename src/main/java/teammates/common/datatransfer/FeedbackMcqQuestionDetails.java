package teammates.common.datatransfer;

import java.util.List;

import teammates.common.util.Const;

public class FeedbackMcqQuestionDetails extends FeedbackAbstractQuestionDetails {
	public int numOfMcqChoices;
	public List<String> mcqChoices;
	public boolean otherEnabled;

	public FeedbackMcqQuestionDetails() {
		super(FeedbackQuestionType.MCQ);
	}

	public FeedbackMcqQuestionDetails(String questionText,
			int numOfMcqChoices,
			List<String> mcqChoices,
			boolean otherEnabled) {
		super(FeedbackQuestionType.MCQ, questionText);
		
		this.numOfMcqChoices = numOfMcqChoices;
		this.mcqChoices = mcqChoices;
		this.otherEnabled = otherEnabled;
	}

	@Override
	public String getQuestionTypeDisplayName() {
		return Const.FeedbackQuestionTypeNames.MCQ;
	}

	@Override
	public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
			int responseIdx, FeedbackAbstractResponseDetails existingResponseDetails,
			FeedbackAbstractQuestionDetails questionDetails) {
		FeedbackMcqQuestionDetails mcqDetails = (FeedbackMcqQuestionDetails) questionDetails;
		FeedbackMcqResponseDetails existingMcqResponseDetails = (FeedbackMcqResponseDetails) existingResponseDetails;
		
		StringBuilder html = new StringBuilder();
		html.append("<table>");
		
		for(int i = 0; i < mcqDetails.numOfMcqChoices; i++) {
			html.append("<tr><td><label><input type=\"radio\" "
						+ "name=\"" + Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnIdx + "-" + responseIdx + "\""
						+ (sessionIsOpen ? "" : "disabled=\"disabled\" ")
						+ "value=\"" + mcqDetails.mcqChoices.get(i) + "\""
						+ (existingMcqResponseDetails.answer.equals(mcqDetails.mcqChoices.get(i)) ? "checked=\"checked\" " : "")
						+ "> " + mcqDetails.mcqChoices.get(i));
			html.append("</label></td></tr>");
		}
		
		html.append("</table>");
		return html.toString();
	}

	@Override
	public String getQuestionWithoutExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx,
			FeedbackAbstractQuestionDetails questionDetails) {
		FeedbackMcqQuestionDetails mcqDetails = (FeedbackMcqQuestionDetails) questionDetails;
		
		StringBuilder html = new StringBuilder();
		html.append("<table>");
		
		for(int i = 0; i < mcqDetails.numOfMcqChoices; i++) {
			html.append("<tr><td><label><input type=\"radio\" "
						+ "name=\"" + Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnIdx + "-" + responseIdx + "\""
						+ (sessionIsOpen ? "" : "disabled=\"disabled\" ")
						+ "value=\"" + mcqDetails.mcqChoices.get(i) + "\""
						+ "> " + mcqDetails.mcqChoices.get(i));
			html.append("</label></td></tr>");
		}
		
		html.append("</table>");
		return html.toString();
	}

	@Override
	public String getQuestionSpecificEditFormHtml(int questionNumber) {
		StringBuilder html = new StringBuilder();
		
		html.append("<tr><td colspan=\"4\"><table>");
		
		for(int i = 0; i < numOfMcqChoices; i++) {
			html.append("<tr id=\"mcqOptionRow-" + i + "-" + questionNumber + "\">"
						+ "<td><input type=\"radio\" class=\"disabled_radio\" disabled=\"disabled\"></td>"
						+ "<td><input type=\"text\" disabled=\"disabled\" "
						+ "name=\"" + Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE + "-" + i + "\" "
						+ "id=\"" + Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE+ "-" + i + "-" + questionNumber + "\" "
						+ "class=\"mcqOptionTextBox\" value=\"" + mcqChoices.get(i) + "\">"
						+ "<a href=\"#\" class=\"removeOptionLink\" id=\"mcqRemoveOptionLink\" " 
						+ "onclick=\"removeMcqOption(" + i + "," + questionNumber + ")\" "
						+ "style=\"display:none\" tabindex=\"-1\"> x</a></td></tr>");		
		}
		
		html.append("<tr id=\"mcqAddOptionRow-" + questionNumber + "\">"
					+ "<td colspan=\"2\"><a href=\"#\" class=\"color_blue\" id=\"mcqAddOptionLink\" "
					+ "onclick=\"addMcqOption(" + questionNumber+ ")\" style=\"display:none\">"
					+ "+add more options</a></td></tr></table>");
		
		html.append("<input type=\"hidden\" name=\"" + Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED+ "\" "
					+ "id=\"" + Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED + "-" + questionNumber + "\" " 
					+ "value=\"" + numOfMcqChoices + "\"></td></tr>");
		
		return html.toString();
	}

}
