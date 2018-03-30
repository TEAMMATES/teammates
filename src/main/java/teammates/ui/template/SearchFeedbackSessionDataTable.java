package teammates.ui.template;

import java.util.List;

/**
 * Contains feedback sessions which have comments containing the search keyword
 * entered by the instructor.
 */
public class SearchFeedbackSessionDataTable {
    private List<FeedbackSessionRow> feedbackSessionRows;

    public SearchFeedbackSessionDataTable(List<FeedbackSessionRow> feedbackSessionRows) {
        this.feedbackSessionRows = feedbackSessionRows;
    }

    public List<FeedbackSessionRow> getFeedbackSessionRows() {
        return feedbackSessionRows;
    }

}
