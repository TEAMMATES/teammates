package teammates.common.datatransfer.questions;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.SanitizationHelper;
import teammates.common.util.Templates;
import teammates.common.util.Templates.FeedbackQuestion.FormTemplates;
import teammates.common.util.Templates.FeedbackQuestion.Slots;
import teammates.ui.template.InstructorFeedbackResultsResponseRow;

public class FeedbackTextQuestionDetails extends FeedbackQuestionDetails {

    private int recommendedLength;

    public FeedbackTextQuestionDetails() {
        super(FeedbackQuestionType.TEXT);
        recommendedLength = 0;
    }

    public FeedbackTextQuestionDetails(String questionText) {
        super(FeedbackQuestionType.TEXT, questionText);
        recommendedLength = 0;
    }

    @Override
    public List<String> getInstructions() {
        return null;
    }

    @Override
    public boolean extractQuestionDetails(
            Map<String, String[]> requestParameters,
            FeedbackQuestionType questionType) {
        String recommendedLengthString = HttpRequestHelper.getValueFromParamMap(requestParameters,
                Const.ParamsNames.FEEDBACK_QUESTION_TEXT_RECOMMENDEDLENGTH);

        recommendedLength = recommendedLengthString == null || recommendedLengthString.isEmpty() ? 0
                : Integer.parseInt(recommendedLengthString);
        return true;
    }

    @Override
    public String getQuestionTypeDisplayName() {
        return Const.FeedbackQuestionTypeNames.TEXT;
    }

    @Override
    public boolean shouldChangesRequireResponseDeletion(FeedbackQuestionDetails newDetails) {
        return false;
    }

    @Override
    public String getQuestionWithExistingResponseSubmissionFormHtml(boolean sessionIsOpen, int qnIdx,
            int responseIdx, String courseId, int totalNumRecipients, FeedbackResponseDetails existingResponseDetails,
            StudentAttributes student) {
        return Templates.populateTemplate(
                FormTemplates.TEXT_SUBMISSION_FORM,
                Slots.IS_SESSION_OPEN, Boolean.toString(sessionIsOpen),
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                "${recommendedLengthDisplay}", recommendedLength == 0 ? "style=\"display:none\"" : "",
                "${recommendedLength}", Integer.toString(recommendedLength),
                Slots.TEXT_EXISTING_RESPONSE,
                    SanitizationHelper.sanitizeForRichText(existingResponseDetails.getAnswerString()));
    }

    @Override
    public String getQuestionWithoutExistingResponseSubmissionFormHtml(
            boolean sessionIsOpen, int qnIdx, int responseIdx, String courseId, int totalNumRecipients,
            StudentAttributes student) {
        return Templates.populateTemplate(
                FormTemplates.TEXT_SUBMISSION_FORM,
                Slots.IS_SESSION_OPEN, Boolean.toString(sessionIsOpen),
                Slots.FEEDBACK_RESPONSE_TEXT, Const.ParamsNames.FEEDBACK_RESPONSE_TEXT,
                Slots.QUESTION_INDEX, Integer.toString(qnIdx),
                Slots.RESPONSE_INDEX, Integer.toString(responseIdx),
                "${recommendedLengthDisplay}", recommendedLength == 0 ? "style=\"display:none\"" : "",
                "${recommendedLength}", Integer.toString(recommendedLength),
                Slots.TEXT_EXISTING_RESPONSE, "");
    }

    @Override
    public String getQuestionSpecificEditFormHtml(int questionNumber) {
        return Templates.populateTemplate(
                FormTemplates.TEXT_EDIT_FORM,
                "${recommendedlength}", recommendedLength == 0 ? "" : Integer.toString(recommendedLength));
    }

    @Override
    public String getNewQuestionSpecificEditFormHtml() {
        return "<div id=\"textForm\">"
                + getQuestionSpecificEditFormHtml(-1)
                + "</div>";
    }

    @Override
    public String getQuestionAdditionalInfoHtml(int questionNumber, String additionalInfoId) {
        return "";
    }

    @Override
    public String getQuestionResultStatisticsHtml(List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            String studentEmail,
            FeedbackSessionResultsBundle bundle,
            String view) {
        if (responses.isEmpty()) {
            return "";
        }

        return "";
        /*
        int averageLength = 0;
        int minLength = Integer.MAX_VALUE;
        int maxLength = Integer.MIN_VALUE;
        int numResponses = 0;
        int totalLength = 0;

        for(FeedbackResponseAttributes response : responses){
            numResponses++;
            String answerString = response.getResponseDetails().getAnswerString();
            minLength = StringHelper.countWords(answerString) < minLength
                        ? StringHelper.countWords(answerString)
                        : minLength;
            maxLength = StringHelper.countWords(answerString) > maxLength
                        ? StringHelper.countWords(answerString)
                        : maxLength;
            totalLength += StringHelper.countWords(answerString);
        }

        averageLength = totalLength/numResponses;

        html = FeedbackQuestionFormTemplates.populateTemplate(
                        FeedbackQuestionFormTemplates.TEXT_RESULT_STATS,
                        "${averageLength}", Integer.toString(averageLength),
                        "${minLength}", (minLength == Integer.MAX_VALUE)? "-" : Integer.toString(minLength),
                        "${maxLength}", (maxLength == Integer.MIN_VALUE)? "-" : Integer.toString(maxLength));
        */
        //TODO: evaluate what statistics are needed for text questions later.
    }

    @Override
    public String getQuestionResultStatisticsCsv(
            List<FeedbackResponseAttributes> responses,
            FeedbackQuestionAttributes question,
            FeedbackSessionResultsBundle bundle) {
        return "";
    }

    @Override
    public String getCsvHeader() {
        return "Feedback";
    }

    @Override
    public String getQuestionTypeChoiceOption() {
        return "<li data-questiontype = \"TEXT\"><a href=\"javascript:;\">"
               + Const.FeedbackQuestionTypeNames.TEXT + "</a></li>";
    }

    @Override
    public List<String> validateQuestionDetails(String courseId) {
        return new ArrayList<>();
    }

    @Override
    public List<String> validateResponseAttributes(
            List<FeedbackResponseAttributes> responses,
            int numRecipients) {
        return new ArrayList<>();
    }

    @Override
    public Comparator<InstructorFeedbackResultsResponseRow> getResponseRowsSortOrder() {
        return null;
    }

    @Override
    public boolean isFeedbackParticipantCommentsOnResponsesAllowed() {
        return false;
    }

    @Override
    public String validateGiverRecipientVisibility(FeedbackQuestionAttributes feedbackQuestionAttributes) {
        return "";
    }

}
