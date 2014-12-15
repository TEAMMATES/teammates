package teammates.common.datatransfer;

import java.util.List;

import teammates.common.util.Sanitizer;

public class FeedbackRubricResponseDetails extends FeedbackResponseDetails {
    private List<String> answer;
    
    public FeedbackRubricResponseDetails() {
        super(FeedbackQuestionType.RUBRIC);
    }
    
    @Override
    public void extractResponseDetails(FeedbackQuestionType questionType,
            FeedbackQuestionDetails questionDetails, String[] answer) {
    }

    @Override
    public String getAnswerString() {
        return "";
    }

    @Override
    public String getAnswerHtml(FeedbackQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForHtml(getAnswerString());
    }

    @Override
    public String getAnswerCsv(FeedbackQuestionDetails questionDetails) {
        return Sanitizer.sanitizeForCsv(getAnswerString());
    }
}
