package teammates.ui.template;

import java.util.ArrayList;
import java.util.List;

import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;

/**
 * View Model for instructor/feedbackEdit/addTemplateQuestionsModal.tag
 *
 * <p>Generates template question details, feedback path, visibility options and other descriptive information
 * for each specific question.
 */
public class FeedbackTemplateQuestionInfo {

    private int qnNumber;
    private String qnType;
    private String qnText;
    private String qnFeedbackPath;
    private String qnVisibilityOption;
    private List<String> qnVisibilityHints;
    private FeedbackQuestionAttributes feedbackQuestion;

    public FeedbackTemplateQuestionInfo() {
        // attributes to be built by Builder
    }

    public static Builder builder() {
        return new Builder();
    }

    /**
     * A Builder class for {@link FeedbackTemplateQuestionInfo}.
     */
    public static class Builder {
        private final FeedbackTemplateQuestionInfo feedbackTemplateQuestionInfo;

        public Builder() {
            feedbackTemplateQuestionInfo = new FeedbackTemplateQuestionInfo();
        }

        public Builder withFeedbackQuestionNumber(int questionNumber) {
            if (questionNumber > 0) {
                feedbackTemplateQuestionInfo.qnNumber = questionNumber;
            }
            return this;
        }

        public Builder withFeedbackQuestionType(String questionType) {
            if (questionType != null) {
                feedbackTemplateQuestionInfo.qnType = questionType;
            }
            return this;
        }

        public Builder withFeedbackQuestionText(String questionText) {
            if (questionText != null) {
                feedbackTemplateQuestionInfo.qnText = questionText;
            }
            return this;
        }

        public Builder withFeedbackQuestionFeedbackPath(String feedbackQuestionFeedbackPath) {
            if (feedbackQuestionFeedbackPath != null) {
                feedbackTemplateQuestionInfo.qnFeedbackPath = feedbackQuestionFeedbackPath;
            }
            return this;
        }

        public Builder withFeedbackQuestionVisibilityOption(String feedbackQuestionVisibilityOption) {
            if (feedbackQuestionVisibilityOption != null) {
                feedbackTemplateQuestionInfo.qnVisibilityOption = feedbackQuestionVisibilityOption;
            }
            return this;
        }

        public Builder withFeedbackQuestionVisibilityHints(List<String> questionVisibilityHints) {
            feedbackTemplateQuestionInfo.qnVisibilityHints =
                    questionVisibilityHints == null ? new ArrayList<>()
                            : new ArrayList<>(questionVisibilityHints);
            return this;
        }

        public Builder withFeedbackQuestionAttributes(FeedbackQuestionAttributes fqa) {
            if (fqa != null) {
                feedbackTemplateQuestionInfo.feedbackQuestion = fqa;
            }
            return this;
        }

        public FeedbackTemplateQuestionInfo build() {
            return feedbackTemplateQuestionInfo;
        }
    }

    public int getQnNumber() {
        return qnNumber;
    }

    public String getQnType() {
        return qnType;
    }

    public String getQnText() {
        return qnText;
    }

    public String getQnFeedbackPath() {
        return qnFeedbackPath;
    }

    public String getQnVisibilityOption() {
        return qnVisibilityOption;
    }

    public List<String> getQnVisibilityHints() {
        return qnVisibilityHints;
    }

    public String getQnDescription() {
        switch (qnNumber) {
        case 1:
            return "Use <b>peer estimates</b> to determine the <b>work distribution percentage</b> "
                    + "among team members in <b>a team activity</b>";
        case 2:
            return "Ask each student to describe something related to themselves i.e., <b>self-reflection</b>";
        case 3:
            return "Ask each student to give <b>confidential peer feedback (qualitative)</b> to other <b>team members</b>";
        case 4:
            return "Ask each student to give <b>confidential feedback (qualitative)</b> about the team behaviour";
        case 5:
            return "Ask each student to give <b>comments about other team members, in confidence</b>";
        default:
            return null;
        }
    }

    public String getQuestionSpecificEditFormHtml() {
        return feedbackQuestion.getQuestionDetails().getQuestionSpecificEditFormHtml(qnNumber);
    }
}
