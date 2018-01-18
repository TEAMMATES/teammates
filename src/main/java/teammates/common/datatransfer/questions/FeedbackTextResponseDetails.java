package teammates.common.datatransfer.questions;

import org.jsoup.Jsoup;

import teammates.common.util.SanitizationHelper;

public class FeedbackTextResponseDetails extends
        FeedbackResponseDetails {

    //For essay questions the response is saved as plain-text due to legacy format before there were multiple question types
    public String answer;

    public FeedbackTextResponseDetails() {
        super(FeedbackQuestionType.TEXT);
        this.answer = "";
    }

    public FeedbackTextResponseDetails(String answer) {
        super(FeedbackQuestionType.TEXT);
        this.answer = SanitizationHelper.sanitizeForRichText(answer);
    }

    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
                                       FeedbackQuestionDetails questionDetails, String[] answer) {
        this.answer = SanitizationHelper.sanitizeForRichText(answer[0]);
    }

    @Override
    public String getAnswerString() {
        return SanitizationHelper.sanitizeForRichText(answer);
    }

    @Override
    public String getAnswerHtmlInstructorView(FeedbackQuestionDetails questionDetails) {
        return SanitizationHelper.sanitizeForRichText(answer);
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return SanitizationHelper.sanitizeForCsv(Jsoup.parse(answer).text());
    }

}
