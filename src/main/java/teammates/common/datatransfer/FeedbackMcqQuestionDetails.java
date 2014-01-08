package teammates.common.datatransfer;

import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;

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
		
		StringBuilder optionListHtml = new StringBuilder();
		String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
		for(int i = 0; i < mcqDetails.numOfMcqChoices; i++) {
			String optionFragment = 
					FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
							"${qnIdx}", Integer.toString(qnIdx),
							"${responseIdx}", Integer.toString(responseIdx),
							"${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
							"${checked}", existingMcqResponseDetails.answer.equals(mcqDetails.mcqChoices.get(i)) ? "checked=\"checked\"" : "",
							"${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
							"${mcqChoiceValue}", mcqDetails.mcqChoices.get(i));
			optionListHtml.append(optionFragment + Const.EOL);
		}
		
		String html = FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM,
				"${mcqSubmissionFormOptionFragments}", optionListHtml.toString());
		
		return html;
	}

	@Override
	public String getQuestionWithoutExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx,
			FeedbackAbstractQuestionDetails questionDetails) {
		FeedbackMcqQuestionDetails mcqDetails = (FeedbackMcqQuestionDetails) questionDetails;
		
		StringBuilder optionListHtml = new StringBuilder();
		String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
		for(int i = 0; i < mcqDetails.numOfMcqChoices; i++) {
			String optionFragment = 
					FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
							"${qnIdx}", Integer.toString(qnIdx),
							"${responseIdx}", Integer.toString(responseIdx),
							"${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
							"${checked}", "",
							"${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
							"${mcqChoiceValue}", mcqDetails.mcqChoices.get(i));
			optionListHtml.append(optionFragment + Const.EOL);
		}
		
		String html = FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM,
				"${mcqSubmissionFormOptionFragments}", optionListHtml.toString());
		
		return html;
	}

	@Override
	public String getQuestionSpecificEditFormHtml(int questionNumber) {
		StringBuilder optionListHtml = new StringBuilder();
		String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_EDIT_FORM_OPTIONFRAGMENT;
		for(int i = 0; i < numOfMcqChoices; i++) {
			String optionFragment = 
					FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
							"${i}", Integer.toString(i),
							"${mcqChoiceValue}", mcqChoices.get(i),
							"${Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_MCQCHOICE);

			optionListHtml.append(optionFragment + Const.EOL);
		}
		
		String html = FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.MCQ_EDIT_FORM,
				"${mcqEditFormOptionFragments}", optionListHtml.toString(),
				"${questionNumber}", Integer.toString(questionNumber),
				"${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
				"${numOfMcqChoices}", Integer.toString(numOfMcqChoices));
		
		return html;
	}

}
