package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.questions.FeedbackQuestionDetails;
import teammates.common.util.SanitizationHelper;

public class FeedbackResponseRow {

    private int questionNumber;
    private String questionText;
    private String questionMoreInfo;
    private String responseText;
    private List<FeedbackResponseCommentRow> responseComments;

    public FeedbackResponseRow(int fbIndex, int personIndex, String personType,
                               FeedbackResponseAttributes response, FeedbackSessionResultsBundle results) {
        String questionId = response.feedbackQuestionId;
        FeedbackQuestionAttributes question = results.questions.get(questionId);
        FeedbackQuestionDetails questionDetails = question.getQuestionDetails();
        this.questionNumber = question.questionNumber;
        this.questionText = results.getQuestionText(questionId);
        this.questionMoreInfo = questionDetails.getQuestionAdditionalInfoHtml(this.questionNumber,
                                                                              personType + "-" + personIndex
                                                                                         + "-session-" + fbIndex);
        if ("recipient".equals(personType)) {
            this.responseText = response.getResponseDetails().getAnswerHtml(questionDetails);
        } else if ("giver".equals(personType)) {
            this.responseText = results.getResponseAnswerHtml(response, question);
        }
        this.responseComments = new ArrayList<FeedbackResponseCommentRow>();
        List<FeedbackResponseCommentAttributes> frcs = results.responseComments.get(response.getId());
        if (frcs != null) {
            for (FeedbackResponseCommentAttributes frc : frcs) {
                this.responseComments.add(new FeedbackResponseCommentRow(frc, frc.giverEmail));
            }
        }
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionText() {
        return SanitizationHelper.sanitizeForHtml(questionText);
    }

    public String getQuestionMoreInfo() {
        return questionMoreInfo;
    }

    public String getResponseText() {
        return responseText;
    }

    public List<FeedbackResponseCommentRow> getResponseComments() {
        return responseComments;
    }

}
