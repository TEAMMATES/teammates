package teammates.ui.template;

import java.util.List;

public class FeedbackTemplateQuestionDetails {

    private int qnNumber;
    private String qnType;
    private String qnDescription;
    private String qnText;
    private String qnFeedbackPath;
    private String qnVisibilityOption;
    private List<String> qnVisibilityHints;

    public FeedbackTemplateQuestionDetails(int qnNumber, String qnType, String qnText, String qnFeedbackPath,
                                           String qnVisibilityOption, List<String> qnVisibilityHints) {
        this.qnNumber = qnNumber;
        this.qnType = qnType;
        this.qnText = qnText;
        this.qnFeedbackPath = qnFeedbackPath;
        this.qnVisibilityOption = qnVisibilityOption;
        this.qnVisibilityHints = qnVisibilityHints;
        this.qnDescription = getQnDescription();
    }

}
