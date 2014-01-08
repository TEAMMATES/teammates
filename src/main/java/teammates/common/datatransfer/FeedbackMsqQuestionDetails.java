package teammates.common.datatransfer;

import java.util.List;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;

public class FeedbackMsqQuestionDetails extends FeedbackAbstractQuestionDetails {
	public int numOfMsqChoices;
	public List<String> msqChoices;
	public boolean otherEnabled;
	
	public FeedbackMsqQuestionDetails() {
		super(FeedbackQuestionType.MSQ);
	}

	public FeedbackMsqQuestionDetails(String questionText,
			int numOfMsqChoices,
			List<String> msqChoices,
			boolean otherEnabled) {
		super(FeedbackQuestionType.MSQ, questionText);
		
		this.numOfMsqChoices = numOfMsqChoices;
		this.msqChoices = msqChoices;
		this.otherEnabled = otherEnabled;
	}

	@Override
	public String getQuestionTypeDisplayName() {
		return Const.FeedbackQuestionTypeNames.MSQ;
	}
	
	@Override
	public boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails) {
		FeedbackMsqQuestionDetails newMsqDetails = (FeedbackMsqQuestionDetails) newDetails;

		if (this.numOfMsqChoices != newMsqDetails.numOfMsqChoices ||
			this.msqChoices.containsAll(newMsqDetails.msqChoices) == false ||
			newMsqDetails.msqChoices.containsAll(this.msqChoices) == false) {
			return true;
		}
		
		return false;
	}


	@Override
	public String getQuestionWithExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx,
			FeedbackAbstractResponseDetails existingResponseDetails,
			FeedbackAbstractQuestionDetails questionDetails) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getQuestionWithoutExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx,
			FeedbackAbstractQuestionDetails questionDetails) {
		// TODO Auto-generated method stub
		return "";
	}

	@Override
	public String getQuestionSpecificEditFormHtml(int questionNumber) {
		StringBuilder optionListHtml = new StringBuilder();
		String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_EDIT_FORM_OPTIONFRAGMENT;
		for(int i = 0; i < numOfMsqChoices; i++) {
			String optionFragment = 
					FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
							"${i}", Integer.toString(i),
							"${msqChoiceValue}", msqChoices.get(i),
							"${Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE}", Const.ParamsNames.FEEDBACK_QUESTION_MSQCHOICE);

			optionListHtml.append(optionFragment + Const.EOL);
		}
		
		String html = FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.MSQ_EDIT_FORM,
				"${msqEditFormOptionFragments}", optionListHtml.toString(),
				"${questionNumber}", Integer.toString(questionNumber),
				"${Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED}", Const.ParamsNames.FEEDBACK_QUESTION_NUMBEROFCHOICECREATED,
				"${numOfMsqChoices}", Integer.toString(numOfMsqChoices));
		
		return html;
	}

}
