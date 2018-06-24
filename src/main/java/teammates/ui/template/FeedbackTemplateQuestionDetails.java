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
}
