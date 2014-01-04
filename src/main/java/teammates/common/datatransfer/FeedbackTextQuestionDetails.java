package teammates.common.datatransfer;

import teammates.common.util.Const;
import teammates.common.util.Sanitizer;

public class FeedbackTextQuestionDetails extends
		FeedbackAbstractQuestionDetails {
	//TODO use this instead of plain text for essay questions
	//will involve converting the existing database
	
	public FeedbackTextQuestionDetails() {
		super(FeedbackQuestionType.TEXT);
	}
	
	public FeedbackTextQuestionDetails(String questionText) {
		super(FeedbackQuestionType.TEXT, questionText);
	}

	@Override
	public String getQuestionTypeDisplayName() {
		return Const.FeedbackQuestionTypeNames.TEXT;
	}

	@Override
	public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
			int responseIdx, FeedbackAbstractResponseDetails existingResponseDetails,
			FeedbackAbstractQuestionDetails questionDetails) {
		return "<textarea rows=\"4\" cols=\"100%\" class=\"textvalue\" "
				+ (sessionIsOpen ? "" : "disabled=\"disabled\" ")
				+ "name=\"" + Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnIdx + "-" + responseIdx + "\">"
				+ Sanitizer.sanitizeForHtml(existingResponseDetails.getAnswerString()) + "</textarea>";
	}

	@Override
	public String getQuestionWithoutExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx,
			FeedbackAbstractQuestionDetails questionDetails) {
		return "<textarea rows=\"4\" cols=\"100%\" class=\"textvalue\" "
				+ (sessionIsOpen ? "" : "disabled=\"disabled\" ")
				+ "name=\"" + Const.ParamsNames.FEEDBACK_RESPONSE_TEXT + "-" + qnIdx + "-" + responseIdx + "\">"
				+ "</textarea>";
	}

	@Override
	public String getQuestionSpecificEditFormHtml(int questionNumber) {
		return "";
	}
}
