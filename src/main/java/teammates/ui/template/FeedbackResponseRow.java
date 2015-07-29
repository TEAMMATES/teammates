package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.FeedbackQuestionAttributes;
import teammates.common.datatransfer.FeedbackQuestionDetails;
import teammates.common.datatransfer.FeedbackResponseAttributes;
import teammates.common.datatransfer.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.FeedbackSessionResultsBundle;
import teammates.common.util.Sanitizer;

public class FeedbackResponseRow {

    private int questionNumber;
    private String questionText;
    private String questionMoreInfo;
    private String responseText;
    private List<FeedbackResponseComment> responseComments;
    
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
        if (personType.equals("recipient")) {
            this.responseText = response.getResponseDetails().getAnswerHtml(questionDetails);
        } else if (personType.equals("giver")) {
            this.responseText = results.getResponseAnswerHtml(response, question);
        }
        this.responseComments = new ArrayList<FeedbackResponseComment>();
        List<FeedbackResponseCommentAttributes> frcs = results.responseComments.get(response.getId());
        if (frcs != null) {
            for (FeedbackResponseCommentAttributes frc : frcs) {
                this.responseComments.add(new FeedbackResponseComment(frc, frc.giverEmail));
            }
        }
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public String getQuestionText() {
        return Sanitizer.sanitizeForHtml(questionText);
    }

    public String getQuestionMoreInfo() {
        return questionMoreInfo;
    }

    public String getResponseText() {
        return responseText;
    }

    public List<FeedbackResponseComment> getResponseComments() {
        return responseComments;
    }

}
