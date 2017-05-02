package teammates.ui.template;

import java.util.List;

/**
 * Data model for the copy question modal on instructorFeedbackEdit.jsp.
 */
public class FeedbackQuestionCopyTable {
    private List<FeedbackQuestionTableRow> questionRows;

    public FeedbackQuestionCopyTable(List<FeedbackQuestionTableRow> questionRows) {
        this.questionRows = questionRows;
    }

    public List<FeedbackQuestionTableRow> getQuestionRows() {
        return questionRows;
    }

}
