package teammates.ui.template;

import java.util.List;

/**
 * Contains feedback sessions which have comments containing the search keyword
 * entered by the instructor.
 */
public class SearchCommentsForResponsesTable {
    private List<FeedbackSessionRow> feedbackSessionRows;

    public SearchCommentsForResponsesTable(List<FeedbackSessionRow> feedbackSessionRows) {
        this.feedbackSessionRows = feedbackSessionRows;
    }

    public List<FeedbackSessionRow> getFeedbackSessionRows() {
        return feedbackSessionRows;
    }

}
