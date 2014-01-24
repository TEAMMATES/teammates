package teammates.common.datatransfer;

import teammates.common.util.Const;
import teammates.common.util.FeedbackQuestionFormTemplates;
import teammates.common.util.StringHelper;

public class FeedbackNumericalScaleQuestionDetails extends
		FeedbackAbstractQuestionDetails {
	public int minScale;
	public int maxScale;
	public double step;
	
	public FeedbackNumericalScaleQuestionDetails() {
		super(FeedbackQuestionType.NUMSCALE);
		this.minScale = 1;
		this.maxScale = 5;
		this.step = 0.5;
	}
	
	public FeedbackNumericalScaleQuestionDetails(String questionText, int minScale, int maxScale, double step) {
		super(FeedbackQuestionType.NUMSCALE, questionText);
		this.minScale = minScale;
		this.maxScale = maxScale;
		this.step = step;
	}
	
	@Override
	public String getQuestionTypeDisplayName() {
		return Const.FeedbackQuestionTypeNames.NUMSCALE;
	}

	@Override
	public String getQuestionWithExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId,
			FeedbackAbstractResponseDetails existingResponseDetails) {
		FeedbackNumericalScaleResponseDetails numscaleResponseDetails = 
				(FeedbackNumericalScaleResponseDetails) existingResponseDetails;
		
		return FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.NUMSCALE_SUBMISSION_FORM,
				"${qnIdx}", Integer.toString(qnIdx),
				"${responseIdx}", Integer.toString(responseIdx),
				"${minScale}", Integer.toString(minScale),
				"${maxScale}", Integer.toString(maxScale),
				"${step}", StringHelper.toDecimalFormatString(step),
				"${existingAnswer}", numscaleResponseDetails.getAnswerString(),
				"${possibleValuesString}", getPossibleValuesString(),
				"${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
	}

	@Override
	public String getQuestionWithoutExistingResponseSubmissionFormHtml(
			boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId) {
		return FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.NUMSCALE_SUBMISSION_FORM,
				"${qnIdx}", Integer.toString(qnIdx),
				"${responseIdx}", Integer.toString(responseIdx),
				"${minScale}", Integer.toString(minScale),
				"${maxScale}", Integer.toString(maxScale),
				"${step}", StringHelper.toDecimalFormatString(step),
				"${existingAnswer}", "",
				"${possibleValuesString}", getPossibleValuesString(),
				"${Const.ParamsNames.FEEDBACK_RESPONSE_TEXT}", Const.ParamsNames.FEEDBACK_RESPONSE_TEXT);
	}

	@Override
	public String getQuestionSpecificEditFormHtml(int questionNumber) {
		return FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.NUMSCALE_EDIT_FORM,
				"${questionNumber}", Integer.toString(questionNumber),
				"${minScale}", Integer.toString(minScale),
				"${maxScale}", Integer.toString(maxScale),
				"${step}", StringHelper.toDecimalFormatString(step),
				"${possibleValues}", getPossibleValuesString(),
				"${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MIN,
				"${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_MAX,
				"${Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP}", Const.ParamsNames.FEEDBACK_QUESTION_NUMSCALE_STEP);
	}

	@Override
	public String getQuestionAdditionalInfoHtml(int questionNumber,
			String additionalInfoId) {
		String additionalInfo = getQuestionTypeDisplayName() + ":<br/>";
		additionalInfo += "Minimum scale: " + minScale 
								+ ". Maximum scale: " + maxScale 
								+ ". Increment: " + step + ".";
		
		return FeedbackQuestionFormTemplates.populateTemplate(
				FeedbackQuestionFormTemplates.FEEDBACK_QUESTION_ADDITIONAL_INFO,
				"${questionNumber}", Integer.toString(questionNumber),
				"${additionalInfoId}", additionalInfoId,
				"${questionAdditionalInfo}", additionalInfo);
	}

	@Override
	public boolean isChangesRequiresResponseDeletion(
			FeedbackAbstractQuestionDetails newDetails) {
		FeedbackNumericalScaleQuestionDetails newNumScaleDetails = 
				(FeedbackNumericalScaleQuestionDetails) newDetails;
		
		if(this.minScale != newNumScaleDetails.minScale 
				|| this.maxScale != newNumScaleDetails.maxScale
				|| this.step != newNumScaleDetails.step) {
			return true;
		}
		return false;
	}

	@Override
	public String getCsvHeader() {
		return "Feedback";
	}

	private String getPossibleValuesString() {
		double cur = minScale + step;
		int possibleValuesCount = 1;
		while ((maxScale - cur) >= -1e-9) {
			cur += step;
			possibleValuesCount++;
		}
		
		String possibleValuesString = "[Possible values: ";
		if (possibleValuesCount > 6) {
			possibleValuesString += StringHelper.toDecimalFormatString(minScale) + ", "
					+ StringHelper.toDecimalFormatString(minScale + step) + ", "
					+ StringHelper.toDecimalFormatString(minScale + 2*step) + ", ..., "
					+ StringHelper.toDecimalFormatString(maxScale - 2*step) + ", "
					+ StringHelper.toDecimalFormatString(maxScale - step) + ", "
					+ StringHelper.toDecimalFormatString(maxScale);
		} else {
			possibleValuesString += minScale;
			cur = minScale + step;
			while ((maxScale - cur) >= -1e-9) {
				possibleValuesString += ", " + StringHelper.toDecimalFormatString(cur);
				cur += step;
			}
		}
		possibleValuesString += "]";
		
		return possibleValuesString;
	}
}
