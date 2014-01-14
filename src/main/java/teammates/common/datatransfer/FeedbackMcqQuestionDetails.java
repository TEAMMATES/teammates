package teammates.common.datatransfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.logic.core.StudentsLogic;

public class FeedbackMcqQuestionDetails extends FeedbackAbstractQuestionDetails {
	public int numOfMcqChoices;
	public List<String> mcqChoices;
	public boolean otherEnabled;
	FeedbackParticipantType generateOptionsFor;

	public FeedbackMcqQuestionDetails() {
		super(FeedbackQuestionType.MCQ);
		
		this.numOfMcqChoices = 0;
		this.mcqChoices = new ArrayList<String>();
		this.otherEnabled = false;
		this.generateOptionsFor = FeedbackParticipantType.NONE;
	}

	public FeedbackMcqQuestionDetails(String questionText,
			int numOfMcqChoices,
			List<String> mcqChoices,
			boolean otherEnabled) {
		super(FeedbackQuestionType.MCQ, questionText);
		
		this.numOfMcqChoices = numOfMcqChoices;
		this.mcqChoices = mcqChoices;
		this.otherEnabled = otherEnabled;
		this.generateOptionsFor = FeedbackParticipantType.NONE;
	}
	
	public FeedbackMcqQuestionDetails(String questionText,
			FeedbackParticipantType generateOptionsFor) {
		super(FeedbackQuestionType.MCQ, questionText);
		
		this.numOfMcqChoices = 0;
		this.mcqChoices = new ArrayList<String>();
		this.otherEnabled = false;
		this.generateOptionsFor = generateOptionsFor;
		Assumption.assertTrue("Can only generate students, teams or instructors",
				generateOptionsFor == FeedbackParticipantType.STUDENTS ||
				generateOptionsFor == FeedbackParticipantType.TEAMS ||
				generateOptionsFor == FeedbackParticipantType.INSTRUCTORS);
	}

	@Override
	public String getQuestionTypeDisplayName() {
		return Const.FeedbackQuestionTypeNames.MCQ;
	}
	
	@Override
	public boolean isChangesRequiresResponseDeletion(FeedbackAbstractQuestionDetails newDetails) {
		FeedbackMcqQuestionDetails newMcqDetails = (FeedbackMcqQuestionDetails) newDetails;

		if (this.numOfMcqChoices != newMcqDetails.numOfMcqChoices ||
			this.mcqChoices.containsAll(newMcqDetails.mcqChoices) == false ||
			newMcqDetails.mcqChoices.containsAll(this.mcqChoices) == false) {
			return true;
		}
		
		return false;
	}

	@Override
	public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
			int responseIdx, String courseId, FeedbackAbstractResponseDetails existingResponseDetails) {
		FeedbackMcqResponseDetails existingMcqResponse = (FeedbackMcqResponseDetails) existingResponseDetails;
		List<String> choices = generateOptionList(courseId);
		
		StringBuilder optionListHtml = new StringBuilder();
		String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
		for(int i = 0; i < choices.size(); i++) {
			String optionFragment = 
					FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
							"${qnIdx}", Integer.toString(qnIdx),
							"${responseIdx}", Integer.toString(responseIdx),
							"${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
							"${checked}", existingMcqResponse.getAnswerString().equals(choices.get(i)) ? "checked=\"checked\"" : "",
							"${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
							"${mcqChoiceValue}", choices.get(i));
			optionListHtml.append(optionFragment + Const.EOL);
		}
		
		String html = FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM,
				"${mcqSubmissionFormOptionFragments}", optionListHtml.toString());
		
		return html;
	}

	@Override
	public String getQuestionWithoutExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
		List<String> choices = generateOptionList(courseId);
		
		StringBuilder optionListHtml = new StringBuilder();
		String optionFragmentTemplate = FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM_OPTIONFRAGMENT;
		for(int i = 0; i < choices.size(); i++) {
			String optionFragment = 
					FeedbackQuestionFormTemplates.populateTemplate(optionFragmentTemplate,
							"${qnIdx}", Integer.toString(qnIdx),
							"${responseIdx}", Integer.toString(responseIdx),
							"${disabled}", sessionIsOpen ? "" : "disabled=\"disabled\"",
							"${checked}", "",
							"${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
							"${mcqChoiceValue}", choices.get(i));
			optionListHtml.append(optionFragment + Const.EOL);
		}
		
		String html = FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.MCQ_SUBMISSION_FORM,
				"${mcqSubmissionFormOptionFragments}", optionListHtml.toString());
		
		return html;
	}

	private List<String> generateOptionList(String courseId) {
		List<String> optionList;

		if (generateOptionsFor == FeedbackParticipantType.NONE) {
			optionList = mcqChoices;
		} else {
			try {
				List<StudentAttributes> studentList = 
						StudentsLogic.inst().getStudentsForCourse(courseId);

				optionList = new ArrayList<String>();
				for (StudentAttributes student : studentList) {
					optionList.add(student.name + " (" + student.team + ")");
				}
				Collections.sort(optionList);
			} catch (EntityDoesNotExistException e) {
				// No students for course
				optionList = new ArrayList<String>();
			}
		}

		return optionList;
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
				"${numOfMcqChoices}", Integer.toString(numOfMcqChoices),
				"${checkedGeneratedOptions}", (generateOptionsFor == FeedbackParticipantType.NONE) ? "" : "checked=\"checked\"", 
				"${Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS}", Const.ParamsNames.FEEDBACK_QUESTION_GENERATEDOPTIONS,
				"${generateOptionsForValue}", generateOptionsFor.toString());
		
		return html;
	}

	@Override
	public String getCsvHeader() {
		return "Feedback";
	}
}
