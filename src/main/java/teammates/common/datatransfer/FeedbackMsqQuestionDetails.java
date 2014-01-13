package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.Sanitizer;
import teammates.common.util.StringHelper;

public class FeedbackMsqQuestionDetails extends FeedbackAbstractQuestionDetails {
	public int numOfMsqChoices;
	public List<String> msqChoices;
	public boolean otherEnabled;
	FeedbackParticipantType generateOptionsFor;
	
	public FeedbackMsqQuestionDetails() {
		super(FeedbackQuestionType.MSQ);
		
		this.numOfMsqChoices = 0;
		this.msqChoices = new ArrayList<String>();
		this.otherEnabled = false;
		this.generateOptionsFor = FeedbackParticipantType.NONE;
	}

	public FeedbackMsqQuestionDetails(String questionText,
			int numOfMsqChoices,
			List<String> msqChoices,
			boolean otherEnabled) {
		super(FeedbackQuestionType.MSQ, questionText);
		
		this.numOfMsqChoices = numOfMsqChoices;
		this.msqChoices = msqChoices;
		this.otherEnabled = otherEnabled;
		this.generateOptionsFor = FeedbackParticipantType.NONE;
	}
	
	public FeedbackMsqQuestionDetails(String questionText,
			FeedbackParticipantType generateOptionsFor) {
		super(FeedbackQuestionType.MSQ, questionText);
		
		this.numOfMsqChoices = 0;
		this.msqChoices = new ArrayList<String>();
		this.otherEnabled = false;
		this.generateOptionsFor = generateOptionsFor;
		Assumption.assertTrue("Can only generate students, teams or instructors",
				generateOptionsFor == FeedbackParticipantType.STUDENTS ||
				generateOptionsFor == FeedbackParticipantType.TEAMS ||
				generateOptionsFor == FeedbackParticipantType.INSTRUCTORS);
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
			FeedbackAbstractResponseDetails existingResponseDetails) {
		FeedbackMsqResponseDetails existingMsqResponse = (FeedbackMsqResponseDetails) existingResponseDetails;
		
		StringBuilder optionListHtml = new StringBuilder();
		String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM_OPTIONFRAGMENT;
		for(int i = 0; i < numOfMsqChoices; i++) {
			String optionFragment = 
					FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
							"${qnIdx}", Integer.toString(qnIdx),
							"${responseIdx}", Integer.toString(responseIdx),
							"${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
							"${checked}", existingMsqResponse.contains(msqChoices.get(i)) ? "checked=\"checked\"" : "",
							"${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
							"${msqChoiceValue}", msqChoices.get(i));
			optionListHtml.append(optionFragment + Const.EOL);
		}
		
		String html = FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM,
				"${msqSubmissionFormOptionFragments}", optionListHtml.toString());
		
		return html;
	}

	@Override
	public String getQuestionWithoutExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx) {
		StringBuilder optionListHtml = new StringBuilder();
		String optionFragmentTemplate = FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM_OPTIONFRAGMENT;
		for(int i = 0; i < numOfMsqChoices; i++) {
			String optionFragment = 
					FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
							"${qnIdx}", Integer.toString(qnIdx),
							"${responseIdx}", Integer.toString(responseIdx),
							"${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
							"${checked}", "",
							"${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
							"${msqChoiceValue}", msqChoices.get(i));
			optionListHtml.append(optionFragment + Const.EOL);
		}
		
		String html = FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.MSQ_SUBMISSION_FORM,
				"${msqSubmissionFormOptionFragments}", optionListHtml.toString());
		
		return html;
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
				"${numOfMsqChoices}", Integer.toString(numOfMsqChoices),
				"${checkedGeneratedOptions}", (generateOptionsFor == FeedbackParticipantType.NONE) ? "" : "checked=\"checked\"", 
				"${Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS}", Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
				"${generateOptionsForValue}", generateOptionsFor.toString());
		
		return html;
	}

	@Override
	public String getCsvHeader() {
		List<String> sanitizedChoices = Sanitizer.sanitizeListForCsv(msqChoices);
		return "Feedbacks:," + StringHelper.toString(sanitizedChoices, ",");
	}
}
